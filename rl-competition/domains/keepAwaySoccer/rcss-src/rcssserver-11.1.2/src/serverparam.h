/* -*- Mode: C++ -*- */

/*
 *Copyright:

    Copyright (C) 1996-2000 Electrotechnical Laboratory.
    	Itsuki Noda, Yasuo Kuniyoshi and Hitoshi Matsubara.
    Copyright (C) 2000, 2001 RoboCup Soccer Server Maintainance Group.
    	Patrick Riley, Tom Howard, Daniel Polani, Itsuki Noda,
	Mikhail Prokopenko, Jan Wendler

    This file is a part of SoccerServer.

    This code is free software; you can redistribute it and/or
    modify it under the terms of the GNU Lesser General Public
    License as published by the Free Software Foundation; either
    version 2.1 of the License, or (at your option) any later version.

    This library is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
    Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public
    License along with this library; if not, write to the Free Software
    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 *EndCopyright:
 */


#ifndef _SERVER_PARAM_H_
#define _SERVER_PARAM_H_

#include "rcssserverconfig.hpp"

#include "utility.h"
#include "types.h"
#include <rcssbase/net/udpsocket.hpp>

#include <boost/shared_ptr.hpp>
#include <map>

#define _USE_MATH_DEFINES
#include <cmath>

namespace rcss
{
    namespace conf
    {
	class Parser;
	class Builder;
	class StreamStatusHandler;
    }
}

