// -*-c++-*-

/***************************************************************************
                                 serializer.h
                    Classes for serializing data to clients
                             -------------------
    begin                : 27-JAN-2002
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

#ifndef _SERIALIZER_H_
#define _SERIALIZER_H_

#include <string>
#include <iostream>
#include <map>
#include "types.h"
#include <rcssbase/lib/factory.hpp>

namespace rcss
{
namespace clang
{
class Msg;
}


class SerializerCommon
{
protected:
    SerializerCommon();

    virtual
    ~SerializerCommon();
public:
    typedef const SerializerCommon&(*Creator)();
    typedef rcss::lib::Factory< Creator, int > Factory;

    static
    Factory&
    factory();

    virtual
    void
    serializeServerParamBegin( std::ostream& ) const
      {}

    virtual
    void
    serializeServerParamEnd( std::ostream& ) const
      {}

    virtual
    void
    serializePlayerParamBegin( std::ostream& ) const
      {}

    virtual
    void
    serializePlayerParamEnd( std::ostream& ) const
      {}

    virtual
    void
    serializePlayerTypeBegin( std::ostream& ) const
      {}

    virtual
    void
    serializePlayerTypeEnd( std::ostream& ) const
      {}

    virtual
    void
    serializeParam( std::ostream&, int ) const
      {}

    virtual
    void
    serializeParam( std::ostream&, unsigned int ) const
      {}

    virtual
    void
    serializeParam( std::ostream&, bool ) const
      {}

    virtual
    void
    serializeParam( std::ostream&, double ) const
      {}

    virtual
    void
    serializeParam( std::ostream&, const std::string& ) const
      {}

    virtual
    void
    serializeParam( std::ostream&,
                    const std::string&,
                    int ) const
      {}

    virtual
    void
    serializeParam( std::ostream&,
                    const std::string&,
                    bool ) const
      {}

    virtual
    void
    serializeParam( std::ostream&,
                    const std::string&,
                    double ) const
      {}

    virtual
    void
    serializeParam( std::ostream&,
                    const std::string&,
                    const std::string& ) const
      {}
};


class Serializer
{
public:
    Serializer( const SerializerCommon& common )
        : m_common( common )
      {}


protected:
    const SerializerCommon&
    commonSerializer() const
      { return m_common; }

public:

    void
    serializeServerParamBegin( std::ostream& strm ) const
      { commonSerializer().serializeServerParamBegin( strm ); }

    void
    serializeServerParamEnd( std::ostream& strm ) const
      { commonSerializer().serializeServerParamEnd( strm ); }

    void
    serializePlayerParamBegin( std::ostream& strm ) const
      { commonSerializer().serializePlayerParamBegin( strm ); }

    void
    serializePlayerParamEnd( std::ostream& strm ) const
      { commonSerializer().serializePlayerParamEnd( strm ); }

    void
    serializePlayerTypeBegin( std::ostream& strm ) const
      { commonSerializer().serializePlayerTypeBegin( strm ); }

    void
    serializePlayerTypeEnd( std::ostream& strm ) const
      { commonSerializer().serializePlayerTypeEnd( strm ); }

    void
    serializeParam( std::ostream& strm, int value ) const
      { commonSerializer().serializeParam( strm, value ); }

    void
    serializeParam( std::ostream& strm, unsigned int value ) const
      { commonSerializer().serializeParam( strm, value ); }

    void
    serializeParam( std::ostream& strm, bool value ) const
      { commonSerializer().serializeParam( strm, value ); }

    void
    serializeParam( std::ostream& strm, double value ) const
      { commonSerializer().serializeParam( strm, value ); }

    void
    serializeParam( std::ostream& strm, const std::string& value ) const
      { commonSerializer().serializeParam( strm, value ); }

    void
    serializeParam( std::ostream& strm,
                    const std::string& name,
                    int value ) const
      { commonSerializer().serializeParam( strm, name, value ); }

    void
    serializeParam( std::ostream& strm,
                    const std::string& name,
                    bool value ) const
      { commonSerializer().serializeParam( strm, name, value ); }

    void
    serializeParam( std::ostream& strm,
                    const std::string& name,
                    double value ) const
      { commonSerializer().serializeParam( strm, name, value ); }

    void
    serializeParam( std::ostream& strm,
                    const std::string& name,
                    const std::string& value ) const
      { commonSerializer().serializeParam( strm, name, value ); }
private:
    const SerializerCommon& m_common;
};


class SerializerPlayer
    : public Serializer
{
public:
    typedef const SerializerPlayer*(*Creator)();
    typedef rcss::lib::Factory< Creator, int > Factory;

    static
    Factory&
    factory();

protected:
    SerializerPlayer( const SerializerCommon& common );

    virtual
    ~SerializerPlayer();
public:

    virtual
    void
    serializeRefAudio( std::ostream& strm,
                       const int& time,
                       const char* msg ) const = 0;
    virtual
    void
    serializeCoachAudio( std::ostream& strm,
                         const int& time,
                         const std::string& name,
                         const char* msg ) const = 0;
    virtual
    void
    serializeCoachStdAudio( std::ostream& strm,
                            const int& time,
                            const std::string& name,
                            const clang::Msg& msg ) const = 0;

    virtual
    void
    serializeSelfAudio( std::ostream& strm,
                        const int& time,
                        const char* msg ) const = 0;

    virtual
    void
    serializePlayerAudio( std::ostream& strm,
                          const int& time,
                          const double& dir,
                          const char* msg ) const = 0;

    virtual
    void
    serializeAllyAudioFull( std::ostream&,
                            const int,
                            const double,
                            const int,
                            const char* ) const
      {}

    virtual
    void
    serializeOppAudioFull( std::ostream&,
                           const int,
                           const double,
                           const char* ) const
      {}

    virtual
    void
    serializeAllyAudioShort( std::ostream&,
                             const int,
                             const int ) const
      {}

    virtual
    void
    serializeOppAudioShort( std::ostream&,
                            const int ) const
      {}


    virtual
    void
    serializeVisualBegin( std::ostream&,
                          int ) const
      {}

    virtual
    void
    serializeVisualEnd( std::ostream& ) const
      {}

    virtual
    void
    serializeVisualObject( std::ostream&,
                           const std::string &,
                           int ) const
      {}

    virtual
    void
    serializeVisualObject( std::ostream&,
                           const std::string &,
                           double, int ) const
      {}

    virtual
    void
    serializeVisualObject( std::ostream&,
                           const std::string &,
                           double, int,
                           double, double ) const
      {}

    virtual
    void
    serializeVisualObject( std::ostream&,
                           const std::string &,
                           double, int,
                           double, double,
                           double ) const
      {}

    virtual
    void
    serializeVisualObject( std::ostream&,
                           const std::string &,
                           double, int,
                           double, double,
                           double, double ) const
      {}

    virtual
    void
    serializeVisualObject( std::ostream&,
                           const std::string &,
                           double, int,
                           double, double,
                           int, int ) const
      {}

    virtual
    void
    serializeVisualObject( std::ostream&,
                           const std::string &,
                           double, int,
                           bool ) const
      {}

    virtual
    void
    serializeVisualObject( std::ostream&,
                           const std::string &,
                           double, int,
                           int, bool ) const
      {}

    virtual
    void
    serializeVisualObject( std::ostream&,
                           const std::string &,
                           double, int,
                           double, double,
                           int, int,
                           bool ) const
      {}

    virtual
    void
    serializeVisualObject( std::ostream&,
                           const std::string &,
                           double, int,
                           double, double,
                           int, int,
                           int, bool ) const
      {}

    virtual
    void
    serializeBodyBegin( std::ostream&, int ) const
      {}

    virtual
    void
    serializeBodyEnd( std::ostream& ) const
      {}

    virtual
    void
    serializeBodyViewMode( std::ostream&,
                           const char*,
                           const char* ) const
      {}

    virtual
    void
    serializeBodyStamina( std::ostream&,
                          double,
                          double ) const
      {}

    virtual
    void
    serializeBodyVelocity( std::ostream&,
                           double ) const
      {}

    virtual
    void
    serializeBodyVelocity( std::ostream&,
                           double,
                           int ) const
      {}

    virtual
    void
    serializeBodyCounts( std::ostream&,
                         int,
                         int,
                         int,
                         int ) const
      {}

    virtual
    void
    serializeBodyCounts( std::ostream&,
                         int,
                         int,
                         int ) const
      {}

    virtual
    void
    serializeNeckAngle( std::ostream&,
                        int ) const
      {}

    virtual
    void
    serializeNeckCount( std::ostream&,
                        int ) const
      {}

    virtual
    void
    serializeArm( std::ostream&,
                  int,
                  int,
                  double,
                  int,
                  int ) const
      {}

    virtual
    void
    serializeFocus( std::ostream&,
                    const char*,
                    int ) const
      {}

    virtual
    void
    serializeFocus( std::ostream&,
                    const char*,
                    int,
                    int ) const
      {}


    virtual
    void
    serializeTackle( std::ostream&,
                     int,
                     int ) const
      {}


    virtual
    void
    serializeFSBegin( std::ostream&,
                      int ) const
      {}

    virtual
    void
    serializeFSEnd( std::ostream& ) const
      {}

    virtual
    void
    serializeFSPlayMode( std::ostream&,
                         const char* ) const
      {}

    virtual
    void
    serializeFSViewMode( std::ostream&,
                         const char*,
                         const char* ) const
      {}

    virtual
    void
    serializeFSCounts( std::ostream&,
                       int,
                       int,
                       int,
                       int,
                       int,
                       int,
                       int,
                       int ) const
      {}

    virtual
    void
    serializeFSScore( std::ostream&,
                      int,
                      int ) const
      {}

    virtual
    void
    serializeFSBall( std::ostream&,
                     double,
                     double,
                     double,
                     double ) const
      {}

    virtual
    void
    serializeFSPlayerBegin( std::ostream&,
                            char,
                            int,
                            bool,
                            int,
                            double,
                            double,
                            double,
                            double,
                            double,
                            double ) const
      {}

    virtual
    void
    serializeFSPlayerArm( std::ostream&,
                          double,
                          double ) const
      {}

    virtual
    void
    serializeFSPlayerEnd( std::ostream&,
                          double,
                          double,
                          double ) const
      {}

    virtual
    void
    serializeInit( std::ostream&,
                   const char*,
                   int,
                   const PlayMode& ) const
      {}

    virtual
    void
    serializeReconnect( std::ostream&,
                        const char*,
                        const PlayMode& ) const
      {}

    //        virtual
    //        void
    //        serializeServerParamBegin( std::ostream& strm ) const
    //        {}

    //        virtual
    //        void
    //        serializeServerParamEnd( std::ostream& strm ) const
    //        {}

    //        virtual
    //        void
    //        serializePlayerParamBegin( std::ostream& strm ) const
    //        {}

    //        virtual
    //        void
    //        serializePlayerParamEnd( std::ostream& strm ) const
    //        {}

    //        virtual
    //        void
    //        serializePlayerTypeBegin( std::ostream& strm ) const
    //        {}

    //        virtual
    //        void
    //        serializePlayerTypeEnd( std::ostream& strm ) const
    //        {}

    //        virtual
    //        void
    //        serializeParam( std::ostream&, int ) const
    //        {}

    //        virtual
    //        void
    //        serializeParam( std::ostream&, unsigned int ) const
    //        {}

    //        virtual
    //        void
    //        serializeParam( std::ostream&, bool ) const
    //        {}

    //        virtual
    //        void
    //        serializeParam( std::ostream&, double ) const
    //        {}

    //        virtual
    //        void
    //        serializeParam( std::ostream&, const std::string& ) const
    //        {}

    //        virtual
    //        void
    //        serializeParam( std::ostream&,
    //                        const std::string&,
    //                        int ) const
    //        {}

    //        virtual
    //        void
    //        serializeParam( std::ostream&,
    //                        const std::string&,
    //                        bool ) const
    //        {}

    //        virtual
    //        void
    //        serializeParam( std::ostream&,
    //                        const std::string&,
    //                        double ) const
    //        {}

    //        virtual
    //        void
    //        serializeParam( std::ostream&,
    //                        const std::string&,
    //                        const std::string& ) const
    //        {}

    virtual
    void
    serializeChangePlayer( std::ostream&,
                           int ) const
      {}

    virtual
    void
    serializeChangePlayer( std::ostream&,
                           int,
                           int ) const
      {}

    virtual
    void
    serializeOKClang( std::ostream&,
                      int,
                      int ) const
      {}

    virtual
    void
    serializeErrorNoTeamName( std::ostream&,
                              const std::string& ) const
      {}

    virtual
    void
    serializeScore( std::ostream&,
                    int,
                    int,
                    int ) const
      {}

};



class SerializerCoach
    : public Serializer
{
public:
    typedef const rcss::SerializerCoach*(*Creator)();
    typedef rcss::lib::Factory< Creator, int > Factory;

    static
    Factory&
    factory();

protected:
    SerializerCoach( const SerializerCommon& common );

    virtual
    ~SerializerCoach();
public:

    virtual
    void
    serializeRefAudio( std::ostream&,
                       const int&,
                       const char* ) const = 0;

    virtual
    void
    serializeCoachAudio( std::ostream&,
                         const int&,
                         const std::string&,
                         const char* ) const = 0;

    virtual
    void
    serializeCoachStdAudio( std::ostream&,
                            const int&,
                            const std::string&,
                            const clang::Msg& ) const = 0;

    virtual
    void
    serializePlayerAudio( std::ostream&,
                          const int&,
                          const std::string&,
                          const char* ) const = 0;


    virtual
    void
    serializeInit( std::ostream& strm ) const = 0;
};



class SerializerOnlineCoach
    : public Serializer
{
public:
    typedef const rcss::SerializerOnlineCoach*(*Creator)();
    typedef rcss::lib::Factory< Creator, int > Factory;

    static
    Factory&
    factory();

protected:
    SerializerOnlineCoach( const SerializerCommon& common );
    virtual
    ~SerializerOnlineCoach();

public:
    virtual
    void
    serializeRefAudio( std::ostream&,
                       const int&,
                       const std::string&,
                       const char* ) const = 0;

    virtual
    void
    serializePlayerAudio( std::ostream&,
                          const int&,
                          const std::string&,
                          const char* ) const = 0;

    virtual
    void
    serializePlayerClangVer( std::ostream&,
                             const std::string&,
                             const unsigned int&,
                             const unsigned int& ) const = 0;

    virtual
    void
    serializeInit( std::ostream&,
                   int side = 0 ) const = 0;

    virtual
    void
    serializeChangedPlayer( std::ostream &,
                            int unum,
                            int type = -1 ) const = 0;
};
}
#endif // _SERIALIZER_H_
