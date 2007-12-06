// -*-c++-*-

/***************************************************************************
                          bodysensor.h  -  A class for storing the data from
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


#ifndef _BODYSENSOR_H_
#define _BODYSENSOR_H_

#include <string>


#include "arm.h"


class Player;

//==============================================================
//
// CLASS: BodySensor
//
// DESC: Iterface implemented by clients that have a body sensor
//
//==============================================================

class BodySensor
{
public:
  enum quality_t
  {
    Q_LOW,
    Q_HIGH,
    Q_NULL
  };

  enum width_t
  {
    W_NARROW,
    W_NORMAL,
    W_WIDE,
    W_NULL
  };

  static const std::string QUALITY_NAMES[];
  static const std::string WIDTH_NAMES[];

    virtual
    ~BodySensor()
      { }
};

std::ostream& operator<< ( std::ostream& o, const BodySensor::quality_t );
std::ostream& operator<< ( std::ostream& o, const BodySensor::width_t );

class BodySensor_v1
  : public BodySensor
{
public:
		struct data_t
  {
    int M_time;
    BodySensor::quality_t M_quality;
    BodySensor::width_t M_width;
    double M_stamina;
    double M_effort;
    double M_vel_mag;
    int M_count_kick;
    int M_count_dash;
    int M_count_turn;
    int M_count_say;

			data_t ( const Player& player );
  };
  
  virtual void send ( const BodySensor_v1::data_t& data ) = 0;
};


class BodySensor_v5
		: public BodySensor_v1
{
public:
		struct data_t
				: public BodySensor_v1::data_t
		{
				int M_head_angle;
				int M_count_turn_neck;

				data_t ( const Player& player );
		};
  
		virtual void send ( const BodySensor_v5::data_t& data ) = 0;
};


class BodySensor_v6
  : public BodySensor_v5
{
public:
  struct data_t
    : public BodySensor_v5::data_t
  {
    int M_vel_head;

			data_t ( const Player& player );
  };
  
  virtual void send ( const BodySensor_v6::data_t& data ) = 0;
};

class BodySensor_v7
  : public BodySensor_v6
{
public:
  struct data_t
    : public BodySensor_v6::data_t
  {
    int M_count_catch;
    int M_count_move;
    int M_count_change_view;  

    data_t ( const Player& player );
  };
  
  virtual void send ( const BodySensor_v7::data_t& data ) = 0;
};

class BodySensor_v8
  : public BodySensor_v7
{
public:
  struct data_t
    : public BodySensor_v7::data_t
  {
    Arm::State M_arm_state;
    unsigned int M_tackle_cycles_remaining;
    unsigned int M_tackle_count;

    const Player* M_focus_target;
    unsigned int M_focus_count;

    data_t ( const Player& player );
  };
  
  virtual void send ( const BodySensor_v8::data_t& data ) = 0;
};


#endif