class ServerParam
//    : /*public ParamReader,*/
//      public rcss::conf::Builder
{
protected:

	RCSSSERVER_API
    ServerParam ( const std::string& progname );
private:

    ServerParam( const ServerParam& ); // not used

    ServerParam&
    operator=( const ServerParam& ); // not used

protected:

public:

    class Printer
    {
    private:
        std::ostream& M_out;
        unsigned int M_version;
    public:
        Printer( std::ostream& out, unsigned int version );

        void
        operator()( const std::pair< const std::string, unsigned int > item );
    };

	RCSSSERVER_API
  static
  ServerParam&
  instance( const std::string& progname );

	RCSSSERVER_API
  static
  ServerParam&
  instance();

	RCSSSERVER_API
  static
  bool
  init( const int& argc, const char * const *argv );

private:
    boost::shared_ptr< rcss::conf::Builder > m_builder;
    boost::shared_ptr< rcss::conf::Parser > m_conf_parser;
    boost::shared_ptr< rcss::conf::StreamStatusHandler > m_err_handler;

public:


  virtual ~ServerParam ();

  server_params_t convertToStruct ();



public:
    typedef std::map< std::string, unsigned int > VerMap;

//   virtual void getOptions(const int& argc, const char * const *argv );
//   virtual void writeConfig ( std::ostream& o );

private:
  virtual
  void
  setDefaults();

//   virtual
//   void
//   createMaps();

    template< typename P >
    void
    addParam(  const std::string& name,
               P& param,
               const std::string& desc,
               int version );

    template< typename S, typename G >
    void
    addParam(  const std::string& name,
               const S& setter,
               const G& getter,
               const std::string& desc,
               int version );

    void
    addParams();

private:
    VerMap m_ver_map;

public:
    const VerMap&
    verMap() const
    { return m_ver_map; }

		Value gwidth ;					/* goal width */
		Value inertia_moment ;			/* intertia moment for turn */
		Value psize ;					/* player size */
		Value pdecay ;					/* player decay */
		Value prand ;					/* player rand */
		Value pweight ;					/* player weight */
		Value pspeed_max ;				/* player speed max */
		// th 6.3.00
		Value paccel_max ;				/* player acceleration max */
		//
		Value stamina_max ;				/* player stamina max */
		Value stamina_inc ;				/* player stamina inc */
		Value recover_init ;			/* player recovery init */
		Value recover_dthr ;			/* player recovery decriment threshold */
		Value recover_min ;				/* player recovery min */
		Value recover_dec ;				/* player recovery decriment */
		Value effort_init ;				/* player dash effort init */
		Value effort_dthr ;				/* player dash effort decriment threshold */
		Value effort_min ;				/* player dash effrot min */
		Value effort_dec ;				/* player dash effort decriment */
		Value effort_ithr ;				/* player dash effort incriment threshold */
		Value effort_inc ;				/* player dash effort incriment */
		// pfr 8/14/00: for RC2000 evaluation
		Value kick_rand;                                /* noise added directly to kicks */
		bool team_actuator_noise;                        /* flag whether to use team specific actuator noise */
		Value prand_factor_l;                           /* factor to multiple prand for left team */
		Value prand_factor_r;                           /* factor to multiple prand for right team */
		Value kick_rand_factor_l;                       /* factor to multiple kick_rand for left team */
		Value kick_rand_factor_r;                       /* factor to multiple kick_rand for right team */

		Value bsize ;					/* ball size */
		Value bdecay ;					/* ball decay */
		Value brand ;					/* ball rand */
		Value bweight ;					/* ball weight */
		Value bspeed_max ;				/* ball speed max */
		// th 6.3.00
		Value baccel_max;				/* ball acceleration max */
		//
		Value dprate ;					/* dash power rate */
		Value kprate ;					/* kick power rate */
		Value kmargin ;					/* kickable margin */
		Value ctlradius ;				/* control radius */

private:
		Value ctlradius_width ;			/* (control radius) - (plyaer size) */

    void
    setCTLRadius( double value )
    {
        ctlradius_width -= ctlradius;
        ctlradius = value;
        ctlradius_width += ctlradius;
    }
public:
    Value
    CtlRadiusWidth() const
    { return ctlradius_width; }

		Value maxp ;					/* max power */
		Value minp ;					/* min power */
		Value maxm ;					/* max moment */
		Value minm ;					/* min moment */
		Value maxnm ;					/* max neck moment */
		Value minnm ;					/* min neck moment */
		Value maxn ;					/* max neck angle */
		Value minn ;					/* min neck angle */
private:
		Value visangle ;				/* visible angle */
public:
		Value visdist ;					/* visible distance */
		Angle windir ;					/* wind direction */
		Value winforce ;				/* wind force */
		Value winang ;					/* wind angle for rand */
		Value winrand ;					/* wind force for force */

private:
		Value kickable_area ;			/* kickable_area */

    void
    setKickMargin( double value )
    {
        kickable_area -= kmargin;
        kmargin = value;
        kickable_area += kmargin;
    }

    void
    setBallSize( double value )
    {
        kickable_area -= bsize;
        bsize = value;
        kickable_area += bsize;
    }

    void
    setPlayerSize( double value )
    {
        ctlradius_width += psize;
        kickable_area -= psize;
        psize = value;
        kickable_area += psize;
        ctlradius_width -= psize;
    }

public:
    Value
    kickableArea() const
    { return kickable_area; }

		Value catch_area_l ;			/* goalie catchable area length */
		Value catch_area_w ;			/* goalie catchable area width */
		Value catch_prob ;				/* goalie catchable possibility */
                int   goalie_max_moves;                 /* goalie max moves after a catch */
                bool kaway ;                         /* keepaway mode on/off */
                Value ka_length ;                    /* keepaway region length */
                Value ka_width ;                     /* keepaway region width */
		Value ckmargin ;				/* corner kick margin */
		Value offside_area ;			/* offside active area size */
		bool win_no ;					/* wind factor is none */
		bool win_random ;				/* wind factor is random */
		int portnum ;					/* port number */
		int coach_pnum ;				/* coach port number */
		int olcoach_pnum ;				/* online coach port number */
		int say_cnt_max ;				/* max count of coach SAY (freeform) */
		int SayCoachMsgSize ;				/* max length of coach SAY (freeform) */
		int clang_win_size;                         /* coach language: time window size */
		int clang_define_win;                       /* coach language: define messages per window */
		int clang_meta_win;                         /* coach language: meta messages per window */
		int clang_advice_win;                       /* coach language: advice messages per window */
		int clang_info_win;                         /* coach language: info messages per window */
		int clang_mess_delay;                       /* coach language: delay between receive and send */
		int clang_mess_per_cycle;                   /* coach language: number of messages to flush per cycle */

private:
		int half_time ;					/* half time */

    double
    getHalfTimeScaler() const
	{
	    double value = ((1000.0 / (sim_st / slow_down_factor)));
	    if( value != 0.0 )
		return value;
	    else
		return EPS;
	}

    //Have to be careful with integer math, see bug # 800540
    void
    setHalfTime( int value )
    {
        half_time = (int)( value * getHalfTimeScaler() + 0.5);
    }

    //Have to be careful with integer math, see bug # 800540
    int
    getRawHalfTime() const
    {
        return (int)(half_time / getHalfTimeScaler() + 0.5);
    }
public:
    int
    halfTime() const
    { return half_time; }

		int drop_time;                                  /* cycles for dropping
																											 the ball after a free kick,
																											 corner kick message and
																											 noone kicking the ball.
																											 0 means don't drop
																											 automatically  */
                int nr_normal_halfs;    /* nr of normal halfs: default 2)      */
                int nr_extra_halfs;     /* nr of extra halfs: -1 is infinite)  */
                bool penalty_shoot_outs;/* penalty shoot outs after extra halfs*/

                int pen_before_setup_wait;/* cycles waited before penalty setup*/
                int pen_setup_wait;       /* cycles waited to setup penalty    */
                int pen_ready_wait;       /* cycles waited to take penalty     */
                int pen_taken_wait;       /* cycles waited to finish penalty   */
                int pen_nr_kicks;         /* number of penalty kicks           */
                int pen_max_extra_kicks;  /* max. nr of extra penalty kicks    */
                double pen_dist_x;        /* distance from goal to place ball  */
                bool pen_random_winner;   /* random winner when draw?          */
                bool pen_allow_mult_kicks;/* can attacker kick mult. times */
                double pen_max_goalie_dist_x;/*max distance from goal for goalie*/
                bool pen_coach_moves_players; /*coach moves players when positioned wrongly */


private:
		int sim_st ;					/* simulator step interval msec */
		int sb_step ;					/* sense_body interval step msec */
		int sv_st ;					/* online coach's look interval step */
		int send_st ;					/* udp send step interval msec */
		int lcm_st ;		                        /* lcm of all the above steps msec */

    void
    setSimStep( int value )
    {
        half_time = (int)( half_time / getHalfTimeScaler() + 0.5);
        sim_st = value * slow_down_factor;
        half_time = (int)( half_time * getHalfTimeScaler() + 0.5);
    }

    int
    getRawSimStep() const
    {
        return sim_st / slow_down_factor;
    }

    void
    setSenseBodyStep( int value )
    {
        sb_step = value * slow_down_factor;
    }

    int
    getRawSenseBodyStep() const
    {
        return sb_step / slow_down_factor;
    }

    void
    setCoachVisualStep( int value )
    {
        sv_st = value * slow_down_factor;
    }

    int
    getRawCoachVisualStep() const
    {
        return sv_st / slow_down_factor;
    }
    void
    setSendStep( int value )
    {
        send_st = value * slow_down_factor;
    }

    int
    getRawSendStep() const
    {
        return send_st / slow_down_factor;
    }

    void
    setSynchOffset( int value )
    {
        synch_offset = value * slow_down_factor;
    }

    int
    getRawSynchOffset() const
    {
        return synch_offset / slow_down_factor;
    }

    void
    setSynchMode( bool value );

    void setTeamLeftStart( std::string start );
    void setTeamRightStart( std::string start );

public:
    void
    clear();

 private:
    void
    setSlowDownFactor( int value )
    {
        if ( value <= 0 )
        {
            return;
        }
        // undo the effect of the last slow_down_factor
        sim_st /= slow_down_factor;
        sb_step /= slow_down_factor;
        sv_st /= slow_down_factor;
        send_st /= slow_down_factor;
        synch_offset /= slow_down_factor;

        // set the slow_down_factor
        slow_down_factor = value;

        // apply the slow_down_factor to all the depandant varaibles
        sim_st *= slow_down_factor;
        sb_step *= slow_down_factor;
        sv_st *= slow_down_factor;
        send_st *= slow_down_factor;
        synch_offset *= slow_down_factor;
        lcm_st = lcm( sim_st,
                      lcm( send_st,
                           lcm( recv_st,
                                lcm( sb_step,
                                     lcm( sv_st,
                                          ( synch_mode ? synch_offset : 1 ) ) ) ) ) );
    }

public:

    int
    simStep() const
    { return sim_st; }

    int
    senseBodyStep() const
    { return sb_step; }

    int
    coachVisualStep() const
    { return sv_st; }

    int
    sendStep() const
    { return send_st; }

    int
    lcmStep() const
    { return lcm_st; }

		int recv_st ;					/* udp recv step interval msec */
		int cban_cycle ;				/* goalie catch ban cycle */
		int slow_down_factor ;                          /* factor to slow down simulator and send intervals */
		bool useoffside ;				/* flag for using off side rule */
		bool kickoffoffside ;			/* flag for permit kick off offside */
		Value offside_kick_margin ;		/* offside kick margin */
		Value audio_dist ;				/* audio cut off distance */
		Value dist_qstep ;				/* quantize step of distance */
		Value land_qstep ;				/* quantize step of distance for landmark */
#ifdef NEW_QSTEP
		Value dir_qstep ;				/* quantize step of direction */
private:
		Value dist_qstep_l ;			/* team right quantize step of distance */
		Value dist_qstep_r ;			/* team left quantize step of distance */
		Value land_qstep_l ;			/* team right quantize step of distance for landmark */
		Value land_qstep_r ;			/* team left quantize step of distance for landmark */
		Value dir_qstep_l ;				/* team left quantize step of direction */
		Value dir_qstep_r ;				/* team right quantize step of direction */

public:
    Value
    distQStepL() const
    {
        if( !defined_qstep_l )
            return = dist_qstep;
        else
            return dist_qstep_l;
    }

    Value
    distQStepR() const
    {
        if( !defined_qstep_r )
            return = dist_qstep;
        else
            return dist_qstep_r;
    }

    Value
    landQStepL() const
    {
        if( !defined_qstep_l_l )
            return = land_qstep;
        else
            return land_qstep_l;
    }

    Value
    landQStepR() const
    {
        if( !defined_qstep_r_l )
            return = land_qstep;
        else
            return land_qstep_r;
    }

    Value
    dirQStepL() const
    {
        if( !defined_qstep_dir_l )
            return = dir_qstep;
        else
            return dir_qstep_l;
    }

    Value
    dirQStepR() const
    {
        if( !defined_qstep_dir_r )
            return = dir_qstep;
        else
            return dir_qstep_r;
    }

#endif
		bool	verbose ;					/* flag for verbose mode */

		//    char logfile[MAX_FILE_LEN] ;	/* server log file name */
		//   char recfile[MAX_FILE_LEN] ;	/* log file name for recording */
		//  int  rec_ver ;					/* version for recording */
		//  int  rec_log ;					/* flag for log recording */
		//  int  rec_msg_mode ;				/* flag for whether to record msg_mode structures */
		//   int  send_log ;					/* flag for log sending */
		// th 6.3.00
		//  int  log_times ;				/* flag for logging server cycle times in the log file */
		//
//  		string replay ;		/* log file name for replay */

		bool CoachMode ;				/* coach mode */
		bool CwRMode ;					/* coach with referee mode */
		bool old_hear ;					/* old format for hear command (coach) */

		bool synch_mode;                               /* pfr:SYNCH whether to run in synchronized mode */
    bool timer_loaded;
private:
		int synch_offset;                        /* pfr:SYNCH the offset from the start of the cycle to tell players to run */

public:
    int
    synchOffset() const
    { return synch_offset * slow_down_factor; }

		int synch_micro_sleep;                   /* pfr:SYNCH the # of microseconds to sleep while waiting for players */

		int start_goal_l;                           /* The starting score of the left team */
		int start_goal_r;                           /* The starting score of the right team */

		bool fullstate_l;                             /* flag to send fullstate messages to left team; supresses visual info */
		bool fullstate_r;                             /* flag to send fullstate messages to right team; supresses visual info */

		double slowness_on_top_for_left_team; /* Kinda self explanatory */
		double slowness_on_top_for_right_team; /* ditto */


private:
		std::string landmark_file;

		bool send_comms;
		bool text_logging;
		bool game_logging;
		int game_log_version;
		std::string text_log_dir;
		std::string game_log_dir;
		std::string text_log_fixed_name;
		std::string game_log_fixed_name;
		bool text_log_fixed;
		bool game_log_fixed;
		bool text_log_dated;
		bool game_log_dated;
		std::string log_date_format;
		bool log_times;
		bool record_messages;
		int text_log_compression;
		int game_log_compression;
		bool M_profile;

  bool kaway_logging;
  std::string kaway_log_dir;


  std::string kaway_log_fixed_name;
  bool kaway_log_fixed;
  bool kaway_log_dated;

  int kaway_start;

    void
    setTextLogDir( const std::string& str )
    {
        text_log_dir = tildeExpand( str );
    }

    void
    setGameLogDir( const std::string& str )
    {
        game_log_dir = tildeExpand( str );
    }

    void
    setKAwayLogDir( const std::string& str )
    {
        kaway_log_dir = tildeExpand( str );
    }


  int M_point_to_ban;
  int M_point_to_duration;

  int M_say_msg_size;			/* string size of say message */
  int M_hear_max;					/* player hear_capacity_max */
  int M_hear_inc;					/* player hear_capacity_inc */
  int M_hear_decay;				/* player hear_capacity_decay */

  double M_tackle_dist;
  double M_tackle_back_dist;
  double M_tackle_width;
  double M_tackle_exponent;
  int M_tackle_cycles;
  double M_tackle_power_rate;

  int M_freeform_wait_period;
  int M_freeform_send_period;

  bool M_free_kick_faults;
  bool M_back_passes;

  bool M_proper_goal_kicks;
  double M_stopped_ball_vel;
  int M_max_goal_kicks;

  int M_clang_del_win;
  int M_clang_rule_win;

  bool M_auto_mode;
  int M_kick_off_wait;
  int M_connect_wait;
  int M_game_over_wait;

  std::string M_team_l_start;
  std::string M_team_r_start;

    double M_ball_stuck_area; /* threshold distance checked by BallStuckRef */

    std::string M_coach_msg_file; /* file name that contains the messages sent to coaches */


  static const char LANDMARK_FILE[];
  static const char SERVER_CONF[];
  static const char OLD_SERVER_CONF[];

  static const int SEND_COMMS;
  static const int TEXT_LOGGING;
  static const int GAME_LOGGING;
  static const int GAME_LOG_VERSION;
  static const char TEXT_LOG_DIR[];
  static const char GAME_LOG_DIR[];
  static const char TEXT_LOG_FIXED_NAME[];
  static const char GAME_LOG_FIXED_NAME[];
  static const int TEXT_LOG_FIXED;
  static const int GAME_LOG_FIXED;
  static const int TEXT_LOG_DATED;
  static const int GAME_LOG_DATED;
  static const char LOG_DATE_FORMAT[];
  static const int LOG_TIMES;
  static const int RECORD_MESSAGES;
  static const int TEXT_LOG_COMPRESSION;
  static const int GAME_LOG_COMPRESSION;
  static const bool PROFILE;

  static const int KAWAY_LOGGING;
  static const char KAWAY_LOG_DIR[];
  static const char KAWAY_LOG_FIXED_NAME[];
  static const int KAWAY_LOG_FIXED;
  static const int KAWAY_LOG_DATED;

  static const int KAWAY_START;

  static const int POINT_TO_BAN;
  static const int POINT_TO_DURATION;

  static const unsigned int SAY_MSG_SIZE;
  static const unsigned int HEAR_MAX;
  static const unsigned int HEAR_INC;
  static const unsigned int HEAR_DECAY;

  static const double TACKLE_DIST;
  static const double TACKLE_BACK_DIST;
  static const double TACKLE_WIDTH;
  static const double TACKLE_EXPONENT;
  static const unsigned int TACKLE_CYCLES;
  static const double TACKLE_POWER_RATE;

  static const int NR_NORMAL_HALFS;
  static const int NR_EXTRA_HALFS;
  static const bool PENALTY_SHOOT_OUTS;

  static const int    PEN_BEFORE_SETUP_WAIT;
  static const int    PEN_SETUP_WAIT;
  static const int    PEN_READY_WAIT;
  static const int    PEN_TAKEN_WAIT;
  static const int    PEN_NR_KICKS;
  static const int    PEN_MAX_EXTRA_KICKS;
  static const bool   PEN_RANDOM_WINNER;
  static const double PEN_DIST_X;
  static const double PEN_MAX_GOALIE_DIST_X;
  static const bool   PEN_ALLOW_MULT_KICKS;
  static const bool   PEN_COACH_MOVES_PLAYERS;


  static const unsigned int FREEFORM_WAIT_PERIOD;
  static const unsigned int FREEFORM_SEND_PERIOD;

  static const bool FREE_KICK_FAULTS;
  static const bool BACK_PASSES;

  static const bool PROPER_GOAL_KICKS;
  static const double STOPPED_BALL_VEL;
  static const int MAX_GOAL_KICKS;

  static const int CLANG_DEL_WIN;
  static const int CLANG_RULE_WIN;

  static const bool S_AUTO_MODE;
  static const int S_KICK_OFF_WAIT;
  static const int S_CONNECT_WAIT;
  static const int S_GAME_OVER_WAIT;

  static const char S_TEAM_L_START[];
  static const char S_TEAM_R_START[];

    static const char S_MODULE_DIR[];

    static const double BALL_STUCK_AREA;
public:

    std::string
    landmarkFile() const { return landmark_file; }

		int sendComms() const { return send_comms; }
		int textLogging() const { return text_logging; }
		int gameLogging() const { return game_logging; }
		int gameLogVersion() const { return game_log_version; }
		std::string textLogDir() const { return tildeExpand( text_log_dir ); }
		std::string gameLogDir() const { return tildeExpand( game_log_dir ); }
		std::string textLogFixedName() const { return text_log_fixed_name; }
		std::string gameLogFixedName() const { return game_log_fixed_name; }
		int textLogFixed() const { return text_log_fixed; }
		int gameLogFixed() const { return game_log_fixed; }
		int textLogDated() const { return text_log_dated; }
		int gameLogDated() const { return game_log_dated; }
		std::string logDateFormat() const { return log_date_format; }
		int logTimes() const { return log_times; }
		int recordMessages() const { return record_messages; }
		int textLogCompression() const { return text_log_compression; }
		int gameLogCompression() const { return game_log_compression; }

		bool profile() const { return M_profile; }

  int kawayLogging() const { return kaway_logging; }
  std::string kawayLogDir() const { return tildeExpand( kaway_log_dir ); }
  std::string kawayLogFixedName() const { return kaway_log_fixed_name; }
  int kawayLogFixed() const { return kaway_log_fixed; }
  int kawayLogDated() const { return kaway_log_dated; }

  int kawayStart() const { return kaway_start; }

  unsigned int
  pointToBan() const
  { return (unsigned int)M_point_to_ban; }

  unsigned int
  pointToDuration() const
  { return (unsigned int)M_point_to_duration; }

  unsigned int
  sayMsgSize() const
  { return (unsigned int)M_say_msg_size; }

  unsigned int
  hearMax() const
  { return (unsigned int)M_hear_max; }

  unsigned int
  hearInc() const
  { return (unsigned int)M_hear_inc; }

  unsigned int
  hearDecay() const
  { return (unsigned int)M_hear_decay; }

  double
  tackleDist() const
  { return M_tackle_dist; }

  double
  tackleBackDist() const
  { return M_tackle_back_dist; }

  double
  tackleWidth() const
  { return M_tackle_width; }

  double
  tackleExponent() const
  { return M_tackle_exponent; }

  unsigned int
  tackleCycles() const
  { return (unsigned int)M_tackle_cycles; }

  double
  tacklePowerRate() const
  { return M_tackle_power_rate; }


  unsigned int
  freeformWaitPeriod() const
  { return (unsigned int)M_freeform_wait_period; }

  unsigned int
  freeformSendPeriod() const
  { return (unsigned int)M_freeform_send_period; }

  double
  visAngle() const
  {
    return Deg2Rad(visangle);
  }

  double
  visAngleDeg() const
  { return visangle; }

  bool
  freeKickFaults() const
  { return M_free_kick_faults; }

  bool
  backPasses() const
  { return M_back_passes; }

  bool
  properGoalKicks() const
  { return M_proper_goal_kicks; }

  double
  stoppedBallVel() const
  { return M_stopped_ball_vel; }

  int
  maxGoalKicks() const
  { return M_max_goal_kicks; }

  int
  clangDelWin() const
  { return M_clang_del_win; }

	int
  clangRuleWin() const
  { return M_clang_rule_win; }

  bool
  autoMode() const
  { return M_auto_mode; }

  int
  kickOffWait() const
  { return M_kick_off_wait * sim_st / recv_st; }

  int
  connectWait() const
  { return M_connect_wait * sim_st / recv_st; }

  int
  gameOverWait() const
  { return M_game_over_wait; }

  std::string
  teamLeftStart() const
  { return M_team_l_start; }

  std::string
  teamRightStart() const
  { return M_team_r_start; }

    double
    goalPostRadius() const
    { return 0.06; }

    rcss::net::Addr::PortType
    playerPort() const
    { return portnum; }

    rcss::net::Addr::PortType
    offlineCoachPort() const
    { return coach_pnum; }

    rcss::net::Addr::PortType
    onlineCoachPort() const
    { return olcoach_pnum; }

    const double &
    ballStuckArea() const
      {
          return M_ball_stuck_area;
      }

    const
    std::string & coachMsgFile() const
      {
          return M_coach_msg_file;
      }

    bool
    getInt( const std::string& param, int& value ) const;

    bool
    getBool( const std::string& param, bool& value ) const;

    bool
    getDoub( const std::string& param, double& value ) const;

    bool
    getStr( const std::string& param, std::string& value ) const;


private:

  static
  bool s_in_init;

};

