// -*-c++-*-

/***************************************************************************
                                   visual.cc
                     Classes for building visual messages
                             -------------------
    begin                : 5-AUG-2002
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

#ifdef HAVE_CONFIG_H
#include "config.h"
#endif

#include "visual.h"
#include "serializer.h"
#include "object.h"
#include "player.h"
#include "field.h"

namespace rcss
{

/*!
//===================================================================
//
//  CLASS: VisualSendor
//
//  DESC: Base class for the visual protocol.
//
//===================================================================
*/

VisualSender::VisualSender( std::ostream & transport )
    :  Sender( transport )
{}

VisualSender::~VisualSender()
{}

/*!
//===================================================================
//
//  CLASS: VisualSenderPlayer
//
//  DESC: Base class for the visual protocol for players.
//
//===================================================================
*/

VisualSenderPlayer::Factory&
VisualSenderPlayer::factory()
{ static Factory rval; return rval; }

VisualSenderPlayer::VisualSenderPlayer( const Params& params )
    : VisualSender( params.m_transport ),
      m_ser( params.m_ser ),
      m_self( params.m_self ),
      m_stadium( params.m_stadium ),
      m_sendcnt( 0 )
{}

VisualSenderPlayer::~VisualSenderPlayer()
{}

/*!
//===================================================================
//
//  CLASS: VisualSensorPlayerV1
//
//  DESC: Class for the version 1* visual protocol.  This version is
//        completely unused as far as I am aware of, but it is here
//        none the less, just in case there is someone somewhere
//        still using it.
//
//        * It's version 1 to the simualtor in it's current form.
//        From what I know the original simulator was written in
//        lisp and the first C++ version was actually version 3.  I
//        don't know if the protocol was compatible with previous
//        versions, so this may well be the version 3 protocol.
//
//===================================================================
*/

VisualSenderPlayerV1::VisualSenderPlayerV1( const Params & params )
    : VisualSenderPlayer( params )
{}

VisualSenderPlayerV1::~VisualSenderPlayerV1()
{}

void
VisualSenderPlayerV1::sendVisual()
{
    incSendCount();

    if ( sendCount() >= self().visSend() )
        resetSendCount();
    else
        return;

    serializer().serializeVisualBegin( transport(), stadium().time() );
    if( self().highquality() )
    {
        M_send_flag = &VisualSenderPlayerV1::sendHighFlag;
        M_send_ball = &VisualSenderPlayerV1::sendHighBall;
        M_send_player = &VisualSenderPlayerV1::sendHighPlayer;
        M_serialize_line = &VisualSenderPlayerV1::serializeHighLine;
    }
    else
    {
        M_send_flag = &VisualSenderPlayerV1::sendLowFlag;
        M_send_ball = &VisualSenderPlayerV1::sendLowBall;
        M_send_player = &VisualSenderPlayerV1::sendLowPlayer;
        M_serialize_line = &VisualSenderPlayerV1::serializeLowLine;
    }
    sendFlags();
    sendBalls();
    sendPlayers();
    sendLines();
    serializer().serializeVisualEnd( transport() );
    transport() << std::ends << std::flush;
}

void
VisualSenderPlayerV1::sendFlags()
{
    const std::vector< PObject * >::const_iterator end = stadium().landmarks().end();
    for ( std::vector< PObject * >::const_iterator it = stadium().landmarks().begin();
          it != end;
          ++it )
    {
        if ( (*it)->objVer() <= self().version() )
        {
            sendFlag( **it );
        }
    }

//     const PObjectTable::Cont::const_iterator end = stadium().votable.objects().end();
//     for ( PObjectTable::Cont::const_iterator it = stadium().votable.objects().begin();
//           it != end;
//           ++it )
//     {
//         switch ( (*it)->getObjectType() ) {
//         case MPObject::OT_GOAL:
//             if( (*it)->objVer() <= self().version )
//                 sendFlag( **it );
//             break;
//
//         case MPObject::OT_FLAG:
//             if( (*it)->objVer() <= self().version )
//                 sendFlag( **it );
//             break;
//         default:
//             break;
//         }
//     }
}

