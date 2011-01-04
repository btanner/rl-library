#include "mdp.hh"

MDPStateAction::~MDPStateAction()
{}

MDPState::Observer::~Observer()
{}

MDPState::~MDPState()
{}

void MDPState::add_observer(const ObserverWeakRef &o)
{
  vector_set_insert(m_observers, o);
}

void MDPState::remove_observer(const ObserverWeakRef &o)
{
  vector_set_erase(m_observers, o);
}

void MDPState::notify_MDP_observers(const TaskRef &a)
{
  std::vector<ObserverWeakRef>::iterator i;
  for (i = m_observers.begin(); i != m_observers.end(); ++i)
    i->lock()->observe_MDP_change(a);
}

MDP::~MDP()
{}

MDPStateRef MDP::state_data(const StateVectorRef &s)
{
  AverageRef average = m_averager->approximate(s);
  StateWeakRef &data = m_state_map[average->state()];
  if (data.expired())
    return State::create(m_this.lock(), average, data);
  else
    return data.lock();
}

void MDP::propagate_changes()
{
  std::set<State::ActionWeakRef>::iterator i;
  for (i = m_inbox.begin(); i != m_inbox.end(); ++i)
    if (!i->expired())
      i->lock()->compute_successors();
  m_inbox.clear();
}

MDP::State::ActionRef
MDP::State::Action::create(const StateWeakRef &parent,
			   const TaskRef &action,
			   const StateActionModelRef &model)
{
  ActionRef result(new Action(parent, action, model));
  result->m_this = result;
  result->m_model->add_observer(result->m_this);
  result->compute_successors();
  return result;
}

MDP::State::Action::
Action(const StateWeakRef &parent,
       const TaskRef &action,
       const StateActionModelRef &model):
  m_parent(parent), m_action(action), m_model(model)
{}

MDP::State::Action::~Action()
{
  std::vector<std::pair<EffectRef, AverageRef> >::const_iterator i;
  for (i = m_averages.begin(); i != m_averages.end(); ++i) {
    const AverageRef &avg = i->second;
    if (avg)
      avg->remove_observer(m_this);
  }
  m_model->remove_observer(m_this);
}

double MDP::State::Action::reward() const
{
  return m_model->reward();
}

const StateDistribution &MDP::State::Action::successor_probabilities() const
{
  return m_succs;
}

void MDP::State::Action::observe_model_change()
{
  m_parent.lock()->m_mdp->m_inbox.insert(m_this);
}

void MDP::State::Action::observe_average_change()
{
  m_parent.lock()->m_mdp->m_inbox.insert(m_this);
}

void MDP::State::Action::compute_successors()
{
  StateRef parent(m_parent);
  const StateVectorRef &s = parent->state();
  assert(parent);
  m_succs.clear(); // This is what we're recomputing.
  // Compare model's given effects against saved effect->average mapping
  const EffectDistribution &effects = m_model->effects();
  EffectDistribution::const_iterator a = effects.begin();
  std::vector<std::pair<EffectRef, AverageRef> >::iterator b =
    m_averages.begin();
  while (a != effects.end()) {
    while (b != m_averages.end() && b->first < a->first) {
      AverageRef &avg = b->second;
      if (avg)
	avg->remove_observer(m_this); // remove erstwhile averages
      b = m_averages.erase(b);
    }
    if (b == m_averages.end() || a->first < b->first) { // add a new average
      const EffectRef &effect = a->first;
      StateVectorRef successor = effect->apply(s);
      AverageRef succavg;
      if (*successor != *s) {
	// Not a self transition, so we approximate
	succavg = parent->m_mdp->m_averager->approximate(successor);
	succavg->add_observer(m_this);
      }
      b = m_averages.insert(b, std::make_pair(a->first, succavg));
    }
    // Add the translated successors for a->first == b->first
    AverageRef &average = b->second;
    if (!average) {
      // Effect was a self-transition, so use parent's state_t ptr
      StateDistribution::iterator ins =
	vector_set_insert(m_succs,
			  std::make_pair(parent->state(), 0.0),
			  LessFirst<StateDistribution::value_type>()).first;
      ins->second += a->second;      
    } else { // Usually, not a self-transition
      const StateDistribution &avg_succ = average->basis_weights();
      StateDistribution::const_iterator i;
      for (i = avg_succ.begin(); i != avg_succ.end(); ++i) {
	StateDistribution::iterator ins =
	  vector_set_insert(m_succs,
			    std::make_pair(i->first, 0.0),
			    LessFirst<StateDistribution::value_type>()).first;
	ins->second += i->second * a->second; // assumes norm == 1
      }
    }
    ++a;
    ++b;
  }
  while (b != m_averages.end()) { // No longer in model, so delete
    AverageRef &avg = b->second;
    if (avg)
      avg->remove_observer(m_this);
    b = m_averages.erase(b);
  }
  parent->notify_MDP_observers(m_action);
}

MDP::StateRef MDP::State::create(const MDPRef &mdp,
				 const AverageRef &average,
				 StateWeakRef &ref)
{
  StateRef result(new State(mdp, average));
  ref = result->m_this = result;
  const StateVectorRef &s = average->state();
  std::vector<TaskRef>::const_iterator i;
  for (i = mdp->m_actions.begin(); i != mdp->m_actions.end(); ++i) {
    const TaskRef &a = *i;
    if (a->available(s)) {
      StateActionModelRef model = a->model(s);
      MDPStateActionRef sa = Action::create(result, a, model);
      vector_map_assoc(result->m_state_action_map, a, sa);
    }
  }
  return result;
}

MDP::State::State(const MDPRef &mdp, const AverageRef &average):
  m_mdp(mdp), m_average(average)
{
//   std::cerr << "MDP::State::State() with " << mdp->m_actions.size()
// 	    << " actions\n";
}

MDP::State::~State()
{
  m_mdp->m_state_map.erase(m_average->state());
}

const StateVectorRef &MDP::State::state() const
{
  return m_average->state();
}

const std::vector<std::pair<TaskRef, MDPStateActionRef> > &
MDP::State::state_actions() const
{
  return m_state_action_map;
}

void MDP::State::observe_average_change()
{}
