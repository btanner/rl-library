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

#ifndef _PLAYER_PARAM_H_
#define _PLAYER_PARAM_H_

#include "rcssserverconfig.hpp"

#include "utility.h"
#include "types.h"

#include <rcssbase/conf/builder.hpp>
#include <boost/shared_ptr.hpp>

#include <map>

// namespace rcss
// {
//     namespace conf
//     {
// 		class GenericBuilder;
// 		class Builder;
//     }
// }

class PlayerParam 
{
protected:

	RCSSSERVER_API
    PlayerParam( rcss::conf::Builder* parent );
private:
    PlayerParam( const PlayerParam& ); // not used

public:
    class Printer
    {
    private:
        std::ostream& M_out;
        unsigned int M_version;
    public:
        Printer( std::ostream& out, unsigned int version );
        
        void
        operator()( const std::pair< const std::string, unsigned int > item );
    };

public:

	RCSSSERVER_API
    virtual ~PlayerParam ();
    
	RCSSSERVER_API
    static
    PlayerParam&
    instance();

	RCSSSERVER_API
    static
    PlayerParam&
    instance( rcss::conf::Builder* parent );
    
	RCSSSERVER_API
    static
    bool
    init( rcss::conf::Builder* parent );
  
protected:
//   void
//   createMaps();

    void
    addParams();
public:
    typedef std::map< std::string, unsigned int > VerMap;
private:
    boost::shared_ptr< rcss::conf::Builder > m_builder;
    VerMap m_ver_map;

public:

    const VerMap&
    verMap() const
    { return m_ver_map; }

protected:

    template< typename P >
    void
    addParam(  const std::string& name, 
               P& param,
               const std::string& desc,
               int version );

    template< typename S, typename G >
    void
    addParam(  const std::string& name, 
               const S& setter,
               const G& getter,
               const std::string& desc,
               int version );

    void
    setDefaults();

public:

    player_params_t convertToStruct ();
    
    
    int playerTypes () const { return player_types; }
    int subsMax () const { return subs_max; }
    int ptMax () const { return pt_max; }
    
    double playerSpeedMaxDeltaMin () const { return player_speed_max_delta_min; }
    
    double playerSpeedMaxDeltaMax () const { return player_speed_max_delta_max; }
    double staminaIncMaxDeltaFactor () const { return stamina_inc_max_delta_factor; }
    
    double playerDecayDeltaMin () const { return player_decay_delta_min; }
    double playerDecayDeltaMax () const { return player_decay_delta_max; }
    double inertiaMomentDeltaFactor () const { return inertia_moment_delta_factor; }
    
    double dashPowerRateDeltaMin () const { return dash_power_rate_delta_min; }
    double dashPowerRateDeltaMax () const { return dash_power_rate_delta_max; }
    double playerSizeDeltaFactor () const { return player_size_delta_factor; }
    
    double kickableMarginDeltaMin () const { return kickable_margin_delta_min; }
    double kickableMarginDeltaMax () const { return kickable_margin_delta_max; }
    double kickRandDeltaFactor () const { return kick_rand_delta_factor; }
    
    double extraStaminaDeltaMin () const { return extra_stamina_delta_min; }
    double extraStaminaDeltaMax () const { return extra_stamina_delta_max; }
    double effortMaxDeltaFactor () const { return effort_max_delta_factor; }
    double effortMinDeltaFactor () const { return effort_min_delta_factor; }
    
    int randomSeed () const { return random_seed; }
    
    double newDashPowerRateDeltaMin() const { return new_dash_power_rate_delta_min; }
    double newDashPowerRateDeltaMax() const { return new_dash_power_rate_delta_max; }
    double newStaminaIncMaxDeltaFactor() const { return new_stamina_inc_max_delta_factor; }
    
    bool
    getInt( const std::string& param, int& value ) const;

    bool
    getBool( const std::string& param, bool& value ) const;

    bool
    getDoub( const std::string& param, double& value ) const;

    bool
    getStr( const std::string& param, std::string& value ) const;


 protected:
// 		 virtual void getOptions(const int& argc, const char * const *argv );
// 		 virtual void writeConfig ( std::ostream& o );


 private:
		 static const char OLD_PLAYER_CONF[];
		 static const char PLAYER_CONF[];

		 static const int DEFAULT_PLAYER_TYPES;
		 static const int DEFAULT_SUBS_MAX;
		 static const int DEFAULT_PT_MAX;

		 static const double DEFAULT_PLAYER_SPEED_MAX_DELTA_MIN;
		 static const double DEFAULT_PLAYER_SPEED_MAX_DELTA_MAX;
		 static const double DEFAULT_STAMINA_INC_MAX_DELTA_FACTOR;

		 static const double DEFAULT_PLAYER_DECAY_DELTA_MIN;
		 static const double DEFAULT_PLAYER_DECAY_DELTA_MAX;
		 static const double DEFAULT_INERTIA_MOMENT_DELTA_FACTOR;

		 static const double DEFAULT_DASH_POWER_RATE_DELTA_MIN;
		 static const double DEFAULT_DASH_POWER_RATE_DELTA_MAX;
		 static const double DEFAULT_PLAYER_SIZE_DELTA_FACTOR;