void
VisualSenderPlayerV1::sendBalls()
{
    if ( stadium().ball().objVer() <= self().version() )
    {
        sendBall( stadium().ball() );
    }
}

void
VisualSenderPlayerV1::sendPlayers()
{
    const Stadium::PlayerCont::const_iterator end = stadium().players().end();
    for ( Stadium::PlayerCont::const_iterator p = stadium().players().begin();
          p != end;
          ++p )
    {
        if ( *p != &self()
             && (*p)->alive != DISABLE
             && (*p)->objVer() <= self().version() )
        {
            sendPlayer( *(*p) );
        }
    }
}


void
VisualSenderPlayerV1::sendLines()
{
    int line_count = 0;
    int max_line_count;
    int min_line_count;
    if ( std::fabs( self().pos().x ) < PITCH_LENGTH*0.5
         && std::fabs( self().pos().y ) < PITCH_WIDTH*0.5 )
    {
        min_line_count = max_line_count = 1;
    }
    else
    {
        max_line_count = 2;
        min_line_count = 0;
    }

    if ( line_count < max_line_count
         && stadium().field.line_l.objVer() <= self().version() )
    {
        if ( sendLine( stadium().field.line_l ) )
            ++line_count;
    }
    if ( line_count < max_line_count
         && stadium().field.line_r.objVer() <= self().version() )
    {
        if( sendLine( stadium().field.line_r ) )
            ++line_count;
    }
    if ( line_count < max_line_count
         && stadium().field.line_t.objVer() <= self().version() )
    {
        if ( sendLine( stadium().field.line_t ) )
            ++line_count;
    }
    if ( line_count < max_line_count
         && stadium().field.line_b.objVer() <= self().version() )
    {
        if ( sendLine( stadium().field.line_b ) )
            ++line_count;
    }
    if ( line_count < min_line_count )
    {
        std::cerr << __FILE__ << ": " << __LINE__
                  << ": Error rendering visual: not enough lines\n"
                  << "Player = " << self() << std::endl;
    }
}

inline
double
VisualSenderPlayerV1::calcUnQuantDist ( const PObject & obj ) const
{
    return self().pos().distance( obj.pos() );
}

inline
double
VisualSenderPlayerV1::calcRadDir( const PObject & obj )
{
    return normalize_angle( self().vangle( obj )
                            - self().angleNeckCommitted() );
}

void
VisualSenderPlayerV1::sendLowFlag( const PObject & flag )
{
    double ang = calcRadDir( flag );

    if ( std::fabs( ang ) < self().visibleAngle() * 0.5 )
    {
        serializer().serializeVisualObject( transport(),
                                            calcName( flag ),
                                            calcDegDir( ang ) );
    }
    else if ( calcUnQuantDist( flag ) <= self().vis_distance )
    {
        serializer().serializeVisualObject( transport(),
                                            calcCloseName( flag ),
                                            calcDegDir( ang ) );
    }
}

void
VisualSenderPlayerV1::sendHighFlag( const PObject & flag )
{
    //double ang = calcRadDir( flag );
    double ang = normalize_angle( self().vangle( flag )
                                  - self().angleNeckCommitted() );

    double qstep = self().landDistQStep();

    //double un_quant_dist = calcUnQuantDist( flag );
    double un_quant_dist = self().pos().distance( flag.pos() );
    double quant_dist = calcQuantDist( un_quant_dist, qstep );

    if ( std::fabs( ang ) < self().visibleAngle() * 0.5 )
    {
        double prob = ( ( quant_dist - UNUM_FAR_LENGTH )
                        / ( UNUM_TOOFAR_LENGTH - UNUM_FAR_LENGTH ) );

        if ( decide( prob ) )
        {
            serializer().serializeVisualObject( transport(),
                                                calcName( flag ),
                                                quant_dist,
                                                calcDegDir( ang ) );
        }
        else
        {
            double dist_chg, dir_chg;
            calcVel( PVector(), flag.pos(),
                     un_quant_dist, quant_dist,
                     dist_chg, dir_chg );
            serializer().serializeVisualObject( transport(),
                                                calcName( flag ),
                                                quant_dist,
                                                calcDegDir( ang ),
                                                dist_chg,
                                                dir_chg );
        }
    }
    else if ( un_quant_dist <= self().vis_distance )
    {
        serializer().serializeVisualObject( transport(),
                                            calcCloseName( flag ),
                                            quant_dist,
                                            calcDegDir( ang ) );
    }
}

