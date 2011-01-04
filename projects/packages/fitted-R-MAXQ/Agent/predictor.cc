#include "predictor.hh"

#include <cmath>

PredictorRef Predictor::create(const PlannerRef &planner,
			       const StatePredicateRef &terminalp,
			       double gamma,
			       double epsilon,
			       const StateVariables &dimensions)
{
  PredictorRef result(new Predictor(planner, terminalp, gamma,
				    epsilon, dimensions));
  result->m_this = result;
  return result;
}

Predictor::Predictor(const PlannerRef &planner,
		     const StatePredicateRef &terminalp,
		     double gamma,
		     double epsilon,
		     const StateVariables &dimensions):
  m_planner(planner), m_terminalp(terminalp), m_gamma(gamma),
  m_epsilon(epsilon), m_dimensions(dimensions)
{}

Predictor::~Predictor()
{}

StateActionModelRef Predictor::model(const StateVectorRef &s)
{
  StateRef p = prediction(m_planner->policy(s), NULL);
  propagate_changes();
  return p;
}

void Predictor::propagate_changes()
{
//   std::cerr << "Predictor::propagate_changes(), "
// 	    << predictions.size() << " predictions\n";

  std::set<StateWeakRef>::iterator i;
  for (i = m_inbox.begin(); i != m_inbox.end(); ++i)
    if (!i->expired())
      i->lock()->propagate_mdp_change();

  while (!m_pqueues[REWARD].empty()) {
    StateRef st = m_pqueues[REWARD].front();
    assert(st);
    st->zero_bound(REWARD);
    st->propagate_reward_change();
  }

  while (!m_pqueues[TRANSITION].empty()) {
    StateRef st = m_pqueues[TRANSITION].front();
    assert(st);
    st->zero_bound(TRANSITION);
    st->propagate_transitions_change();
  }

  // The propagate_reward_change() and propagate_transitions_change()
  // methods should add State objects to the inbox, as appropriate.
  for (i = m_inbox.begin(); i != m_inbox.end(); ++i)
    if (!i->expired())
      i->lock()->propagate_model_change();
  m_inbox.clear();
}

Predictor::StatePrediction::~StatePrediction()
{}

Predictor::StatePredictionRef Predictor::TerminalStatePrediction::
create(const StateVectorRef &succ, const StateVariables &dimensions,
       StatePredictionWeakRef &ref)
{
  StatePredictionRef result(new TerminalStatePrediction(succ, dimensions));
  ref = result;
  return result;
}

Predictor::TerminalStatePrediction::
TerminalStatePrediction(const StateVectorRef &succ,
			const StateVariables &dimensions):
  m_terminal(AbsoluteEffect::create(succ, dimensions))
{}

Predictor::TerminalStatePrediction::~TerminalStatePrediction()
{}

double Predictor::TerminalStatePrediction::predict_return(double p) const
{
  return 0; // Can't earn additional reward after termination
}

void Predictor::TerminalStatePrediction::
predict_terminals(EffectDistribution &term, double p) const
{
  assert(p >= 0);
  EffectDistribution::value_type val(m_terminal, p);
  EffectDistribution::iterator i =
    std::lower_bound(term.begin(), term.end(), val,
		     LessFirst<EffectDistribution::value_type>());
  if (i == term.end() || m_terminal < i->first) // m_terminal not in term
    term.insert(i, val);
  else // i->first == m_terminal
    i->second += p;
}

void Predictor::TerminalStatePrediction::
add_predecessor(const StateWeakRef &pred, double probability)
{} // No changes to propagate

void Predictor::TerminalStatePrediction::
remove_predecessor(const StateWeakRef &pred)
{} // Never added any predecessors anyway

Predictor::StateRef Predictor::State::
create(const PredictorRef &predictor, const ModelBasedStatePolicyRef &policy,
       StateWeakRef &ref, StatePredictionWeakRef *ptr)
{
  StateRef result(new State(predictor, policy));
  ref = result->m_this = result;
  if (ptr != NULL)
    *ptr = ref;
  policy->add_observer(result->m_this);
  result->m_mdp_state->add_observer(result->m_this);
  result->initialize();
  return result;
}

