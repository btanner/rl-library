#ifndef _PRIMITIVE_HH_
#define _PRIMITIVE_HH_

/** \file
    Definitions of tasks that estimate their models directly from
    primitive-action data. */

#include "action.hh"
#include "averager.hh"

#include <map>

class VectorEffect;
typedef boost::shared_ptr<VectorEffect> VectorEffectRef;

/** An Effect that performs vector addition to states to which it is
    applied. */
class VectorEffect: public Effect
{
public:
  /** \param s A state
      \param succ The state that should be obtained by applying the
                  new Effect to s.  succ must have the same size as
                  s.
      \param dimensions The new Effect will only consider the state
                        indices that appear as keys in this map. */
  static VectorEffectRef create(const StateVectorRef &s,
				const StateVectorRef &succ,
				const StateVariables &dimensions);

  virtual ~VectorEffect();

  virtual StateVectorRef apply(const StateVectorRef &s) const;

  virtual void debug(std::ostream &out) const;

  bool operator<(const VectorEffect &other) const {
    return m_changes < other.m_changes;
  }

private:
  VectorEffect(const StateVectorRef &s,
	       const StateVectorRef &succ,
	       const StateVariables &dimensions);

  /** A sparse representation of the state vector to add, assuming
      that many of the elements of the state vector may be 0.  Each
      pair in the array associates a state vector index with the value
      to add to that state variable. */
  std::vector<std::pair<unsigned,double> > m_changes;
};

class PrimitiveTask;
typedef boost::shared_ptr<PrimitiveTask> PrimitiveTaskRef;

/** A Task that represents a primitive action provided by the
    environment.  This object estimates the model of a primitive
    action from data.  This model employs R-max-style optimism to
    ensure exploration when used in the Planner.  In particular, when
    insufficient data exists for a state whose model is queried, a
    PrimitiveTask predicts a terminal transition (no successors) and
    an optimistic immediate reward. */
class PrimitiveTask: public Task
{
public:
  /** Creates a new PrimitiveTask object, initialized with no data.
      \param name A unique name for this task.  If left empty, the name
                  is generated from the primitive action number.
      \param primitive The action primitive, assumed to be an int
      \param threshold The amount of (generalized, weighted) data
                       required before turning off optimism at a given
                       query state
      \param maxval An upper bound on the value of a state, used as
                    the one-step reward of an insufficiently explored action
      \param modelapproximator The Averager used to generalize from
                               data to a given query state.  This
                               pointer must remain valid of the
                               lifetime of this object.
      \param precondition The predicate that determines when this
                          primitive is available */
  static TaskRef create(const std::string &name,
			int primitive,
			double threshold,
			double maxval,
			const DynamicAveragerRef &modelapproximator,
			const StatePredicateRef &precondition);

  virtual ~PrimitiveTask();

  virtual const std::string &name() const;
  virtual bool available(const StateVectorRef &s) const;
  virtual bool terminal(const StateVectorRef &s) const;
  virtual StatePolicyRef policy(const StateVectorRef &s);
  virtual StateActionModelRef model(const StateVectorRef &s);
  virtual void propagate_changes();
  virtual void debug(std::ostream &out);

  /** Incorporates a nonterminal experience to update the model
      \param s The state at which the primitive action was executed
      \param r The immediate reward obtained
      \param succ The successor state observed */
  virtual void update(const StateVectorRef &s,
		      double r,
		      const StateVectorRef &succ);

  /** Incorporates a terminal experience to update the model
      \param s The state at which the primitive action was executed
      \param r The immediate reward obtained */
  virtual void update(const StateVectorRef &s, double r);

  /** The primitive action that this Task models. */
  int action() const {
    return m_primitive;
  }

protected:
  /** A tally of observed effects.  The contents of this data
      structure must be sorted (using the default comparator), and no
      two elements can have the same first part (Effect pointer). */
  typedef std::vector<std::pair<EffectRef, unsigned> > EffectCounts;
  