void
VisualSenderPlayerV1::sendLowBall( const MPObject & ball )
{
    double ang = calcRadDir( ball );

    if( std::fabs( ang ) < self().visibleAngle() * 0.5 )
    {
        serializer().serializeVisualObject( transport(),
                                            calcName( ball ),
                                            calcDegDir( ang ) );
    }
    else if( calcUnQuantDist( ball ) <= self().vis_distance )
    {
        serializer().serializeVisualObject( transport(),
                                            calcCloseName( ball ),
                                            calcDegDir( ang ) );
    }
}


void
VisualSenderPlayerV1::sendHighBall( const MPObject & ball )
{
    double ang = calcRadDir( ball );
    double un_quant_dist = calcUnQuantDist( ball );

    double qstep = self().distQStep();

    double quant_dist = calcQuantDist( un_quant_dist, qstep );

    if ( std::fabs( ang ) < self().visibleAngle() * 0.5 )
    {
        double prob = ( ( quant_dist - UNUM_FAR_LENGTH )
                        / ( UNUM_TOOFAR_LENGTH - UNUM_FAR_LENGTH ) );

        if ( decide( prob ) )
        {
            serializer().serializeVisualObject( transport(),
                                                calcName( ball ),
                                                quant_dist,
                                                calcDegDir( ang ) );
        }
        else
        {
            double dist_chg, dir_chg;
            calcVel( ball.vel(), ball.pos(),
                     un_quant_dist, quant_dist,
                     dist_chg, dir_chg );
            serializer().serializeVisualObject( transport(),
                                                calcName( ball ),
                                                quant_dist,
                                                calcDegDir( ang ),
                                                dist_chg,
                                                dir_chg );
        }
    }
    else if ( un_quant_dist <= self().vis_distance )
    {
        serializer().serializeVisualObject( transport(),
                                            calcCloseName( ball ),
                                            quant_dist,
                                            calcDegDir( ang ) );
    }
}

void
VisualSenderPlayerV1::sendLowPlayer( const Player & player )
{
    double ang = calcRadDir( player );
    double un_quant_dist = calcUnQuantDist( player );

    if ( std::fabs( ang ) < self().visibleAngle() * 0.5 )
    {
        double qstep = self().distQStep();

        double quant_dist = calcQuantDist( un_quant_dist, qstep );

        double prob = ( ( quant_dist - TEAM_FAR_LENGTH )
                        / ( TEAM_TOOFAR_LENGTH - TEAM_FAR_LENGTH ) );

        if ( decide( prob ) )
        {
            serializer().serializeVisualObject( transport(),
                                                calcTFarName( player ),
                                                calcDegDir( ang ) );
        }
        else
        {
            prob = ( ( quant_dist - UNUM_FAR_LENGTH )
                     / ( UNUM_TOOFAR_LENGTH - UNUM_FAR_LENGTH ) );
            if ( decide( prob ) )
            {
                serializer().serializeVisualObject( transport(),
                                                    calcUFarName( player ),
                                                    calcDegDir( ang ) );
            }
            else
            {
                serializer().serializeVisualObject( transport(),
                                                    calcName( player ),
                                                    calcDegDir( ang ) );
            }
        }
    }
    else if ( un_quant_dist <= self().vis_distance )
    {
        serializer().serializeVisualObject( transport(),
                                            calcCloseName( player ),
                                            calcDegDir( ang ) );
    }
}


