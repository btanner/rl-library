#ifndef _ACTION_HH_
#define _ACTION_HH_

/** \file
    Definitions of types concerning actions: how an agent manipulates
    state and earns reward. */

#include "state.hh"
#include <boost/weak_ptr.hpp>
#include <string>

class Task; // Forward declaration for StatePolicy.
typedef boost::shared_ptr<Task> TaskRef;

/** Interface for objects representing a deterministic effect of an
    action.  Conceptually, these objects implement functions that
    deterministically transform a given state to a successor state.
    Actions can then be represented as a distribution over effects. */
class Effect
{
public:
  virtual ~Effect();

  /** Apply this effect to a given state.
      \param s The state to which the effect should be applied
      \return The resulting state */
  virtual StateVectorRef apply(const StateVectorRef &s) const = 0;

  virtual void debug(std::ostream &out) const = 0;
};

typedef boost::shared_ptr<const Effect> EffectRef;

/** Calls debug on the Effect object. */
std::ostream &operator<<(std::ostream &out, const EffectRef &e);

/** The probability of a certain effect, represented as a pointer.
    The pointer must be non-NULL, and the probability must be
    nonnegative.  Typically used as the value type in
    EffectDistribution. */
typedef std::pair<EffectRef, double> EffectProbability;

/** A probability distribution over effects.  The probabilities sum to
    1.  The contents of this data structure must be sorted (using the
    default comparator), and no two elements can have the same first
    part (Effect pointer). */
typedef std::vector<EffectProbability> EffectDistribution;

class AbsoluteEffect;
typedef boost::shared_ptr<AbsoluteEffect> AbsoluteEffectRef;

/** An Effect that simply sets the values of the relevant state
    variables. */
class AbsoluteEffect: public Effect {
public:
  /** \param succ The state to which this AbsoluteEffect will
                  transition, subject to the state variables specified
                  in dimensions
      \param dimensions The state variables that this AbsoluteEffect
                        will modify. */
  static AbsoluteEffectRef create(const StateVectorRef &succ,
				  const StateVariables &dimensions);

  virtual ~AbsoluteEffect();

  virtual StateVectorRef apply(const StateVectorRef &s) const;

  virtual void debug(std::ostream &out) const;

  bool operator<(const AbsoluteEffect &other) const {
    return m_changes < other.m_changes;
  }

private:
  AbsoluteEffect(const StateVectorRef &succ,
		 const StateVariables &dimensions);

  /** A sparse representation of the state vector to set, assuming
      that many of the elements of the state vector may be 0.  Each
      pair in the array associates a state vector index with the value
      to give to that state variable. */
  std::vector<std::pair<unsigned,double> > m_changes;
};

class StateActionModel;
typedef boost::shared_ptr<StateActionModel> StateActionModelRef;
typedef boost::weak_ptr<StateActionModel> StateActionModelWeakRef;

/** Interface for objects that predict the dynamics of a given action
    at a given state.  Formally speaking, such an object represents
    for a given state s and (possibly abstract) action a the value
    R(s,a) of the reward function and the distribution over effects
    P(s,a,-). */
class StateActionModel
{
public:
  /** Interface for objects that wish to be notified when a
      StateActionModel changes its estimation of its state-action's
      dynamics. */
  class Observer {
  public:
    virtual ~Observer();

    /** Informs the Observer that the StateActionModel has changed. */
    virtual void observe_model_change() = 0;
  };
  
  typedef boost::shared_ptr<Observer> ObserverRef;
  typedef boost::weak_ptr<Observer> ObserverWeakRef;

  virtual ~StateActionModel();

  /** \return The state that this StateActionModel models. */
  virtual const StateVectorRef &state() const = 0;

  /** \return The estimated expected reward for executing the modeled
              action at the modeled state. */
  virtual double reward() const = 0;

  /** \return The predicted distribution of effects for the modeled
              state and action.  Note that the pointers in this data
              structure are only guaranteed to remain valid while they
              exist in the data structure.  The caller may subscribe
              to this StateActionModel to be notified about changes to
              this structure. */
  virtual const EffectDistribution &effects() const = 0;

