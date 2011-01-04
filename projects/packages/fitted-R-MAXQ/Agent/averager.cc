#include "averager.hh"

#include <cmath>
#include <iterator>

Average::Observer::~Observer()
{}

Average::~Average()
{}

void Average::add_observer(const ObserverWeakRef &o)
{
  vector_set_insert(m_observers, o);
}

void Average::remove_observer(const ObserverWeakRef &o)
{
  vector_set_erase(m_observers, o);
}

void Average::notify_average_observers()
{
  std::vector<ObserverWeakRef>::iterator i = m_observers.begin();
  for (i = m_observers.begin(); i != m_observers.end(); ++i)
    i->lock()->observe_average_change();
}

Averager::~Averager()
{}

DynamicAverager::~DynamicAverager()
{}

DynamicAveragerRef KernelAverager::create(double breadth,
					  double minweight,
					  double minfraction,
					  const StateVariables &scale)
{
  KernelAveragerRef result(new KernelAverager(breadth, minweight,
					      minfraction, scale));
  result->m_this = result;
  return result;
}

KernelAverager::KernelAverager(double breadth,
			       double minweight,
			       double minfraction,
			       const StateVariables &scale):
  m_bb(breadth * breadth),
  m_minfraction(minfraction),
  m_maxd(std::sqrt(-m_bb * std::log(minweight))),
  m_scale(scale),
  m_querytree(DistanceFunction(scale)),
  m_basistree(DistanceFunction(scale))
{}

KernelAverager::~KernelAverager()
{}

const StateVariables &KernelAverager::dimensions() const
{
  return m_scale;
}

AverageRef KernelAverager::approximate(const StateVectorRef &s)
{
  StateVectorRef query = m_querytree.insert(s);
  QueryContainer::value_type val(query, KernelAverageWeakRef());
  std::pair<QueryContainer::iterator, bool> result = m_queries.insert(val);
  KernelAverageWeakRef &average = result.first->second;
  if (average.expired())
    return KernelAverage::create(m_this.lock(), query, average);
  else
    return average.lock();
}

StateVectorRef KernelAverager::add_basis(const StateVectorRef &s)
{
  StateVectorRef query = m_querytree.insert(s);
  if (query == s) { // new query (and basis)
    QueryContainer::value_type val(query, KernelAverageWeakRef());
    m_queries.insert(val);
  }
  if (m_bases.insert(query).second) { // new basis
    StateVectorRef basis = m_basistree.insert(query);
    assert(basis == query);
    // s is novel; update relevant queries
    // find possibly relevant queries
    std::vector<std::pair<double, StateVectorRef> > buffer;
    m_querytree.neighbors(std::back_inserter(buffer), basis, m_maxd);

    // call include method on each query
    std::vector<std::pair<double, StateVectorRef> >::iterator i;
    for (i = buffer.begin(); i != buffer.end(); ++i) {
      QueryContainer::iterator it = m_queries.find(i->second);
      assert(it != m_queries.end());
      KernelAverageWeakRef &ka = it->second;
      if (!ka.expired())
	ka.lock()->include(i->first, basis);
    }
  }
  return query;
}

KernelAverager::KernelAverageRef KernelAverager::
KernelAverage::create(const KernelAveragerRef &averager,
		      const StateVectorRef &s,
		      KernelAverageWeakRef &ref)
{
  KernelAverageRef result(new KernelAverage(averager, s));
  ref = result;
  return result;
}

KernelAverager::KernelAverage::KernelAverage(const KernelAveragerRef &averager,
					     const StateVectorRef &s):
  m_averager(averager), m_s(s), m_sum(0.0)
{
  assert(m_averager);
  assert(m_s);
  m_averager->m_basistree.neighbors(std::back_inserter(m_neighbors),
  				    m_s,
  				    m_averager->m_maxd);
  std::sort(m_neighbors.begin(), m_neighbors.end());
  compute_weights();
}

