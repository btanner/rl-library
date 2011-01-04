#ifndef _AVERAGER_HH_
#define _AVERAGER_HH_

/** \file
    Definition of the interfaces and implementation of Average and
    Averager. */

#include "covertree.hh"
#include "state.hh"
#include <boost/weak_ptr.hpp>
#include <ext/hash_map>
#include <ext/hash_set>
#include <map>
#include <set>

class Average;
typedef boost::shared_ptr<Average> AverageRef;

/** Interface for objects that approximate a given state as a weighted
    average of other states.  Such averages are used to approximate
    the model of a given state using data averaged from visited
    states, as well as to approximate the value of a given state using
    the values of a fixed finite set of states. */
class Average
{
public:
  /** Interface for objects that wish to be notified when an Average
      changes how it approximates its query state. */
  class Observer
  {
  public:
    virtual ~Observer();

    /** Delivers a message to this Observer that its observed Average
	has changed.  In particular, the Average now returns a
	different value for norm or bases. */
    virtual void observe_average_change() = 0;
  };

  typedef boost::shared_ptr<Observer> ObserverRef;
  typedef boost::weak_ptr<Observer> ObserverWeakRef;

  virtual ~Average();

  /** \return The state that this average approximates. */
  virtual const StateVectorRef &state() const = 0;

  /** \return A mapping of states to UNNORMALIZED weights.  Each state
              reference in the mapping is non-NULL. */
  virtual const StateDistribution &basis_weights() const = 0;

  /** Informs an Average that an Average::Observer would like to be
      notified of any changes in the return value of
      basis_probabilities().
      \param o The observer to add. */
  virtual void add_observer(const ObserverWeakRef &o);

  /** Informs an Average that an Average::Observer would like to stop
      receiving notifications.
      \param o The Average::Observer that would like to stop receiving
               notifications messages. */
  virtual void remove_observer(const ObserverWeakRef &o);

protected:
  /** Sends a notification message to all Average::Observers. */
  virtual void notify_average_observers();

private:
  /** Container for all the Average::Observers subscribed to this
      Average. */
  std::vector<ObserverWeakRef> m_observers;
};

/** Interface for objects that construct Average objects.  Averager
    objects have the property that all the StateVectorRef objects returned
    by any of their components are canonical: if *p1 == *p2, then p1
    == p2. */
class Averager
{
public:
  virtual ~Averager();

  /** Specifies the importance of each state dimension to this Averager.
      \return A mapping whose keys comprise all the state dimensions
              that this Averager uses in its approximation.  The data
              give the scaling coefficient for each of these
              dimensions. */
  virtual const StateVariables &dimensions() const = 0;

  /** Approximates a given state using an Average.
      \param s The state to approximate.
      \return An Average approximating s. */
  virtual AverageRef approximate(const StateVectorRef &s) = 0;
};

typedef boost::shared_ptr<Averager> AveragerRef;

/** Interface for Averagers that permit the dynamic (user-directed)
    growth of the set of states used to approximate query states. */
class DynamicAverager: public Averager
{
public:
  virtual ~DynamicAverager();

  /** Adds a state to the set of bases used to approximate query
      states.
      \param s The state to add.
      \return Pointer to the averager's canonical of s. */
  virtual StateVectorRef add_basis(const StateVectorRef &s) = 0;
};

typedef boost::shared_ptr<DynamicAverager> DynamicAveragerRef;

class KernelAverager;
typedef boost::shared_ptr<KernelAverager> KernelAveragerRef;

/** An Averager that uses Gaussian kernels to approximate states.  In
    particular, for any given query state s, it assigns to each basis
    state x the weight e^(-d(x,s)^2 / b^2), where b is a
    generalization breadth and d is a scaled Euclidean distance
    function.  Note that all weights therefore lie in the interval
    (0,1].  For efficiency reasons, a weight is set to zero if it
    falls below a certain threshold or if it is smaller than a certain
    fraction of the sum of the larger weights.  (The tail of the
    distribution is truncated, since it has a small effect on the
    approximation.)  */
