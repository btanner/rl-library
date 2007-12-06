# @synopsis AX_RPM_FIND_DIR([VAR],[DIR],[ACTION-IF-FOUND],[ACITON-IF-NOT-FOUND])
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
# Desc: Tries to determine the path to a specified rpm directory
#       and checks that it exisits
# Example:
#       AX_RPM_FIND_DIR([AX_RPM_SOURCEDIR],
#                       [sourcedir],
#                       [echo $AX_RPM_SOURCEDIR]) 
#
AC_DEFUN([AX_RPM_FIND_DIR],
[
AX_RPM_EXPAND_MACRO([ax_rpm_find_dir_$2],[$2],[
  AC_CACHE_CHECK([for directory $ax_rpm_find_dir_$2],
                 [ax_cv_rpm_find_dir_$2_exists],
                 [
                   if test -d "$ax_rpm_find_dir_$2"; then
                     ax_cv_rpm_find_dir_$2_exists=yes
                   else
                     ax_cv_rpm_find_dir_$2_exists=no
                   fi
                 ]) # check dir exists
  ])
AS_IF([test "x$ax_cv_rpm_find_dir_$2_exists" = "xyes"], 
      [$1=$ax_rpm_find_dir_$2; $3],
      [$4])
]) # AX_RPM_FIND_DIR