KernelAverager::KernelAverage::~KernelAverage()
{
  QueryContainer::iterator q = m_averager->m_queries.find(m_s);
  assert(q != m_averager->m_queries.end());
  assert(q->second.expired());
  if (m_averager->m_bases.find(q->first) == m_averager->m_bases.end()) {
    // Not a basis, safe to delete entirely
    m_averager->m_querytree.remove(q->first);
    m_averager->m_queries.erase(q);
  }
}

const StateVectorRef &KernelAverager::KernelAverage::state() const
{
  return m_s;
}

const StateDistribution &KernelAverager::KernelAverage::basis_weights() const
{
  return m_weights;
}

void KernelAverager::KernelAverage::compute_weights()
{
  m_weights.clear();
  m_sum = 0.0;
  NeighborContainer::iterator i = m_neighbors.begin();
  while (i != m_neighbors.end()) { // note also break below
    const double d = i->first;
    const StateVectorRef &x = i->second;
    const double w = std::exp(-d*d / m_averager->m_bb);
    if (m_sum == 0.0 || w/m_sum >= m_averager->m_minfraction) {
      vector_map_assoc(m_weights, x, w);
      m_sum += w;
      ++i;
    } else
      break;
  }
  m_neighbors.erase(i, m_neighbors.end());
  if (m_neighbors.capacity() > 2 * m_neighbors.size()) {
    // Decrease memory footprint.
    NeighborContainer dummy(m_neighbors); // new vector has smaller capacity()
    m_neighbors.swap(dummy);
    StateDistribution dummy2(m_weights); // new vector has smaller capacity()
    m_weights.swap(dummy2);
  }
}

void KernelAverager::KernelAverage::include(double d, const StateVectorRef &s)
{
  assert(d >= 0.0);
  assert(s);
  std::pair<double, StateVectorRef> val(d,s);
  const double w = std::exp(-d*d / m_averager->m_bb);
  if (m_neighbors.empty() || val < m_neighbors.back()) {
    vector_set_insert(m_neighbors, val);
    compute_weights();
    notify_average_observers();
  } else {
    assert(m_sum > 0.0); // since !neighbors.empty()
    if (w/m_sum >= m_averager->m_minfraction) {
      m_neighbors.push_back(val);
      compute_weights();
      notify_average_observers();
    }
  }
}

AveragerRef InterpolationAverager::create(int resolutionfactor,
					  const StateVariables &scale)
{
  InterpolationAveragerRef
    result(new InterpolationAverager(resolutionfactor, scale));
  result->m_this = result;
  return result;
}

InterpolationAverager::
InterpolationAverager(int resolutionfactor, const StateVariables &scale):
  m_res(resolutionfactor), m_scale(scale),
  m_queries(AbstractStateComparator(m_scale))
{}

InterpolationAverager::~InterpolationAverager()
{}

const StateVariables &InterpolationAverager::dimensions() const
{
  return m_scale;
}

AverageRef InterpolationAverager::approximate(const StateVectorRef &s)
{
  QueryContainer::value_type val(s, InterpolationAverageWeakRef());
  std::pair<QueryContainer::iterator, bool> result = m_queries.insert(val);
  const bool was_inserted = result.second;
  QueryContainer::iterator &it = result.first;
  const StateVectorRef &state = it->first;
  InterpolationAverageWeakRef &average = it->second;
  if (average.expired()) // new query
    return InterpolationAverage::
      create(m_this.lock(), state, !was_inserted, average);
  else
    return average.lock();
}

InterpolationAverager::InterpolationAverageRef InterpolationAverager::InterpolationAverage::
create(const InterpolationAveragerRef &averager,
       const StateVectorRef &s,
       bool is_basis,
       InterpolationAverageWeakRef &ref)
{
  InterpolationAverageRef
    result(new InterpolationAverage(averager, s, is_basis));
  ref = result;
  return result;
}

