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
  // Number of state variables
  const unsigned discrete_dim = getNumIntObs(ts);
  dimensions.clear();
  for (unsigned i = 0; i < discrete_dim; ++i)
    dimensions[i] = 1.0;

  const unsigned continuous_dim = getNumDoubleObs(ts);
  for (unsigned i = 0; i < continuous_dim; ++i) {
    const double max = getDoubleObsMax(ts, i);
    const double min = getDoubleObsMin(ts, i);
    dimensions[discrete_dim + i] = 1.0 / (max - min);
  }

  assert(getNumIntAct(ts) == 1);
  assert(getNumDoubleAct(ts) == 0);
  int firstaction = getIntActMin(ts, 0);
  int lastaction = getIntActMax(ts, 0);
  // Create primitive tasks.
  std::vector<TaskRef> primitives;
  for (int i = firstaction; i <= lastaction; ++i) {
    DynamicAveragerRef model_averager = KernelAverager::create(modelbreadth,
							       minweight,
							       minfraction,
							       dimensions);
    TaskRef task = PrimitiveTask::create("",
					 i,
					 explorationthreshold,
					 maxval,
					 model_averager,
					 truep);

    primitives.push_back(task);
  }

  // Create root task
  AveragerRef value_averager =
    InterpolationAverager::create(resolutionfactor, dimensions);
  return CompositeTask::create("ROOT",
			       primitives.begin(),
			       primitives.end(),
			       truep,
			       falsep,
			       zerof,
			       value_averager,
			       discountfactor,
			       epsilon);
}
