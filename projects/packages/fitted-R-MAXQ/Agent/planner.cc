#include "planner.hh"
#include "primitive.hh" // for debugging XXX

#include <cassert>

ModelBasedStatePolicy::~ModelBasedStatePolicy()
{}

PlannerRef Planner::create(const MDPRef &mdp,
			   const StatePredicateRef &terminal,
			   const StateFunctionRef &goal,
			   double gamma,
			   double epsilon)
{
  PlannerRef result(new Planner(mdp, terminal, goal, gamma, epsilon));
  result->m_this = result;
  return result;
}

Planner::Planner(const MDPRef &mdp,
		 const StatePredicateRef &terminal,
		 const StateFunctionRef &goal,
		 double gamma,
		 double epsilon):
  m_mdp(mdp), m_terminal(terminal), m_goal(goal),
  m_gamma(gamma), m_epsilon(epsilon)
{}

Planner::~Planner()
{}

ModelBasedStatePolicyRef Planner::policy(const StateVectorRef &s)
{
  DecisionStateRef ds = policy(m_mdp->state_data(s), NULL);
  propagate_changes();
  return ds;
}

void Planner::propagate_changes()
{
  // std::cerr << "Planner::propagate_changes with subgoal ";
  // m_terminal->debug(std::cerr);

  // Recompute successors for state-actions in inbox and add to
  // priority queue.
  std::set<DecisionStateWeakRef>::iterator i;
  for (i = m_inbox.begin(); i != m_inbox.end(); ++i)
    if (!i->expired())
      i->lock()->propagate_MDP_change();
  m_inbox.clear();
  // std::cerr << "Done seeding priority queue\n";

  // Propagate value changes using prioritized sweeping.
  while (!m_pqueue.empty()) {
    // debug_pqueue(std::cerr);

    const DecisionStateRef ds = m_pqueue.front();
    assert(ds);
    ds->zero_bound();
    ds->propagate_value_change(); // may add more states to pqueue
  }
  // std::cerr << "Done propagating value changes\n";

  // Send notifications of policy changes.
  std::map<DecisionStateWeakRef, TaskRef>::iterator j;
  for (j = m_outbox.begin(); j != m_outbox.end(); ++j)
    if (!j->first.expired())
      j->first.lock()->propagate_policy_change(j->second);
  m_outbox.clear();
}

void Planner::perform_standard_value_iteration()
{
  std::cerr << "perform_standard_value_iteration()\n";
  NonterminalsContainer::const_iterator i;
  double norm_residual;
  unsigned counter = 0;
  do {
    norm_residual = 0;
    std::cerr << "\titeration " << ++counter << "\n";
    for (i = m_nonterminals.begin(); i != m_nonterminals.end(); ++i) {
      if (i->second.expired()) continue;
      DecisionStateRef ds(i->second);
      double change = ds->backup_values();
      if (change < 0)
	change = -change;
      if (norm_residual < change)
	norm_residual = change;
    }
  } while (norm_residual >= m_epsilon);
}

void Planner::debug(std::ostream &out)
{
  // Output policy
  std::vector<std::vector<StateVectorRef> > policy(4);

  NonterminalsContainer::const_iterator i;

  out << "model:\n";
  for (i = m_nonterminals.begin(); i != m_nonterminals.end(); ++i) {
    if (i->second.expired()) continue;
    DecisionStateRef ds(i->second);
    ds->debug(out);
  }
  out << "end model\n";

  out << "value function:\n";
  write_value_function(out);
  out << "end value function\n";

  out << "policy:\n";
  write_policy(out);
  out << "end policy\n";
}

void Planner::write_value_function(std::ostream &out) const
{
  NonterminalsContainer::const_iterator i;
  for (i = m_nonterminals.begin(); i != m_nonterminals.end(); ++i) {
    if (i->second.expired()) continue;
    DecisionStateRef ds(i->second);
    out << ds->state() << ds->value() << "\n";
  }
}