Predictor::State::State(const PredictorRef &predictor,
			const ModelBasedStatePolicyRef &policy):
  m_predictor(predictor), m_mdp_state(policy->policy_model()),
  m_policy(policy), m_r(0.0)
{
  for (unsigned idx = REWARD; idx <= TRANSITION; ++idx) {
    m_error_bounds[idx] = 0.0;
    m_heap_indices[idx] = -1;
  }
}

Predictor::State::~State()
{
  SuccessorsContainer::iterator i;
  for (i = m_successors.begin(); i != m_successors.end(); ++i) {
    StatePredictionRef &successor = i->second.first;
    if (successor)
      successor->remove_predecessor(m_this);
  }
  m_policy->remove_observer(m_this);
  m_mdp_state->remove_observer(m_this);
}

const StateVectorRef &Predictor::State::state() const
{
  return m_mdp_state->state();
}

double Predictor::State::reward() const
{
  return m_r;
}

const EffectDistribution &Predictor::State::effects() const
{
  return m_effect_map;
}

double Predictor::State::predict_return(double p) const
{
  assert(p >= 0);
  return p * m_r;
}

void Predictor::State::predict_terminals(EffectDistribution &term,
					 double p) const
{
  assert(std::isfinite(p));
  assert(p >= 0);
  EffectDistribution::const_iterator i;
  for (i = m_effect_map.begin(); i != m_effect_map.end(); ++i) {
    EffectDistribution::value_type val(i->first, p * i->second);
    assert(std::isfinite(val.second));
    assert(val.second >= 0);
    EffectDistribution::iterator j =
      std::lower_bound(term.begin(), term.end(), val,
		       LessFirst<EffectDistribution::value_type>());
    if (j == term.end() || val.first < j->first) // not already in term
      term.insert(j, val);
    else // j->first == val.first
      j->second += val.second;
  }
}

void Predictor::State::add_predecessor(const StateWeakRef &pred,
				       double probability)
{
  assert(!pred.expired());
  assert(std::isfinite(probability));
  assert(probability >= 0);
  vector_map_assoc(m_predecessors, pred, probability);
}

void Predictor::State::remove_predecessor(const StateWeakRef &pred)
{
  vector_set_erase(m_predecessors,
		   std::pair<StateWeakRef, double>(pred, 0.0),
		   LessFirst<std::pair<StateWeakRef, double> >());
}

void Predictor::State::observe_MDP_change(const TaskRef &a)
{
//   std::cout << "Predictor::State::observe_MDP_change\n";
  assert(m_action != this->m_mdp_state->state_actions().end());
  if (a == m_action->first)
    m_predictor->m_inbox.insert(m_this);
}

void Predictor::State::observe_policy_change()
{
//   std::cout << "Predictor::observe_policy_change\n";
  m_predictor->m_inbox.insert(m_this);
}

void Predictor::State::initialize()
{
//   std::cout << "Predictor::State::initialize() " << *state() << "\n";
  m_action = vector_map_find(m_mdp_state->state_actions(),
			     m_policy->policy_action());
  assert(m_action != m_mdp_state->state_actions().end());
  propagate_mdp_change();
}

void Predictor::State::propagate_mdp_change()
{
  if (m_action->first != m_policy->policy_action()) {
    m_action = vector_map_find(m_mdp_state->state_actions(),
			       m_policy->policy_action());
    assert(m_action != m_mdp_state->state_actions().end());
  }
  const StateDistribution &mdp_succs =
    m_action->second->successor_probabilities();
  StateDistribution::const_iterator a = mdp_succs.begin();
  SuccessorsContainer::iterator b = m_successors.begin();
  while (a != mdp_succs.end()) {
    while (b != m_successors.end() && b->first < a->first) {
      // Remove erstwhile successors.
      const StatePredictionRef &succ = b->second.first;
      if (succ)
	succ->remove_predecessor(m_this);
      b = m_successors.erase(b);
    }
    if (b == m_successors.end() || a->first < b->first) {
      // Add new successor.
      std::pair<StatePredictionRef, double> succ_weight;
      succ_weight.second = a->second;
      StatePredictionRef &succ = succ_weight.first;
      succ = m_predictor->prediction(a->first);
      succ->add_predecessor(m_this, a->second);
      // Use a NULL reference as a proxy for this to avoid maintain a
      // strong self-reference.
      if (succ.get() == this)
	succ.reset();
      SuccessorsContainer::value_type val(a->first, succ_weight);
      b = m_successors.insert(b, val);
    } else {
      // Update existing successor.
      assert(b->first == a->first);
      b->second.second = a->second;
    }
    ++a; ++b;
  }
  while (b != m_successors.end()) {
    // Remove erstwhile successors
    const StatePredictionRef &succ = b->second.first;
    succ->remove_predecessor(m_this);
    b = m_successors.erase(b);
  }
  propagate_reward_change();
  propagate_transitions_change();
}

