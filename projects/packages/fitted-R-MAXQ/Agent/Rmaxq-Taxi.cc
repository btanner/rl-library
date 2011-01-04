#include "FittedRmaxq.hh"

namespace {
  /** The amount of data required before considering a state-action
      explored, including data generalized (and weighted) from nearby
      states. */
  const double explorationthreshold = 5.0;

  /** An upper bound on the value of a state or state-action. */
  const double maxval = 20.0;

  /** Factor multiplied to expected future rewards, known as gamma in
      the RL literature. */
  const double discountfactor = 1.0;

  /** Terminal Bellman residual threshold for value iteration.
      Planning terminates after each time step when the largest value
      change is smaller than this threshold. */
  const double epsilon = 0.01;

  StatePredicateRef truep = TrueStatePredicate::create();
  StatePredicateRef falsep = FalseStatePredicate::create();
  StateFunctionRef upper_bound = ConstantStateFunction::create(maxval);
} // end of anonymous namespace

TaskRef create_subtask(const std::string &name,
		       StatePredicateRef termination,
		       const std::vector<TaskRef> &children,
		       DynamicAveragerRef state_variables)
{
  return CompositeTask::create(name,
			       children.begin(),
			       children.end(),
			       NegationPredicate::create(termination),
			       termination,
			       upper_bound,
			       state_variables,
			       discountfactor,
			       epsilon);
}

TaskRef get_task_hierarchy(taskspec_t *ts)
{
  // Verify that the environment looks like the Taxi domain.
  assert(getNumDoubleObs(ts) == 0);
  assert(getNumDoubleAct(ts) == 0);
  assert(getNumIntObs(ts) == 4);
  assert(getNumIntAct(ts) == 1);
  assert(getIntActMin(ts, 0) == 0);
  assert(getIntActMax(ts, 0) == 5);

  StateVariables dimensions;

  // Create north, south, east, west
  static const char *nsew[] = { "north", "south", "east", "west" };
  std::vector<TaskRef> navigation_primitives;
  for (unsigned i = 0; i < 2; ++i)
    dimensions[i] = 1.0;
  DynamicAveragerRef xy_space = ProjectionAverager::create(dimensions);
  for (int i = 0; i < 4; ++i) {
    TaskRef primitive = PrimitiveTask::create(nsew[i],
					      i,
					      explorationthreshold,
					      maxval,
					      xy_space,
					      truep);
    navigation_primitives.push_back(primitive);
  }

  std::vector<TaskRef> navigation_subtasks;

  // Create NAVIGATE(RED)
  std::map<unsigned,double> red_landmark;
  red_landmark[0] = 4.0;
  red_landmark[1] = 0.0;
  StatePredicateRef red_landmark_predicate =
    GoalStatePredicate::create(red_landmark.begin(), red_landmark.end());
  navigation_subtasks.push_back(create_subtask("NAVIGATE(RED)",
					       red_landmark_predicate,
					       navigation_primitives,
					       xy_space));

  // Create NAVIGATE(BLUE)
  std::map<unsigned,double> blue_landmark;
  blue_landmark[0] = 0.0;
  blue_landmark[1] = 3.0;
  StatePredicateRef blue_landmark_predicate =
    GoalStatePredicate::create(blue_landmark.begin(), blue_landmark.end());
  navigation_subtasks.push_back(create_subtask("NAVIGATE(BLUE)",
					       blue_landmark_predicate,
					       navigation_primitives,
					       xy_space));

  // Create NAVIGATE(GREEN)
  std::map<unsigned,double> green_landmark;
  green_landmark[0] = 4.0;
  green_landmark[1] = 4.0;
  StatePredicateRef green_landmark_predicate =
    GoalStatePredicate::create(green_landmark.begin(), green_landmark.end());
  navigation_subtasks.push_back(create_subtask("NAVIGATE(GREEN)",
					       green_landmark_predicate,
					       navigation_primitives,
					       xy_space));

  // Create NAVIGATE(YELLOW)
  std::map<unsigned,double> yellow_landmark;
  yellow_landmark[0] = 0.0;
  yellow_landmark[1] = 0.0;
  StatePredicateRef yellow_landmark_predicate =
    GoalStatePredicate::create(yellow_landmark.begin(), yellow_landmark.end());
  navigation_subtasks.push_back(create_subtask("NAVIGATE(YELLOW)",
					       yellow_landmark_predicate,
					       navigation_primitives,
					       xy_space));

  // Create pickup primitive
  dimensions[2] = 1.0;
  DynamicAveragerRef xy_pass_space = ProjectionAverager::create(dimensions);
  TaskRef pickup_primitive = PrimitiveTask::create("pickup",
						   4,
						   explorationthreshold,
						   maxval,
						   xy_pass_space,
						   truep);

  // Create GET subtask
  std::map<unsigned,double> passenger_in_taxi_landmark;
  passenger_in_taxi_landmark[2] = 4.0;
  StatePredicateRef passenger_in_taxi =
    GoalStatePredicate::create(passenger_in_taxi_landmark.begin(),
			       passenger_in_taxi_landmark.end());
  std::vector<TaskRef> children = navigation_subtasks;
  children.push_back(pickup_primitive);
  TaskRef get_subtask = create_subtask("GET",
				       passenger_in_taxi,
				       children,
				       xy_pass_space);

  // Create putdown primitive
  dimensions[3] = 1.0;
  DynamicAveragerRef full_space = ProjectionAverager::create(dimensions);
  TaskRef putdown_primitive = PrimitiveTask::create("putdown",
						    5,
						    explorationthreshold,
						    maxval,
						    full_space,
						    truep);

  // Create PUT subtask
  children = navigation_subtasks;
  children.push_back(putdown_primitive);
  TaskRef put_subtask = create_subtask("PUT",
				       NegationPredicate::create(passenger_in_taxi),
				       children,
				       full_space);

  // Create ROOT task
  children.clear();
  children.push_back(get_subtask);
  children.push_back(put_subtask);
  return create_subtask("ROOT", falsep, children, full_space);
}
