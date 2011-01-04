#include "FittedRmaxq.hh"

namespace {
  /** The amount of data required before considering a state-action
      explored, including data generalized (and weighted) from nearby
      states. */
  const double explorationthreshold = 1.0;

  /** An upper bound on the value of a state or state-action. */
  const double maxval = 0.0;

  /** Factor multiplied to expected future rewards, known as gamma in
      the RL literature. */
  const double discountfactor = 1.0;

  /** Terminal Bellman residual threshold for value iteration.
      Planning terminates after each time step when the largest value
      change is smaller than this threshold. */
  const double epsilon = 0.01;

  /** Degree of generalization used to estimate the model for each
      action.  A Gaussian kernel with this standard deviation (in the
      scaled state space) weights the contribution of nearby states to
      the model for a given action at a given state. */
  const double modelbreadth = 1.0 / 16.0;

  /** Controls the resolution of the evenly spaced grid used to
      approximate the value function.  For each unit distance in the
      scaled state space, the grid will have 2^(resolutionfactor)
      points. */
  const int resolutionfactor = 4;

  /** Threshold for the weight of a transition used to approximate an
      action's model, as a fraction of the maximum possible weight of
      a transition.  Combined with modelbreadth, determines the
      maximum distance over which generalization can occur.  Setting
      this number to 1.0 would remove all generalization.  */
  const double minweight = 0.01;

  /** Threshold for the weight of a transition used to approximate an
      action's model, as a fraction of the cumulative weight of
      higher-weighted transitions. */
  const double minfraction = 0.01;

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

  // Create four navigational actions.
  std::vector<TaskRef> navigational_primitives;
  dimensions.clear();
  for (unsigned i = 0; i < 2; ++i)
    dimensions[i] = 1.0 / (getDoubleObsMax(ts, i) - getDoubleObsMin(ts, i));

  // Depend only on first two state variables: x-y coordinates.
  static const char *udrl[] = { "up", "down", "right", "left" };
  for (int i = firstaction; i < firstaction + 4; ++i) {
    DynamicAveragerRef model_averager =
      KernelAverager::create(modelbreadth, minweight, minfraction, dimensions);
    TaskRef task =
      PrimitiveTask::create(udrl[i - firstaction],
			    i, explorationthreshold,
			    maxval, model_averager, truep);
    navigational_primitives.push_back(task);
  }

  std::vector<TaskRef> rootchildren;

  // Fifth action only available if all subgoals have been achieved,
  // and its behavior otherwise does not depend on the current state.
  std::map<unsigned,double> goalcondition;
  for (unsigned i = 2; i < dim; ++i)
    goalcondition[i] = 1.0;
  StatePredicateRef all_goals =
    GoalStatePredicate::create(goalcondition.begin(), goalcondition.end());
  DynamicAveragerRef model_averager =
    KernelAverager::create(modelbreadth, minweight, minfraction,
			   StateVariables());
  TaskRef final_action =
    PrimitiveTask::create("finish", firstaction + 4, explorationthreshold, maxval,
			  model_averager, all_goals);
  rootchildren.push_back(final_action);

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
    DynamicAveragerRef model_averager =
      KernelAverager::create(modelbreadth, minweight, minfraction, dimensions);
    std::string primitive_name = "pickup";
    primitive_name.append(1, 'A' + i);
    TaskRef subgoal_primitive =
      PrimitiveTask::create(primitive_name,
			    i + firstaction + 5, explorationthreshold,
			    maxval, model_averager, precond);

    std::vector<TaskRef> subgoal_children(navigational_primitives);
    subgoal_children.push_back(subgoal_primitive);
    AveragerRef value_averager =
      InterpolationAverager::create(resolutionfactor, dimensions);
    std::string composite_name = "GET-";
    composite_name.append(1, 'A' + i);
    TaskRef subgoal = CompositeTask::create(composite_name,
					    subgoal_children.begin(),
					    subgoal_children.end(),
					    precond,
					    postcond,
					    zerof,
					    value_averager,
					    discountfactor,
					    epsilon);
    rootchildren.push_back(subgoal);

    dimensions.erase(i+2);
  }

  // Finally, create root task with full state variable representation.
  for (unsigned i = 2; i < dim; ++i)
    dimensions[i] = 1.0 / (getDoubleObsMax(ts, i) - getDoubleObsMin(ts, i));
  AveragerRef value_averager =
    InterpolationAverager::create(resolutionfactor, dimensions);
  return CompositeTask::create("ROOT",
			       rootchildren.begin(),
			       rootchildren.end(),
			       truep,
			       falsep,
			       zerof,
			       value_averager,
			       discountfactor,
			       epsilon);
}
