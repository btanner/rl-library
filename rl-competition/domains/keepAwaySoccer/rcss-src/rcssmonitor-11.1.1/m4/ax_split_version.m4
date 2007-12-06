# @synopsis AX_SPLIT_VERSION()
# ---------------------------------------------------------
# @author Tom Howard <tomhoward@users.sf.net>
# @version $Id: ax_split_version.m4,v 1.1.1.1 2005/02/23 18:05:48 tomhoward Exp $
# @copyright 2005 Tom Howard
#
# Splits a version number in the format MAJOR.MINOR.POINT into it's
# separeate components.
#
# Sets the variables 
#
AC_DEFUN([AX_SPLIT_VERSION],[
    AX_MAJOR_VERSION=`echo "$VERSION" | $SED 's/\([[^.]][[^.]]*\).*/\1/'`
    AX_MINOR_VERSION=`echo "$VERSION" | $SED 's/[[^.]][[^.]]*.\([[^.]][[^.]]*\).*/\1/'`
    AX_POINT_VERSION=`echo "$VERSION" | $SED 's/[[^.]][[^.]]*.[[^.]][[^.]]*.\(.*\)/\1/'`
    AC_MSG_CHECKING([Major version])
    AC_MSG_RESULT([$AX_MAJOR_VERSION])
    AC_MSG_CHECKING([Minor version])
    AC_MSG_RESULT([$AX_MINOR_VERSION])
    AC_MSG_CHECKING([Point version])
    AC_MSG_RESULT([$AX_POINT_VERSION])
])