void Planner::write_policy(std::ostream &out) const
{
  std::map<std::string, std::vector<StateVectorRef> > policy;
  NonterminalsContainer::const_iterator i;
  for (i = m_nonterminals.begin(); i != m_nonterminals.end(); ++i) {
    if (i->second.expired()) continue;
    DecisionStateRef ds(i->second);
    TaskRef a = ds->policy_action();
    const std::string &task_name = a->name();
    policy[task_name].push_back(ds->state());
  }

  std::map<std::string, std::vector<StateVectorRef> >::const_iterator j;
  for (j = policy.begin(); j != policy.end(); ++j) {
    const std::string &task_name = j->first;
    const std::vector<StateVectorRef> &states = j->second;
    // For Gnuplot's convenience, comment out the name of each action
    // and leave two blank lines between each action.
    out << "# " << task_name << ":\n";
    std::vector<StateVectorRef>::const_iterator k;
    for (k = states.begin(); k != states.end(); ++k) {
      out << *k << "\n";
    }
    out << "\n\n";
  }
}

Planner::ValueState::Observer::~Observer()
{}

Planner::ValueStateRef Planner::ValueState::
create(ValueStateWeakRef &ref, double value)
{
  ValueStateRef result(new ValueState(value));
  ref = result;
  return result;
}

Planner::ValueState::ValueState(double value): m_V(value)
{}

Planner::ValueState::~ValueState()
{}

void Planner::ValueState::add_observer(const ObserverWeakRef &o, double weight)
{
  assert(weight >= 0);
  vector_map_assoc(m_observers, o, weight);
}

void Planner::ValueState::remove_observer(const ObserverWeakRef &o)
{
  std::vector<std::pair<ObserverWeakRef, double> >::iterator del =
    vector_map_find(m_observers, o);
  assert(del != m_observers.end());
  m_observers.erase(del);
}

void Planner::ValueState::set_value(double value)
{
  const double change = value - m_V;
  m_V = value;
  std::vector<std::pair<ObserverWeakRef, double> >::const_iterator i;
  for (i = m_observers.begin(); i != m_observers.end(); ++i) {
    const double w = i->second;
    i->first.lock()->observe_value_change(w * change);
  }
}

Planner::DecisionStateRef Planner::DecisionState::
create(const PlannerRef &planner, const MDPStateRef &model,
       DecisionStateWeakRef &ref, ValueStateWeakRef *ptr)
{
  DecisionStateRef result(new DecisionState(planner, model));
  ref = result->m_this = result;
  if (ptr)
    *ptr = ref;
  model->add_observer(result->m_this);
  result->initialize();
  return result;
}

Planner::DecisionState::DecisionState(const PlannerRef &planner,
				      const MDPStateRef &model):
  m_planner(planner), m_model(model), m_max(TaskRef(), ActionRef()),
  m_errorbound(0), m_heapindex(-1)
{
//   std::cout << "DecisionState() at " << state() << "\n";
}

Planner::DecisionState::~DecisionState()
{
//   std::cout << "~DecisionState() at " << state() << "\n";

  m_model->remove_observer(m_this);

  assert(m_heapindex < 0);
  // if (m_heapindex >= 0)
  //   dequeue();
}

void Planner::DecisionState::initialize()
{
//   std::cout << "DecisionState::initialize() at " << *model->state() << "\n";
  const std::vector<std::pair<TaskRef, MDPStateActionRef> > & model_actions =
    m_model->state_actions();
  std::vector<std::pair<TaskRef, MDPStateActionRef> >::const_iterator a =
    model_actions.begin();
  std::vector<std::pair<TaskRef, ActionRef> >::iterator b =
    m_action_values.begin();
  while (a != model_actions.end()) {
    assert(b == m_action_values.end() ||
	   a->first < b->first ||
	   a->first == b->first);
    if (b == m_action_values.end() || a->first < b->first) {
      // This can lead to the construction and initialization of other
      // DecisionState objects!
      b = m_action_values.insert(b, std::make_pair(a->first, ActionRef()));
      b->second = Action::create(m_this.lock(), a->second);
    }
    assert(a->first == b->first);
    if (!m_max.second || m_max.second->value() < b->second->value())
      m_max = *b;
    ++a;
    ++b;
  }
  assert(b == m_action_values.end());
  set_value(m_max.second->value());
//   std::cout << "EXIT DecisionState::initialize() at " << *model.state() << "\n";
}

