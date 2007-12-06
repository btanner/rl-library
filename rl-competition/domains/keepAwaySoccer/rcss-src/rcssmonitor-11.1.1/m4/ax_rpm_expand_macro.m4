# @synopsis AX_RPM_EXPAND_MACRO([VAR],[MACRO],[ACTION-IF-FOUND],[ACITON-IF-NOT-FOUND])
# ---------------------------------------------------------
# Author: Tom Howard <tomhoward@users.sf.net>
# Version: 1.0
# Copyright (C) 2005, Tom Howard
#
# Copying and distribution of this file, with or without
# modification, are permitted in any medium without
# royalty provided the copyright notice and this notice
# are preserved.
#
# Desc: Tries to expand MACRO by reading rpm --showrc and sets VAR
#       to the result
#
# Example:
#       AX_RPM_EXPAND_MACRO([AX_RPM_SOURCEDIR],
#                           [sourcedir],
#                           [echo $AX_RPM_SOURCEDIR]) 
#
AC_DEFUN([AX_RPM_EXPAND_MACRO],
[
AC_REQUIRE([AC_PROG_AWK])
AC_REQUIRE([AC_PROG_EGREP])
if test "x$AWK" != "x"; then
  if test "x$EGREP" != "x"; then
    AC_CHECK_PROGS(RPM,[rpm])
    if test "x$RPM" != "x"; then
      $RPM --showrc > /dev/null 2>&1
      if test "$?" -eq 0; then
      AC_CACHE_CHECK([expansion of \"%{_$2}\" in \`$RPM --showrc'],
                     [ax_cv_rpm_expand_macro_$2],
                     [
        ax_cv_rpm_expand_macro_$2=%{_$2}
        echo "$ax_cv_rpm_expand_macro_$2" | $EGREP "%{.*}" > /dev/null 2>&1
        ax_rpm_expand_macro_exit=0;
        while test "$ax_rpm_expand_macro_exit" -eq "0"; do
          ax_cv_rpm_expand_macro_$2=`echo "$ax_cv_rpm_expand_macro_$2" | \ 
            $AWK -v showrc_cmd="$RPM --showrc" '{ \
              match( @S|@0, /%{@<:@^%@:>@*}/ ); \
              prefix = substr( @S|@0, 0, RSTART - 1 ); \
              macro = substr( @S|@0, RSTART + 2, RLENGTH - 3 ); \
              suffix = substr( @S|@0, RSTART + RLENGTH ); \
              while( ( showrc_cmd | getline ) > 0 ) \
                { if( @S|@2 == macro ) { print prefix  substr( @S|@0, i@&t@ndex( @S|@0, @S|@3 ) ) suffix; exit; } } \
              exit -1; }'`
          if test "$?" -eq "-1"; then
            ax_cv_rpm_expand_macro_$2="";
            ax_rpm_expand_macro_exit=-1;
          else
            echo "$ax_cv_rpm_expand_macro_$2" | $EGREP "%{.*}" > /dev/null 2>&1
            ax_rpm_expand_macro_exit=$?;
          fi;
        done;
      ]) # expand RPM macro
      fi # check rpm --showrc
    fi # check rpm
  fi # EGREP
fi # AWK
AS_IF([test "x$ax_cv_rpm_expand_macro_$2" != "x"], [
  $1=$ax_cv_rpm_expand_macro_$2; $3],[$4])
]) # AX_RPM_EXPAND_MACRO