void
VisualSenderPlayerV1::sendHighPlayer( const Player & player )
{
    double ang = calcRadDir( player );
    double un_quant_dist = calcUnQuantDist( player );

    double qstep = self().distQStep();
    double quant_dist = calcQuantDist( un_quant_dist, qstep );

    if ( std::fabs( ang ) < self().visibleAngle() * 0.5 )
    {
        double prob = ( ( quant_dist - TEAM_FAR_LENGTH )
                        / ( TEAM_TOOFAR_LENGTH - TEAM_FAR_LENGTH ) );

        if ( decide( prob ) )
        {
            serializer().serializeVisualObject( transport(),
                                                calcTFarName( player ),
                                                quant_dist,
                                                calcDegDir( ang ) );
        }
        else
        {
            prob = ( ( quant_dist - UNUM_FAR_LENGTH )
                     / ( UNUM_TOOFAR_LENGTH - UNUM_FAR_LENGTH ) );

            if ( decide( prob ) )
            {
                serializePlayer( player,
                                 calcUFarName( player ),
                                 quant_dist,
                                 calcDegDir( ang ) );
            }
            else
            {
                double dist_chg, dir_chg;
                calcVel( player.vel(), player.pos(),
                         un_quant_dist, quant_dist,
                         dist_chg, dir_chg );
                serializePlayer( player,
                                 calcName( player ),
                                 quant_dist,
                                 calcDegDir( ang ),
                                 dist_chg, dir_chg );
            }
        }
    }
    else if ( un_quant_dist <= player.vis_distance )
    {
        serializer().serializeVisualObject( transport(),
                                            calcCloseName( player ),
                                            quant_dist,
                                            calcDegDir( ang ) );
    }
}


bool
VisualSenderPlayerV1::sendLine( const PObject & line )
{
    /*! the angle of an outward pointing normal ( 90degs to ) to
      the line */
    double line_normal;

    //! perp distance of the player from the line
    double player_2_line;

    //! the x of y value of where the line starts
    double line_start;

    //! the x or y value of where the line stops
    double line_stop;

    //! a flag to specify if the line is vertical or horizontal
    bool vert;

    /*! be very carefull here.  The lines pos.x is actually it's
      distance from the center of the field, not neccesarily it's
      x position. */

    //! left line
    if ( line.pos().x == -PITCH_LENGTH*0.5 )
    {
        line_normal = M_PI;
        if ( self().pos().x < line.pos().x )
            line_normal = 0.0;
        player_2_line = line.pos().x - self().pos().x;
        line_start = -PITCH_WIDTH*0.5;
        line_stop = PITCH_WIDTH*0.5;
        vert = true;
    }
    //! right line
    else if ( line.pos().x == PITCH_LENGTH*0.5 )
    {
        line_normal = 0.0;
        if( self().pos().x > line.pos().x )
            line_normal = M_PI;
        player_2_line = line.pos().x - self().pos().x;
        line_start = -PITCH_WIDTH*0.5;
        line_stop = PITCH_WIDTH*0.5;
        vert = true;
    }
    //! top line
    else if ( line.pos().x == -PITCH_WIDTH*0.5 )
    {
        line_normal = -M_PI*0.5;
        if ( self().pos().y < line.pos().x )
            line_normal = M_PI*0.5;
        player_2_line = line.pos().x - self().pos().y;
        line_start = -PITCH_LENGTH*0.5;
        line_stop = PITCH_LENGTH*0.5;
        vert = false;
    }
    //! bottom line
    else if ( line.pos().x == PITCH_WIDTH*0.5 )
    {
        line_normal = M_PI*0.5;
        if ( self().pos().y > line.pos().x )
            line_normal = -M_PI*0.5;
        player_2_line = line.pos().x - self().pos().y;
        line_start = -PITCH_LENGTH*0.5;
        line_stop = PITCH_LENGTH*0.5;
        vert = false;
    }
    else
    {
        std::cerr << __FILE__ << ": " << __LINE__
                  << ": Error, unknown line: " << line << std::endl;
        return false;
    }

    //! angle between the players line of sight and the line's normal
    double sight_2_line_ang = calcLineRadDir( line_normal );

    /*! if the angle between the line of sight and the line's norm
      is not within -90.0 and 90 degrees then the player is
      looking parallel or away from the line, thus it cannot be
      visible. */
    if ( std::fabs( sight_2_line_ang ) >= M_PI*0.5 )
    {
        return false;
    }

    /*! this gives us the x or y offset from the player for where
      their line of sight intersects the line */
    double line_intersect = player_2_line * tan( sight_2_line_ang );

    /*! this calculates the actual x or y value for where the line
      of sight intersects the line.  Because the y axis is
      inverted, we need to use -line_intersect if the line is
      vertical. */
    if ( vert )
        line_intersect = self().pos().y - line_intersect;
    else
        line_intersect += self().pos().x;

    /*! If the point that the players line of sight intersects the
      line beyond it's beginning or end then the player wont see
      this line */
    if ( line_intersect < line_start
         || line_intersect > line_stop )
    {
        return false;
    }

    serializeLine( calcName( line ),
                   calcLineDir( sight_2_line_ang ),
                   sight_2_line_ang, player_2_line );
    return true;
}

