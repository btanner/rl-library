#ifndef _PLANNER_HH_
#define _PLANNER_HH_

/** \file This code computes an optimal policy for a given MDP. */

#include <ext/hash_map>
#include <map>
#include <set>
#include <vector>

#include "action.hh"
#include "mdp.hh"

/** A refinement of the StatePolicy interface that provides a pointer
    to the local MDP data for the represented state.  This information
    is used by a predictor to perform policy evaluation. */
class ModelBasedStatePolicy: public StatePolicy {
public:
  virtual ~ModelBasedStatePolicy();

  virtual MDPStateRef policy_model() const = 0;
};

typedef boost::shared_ptr<ModelBasedStatePolicy> ModelBasedStatePolicyRef;
typedef boost::weak_ptr<ModelBasedStatePolicy> ModelBasedStatePolicyWeakRef;

class Planner;
typedef boost::shared_ptr<Planner> PlannerRef;
typedef boost::weak_ptr<Planner> PlannerWeakRef;

/** Class that uses value iteration to output StatePolicy objects
    given an MDP. */
class Planner {
public:
  /** \param mdp The Markov decision process for which to compute the
                 optimal policy
      \param terminal A function that specifies terminal states, where
                      the policy may not determine the action taken.
                      The value of this function may not change.
      \param goal A function specifying the value of each terminal
                  state.  The value of this function may not change.
      \param gamma The discount factor to use
      \param epsilon This planner will not back up changes in value
                     smaller than this threshold */
  static PlannerRef create(const MDPRef &mdp,
			   const StatePredicateRef &terminal,
			   const StateFunctionRef &goal,
			   double gamma,
			   double epsilon);

  ~Planner();

  /** Outputs (after possibly computing) the optimal policy for the MDP.
      \param s The state at which to evaluate the optimal policy
      \return A object specifying the optimal child action, as well as
              giving access to the MDPState describing that
              state-action's behavior. */
  ModelBasedStatePolicyRef policy(const StateVectorRef &s);

  /** Updates the ModelBasedStatePolicy objects returned by policy
      given changes to the MDP used as the input to this Planner. */
  void propagate_changes();
  
  /** For debugging purposes: do full sweeps over all state objects
      until convergence, skipping prioritization logic. */
  void perform_standard_value_iteration();

  void debug(std::ostream &out);

  void write_value_function(std::ostream &out) const;
  void write_policy(std::ostream &out) const;

protected:
  class ValueState;
  typedef boost::shared_ptr<ValueState> ValueStateRef;
  typedef boost::weak_ptr<ValueState> ValueStateWeakRef;

  /** Base class for states that are reachable from some state given
      to policy(), which must have some value that can be backed up. */
  class ValueState {
  public:
    /** Interface for objects that want to receive notification when
	the value of a ValueState changes. */
    class Observer {
    public:
      virtual ~Observer();

      /** Method called by a ValueState when its value changes.
	  \param change The weighted difference between the new and
	                old values, weighted by the probability given
	                to the ValueState in add_observer().  A
	                positive change implies the value
	                increased. */
      virtual void observe_value_change(double change) = 0;
    };

    typedef boost::shared_ptr<Observer> ObserverRef;
    typedef boost::weak_ptr<Observer> ObserverWeakRef;

    /** \param ref A pointer that should be assigned the new
	           ValueState
        \param value The initial value of this ValueState */
    static ValueStateRef create(ValueStateWeakRef &ref,
				double value = 0.0);

    virtual ~ValueState();

    /** Accessor method for the value of this ValueState. */
    double value() const {
      return m_V;
    }

    /** Informs this ValueState that the Observer would like to
	receive notifications of changes to the value of this
	ValueState.
	\param o The object that desires notification
	\param weight The weight to multiply to the change in value in
	              the notification.  Typically this weight is the
	              probability that a MDPStateAction will
	              "transition" to this ValueState. */
    virtual void add_observer(const ObserverWeakRef &o, double weight);

    virtual void remove_observer(const ObserverWeakRef &o);

  protected:
    ValueState(double value = 0.0);

    /** Changes the value of this ValueState and sends a notification
	to all of the observers of this ValueState.
	\param value The new value of this ValueState */
    void set_value(double value);

  private:
    double m_V;

