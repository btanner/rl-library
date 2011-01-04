#include "primitive.hh"

#include <sstream>

EffectDistribution PrimitiveTask::PrimitiveStateActionModel::s_empty;

VectorEffectRef VectorEffect::create(const StateVectorRef &s,
				     const StateVectorRef &succ,
				     const StateVariables &dimensions)
{
  return VectorEffectRef(new VectorEffect(s, succ, dimensions));
}

VectorEffect::VectorEffect(const StateVectorRef &s,
			   const StateVectorRef &succ,
			   const StateVariables &dimensions)
{
  assert(dimensions.size() < 10);
  assert(s->size() == succ->size());
  StateVariables::const_iterator i;
  for (i = dimensions.begin(); i != dimensions.end(); ++i) {
    const unsigned index = i->first;
    assert(s->size() > index && succ->size() > index);
    const double diff = (*succ)[index] - (*s)[index];
    if (diff != 0)
      m_changes.push_back(std::make_pair(index, diff));
  }
}

VectorEffect::~VectorEffect()
{}

StateVectorRef VectorEffect::apply(const StateVectorRef &s) const
{
  boost::shared_ptr<std::vector<double> > result(new std::vector<double>(*s));
  std::vector<std::pair<unsigned,double> >::const_iterator i;
  for (i = m_changes.begin(); i != m_changes.end(); ++i) {
    assert(result->size() > i->first);
    (*result)[i->first] += i->second;
  }
  return result;
}

void VectorEffect::debug(std::ostream &out) const
{
  std::vector<std::pair<unsigned,double> >::const_iterator i;
  for (i = m_changes.begin(); i != m_changes.end(); ++i)
    out << i->first << (i->second > 0 ? ":+" : ":") << i->second << ";";
}

TaskRef PrimitiveTask::create(const std::string &name,
			      int primitive,
			      double threshold,
			      double maxval,
			      const DynamicAveragerRef &modelapproximator,
			      const StatePredicateRef &precondition)
{
  PrimitiveTaskRef result(new PrimitiveTask(name, primitive, threshold, maxval,
					    modelapproximator, precondition));
  result->m_this = result;
  return result;
}

PrimitiveTask::PrimitiveTask(const std::string &name,
			     int primitive,
			     double threshold,
			     double maxval,
			     const DynamicAveragerRef &modelapproximator,
			     const StatePredicateRef &precondition):
  m_name(name.length() > 0 ? name : compute_name(primitive)),
  m_primitive(primitive), m_threshold(threshold), m_maxval(maxval),
  m_averager(modelapproximator), m_precondition(precondition)
{}

PrimitiveTask::~PrimitiveTask()
{
//   std::cout << "Data for primitive action " << primitive << "\n";
//   DataContainer::const_iterator it;
//   for (it = m_data.begin(); it != m_data.end(); ++it)
//     std::cout << it->first << "\n";
//   std::cout << "END DATA\n";

  DataContainer::iterator i;
  for (i = m_data.begin(); i != m_data.end(); ++i) {
    PrimitiveStateActionData *data = i->second;
    delete data;
  }
}

const std::string &PrimitiveTask::name() const
{
  return m_name;
}

bool PrimitiveTask::available(const StateVectorRef &s) const
{
  return (*m_precondition)(s);
}

bool PrimitiveTask::terminal(const StateVectorRef &s) const
{
  return true;
}

StatePolicyRef PrimitiveTask::policy(const StateVectorRef &s)
{
  return StatePolicyRef();
}

StateActionModelRef PrimitiveTask::model(const StateVectorRef &s)
{
  AverageRef average = averager()->approximate(s);
  PrimitiveStateActionModelWeakRef &m = m_models[average->state()];
  if (m.expired())
    return PrimitiveStateActionModel::create(m_this.lock(), average, m);
  else 
    return m.lock();
}

void PrimitiveTask::propagate_changes()
{
  std::set<PrimitiveStateActionModelWeakRef>::iterator i;
  for (i = m_inbox.begin(); i != m_inbox.end(); ++i)
    if (!i->expired())
      i->lock()->compute_model();
  m_inbox.clear();
}

