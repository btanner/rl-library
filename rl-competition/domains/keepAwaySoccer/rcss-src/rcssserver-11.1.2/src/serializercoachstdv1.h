// -*-c++-*-

/***************************************************************************
                            serializercoachstdv1.h
               Class for serializing data to std v1 offline coaches
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

#ifndef SERIALIZERCOACHSTDV1_H
#define SERIALIZERCOACHSTDV1_H

#include "serializer.h"

namespace rcss
{
class SerializerCoachStdv1
    : public SerializerCoach
{
protected:
    SerializerCoachStdv1( const SerializerCommon& common );


public:
    virtual
    ~SerializerCoachStdv1();

    static const SerializerCoachStdv1*
    instance();

    virtual
    void
    serializeRefAudio( std::ostream& strm,
                       const int& time,
                       const char* msg ) const;

    virtual
    void
    serializeCoachAudio( std::ostream& strm,
                         const int& time,
                         const std::string& name,
                         const char* msg ) const;

    virtual
    void
    serializeCoachStdAudio( std::ostream& strm,
                            const int& time,
                            const std::string& name,
                            const rcss::clang::Msg& msg ) const;

    virtual
    void
    serializePlayerAudio( std::ostream& strm,
                          const int& time,
                          const std::string& name,
                          const char* msg ) const;

    virtual
    void
    serializeInit( std::ostream& ) const;
};
}
#endif
