dnl Process this file with autoconf to produce a configure script.

AC_PREREQ(2.59)

AC_INIT([RCSSBase], 11.1.0, sserver-admin@users.sf.net, rcssbase)
AC_CONFIG_SRCDIR([rcssbase/net/udpsocket.hpp])
AC_CONFIG_HEADERS([config.h])

AM_INIT_AUTOMAKE([gnu 1.7.2 check-news dist-bzip2 dist-zip])

AC_PROG_MAKE_SET

AC_PROG_AWK
AC_ARG_VAR(SED, "sed exectuable to use")
AC_CHECK_PROG(SED, sed, sed, AC_MSG_ERROR([Cannot find sed on your system]))

AC_LANG(C++)

AC_PROG_CXX
AC_PROG_CC
AC_PROG_CPP
AC_PROG_INSTALL
AC_PROG_LN_S

#ACX_PTHREAD([LIBS="$PTHREAD_LIBS $LIBS"
#             CFLAGS="$CFLAGS $PTHREAD_CFLAGS"
#             CXXFLAGS="$CXXFLAGS $PTHREAD_CFLAGS"
#             CC="$PTHREAD_CC"])
ACX_PTHREAD

AC_HEADER_STDC
AC_CHECK_HEADERS(unistd.h)
AC_CHECK_HEADERS(math.h)
AC_CHECK_HEADERS(sys/time.h)
AC_CHECK_HEADERS(sys/types.h)
AC_CHECK_HEADERS(inttypes.h)
AC_CHECK_HEADERS(sys/socket.h)
AC_CHECK_HEADERS(poll.h)
AC_CHECK_HEADERS(arpa/nameser.h)
#AC_CHECK_HEADERS(resolv.h)
AC_CHECK_HEADERS(netdb.h)
AC_CHECK_HEADERS(fcntl.h)
AC_CHECK_HEADERS(sys/socket.h)
AC_CHECK_HEADERS(netinet/in.h)
AC_CHECK_HEADERS(arpa/inet.h)
#AC_CHECK_HEADERS(boost/version.hpp, [], [
#AC_MSG_ERROR([Boost required])
#])

AX_BOOST_BASE([1.32.0])
AX_BOOST_FILESYSTEM
CPPFLAGS="$CPPFLAGS $BOOST_CPPFLAGS"
LDFLAGS="$LDFLAGS $BOOST_LDFLAGS"
#LIBS="$LIBS $BOOST_FILESYSTEM_LIB"

AC_HEADER_STDBOOL
AC_C_CONST
AC_C_INLINE
AC_TYPE_SIZE_T
AC_HEADER_TIME

AC_CHECK_TYPES([socklen_t], [], [], [
#include <sys/types.h>
#include <sys/socket.h>
])
AC_CHECK_TYPES([size_t], [], [], [
#include <sys/types.h>
#include <sys/socket.h>
])


AC_ARG_WITH(sstream,
        AS_HELP_STRING(--with-sstream,use sstream instead of strstream (default=yes)),
                       use_sstream=$withval, use_sstream=yes)
if test "$use_sstream" = "yes"; then
   AC_CXX_HAVE_SSTREAM
fi

#AC_SUBST([FLEX])
#AC_SUBST([FLEXFLAGS])
#AC_SUBST([AM_FLEXFLAGS])
#AC_CHECK_PROG([FLEX],
#              [flex],
#              flex,
#              $srcdir/missingflex)
#AM_FLEXFLAGS="-olex.yy.c"

#AC_PROG_YACC

AC_SUBST(TEST_LIBS)
#AC_LIB_RCSSTEST([TEST_LIBS="-lrcsstest"])

AC_SUBST(BUILD_TESTS)
AM_CONDITIONAL(BUILD_TEST, [test $TEST_LIBS])


AC_SUBST(NET_LIBS)
AC_CHECK_LIB(socket, socket, [NET_LIBS="-lsocket"])
AC_CHECK_LIB(nsl, gethostbyname, [NET_LIBS="$NET_LIBS -lnsl"])
AC_CHECK_LIB(posix4, sched_yield, [NET_LIBS="$NET_LIBS -lposix4"])
AC_CHECK_LIB(resolv, hstrerror, [NET_LIBS="$NET_LIBS -lresolv"])

AC_SUBST(GZ_LIBS)
AC_CHECK_LIB([z], [deflate],
             [AC_DEFINE([HAVE_LIBZ], [1],
                        [Define to 1 if you have the `z' library (-lz).])
              GZ_LIBS="-lz"])

AC_CHECK_FUNCS([gethostbyname gettimeofday inet_ntoa memset socket strerror])


AC_DISABLE_STATIC
AC_WITH_LTDL
if test "x$INCLTDL" != "x"; then
    LTDLDIR=libltdl
fi
#AC_LIBTOOL_WIN32_DLL
AC_PROG_LIBTOOL
AC_SUBST(LIBTOOL_DEPS)
AC_SUBST(LTDLDIR)

./libtool --features | grep '^enable shared libraries$' >& /dev/null
ENABLE_SHARED=$?
AM_CONDITIONAL(NODLOPEN, [test $ENABLE_SHARED -ne 0])

AX_EXTRA_DIST
AX_UPLOAD([ncftpput -v upload.sourceforge.net /incoming %%s])
AX_RPM_GEN_SPEC([spec.tmpl],
	    [The RoboCup Soccer Simulator Base Code],
            [LGPL],
	    [Development/Libraries],
	    [http://sserver.sf.net],
	    [http://prdownloads.sourceforge.net/sserver/],
[The RoboCup Soccer Simulator Base Code package (RCSSBase) contains\nbase code used by the various other RoboCup Soccer Simulator Packages.])
AX_DIST_RPM
#AX_DIST_MSI([$srcdir/Setup/Release/${PACKAGE_NAME}Setup.msi])
AX_CVS([:pserver:anonymous@cvs.sourceforge.net:/cvsroot/sserver])
AX_PKG_GEN_PLIST([Info.plist], [Description.plist],
	         [RoboCup], [com.robocup.${PACKAGE}])
AX_DIST_DMG

AC_SUBST(VERSION)

AC_CONFIG_FILES([Makefile
		rcssbase/Makefile
		rcssbase/error/Makefile
		rcssbase/lib/Makefile
		rcssbase/lib/factorytest/Makefile
		rcssbase/lib/loadertest/Makefile
		rcssbase/net/Makefile
		rcssbase/net/addrtest/Makefile
		rcssbase/net/udpsockettest/Makefile
		rcssbase/net/tcpsockettest/Makefile
		rcssbase/net/iosocketstreamtest/Makefile
 		rcssbase/conf/Makefile
		rcssbase/gzip/Makefile])

AC_OUTPUT