InterpolationAverager::InterpolationAverage::
InterpolationAverage(const InterpolationAveragerRef &averager,
		     const StateVectorRef &s,
		     bool is_basis):
  m_averager(averager), m_s(s), m_basis(is_basis)
{
  assert(m_averager);
  assert(m_s);
  const StateVariables &scale = m_averager->m_scale;
  const size_t dim = scale.size();
  const double increment = std::ldexp(1.0, -m_averager->m_res);

  std::vector<double> floor(dim);
  std::vector<double> alpha(dim);
  unsigned i = 0;
  StateVariables::const_iterator it;
  for (it = scale.begin(); it != scale.end(); ++it) {
    assert(m_s->size() > it->first);
    const double element = (*m_s)[it->first] * it->second;
    const double intermediate =
      std::floor(std::ldexp(element, m_averager->m_res));
    floor[i] = std::ldexp(intermediate, -m_averager->m_res);
    alpha[i] = (element - floor[i]) / increment;
    ++i;
  }
  assert(i == dim);

  const unsigned numsucc = 1 << dim;
  for (unsigned bitvector = 0; bitvector < numsucc; ++bitvector) {
    boost::shared_ptr<std::vector<double> >
      successor(new std::vector<double>(m_s->size(), 0.0));
    double weight = 1.0;
    i = 0;
    for (it = scale.begin(); it != scale.end(); ++it) {
      const unsigned index = it->first;
      if (0 == (bitvector & (1 << i))) {
	(*successor)[index] = floor[i] / it->second;
	weight *= 1.0 - alpha[i];
      } else {
	(*successor)[index] = (floor[i] + increment) / it->second;
	weight *= alpha[i];
      }
      ++i;
    }
    assert(i == dim);
    if (weight > 0) {
      QueryContainer::value_type val(successor, InterpolationAverageWeakRef());
      QueryContainer::iterator it = m_averager->m_queries.insert(val).first;
      if (!it->second.expired()) {
	// Make sure that any existing Average approximating successor
	// knows that successor is a basis state.
	InterpolationAverageRef avg(it->second);
	avg->set_basis();
      }
      const StateVectorRef &succ = it->first;
      vector_map_assoc(m_weights, succ, weight);
    }
  }
}

InterpolationAverager::InterpolationAverage::~InterpolationAverage()
{
  if (!m_basis) {
    QueryContainer::iterator it = m_averager->m_queries.find(state());
    assert(it != m_averager->m_queries.end());
    assert(it->second.expired());
    m_averager->m_queries.erase(it);
  }
}

const StateVectorRef &InterpolationAverager::InterpolationAverage::state() const
{
  return m_s;
}

const StateDistribution &
InterpolationAverager::InterpolationAverage::basis_weights() const
{
  return m_weights;
}

DynamicAveragerRef ProjectionAverager::create(const StateVariables &dims)
{
  ProjectionAveragerRef result(new ProjectionAverager(dims));
  result->m_this = result;
  return result;
}

ProjectionAverager::ProjectionAverager(const StateVariables &dims):
  m_dims(dims), m_averages(AbstractStateComparator(m_dims))
{} 

ProjectionAverager::~ProjectionAverager()
{}

const StateVariables &ProjectionAverager::dimensions() const
{
  return m_dims;
}

AverageRef ProjectionAverager::approximate(const StateVectorRef &s)
{
  AveragesContainer::value_type val(s, ProjectionAverageWeakRef());
  std::pair<AveragesContainer::iterator, bool> result = m_averages.insert(val);
  AveragesContainer::iterator &it = result.first;
  ProjectionAverageWeakRef &avg = it->second;
  if (avg.expired())
    return ProjectionAverage::create(m_this.lock(), it->first, avg);
  else
    return avg.lock();
}

StateVectorRef ProjectionAverager::add_basis(const StateVectorRef &s)
{
  AveragesContainer::value_type val(s, ProjectionAverageWeakRef());
  return m_averages.insert(val).first->first;
}

ProjectionAverager::ProjectionAverageRef
ProjectionAverager::ProjectionAverage::
create(const ProjectionAveragerRef &averager,
       const StateVectorRef &s,
       ProjectionAverageWeakRef &ref)
{
  ProjectionAverageRef result(new ProjectionAverage(averager, s));
  ref = result;
  return result;
}

ProjectionAverager::ProjectionAverage::
ProjectionAverage(const ProjectionAveragerRef &averager, const StateVectorRef &s):
  m_averager(averager)
{
  m_base.push_back(std::make_pair(s, 1.0));
}

