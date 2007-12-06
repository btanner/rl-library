// -*-c++-*-

/***************************************************************************
                            serializercoachstdv8.cc
               Class for serializing data to std v8 offline coaches
                             -------------------
    begin                : 27-JAN-2003
    copyright            : (C) 2003 by The RoboCup Soccer Server
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

#include "serializercoachstdv8.h"
#include "clangmsg.h"

namespace rcss
{
SerializerCoachStdv8::SerializerCoachStdv8( const SerializerCommon& common )
    : SerializerCoachStdv7( common )
{}


SerializerCoachStdv8::~SerializerCoachStdv8() {}

const SerializerCoachStdv8*
SerializerCoachStdv8::instance ()
{
    rcss::SerializerCommon::Creator cre;
    if( !rcss::SerializerCommon::factory().getCreator( cre, 8 ) )
        return NULL;
    static SerializerCoachStdv8 ser( cre() );
    return &ser;
}

namespace
{
const SerializerCoach*
create()
{ return SerializerCoachStdv8::instance(); }

lib::RegHolder v8 = SerializerCoach::factory().autoReg( &create, 8 );
lib::RegHolder v9 = SerializerCoach::factory().autoReg( &create, 9 );
lib::RegHolder v10 = SerializerCoach::factory().autoReg( &create, 10 );
lib::RegHolder v11 = SerializerCoach::factory().autoReg( &create, 11 );
}
}
