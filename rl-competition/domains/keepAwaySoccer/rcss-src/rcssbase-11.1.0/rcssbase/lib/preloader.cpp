// -*-c++-*-

/***************************************************************************
                              preloader.cpp
                             -------------------
                        Used to preload libs
    begin                : 2003-Sep-01
    copyright            : (C) 2003 by The RoboCup Soccer Simulator 
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

#include "preloader.hpp"
#include "loader.hpp"

namespace rcss
{
    namespace lib
    {

        Preloader::Preloader( const std::string& name,
			      Initialize init,
			      Finalize fin )
            : m_name( name ), m_init( init ), m_fin( fin )
        {
            addLib( *this );
        }

        Preloader::~Preloader()
        {
            remLib( *this ); 
        }

        const std::string&
        Preloader::name() const
        { return m_name; }

        Preloader::Initialize
        Preloader::init() const
        { return m_init; }
        
        Preloader::Finalize
        Preloader::fin() const
        { return m_fin; }       


        void
        Preloader::addLib( const Preloader& preload )
        { 
            s_preloads.push_back( &preload ); 
        }
        
        void
        Preloader::remLib( const Preloader& preload )
        {  
            Holder::iterator i = s_preloads.begin();
            while( i != s_preloads.end() )
            {
                if( *i == &preload )
                {
                    i = s_preloads.erase( i );
                }
                else
                    ++i;
            }   
        }

        Preloader::Holder&
        Preloader::preloads()
        { return s_preloads; }

        Preloader::Holder Preloader::s_preloads;
    }
}