ProjectionAverager::ProjectionAverage::~ProjectionAverage()
{
  // Leave our entry in the hash table, since state() is a basis state.
}

const StateVectorRef &ProjectionAverager::ProjectionAverage::state() const
{
  return m_base.begin()->first;
}

const StateDistribution &ProjectionAverager::ProjectionAverage::
basis_weights() const
{
  return m_base;
}

AveragerRef DiscretizationAverager::create(int resolutionfactor,
					   const StateVariables &scale)
{
  DiscretizationAveragerRef
    result(new DiscretizationAverager(resolutionfactor, scale));
  result->m_this = result;
  return result;
}

DiscretizationAverager::
DiscretizationAverager(int resolutionfactor,
		       const StateVariables &scale):
  m_res(resolutionfactor), m_scale(scale),
  m_queries(AbstractStateComparator(m_scale))
{}

DiscretizationAverager::~DiscretizationAverager()
{}

const StateVariables &DiscretizationAverager::dimensions() const
{
  return m_scale;
}

AverageRef DiscretizationAverager::approximate(const StateVectorRef &s)
{
  QueryContainer::value_type val(s, DiscretizationAverageWeakRef());
  std::pair<QueryContainer::iterator, bool> result = m_queries.insert(val);
  DiscretizationAverageWeakRef &avg = result.first->second;
  if (avg.expired()) { // new query
    const StateVectorRef &state = result.first->first;
    bool is_basis = !result.second; // s already there but no query
    return DiscretizationAverage::create(m_this.lock(), state, is_basis, avg);
  } else
    return avg.lock();
}

DiscretizationAverager::DiscretizationAverageRef DiscretizationAverager::DiscretizationAverage::
create(const DiscretizationAveragerRef &averager,
       const StateVectorRef &s,
       bool is_basis,
       DiscretizationAverageWeakRef &ref)
{
  DiscretizationAverageRef
    result(new DiscretizationAverage(averager, s, is_basis));
  ref = result;
  return result;
}

DiscretizationAverager::DiscretizationAverage::
DiscretizationAverage(const DiscretizationAveragerRef &averager,
		      const StateVectorRef &s,
		      bool is_basis):
  m_averager(averager), m_s(s), m_basis(is_basis)
{
  assert(m_averager);
  assert(m_s);
  const StateVariables &scale = m_averager->m_scale;
  const double increment = std::ldexp(1.0, -m_averager->m_res);
  
  boost::shared_ptr<std::vector<double> >
    successor(new std::vector<double>(s->size(), 0.0));
  StateVariables::const_iterator it;
  for (it = scale.begin(); it != scale.end(); ++it) {
    assert(m_s->size() > it->first);
    const unsigned index = it->first;
    const double element = (*s)[index] * it->second;
    const double intermediate =
      std::floor(std::ldexp(element, m_averager->m_res));
    const double floor = std::ldexp(intermediate, -m_averager->m_res);
    const double alpha = (element - floor) / increment;
    if (alpha > 0.5)
      (*successor)[index] = (floor + increment) / it->second;
    else
      (*successor)[index] = floor / it->second;
  }
  
  QueryContainer::value_type val(successor, DiscretizationAverageWeakRef());
  QueryContainer::iterator ins = m_averager->m_queries.insert(val).first;
  if (!ins->second.expired())
    ins->second.lock()->set_basis();
  const StateVectorRef &succ = ins->first;
  vector_map_assoc(m_weights, succ, 1.0);
}

DiscretizationAverager::DiscretizationAverage::~DiscretizationAverage()
{
  if (!m_basis) {
    QueryContainer::iterator it = m_averager->m_queries.find(state());
    assert(it != m_averager->m_queries.end());
    assert(it->second.expired());
    m_averager->m_queries.erase(it);
  }
}

const StateVectorRef &DiscretizationAverager::DiscretizationAverage::state() const
{
  return m_s;
}

const StateDistribution &
DiscretizationAverager::DiscretizationAverage::basis_weights() const
{
  return m_weights;
}
