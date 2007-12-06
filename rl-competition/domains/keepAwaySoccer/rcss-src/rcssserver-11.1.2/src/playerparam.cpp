/* -*- Mode: C++ -*- */

/*
 *Copyright:

    Copyright (C) 1996-2000 Electrotechnical Laboratory.
    	Itsuki Noda, Yasuo Kuniyoshi and Hitoshi Matsubara.
    Copyright (C) 2000, 2001 RoboCup Soccer Server Maintainance Group.
    	Patrick Riley, Tom Howard, Daniel Polani, Itsuki Noda,
	Mikhail Prokopenko, Jan Wendler

    This file is a part of SoccerServer.

    This code is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 *EndCopyright:
 */

#ifdef HAVE_CONFIG_H
#include "config.h"
#endif

#include "playerparam.h"

#include <rcssbase/conf/parser.hpp>
#include <rcssbase/conf/builder.hpp>

#include <boost/filesystem/path.hpp>
#include <string>
#include <iostream>
#include <cerrno>


#ifdef HAVE_SYS_PARAM_H
#include <sys/param.h> /* needed for htonl, htons, ... */
#endif
#ifdef HAVE_WINSOCK2_H
#include <Winsock2.h> /* needed for htonl, htons, ... */
#endif

#ifdef HAVE_NETINET_IN_H
#include <netinet/in.h>
#endif

inline
Int32
roundint( const double & value )
{
	return Int32( value + 0.5 );
}

PlayerParam::Printer::Printer( std::ostream& out, unsigned int version )
    : M_out( out ),
      M_version( version )
{}

void
PlayerParam::Printer::operator()( const std::pair< const std::string, unsigned int > item )
{
            if( item.second <= M_version )
            {
		int ivalue;
		if( PlayerParam::instance().getInt( item.first, ivalue ) )
		{
                    M_out << "(" << item.first << " " << ivalue << ")";
                    return;
                }

		bool bvalue;
		if( PlayerParam::instance().getBool( item.first, bvalue ) )
		{
		    M_out << "(" << item.first << " " << bvalue << ")";
                    return;
                }

		double dvalue;
		if( PlayerParam::instance().getDoub( item.first, dvalue ) )
		{
                    M_out << "(" << item.first << " " << dvalue << ")";
                    return;
                }

                std::string svalue;
		if( PlayerParam::instance().getStr( item.first, svalue ) )
		{
                    M_out << "(" << item.first << " " << svalue << ")";
                    return;
                }
            }
}


#if defined(_WIN32) || defined(__WIN32__) || defined (WIN32) || defined (__CYGWIN__)
#  ifndef WIN32
#    define WIN32
#  endif
#endif

#ifdef WIN32
const char PlayerParam::OLD_PLAYER_CONF[] = "~\\.rcssserver-player.conf";
const char PlayerParam::PLAYER_CONF[] = "~\\.rcssserver\\player.conf";
#else
const char PlayerParam::OLD_PLAYER_CONF[] = "~/.rcssserver-player.conf";
const char PlayerParam::PLAYER_CONF[] = "~/.rcssserver/player.conf";
#endif

const int PlayerParam::DEFAULT_PLAYER_TYPES = 7;
const int PlayerParam::DEFAULT_SUBS_MAX = 3;
const int PlayerParam::DEFAULT_PT_MAX = 3;

const double PlayerParam::DEFAULT_PLAYER_SPEED_MAX_DELTA_MIN = 0.0;
const double PlayerParam::DEFAULT_PLAYER_SPEED_MAX_DELTA_MAX = 0.0;
const double PlayerParam::DEFAULT_STAMINA_INC_MAX_DELTA_FACTOR = 0.0;

const double PlayerParam::DEFAULT_PLAYER_DECAY_DELTA_MIN = 0.0;
const double PlayerParam::DEFAULT_PLAYER_DECAY_DELTA_MAX = 0.2;
const double PlayerParam::DEFAULT_INERTIA_MOMENT_DELTA_FACTOR = 25.0;

const double PlayerParam::DEFAULT_DASH_POWER_RATE_DELTA_MIN = 0.0;
const double PlayerParam::DEFAULT_DASH_POWER_RATE_DELTA_MAX = 0.0;
const double PlayerParam::DEFAULT_PLAYER_SIZE_DELTA_FACTOR = -100.0;