const StateVectorRef &Planner::DecisionState::state() const
{
  return m_model->state();
}

TaskRef Planner::DecisionState::policy_action() const
{
  return m_max.first;
}

MDPStateRef Planner::DecisionState::policy_model() const
{
  return m_model;
}

void Planner::DecisionState::observe_MDP_change(const TaskRef &action)
{
  m_inbox.insert(action);
  m_planner->m_inbox.insert(m_this);
}

void Planner::DecisionState::propagate_MDP_change()
{
  std::set<TaskRef>::iterator i = m_inbox.begin();
  std::vector<std::pair<TaskRef, ActionRef> >::iterator j =
    m_action_values.begin();
  while (i != m_inbox.end()) {
    assert(j != m_action_values.end());
    while (j->first < *i) {
      ++j;
      assert(j != m_action_values.end());
    }
    assert(j != m_action_values.end() && j->first == *i);
    j->second->compute_successors();
    ++i;
    ++j;
  }
  m_inbox.clear();
  propagate_value_change();
}

void Planner::DecisionState::propagate_value_change()
{
  const std::pair<DecisionStateWeakRef, TaskRef> original(m_this, m_max.first);
  std::vector<std::pair<TaskRef, ActionRef> >::iterator i =
    m_action_values.begin();
  assert(i != m_action_values.end());
  i->second->update_value();
  m_max = *i;
  while (++i != m_action_values.end()) {
    i->second->update_value();
    if (m_max.second->value() < i->second->value())
      m_max = *i;    
  }

  if (m_max.first != original.second) // new policy action!
    // m_planner->m_outbox is a map, so the insert will have no effect if
    // m_this already exists as a key
    m_planner->m_outbox.insert(original);

  // std::cerr << "propagate_value_change: Value of " << state()
  // 	    << " (" << m_action_values.size() << " actions): "
  // 	    << value() << " -> " << m_max.second->value() << "\n";
  // if (m_max.first != original.second)
  //   std::cerr << "\tnew policy action\n";

  if (m_max.second->value() != value())
    set_value(m_max.second->value());
}

void Planner::DecisionState::propagate_policy_change(const TaskRef &original)
{
  // The newest policy action may have changed back to the original
  // policy action.
  if (original != m_max.first)
    notify_policy_observers();
}

void Planner::DecisionState::debug(std::ostream &out) const
{
  out << "State " << state() << "\n";
  std::vector<std::pair<TaskRef, ActionRef> >::const_iterator i;
  for (i = m_action_values.begin(); i != m_action_values.end(); ++i) {
    const TaskRef &task = i->first;
    const ActionRef &action = i->second;
    const PrimitiveTaskRef &prim =
      boost::dynamic_pointer_cast<PrimitiveTask, Task>(task);
    if (prim)
      out << "\tAction " << prim->action() << "\n";
    else
      out << "\t(Composite)\n";
    action->debug(out);
  }
  out << "\n";
}

double Planner::DecisionState::backup_values()
{
  double oldvalue = value();
  const std::pair<DecisionStateWeakRef, TaskRef> original(m_this, m_max.first);
  std::vector<std::pair<TaskRef, ActionRef> >::iterator i =
    m_action_values.begin();
  assert(i != m_action_values.end());
  i->second->compute_value();
  m_max = *i;
  while (++i != m_action_values.end()) {
    i->second->compute_value();
    if (m_max.second->value() < i->second->value())
      m_max = *i;    
  }
  if (m_max.first != original.second) // new policy action!
    // m_planner->m_outbox is a map, so the insert will have no effect
    // if m_this already exists as a key
    m_planner->m_outbox.insert(original);
  if (m_max.second->value() != value())
    set_value(m_max.second->value());
  return value() - oldvalue;
}

