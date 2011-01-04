#include "FittedRmaxq.hh"

namespace {
  /** The amount of data required before considering a state-action
      explored, including data generalized (and weighted) from nearby
      states. */
  const double explorationthreshold = 2.0;

  /** An upper bound on the value of a state or state-action. */
  const double maxval = 0.0;

  /** Factor multiplied to expected future rewards, known as gamma in
      the RL literature. */
  const double discountfactor = 1.0;

  /** Terminal Bellman residual threshold for value iteration.
      Planning terminates after each time step when the largest value
      change is smaller than this threshold. */
  const double epsilon = 0.01;

  /** Controls the resolution of the evenly spaced grid used to
      approximate the value function.  For each unit distance in the
      scaled state space, the grid will have 2^(resolutionfactor)
      points. */
  const int resolutionfactor = 4;

  StatePredicateRef truep = TrueStatePredicate::create();
  StatePredicateRef falsep = FalseStatePredicate::create();
  StateFunctionRef zerof = ZeroStateFunction::create();

  StateVariables dimensions;
} // end of anonymous namespace

TaskRef get_task_hierarchy(taskspec_t *ts)
{
  assert(getNumIntObs(ts) == 0);
  // Number of state variables
  const unsigned dim = getNumDoubleObs(ts);

  // First four actions are navigational.  Fifth is used to terminate
  // an episode.  Remaining actions activate subgoals.
  int firstaction = getIntActMin(ts, 0);
  int lastaction = getIntActMax(ts, 0);
  int num_actions = lastaction - firstaction + 1;
  int numgoals = num_actions - 4 - 1; // Four movement actions, one end-of-task

  std::vector<TaskRef> root_children;

  // Create four navigational actions.
  dimensions.clear();
  for (unsigned i = 0; i < 2; ++i)
    dimensions[i] = 1.0 / (getDoubleObsMax(ts, i) - getDoubleObsMin(ts, i));

  // Depend only on first two state variables: x-y coordinates.
  static const char *udrl[] = { "up", "down", "right", "left" };
  for (int i = firstaction; i < firstaction + 4; ++i) {
    TaskRef task = DiscretizedPrimitiveTask::create(udrl[i - firstaction],
						    i,
						    explorationthreshold,
						    maxval,
						    resolutionfactor,
						    dimensions,
						    truep);
    root_children.push_back(task);
  }

  // Fifth action only available if all subgoals have been achieved,
  // and its behavior otherwise does not depend on the current state.
  std::map<unsigned,double> goalcondition;
  for (unsigned i = 2; i < dim; ++i)
    goalcondition[i] = 1.0;
  StatePredicateRef all_goals =
    GoalStatePredicate::create(goalcondition.begin(), goalcondition.end());
  TaskRef final_action = DiscretizedPrimitiveTask::create("finish",
							  firstaction + 4,
							  explorationthreshold,
							  maxval,
							  resolutionfactor,
							  dimensions,
							  all_goals);
  root_children.push_back(final_action);

  // Subgoal attainment actions depend on three state variables: the
  // x-y coordinates and the corresponding goal attainment flag.
  // These actions should only be available when the corresponding
  // flag is not yet set.
  for (int i = 0; i < numgoals; ++i) {
    goalcondition.clear();
    goalcondition[i + 2] = 0.0;
    StatePredicateRef precond =
      GoalStatePredicate::create(goalcondition.begin(), goalcondition.end());
    goalcondition[i + 2] = 1.0;
    StatePredicateRef postcond =
      GoalStatePredicate::create(goalcondition.begin(), goalcondition.end());
    dimensions[i+2] = 1.0 / (getDoubleObsMax(ts, i+2) - getDoubleObsMin(ts, i+2));
    std::string name = "pickup";
    name.append(1, 'A' + i);
    TaskRef subgoal_primitive =
      DiscretizedPrimitiveTask::create(name,
				       i + firstaction + 5,
				       explorationthreshold,
				       maxval,
				       resolutionfactor,
				       dimensions,
				       precond);

    root_children.push_back(subgoal_primitive);

    dimensions.erase(i+2);
  }

  // Finally, create root task with full state variable representation.
  for (unsigned i = 2; i < dim; ++i)
    dimensions[i] = 1.0 / (getDoubleObsMax(ts, i) - getDoubleObsMin(ts, i));
  AveragerRef value_averager = ProjectionAverager::create(dimensions);
  return CompositeTask::create("ROOT",
			       root_children.begin(),
			       root_children.end(),
			       truep,
			       falsep,
			       zerof,
			       value_averager,
			       discountfactor,
			       epsilon);
}
