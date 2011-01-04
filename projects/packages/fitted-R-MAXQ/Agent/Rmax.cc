#include "FittedRmaxq.hh"

namespace {
  /** The amount of data required before considering a state-action
      explored. */
  const unsigned explorationthreshold = 5;

  /** An upper bound on the value of a state or state-action. */
  const double maxval = 20.0;

  /** Factor multiplied to expected future rewards, known as gamma in
      the RL literature. */
  const double discountfactor = 1.0;

  /** Terminal Bellman residual threshold for value iteration.
      Planning terminates after each time step when the largest value
      change is smaller than this threshold. */
  const double epsilon = 0.01;

  /** Controls the resolution of the evenly spaced grid used to
      approximate the model and the value function, if any state
      variables have floating-point values.  For each unit distance in
      the scaled state space, the grid will have 2^(resolutionfactor)
      points. */
  const int resolutionfactor = 4;

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

  int firstaction = getIntActMin(ts, 0);
  int lastaction = getIntActMax(ts, 0);
  // Create primitive tasks.
  std::vector<TaskRef> primitives;
  for (int i = firstaction; i <= lastaction; ++i) {
    TaskRef task;
    if (continuous_dim > 0) {
      task = DiscretizedPrimitiveTask::create("",
					      i,
					      explorationthreshold,
					      maxval,
					      resolutionfactor,
					      dimensions,
					      truep);
    } else {
      DynamicAveragerRef model_averager = ProjectionAverager::create(dimensions);
      task = PrimitiveTask::create("",
				   i,
				   explorationthreshold,
				   maxval,
				   model_averager,
				   truep);
    }
    primitives.push_back(task);
  }

  // Create root task
  AveragerRef value_averager = ProjectionAverager::create(dimensions);
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