Planner::DecisionState::ActionRef Planner::DecisionState::Action::
create(const DecisionStateWeakRef &parent, const MDPStateActionRef &model)
{
  ActionRef result(new Action(parent, model));
  result->m_this = result;
  result->compute_successors();
  result->compute_value();
  return result;
}

Planner::DecisionState::Action::
Action(const DecisionStateWeakRef &parent, const MDPStateActionRef &model):
  m_parent(parent), m_model(model)
{
//   std::cout << "Action constructor\n";
}

Planner::DecisionState::Action::~Action()
{
  std::vector<std::pair<StateVectorRef, ValueStateProbability> >::iterator i;
  for (i = m_successors.begin(); i != m_successors.end(); ++i) {
    const ValueStateRef &successor = i->second.first;
    if (successor)
      successor->remove_observer(m_this);
  }
}

void Planner::DecisionState::Action::observe_value_change(double change)
{
  DecisionStateRef parent(m_parent);
  assert(parent);
  if (change < 0)
    change = -change;
  change *= parent->m_planner->m_gamma;
  m_errorbound += change;
  parent->child_bound(m_this.lock(), m_errorbound);

//   parent.increase_bound(change);

//   parent.planner.pqueue.insert(parent.model.state(), change, true);


//   Q += change * parent.planner.gamma;

//   // Difference between this Action object's Q value and the parent
//   // DecisionState object's stored value.  We only bother to change
//   // the parent's stored value if we can beat the stored value or if
//   // the stored value was based on this Action object's Q value and
//   // this value has decreased.
//   double diff = Q - parent.value();

//   std::cout << "observe_value_change(" << change
// 	    << ") at " << parent.state() << "\n"
// 	    << "Q = " << Q
// 	    << ", diff = " << diff
// 	    << (this == parent.max.second ? " (policy action)\n" : "\n");
//   if (change > 0)
//     std::cout << "Increase?\n";
//   if (Q > 0) {
//     std::cout << "Positive?!\n";
//     const state_dist_t &model_succs = model.successor_probabilities();
//     state_dist_t::const_iterator a = model_succs.begin();
//     std::vector<std::pair<const state_t *, ValueState *> >::iterator b = successors.begin();
//     double debugQ = 0;
//     while (a != model_succs.end()) {
//       while (b != successors.end() && b->first < a->first) {
// 	std::cout << "Extra successor?\n";
// 	++b;
//       }
//       if (b == successors.end() || a->first < b->first)
// 	std::cout << "Missing successor?\n";
//       else {
// 	std::cout << "\t" << a->second << ": " << b->second->value() << "\n";
// 	debugQ += a->second * b->second->value();
//       }
//       ++a;
//       ++b;
//     }
//     debugQ *= parent.planner.gamma;
//     debugQ += model.reward();
//     std::cout << "debugQ: " << debugQ << " versus " << Q - change << "\n";
//   }

//   if (this == parent.max.second && diff < 0)
//     diff = -diff; // The value of the policy action decreased!
//   if (diff > parent.planner.epsilon)
//     parent.planner.pqueue.insert(parent.model.state(), diff);
}