void Predictor::State::propagate_reward_change()
{
  // std::cerr << "propagate_reward_change() at " << m_mdp_state->state() << "\n";
  
  // Compute new estimated reward
  double backup = 0.0;
  SuccessorsContainer::const_iterator i;
  for (i = m_successors.begin(); i != m_successors.end(); ++i) {
    const StatePredictionRef &succ = i->second.first;
    const double p = i->second.second;
    assert(std::isfinite(p));
    assert(p >= 0);
    // NULL reference is a proxy for this
    const double ret = succ ? succ->predict_return(p) : predict_return(p);
    // std::cerr << "\t" << p << ": " << i->first << " -> " << ret << "\n";
    assert(std::isfinite(ret));
    backup += ret;
    // if (!std::isfinite(backup)) {
    //   std::cerr << "*this at " << state() << "\nthis is " << this << "\nsucc is " << succ << "\np was" << p << "\nret was " << ret << "\n";
    // }
  }
  assert(std::isfinite(backup));
  backup *= m_predictor->m_gamma;
  assert(std::isfinite(backup));
  assert(std::isfinite(m_action->second->reward()));
  // std::cerr << "\tr = " << m_action->second->reward() << "\n";
  backup += m_action->second->reward();
  // std::cerr << "\tsum = " << backup << "\n";
  assert(std::isfinite(backup));

  const double error = std::fabs(backup - m_r);
  assert(std::isfinite(error));
  assert(error >= 0);
  m_r = backup;

  // Propagate change to predecessors
  std::vector<std::pair<StateWeakRef, double> >::iterator j;
  for (j = m_predecessors.begin(); j != m_predecessors.end(); ++j) {
    StateRef pred(j->first);
    assert(pred);
    const double p = j->second;
    assert(p >= 0);
    pred->increase_bound(REWARD, p * error);
  }
}

void Predictor::State::propagate_transitions_change()
{
  // Compute new transition probabilities.
  static EffectDistribution backup;
  backup.clear();
  SuccessorsContainer::const_iterator i;
  EffectDistribution::const_iterator a, b;
  for (i = m_successors.begin(); i != m_successors.end(); ++i) {
    const StatePredictionRef &succ = i->second.first;
    const double &one_step_transition_probability = i->second.second;
    if (succ)
      succ->predict_terminals(backup, one_step_transition_probability);
    else // NULL reference is a proxy for this
      predict_terminals(backup, one_step_transition_probability);
  }

  // Compute L1 error, for propagation threshold purposes
  double error = 0.0;
  a = backup.begin();
  b = m_effect_map.begin();
  while (a != backup.end()) {
    while (b != m_effect_map.end() && b->first < a->first) {
      error += b->second;
      ++b;
    }
    if (b == m_effect_map.end() || a->first < b->first) {
      error += a->second;
    } else {
      error += std::fabs(a->second - b->second);
      ++b;
    }
    ++a;
  }
  while (b != m_effect_map.end()) {
    error += b->second;
    ++b;
  }
  m_effect_map.swap(backup); // Swapping cheaper than copying.

  // Propagate to predecessors
  assert(error >= 0);
  std::vector<std::pair<StateWeakRef, double> >::iterator j;
  for (j = m_predecessors.begin(); j != m_predecessors.end(); ++j) {
    assert(j->second >= 0);
    StateRef predecessor(j->first);
    assert(predecessor);
    predecessor->increase_bound(TRANSITION, j->second * error);
  }
}

void Predictor::State::propagate_model_change()
{
  notify_model_observers();
}

