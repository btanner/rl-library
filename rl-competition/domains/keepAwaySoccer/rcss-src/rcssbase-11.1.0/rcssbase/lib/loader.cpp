// -*-c++-*-

/***************************************************************************
                                loader.cpp 
                             -------------------
                             dlopen a library
    begin                : 2002-10-10
    copyright            : (C) 2002-2005 by The RoboCup Soccer Simulator 
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

#include "loader.hpp"
#include <stdio.h>

#include <boost/filesystem/convenience.hpp>

namespace rcss
{
    namespace lib 
    {
		const char Loader::S_OK_ERR_STR[] = "no error";
		const char Loader::S_NOT_FOUND_ERR_STR[] = "module not found";
		const char Loader::S_INIT_ERR_STR[] = "module could not initialise";

        Loader::CacheMap Loader::s_cached_libs;
        Loader::DepMap Loader::s_deps;
		std::vector< boost::filesystem::path > Loader::s_path;

		std::vector< boost::filesystem::path > Loader::s_available;
		bool Loader::s_available_valid = false;
        
        void
        Loader::addPath( const boost::filesystem::path& path )
        {
			s_available_valid = false;	
			s_path.push_back( path );
        }

        void
        Loader::addPath( const std::vector< boost::filesystem::path >& path )
		{
			s_available_valid = false;
			s_path.insert( s_path.end(), path.begin(), path.end() );
		}

        void
        Loader::setPath( const boost::filesystem::path& path )
		{
			s_available_valid = false;
			s_path.clear();
			s_path.push_back( path );
        } 

        void
        Loader::setPath( const std::vector< boost::filesystem::path >& path )
		{
			s_available_valid = false;
			s_path = path;
		}

        void
        Loader::clearPath()
		{
			s_available_valid = false;
			s_path.clear();
		}

        const std::vector< boost::filesystem::path >&
        Loader::getPath()
        { 
			return s_path;
        }

		const std::vector< boost::filesystem::path >&
		Loader::listAvailableModules( ForceRecalc force )
		{
			if( s_available_valid && force == TRY_CACHE )
			{
				return s_available;
			}	    
			s_available.clear();
            std::list< LoaderStaticImpl::Factory::Index > sloaders = LoaderStaticImpl::factory().list();

            for( std::list< LoaderStaticImpl::Factory::Index >::iterator iter = sloaders.begin();
                 iter != sloaders.end(); ++iter )
            {
                LoaderStaticImpl::Factory::Creator creator;
                if( LoaderStaticImpl::factory().getCreator( creator, *iter ) )
                {
					LoaderStaticImpl::Ptr impl = creator();
					std::vector< boost::filesystem::path > curr = impl->listAvailableModules();
					s_available.insert( s_available.end(), curr.begin(), curr.end() );
				}
            }
			s_available_valid = true;
			return s_available;
		}
        
        Loader::Impl
        Loader::loadFromCache( const std::string& lib )
        {
            CacheMap::iterator i = s_cached_libs.find( lib );
	    if( i != s_cached_libs.end() )
		{
		    if( i->second.expired() )
			{
			    s_cached_libs.erase( i );
			    return Impl();
			}
		    else
			{
			    return i->second.lock();
			}
		}
            return Impl();
        }
       
        size_t
        Loader::libsLoaded()
        {  return s_cached_libs.size(); }

        std::string
        Loader::strip( const boost::filesystem::path& filename )
        { return stripExt( stripDirName( filename ) ); }
        
        std::string
        Loader::stripExt( const boost::filesystem::path& filename )
        {
			std::string rval = boost::filesystem::basename( filename );
			return rval;
		}


        boost::filesystem::path
        Loader::stripDirName( const boost::filesystem::path& filename )
        {
			std::string rval = filename.leaf();
			return rval;
        }        
        
        Loader::Loader()
	    : m_error( LIB_OK )
        {}

        Loader::Loader( WeakLoader& loader )
        {
            if( !loader.m_impl.expired() )
                m_impl = Impl( loader.m_impl );
        }

        bool
        Loader::open( const boost::filesystem::path& libname,
		      AutoExt auto_ext )
        { 
            if( libname.empty() )
                return false;
            
             // try loading from the cache
            m_impl = loadFromCache( strip( libname ) );
            if( m_impl )
                return true;
            
			std::list< LoaderImpl::Factory::Index > loaders = LoaderImpl::factory().list();
			for( std::list< LoaderImpl::Factory::Index >::iterator iter = loaders.begin();
				 iter != loaders.end(); ++iter )
			{
				LoaderImpl::Factory::Creator creator;
				if( LoaderImpl::factory().getCreator( creator, *iter ) )
				{
					m_impl = creator( libname, 
									  (LoaderImpl::AutoExt)auto_ext,
									  s_path );
					if( m_impl->valid() )
					{
						addToCache( libname, m_impl );
						addDep( m_impl );
						// the library we just loaded may make a new
						// loader available, so we can no longer reley
						// on the currently cached value (if there is
						// one) of available modules.
						s_available_valid = false;
						return true;
					}
					else
					{
						m_error = static_cast< Error >( m_impl->error() );
						if( m_error == SYSTEM_ERROR )
							m_system_err_str = m_impl->errorStr();
						m_impl.reset();
					}
				}
			}
			return false;
        }
        
        bool
        Loader::isOpen() const
        { return m_impl.get() != NULL; }

        void
        Loader::close()
        { m_impl.reset(); }

        void
        Loader::addToCache( const boost::filesystem::path& lib_name,
							const boost::shared_ptr< LoaderImpl >& lib )
        {
            s_cached_libs[ strip( lib_name ) ] = lib; 
        }
        
        void
        Loader::addDep( const Loader::Impl& lib )
        {
			if( s_deps.empty() )
			{
				s_deps.push_back( std::make_pair( Impl(), lib ) );
			}
			else
			{
				Impl dep;
				do
				{
					dep = s_deps.back().second.lock();
					if( !dep ) s_deps.pop_back();
				} while( !dep && !s_deps.empty() );
				
				if( s_deps.empty() )
					s_deps.push_back( std::make_pair( Impl(), lib ) );
				else
					s_deps.push_back( std::make_pair( dep,
													  WeakImpl( lib ) ) );
			}   
        }

        boost::filesystem::path
        Loader::name() const
        { return ( m_impl ? m_impl->name(): boost::filesystem::path() ); }

        std::string
        Loader::strippedName() const
        { return ( m_impl ? m_impl->strippedName(): std::string() ); }

		Loader::Error
		Loader::error() const
		{
			return m_error;
		}
		
        const char*
		Loader::errorStr() const
		{
			switch ( error() )
			{
				case LIB_OK:
					return S_OK_ERR_STR;
				case NOT_FOUND:
					return S_NOT_FOUND_ERR_STR;
				case INIT_ERROR:
					return S_INIT_ERR_STR;
				case SYSTEM_ERROR:
					return m_system_err_str.c_str();
			}
			return "unknown error";
		}
    }
}