const double PlayerParam::DEFAULT_KICKABLE_MARGIN_DELTA_MIN = 0.0;
const double PlayerParam::DEFAULT_KICKABLE_MARGIN_DELTA_MAX = 0.2;
const double PlayerParam::DEFAULT_KICK_RAND_DELTA_FACTOR = 0.5;

const double PlayerParam::DEFAULT_EXTRA_STAMINA_DELTA_MIN = 0.0;
const double PlayerParam::DEFAULT_EXTRA_STAMINA_DELTA_MAX = 100.0;
const double PlayerParam::DEFAULT_EFFORT_MAX_DELTA_FACTOR = -0.002;
const double PlayerParam::DEFAULT_EFFORT_MIN_DELTA_FACTOR = -0.002;

const int    PlayerParam::DEFAULT_RANDOM_SEED = -1; //negative means generate a new seed

const double PlayerParam::DEFAULT_NEW_DASH_POWER_RATE_DELTA_MIN = 0.0;
const double PlayerParam::DEFAULT_NEW_DASH_POWER_RATE_DELTA_MAX = 0.002;
const double PlayerParam::DEFAULT_NEW_STAMINA_INC_MAX_DELTA_FACTOR = -10000.0;




PlayerParam&
PlayerParam::instance( rcss::conf::Builder* parent )
{
    static bool parent_set = false;
    if( parent != NULL || parent_set )
    {
	static PlayerParam rval( parent );
	parent_set = true;
	return rval;
    }
    // hack to allow link testing to call instance without crashing
    // do not used the return value in these situations
    PlayerParam* rval = NULL;
    return *rval;
}

PlayerParam&
PlayerParam::instance()
{ return PlayerParam::instance( NULL ); }

bool
PlayerParam::init( rcss::conf::Builder* parent )
{
    instance( parent );
#ifndef WIN32
    if( system( ( "ls " + tildeExpand( PlayerParam::OLD_PLAYER_CONF ) + " > /dev/null 2>&1" ).c_str() ) == 0 )
    {
        if( system( ( "ls " + tildeExpand( PlayerParam::PLAYER_CONF ) + " > /dev/null 2>&1" ).c_str() ) != 0 )
        {
            if( system( "which awk > /dev/null 2>&1" ) == 0 )
            {
                std::cout << "Trying to convert old configuration file '"
                          << PlayerParam::OLD_PLAYER_CONF
                          << "'\n";

        char filename[] = "/tmp/rcssplayer-oldconf-XXXXXX";
        int fd = mkstemp( filename );
        if( fd != -1 )
        {
            close( fd );
            std::string command = "awk '/^[ \\t]*$/ {} ";
            command += "/^[^#]+[:]/ { gsub(/:/, \"=\" ); $1 = \"player::\" $1; } ";
            command += "/^[ \\t]*[^#:=]+$/ { $1 = \"player::\" $1 \" = true\"; }";
            command += "{ print; }' ";
            command +=  tildeExpand( PlayerParam::OLD_PLAYER_CONF );
            command += " > ";
            command += filename;
            if( system( command.c_str() ) == 0 )
            {
                std::cout << "Conversion successful\n";
                instance().m_builder->parser()->parse( filename );
            }
            else
            {
                std::cout << "Conversion failed\n";
            }
        }
        else
        {
            std::cout << "Conversion failed\n";
        }
    }
        }
    }
#endif // not win32
    if( !instance().m_builder->parser() )
    {
		std::cerr << __FILE__ << ": " << __LINE__
				  << ": internal error: player param could not find configuration parser\n";
		std::cerr << "Please submit a full bug report to sserver-bugs@lists.sf.net\n";
		return false;
    }

    boost::filesystem::path conf_path( tildeExpand( PlayerParam::PLAYER_CONF ),
                                       boost::filesystem::portable_posix_name );
    if( !instance().m_builder->parser()->parseCreateConf( conf_path,
                                                          "player" ) )
    {
        std::cerr << "could not parse configuration file '"
                  << PlayerParam::PLAYER_CONF
                  << "'\n";
        return false;
    }

    return true;
}

PlayerParam::PlayerParam( rcss::conf::Builder* parent )
    : m_builder( new rcss::conf::Builder( parent, "player" ) )
{
  setDefaults();
  addParams();
}

PlayerParam::~PlayerParam()
{
}

