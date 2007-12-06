# @synopsis AX_ADD_BIN_UPLOAD([NAME],[FILE])
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
# Desc: Adds upload support for FILE, by adding make target upload-NAME
#       which will depend on dist-NAME. Additionally FILE will be uploaded
#       when the upload-bin or upload targets are made
#
AC_DEFUN([AX_ADD_BIN_UPLOAD],
[
AX_ADD_UPLOAD([$1],[$2])
if test "$USING_AX_UPLOAD" != "x"; then
AX_ADD_MK_MACRO([[
UPLOAD_BIN += upload-$1
]])
fi
])#AX_ADD_BIN_UPLOAD
