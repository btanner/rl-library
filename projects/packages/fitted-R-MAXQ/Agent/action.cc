#include "action.hh"

#include <algorithm>
#include <cassert>

Effect::~Effect()
{}

std::ostream &operator<<(std::ostream &out, const EffectRef &e)
{
  e->debug(out);
  return out;
}

AbsoluteEffectRef AbsoluteEffect::create(const StateVectorRef &succ,
			 const StateVariables &dimensions)
{
  return AbsoluteEffectRef(new AbsoluteEffect(succ, dimensions));
}

AbsoluteEffect::AbsoluteEffect(const StateVectorRef &succ,
			       const StateVariables &dimensions)
{
  StateVariables::const_iterator i;
  for (i = dimensions.begin(); i != dimensions.end(); ++i) {
    const unsigned index = i->first;
    assert(succ->size() > index);
    m_changes.push_back(std::make_pair(index, (*succ)[index]));
  }
}

AbsoluteEffect::~AbsoluteEffect()
{}

StateVectorRef AbsoluteEffect::apply(const StateVectorRef &s) const
{
  boost::shared_ptr<std::vector<double> > result(new std::vector<double>(*s));
  std::vector<std::pair<unsigned,double> >::const_iterator i;
  for (i = m_changes.begin(); i != m_changes.end(); ++i) {
    assert(result->size() > i->first);
    (*result)[i->first] = i->second;
  }
  return result;
}

void AbsoluteEffect::debug(std::ostream &out) const
{
  out << "AbsoluteEffect::debug not yet implemented.\n";
}

StateActionModel::Observer::~Observer()
{}

StateActionModel::~StateActionModel()
{}

void StateActionModel::add_observer(const ObserverWeakRef &o)
{
  vector_set_insert(m_observers, o);
}

void StateActionModel::remove_observer(const ObserverWeakRef &o)
{
  vector_set_erase(m_observers, o);
}

void StateActionModel::debug(std::ostream &out) const
{
  out << "StateActionModel " << state() << ": r = " << reward() << "\n";
  EffectDistribution::const_iterator i;
  double p = 1.0;
  for (i = effects().begin(); i != effects().end(); ++i) {
    out << "\t" << i->second << ": " << i->first << "\n";
    p -= i->second;
  }
  if (p > 0)
    out << "\t" << p << ": terminate\n";
}

void StateActionModel::notify_model_observers() {
  size_t index = 0;
  while (index < m_observers.size()) {
    if (m_observers[index].expired())
      m_observers.erase(m_observers.begin() + index);
    else {
      ObserverRef observer(m_observers[index++]);
      observer->observe_model_change();
    }
  }
}

StatePolicy::Observer::~Observer()
{}

StatePolicy::~StatePolicy()
{} 

void StatePolicy::add_observer(const ObserverWeakRef &o)
{
  vector_set_insert(m_observers, o);
}

void StatePolicy::remove_observer(const ObserverWeakRef &o)
{
  vector_set_erase(m_observers, o);
}

void StatePolicy::notify_policy_observers()
{
  size_t index = 0;
  while (index < m_observers.size()) {
    if (m_observers[index].expired())
      m_observers.erase(m_observers.begin() + index);
    else {
      ObserverRef observer(m_observers[index++]);
      observer->observe_policy_change();
    }
  }
}

void StatePolicy::debug(std::ostream &out) const
{}

Task::~Task()
{}