void Planner::DecisionState::Action::compute_successors()
{
  DecisionStateRef parent(m_parent);
  assert(parent);
//   std::cout << "compute_successors() at " << parent.state() << "\n";
  const StateDistribution &model_succs = m_model->successor_probabilities();
  StateDistribution::const_iterator a = model_succs.begin();
  std::vector<std::pair<StateVectorRef, ValueStateProbability> >::iterator b =
    m_successors.begin();
  while (a != model_succs.end()) {
    while (b != m_successors.end() && b->first < a->first) {
      // Remove erstwhile successor.
      const ValueStateRef &vs = b->second.first;
      if (vs)
	vs->remove_observer(m_this);
      b = m_successors.erase(b);
    }
    if (b == m_successors.end() || a->first < b->first) {
      ValueStateRef succ = parent->m_planner->successor_value(a->first);
      assert(succ);
      succ->add_observer(m_this, a->second);
      // Use a NULL reference as a proxy for parent to avoid creating
      // a cycle of strong references.
      if (succ == parent)
	succ.reset();
      ValueStateProbability prob(succ, a->second);
      b = m_successors.insert(b, std::make_pair(a->first, prob));
    } else if (b->second.second != a->second) { // Update existing successor.
      const ValueStateRef &vs = b->second.first;
      if (vs)
	vs->add_observer(m_this, a->second);
      else {
	ValueState *parent_as_value = parent.get();
	parent_as_value->add_observer(m_this, a->second);
      }
      b->second.second = a->second;
    }
    ++a;
    ++b;
  }
  while (b != m_successors.end()) {
    // Remove erstwhile successor.
    const ValueStateRef &vs = b->second.first;
    if (vs)
      vs->remove_observer(m_this);
    else {
      ValueState *parent_as_value = parent.get();
      parent_as_value->remove_observer(m_this);
    }
    b = m_successors.erase(b);
  }
  m_errorbound = std::numeric_limits<double>::infinity();
}

void Planner::DecisionState::Action::compute_value()
{
  DecisionStateRef parent(m_parent);
//   std::cout << "compute_value() at " << parent.state() << "\n";
  m_Q = 0.0;
  std::vector<std::pair<StateVectorRef, ValueStateProbability> >::iterator b;
  for (b = m_successors.begin(); b != m_successors.end(); ++b) {
    // We used NULL pointers as a proxy for our parent to avoid strong
    // reference cycles.
    const ValueStateRef &vs = b->second.first;
    const ValueStateRef &successor =  vs ? vs : parent;
    const double &successor_probability = b->second.second;
    m_Q += successor_probability * successor->value();

//     std::cout << "\tp = " << b->second.second << ", V = " << b->second.first->value() << ", Q: " << Q << "\n";
  }

  m_Q *= parent->m_planner->m_gamma;
  m_Q += m_model->reward();
  m_errorbound = 0.0;
//   std::cout << "\tQ = " << Q << "\n";
//   if (Q > 0)
//     std::cout << "Positive?!\n";
}

void Planner::DecisionState::Action::debug(std::ostream &out) const
{  
  std::vector<std::pair<StateVectorRef, ValueStateProbability> >::
    const_iterator b;
  for (b = m_successors.begin(); b != m_successors.end(); ++b) {
    const StateVectorRef &s = b->first;
    ValueStateRef vs = b->second.first;
    if (!vs)
      vs = m_parent.lock();
    double p = b->second.second;
    out << "\t\t" << p << ": " << s << "(" << vs->value() << ")\n";
  }
  out << "\t\tr = " << m_model->reward() << ", Q = " << value() << "\n";
}

void Planner::DecisionState::child_bound(const ActionRef &child, double bound)
{
  // if not policy action, then discount bound by advantage of the
  // policy action
  if (child != m_max.second) 
    bound -= value() - child->value();
  if (m_errorbound < bound) {
    m_errorbound = bound; // errorbound increases
    if (m_errorbound > m_planner->m_epsilon) {
      if (m_heapindex < 0) { // Must insert into priority queue
	m_heapindex = static_cast<int>(m_planner->m_pqueue.size());
	m_planner->m_pqueue.push_back(m_this.lock());
      }
      insert_heapify();
    }
  }
}