void
VisualSenderPlayerV1::serializeLowLine( const std::string & name,
                                        const int dir,
                                        const double &, const double & )
{
    serializer().serializeVisualObject( transport(), name, dir );
}

void
VisualSenderPlayerV1::serializeHighLine( const std::string & name,
                                         const int dir,
                                         const double & sight_2_line_ang,
                                         const double & player_2_line )
{
    double dist = calcLineDist( sight_2_line_ang, player_2_line,
                                self().landDistQStep() );
    serializer().serializeVisualObject( transport(), name, dist, dir );
}

// double
// VisualSenderPlayerV1::calcRadDir( const PObject & obj )
// {
//     return normalize_angle( self().vangle( obj )
//                             - self().angle_neck_committed );
// }

// int
// VisualSenderPlayerV1::calcDegDir( const double & rad_dir ) const
// {
//     return rad2Deg( rad_dir );
// }

double
VisualSenderPlayerV1::calcLineRadDir( const double & line_normal ) const
{
    return normalize_angle( line_normal
                            - self().angleBodyCommitted()
                            - self().angleNeckCommitted() );
}

// int
// VisualSenderPlayerV1::calcLineDir( const double & sight_2_line_ang ) const
// {
//     if ( sight_2_line_ang > 0 )
//         return rad2Deg( sight_2_line_ang - M_PI*0.5 );
//     else
//         return rad2Deg( sight_2_line_ang + M_PI*0.5 );
// }

// double
// VisualSenderPlayerV1::calcUnQuantDist ( const PObject & obj ) const
// {
//     return self().pos().distance( obj.pos );
// }

// double
// VisualSenderPlayerV1::calcQuantDist( const double & dist,
//                                      const double & qstep ) const
// {
//     return Quantize( std::exp( Quantize( std::log( dist + EPS ), qstep ) ), 0.1 );
// }

// double
// VisualSenderPlayerV1::calcLineDist( const double & sight_2_line_ang,
//                                     const double & player_2_line,
//                                     const double & qstep ) const
// {
//     return Quantize( std::exp( Quantize( std::log( std::fabs( player_2_line
//                                                               / std::cos( sight_2_line_ang ) )
//                                                    + EPS ),
//                                          qstep ) ), 0.1 );
// }