// class ServerParamSensor
// {
// public:
//     virtual
//     ~ServerParamSensor()
//       { }

// };

// class ServerParamSensor_v7
// 	: public ServerParamSensor
// {
// public:
// 	struct data_t
// 	{
// 		double M_gwidth;
// 		double M_inertia_moment ;
// 		double M_psize;
// 		double M_pdecay;
// 		double M_prand;
// 		double M_pweight;
// 		double M_pspeed_max;
// 		double M_paccel_max;
// 		double M_stamina_max;
// 		double M_stamina_inc;
// 		double M_recover_init;
// 		double M_recover_dthr;
// 		double M_recover_min;
// 		double M_recover_dec;
// 		double M_effort_init;
// 		double M_effort_dthr;
// 		double M_effort_min;
// 		double M_effort_dec;
// 		double M_effort_ithr;
// 		double M_effort_inc;
// 		double M_kick_rand;
// 		bool M_team_actuator_noise;
// 		double M_prand_factor_l;
// 		double M_prand_factor_r;
// 		double M_kick_rand_factor_l;
// 		double M_kick_rand_factor_r;
// 		double M_bsize;
// 		double M_bdecay;
// 		double M_brand;
// 		double M_bweight;
// 		double M_bspeed_max;
// 		double M_baccel_max;
// 		double M_dprate;
// 		double M_kprate;
// 		double M_kmargin;
// 		double M_ctlradius;
// 		double M_ctlradius_width;
// 		double M_maxp;
// 		double M_minp;
// 		double M_maxm;
// 		double M_minm;
// 		double M_maxnm;
// 		double M_minnm;
// 		double M_maxn;
// 		double M_minn;
// 		double M_visangle;
// 		double M_visdist;
// 		double M_windir;
// 		double M_winforce;
// 		double M_winang;
// 		double M_winrand;
// 		double M_kickable_area;
// 		double M_catch_area_l;
// 		double M_catch_area_w;
// 		double M_catch_prob;
// 		int M_goalie_max_moves;
// 		double M_ckmargin;
// 		double M_offside_area;
// 		bool M_win_no;
// 		bool M_win_random;
// 		int M_say_cnt_max;
// 		int M_SayCoachMsgSize;
// 		int M_clang_win_size;
// 		int M_clang_define_win;
// 		int M_clang_meta_win;
// 		int M_clang_advice_win;
// 		int M_clang_info_win;
// 		int M_clang_mess_delay;
// 		int M_clang_mess_per_cycle;
// 		int M_half_time;
// 		int M_sim_st;
// 		int M_send_st;
// 		int M_recv_st;
// 		int M_sb_step;
// 		int M_lcm_st;
// 		int M_say_msg_size;
// 		int M_hear_max;
// 		int M_hear_inc;
// 		int M_hear_decay;
// 		int M_cban_cycle;
// 		int M_slow_down_factor;
// 		bool M_useoffside;
// 		bool M_kickoffoffside;
// 		double M_offside_kick_margin;
// 		double M_audio_dist;
// 		double M_dist_qstep;
// 		double M_land_qstep;
// 		double M_dir_qstep;
// 		double M_dist_qstep_l;
// 		double M_dist_qstep_r;
// 		double M_land_qstep_l;
// 		double M_land_qstep_r;
// 		double M_dir_qstep_l;
// 		double M_dir_qstep_r;
// 		bool M_CoachMode;
// 		bool M_CwRMode;
// 		bool M_old_hear;
// 		int M_sv_st;
// 		int M_start_goal_l;
// 		int M_start_goal_r;
// 		bool M_fullstate_l;
// 		bool M_fullstate_r;
// 		int M_drop_time;