void
PlayerParam::addParams()
{
  addParam( "player_types", player_types, "", 7 );
  addParam( "subs_max", subs_max, "", 7 );
  addParam( "pt_max", pt_max, "", 7 );
  addParam( "player_speed_max_delta_min", player_speed_max_delta_min, "", 7 );
  addParam( "player_speed_max_delta_max", player_speed_max_delta_max, "", 7 );
  addParam( "stamina_inc_max_delta_factor", stamina_inc_max_delta_factor, "", 7 );
  addParam( "player_decay_delta_min", player_decay_delta_min, "", 7 );
  addParam( "player_decay_delta_max", player_decay_delta_max, "", 7 );
  addParam( "inertia_moment_delta_factor", inertia_moment_delta_factor, "", 7 );
  addParam( "dash_power_rate_delta_min", dash_power_rate_delta_min, "", 7 );
  addParam( "dash_power_rate_delta_max", dash_power_rate_delta_max, "", 7 );
  addParam( "player_size_delta_factor", player_size_delta_factor, "", 7 );
  addParam( "kickable_margin_delta_min", kickable_margin_delta_min, "", 7 );
  addParam( "kickable_margin_delta_max", kickable_margin_delta_max, "", 7 );
  addParam( "kick_rand_delta_factor", kick_rand_delta_factor, "", 7 );
  addParam( "extra_stamina_delta_min", extra_stamina_delta_min, "", 7 );
  addParam( "extra_stamina_delta_max", extra_stamina_delta_max, "", 7 );
  addParam( "effort_max_delta_factor", effort_max_delta_factor, "", 7 );
  addParam( "effort_min_delta_factor", effort_min_delta_factor, "", 7 );
  addParam( "random_seed", random_seed, "", 8 );
  addParam( "new_dash_power_rate_delta_min", new_dash_power_rate_delta_min, "", 8 );
  addParam( "new_dash_power_rate_delta_max", new_dash_power_rate_delta_max, "", 8 );
  addParam( "new_stamina_inc_max_delta_factor", new_stamina_inc_max_delta_factor, "", 8 );
}

    template< typename P >
    void
    PlayerParam::addParam(  const std::string& name,
               P& param,
               const std::string& desc,
               int version )
    {
        m_builder->addParam( name, param, desc );
        m_ver_map[ name ] = version;
    }

    template< typename S, typename G >
    void
    PlayerParam::addParam(  const std::string& name,
               const S& setter,
               const G& getter,
               const std::string& desc,
               int version )
    {
        m_builder->addParam( name, setter, getter, desc );
        m_ver_map[ name ] = version;
    }


void
PlayerParam::setDefaults()
{
  player_types = PlayerParam::DEFAULT_PLAYER_TYPES;
  subs_max = PlayerParam::DEFAULT_SUBS_MAX;
  pt_max = PlayerParam::DEFAULT_PT_MAX;

  player_speed_max_delta_min = PlayerParam::DEFAULT_PLAYER_SPEED_MAX_DELTA_MIN;
  player_speed_max_delta_max = PlayerParam::DEFAULT_PLAYER_SPEED_MAX_DELTA_MAX;
  stamina_inc_max_delta_factor = PlayerParam::DEFAULT_STAMINA_INC_MAX_DELTA_FACTOR;

  player_decay_delta_min = PlayerParam::DEFAULT_PLAYER_DECAY_DELTA_MIN;
  player_decay_delta_max = PlayerParam::DEFAULT_PLAYER_DECAY_DELTA_MAX;
  inertia_moment_delta_factor = PlayerParam::DEFAULT_INERTIA_MOMENT_DELTA_FACTOR;

  dash_power_rate_delta_min = PlayerParam::DEFAULT_DASH_POWER_RATE_DELTA_MIN;
  dash_power_rate_delta_max = PlayerParam::DEFAULT_DASH_POWER_RATE_DELTA_MAX;
  player_size_delta_factor = PlayerParam::DEFAULT_PLAYER_SIZE_DELTA_FACTOR;

  kickable_margin_delta_min = PlayerParam::DEFAULT_KICKABLE_MARGIN_DELTA_MIN;
  kickable_margin_delta_max = PlayerParam::DEFAULT_KICKABLE_MARGIN_DELTA_MAX;
  kick_rand_delta_factor = PlayerParam::DEFAULT_KICK_RAND_DELTA_FACTOR;

  extra_stamina_delta_min = PlayerParam::DEFAULT_EXTRA_STAMINA_DELTA_MIN;
  extra_stamina_delta_max = PlayerParam::DEFAULT_EXTRA_STAMINA_DELTA_MAX;
  effort_max_delta_factor = PlayerParam::DEFAULT_EFFORT_MAX_DELTA_FACTOR;
  effort_min_delta_factor = PlayerParam::DEFAULT_EFFORT_MIN_DELTA_FACTOR;

  random_seed = PlayerParam::DEFAULT_RANDOM_SEED;

  new_dash_power_rate_delta_min = PlayerParam::DEFAULT_NEW_DASH_POWER_RATE_DELTA_MIN;
  new_dash_power_rate_delta_max = PlayerParam::DEFAULT_NEW_DASH_POWER_RATE_DELTA_MAX;
  new_stamina_inc_max_delta_factor = PlayerParam::DEFAULT_NEW_STAMINA_INC_MAX_DELTA_FACTOR;
}

