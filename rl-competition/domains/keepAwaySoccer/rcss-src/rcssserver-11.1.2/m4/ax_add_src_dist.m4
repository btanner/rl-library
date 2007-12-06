# @synopsis AX_ADD_EXTRA_SRC_DIST([NAME])
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
# Desc: Adds dist-NAME to the list of source dist targets.  dist-NAME
#       will be made when dist-src or all-dist is made
#
AC_DEFUN([AX_ADD_EXTRA_SRC_DIST],
[
AC_REQUIRE([AX_EXTRA_DIST])
AC_MSG_NOTICE([adding custom source dist $1])
if test "$USING_AX_EXTRA_DIST"; then
AX_ADD_MK_MACRO([[
EXTRA_SRC_DISTS += dist-$1
]])
fi
]) # AX_ADD_EXTRA_SRC_DIST
