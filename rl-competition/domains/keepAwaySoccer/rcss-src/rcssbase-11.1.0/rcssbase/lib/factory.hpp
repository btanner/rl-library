// -*-c++-*-

/***************************************************************************
                                factory.hpp
                             -------------------
    Template singleton for creating polymorphic objects based on some idx
    begin                : 2002-10-08
    copyright            : (C) 2002 by The RoboCup Soccer Simulator
                           Maintenance Group.
    email                : sserver-admin@lists.sourceforge.net
***************************************************************************/

/***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU LGPL as published by the Free Software  *
 *   Foundation; either version 2 of the License, or (at your option) any  *
 *   later version.                                                        *
 *                                                                         *
 ***************************************************************************/


#ifndef RCSSFACTORY_H
#define RCSSFACTORY_H

#include <map>
#include <list>
#include <stack>
#include <iostream>
#include <memory>

namespace rcss
{
namespace lib
{
template< typename X >
class less
{
public:
    bool operator()( X a, X b ) const
      {
          return a < b;
      }
};

template<>
class less< const char* >
{
public:
    bool operator()( const char* a, const char* b ) const
      {
          return strcmp( a, b ) < 0;
      }
};

template<>
class less< char* >
{
public:
    bool operator()( char* a, char* b ) const
			{
          return strcmp( a, b ) < 0;
			}
};


/*!
//===========================================================
//
//  CLASS: RegHolderImpl
//
//  DESC: Base type for AutoReger
//
//===========================================================
*/

class RegHolderImpl
{
public:
    RegHolderImpl()
      {}

    virtual
    ~RegHolderImpl()
      {}
};

typedef std::auto_ptr< RegHolderImpl > RegHolder;


/*!
//===========================================================
//
//  CLASS: AutoReger
//
//  DESC: Used for automatic registration.
//  NOTE: Auto registration Cannot be used in dynamic libraries
//
//===========================================================
*/

template< typename OF >
class AutoReger
    : public RegHolderImpl
{
public:
    typedef OF Factory;
    typedef typename Factory::Creator Creator;
    typedef typename Factory::Index Index;

    AutoReger( Factory& fact, Creator creator, const Index& idx )
        : m_fact( fact ),
          m_idx( idx )
      {
          m_fact.reg( creator, idx );
      }

    virtual
    ~AutoReger()
      { m_fact.dereg( m_idx ); }
private:
    template< class OF2 >
    AutoReger( const AutoReger< OF2 >& ); // not used

    template< class OF2 >
    AutoReger&
    operator=( const AutoReger< OF2 >& ); // not used

private:
    Factory& m_fact;
    const Index& m_idx;
};


/*!
//===================================================================
//
//  CLASS: Factory
//
//  DESC: An Generic Object Factory (aka Class Store)
//
//===================================================================
*/

template< class Cre,
          class I = const char*,
          class Com = less< I > >
class Factory
{
public:
    typedef Cre Creator;
    typedef I Index;
    typedef Com Compare;
private:
    typedef std::map< Index, std::stack< Creator >, Compare > Map;

public:
    Factory()
      {}

    ~Factory()
      {}

    void
    reg( Creator c, const Index& idx )
      {
          m_creators[ idx ].push( c );
      }

    void
    dereg( const Index& idx )
      {
          //std::cerr << "lib::Factory::dereg " << idx << std::endl;
          typename Map::iterator i = m_creators.find( idx );
          if( i != m_creators.end() )
          {
              if( !i->second.empty() )
                  i->second.pop();
              if( i->second.empty() )
                  m_creators.erase( i );
          }
      }

    bool
    getCreator( Creator& c, const Index& idx )
      {
          typename Map::iterator i = m_creators.find( idx );
          if( i != m_creators.end() && !i->second.empty() )
          {
              c = i->second.top();
              return true;
          }
          return false;
      }

    std::list< Index >
    list()
      {
          std::list< Index > rval;
          for( typename Map::iterator i = m_creators.begin();
               i != m_creators.end(); ++i )
              rval.push_back( i->first );
          return rval;
      }

    std::ostream&
    printList( std::ostream& o = std::cout )
      {
          for( typename Map::iterator i = m_creators.begin();
               i != m_creators.end(); ++i )
              o << "\t" << i->first
                << "(" << i->second.size() << ")"
                << std::endl;
          return o;
      }

    size_t
    size() const
      { return m_creators.size(); }

    size_t
    size( const Index& idx ) const
      {
          for( typename Map::const_iterator i = m_creators.begin();
               i != m_creators.end(); ++i )
          {
              if( !Compare()( idx, i->first )
                  && !Compare()( i->first, idx ) )
              {
                  return i->second.size();
              }
          }
          return 0;
      }

    RegHolder
    autoReg( Creator c, const Index& i )
      {
          return RegHolder( new AutoReger< Factory< Creator, Index, Compare > >( *this, c, i ) );
      }

private:
    Map m_creators;
};
}
}

#endif
