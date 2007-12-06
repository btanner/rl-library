// -*-c++-*-

/***************************************************************************
                                loaderimpl.cpp
                             -------------------
                base class for implementing a dynamically library loader
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


#include "loaderimpl.hpp"
#include "loader.hpp"
#include "preloadloader.hpp"
#include "systemloader.hpp"


namespace rcss
{
    namespace lib
    {
        class FactoryHolder
        {
        public:
            FactoryHolder()
                : m_fact()
            {
                // Since this is going to be a library, we cannot use
                // autoregistration, thus we must register the basic
                // loader classes this way.
	        // If you change something here, make sure you do
	        // likewise in the FactoryHolder destructor
                m_fact.reg( &PreloadLoader::create, "PreloadLoader" );
                m_fact.reg( &SystemLoader::create, "SystemLoader" );
            }

            ~FactoryHolder()
            {
                m_fact.dereg( "SystemLoader" );
                m_fact.dereg( "PreloadLoader" );
            }

            LoaderImpl::Factory&
            factory()
            { return m_fact; }
        private:
            LoaderImpl::Factory m_fact;
        };



        LoaderImpl::Factory&
        LoaderImpl::factory()
        {
            static FactoryHolder fh;
            return fh.factory();
        }

		LoaderImpl::LoaderImpl( const boost::filesystem::path& lib, 
								AutoExt auto_ext, 
								const std::vector< boost::filesystem::path >& path )
            : m_name( lib ),
			  m_stripped_name( Loader::strip( lib ) ),
			  m_auto_ext( auto_ext ),
              m_error( LIB_OK ),
			  m_path( path )
        {}
        
        LoaderImpl::~LoaderImpl()
        {}
        
        void
        LoaderImpl::load()
        {
            doLoad();
            if( valid() )
                initialize();
        }
        
        void
        LoaderImpl::close()
        {
            if( valid() )
            {
                Loader::CacheMap::iterator i 
		    = Loader::s_cached_libs.find( m_stripped_name );
                if( i != Loader::s_cached_libs.end() )
                {
		    Loader::s_cached_libs.erase( i );
		}
                finalize();
                doClose();
                if( !Loader::s_deps.empty() )
                    Loader::s_deps.pop_back();
            }
        }
        
        void
        LoaderImpl::initialize()
        {
            Initialize symb = doGetInitialize();
            if( !(void*)symb || !symb() )
			{
				doClose();
				error( INIT_ERROR );
			}
        }
        
        void
        LoaderImpl::finalize()
        {
            Finalize symb = doGetFinalize();
            if( (void*)symb )
                symb();
        }        


        LoaderImpl::Error
        LoaderImpl::error() const
        { return m_error; }

        bool
        LoaderImpl::valid() const
        { return m_error == LIB_OK; }

        void
        LoaderImpl::error( Error e )
        { m_error = e; }

        void
        LoaderImpl::error( const std::string& err_str )
        {
			m_error = SYSTEM_ERROR;
			m_system_err_str = err_str;
		}


        class FactoryStaticHolder
        {
        public:
            FactoryStaticHolder()
                : m_fact()
            {
                // Since this is going to be a library, we cannot use
                // autoregistration, thus we must register the basic
                // loader classes this way.
	        // If you change something here, make sure you do
	        // likewise in the FactoryHolder destructor
                m_fact.reg( &PreloadLoaderStatic::create, "PreloadLoader" );
                m_fact.reg( &SystemLoaderStatic::create, "SystemLoader" );
            }

            ~FactoryStaticHolder()
            {
                m_fact.dereg( "SystemLoader" );
                m_fact.dereg( "PreloadLoader" );
            }

            LoaderStaticImpl::Factory&
            factory()
            { return m_fact; }
        private:
            LoaderStaticImpl::Factory m_fact;
        };



        LoaderStaticImpl::Factory&
        LoaderStaticImpl::factory()
        {
            static FactoryStaticHolder fh;
            return fh.factory();
        }

        LoaderStaticImpl::LoaderStaticImpl()
        {}
        
        LoaderStaticImpl::~LoaderStaticImpl()
        {}
    }
}
