// -*-c++-*-

/***************************************************************************
                          bodysensor.cc  -  A class for storing the data from
			                    the players body sensor
                             -------------------
    begin                : 25-NOV-2001
    copyright            : (C) 2001 by The RoboCup Soccer Server
                           Maintainance Group.
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

#include "bodysensor.h"
#include "player.h"
#include "field.h"

const std::string QUALITY_NAMES[] = { "low", "high", "null" };
const std::string WIDTH_NAMES[] = { "narrow", "normal", "wide", "null" };

std::ostream &
operator<<( std::ostream & o, const BodySensor::quality_t quality )
{
  return o << QUALITY_NAMES [ quality ];
}

std::ostream&
operator<<( std::ostream & o, const BodySensor::width_t width )
{
  return o << WIDTH_NAMES [ width ];
}

BodySensor_v1::data_t::data_t( const Player & player )
		: M_time( player.stadium().time() ),
		  M_quality( player.highquality()
                 ? BodySensor::Q_HIGH
                 : BodySensor::Q_LOW ),
			M_width( player.visibleAngle() == player.defangle/2
               ? BodySensor::W_NARROW
               : ( player.visibleAngle() == player.defangle*2
                   ? BodySensor::W_WIDE
                   : BodySensor::W_NORMAL ) ),
			M_stamina( player.stamina() ),
			M_effort( player.effort() ),
			M_vel_mag( Quantize ( player.vel().r(), 0.01 ) ),
			M_count_kick( player.kickCount() ),
			M_count_dash( player.dashCount() ),
			M_count_turn( player.turnCount() ),
			M_count_say( player.sayCount() )
{
}


BodySensor_v5::data_t::data_t( const Player & player )
		: BodySensor_v1::data_t( player ),
			M_head_angle( Rad2IDeg ( player.angleNeckCommitted() ) ),
      M_count_turn_neck( player.turnNeckCount() )
{
}


BodySensor_v6::data_t::data_t( const Player & player )
		: BodySensor_v5::data_t( player ),
			M_vel_head ( Rad2IDeg( normalize_angle ( player.vel().th()
                                               - player.angleBodyCommitted()
                                               - player.angleNeckCommitted() ) ) )
{
}


BodySensor_v7::data_t::data_t( const Player & player )
		: BodySensor_v6::data_t( player ),
			M_count_catch( player.catchCount() ),
      M_count_move( player.moveCount() ),
      M_count_change_view( player.changeViewCount() )
{
}

BodySensor_v8::data_t::data_t( const Player & player )
  : BodySensor_v7::data_t( player ),
    M_arm_state( player.getArm().getState( rcss::geom::Vector2D( player.pos().x,
                                                                 player.pos().y ),
                                           player.angleBodyCommitted()
                                           + player.angleNeckCommitted() ) ),
                   M_tackle_cycles_remaining( player.getTackleCycles() ),
                   M_tackle_count( player.getTackleCount() ),
                   M_focus_target( player.getFocusTarget() ),
                   M_focus_count( player.getFocusCount() )
{
}
