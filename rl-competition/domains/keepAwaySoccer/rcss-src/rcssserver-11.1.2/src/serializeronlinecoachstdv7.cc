// -*-c++-*-

/***************************************************************************
                            serializeronlinecoachstdv7.cc
               Class for serializing data to std v7 online coaches
                             -------------------
    begin                : 27-MAY-2002
    copyright            : (C) 2002 by The RoboCup Soccer Server
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

#include "serializeronlinecoachstdv7.h"

namespace rcss
{

SerializerOnlineCoachStdv7::SerializerOnlineCoachStdv7( const SerializerCommon& common )
    : SerializerOnlineCoachStdv6( common )
{}

SerializerOnlineCoachStdv7::~SerializerOnlineCoachStdv7() {}

const SerializerOnlineCoachStdv7*
SerializerOnlineCoachStdv7::instance()
{
    rcss::SerializerCommon::Creator cre;
    if( !rcss::SerializerCommon::factory().getCreator( cre, 7 ) )
        return NULL;
    static SerializerOnlineCoachStdv7 ser( cre() );
    return &ser;
}

void
SerializerOnlineCoachStdv7::serializeRefAudio( std::ostream& strm,
                                               const int& time,
                                               const std::string& name,
                                               const char* msg ) const
{
    strm << "(hear " << time << " " << name
         << " " << msg << ")";
}

void
SerializerOnlineCoachStdv7::serializePlayerAudio( std::ostream& strm,
                                                  const int& time,
                                                  const std::string& name,
                                                  const char* msg ) const
{
    strm << "(hear " << time << " " << name << " \""
         << msg << "\")";
}

void
SerializerOnlineCoachStdv7::serializeChangedPlayer( std::ostream& strm,
                                                    int unum,
                                                    int type ) const
{
    strm << "(change_player_type " << unum;
    if( type >= 0 )
        strm << " " << type;
    strm << ")";
}

namespace
{
const SerializerOnlineCoach*
create()
{ return SerializerOnlineCoachStdv7::instance(); }

lib::RegHolder v7 = SerializerOnlineCoach::factory().autoReg( &create, 7 );
}
}
