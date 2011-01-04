#ifndef _STATE_HH_
#define _STATE_HH_

/** \file
    The representation of states. */

#include <boost/shared_ptr.hpp>
#include <ext/functional>
#include <algorithm>
#include <cassert>
#include <functional>
#include <iostream>
#include <map>
#include <utility>
#include <vector>

/** Type representing an individual state.  The agent code uses this
    type as its internal representation of a state.  Therefore, it
    first transforms any observation from RL Glue into a
    std::vector<double>.  Smart pointers allow us to keep in memory
    only those instances still used by the agent. */
typedef std::vector<double> StateVector;
typedef boost::shared_ptr<const StateVector> StateVectorRef;

/** The probability of a certain state.  The state pointer must be
    valid (not NULL) and the probability must be nonnegative.
    Typically used as the value type in StateDistribution. */
typedef std::pair<StateVectorRef, double> StateProbability;

/** A common data structure representing a probability distribution
    over states.  Unless otherwise specified, the probabilities sum to
    1.  The contents of this data structure must be sorted (using the
    default comparator), and no two elements can have the same first
    part (state reference).  Note that we use a sorted vector instead
    of a map to minimize the memory footprint and optimize for fast
    iteration. */
typedef std::vector<StateProbability> StateDistribution;

/** A data structure that specifies how to interpret state vectors by
    weighting the state variables.  Each key is an index into a state
    vector, and the corresponding datum is the (positive) factor by
    which to multiply that state variable's value, to obtain the
    normalized state.  Straight Euclidean distance in this transformed
    space emulates scaled Euclidean distance in the original space.
    State vector indices that do not appear in the map are assumed to
    be irrelevant: the data associated with these keys are assumed to
    be 0, in effect implementing state abstraction. */
typedef std::map<size_t, double> StateVariables;

/** Output operator for printing states.  It prints the value of each
    state variable in order, separated by spaces, including a trailing
    space.  This operator aids in debugging.
    \param out The output stream on which to print the state
    \param s The state to print
    \return The output stream, after having the state printed to it */
std::ostream &operator<<(std::ostream &out, const StateVectorRef &s);

/** Function object used to hash states, for use with
    __gnu_cxx::hash_map. */
struct StateHasher: std::unary_function<StateVectorRef, size_t>
{
  size_t operator()(const StateVectorRef &x) const { 
    return reinterpret_cast<size_t>(x.get());
  }
};

/** Scaled Euclidean distance between states.  This implementation may
    optionally ignore certain dimensions of the state space.*/
class DistanceFunction:
  public std::binary_function<StateVectorRef, StateVectorRef, double>
{
public:
  /** \param scale Determines how to scale the inputs before computing
                   distances. */
  DistanceFunction(const StateVariables &scale);

  /** Computes scaled Euclidean distance between two states.
      \param x State with size() larger than the greatest index in the
               scale mapping provided to this Distance Function.
      \param y State with size() larger than the greatest index in the
               scale mapping provided to this Distance Function.
      \return The scaled Euclidean distance in the specified
              dimensions */
  double operator()(const StateVectorRef &x, const StateVectorRef &y) const;

private:
  const StateVariables m_scale;
};

/** Comparator for states that only uses certain dimensions of the
    state space. */
class AbstractStateComparator:
  public std::binary_function<StateVectorRef, StateVectorRef, bool>
{
public:
  /** \param dimensions The keys of this mapping are the indices of
                        the state vectors to consider.  The data
                        associated with each key are ignored. */
  AbstractStateComparator(const StateVariables &dimensions);

  /** \param x A state such that the largest element of
               dimensions is a valid index
      \param y A state such that the largest element of
               dimensions is a valid index
      \return True iff the relevant elements of x are
              lexicographically smaller than the relevant elements of
              y */
  bool operator()(const StateVectorRef &x, const StateVectorRef &y) const;

private:
  const StateVariables m_dimensions;
};

/** An object that computes a real-valued function over states. */
class StateFunction: public std::unary_function<StateVectorRef, double>
{
public:
  virtual ~StateFunction();

  /** \param x The state to evaluate
      \return The value of this function for the given state */
  virtual double operator()(const StateVectorRef &x) const = 0;
};

typedef boost::shared_ptr<const StateFunction> StateFunctionRef;

/** A function whose value is always zero. */
class ZeroStateFunction: public StateFunction
{
public:
  static StateFunctionRef create();

  virtual ~ZeroStateFunction();
  virtual double operator()(const StateVectorRef &x) const;

private:
  ZeroStateFunction();
};

/** A function whose value is constant. */
class ConstantStateFunction: public StateFunction
{
public:
  static StateFunctionRef create(double value);

  virtual ~ConstantStateFunction();
  virtual double operator()(const StateVectorRef &x) const;

private:
  ConstantStateFunction(double value);

  double m_value;
};

/** An object that computes a binary function over states. */
class StatePredicate: public std::unary_function<StateVectorRef, bool>
{
public:
  virtual ~StatePredicate();

  /** \param x The state to evaluate
      \return The value of this function for the given state */
  virtual bool operator()(const StateVectorRef &x) const = 0;

  virtual void debug(std::ostream &out) const;
};

typedef boost::shared_ptr<const StatePredicate> StatePredicateRef;

/** A binary function whose value is always true. */
class TrueStatePredicate: public StatePredicate
{
public:
  static StatePredicateRef create();

  virtual ~TrueStatePredicate();
  virtual bool operator()(const StateVectorRef &x) const;

private:
  TrueStatePredicate();
};

/** A binary function whose value is always false. */
class FalseStatePredicate: public StatePredicate
{
public:
  static StatePredicateRef create();

  virtual ~FalseStatePredicate();
  virtual bool operator()(const StateVectorRef &x) const;

private:
  FalseStatePredicate();
};