    /** A map of Observer objects to their weights, stored as a sorted
	vector. */
    std::vector<std::pair<ObserverWeakRef, double> > m_observers;
  };

  class DecisionState;
  typedef boost::shared_ptr<DecisionState> DecisionStateRef;
  typedef boost::weak_ptr<DecisionState> DecisionStateWeakRef;

  /** Class representing states at which the planner must determine
      the optimal action.  Note that this set of states includes the
      states given as arguments to policy() as well as all the
      nonterminal states reachable from those states. */
  class DecisionState: public ModelBasedStatePolicy,
		       public ValueState,
		       public MDPState::Observer {
  public:
    /** \param planner The planner that contains this DecisionState
	\param model The MDP model of the state at which the planner
	             is trying to compute the optimal action
	\param ref A DecisionState ref that should be assigned the new
	           DecisionState before it initalizes.
	\param ptr An optional ValueState ref that should be assigned
	           the new DecisionState before it initializes. */
    static DecisionStateRef create(const PlannerRef &planner,
				   const MDPStateRef &model,
				   DecisionStateWeakRef &ref,
				   ValueStateWeakRef *ptr);

    virtual ~DecisionState();

    /** Compute the initial value of this state.  Note that this
	method is separate from the constructor, since otherwise a
	cycle in the MDP structure might cause the planner to try to
	create a new DecisionState object for a state in which another
	DecisionState object is still being constructed.  This way, we
	can update the hash table mapping states of DecisionState
	objects after construction but before initialization, since
	the initialization is what causes the cycle. */
    void initialize();

    virtual const StateVectorRef &state() const;
    virtual TaskRef policy_action() const;
    virtual MDPStateRef policy_model() const;

    virtual void observe_MDP_change(const TaskRef &a);

    void propagate_MDP_change();
    void propagate_value_change();
    void propagate_policy_change(const TaskRef &original);

    /** Resets the bound on Bellman error to zero.  May only be called
	if this DecisionState is at the head of the priority
	queue. Removes this DecisionState from the priority queue. */
    void zero_bound() {
      assert(m_heapindex == 0); // may only be called at head of queue
      m_errorbound = 0.0;
      dequeue();
    }

    /** Performs a value update for each action.
	\return The change in value for this state. */
    double backup_values();

    virtual void debug(std::ostream &out) const;

  protected:
    class Action;
    typedef boost::shared_ptr<Action> ActionRef;
    typedef boost::weak_ptr<Action> ActionWeakRef;

    /** Maintains the value for a state-action. */
    class Action: public ValueState::Observer {
    public:
      static ActionRef create(const DecisionStateWeakRef &parent,
			      const MDPStateActionRef &model);

      virtual ~Action();

      virtual void observe_value_change(double change);

      /** Computes a distribution of ValueState successors based on
	  the MDPStateAction model and planner. */
      void compute_successors();

      /** Computes the value of this state action as a weighted
	  average of the value of its successors. */
      void compute_value();

      void debug(std::ostream &out) const;

      void update_value() {
	if (m_errorbound > 0)
	  compute_value();
      }

      const MDPStateActionRef &mdp() const {
	return m_model;
      }

      double value() const {
	return m_Q;
      }

    private:
      Action(const DecisionStateWeakRef &parent, const MDPStateActionRef &model);

      const DecisionStateWeakRef m_parent;
      const MDPStateActionRef m_model;
      double m_Q;
      double m_errorbound;

      typedef std::pair<ValueStateRef, double> ValueStateProbability;
      
      /** Keys: states from the successor_probabilities() method of an
	  MDPStateAction.  Data: The probability of the key in
	  successor_probabilities() and the corresponding ValueState.
	  A NULL ValueStateRef serves as a proxy for this Action's
	  parent DecisionState, to avoid a cycle of strong
	  references. */
      std::vector<std::pair<StateVectorRef, ValueStateProbability > >
      m_successors;

      ActionWeakRef m_this;
    };

    friend class Action;

    /** Informs this DecisionState that one of its child Action
	objects updated its error bound.  This may change the error
	bound for this DecisionState, possibly adding this
	DecisionState to the planner's priority queue or increasing
	its priority in the queue.
	\param child An Action object in this DecisionState object's
	             action_values container.
	\param bound The error bound for the child. */
    void child_bound(const ActionRef &child, double bound);

    /** If this DecisionState is in the priority queue of its
	enclosing Planner, then enforce the heap invariant for the
	subtree rooted by this DecisionState, assuming that the
	subtrees rooted at each children is a heap. */
    void heapify();

    /** If this DecisionState is in the priority queue of its
	enclosing Planner, then move it towards the head of the queue
	while its error bound is greater than its parent's. */
    void insert_heapify();

    /** Removes this DecisionState objects from the priority queue of
	its enclosing planner. */
    void dequeue();

  private:
    DecisionState(const PlannerRef &planner, const MDPStateRef &model);

    const PlannerRef m_planner;
    const MDPStateRef m_model;

    /** A mapping from child actions to state-action values, stored as
	a vector of pairs sorted by the Task pointers.  Invariant: The
	keys of action_values are precisely the keys in
	m_model->successor_probabilities().  For each key, the value()
	method on the corresponding data pointer is equal to the
	one-step backup of the corresponding state-action given model
	and the ValueState objects in this planner. */
    std::vector<std::pair<TaskRef, ActionRef> > m_action_values;

    /** Invariant: max is the value in action_values that maximizes
	max.second->value().  Additionally, this->value() equals
	max.second->value(). */
    std::pair<TaskRef, ActionRef> m_max;

    /** The set of Task objects that have been received as arguments
	to observe_MDP_change since the last time
	propagate_MDP_change() executed. */
    std::set<TaskRef> m_inbox;

    /** This nonnegative value is an upper bound on the Bellman error:
	the difference between the current value of this->value() and
	the value that would obtain after executing
	propagate_value_change(). */
    double m_errorbound;

    /** Indicates the position of this DecisionState in its planner's
	priority queue, if error > planner.epsilon.  If negative,
	then this DecisionState is not in the priority queue. */
    int m_heapindex;

    DecisionStateWeakRef m_this;
  };

