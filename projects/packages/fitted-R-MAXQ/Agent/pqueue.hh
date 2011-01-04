#ifndef _PQUEUE_H_
#define _PQUEUE_H_

/** \file
    Declaration of PriorityQueue. */

#include <ext/hash_map>
#include <map>

/** A generic implementation of a priority queue. */
template <class Key, class HashFcn = __gnu_cxx::hash<Key> >
class PriorityQueue {
public:
  PriorityQueue() {}

  PriorityQueue(const PriorityQueue &other): order(other.order) {
    typename order_t::const_iterator i;
    for (i = order.begin(); i != order.end(); ++i)
      priorities[i->second] = i->first;
  }

  PriorityQueue &operator=(const PriorityQueue &other) {
    order = other.order;
    priorities.clear();
    typename order_t::const_iterator i;
    for (i = order.begin(); i != order.end(); ++i)
      priorities[i->second] = i->first;
  }

  /** \return The highest priority element in the queue. */
  const Key &front() const {
    assert(order.begin() != order.end());
    return order.begin()->second;
  }

  double front_priority() const {
    assert(order.begin() != order.end());
    return order.begin()->first;
  }

  /** \return The number of elements in the queue. */
  unsigned size() const { return order.size(); }

  /** \return True iff the queue contains no elements. */
  bool empty() const {
    return order.empty();
  }

  /** Removes the highest priority element from the queue. */
  void pop_front() {
    if (!order.empty()) {
      typename order_t::iterator it = order.begin();
      priorities.erase(it->second);
      order.erase(it);
    }
  }

  /** Adds an element to the queue with the given priority. 
      \param k The key to insert
      \param priority The priority to give to k
      \param add Determines the behavior if k already exists.  If
                 true, then priority is added to the existing
                 priority.  If false, the new priority is used if it
                 exceeds the existing priority. */
  void insert(const Key &k, double priority, bool add = false) {
    std::pair<typename priorities_t::iterator, bool> ret =
      priorities.insert(typename priorities_t::value_type(k, order.end()));
    typename order_t::iterator &it = ret.first->second;
    if (ret.second) // new key
      it = order.insert(typename order_t::value_type(priority,k));
    else { // existing key
      if (add)
	priority += it->first;
      if (priority > it->first) {
	order.erase(it);
	it = order.insert(typename order_t::value_type(priority,k));
      }
    }
  }

protected:
  typedef std::multimap<double, Key, std::greater<double> > order_t;
  typedef __gnu_cxx::hash_map<Key,
			      typename order_t::iterator,
			      HashFcn> priorities_t;

private:
  order_t order;
  priorities_t priorities;
};

#endif