class KernelAverager: public DynamicAverager
{
public:
  /** Constructs a new KernelAverager that begins with no basis states.
      \param breadth The generalization breadth b
      \param minweight The minimum nonzero value of a weight
      \param minfraction Weights smaller than this fraction of the sum
                         of larger weights are set to zero.
      \param scale The scale parameters of the DistanceFunction d. */
  static DynamicAveragerRef create(double breadth,
				   double minweight,
				   double minfraction,
				   const StateVariables &scale);

  virtual ~KernelAverager();

  virtual const StateVariables &dimensions() const;
  virtual AverageRef approximate(const StateVectorRef &s);
  virtual StateVectorRef add_basis(const StateVectorRef &s);

protected:
  class KernelAverage;
  typedef boost::shared_ptr<KernelAverage> KernelAverageRef;
  typedef boost::weak_ptr<KernelAverage> KernelAverageWeakRef;

  /** Implementation of Average used by KernelAverager */
  class KernelAverage: public Average
  {
  public:
    /** Creates a new KernelAverage
	\param averager The KernelAverager asked to approximate s 
	\param s The state to approximate.
	\param ref A weak reference that should point to the new
	           average */
    static KernelAverageRef create(const KernelAveragerRef &averager,
				   const StateVectorRef &s,
				   KernelAverageWeakRef &ref);

    virtual ~KernelAverage();

    virtual const StateVectorRef &state() const;
    virtual const StateDistribution &basis_weights() const;

    /** Computes the approximation weights from the set of basis
	neighbors, which are pruned according to the KernelAverager's
	parameters. */
    void compute_weights();

    /** Potentially updates this KernelAverage to include a new basis.
	\param d The distance from s to this KernelAverage's state
	         according to the KernelAverager's DistanceFunction
	\param s The new basis state */
    void include(double d, const StateVectorRef &s);

  private:
    KernelAverage(const KernelAveragerRef &averager, const StateVectorRef &s);

    typedef std::vector<std::pair<double, StateVectorRef> > NeighborContainer;

    const KernelAveragerRef m_averager;
    const StateVectorRef m_s;

    /** A sorted array of the basis states near the given state,
	sorted in ascending order of distance.  Should contain all the
	states within m_averager->m_maxd of the given state, except
	the ones pruned according to m_averager->m_minfraction. */
    NeighborContainer m_neighbors;

    StateDistribution m_weights;
    double m_sum;
  };

  friend class KernelAverage;

private:
  KernelAverager(double breadth, double minweight, double minfraction,
		 const StateVariables &scale);

  const double m_bb;
  const double m_minfraction;
  const double m_maxd;
  const StateVariables m_scale;

  /** Invariant: contains all the keys in the queries hash. */
  CoverTree<StateVectorRef, DistanceFunction> m_querytree;

  /** Invariant: for each state given to add_basis as an argument,
      contains exactly one reference to a state equivalent on the
      dimensions used by this Averager. */
  CoverTree<StateVectorRef, DistanceFunction> m_basistree;

  typedef __gnu_cxx::hash_map<StateVectorRef,
			      KernelAverageWeakRef,
			      StateHasher> QueryContainer;

  /** Invariant: contains all the existing KernelAverages created by
      this KernelAverager.  The keys serve as both queries and bases
      (queries are a superset of bases), so a datum may be NULL if the
      associated key is just a basis, not a query. */
  QueryContainer m_queries;

  /** The subset of the keys of queries that are also bases.  This
      container does not own any of its keys. */
  __gnu_cxx::hash_set<StateVectorRef, StateHasher> m_bases;

  /** Weak pointer to self, passed to KernelAverage constructor. */
  boost::weak_ptr<KernelAverager> m_this;
};

class InterpolationAverager;
typedef boost::shared_ptr<InterpolationAverager> InterpolationAveragerRef;