  friend class DecisionState;

  /** \param s The state at which the policy should be evaluated.
      \param ptr If non-NULL, *ptr will be assigned the return value
                 of any newly constructed DecisionState objects before
                 initialization, to avoid unbounded recursions. */
  DecisionStateRef policy(const MDPStateRef &s, ValueStateWeakRef *ptr);

  /** \param successor A basis state that appears in the value of
                       successor_probabilities() for some
                       MDPStateAction object. */
  ValueStateRef successor_value(const StateVectorRef &successor);

  void debug_pqueue(std::ostream &out) const;

private:
  Planner(const MDPRef &mdp,
	  const StatePredicateRef &terminal,
	  const StateFunctionRef &goal,
	  double gamma,
	  double epsilon);

  const MDPRef m_mdp;
  const StatePredicateRef m_terminal;
  const StateFunctionRef m_goal;
  const double m_gamma;
  const double m_epsilon;

  typedef __gnu_cxx::hash_map<StateVectorRef,
			      DecisionStateWeakRef,
			      StateHasher> NonterminalsContainer;

  /** Each key is the return value of the state() method for an
      MDPState.  Each datum is the DecisionState object constructed
      with that MDPState. */
  NonterminalsContainer m_nonterminals;

  /** Each key is a basis state that appears in the value of
      successors() for some MDPStateAction object.  The data pointers
      are either DecisionState objects in m_nonterminals or ValueState
      objects representing terminal states. */
  __gnu_cxx::hash_map<StateVectorRef, ValueStateWeakRef, StateHasher> m_completions;

  /** The inbox includes all DecisionState objects that have not
      executed propagate_value_change since their observed MDPState
      object changed. */
  std::set<DecisionStateWeakRef> m_inbox;

  /** Keys: DecisionState objects that have changed their policy
      action.  Data: The original policy action before the first
      change.  (The data can be used to avoid sending policy change
      notifications unnecessarily, when a DecisionState object
      switches back to its original policy action.) */
  std::map<DecisionStateWeakRef, TaskRef> m_outbox;

  /** This binary heap contains all the DecisionState objects created
      by this Planner that have (Bellman) error greater than epsilion.
      This error is used to prioritize value backups, which eliminate
      this error. */
  std::vector<DecisionStateRef> m_pqueue;

  PlannerWeakRef m_this;
};

#endif