  /** Informs a StateActionModel that a StateActionModel::Observer
      would like to be notified of any changes in the return values of
      reward() or effects().
      \param o The observer to subscribe. */
  virtual void add_observer(const ObserverWeakRef &o);

  /** Informs a StateActionModel that a StateActionModel::Observer
      would like to stop receiving notifications.
      \param o The StateActionModel::Observer that would like to stop
               receiving notifications messages. */
  virtual void remove_observer(const ObserverWeakRef &o);

  virtual void debug(std::ostream &out) const;

protected:
  /** Sends a notification message to all subscribed
      StateActionModel::Observers. */
  virtual void notify_model_observers();

private:
  std::vector<ObserverWeakRef> m_observers;
};

class StatePolicy;
typedef boost::shared_ptr<StatePolicy> StatePolicyRef;
typedef boost::weak_ptr<StatePolicy> StatePolicyWeakRef;

/** Interface for objects that specify an action (subtask) to execute
    at a given state.  A family of such objects for a given set of
    states represents a policy pi(s) over that set. */
class StatePolicy {
public:
  /** Interface for objects that wish to be notified when a
      StatePolicy changes the action it selects. */
  class Observer {
  public:
    virtual ~Observer();

    /** Informs the Observer that its StatePolicy has changed. */
    virtual void observe_policy_change() = 0;
  };

  typedef boost::shared_ptr<Observer> ObserverRef;
  typedef boost::weak_ptr<Observer> ObserverWeakRef;

  virtual ~StatePolicy();

  /** \return The state for which this StatePolicy defines an
              action. */
  virtual const StateVectorRef &state() const = 0;

  /** \return The subtask that the policy specifies for this state. */
  virtual TaskRef policy_action() const = 0;

  /** Informs a StatePolicy that a StatePolicy::Observer would like to
      be notified of changes in the return value of policy_action().
      \param o The observer to add. */
  virtual void add_observer(const ObserverWeakRef &o);

  /** Informs a StatePolicy that a StatePolicy::Observer would like to
      stop receiving notifications.
      \param o The StatePolicy::Observer that would like to stop
               receiving notifications messages. */
  virtual void remove_observer(const ObserverWeakRef &o);

  /** Prints out some debugging information.  The base implementation
      of this method prints nothing.
      \param out The output stream on which to place the debugging
                 information. */
  virtual void debug(std::ostream &out) const;

protected:
  /** Sends a notification message to all subscribed
      StatePolicy::Observers. */
  virtual void notify_policy_observers();

private:
  std::vector<ObserverWeakRef> m_observers;
};

/** Interface for an arbitrary task, which may either be a primitive
    action or an option-like temporal abstraction.  (I use the name
    "Task" to avoid conflicts with the RL Glue type "Action".) */
class Task {
public:
  virtual ~Task();

  virtual const std::string &name() const = 0;

  /** Whether this task may be executed in a given state
      \param s A state
      \return True iff this task may be executed at s */
  virtual bool available(const StateVectorRef &s) const = 0;

  /** Whether this task terminates in a given state
      \param s A state
      \return True iff this task would terminate at state s */
  virtual bool terminal(const StateVectorRef &s) const = 0;

  /** Gives the subtask to perform at the given state
      \param s A state
      return NULL if this task is primitive, otherwise a StatePolicy
             that gives the subtask to execute at the given state.
             This policy, along with the subtask's models, define the
             model reported by this Task's model() method. */
  virtual StatePolicyRef policy(const StateVectorRef &s) = 0;

  /** Gives the dynamics of this task at the given state
      \param s A state
      \return A StateActionModel predicting the effects of this Task
              at the given state. */
  virtual StateActionModelRef model(const StateVectorRef &s) = 0;

  /** Propagates changes in either the data (for PrimitiveTasks) or
      from lower level tasks (for CompositeTasks) to the (possibly
      abstract) model of this Task. */
  virtual void propagate_changes() = 0;

  virtual void debug(std::ostream &out) = 0;
};

#endif