/** An Averager that uses multilinear interpolation to approximate
    states.  It maps each query state to a scaled Euclidean space,
    where the query is approximated using a uniform grid with spacing
    equal to 2^resolutionfactor points per unit distance.  Where d is
    the number of dimensions, each query is approximated as the
    Average of exactly 2^d points in this grid.  Note that each of
    these points has one of exactly two values for each dimension.
    For each of these points, the weight is the product of the weights
    for the d linear interpolations of each dimension of the query
    using the two grid values in that dimension.

    Note that this Averager generates and maintains its own set of
    basis vectors.  It allocates the vectors lazily.  These vectors
    will only have nonzero values for those indices specified by the
    dimensions parameter to the constructor.

    This Averager generates Average objects with the property that all
    the weights sum to 1. */
class InterpolationAverager: public Averager
{
public:
  /** Creates a new InterpolationAverager.
      \param resolutionfactor Controls the spacing of the uniform grid
                              used as the basis set
      \param scale The weights used to obtain the scaled Euclidean
	           space.  Unused dimensions are set to 0 in the basis
	           vectors. */
  static AveragerRef create(int resolutionfactor, const StateVariables &scale);

  virtual ~InterpolationAverager();

  virtual const StateVariables &dimensions() const;
  virtual AverageRef approximate(const StateVectorRef &s);

protected:
  class InterpolationAverage;
  typedef boost::shared_ptr<InterpolationAverage> InterpolationAverageRef;
  typedef boost::weak_ptr<InterpolationAverage> InterpolationAverageWeakRef;
  
  /** Implementation of Average used by InterpolationAverager */
  class InterpolationAverage: public Average
  {
  public:
    /** Creates a new InterpolationAverage
	\param averager The InterpolationAverager asked to approximate s 
	\param s The state to approximate.
	\param is_basis Whether s is a basis state
	\param ref A pointer that should be assigned the new average */
    static InterpolationAverageRef
    create(const InterpolationAveragerRef &averager,
	   const StateVectorRef &s,
	   bool is_basis,
	   InterpolationAverageWeakRef &ref);

    virtual ~InterpolationAverage();

    virtual const StateVectorRef &state() const;
    virtual const StateDistribution &basis_weights() const;

    void set_basis() {
      m_basis = true;
    }

  private:
    InterpolationAverage(const InterpolationAveragerRef &averager,
			 const StateVectorRef &s,
			 bool is_basis);

    const InterpolationAveragerRef m_averager;
    const StateVectorRef m_s;
    StateDistribution m_weights;
    bool m_basis; // True if this query is also a basis
  };

  friend class InterpolationAverage;

private:
  InterpolationAverager(int resolutionfactor, const StateVariables &scale);

  const int m_res;
  const StateVariables m_scale;

  typedef std::map<StateVectorRef,
		   InterpolationAverageWeakRef,
		   AbstractStateComparator> QueryContainer;

  /** Invariant: contains all the existing InterpolationAverages that
      this InterpolationAverager has created.  The keys include both
      bases and queries, so some keys have a NULL value. */
  QueryContainer m_queries;

  /** Weak reference to self, passed to Average constructors. */
  boost::weak_ptr<InterpolationAverager> m_this;
};

class ProjectionAverager;
typedef boost::shared_ptr<ProjectionAverager> ProjectionAveragerRef;

/** An Averager that projects a given state onto the state subspace
    obtained by ignoring certain state dimensions.  Since each Average
    only uses a single basis state, this approximation corresponds
    exactly to state abstraction.  Each Average object created by this
    averager returns a value for basis_weights() that has exactly one
    basis with weight 1.0. */
class ProjectionAverager: public DynamicAverager {
public:
  /** \param dims The state variables than span the desired subspace.
                  Note that the weights attached to the included
                  variables are irrelevant. */
  static DynamicAveragerRef create(const StateVariables &dims);

  virtual ~ProjectionAverager();

