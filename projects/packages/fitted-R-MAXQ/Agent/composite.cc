#include "composite.hh"

const std::string &CompositeTask::name() const
{
  return m_name;
}

bool CompositeTask::available(const StateVectorRef &s) const
{
//   std::cerr << "CompositeTask::available()\n";
  return (*m_init)(s) && !(*m_term)(s);
}

bool CompositeTask::terminal(const StateVectorRef &s) const
{
  return (*m_term)(s);
}

StatePolicyRef CompositeTask::policy(const StateVectorRef &s)
{
  return m_planner->policy(s);
}

StateActionModelRef CompositeTask::model(const StateVectorRef &s)
{
  // std::cerr << "CompositeTask::model with subgoal ";
  // m_term->debug(std::cerr);

  return m_predictor->model(s);
}

// XXX These methods can revisit a descendent more than once....
void CompositeTask::propagate_changes()
{
  std::vector<TaskRef>::const_iterator i;
  for (i = m_mdp->subtasks().begin(); i != m_mdp->subtasks().end(); ++i) {
    const TaskRef &subtask = *i;
    subtask->propagate_changes();
  }

  m_mdp->propagate_changes();
  m_planner->propagate_changes();
  m_predictor->propagate_changes();

  // std::cerr << "Policy for task with subgoal: ";
  // m_term->debug(std::cerr);
  // m_planner->debug(std::cerr);
}

void CompositeTask::debug(std::ostream &out)
{
  m_planner->debug(out);
}

void CompositeTask::write_value_function(std::ostream &out) const
{
  m_planner->write_value_function(out);
}

void CompositeTask::write_policy(std::ostream &out) const
{
  m_planner->write_policy(out);
}