player_params_t PlayerParam::convertToStruct ()
{
  player_params_t tmp;

  tmp.player_types = htons( (Int16) player_types );
  tmp.subs_max = htons( (Int16) subs_max );
  tmp.pt_max = htons( (Int16) pt_max );

  tmp.player_speed_max_delta_min = htonl( (Int32)roundint((SHOWINFO_SCALE2 * player_speed_max_delta_min) ));
  tmp.player_speed_max_delta_max = htonl( (Int32)roundint((SHOWINFO_SCALE2 * player_speed_max_delta_max) ));
  tmp.stamina_inc_max_delta_factor = htonl( (Int32)roundint((SHOWINFO_SCALE2 * stamina_inc_max_delta_factor) ));

  tmp.player_decay_delta_min = htonl( (Int32)roundint((SHOWINFO_SCALE2 * player_decay_delta_min) ));
  tmp.player_decay_delta_max = htonl( (Int32)roundint((SHOWINFO_SCALE2 * player_decay_delta_max) ));
  tmp.inertia_moment_delta_factor = htonl( (Int32)roundint((SHOWINFO_SCALE2 * inertia_moment_delta_factor) ));

  tmp.dash_power_rate_delta_min = htonl( (Int32)roundint((SHOWINFO_SCALE2 * dash_power_rate_delta_min) ));
  tmp.dash_power_rate_delta_max = htonl( (Int32)roundint((SHOWINFO_SCALE2 * dash_power_rate_delta_max) ));
  tmp.player_size_delta_factor = htonl( (Int32)roundint((SHOWINFO_SCALE2 * player_size_delta_factor) ));

  tmp.kickable_margin_delta_min = htonl( (Int32)roundint((SHOWINFO_SCALE2 * kickable_margin_delta_min) ));
  tmp.kickable_margin_delta_max = htonl( (Int32)roundint((SHOWINFO_SCALE2 * kickable_margin_delta_max) ));
  tmp.kick_rand_delta_factor = htonl( (Int32)roundint((SHOWINFO_SCALE2 * kick_rand_delta_factor) ));

  tmp.extra_stamina_delta_min = htonl( (Int32)roundint((SHOWINFO_SCALE2 * extra_stamina_delta_min) ));
  tmp.extra_stamina_delta_max = htonl( (Int32)roundint((SHOWINFO_SCALE2 * extra_stamina_delta_max) ));
  tmp.effort_max_delta_factor = htonl( (Int32)roundint((SHOWINFO_SCALE2 * effort_max_delta_factor) ));
  tmp.effort_min_delta_factor = htonl( (Int32)roundint((SHOWINFO_SCALE2 * effort_min_delta_factor) ));
  tmp.random_seed = htonl( (Int32)random_seed );

  tmp.new_dash_power_rate_delta_min = htonl( (Int32)roundint((SHOWINFO_SCALE2 * new_dash_power_rate_delta_min) ));
  tmp.new_dash_power_rate_delta_max = htonl( (Int32)roundint((SHOWINFO_SCALE2 * new_dash_power_rate_delta_max) ));
  tmp.new_stamina_inc_max_delta_factor = htonl( (Int32)roundint((SHOWINFO_SCALE2 * new_stamina_inc_max_delta_factor) ));

  return tmp;
}

bool
PlayerParam::getInt( const std::string& param, int& value ) const
{
	return m_builder->get( param, value );
}

bool
PlayerParam::getBool( const std::string& param, bool& value ) const
{
	return m_builder->get( param, value );
}

bool
PlayerParam::getDoub( const std::string& param, double& value ) const
{
	return m_builder->get( param, value );
}

