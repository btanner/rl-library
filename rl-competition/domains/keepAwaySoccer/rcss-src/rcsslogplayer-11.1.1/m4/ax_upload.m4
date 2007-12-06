# @synopsis AX_UPLOAD([command])
# ---------------------------------------------------------
# Author: Tom Howard <tomhoward@users.sf.net>
# Version: 1.1
# Copyright (C) 2004, 2005, Tom Howard
#
# Copying and distribution of this file, with or without
# modification, are permitted in any medium without
# royalty provided the copyright notice and this notice
# are preserved.
#
# Desc: Adds support for uploading dist files. %%s in the command will be
#       substituted with the name of the file. e.g
#
#          AX_UPLOAD([ncftpput -v upload.sourceforge.net /incoming %%s])
#
#       To add upload support for other custom dists use AX_ADD_SRC_UPLOAD or
#       AX_ADD_BIN_UPLOAD
#
#       You can then upload of the src distribution files by running
#
#          make upload-src
#
#       all the binary distribution files by running
#
#          make upload-bin
#
#       or both by running
#
#          make upload
#
AC_DEFUN([AX_UPLOAD],
[
AC_MSG_NOTICE([adding upload support])
USING_AX_UPLOAD=true
AC_MSG_NOTICE([setting upload command... \`$1\`])
AX_UPLOAD_COMMAND="$1"
AX_ADD_MK_MACRO([[
UPLOAD_BIN =
UPLOAD_SRC =

upload-src:
	@cd \$(top_builddir); \\
	for TARGET in \$(UPLOAD_SRC); do \\
	    \$(MAKE) \"\$\$TARGET\"; \\
	done

upload-bin:
	@cd \$(top_builddir); \\
	for TARGET in \$(UPLOAD_BIN); do \\
	    \$(MAKE) \"\$\$TARGET\"; \\
	done

upload upload-all all-upload: upload-src upload-bin
]])
AX_ADD_SRC_UPLOAD([gzip],[$PACKAGE-$VERSION.tar.gz])
AX_ADD_SRC_UPLOAD([bzip2],[$PACKAGE-$VERSION.tar.bz2])
AX_ADD_SRC_UPLOAD([zip],[$PACKAGE-$VERSION.zip])
])# AX_UPLOAD