// 		data_t ( const ServerParam& sp )
// 			: M_gwidth ( sp.gwidth ),
// 				M_inertia_moment  ( sp.inertia_moment ),
// 				M_psize ( sp.psize ),
// 				M_pdecay ( sp.pdecay ),
// 				M_prand ( sp.prand ),
// 				M_pweight ( sp.pweight ),
// 				M_pspeed_max ( sp.pspeed_max ),
// 				M_paccel_max ( sp.paccel_max ),
// 				M_stamina_max ( sp.stamina_max ),
// 				M_stamina_inc ( sp.stamina_inc ),
// 				M_recover_init ( sp.recover_init ),
// 				M_recover_dthr ( sp.recover_dthr ),
// 				M_recover_min ( sp.recover_min ),
// 				M_recover_dec ( sp.recover_dec ),
// 				M_effort_init ( sp.effort_init ),
// 				M_effort_dthr ( sp.effort_dthr ),
// 				M_effort_min ( sp.effort_min ),
// 				M_effort_dec ( sp.effort_dec ),
// 				M_effort_ithr ( sp.effort_ithr ),
// 				M_effort_inc ( sp.effort_inc ),
// 				M_kick_rand ( sp.kick_rand ),
// 				M_team_actuator_noise ( sp.team_actuator_noise ),
// 				M_prand_factor_l ( sp.prand_factor_l ),
// 				M_prand_factor_r ( sp.prand_factor_r ),
// 				M_kick_rand_factor_l ( sp.kick_rand_factor_l ),
// 				M_kick_rand_factor_r ( sp.kick_rand_factor_r ),
// 				M_bsize ( sp.bsize ),
// 				M_bdecay ( sp.bdecay ),
// 				M_brand ( sp.brand ),
// 				M_bweight ( sp.bweight ),
// 				M_bspeed_max ( sp.bspeed_max ),
// 				M_baccel_max ( sp.baccel_max ),
// 				M_dprate ( sp.dprate ),
// 				M_kprate ( sp.kprate ),
// 				M_kmargin ( sp.kmargin ),
// 				M_ctlradius ( sp.ctlradius ),
// 				M_ctlradius_width ( sp.CtlRadiusWidth() ),
// 				M_maxp ( sp.maxp ),
// 				M_minp ( sp.minp ),
// 				M_maxm ( sp.maxm ),
// 				M_minm ( sp.minm ),
// 				M_maxnm ( sp.maxnm ),
// 				M_minnm ( sp.minnm ),
// 				M_maxn ( sp.maxn ),
// 				M_minn ( sp.minn ),
// 				M_visangle ( sp.visAngleDeg() ),
// 				M_visdist ( sp.visdist ),
// 				M_windir ( sp.windir ),
// 				M_winforce ( sp.winforce ),
// 				M_winang ( sp.winang ),
// 				M_winrand ( sp.winrand ),
// 				M_kickable_area ( sp.kickableArea() ),
// 				M_catch_area_l ( sp.catch_area_l ),
// 				M_catch_area_w ( sp.catch_area_w ),
// 				M_catch_prob ( sp.catch_prob ),
// 				M_goalie_max_moves ( sp.goalie_max_moves ),
// 				M_ckmargin ( sp.ckmargin  ),
// 				M_offside_area ( sp.offside_area ),
// 				M_win_no ( sp.win_no ),
// 				M_win_random ( sp.win_random ),
// 				M_say_cnt_max ( sp.say_cnt_max ),
// 				M_SayCoachMsgSize ( sp.SayCoachMsgSize ),
// 				M_clang_win_size ( sp.clang_win_size ),
// 				M_clang_define_win ( sp.clang_define_win ),
// 				M_clang_meta_win ( sp.clang_meta_win ),
// 				M_clang_advice_win ( sp.clang_advice_win ),
// 				M_clang_info_win ( sp.clang_info_win ),
// 				M_clang_mess_delay ( sp.clang_mess_delay ),
// 				M_clang_mess_per_cycle ( sp.clang_mess_per_cycle ),
// 				M_half_time ( sp.halfTime() ),
// 				M_sim_st ( sp.simStep() ),
// 				M_send_st ( sp.sendStep() ),
// 				M_recv_st ( sp.recv_st ),
// 				M_sb_step ( sp.senseBodyStep() ),
// 				M_lcm_st ( sp.lcmStep() ),
// 				M_say_msg_size ( sp.sayMsgSize() ),
// 				M_hear_max ( sp.hearMax() ),
// 				M_hear_inc ( sp.hearInc() ),
// 				M_hear_decay ( sp.hearDecay() ),
// 				M_cban_cycle ( sp.cban_cycle ),
// 				M_slow_down_factor ( sp.slow_down_factor ),
// 				M_useoffside ( sp.useoffside ),
// 				M_kickoffoffside ( sp.kickoffoffside ),
// 				M_offside_kick_margin ( sp.offside_kick_margin ),
// 				M_audio_dist ( sp.audio_dist ),
// 				M_dist_qstep ( sp.dist_qstep ),
// 				M_land_qstep ( sp.land_qstep ),
// #ifdef NEW_QSTEP
// 				M_dir_qstep ( sp.dir_qstep ),
// 				M_dist_qstep_l ( sp.dist_qstep_l ),
// 				M_dist_qstep_r ( sp.dist_qstep_r ),
// 				M_land_qstep_l ( sp.land_qstep_l ),
// 				M_land_qstep_r ( sp.land_qstep_r ),
// 				M_dir_qstep_l ( sp.dir_qstep_l ),
// 				M_dir_qstep_r ( sp.dir_qstep_r ),
// #else
// 				M_dir_qstep ( -1 ),
// 				M_dist_qstep_l ( -1 ),
// 				M_dist_qstep_r ( -1 ),
// 				M_land_qstep_l ( -1 ),
// 				M_land_qstep_r ( -1 ),
// 				M_dir_qstep_l ( -1 ),
// 				M_dir_qstep_r ( -1 ),
// #endif
// 				M_CoachMode ( sp.CoachMode ),
// 				M_CwRMode ( sp.CwRMode ),
// 				M_old_hear ( sp.old_hear ),
// 				M_sv_st ( sp.coachVisualStep() ),
// 				M_start_goal_l ( sp.start_goal_l ),
// 				M_start_goal_r ( sp.start_goal_r ),
// 				M_fullstate_l ( sp.fullstate_l ),
// 				M_fullstate_r ( sp.fullstate_r ),
// 				M_drop_time ( sp.drop_time )
// 			{
// 			}
// 	};

// 	virtual void send ( const ServerParamSensor_v7::data_t& data ) = 0;
// };

// std::ostream& toStr ( std::ostream& o, const ServerParamSensor_v7::data_t& data );

// class ServerParamSensor_v8
//  	: public ServerParamSensor_v7
// {
// public:
// 	struct data_t
// 	{
//         const ServerParam& m_sp;

//     data_t( const ServerParam& sp )
//         : m_sp( sp )
//     {}

// 	};

// 	virtual void send ( const ServerParamSensor_v8::data_t& data ) = 0;


// };

// std::ostream& toStr ( std::ostream& o, const ServerParamSensor_v8::data_t& data );

#endif