void Predictor::State::increase_bound(queue_t idx, double increase)
{
  assert(increase >= 0);
  double &error_bound = m_error_bounds[idx];
  error_bound += increase;
  if (error_bound > m_predictor->m_epsilon) {
    int &heap_index = m_heap_indices[idx];
    if (heap_index < 0) {
      std::vector<StateRef> &pqueue = m_predictor->m_pqueues[idx];
      heap_index = static_cast<int>(pqueue.size());
      pqueue.push_back(m_this.lock());
    }
    insert_heapify(idx);
  }
}

void Predictor::State::heapify(queue_t idx)
{
  int &heap_index = m_heap_indices[idx];
  if (heap_index >= 0) {
    std::vector<StateRef> &pqueue = m_predictor->m_pqueues[idx];
    assert(pqueue[heap_index].get() == this);
    unsigned child_index = 2*heap_index + 1; // identify children
    while (child_index < pqueue.size()) { // as long as there's a child...
      StateRef *child = &pqueue[child_index];
      assert((*child)->m_heap_indices[idx] == static_cast<int>(child_index));
      unsigned other_child_index = child_index + 1;
      if (other_child_index < pqueue.size()) {
	StateRef *other_child = &pqueue[other_child_index];
	assert((*other_child)->m_heap_indices[idx] ==
	       static_cast<int>(other_child_index));
	// find the bigger child
	if ((*child)->m_error_bounds[idx] <
	    (*other_child)->m_error_bounds[idx]) {
	  child = other_child;
	  child_index = other_child_index;
	}
      }
      if (m_error_bounds[idx] < (*child)->m_error_bounds[idx]) {
	// if out errorbound is bigger than child's
	child->swap(pqueue[heap_index]); // swap spots with child
	// give child our index
	pqueue[heap_index]->m_heap_indices[idx] = heap_index;
	heap_index = child_index; // and take the child's index
	child_index = 2 * child_index + 1;
      } else
	break;
    }
  }
}

void Predictor::State::insert_heapify(queue_t idx)
{
  int &heap_index = m_heap_indices[idx];
  std::vector<StateRef> &pqueue = m_predictor->m_pqueues[idx];
  assert(heap_index < 0 || pqueue[heap_index].get() == this);
  while (heap_index > 0) {
    int parent_index = (heap_index - 1)/2;
    StateRef *parent = &pqueue[parent_index];
    assert((*parent)->m_heap_indices[idx] == parent_index);
    if (m_error_bounds[idx] <= (*parent)->m_error_bounds[idx])
      break;
    parent->swap(pqueue[heap_index]); // Swap with parent
    // parent takes my index
    pqueue[heap_index]->m_heap_indices[idx] = heap_index;
    heap_index = parent_index; // I take parent's index
  }
}

void Predictor::State::dequeue(queue_t idx)
{
  int &heap_index = m_heap_indices[idx];
  assert(heap_index >= 0);
  std::vector<StateRef> &pqueue = m_predictor->m_pqueues[idx];
  StateRef *hole = &pqueue[heap_index];
  hole->swap(pqueue.back()); // Swap spots with back
  pqueue.pop_back(); // remove back in vector
  if (heap_index != static_cast<int>(pqueue.size())) {
    const StateRef old_back = *hole;

    // Give the old back element our index.
    old_back->m_heap_indices[idx] = heap_index;

    // Move old back element toward front if necessary (unlikely)
    if (heap_index > 0)
      old_back->insert_heapify(idx);

    // Move old back toward back if necessary.
    old_back->heapify(idx);
  }
  heap_index = -1; // remove this DecisionState from queue
}

Predictor::StateRef Predictor::
prediction(const ModelBasedStatePolicyRef &pi_s, StatePredictionWeakRef *ptr)
{
  StateWeakRef &st = m_predictions[pi_s->state()];
  if (st.expired())
    return State::create(m_this.lock(), pi_s, st, ptr);
  else
    return st.lock();
}

Predictor::StatePredictionRef Predictor::prediction(const StateVectorRef &s)
{
  StatePredictionWeakRef &st = m_succ_predictions[s];
  if (st.expired()) {
    if ((*m_terminalp)(s))
      return TerminalStatePrediction::create(s, m_dimensions, st);
    else
      return prediction(m_planner->policy(s), &st);
  } else
    return st.lock();
}
