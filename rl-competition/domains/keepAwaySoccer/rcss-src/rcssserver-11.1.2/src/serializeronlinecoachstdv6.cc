// -*-c++-*-

/***************************************************************************
                            serializeronlinecoachstdv6.cc
               Class for serializing data to std v6 online coaches
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

#include "serializeronlinecoachstdv6.h"

namespace rcss
{

SerializerOnlineCoachStdv6::SerializerOnlineCoachStdv6( const SerializerCommon& common )
    : SerializerOnlineCoachStdv1( common )
{}

SerializerOnlineCoachStdv6::~SerializerOnlineCoachStdv6() {}

const SerializerOnlineCoachStdv6*
SerializerOnlineCoachStdv6::instance()
{
    rcss::SerializerCommon::Creator cre;
    if( !rcss::SerializerCommon::factory().getCreator( cre, 6 ) )
        return NULL;
    static SerializerOnlineCoachStdv6 ser( cre() );
    return &ser;
}

void
SerializerOnlineCoachStdv6::serializeInit( std::ostream& strm,
                                           int side ) const
{
    strm << "(init "
         << ( side == LEFT ? 'l' : 'r' )
         << " ok)";
}

namespace
{
const SerializerOnlineCoach*
create()
{ return SerializerOnlineCoachStdv6::instance(); }

lib::RegHolder v6 = SerializerOnlineCoach::factory().autoReg( &create, 6 );
}
}
