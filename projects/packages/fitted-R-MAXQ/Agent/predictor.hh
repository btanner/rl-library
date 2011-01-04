#ifndef _PREDICTOR_HH_
#define _PREDICTOR_HH_

/** \file This code evaluates the policy output by a Planner object.
    It computes the expected reward and terminal state distribution
    for following the policy. */

#include <ext/hash_map>
#include <map>
#include <set>

#include "state.hh"
#include "action.hh"
#include "mdp.hh"
#include "planner.hh"

class Predictor;
typedef boost::shared_ptr<Predictor> PredictorRef;
typedef boost::weak_ptr<Predictor> PredictorWeakRef;

/** This object outputs StateActionModel objects given a Planner
    object (and the policy it implies) as input.  This computation
    completes the recursive composition of child StateActionModel
    objects first into an MDP, then into a policy, and now into the
    model of an abstract action that corresponds to achieving the goal
    function given to the Planner object, using the child actions. */
class Predictor {
public:
  /** \param planner The planner whose policy this Predictor will
                     evaluate
      \param terminalp The termination function used by the planner
      \param gamma The discount factor used by the planner
      \param epsilon The termination criterion that determines the
                     precision of the computed abstract model.  The
                     predictor will not backup a value or transition
                     probability change smaller than this threshold
      \param dimensions The state representation to use */
  static PredictorRef create(const PlannerRef &planner,
			     const StatePredicateRef &terminalp,
			     double gamma,
			     double epsilon,
			     const StateVariables &dimensions);

  ~Predictor();

  /** \param s A state
      \return A prediction of the expected reward and the distribution
              over terminal states that would result from executing
              the policy given by the Planner object given to this
              Predictor.*/
  StateActionModelRef model(const StateVectorRef &s);

  /** Updates the StateActionModel objects created by this Predictor,
      in response to changes in the MDPState and StatePolicy objects
      that serve as inputs to this Predictor. */
  void propagate_changes();

protected:
  enum queue_t { REWARD, TRANSITION };

  class State;
  typedef boost::shared_ptr<State> StateRef;
  typedef boost::weak_ptr<State> StateWeakRef;

  class StatePrediction;
  typedef boost::shared_ptr<StatePrediction> StatePredictionRef;
  typedef boost::weak_ptr<StatePrediction> StatePredictionWeakRef;

  /** Interface for reachable states that facilitates backups of the
      predicted return and predicted terminal-state distribution. */
  class StatePrediction {
  public:
    virtual ~StatePrediction();

    /** \param p A probability
	\return The predicted return from this state, weighted by the
	        given probability */
    virtual double predict_return(double p) const = 0;

    /** \param term An effect distribution that accumulates a
	            predicted distribution over terminal states.  This
	            method will add the predicted terminal states for
	            this StatePrediction, weighted by the given
	            probability p, to the existing predicted terminal
	            states (from other StatePrediction objects).
	\param p A probability */
    virtual void predict_terminals(EffectDistribution &term,
				   double p) const = 0;

    /** Informs this StatePrediction to backup reward and transition
	distribution changes to the given predecessor, weighted by the
	given probability.  Note that this method is used to update
	the probability for an existing predecessor.
	\param pred A State that can transition to this StatePrediction
	\param probability The probability with which pred transitions
	                   to this StatePrediction */
    virtual void add_predecessor(const StateWeakRef &pred,
				 double probability) = 0;

    /** Informs this StatePrediction to cease backing up reward and
	transition distribution changes to a certain predecessor
	State.
	\param pred A State that no longer transitions to this
	            StatePrediction */
    virtual void remove_predecessor(const StateWeakRef &pred) = 0;
  };

  /** An implementation of StatePrediction for terminal states. */
  class TerminalStatePrediction: public StatePrediction {
  public:
    /** \param succ A terminal state
	\param dimensions The state representation to use
        \param ref A StatePredictionWeakRef that should be assigned
	           the new TerminalStatePrediction */
    static StatePredictionRef create(const StateVectorRef &succ,
				     const StateVariables &dimensions,
				     StatePredictionWeakRef &ref);
    
    virtual ~TerminalStatePrediction();

    virtual double predict_return(double p) const;
    virtual void predict_terminals(EffectDistribution &term, double p) const;

    virtual void add_predecessor(const StateWeakRef &pred,
				 double probability);
    virtual void remove_predecessor(const StateWeakRef &pred);

  private:
    TerminalStatePrediction(const StateVectorRef &succ,
			    const StateVariables &dimensions);

    AbsoluteEffectRef m_terminal;
  };