void
VisualSenderPlayerV1::calcVel( const PVector & obj_vel,
                               const PVector & obj_pos,
                               const double & un_quant_dist,
                               const double & quant_dist,
                               double& dist_chg,
                               double& dir_chg ) const
{
    if ( un_quant_dist != 0.0 )
    {
        double qstep_dir = self().dirQStep();

        PVector vtmp = obj_vel - self().vel();
        PVector etmp = obj_pos - self().pos();
        etmp /= un_quant_dist;

        dist_chg = vtmp.x * etmp.x + vtmp.y * etmp.y;
        dir_chg = RAD2DEG * ( vtmp.y * etmp.x
                              - vtmp.x * etmp.y ) / un_quant_dist;

        dir_chg = ( dir_chg == 0.0
                    ? 0.0
                    : Quantize( dir_chg, qstep_dir ) );
        dist_chg = ( quant_dist
                     * Quantize( dist_chg
                                 / un_quant_dist, 0.02 ) );
    }
    else
    {
        dir_chg = 0.0;
        dist_chg = 0.0;
    }
}

// bool
// VisualSenderPlayerV1::decide( const double & prob )
// {
//     if ( prob >= 1.0 )
//         return true;
//     if ( prob <= 0.0 )
//         return false;
//     return boost::bernoulli_distribution<>( prob )( random::DefaultRNG::instance() );
// }

void
VisualSenderPlayerV1::serializePlayer( const Player&,
                                       const std::string & name,
                                       const double & dist,
                                       const int dir,
                                       const double & dist_chg,
                                       const double & dir_chg )
{
    serializer().serializeVisualObject( transport(),
                                        name,
                                        dist, dir,
                                        dist_chg, dir_chg );
}

void
VisualSenderPlayerV1::serializePlayer( const Player&,
                                       const std::string & name,
                                       const double & dist,
                                       const int dir )
{
    serializer().serializeVisualObject( transport(),
                                        name,
                                        dist,
                                        dir );
}

const
std::string &
VisualSenderPlayerV1::calcName( const PObject & obj ) const
{
    return obj.name();
}

const
std::string &
VisualSenderPlayerV1::calcCloseName( const PObject & obj ) const
{
    return obj.closeName();
}

const
std::string &
VisualSenderPlayerV1::calcTFarName( const Player & obj ) const
{
    return obj.nameTooFar();
}

const
std::string &
VisualSenderPlayerV1::calcUFarName( const Player & obj ) const
{
    return obj.nameFar();
}

/*!
//===================================================================
//
//  CLASS: VisualSenderPlayerV4
//
//  DESC: Class for the version 4 visual protocol.  This version
//        introduced body directions of players. Everything else is
//        the same.
//
//===================================================================
*/

VisualSenderPlayerV4::VisualSenderPlayerV4( const Params & params )
    : VisualSenderPlayerV1( params )
{}

VisualSenderPlayerV4::~VisualSenderPlayerV4()
{}

void
VisualSenderPlayerV4::serializePlayer( const Player & player,
                                       const double & dist,
                                       const int dir,
                                       const double & dist_chg,
                                       const double & dir_chg )
{
    serializer().serializeVisualObject( transport(),
                                        calcName( player ),
                                        dist, dir, dist_chg, dir_chg,
                                        calcBodyDir( player ) );
}

int
VisualSenderPlayerV4::calcBodyDir( const Player & player ) const
{
    return rad2Deg( normalize_angle( player.angleBodyCommitted()
                                     - self().angleBodyCommitted()
                                     - self().angleNeckCommitted() ) );
}

/*!
//===================================================================
//
//  CLASS: VisualSensorPlayerV5
//
//  DESC: Class for the version 5 visual protocol.  This version
//        introduced head directions of players. Everything else is
//        the same
//
//===================================================================
*/


VisualSenderPlayerV5::VisualSenderPlayerV5( const Params & params )
    : VisualSenderPlayerV4( params )
{}


VisualSenderPlayerV5::~VisualSenderPlayerV5()
{}