void Planner::DecisionState::heapify()
{
  if (m_heapindex >= 0) {
    std::vector<DecisionStateRef> &pqueue = m_planner->m_pqueue;
    assert(pqueue[m_heapindex] == m_this.lock());
    unsigned child_index = 2*m_heapindex + 1; // identify children
    while (child_index < pqueue.size()) { // as long as there's a child...
      DecisionStateRef *child = &pqueue[child_index];
      assert((*child)->m_heapindex == static_cast<int>(child_index));
      unsigned other_child_index = child_index + 1;
      if (other_child_index < pqueue.size()) {
	DecisionStateRef *other_child = &pqueue[other_child_index];
	assert((*other_child)->m_heapindex ==
	       static_cast<int>(other_child_index));
	// find the bigger child
	if ((*child)->m_errorbound < (*other_child)->m_errorbound) {
	  child = other_child;
	  child_index = other_child_index;
	}
      }
      if (m_errorbound < (*child)->m_errorbound) {
	// if out errorbound is bigger than child's
	child->swap(pqueue[m_heapindex]); // swap entries in pqueue with child
	pqueue[m_heapindex]->m_heapindex = m_heapindex; // give child our index
	m_heapindex = child_index; // and take the child's index
	child_index = 2 * child_index + 1;
      } else
	break;
    }
  }
}

void Planner::DecisionState::insert_heapify()
{
  std::vector<DecisionStateRef> &pqueue = m_planner->m_pqueue;
  assert(m_heapindex < 0 || pqueue[m_heapindex] == m_this.lock());
  while (m_heapindex > 0) {
    int parent_index = (m_heapindex - 1)/2;
    DecisionStateRef *parent = &pqueue[parent_index];
    assert((*parent)->m_heapindex == parent_index);
    if (m_errorbound <= (*parent)->m_errorbound)
      break;
    parent->swap(pqueue[m_heapindex]); // swap spots with parent
    pqueue[m_heapindex]->m_heapindex = m_heapindex; // parent takes my index
    m_heapindex = parent_index; // I take parent's index
  }
}

void Planner::DecisionState::dequeue()
{
  assert(m_heapindex >= 0);
  std::vector<DecisionStateRef> &pqueue = m_planner->m_pqueue;
  DecisionStateRef *hole = &pqueue[m_heapindex];
  hole->swap(pqueue.back()); // swap positions with back
  pqueue.pop_back(); // remove back in vector
  if (m_heapindex != static_cast<int>(pqueue.size())) {
    const DecisionStateRef old_back = *hole;

    // Give the old back element our index
    old_back->m_heapindex = m_heapindex;

    // Move old back element toward front if necessary (unlikely)
    if (m_heapindex > 0)
      old_back->insert_heapify();

    // Move old back toward back if necessary.
    old_back->heapify();
  }
  m_heapindex = -1; // remove this DecisionState from queue
}

Planner::DecisionStateRef Planner::policy(const MDPStateRef &s,
					  ValueStateWeakRef *ptr)
{
  DecisionStateWeakRef &ds = m_nonterminals[s->state()];
  if (ds.expired())
    return DecisionState::create(m_this.lock(), s, ds, ptr);
  else
    return ds.lock();
}

Planner::ValueStateRef Planner::successor_value(const StateVectorRef &successor)
{
  ValueStateWeakRef &vs = m_completions[successor];
  if (vs.expired()) {
    if ((*m_terminal)(successor))
      return ValueState::create(vs, (*m_goal)(successor));
    else
      return policy(m_mdp->state_data(successor), &vs);
  } else
    return vs.lock();
}

void Planner::debug_pqueue(std::ostream &out) const
{
  out << "Priority queue for task with goal ";
  m_terminal->debug(out);
  std::vector<DecisionStateRef>::const_iterator i;
  for (i = m_pqueue.begin(); i != m_pqueue.end(); ++i) {
    DecisionStateRef ds = *i;
    out << "\t" << ds->state() << "\n";
  }
}