  // Used in the update methods, which always produce vector effects.
  struct VectorEffectCountsComparator {
    bool operator()(const EffectCounts::value_type &x,
		    const EffectCounts::value_type &y) const {
      assert(dynamic_cast<const VectorEffect *>(x.first.get()));
      assert(dynamic_cast<const VectorEffect *>(y.first.get()));
      const VectorEffect *vx = static_cast<const VectorEffect *>(x.first.get());
      const VectorEffect *vy = static_cast<const VectorEffect *>(y.first.get());
      return *vx < *vy;
    }
  };

  class PrimitiveStateActionModel;
  typedef boost::shared_ptr<PrimitiveStateActionModel>
  PrimitiveStateActionModelRef;
  typedef boost::weak_ptr<PrimitiveStateActionModel>
  PrimitiveStateActionModelWeakRef;

  /** A simple struct for storing the observed outcomes for a
      particular state-action pair. */
  struct PrimitiveStateActionData {
    PrimitiveStateActionData();

    unsigned count;
    double cumulative_reward;
    EffectCounts effect_counts;
    std::vector<PrimitiveStateActionModelWeakRef> observers;
  };

  /** A model of a primitive action at a given instance state,
      generalized from data. */
  class PrimitiveStateActionModel: public StateActionModel,
				   public Average::Observer
  {
  public:
    /** \param parent The PrimitiveTask that will contain the new
 	              PrimitiveStateActionModel.
	\param average The approximation of the state that this
	               PrimitiveStateActionModel will model.
	\param ref A pointer that should be assigned the new object */
    static PrimitiveStateActionModelRef
    create(const PrimitiveTaskRef &parent, const AverageRef &average,
	   PrimitiveStateActionModelWeakRef &ref);

    virtual ~PrimitiveStateActionModel();

    virtual const StateVectorRef &state() const;
    virtual double reward() const;
    virtual const EffectDistribution &effects() const;

    virtual void observe_average_change();

    /** Computes the values to return for reward() and effects(),
	given this object's parent and average.  This method may
	generate notifications to any objects subscribed to this
	PrimitiveStateActionModel. */
    void compute_model();

    void debug(std::ostream &out) const;

  private:
    PrimitiveStateActionModel(const PrimitiveTaskRef &parent,
			      const AverageRef &average);

    const PrimitiveTaskRef m_parent;
    const AverageRef m_average;
    double m_sum;
    double m_r;

    typedef std::vector<std::pair<StateVectorRef, PrimitiveStateActionData *> >
    StateTranslation;

    /** Translates state references to the records of what happened
	when this action was executed at that state.  Invariant: the
	data of this map (represented as a vector) are precisely the
	PrimitiveStateActionData objects that include this
	PrimitiveStateActionModel as an observer.  The keys correspond
	to the states in average.bases() that parent.data maps to the
	PrimitiveStateActionData objects.  This container does not own
	the data pointers.*/
    StateTranslation m_translation;

    /** The value to return for effects(). */
    EffectDistribution m_effects_map;

    /** Terminal effect returned when insufficient data exists. */
    static EffectDistribution s_empty;

    PrimitiveStateActionModelWeakRef m_this;
  };

  static std::string compute_name(int primitive);

  PrimitiveTask(const std::string &name,
		int primitive,
		double threshold,
		double maxval,
		const DynamicAveragerRef &modelapproximator,
		const StatePredicateRef &precondition);

  void set_weak_this_reference(const PrimitiveTaskRef &ref) {
    m_this = ref;
  }

  // Hook for subclass to replace dynamic averager with non-dynamic.
  virtual AveragerRef averager() const;

  /** Fetches or creates a data structure for recording the observed
      data for a given state (with this primitive action).
      \param state A state whose data to fetch.
      \return A PrimitiveStateActionData that contains all known data
              at the given state for this primitive action.  This
              object will exist for the lifetime of this
              PrimitiveTask. */
  PrimitiveStateActionData *get_data(const StateVectorRef &state) {
    PrimitiveStateActionData *&dat = m_data[state];
    if (dat == NULL)
      dat = new PrimitiveStateActionData;
    return dat;
  }

  /** Completes an update of the data record (for some state),
      performing all the changes that do not depend on whether or not
      the new data comes from a terminal transition.  This procedure
      updates the cumulative reward, the weight, and adds the
      appropriate PrimitiveStateActionModel objects to the inbox. */
  void update(PrimitiveStateActionData *dat, double r);

private:
  const std::string m_name;