bool
PlayerParam::getStr( const std::string& param, std::string& value ) const
{
	return m_builder->get( param, value );
}

// std::ostream& toStr ( std::ostream& o, const PlayerParamSensor_v7::data_t& data )
// {
//     return o << "(player_param "
// 	     << data.M_player_types << " "
// 	     << data.M_subs_max << " "
// 	     << data.M_pt_max << " "

// 	     << data.M_player_speed_max_delta_min << " "
// 	     << data.M_player_speed_max_delta_max << " "
// 	     << data.M_stamina_inc_max_delta_factor << " "

// 	     << data.M_player_decay_delta_min << " "
// 	     << data.M_player_decay_delta_max << " "
// 	     << data.M_inertia_moment_delta_factor << " "

// 	     << data.M_dash_power_rate_delta_min << " "
// 	     << data.M_dash_power_rate_delta_max << " "
// 	     << data.M_player_size_delta_factor << " "

// 	     << data.M_kickable_margin_delta_min << " "
// 	     << data.M_kickable_margin_delta_max << " "
// 	     << data.M_kick_rand_delta_factor << " "

// 	     << data.M_extra_stamina_delta_min << " "
// 	     << data.M_extra_stamina_delta_max << " "
// 	     << data.M_effort_max_delta_factor << " "
// 	     << data.M_effort_min_delta_factor
// 	     << ")";
// }

// std::ostream& toStr ( std::ostream& o, const PlayerParamSensor_v8::data_t& data )
// {
//   o << "(player_param ";
//   std::for_each( data.m_sp.verMap().begin(),
//                  data.m_sp.verMap().end(),
//                  PlayerParam::Printer( o, 8 ) );
// //   for_each( data.int_map.begin(), data.int_map.end(), PlayerParam::Printer( o, 8 ) );
// //   for_each( data.str_map.begin(), data.str_map.end(), PlayerParam::QuotedPrinter( o, 8 ) );
// //   for_each( data.bool_map.begin(), data.bool_map.end(), PlayerParam::Printer( o, 8 ) );
// //   for_each( data.onoff_map.begin(), data.onoff_map.end(), PlayerParam::Printer( o, 8 ) );
// //   for_each( data.double_map.begin(), data.double_map.end(), PlayerParam::Printer( o, 8 ) );
//   o << ")";

//   return o;

// //      return o << "(player_param "
// //  						 << "(player_types " << data.M_player_types
// //  						 << ") (subs_max " << data.M_subs_max
// //  						 << ") (pt_max " << data.M_pt_max
// //  						 << ") (player_speed_max_delta_min " << data.M_player_speed_max_delta_min
// //  						 << ") (player_speed_max_delta_max " << data.M_player_speed_max_delta_max
// //  						 << ") (stamina_inc_max_delta_factor " << data.M_stamina_inc_max_delta_factor
// //  						 << ") (player_decay_delta_min " << data.M_player_decay_delta_min
// //  						 << ") (player_decay_delta_max " << data.M_player_decay_delta_max
// //  						 << ") (inertia_moment_delta_factor " << data.M_inertia_moment_delta_factor
// //  						 << ") (dash_power_rate_delta_min " << data.M_dash_power_rate_delta_min
// //  						 << ") (dash_power_rate_delta_max " << data.M_dash_power_rate_delta_max
// //  						 << ") (player_size_delta_factor " << data.M_player_size_delta_factor
// //  						 << ") (kickable_margin_delta_min " << data.M_kickable_margin_delta_min
// //  						 << ") (kickable_margin_delta_max " << data.M_kickable_margin_delta_max
// //  						 << ") (kick_rand_delta_factor " << data.M_kick_rand_delta_factor
// //  						 << ") (extra_stamina_delta_min " << data.M_extra_stamina_delta_min
// //  						 << ") (extra_stamina_delta_max " << data.M_extra_stamina_delta_max
// //  						 << ") (effort_max_delta_factor " << data.M_effort_max_delta_factor
// //  						 << ") (effort_min_delta_factor " << data.M_effort_min_delta_factor
// //  						 << ") (random_seed " << data.M_random_seed
// //               << ") (new_dash_power_rate_delta_min " << data.M_new_dash_power_rate_delta_min
// //               << ") (new_dash_power_rate_delta_max " << data.M_new_dash_power_rate_delta_max
// //               << ") (new_stamina_inc_max_delta_factor " << data.M_new_stamina_inc_max_delta_factor
// //               << "))";
// }