void PrimitiveTask::debug(std::ostream &out)
{
  out << "models for primitive action " << action() << "\n";
  ModelContainer::const_iterator i;
  for (i = m_models.begin(); i != m_models.end(); ++i)
    i->second.lock()->debug(out);
  out << "end models\n";
}

void PrimitiveTask::update(const StateVectorRef &s, double r, const StateVectorRef &succ)
{
  PrimitiveStateActionData *dat = get_data(m_averager->add_basis(s));
  EffectCounts::value_type
    val(VectorEffect::create(s, succ, m_averager->dimensions()), 0);
  VectorEffectCountsComparator comparator;
  EffectCounts::iterator cnt =
    vector_set_insert(dat->effect_counts, val, comparator).first;
  ++cnt->second;

  update(dat, r);
}

void PrimitiveTask::update(const StateVectorRef &s, double r)
{
  update(get_data(m_averager->add_basis(s)), r);
}

PrimitiveTask::PrimitiveStateActionData::PrimitiveStateActionData():
  count(0), cumulative_reward(0.0)
{}

PrimitiveTask::PrimitiveStateActionModelRef
PrimitiveTask::PrimitiveStateActionModel::
create(const PrimitiveTaskRef &parent, const AverageRef &average,
       PrimitiveStateActionModelWeakRef &ref)
{
  PrimitiveStateActionModelRef
    result(new PrimitiveStateActionModel(parent, average));
  ref = result->m_this = result;
  result->m_average->add_observer(result->m_this);
  result->compute_model();
  return result;
}

PrimitiveTask::PrimitiveStateActionModel::
PrimitiveStateActionModel(const PrimitiveTaskRef &parent,
			  const AverageRef &average):
  m_parent(parent), m_average(average)
{}

PrimitiveTask::PrimitiveStateActionModel::~PrimitiveStateActionModel()
{
  std::vector<std::pair<StateVectorRef, PrimitiveStateActionData *> >::iterator i;
  for (i = m_translation.begin(); i != m_translation.end(); ++i)
    vector_set_erase(i->second->observers, m_this);

  m_average->remove_observer(m_this);
}

const StateVectorRef &PrimitiveTask::PrimitiveStateActionModel::state() const
{
  return m_average->state();
}

double PrimitiveTask::PrimitiveStateActionModel::reward() const
{
  return m_sum < m_parent->m_threshold ? m_parent->m_maxval : m_r;
}

const EffectDistribution &
PrimitiveTask::PrimitiveStateActionModel::effects() const
{
  return m_sum < m_parent->m_threshold ? s_empty : m_effects_map;
}

void PrimitiveTask::PrimitiveStateActionModel:: observe_average_change()
{
  m_parent->m_inbox.insert(m_this);
}

// XXX optimize to only compute effects if sum >= parent.threshold?
void PrimitiveTask::PrimitiveStateActionModel::compute_model()
{
  m_sum = 0.0;
  m_r = 0.0;
  m_effects_map.clear();

  const StateDistribution &instances = m_average->basis_weights();
  StateDistribution::const_iterator a = instances.begin();
  StateTranslation::iterator b = m_translation.begin();
  while (a != instances.end()) {
    // Remove erstwhile instances
    while (b != m_translation.end() && b->first < a->first) {
      vector_set_erase(b->second->observers, m_this);
      b = m_translation.erase(b);
    }
    // Subscribe if necessary
    if (b == m_translation.end() || a->first < b->first) {
      PrimitiveStateActionData *data = m_parent->get_data(a->first);
      b = m_translation.insert(b, std::make_pair(a->first, data));
      vector_set_insert(b->second->observers, m_this);
    }

    // Add the weighted data for this instance
    m_sum += a->second * b->second->count;
    m_r += a->second * b->second->cumulative_reward;
    const EffectCounts &data_effects = b->second->effect_counts;
    EffectCounts::const_iterator i;
    for (i = data_effects.begin(); i != data_effects.end(); ++i) {
      EffectDistribution::value_type val(i->first, 0);
      LessFirst<EffectDistribution::value_type> comparator;
      EffectDistribution::iterator e =
	vector_set_insert(m_effects_map, val, comparator).first;
      e->second += a->second * i->second;
    }

    ++a; ++b;
  }

  // Remove erstwhile instances
  while (b != m_translation.end()) {
    vector_set_erase(b->second->observers, m_this);
    b = m_translation.erase(b);
  }

  // normalize
  m_r /= m_sum;
  EffectDistribution::iterator i;
  for (i = m_effects_map.begin(); i != m_effects_map.end(); ++i)
    i->second /= m_sum;

//   debug(std::cout); //XXX

  if (m_sum >= m_parent->m_threshold)
    notify_model_observers();
}

