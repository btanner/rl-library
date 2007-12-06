# @synopsis AX_ADD_UPLOAD([NAME],[FILE])
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
#       which will depend on dist-NAME.  Normally this macro is used by
#       AX_ADD_SRC_UPLOAD or AX_ADD_BIN_UPLOAD and should not be used
#       directly.
#
AC_DEFUN([AX_ADD_UPLOAD],
[
AC_MSG_NOTICE([adding upload support for $1])
if test "$USING_AX_UPLOAD" != "x"; then
AX_ADD_MK_MACRO([[

upload-$1: dist-$1
	@echo \"Uploading $2 ...\"; \\
	cd \$(top_builddir); \\
	UPLOAD_COMMAND=\`printf \"$AX_UPLOAD_COMMAND\" $2\`; \\
	\$\$UPLOAD_COMMAND
]])
fi
])#AX_ADD_UPLOAD