void
VisualSenderPlayerV5::serializePlayer( const Player & player,
                                       const double & dist,
                                       const int dir,
                                       const double & dist_chg,
                                       const double & dir_chg )
{
    serializer().serializeVisualObject( transport(),
                                        calcName( player ),
                                        dist, dir, dist_chg, dir_chg,
                                        calcBodyDir( player ),
                                        calcHeadDir( player ) );
}

int
VisualSenderPlayerV5::calcHeadDir( const Player & player ) const
{
    return rad2Deg( normalize_angle( player.angleNeckCommitted()
                                     + player.angleBodyCommitted()
                                     - self().angleBodyCommitted()
                                     - self().angleNeckCommitted() ) );
}

/*!
//===================================================================
//
//  CLASS: VisualSensorPlayerV6
//
//  DESC: Class for the version 6 visual protocol.  This version
//        introduced shortened names for all the objects meaning the
//        name calculation must be redefined.
//
//===================================================================
*/

VisualSenderPlayerV6::VisualSenderPlayerV6( const Params & params )
    : VisualSenderPlayerV5( params )
{}

VisualSenderPlayerV6::~VisualSenderPlayerV6()
{}

const
std::string &
VisualSenderPlayerV6::calcName( const PObject & obj ) const
{
    return obj.shortName();
}

const
std::string &
VisualSenderPlayerV6::calcCloseName( const PObject & obj ) const
{
    return obj.shortCloseName();
}


const
std::string &
VisualSenderPlayerV6::calcUFarName( const Player & obj ) const
{
    return obj.shortNameFar();
}

const
std::string &
VisualSenderPlayerV6::calcTFarName( const Player & obj ) const
{
    return obj.shortNameTooFar();
}

/*!
//===================================================================
//
//  CLASS: VisualSensorPlayerV7
//
//  DESC: Class for the version 7 visual protocol.  This version
//        fixed a bug in the generation of directions in that they
//        were truncated to int, rather than rounded.  This meant
//        error in the direction pointed was at most times between
//        -0.5 and +0.5 degrees, but occationally between -1.0 and
//        +1.0 degrees and at other times exact.  With the new method
//        of rounding, the error is always between -0.5 and +0.5.
//
//===================================================================
*/

VisualSenderPlayerV7::VisualSenderPlayerV7( const Params& params )
    : VisualSenderPlayerV6( params )
{}

VisualSenderPlayerV7::~VisualSenderPlayerV7()
{}


/*!
//===================================================================
//
//  CLASS: VisualSensorPlayerV8
//
//  DESC: Class for the version 8 visual protocol.  This version
//        introduced observation of the a new arm feature, that
//        allows plays to point to different spots, however, on
//        direction the arm is pointing to is visable, not the
//        distance to the spot the arm is pointing to.
//
//===================================================================
*/

VisualSenderPlayerV8::VisualSenderPlayerV8( const Params& params )
    : VisualSenderPlayerV7( params )
{}

VisualSenderPlayerV8::~VisualSenderPlayerV8()
{}

int
VisualSenderPlayerV8::calcPointDir( const Player & player )
{
    double dir = 0.0;
    if ( player.getArmDir( dir ) )
    {
        dir += player.angleNeckCommitted()
            + player.angleBodyCommitted()
            - self().angleBodyCommitted()
            - self().angleNeckCommitted();

        // add random noise

        // With a normal distribution, 95% of the values are within 2
        // std deviations of the mean, so if we set sigma to 1/2 our
        // desired range, then 95% of values will be in that range.
        // NOTE: This means that 5% of the time it will be outside
        // of this range.
        double sigma = self().pos().distance( player.pos() )
            / 60.0; // this should be replaced by the line below.
        //        / ServerParam::instance().getTeamTooFarLength ();
        sigma = std::pow( sigma, 4 ); // 4 should be parameterized
        // sigma is now a range between 0 and 1.0

        double min_noise_level = Deg2Rad( 2.5 ) * 0.5;
        // 5.0 should be parameterized.  It is halved for the same
        // reason we want sigma to be half the desired range
        sigma *= M_PI - min_noise_level;
        sigma += min_noise_level;

        //sigma is now in a range of 2.5 to 180 degrees, dependant on
        //the distance of the player.  95% of the returned random values
        //will be within +- 2*sigma of dir
        boost::normal_distribution<> rng( dir, sigma );
        boost::variate_generator< rcss::random::DefaultRNG &,
            boost::normal_distribution<> >
            gen( rcss::random::DefaultRNG::instance(), rng );

        return rad2Deg( normalize_angle( gen() ) );
    }
    else
    {
        return 0;
    }
}

