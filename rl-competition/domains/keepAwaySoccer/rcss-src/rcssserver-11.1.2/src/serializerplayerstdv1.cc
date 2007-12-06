// -*-c++-*-

/***************************************************************************
                            serializerplayerstdv1.cc
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

#include "serializerplayerstdv1.h"
#include "param.h"
#include "clangmsg.h"

static char *PlayModeString[] = PLAYMODE_STRINGS;


namespace rcss
{

SerializerPlayerStdv1::SerializerPlayerStdv1( const SerializerCommon& common )
    : SerializerPlayer( common )
{}

SerializerPlayerStdv1::~SerializerPlayerStdv1()
{}

const SerializerPlayerStdv1*
SerializerPlayerStdv1::instance()
{
    SerializerCommon::Creator cre;
    if( !SerializerCommon::factory().getCreator( cre, 1 ) )
        return NULL;
    static SerializerPlayerStdv1 ser( cre() );
    return &ser;
}

void
SerializerPlayerStdv1::serializeRefAudio( std::ostream& strm,
                                          const int& time,
                                          const char* msg ) const
{
    strm << "(hear " << time << " "
         << REFEREE_NAME << " " << msg << ")";
}

void
SerializerPlayerStdv1::serializeCoachAudio( std::ostream& strm,
                                            const int& time,
                                            const std::string& name,
                                            const char* msg ) const
{
    strm << "(hear " << time << " "
         << name << " " << msg << ")";
}

void
SerializerPlayerStdv1::serializeCoachStdAudio( std::ostream& strm,
                                               const int& time,
                                               const std::string& name,
                                               const rcss::clang::Msg& msg ) const
{
    strm << "(hear " << time << " "
         << name << " " << msg << ")";
}

void
SerializerPlayerStdv1::serializeSelfAudio( std::ostream& strm,
                                           const int& time,
                                           const char* msg ) const
{
    strm << "(hear " << time << " self "
         << msg << ")";
}

void
SerializerPlayerStdv1::serializePlayerAudio( std::ostream& strm,
                                             const int& time,
                                             const double& dir,
                                             const char* msg ) const
{
    strm << "(hear " << time << " "
         << dir << " " << msg << ")";
}

void
SerializerPlayerStdv1::serializeVisualBegin( std::ostream& strm,
                                             int time ) const
{
    strm << "(see " << time;
}

void
SerializerPlayerStdv1::serializeVisualEnd( std::ostream& strm ) const
{
    strm << ")";
}

void
SerializerPlayerStdv1::serializeVisualObject( std::ostream& strm,
                                              const std::string & name,
                                              int dir ) const
{
    strm << " (" << name << " " << dir << ")";
}

void
SerializerPlayerStdv1::serializeVisualObject( std::ostream& strm,
                                              const std::string & name,
                                              double dist, int dir ) const
{
    strm << " (" << name << " " << dist << " " << dir << ")";
}

void
SerializerPlayerStdv1::serializeVisualObject( std::ostream& strm,
                                              const std::string & name,
                                              double dist, int dir,
                                              double dist_chg,
                                              double dir_chg ) const
{
    strm << " (" << name << " " << dist << " " << dir
         << " " << dist_chg << " " << dir_chg
         << ")";
}

void
SerializerPlayerStdv1::serializeVisualObject( std::ostream& strm,
                                              const std::string & name,
                                              double dist, int dir,
                                              double dist_chg,
                                              double dir_chg,
                                              double body_dir ) const
{
    strm << " (" << name << " " << dist << " " << dir
         << " " << dist_chg << " " << dir_chg
         << " " << body_dir
         << ")";
}

void
SerializerPlayerStdv1::serializeVisualObject( std::ostream& strm,
                                              const std::string & name,
                                              double dist, int dir,
                                              double dist_chg,
                                              double dir_chg,
                                              double body_dir,
                                              double head_dir ) const
{
    strm << " (" << name << " " << dist << " " << dir
         << " " << dist_chg << " " << dir_chg
         << " " << body_dir << " " << head_dir
         << ")";
}

void
SerializerPlayerStdv1::serializeVisualObject( std::ostream& strm,
                                              const std::string & name,
                                              double dist, int dir,
                                              bool tackling ) const
{
    strm << " (" << name << " " << dist << " " << dir;
    if( tackling )
        strm << " t";
    strm << ")";
}

void
SerializerPlayerStdv1::serializeVisualObject( std::ostream& strm,
                                              const std::string & name,
                                              double dist, int dir,
                                              int point_dir, bool tackling ) const
{
    strm << " (" << name << " " << dist << " " << dir
         << " " << point_dir;
    if( tackling )
        strm << " t";
    strm << ")";
}

void
SerializerPlayerStdv1::serializeVisualObject( std::ostream& strm,
                                              const std::string & name,
                                              double dist, int dir,
                                              double dist_chg, double dir_chg,
                                              int body_dir, int head_dir,
                                              bool tackling ) const
{
    strm << " (" << name << " " << dist << " " << dir
         << " " << dist_chg << " " << dir_chg
         << " " << body_dir << " " << head_dir;
    if( tackling )
        strm << " t";
    strm << ")";
}

void
SerializerPlayerStdv1::serializeVisualObject( std::ostream& strm,
                                              const std::string & name,
                                              double dist, int dir,
                                              double dist_chg, double dir_chg,
                                              int body_dir, int head_dir,
                                              int point_dir, bool tackling ) const
{
    strm << " (" << name << " " << dist << " " << dir
         << " " << dist_chg << " " << dir_chg
         << " " << body_dir << " " << head_dir
         << " " << point_dir;
    if( tackling )
        strm << " t";
    strm << ")";
}

void
SerializerPlayerStdv1::serializeBodyBegin( std::ostream& strm,
                                           int time ) const
{ strm << "(sense_body " << time; }

void
SerializerPlayerStdv1::serializeBodyEnd( std::ostream& strm ) const
{ strm << ")"; }

void
SerializerPlayerStdv1::serializeBodyViewMode( std::ostream& strm,
                                              const char* qual,
                                              const char* width ) const
{ strm << " (view_mode " << qual << " " << width << ")"; }

void
SerializerPlayerStdv1::serializeBodyStamina( std::ostream& strm,
                                             double stamina,
                                             double effort ) const
{ strm << " (stamina " << stamina << " " << effort << ")"; }

void
SerializerPlayerStdv1::serializeBodyVelocity( std::ostream& strm,
                                              double mag ) const
{ strm << " (speed " << mag << ")"; }

void
SerializerPlayerStdv1::serializeBodyVelocity( std::ostream& strm,
                                              double mag,
                                              int head ) const
{ strm << " (speed " << mag << " " << head << ")"; }

void
SerializerPlayerStdv1::serializeBodyCounts( std::ostream& strm,
                                            int count_kick,
                                            int count_dash,
                                            int count_turn,
                                            int count_say ) const
{
    strm << " (kick " << count_kick << ")";
    strm << " (dash " << count_dash << ")";
    strm << " (turn " << count_turn << ")";
    strm << " (say " << count_say << ")";
}

void
SerializerPlayerStdv1::serializeBodyCounts( std::ostream& strm,
                                            int count_catch,
                                            int count_move,
                                            int count_change_view ) const
{
    strm << " (catch " << count_catch << ")";
    strm << " (move " << count_move << ")";
    strm << " (change_view " << count_change_view << ")";
}

void
SerializerPlayerStdv1::serializeNeckAngle( std::ostream& strm,
                                           int ang ) const
{ strm << " (head_angle " << ang << ")"; }

void
SerializerPlayerStdv1::serializeNeckCount( std::ostream& strm,
                                           int count_turn_neck ) const
{ strm << " (turn_neck " << count_turn_neck << ")"; }

void
SerializerPlayerStdv1::serializeArm( std::ostream& strm,
                                     int movable_cycles,
                                     int expires_cycles,
                                     double dist,
                                     int head,
                                     int count ) const
{
    strm << " (arm";
    strm << " (movable " << movable_cycles << ")";
    strm << " (expires " << expires_cycles << ")";
    strm << " (target " << dist << " " << head << ")";
    strm << " (count " << count << ")";
    strm << ")";
}

void
SerializerPlayerStdv1::serializeFocus( std::ostream& strm,
                                       const char* name,
                                       int count ) const
{
    strm << " (focus";
    strm << " (target " << name << ")";
    strm << " (count " << count << ")";
    strm << ")";
}

void
SerializerPlayerStdv1::serializeFocus( std::ostream& strm,
                                       const char* team,
                                       int unum,
                                       int count ) const
{
    strm << " (focus";
    strm << " (target " << team << " " << unum << ")";
    strm << " (count " << count << ")";
    strm << ")";
}


void
SerializerPlayerStdv1::serializeTackle( std::ostream& strm,
                                        int cycles,
                                        int count ) const
{
    strm << " (tackle";
    strm << " (expires " << cycles << ")";
    strm << " (count " << count << ")";
    strm << ")";
}


void
SerializerPlayerStdv1::serializeFSBegin( std::ostream& strm,
                                         int time ) const
{
    strm.precision(6);
    strm << "(fullstate " << time;
}

void
SerializerPlayerStdv1::serializeFSEnd( std::ostream& strm) const
{
    strm << ")";
}

void
SerializerPlayerStdv1::serializeFSViewMode( std::ostream& strm,
                                            const char* qual,
                                            const char* width ) const
{
    strm << " (vmode " << qual << " " << width << ")";
}

void
SerializerPlayerStdv1::serializeFSPlayMode( std::ostream& strm,
                                            const char* mode ) const
{
    strm << " (pmode " << mode << ")";
}

// caution: in version 5 left is the points for the left team
// and right is the points for the right team.  In version 8
// and later left is the points of the players team and right
// is the points for the opp
void
SerializerPlayerStdv1::serializeFSScore( std::ostream& strm,
                                         int left,
                                         int right ) const
{
    strm << " (score " << left << " " << right << ")";
}


void
SerializerPlayerStdv1::serializeFSBall( std::ostream& strm,
                                        double x,
                                        double y,
                                        double vel_x,
                                        double vel_y ) const
{
    strm << " (ball"
         << " " << x
         << " " << y
         << " " << vel_x
         << " " << vel_y
         << ")";
}

void
SerializerPlayerStdv1::serializeFSPlayerBegin( std::ostream& strm,
                                               char side,
                                               int unum,
                                               bool,
                                               int,
                                               double x,
                                               double y,
                                               double vel_x,
                                               double vel_y,
                                               double body_dir,
                                               double neck_dir ) const

{
    strm << " (" << side << "_" << unum
         << " " << x
         << " " << y
         << " " << vel_x
         << " " << vel_y
         << " " << body_dir
         << " " << neck_dir;
}

void
SerializerPlayerStdv1::serializeFSPlayerEnd( std::ostream& strm,
                                             double stamina,
                                             double effort,
                                             double recovery ) const
{
    strm << " " << stamina
         << " " << effort
         << " " << recovery
         << ")";
}


void
SerializerPlayerStdv1::serializeInit( std::ostream& strm,
                                      const char* side,
                                      int unum,
                                      const PlayMode& mode ) const
{
    strm << "(init " << side << " " << unum << " "
         << PlayModeString[ mode ] << ")";
}

void
SerializerPlayerStdv1::serializeReconnect( std::ostream& strm,
                                           const char* side,
                                           const PlayMode& mode ) const
{
    strm << "(reconnect " << side << " "
         << PlayModeString[ mode ] << ")";
}

void
SerializerPlayerStdv1::serializeOKClang( std::ostream& strm,
                                         int min,
                                         int max ) const
{
    strm << "(ok clang (ver " << min << " " << max << "))";
}

void
SerializerPlayerStdv1::serializeErrorNoTeamName( std::ostream& strm,
                                                 const std::string& team_name ) const
{
    strm << "(error no team with name " << team_name << ")";
}

void
SerializerPlayerStdv1::serializeScore( std::ostream& strm,
                                       int time,
                                       int our,
                                       int opp ) const
{
    strm << "(score " << time << " " << our << " " << opp << ")";
}

namespace
{
const SerializerPlayer*
create()
{ return SerializerPlayerStdv1::instance(); }

lib::RegHolder v1 = SerializerPlayer::factory().autoReg( &create, 1 );
lib::RegHolder v2 = SerializerPlayer::factory().autoReg( &create, 2 );
lib::RegHolder v3 = SerializerPlayer::factory().autoReg( &create, 3 );
lib::RegHolder v4 = SerializerPlayer::factory().autoReg( &create, 4 );
lib::RegHolder v5 = SerializerPlayer::factory().autoReg( &create, 5 );
lib::RegHolder v6 = SerializerPlayer::factory().autoReg( &create, 6 );
}
}
