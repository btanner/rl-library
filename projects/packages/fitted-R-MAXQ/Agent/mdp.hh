#ifndef _MDP_HH_
#define _MDP_HH_

/** \file
    Definition of objects that translate StateActionModel objects into
    the MDP formalism. */

#include <vector>
#include "action.hh"
#include "averager.hh"

/** Interface for objects that give the dynamics of a certain
    state-action pair in the standard MDP representation.  Note that
    this differs from StateActionModel in that it gives the effects of
    an action as a distribution over successor states, not as a
    distribution over Effect objects. */
class MDPStateAction
{
public:
  virtual ~MDPStateAction();

  /** \return The estimated reward for executing the modeled action at
              the modeled state. */
  virtual double reward() const = 0;

  /** The probabilities of each successor for the modeled state and
      action.  The caller may subscribe to this StateActionModel to
      be notified about changes to this structure.
      \return A mapping in which the keys are pointers to states and
              the data are the corresponding transition
              probabilities. */
  virtual const StateDistribution &successor_probabilities() const = 0;
};

typedef boost::shared_ptr<MDPStateAction> MDPStateActionRef;
typedef boost::weak_ptr<MDPStateAction> MDPStateActionWeakRef;

/** Interface for objects that maintain MDPStateAction objects for
    each action available at a certain state. */
class MDPState
{
public:
  /** Interface for objects that wish to be notified when a MDPState
      changes. */
  class Observer {
  public:
    virtual ~Observer();

    /** Informs the Observer that the MDPState has changed.
	\param a The action whose data changed in mdp_state */
    virtual void observe_MDP_change(const TaskRef &a) = 0;
  };

  typedef boost::shared_ptr<Observer> ObserverRef;
  typedef boost::weak_ptr<Observer> ObserverWeakRef;

  virtual ~MDPState();

  /** \return The state that this MDPState models. */
  virtual const StateVectorRef &state() const = 0;

  virtual const std::vector<std::pair<TaskRef, MDPStateActionRef> > &
  state_actions() const = 0;

  /** Informs a MDPState that a MDPState::Observer would like to be
      notified of any changes in any of the MDPStateAction objects'
      return values for norm(), reward, or effects().
      \param o The observer to subscribe. */
  virtual void add_observer(const ObserverWeakRef &o);

  /** Informs a MDPState that a MDPState::Observer would like to stop
      receiving notifications.
      \param o The MDPStateAction::Observer that would like to stop
               receiving notifications messages. */
  virtual void remove_observer(const ObserverWeakRef &o);

protected:
  /** Sends a notification message to all subscribed
      MDPState::Observers.
      \param a The key in the return value of state_actions() whose
               corresponding data has changed. */
  void notify_MDP_observers(const TaskRef &a);

private:
  std::vector<ObserverWeakRef> m_observers;
};

typedef boost::shared_ptr<MDPState> MDPStateRef;
typedef boost::weak_ptr<MDPState> MDPStateWeakRef;

class MDP;
typedef boost::shared_ptr<MDP> MDPRef;
typedef boost::weak_ptr<MDP> MDPWeakRef;

/** This class combines the representational decisions of an Averager
    with a set of action models to obtain a concrete Markov decision
    process.

    All StateVectorRef objects returned by any component of this class have
    the property that *p1 == *p2 implies p1 == p2. */
class MDP
{
public:
  /** \param actions_begin An InputIterator with value_type TaskRef
      \param actions_end And InputIterator with value_type TaskRef
      \param averager The averager that determines the fitted state
                      space.  This averager must have the property
                      that all the weights sum to 1.0 */
  template <class InputIterator>
  static MDPRef create(InputIterator actions_begin,
		       InputIterator actions_end,
		       const AveragerRef &averager);

  ~MDP();

  /** \param s A state
      \return Pointer to an MDPState object that contains
              MDPStateAction objects for each Task in this MDP, for
              the given state. */
  MDPStateRef state_data(const StateVectorRef &s);

  /** Update all the MDPStateAction objects managed by this MDP, given
      any StateActionModel and Average changes from lower-level
      tasks. */
  void propagate_changes();

  /** All the subtasks. */
  const std::vector<TaskRef> &subtasks() const {
    return m_actions;
  }

protected:
  class State;
  typedef boost::shared_ptr<State> StateRef;
  typedef boost::weak_ptr<State> StateWeakRef;

  /** Implementation of the MDPState interface.  Objects of this class
      are the primary data managed by the MDP class. */
  class State: public MDPState, public Average::Observer {
  public:
    class Action;
    typedef boost::shared_ptr<Action> ActionRef;
    typedef boost::weak_ptr<Action> ActionWeakRef;

    /** Implementation fo the MDPStateAction interface.  State objects
	manage an Action object for each child Task in the MDP. */
    class Action: public MDPStateAction,
		  public StateActionModel::Observer,
		  public Average::Observer
    {
    public:
      /** \param parent The State object that will contain this Action.
	  \param action The Task simulated by this Action
	  \param model Model generated by action's model method on the
	               state represented by parent */
      static ActionRef create(const StateWeakRef &parent,
			      const TaskRef &action,
			      const StateActionModelRef &model);

      virtual ~Action();

      virtual double reward() const;
      virtual const StateDistribution &successor_probabilities() const;

      virtual void observe_model_change();
      virtual void observe_average_change();

      void compute_successors();

    private:
      Action(const StateWeakRef &parent,
	     const TaskRef &action,
	     const StateActionModelRef &model);

      const StateWeakRef m_parent;
      const TaskRef m_action; // The key under which this object is found in parent.
      const StateActionModelRef m_model;

      /** Invariant: The data of this mapping comprise all the Average
	  objects that this MDPStateAction is observing. */
      std::vector<std::pair<EffectRef, AverageRef> > m_averages;

      StateDistribution m_succs;

      ActionWeakRef m_this;
    };

    friend class Action;

    static StateRef create(const MDPRef &mdp,
			   const AverageRef &average,
			   StateWeakRef &ref);

    virtual ~State();

    virtual const StateVectorRef &state() const;
    virtual const std::vector<std::pair<TaskRef, MDPStateActionRef> > &
    state_actions() const;

    virtual void observe_average_change();

  private:
    State(const MDPRef &mdp, const AverageRef &average);

    const MDPRef m_mdp;

    /** Observing this average guarantees that its state pointer will
	remain valid. */
    const AverageRef m_average;

    std::vector<std::pair<TaskRef, MDPStateActionRef> > m_state_action_map;
    
    StateWeakRef m_this;
  };

  friend class State;

private:
  template <class InputIterator>
  MDP(InputIterator actions_begin, InputIterator actions_end,
      const AveragerRef &averager);

  typedef __gnu_cxx::hash_map<StateVectorRef, StateWeakRef, StateHasher>
  StateContainer;

  const std::vector<TaskRef> m_actions;
  const AveragerRef m_averager;

  /** Keys: States from the state() method of an average.  Data: State
      objects constructed with the corresponding Average objects. */
  StateContainer m_state_map;

  std::set<State::ActionWeakRef> m_inbox;

  MDPWeakRef m_this;
};

template <class InputIterator>
MDPRef MDP::create(InputIterator actions_begin, InputIterator actions_end,
		   const AveragerRef &averager)
{
  MDPRef result(new MDP(actions_begin, actions_end, averager));
  result->m_this = result;
  return result;
}

template <class InputIterator>
MDP::MDP(InputIterator actions_begin, InputIterator actions_end,
	 const AveragerRef &averager):
  m_actions(actions_begin, actions_end), m_averager(averager)
{}

#endif