void
VisualSenderPlayerV8::serializePlayer( const Player & player,
                                       const std::string & name,
                                       const double & dist,
                                       const int dir,
                                       const double & dist_chg,
                                       const double & dir_chg )
{
    if ( player.getArm().isPointing() )
    {
        int point_dir = calcPointDir( player );
        serializer().serializeVisualObject( transport(),
                                            name,
                                            dist, dir,
                                            dist_chg, dir_chg,
                                            calcBodyDir( player ),
                                            calcHeadDir( player ),
                                            point_dir,
                                            player.isTackling() );
    }
    else
    {
        serializer().serializeVisualObject( transport(),
                                            name,
                                            dist, dir,
                                            dist_chg, dir_chg,
                                            calcBodyDir( player ),
                                            calcHeadDir( player ),
                                            player.isTackling() );
    }
}

void
VisualSenderPlayerV8::serializePlayer( const Player & player,
                                       const std::string & name,
                                       const double & dist,
                                       const int dir )
{
    if ( player.getArm().isPointing() )
    {
        int point_dir = calcPointDir( player );
        serializer().serializeVisualObject( transport(),
                                            name,
                                            dist,
                                            dir,
                                            point_dir,
                                            player.isTackling() );
    }
    else
    {
        serializer().serializeVisualObject( transport(),
                                            name,
                                            dist,
                                            dir,
                                            player.isTackling() );
    }
}

/*!
//===================================================================
//
//  Register senders for different versions
//
//===================================================================
*/

namespace visual
{
template< typename Sender >
std::auto_ptr< VisualSenderPlayer >
create( const VisualSenderPlayer::Params& params )
{ return std::auto_ptr< VisualSenderPlayer >( new Sender( params ) ); }

lib::RegHolder vp1 = VisualSenderPlayer::factory().autoReg( &create< VisualSenderPlayerV1 >, 1 );
lib::RegHolder vp2 = VisualSenderPlayer::factory().autoReg( &create< VisualSenderPlayerV1 >, 2 );
lib::RegHolder vp3 = VisualSenderPlayer::factory().autoReg( &create< VisualSenderPlayerV1 >, 3 );
lib::RegHolder vp4 = VisualSenderPlayer::factory().autoReg( &create< VisualSenderPlayerV4 >, 4 );
lib::RegHolder vp5 = VisualSenderPlayer::factory().autoReg( &create< VisualSenderPlayerV5 >, 5 );
lib::RegHolder vp6 = VisualSenderPlayer::factory().autoReg( &create< VisualSenderPlayerV6 >, 6 );
lib::RegHolder vp7 = VisualSenderPlayer::factory().autoReg( &create< VisualSenderPlayerV7 >, 7 );
lib::RegHolder vp8 = VisualSenderPlayer::factory().autoReg( &create< VisualSenderPlayerV8 >, 8 );
lib::RegHolder vp9 = VisualSenderPlayer::factory().autoReg( &create< VisualSenderPlayerV8 >, 9 );
lib::RegHolder vp10 = VisualSenderPlayer::factory().autoReg( &create< VisualSenderPlayerV8 >, 10 );
lib::RegHolder vp11 = VisualSenderPlayer::factory().autoReg( &create< VisualSenderPlayerV8 >, 11 );
}
}