void PrimitiveTask::PrimitiveStateActionModel::debug(std::ostream &out) const
{
  out << "Primitive model of State " << state() << "\n";
  out << "\tsum = " << m_sum << "\n";
  out << "\tr = " << m_r << "\n";
  out << "\t" << m_effects_map.size() << " effects\n";
}

std::string PrimitiveTask::compute_name(int primitive)
{
  std::stringstream buffer;
  buffer << primitive;
  return buffer.str();
}

AveragerRef PrimitiveTask::averager() const
{
  return m_averager;
}

void PrimitiveTask::update(PrimitiveStateActionData *dat, double r)
{
  ++dat->count;
  dat->cumulative_reward += r;
  std::vector<PrimitiveStateActionModelWeakRef>::const_iterator o;
  for (o = dat->observers.begin(); o != dat->observers.end(); ++o)
    m_inbox.insert(*o);
}

TaskRef DiscretizedPrimitiveTask::create(const std::string &name,
					 int primitive,
					 unsigned threshold,
					 double maxval,
					 int resolutionfactor,
					 const StateVariables &scale,
					 const StatePredicateRef & precondition)
{
  DiscretizedPrimitiveTaskRef
    result(new DiscretizedPrimitiveTask(name, primitive, threshold, maxval,
					resolutionfactor, scale,
					precondition));
  result->set_weak_this_reference(result);
  return result;
}

DiscretizedPrimitiveTask::
DiscretizedPrimitiveTask(const std::string &name,
			 int primitive,
			 unsigned threshold,
			 double maxval,
			 int resolutionfactor,
			 const StateVariables &scale,
			 const StatePredicateRef &precondition):
  PrimitiveTask(name, primitive, threshold, maxval,
		DynamicAveragerRef(), precondition),
  m_discrete_averager(DiscretizationAverager::create(resolutionfactor, scale))
{}

DiscretizedPrimitiveTask::~DiscretizedPrimitiveTask()
{}

// get_data assumes that the StateVectorRef is canonical and will always remain alive.
void DiscretizedPrimitiveTask::update(const StateVectorRef &s, double r, const StateVectorRef &succ)
{
  AverageRef avg1 = m_discrete_averager->approximate(s);
  const StateDistribution &temp1 = avg1->basis_weights();
  assert(temp1.size() == 1);
  const StateVectorRef ds = temp1.begin()->first;

  AverageRef avg2 = m_discrete_averager->approximate(succ);
  const StateDistribution &temp2 = avg2->basis_weights();
  assert(temp2.size() == 1);
  const StateVectorRef dsucc = temp2.begin()->first;

  PrimitiveStateActionData *dat = get_data(ds);
  EffectCounts::value_type
    val(AbsoluteEffect::create(dsucc, m_discrete_averager->dimensions()), 0);
  AbsoluteEffectCountsComparator comparator;
  EffectCounts::iterator cnt =
    vector_set_insert(dat->effect_counts, val, comparator).first;
  ++cnt->second;

  PrimitiveTask::update(dat, r);
}

void DiscretizedPrimitiveTask::update(const StateVectorRef &s, double r)
{
  AverageRef avg = m_discrete_averager->approximate(s);
  const StateDistribution &temp1 = avg->basis_weights();
  assert(temp1.size() == 1);
  const StateVectorRef &ds = temp1.begin()->first;

  PrimitiveTask::update(get_data(ds), r);
}

AveragerRef DiscretizedPrimitiveTask::averager() const {
  return m_discrete_averager;
}