  /** The primary data structure used by Planner, this class stores
      the predicted reward and terminal-state distribution for a
      nonterminal state.  As a StatePrediction, it exports this
      information to other StatePrediction objects, and it exports
      this data outside the enclosing Planner via the StateActionModel
      interface. */
  class State: public StateActionModel,
	       public StatePrediction,
	       public MDPState::Observer,
	       public StatePolicy::Observer {
  public:
    /** The caller should invoke the initialize method as soon as a
	pointer to the new State has been added to the predictions
	hash table, to avoid unbounded recursion.
	\param predictor The Predictor object that is creating and
	                 will contain this State.
	\param policy The ModelBasedStatePolicy object for which this
	              State object will perform the corresponding
	              prediction.
        \param ref A StateWeakRef that should be assigned the new
	           State before it initializes.
        \param ptr An optional StatePredictionWeakRef that should be
	           assigned the new State before it initializes. */
    static StateRef create(const PredictorRef &predictor,
			   const ModelBasedStatePolicyRef &policy,
			   StateWeakRef &ref,
			   StatePredictionWeakRef *ptr);

    virtual ~State();

    virtual const StateVectorRef &state() const;
    virtual double reward() const;
    virtual const EffectDistribution &effects() const;

    virtual double predict_return(double p) const;
    virtual void predict_terminals(EffectDistribution &term, double p) const;
    virtual void add_predecessor(const StateWeakRef &pred,
				 double probability);
    virtual void remove_predecessor(const StateWeakRef &pred);

    virtual void observe_MDP_change(const TaskRef &a);

    virtual void observe_policy_change();

    /** Complete the initialization of this object's data members.
	This initialization can't occur in the constructor, since it
	could lead to unbounded recursion.  This way, the Predictor
	containing this State can assign this object's pointer to a
	hash table, preventing it from trying to construct a second
	State for the same state_t pointer. */
    void initialize();

    /** Updates this State given a change in the MDPStateAction that
	determines the behavior of the policy action at this state. */
    void propagate_mdp_change();

    /** Updates the predicted reward of this State given a change in
	the predicted reward of some successor, and propagates this
	change to the predecessors of this State. */
    void propagate_reward_change();

    /** Updates the predicted transitions of this State given a change
	in the predicted transitions of some successor, and propagates
	this change to the predecessors of this State. */
    void propagate_transitions_change();

    /** Notifies the observers of this StateModel that the predicted
	behavior of this composite action has changed. */
    void propagate_model_change();

    /** Resets the appropriate bound on Bellman error to zero.  May
	only be called if this State is at the head of the
	corresponding priority queue. Removes this State from that
	priority queue.
	\param idx Which error to zero out. */
    void zero_bound(queue_t idx) {
      m_error_bounds[idx] = 0.0;
      dequeue(idx);
    }

  protected:
    /** Increases the appropriate error bound, possibly adding this
	State to the appropriate priority queue or changing its
	position in the queue.
	\param idx The error bound to increase.
	\param increase The (positive) amount of the increase */
    void increase_bound(queue_t idx, double increase);

    /** If this State is in the appropriate priority queue of its
	enclosing Predictor, then enforce the heap invariant for the
	subtree rooted by this State, assuming that the subtrees
	rooted at each children is a heap.
	\param idx Which priority queue to heapify */
    void heapify(queue_t idx);

    /** If this State is in the appropriate priority queue of its
	enclosing Predictor, then move it towards the head of the
	queue while its error bound is greater than its parent's.
	\param idx Which priority queue to heapify */
    void insert_heapify(queue_t idx);

    /** Removes this State object from the appropriate priority queue
	of its enclosing Predictor.
	\param idx Which priority queue to heapify */
    void dequeue(queue_t idx);

  private:
    State(const PredictorRef &predictor,
	  const ModelBasedStatePolicyRef &policy);
    
    const PredictorRef m_predictor;
    const MDPStateRef m_mdp_state;
    const ModelBasedStatePolicyRef m_policy;

    /** An iterator into the data structure returned by the
	state_actions() method on mdp_state, indicating the policy
	state-action. */ // XXX only safe if stable subtask set
    std::vector<std::pair<TaskRef, MDPStateActionRef> >::const_iterator
    m_action;

    typedef std::pair<StatePredictionRef, double> StatePredictionProbability;
    typedef std::vector<std::pair<StateVectorRef, StatePredictionProbability> >
    SuccessorsContainer;

    /** A mapping, stored as a sorted vector, from state_t pointers
	(obtained from the successor_probabilities() method of an
	MDPStateAction) to pairs containing first the corresponding
	StatePrediction object in this Predictor and second the
	successor probability.  A NULL StatePredictionRef serves as a
	proxy for a self-reference. */
    SuccessorsContainer m_successors;

    /** A sorted container in which each element is a State with a
	transition to this State object, along with the associated
	transition probability. */
    std::vector<std::pair<StateWeakRef, double> > m_predecessors;

    double m_r;
    EffectDistribution m_effect_map;

    double m_error_bounds[2];
    int m_heap_indices[2];

    StateWeakRef m_this;
  };
  
  friend class State;
  
  /** \param pi_s A state from the Planner associated with this
                  Predictor
      \param ptr If non-NULL and if a new State object must be
                 constructed, this StatePrediction pointer will be
                 assigned the return value of this method before the
                 initialize() method is called on the new State
                 object */
  StateRef prediction(const ModelBasedStatePolicyRef &pi_s,
		      StatePredictionWeakRef *ptr);

  /** \param s A state appearing in the return value of
               successor_probabilities() for some MDPStateAction. */
  StatePredictionRef prediction(const StateVectorRef &s);

private:
  Predictor(const PlannerRef &planner,
	    const StatePredicateRef &terminalp,
	    double gamma,
	    double epsilon,
	    const StateVariables &dimensions);
  
  const PlannerRef m_planner;
  const StatePredicateRef m_terminalp;
  const double m_gamma;
  const double m_epsilon;
  const StateVariables m_dimensions;

  typedef __gnu_cxx::hash_map<StateVectorRef,
			      StateWeakRef,
			      StateHasher> PredictionsContainer;

  /** Keys: return values from ModelBasedStatePolicy::state()
      Data: State objects */
  PredictionsContainer m_predictions;

  /** Keys: pointers from MDPStateAction::successor_probabilities()
      Data: StatePrediction objects. */
  __gnu_cxx::hash_map<StateVectorRef, StatePredictionWeakRef, StateHasher>
  m_succ_predictions;

  std::set<StateWeakRef> m_inbox;
  std::vector<StateRef> m_pqueues[2];

  PredictorWeakRef m_this;
};

#endif
