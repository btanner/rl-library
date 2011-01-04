#include "state.hh"

#include <algorithm>
#include <cmath>
#include <iterator>

std::ostream &operator<<(std::ostream &out, const StateVectorRef &s)
{
  std::copy(s->begin(), s->end(), std::ostream_iterator<double>(out, " "));
  return out;
}

DistanceFunction::DistanceFunction(const StateVariables &scale):
  m_scale(scale)
{}

double DistanceFunction::operator()(const StateVectorRef &x,
				    const StateVectorRef &y) const
{
  assert(x && y);
  double dd = 0.0;
  StateVariables::const_iterator i;
  for (i = m_scale.begin(); i != m_scale.end(); ++i) {
    const unsigned index = i->first;
    const double scale = i->second;
    assert(x->size() > index && y->size() > index);
    const double d = scale * ((*y)[index] - (*x)[index]);
    dd += d*d;
  }
  return std::sqrt(dd);
}

AbstractStateComparator::
AbstractStateComparator(const StateVariables &dimensions):
  m_dimensions(dimensions)
{}

bool AbstractStateComparator::
operator()(const StateVectorRef &x, const StateVectorRef &y) const
{
  StateVariables::const_iterator i;
  for (i = m_dimensions.begin(); i != m_dimensions.end(); ++i) {
    const unsigned index = i->first;
    assert(x->size() > index && y->size() > index);
    const double a = (*x)[index];
    const double b = (*y)[index];
    if (a < b)
      return true;
    if (a > b)
      return false;
  }
  return false;
}

StateFunction::~StateFunction()
{}

StateFunctionRef ZeroStateFunction::create()
{
  return StateFunctionRef(new ZeroStateFunction);
}

ZeroStateFunction::~ZeroStateFunction()
{}

double ZeroStateFunction::operator()(const StateVectorRef &x) const
{
  return 0;
}

ZeroStateFunction::ZeroStateFunction()
{}

StateFunctionRef ConstantStateFunction::create(double value)
{
  return StateFunctionRef(new ConstantStateFunction(value));
}

ConstantStateFunction::~ConstantStateFunction()
{}

double ConstantStateFunction::operator()(const StateVectorRef &x) const
{
  return m_value;
}

ConstantStateFunction::ConstantStateFunction(double value): m_value(value)
{}

StatePredicate::~StatePredicate()
{}

void StatePredicate::debug(std::ostream &out) const
{
  out << "StatePredicate(no debug information)\n";
}

StatePredicateRef TrueStatePredicate::create()
{
  return StatePredicateRef(new TrueStatePredicate);
}

TrueStatePredicate::~TrueStatePredicate()
{}

bool TrueStatePredicate::operator()(const StateVectorRef &x) const
{
  return true;
}

TrueStatePredicate::TrueStatePredicate()
{}

StatePredicateRef FalseStatePredicate::create()
{
  return StatePredicateRef(new FalseStatePredicate);
}

FalseStatePredicate::~FalseStatePredicate()
{}

bool FalseStatePredicate::operator()(const StateVectorRef &x) const
{
  return false;
}

FalseStatePredicate::FalseStatePredicate()
{}

StatePredicateRef NegationPredicate::create(const StatePredicateRef &predicate)
{
  return StatePredicateRef(new NegationPredicate(predicate));
}

NegationPredicate::~NegationPredicate()
{}

bool NegationPredicate::operator()(const StateVectorRef &x) const
{
  return !(*original)(x);
}

NegationPredicate::NegationPredicate(const StatePredicateRef &predicate):
  original(predicate)
{}

GoalStatePredicate::~GoalStatePredicate()
{}

bool GoalStatePredicate::operator()(const StateVectorRef &x) const
{
  std::map<size_t, double>::const_iterator i;
  for (i = m_goal.begin(); i != m_goal.end(); ++i) {
    assert(i->first < x->size());
    if ((*x)[i->first] != i->second)
      return false;
  }
  return true;
}

void GoalStatePredicate::debug(std::ostream &out) const
{
  out << "GoalStatePredicate";
  std::map<size_t, double>::const_iterator i;
  for (i = m_goal.begin(); i != m_goal.end(); ++i) {
    out << "(s" << i->first << "=" << i->second << ")";
  }
  out << "\n";
}