class NegationPredicate: public StatePredicate
{
public:
  static StatePredicateRef create(const StatePredicateRef &predicate);

  virtual ~NegationPredicate();
  virtual bool operator()(const StateVectorRef &x) const;

private:
  NegationPredicate(const StatePredicateRef &predicate);

  StatePredicateRef original;
};

/** A binary function that returns true for states that match fixed
    goal values on some dimensions. */
class GoalStatePredicate: public StatePredicate
{
public:
  /** Creates a new GoalStatePredicate with a given goal, specified as
      a mapping from state variable dimensions to desired values.
      \param begin InputIterator with value_type convertible to
                   std::pair<size_t, double>
      \param end InputIterator with value_type convertible to
                 std::pair<size_t, double> */
  template <class InputIterator>
  static StatePredicateRef create(InputIterator begin, InputIterator end) {
    return StatePredicateRef(new GoalStatePredicate(begin, end));
  }

  virtual ~GoalStatePredicate();
  virtual bool operator()(const StateVectorRef &x) const;

  virtual void debug(std::ostream &out) const;

private:
  template <class InputIterator>
  GoalStatePredicate(InputIterator begin, InputIterator end)
    : m_goal(begin, end)
  {}

  const std::map<size_t, double> m_goal;
};

// Utility stuff

/** Comparator that compares pairs only by the first part of each pair. */
template <class PairT>
struct LessFirst: public std::binary_function<PairT, PairT, bool>
{
  bool operator()(const PairT &x, const PairT &y) const {
    return x.first < y.first;
  }
};

/** Inserts an element into a set stored as a sorted vector.
    \param v A vector sorted using the default comparator.
    \param x An element to insert into the vector set
    \return A pair containing first, an iterator to the copied element
            in the vector set, and second, whether or not the vector
            increased in size */
template <class T>
inline std::pair<typename std::vector<T>::iterator, bool>
vector_set_insert(std::vector<T> &v, const T &x)
{
  typename std::vector<T>::iterator pos;
  pos = std::lower_bound(v.begin(), v.end(), x);
  if (pos == v.end() || x < *pos)
    return std::make_pair(v.insert(pos, x), true);
  return std::make_pair(pos, false);
}

/** Inserts an element into a set stored as a sorted vector.
    \param v A vector sorted using the comparator cmp.
    \param x An element to insert into the vector set.
    \param cmp A comparator
    \return A pair containing first, an iterator to the copied element
            in the vector set, and second, whether or not the vector
            increased in size */
template <class T, class Cmp>
inline std::pair<typename std::vector<T>::iterator, bool>
vector_set_insert(std::vector<T> &v, const T &x, Cmp cmp)
{
  typename std::vector<T>::iterator pos;
  pos = std::lower_bound(v.begin(), v.end(), x, cmp);
  if (pos == v.end() || cmp(x, *pos))
    return std::make_pair(v.insert(pos, x), true);
  return std::make_pair(pos, false);
}

/** Erases an element from a set stored as a sorted vector.
    \param v A vector sorted using the default comparator.
    \param x An element to erase
    \return True iff the size of the vector decreased. */
template <class T>
inline bool vector_set_erase(std::vector<T> &v, const T &x)
{
  typename std::vector<T>::iterator del;
  del = std::lower_bound(v.begin(), v.end(), x);
  if (del != v.end() && !(x < *del)) {
    v.erase(del);
    return true;
  }
  return false;
}

/** Erases an element from a set stored as a vector sorted with a
    given comparator.
    \param v A vector sorted using the default comparator.
    \param x An element to erase
    \param cmp A comparator
    \return True iff the size of the vector decreased. */
template <class T, class Cmp>
inline bool vector_set_erase(std::vector<T> &v, const T &x, Cmp cmp)
{
  typename std::vector<T>::iterator del;
  del = std::lower_bound(v.begin(), v.end(), x, cmp);
  if (del != v.end() && !cmp(x, *del)) {
    v.erase(del);
    return true;
  }
  return false;
}

/** Associates a given key with a given datum in a given map stored as
    a sorted vector of pairs.
    \param v A vector of pairs sorted using the default comparator and
             with no duplicate keys
    \param x A key
    \param y A datum
    \return An iterator to the associated key-datum pair in the vector */
template <class A, class B>
inline typename std::vector<std::pair<A,B> >::iterator
vector_map_assoc(std::vector<std::pair<A,B> > &v, const A &x, const B &y)
{
  typename std::pair<A,B> val(x,y);
  typename std::pair<typename std::vector<std::pair<A,B> >::iterator,bool> ret =
    vector_set_insert(v, val, LessFirst<std::pair<A,B> >());
  if (!ret.second) // x was already a key in v
    ret.first->second = y; // change data associated with x
  return ret.first;
}

template <class A, class B>
inline typename std::vector<std::pair<A,B> >::const_iterator
vector_map_find(const std::vector<std::pair<A,B> > &v, const A &x)
{
  typename std::pair<A,B> val(x, B());
  typename std::vector<std::pair<A,B> >::const_iterator ret =
    std::lower_bound(v.begin(), v.end(), val, LessFirst<std::pair<A,B> >());
  if (ret != v.end() && x < ret->first)
    ret = v.end();
  return ret;
}

template <class A, class B>
inline typename std::vector<std::pair<A,B> >::iterator
vector_map_find(std::vector<std::pair<A,B> > &v, const A &x)
{
  typename std::pair<A,B> val(x, B());
  typename std::vector<std::pair<A,B> >::iterator ret =
    std::lower_bound(v.begin(), v.end(), val, LessFirst<std::pair<A,B> >());
  if (ret != v.end() && x < ret->first)
    ret = v.end();
  return ret;
}

#endif