  const int m_primitive;
  const double m_threshold;
  const double m_maxval;

  const DynamicAveragerRef m_averager;
  const StatePredicateRef m_precondition;

  typedef __gnu_cxx::hash_map<StateVectorRef,
			      PrimitiveStateActionData *,
			      StateHasher> DataContainer;

  /** Container for the StateActionData objects that comprise all the
      data for this primitive action.  The keys are basis states in
      the averager.  The data are pointers to the StateActionData
      objects that store the corresponding transition and reward data.
      This container owns the data pointers. */
  DataContainer m_data;

  typedef __gnu_cxx::hash_map<StateVectorRef,
			      PrimitiveStateActionModelWeakRef,
			      StateHasher> ModelContainer;

  /** Container for the PrimitiveStateActionModel objects that this
      PrimitiveTask returns via model().  The keys are query states
      stored in the averager.  The data are PrimitiveStateActionModel
      objects that are return via model(). */
  ModelContainer m_models;

  /** Container for PrimitiveStateActionModel objects that have
      received an update message from a StateActionData and are
      therefore "dirty."  These objects are cleaned in
      propagate_changes(). */
  std::set<PrimitiveStateActionModelWeakRef> m_inbox;

  boost::weak_ptr<PrimitiveTask> m_this;
};

class DiscretizedPrimitiveTask;
typedef boost::shared_ptr<DiscretizedPrimitiveTask>
DiscretizedPrimitiveTaskRef;

/** This specialization of PrimitiveTask optimizes the combination of
    PrimitiveTask with a dynamic (model) discretization averager that
    approximates a given point with a uniform distribution over the
    basis states added to the points grid cell.  In this case, there
    is no need to compute this average explicitly.  Instead, just bin
    the data as in most implementations of discretization. */
class DiscretizedPrimitiveTask: public PrimitiveTask
{
public:
  /** Creates a new DiscretizedPrimitiveTask object, initialized with no data.
      \param name A unique name for this task.  If left empty, the name
                  is generated from the primitive action number.
      \param primitive The action primitive, assumed to be an int
      \param threshold The amount of data
                       required before turning off optimism at a given
                       query state
      \param maxval An upper bound on the value of a state, used as
                    the one-step reward of an insufficiently explored action
      \param resolutionfactor Controls the spacing of the uniform grid
                              used as the basis set
      \param scale The weights used to obtain the scaled Euclidean
	           space.  Unused dimensions are set to 0 in the basis
	           vectors.
      \param precondition The predicate that determines when this
                          primitive is available */
  static TaskRef create(const std::string &name,
			int primitive,
			unsigned threshold,
			double maxval,
			int resolutionfactor,
			const StateVariables &scale,
			const StatePredicateRef &precondition);

  virtual ~DiscretizedPrimitiveTask();
  
  // Override superclass implementation of these methods for efficiency.
  virtual void update(const StateVectorRef &s,
		      double r,
		      const StateVectorRef &succ);
  virtual void update(const StateVectorRef &s, double r);

protected:
  // Used in the update methods, which always produce absolute effects.
  struct AbsoluteEffectCountsComparator {
    bool operator()(const EffectCounts::value_type &x,
		    const EffectCounts::value_type &y) const {
      assert(dynamic_cast<const AbsoluteEffect *>(x.first.get()));
      assert(dynamic_cast<const AbsoluteEffect *>(y.first.get()));
      const AbsoluteEffect *vx = static_cast<const AbsoluteEffect *>(x.first.get());
      const AbsoluteEffect *vy = static_cast<const AbsoluteEffect *>(y.first.get());
      return *vx < *vy;
    }
  };

  virtual AveragerRef averager() const;

private:
  DiscretizedPrimitiveTask(const std::string &name,
			   int primitive,
			   unsigned threshold,
			   double maxval,
			   int resolutionfactor,
			   const StateVariables &scale,
			   const StatePredicateRef &precondition);

  AveragerRef m_discrete_averager;
};

#endif
