// -*-c++-*-

/***************************************************************************
                            serializerplayerstdv1.h
                  Class for serializing data to std v1 players
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

#ifndef SERIALIZERPLAYERSTDV1_H
#define SERIALIZERPLAYERSTDV1_H

#include "serializer.h"

namespace rcss
{
class SerializerPlayerStdv1
    : public SerializerPlayer
{
protected:
    SerializerPlayerStdv1( const SerializerCommon& common );

public:
    virtual
    ~SerializerPlayerStdv1();

    static
    const SerializerPlayerStdv1*
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
    serializeSelfAudio( std::ostream& strm,
                        const int& time,
                        const char* msg ) const;

    virtual
    void
    serializePlayerAudio( std::ostream& strm,
                          const int& time,
                          const double& dir,
                          const char* msg ) const;

    virtual
    void
    serializeVisualBegin( std::ostream& strm,
                          int time ) const;

    virtual
    void
    serializeVisualEnd( std::ostream& strm ) const;

    virtual
    void
    serializeVisualObject( std::ostream& strm,
                           const std::string & name,
                           int dir ) const;

    virtual
    void
    serializeVisualObject( std::ostream& strm,
                           const std::string & name,
                           double dist, int dir ) const;

    virtual
    void
    serializeVisualObject( std::ostream& strm,
                           const std::string & name,
                           double dist, int dir,
                           double dist_chg, double dir_chg ) const;

    virtual
    void
    serializeVisualObject( std::ostream& strm,
                           const std::string & name,
                           double dist, int dir,
                           double dist_chg, double dir_chg,
                           double body_dir ) const;

    virtual
    void
    serializeVisualObject( std::ostream& strm,
                           const std::string & name,
                           double dist, int dir,
                           double dist_chg, double dir_chg,
                           double body_dir, double head_dir ) const;


    virtual
    void
    serializeVisualObject( std::ostream& strm,
                           const std::string & name,
                           double dist, int dir,
                           bool tackling ) const;

    virtual
    void
    serializeVisualObject( std::ostream& strm,
                           const std::string & name,
                           double dist, int dir,
                           int point_dir, bool tackling ) const;

    virtual
    void
    serializeVisualObject( std::ostream& strm,
                           const std::string & name,
                           double dist, int dir,
                           double dist_chg, double dir_chg,
                           int body_dir, int head_dir,
                           bool tackling ) const;

    virtual
    void
    serializeVisualObject( std::ostream& strm,
                           const std::string & name,
                           double dist, int dir,
                           double dist_chg, double dir_chg,
                           int body_dir, int head_dir,
                           int point_dir, bool tackling ) const;

    virtual
    void
    serializeBodyBegin( std::ostream& strm, int time ) const;

    virtual
    void
    serializeBodyEnd( std::ostream& strm ) const;

    virtual
    void
    serializeBodyViewMode( std::ostream& strm,
                           const char* qual,
                           const char* width ) const;

    virtual
    void
    serializeBodyStamina( std::ostream& strm,
                          double stamina,
                          double effort ) const;

    virtual
    void
    serializeBodyVelocity( std::ostream& strm,
                           double mag ) const;

    virtual
    void
    serializeBodyVelocity( std::ostream& strm,
                           double mag,
                           int head ) const;

    virtual
    void
    serializeBodyCounts( std::ostream& strm,
                         int count_kick,
                         int count_dash,
                         int count_turn,
                         int count_say ) const;

    virtual
    void
    serializeBodyCounts( std::ostream& strm,
                         int count_catch,
                         int count_move,
                         int count_change_view ) const;

    virtual
    void
    serializeNeckAngle( std::ostream& strm,
                        int ang ) const;

    virtual
    void
    serializeNeckCount( std::ostream& strm,
                        int count_turn_neck ) const;

    virtual
    void
    serializeArm( std::ostream& strm,
                  int movable_cycles,
                  int expires_cycles,
                  double dist,
                  int head,
                  int count ) const;

    virtual
    void
    serializeFocus( std::ostream& strm,
                    const char* name,
                    int count ) const;

    virtual
    void
    serializeFocus( std::ostream& strm,
                    const char* team,
                    int unum,
                    int count ) const;


    virtual
    void
    serializeTackle( std::ostream& strm,
                     int cycles,
                     int count ) const;


    virtual
    void
    serializeFSBegin( std::ostream& strm,
                      int time ) const;

    virtual
    void
    serializeFSEnd( std::ostream& strm ) const;

    virtual
    void
    serializeFSPlayMode( std::ostream& strm,
                         const char* mode ) const;

    virtual
    void
    serializeFSViewMode( std::ostream& strm,
                         const char* qual,
                         const char* width ) const;

    virtual
    void
    serializeFSScore( std::ostream& strm,
                      int left,
                      int right ) const;

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
                            bool,
                            int,
                            double x,
                            double y,
                            double vel_x,
                            double vel_y,
                            double body_dir,
                            double neck_dir ) const;

    virtual
    void
    serializeFSPlayerEnd( std::ostream& strm,
                          double stamina,
                          double effort,
                          double recovery ) const;

    virtual
    void
    serializeInit( std::ostream& strm,
                   const char* side,
                   int unum,
                   const PlayMode& mode ) const;

    virtual
    void
    serializeReconnect( std::ostream& strm,
                        const char* side,
                        const PlayMode& mode ) const;


    virtual
    void
    serializeOKClang( std::ostream& strm,
                      int min,
                      int max ) const;

    virtual
    void
    serializeErrorNoTeamName( std::ostream& strm,
                              const std::string& team_name ) const;

    virtual
    void
    serializeScore( std::ostream& strm,
                    int time,
                    int our,
                    int opp ) const;
};
}

#endif