  virtual const StateVariables &dimensions() const;
  virtual AverageRef approximate(const StateVectorRef &s);
  virtual StateVectorRef add_basis(const StateVectorRef &s);

protected:
  class ProjectionAverage;
  typedef boost::shared_ptr<ProjectionAverage> ProjectionAverageRef;
  typedef boost::weak_ptr<ProjectionAverage> ProjectionAverageWeakRef;

  class ProjectionAverage: public Average {
  public:
    static ProjectionAverageRef
    create(const ProjectionAveragerRef &averager,
	   const StateVectorRef &s,
	   ProjectionAverageWeakRef &ref);

    virtual ~ProjectionAverage();

    virtual const StateVectorRef &state() const;
    virtual const StateDistribution &basis_weights() const;

  private:
    ProjectionAverage(const ProjectionAveragerRef &averager,
		      const StateVectorRef &s);

    const ProjectionAveragerRef m_averager;
    StateDistribution m_base;
  };

  friend class ProjectionAverage;

  typedef std::map<StateVectorRef,
		   ProjectionAverageWeakRef,
		   AbstractStateComparator> AveragesContainer;

private:
  ProjectionAverager(const StateVariables &dims);

  const StateVariables m_dims;

  /** The keys of this map are the canonical forms of all the states
      seen by this averager.  The data of this map are the averages
      that this averager has create. */
  AveragesContainer m_averages;

  /** Weak reference to self, passed to Average constructors. */
  boost::weak_ptr<ProjectionAverager> m_this;
};

class DiscretizationAverager;
typedef boost::shared_ptr<DiscretizationAverager> DiscretizationAveragerRef;

class DiscretizationAverager: public Averager
{
public:
  /** Creates a new DiscretizationAverager.
      \param resolutionfactor Controls the spacing of the uniform grid
                              used as the basis set
      \param scale The weights used to obtain the scaled Euclidean
	           space.  Unused dimensions are set to 0 in the basis
	           vectors. */
  static AveragerRef create(int resolutionfactor, const StateVariables &scale);

  virtual ~DiscretizationAverager();

  virtual const StateVariables &dimensions() const;
  virtual AverageRef approximate(const StateVectorRef &s);

protected:
  class DiscretizationAverage;
  typedef boost::shared_ptr<DiscretizationAverage> DiscretizationAverageRef;
  typedef boost::weak_ptr<DiscretizationAverage> DiscretizationAverageWeakRef;

  /** Implementation of Average used by DiscretizationAverager */
  class DiscretizationAverage: public Average
  {
  public:
    /** Creates a new DiscretizationAverage
	\param averager The DiscretizationAverager asked to approximate s 
	\param s The state to approximate.
	\param is_basis Whether s is a basis state
	\param ref A weak pointer that should be assigned the new
	           average */
    static DiscretizationAverageRef
    create(const DiscretizationAveragerRef &averager,
	   const StateVectorRef &s,
	   bool is_basis,
	   DiscretizationAverageWeakRef &ref);

    virtual ~DiscretizationAverage();

    virtual const StateVectorRef &state() const;
    virtual const StateDistribution &basis_weights() const;

    void set_basis() {
      m_basis = true;
    }

  private:
    DiscretizationAverage(const DiscretizationAveragerRef &averager,
			  const StateVectorRef &s,
			  bool is_basis);

    const DiscretizationAveragerRef m_averager;
    const StateVectorRef m_s;
    StateDistribution m_weights;
    bool m_basis; // True if this query is also a basis
  };

  friend class DiscretizationAverage;

private:
  DiscretizationAverager(int resolutionfactor, const StateVariables &scale);

  const int m_res;
  const StateVariables m_scale;

  typedef std::map<StateVectorRef,
		   DiscretizationAverageWeakRef,
		   AbstractStateComparator> QueryContainer;

  /** Invariant: contains all the existing DiscretizationAverages that
      this DiscretizationAverager has created.  The keys include both
      bases and queries, so the data pointer may be NULL. */
  QueryContainer m_queries;

  /** Weak reference to self, passed to Average constructors. */
  boost::weak_ptr<DiscretizationAverager> m_this;
};

#endif
