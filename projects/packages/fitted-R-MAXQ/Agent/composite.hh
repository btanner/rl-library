#ifndef _COMPOSITE_HH_
#define _COMPOSITE_HH_

/** \file
    Definitions of tasks that estimate their models from the models of
    other actions. */

#include "state.hh"
#include "action.hh"
#include "averager.hh"
#include "mdp.hh"
#include "planner.hh"
#include "predictor.hh"

class CompositeTask: public Task {
public:
  template <class InputIterator>
  static TaskRef create(const std::string &name,
			InputIterator subtasks_begin,
			InputIterator subtasks_end,
			const StatePredicateRef &initiation_set,
			const StatePredicateRef &termination_condition,
			const StateFunctionRef &goal_function,
			const AveragerRef &averager,
			double gamma,
			double epsilon);

  virtual const std::string &name() const;
  virtual bool available(const StateVectorRef &s) const;
  virtual bool terminal(const StateVectorRef &s) const;
  virtual StatePolicyRef policy(const StateVectorRef &s);
  virtual StateActionModelRef model(const StateVectorRef &s);
  virtual void propagate_changes();

  virtual void debug(std::ostream &out);

  void write_value_function(std::ostream &out) const;
  void write_policy(std::ostream &out) const;

private:
  template <class InputIterator>
  CompositeTask(const std::string &name,
		InputIterator subtasks_begin,
		InputIterator subtasks_end,
		const StatePredicateRef &initiation_set,
		const StatePredicateRef &termination_condition,
		const StateFunctionRef &goal_function,
		const AveragerRef &averager,
		double gamma,
		double epsilon);

  const std::string m_name;

  const StatePredicateRef m_init;
  const StatePredicateRef m_term;

  const MDPRef m_mdp;
  const PlannerRef m_planner;
  const PredictorRef m_predictor;
};

template <class InputIterator>
TaskRef CompositeTask::create(const std::string &name,
			      InputIterator subtasks_begin,
			      InputIterator subtasks_end,
			      const StatePredicateRef &initiation_set,
			      const StatePredicateRef &termination_condition,
			      const StateFunctionRef &goal_function,
			      const AveragerRef &averager,
			      double gamma,
			      double epsilon)
{
  return TaskRef(new CompositeTask(name, subtasks_begin, subtasks_end,
				   initiation_set, termination_condition,
				   goal_function, averager, gamma, epsilon));
}

template <class InputIterator>
CompositeTask::CompositeTask(const std::string &name,
			     InputIterator subtasks_begin,
			     InputIterator subtasks_end,
			     const StatePredicateRef &initiation_set,
			     const StatePredicateRef &termination_condition,
			     const StateFunctionRef &goal_function,
			     const AveragerRef &averager,
			     double gamma,
			     double epsilon):
  m_name(name),
  m_init(initiation_set),
  m_term(termination_condition),
  m_mdp(MDP::create(subtasks_begin, subtasks_end, averager)),
  m_planner(Planner::create(m_mdp, m_term, goal_function, gamma, epsilon)),
  m_predictor(Predictor::create(m_planner, m_term, gamma, epsilon,
				averager->dimensions()))
{}

#endif
