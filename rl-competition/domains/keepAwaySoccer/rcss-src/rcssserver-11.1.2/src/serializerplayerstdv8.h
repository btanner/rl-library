// -*-c++-*-

/***************************************************************************
                            serializerplayerstdv8.h
                  Class for serializing data to std v8 players
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

#ifndef SERIALIZERPLAYERSTDV8_H
#define SERIALIZERPLAYERSTDV8_H

#include "serializerplayerstdv7.h"

namespace rcss
{
class SerializerPlayerStdv8
    : public SerializerPlayerStdv7
{
protected:
    SerializerPlayerStdv8( const SerializerCommon& common );

public:
    virtual
    ~SerializerPlayerStdv8();

    static
    const SerializerPlayerStdv8*
    instance();

    virtual
    void
    serializeAllyAudioFull( std::ostream& strm,
                            const int time,
                            const double dir,
                            const int unum,
                            const char* msg ) const;

    virtual
    void
    serializeOppAudioFull( std::ostream& strm,
                           const int time,
                           const double dir,
                           const char* msg ) const;

    virtual
    void
    serializeAllyAudioShort( std::ostream& strm,
                             const int time,
                             const int unum ) const;

    virtual
    void
    serializeOppAudioShort( std::ostream& strm,
                            const int time ) const;

    virtual
    void
    serializeFSBall( std::ostream& strm,
                     double x,
                     double y,
                     double vel_x,
                     double vel_y ) const;

    virtual
    void
    serializeFSPlayerBegin( std::ostream& strm,
                            char side,
                            int unum,
                            bool goalie,
                            int type,
                            double x,
                            double y,
                            double vel_x,
                            double vel_y,
                            double body_dir,
                            double neck_dir ) const;

    virtual
    void
    serializeFSPlayerArm( std::ostream& strm,
                          double mag,
                          double head ) const;

    virtual
    void
    serializeFSPlayerEnd( std::ostream& strm,
                          double stamina,
                          double effort,
                          double recovery ) const;

    virtual
    void
    serializeFSCounts( std::ostream& strm,
                       int count_kick,
                       int count_dash,
                       int count_turn,
                       int count_catch,
                       int count_move,
                       int count_turn_neck,
                       int count_change_view,
                       int count_say ) const;

    virtual
    void
    serializeServerParamBegin( std::ostream& strm ) const;

    virtual
    void
    serializePlayerParamBegin( std::ostream& strm ) const;

    virtual
    void
    serializePlayerTypeBegin( std::ostream& strm ) const;

    virtual
    void
    serializeParam( std::ostream& strm,
                    const std::string& name,
                    int param ) const;

    virtual
    void
    serializeParam( std::ostream& strm,
                    const std::string& name,
                    bool param ) const;

    virtual
    void
    serializeParam( std::ostream& strm,
                    const std::string& name,
                    double param ) const;

    virtual
    void
    serializeParam( std::ostream& strm,
                    const std::string& name,
                    const std::string& param ) const;

};
}

#endif