		 static const double DEFAULT_KICKABLE_MARGIN_DELTA_MIN;
		 static const double DEFAULT_KICKABLE_MARGIN_DELTA_MAX;
		 static const double DEFAULT_KICK_RAND_DELTA_FACTOR;

		 static const double DEFAULT_EXTRA_STAMINA_DELTA_MIN;
		 static const double DEFAULT_EXTRA_STAMINA_DELTA_MAX;
		 static const double DEFAULT_EFFORT_MAX_DELTA_FACTOR;
		 static const double DEFAULT_EFFORT_MIN_DELTA_FACTOR;

		 static const int    DEFAULT_RANDOM_SEED;

		 static const double DEFAULT_NEW_DASH_POWER_RATE_DELTA_MIN;
		 static const double DEFAULT_NEW_DASH_POWER_RATE_DELTA_MAX;
		 static const double DEFAULT_NEW_STAMINA_INC_MAX_DELTA_FACTOR;

		 int player_types;
		 int subs_max;
		 int pt_max;

		 double player_speed_max_delta_min;
		 double player_speed_max_delta_max;
		 double stamina_inc_max_delta_factor;

		 double player_decay_delta_min;
		 double player_decay_delta_max;
		 double inertia_moment_delta_factor;

		 double dash_power_rate_delta_min;
		 double dash_power_rate_delta_max;
		 double player_size_delta_factor;

		 double kickable_margin_delta_min;
		 double kickable_margin_delta_max;
		 double kick_rand_delta_factor;

		 double extra_stamina_delta_min;
		 double extra_stamina_delta_max;
		 double effort_max_delta_factor;
		 double effort_min_delta_factor;

		 double new_dash_power_rate_delta_min;
		 double new_dash_power_rate_delta_max;
		 double new_stamina_inc_max_delta_factor;

		 int random_seed;
 };

//  class PlayerParamSensor
//  {
//  public:
//      virtual
//      ~PlayerParamSensor()
//        { }
//  };

// class PlayerParamSensor_v7
//     : public PlayerParamSensor
// {
// public:
//     struct data_t
//     {
//         int M_player_types;
//         int M_subs_max;
//         int M_pt_max;

//         double M_player_speed_max_delta_min;
//         double M_player_speed_max_delta_max;
//         double M_stamina_inc_max_delta_factor;

//         double M_player_decay_delta_min;
//         double M_player_decay_delta_max;
//         double M_inertia_moment_delta_factor;

//         double M_dash_power_rate_delta_min;
//         double M_dash_power_rate_delta_max;
//         double M_player_size_delta_factor;

//         double M_kickable_margin_delta_min;
//         double M_kickable_margin_delta_max;
//         double M_kick_rand_delta_factor;

//         double M_extra_stamina_delta_min;
//         double M_extra_stamina_delta_max;
//         double M_effort_max_delta_factor;
//         double M_effort_min_delta_factor;

//         data_t ( const PlayerParam& pp )
//             :	M_player_types ( pp.playerTypes () ),
//                 M_subs_max ( pp.subsMax () ),
//                 M_pt_max ( pp.ptMax () ),

//                 M_player_speed_max_delta_min ( pp.playerSpeedMaxDeltaMin () ),
//                 M_player_speed_max_delta_max ( pp.playerSpeedMaxDeltaMax () ),
//                 M_stamina_inc_max_delta_factor ( pp.staminaIncMaxDeltaFactor () ),

//                 M_player_decay_delta_min ( pp.playerDecayDeltaMin () ),
//                 M_player_decay_delta_max ( pp.playerDecayDeltaMax () ),
//                 M_inertia_moment_delta_factor ( pp.inertiaMomentDeltaFactor () ),

//                 M_dash_power_rate_delta_min ( pp.dashPowerRateDeltaMin () ),
//                 M_dash_power_rate_delta_max ( pp.dashPowerRateDeltaMax () ),
//                 M_player_size_delta_factor ( pp.playerSizeDeltaFactor () ),

//                 M_kickable_margin_delta_min ( pp.kickableMarginDeltaMin () ),
//                 M_kickable_margin_delta_max ( pp.kickableMarginDeltaMax () ),
//                 M_kick_rand_delta_factor ( pp.kickRandDeltaFactor () ),

//                 M_extra_stamina_delta_min ( pp.extraStaminaDeltaMin () ),
//                 M_extra_stamina_delta_max ( pp.extraStaminaDeltaMax () ),
//                 M_effort_max_delta_factor ( pp.effortMaxDeltaFactor () ),
//                 M_effort_min_delta_factor ( pp.effortMinDeltaFactor () )
//         {
//         }
//     };

//     virtual void send ( const PlayerParamSensor_v7::data_t& data ) = 0;
// };

// std::ostream& toStr ( std::ostream& o, const PlayerParamSensor_v7::data_t& data );

// class PlayerParamSensor_v8
// 		: public PlayerParamSensor_v7
// {
// public:
//   struct data_t
//   {
//       const PlayerParam& m_sp;

//     data_t( const PlayerParam& sp )
//         : m_sp( sp )
//     {}
//   };

//     virtual void send ( const PlayerParamSensor_v8::data_t& data ) = 0;
// };

// std::ostream& toStr ( std::ostream& o, const PlayerParamSensor_v8::data_t& data );

#endif


