# @synopsis AX_DIST_DMG
# ---------------------------------------------------------
# Author: Tom Howard <tomhoward@users.sf.net>
# Version: 1.2
# Copyright (C) 2005, Tom Howard
#
# Copying and distribution of this file, with or without
# modification, are permitted in any medium without
# royalty provided the copyright notice and this notice
# are preserved.
#
# Desc: Adds support for a OS X PackageMaker dist target within your Makefile
#
# Note: packagemaker requires that the correct permissions be set on the files
#       being packaged.  This is done automatically for you by this macro
#       however, doing so requires the user to be a sudoer.  The only other
#	option is to create a non-admin/non-root installation package, but as
#       of yet I cannot find a way to specify the users home directory as the
#       default install location which is what would be required in that
#       situation.  Please let me know if you can find a better solution.
#
# See Also: AX_MK_MACROS
#
# ChangeLog:
#
# 2005-07-01	Tom Howard	<tomhoward@users.sf.net>
#
#	* Added extra step to create dmg file.  The dmg file is uploaded,
#	  not the pkg, because the pkg is actually a directory.
#
#	* Renamed from AX_DIST_PKG to AX_DIST_DMG
#
# 2005-06-27	Tom Howard	<tomhoward@users.sf.net>
#
#	* Moved plist functionality into ax_pkg_gen_plist and
#         ax_pkg_custom_plist
#
AC_DEFUN([AX_DIST_DMG],
[
AC_MSG_NOTICE([adding packagemaker support])
AC_REQUIRE([AX_INSTALL_FILES])
# check for info file
if test "x$AX_PKG_INFO_FILE" = "x"; then
  AC_MSG_ERROR([pkg info file not set.  Use either ax_pkg_gen_plist or ax_rpm_custom_plist before calling ax_dist_dmg])
fi
# check for desc file
if test "x$AX_PKG_DESC_FILE" = "x"; then
  AC_MSG_ERROR([pkg description file not set.  Use either ax_pkg_gen_plist or ax_rpm_custom_plist before calling ax_dist_dmg])
fi

AC_ARG_VAR(PACKAGE_MAKER, [packagemaker executable to use])
if test "x$PACKAGE_MAKER" = "x"; then
  AC_PATH_PROG([PACKAGE_MAKER],[packagemaker],[],[$PATH:/Developer/Tools])
fi

AC_ARG_VAR(HDIUTIL, [hdiutil executable to use])
if test "x$HDIUTIL" = "x"; then
  AC_PATH_PROG([HDIUTIL],[hdiutil])
fi
if test "x$PACKAGE_MAKER" != "x"; then
  if test "x$HDIUTIL" != "x"; then
  AX_PKG_STAGING="\$(top_builddir)/pkgstaging"
  AC_SUBST(AX_PKG_STAGING)
  AX_ADD_MK_MACRO([[

$AX_PKG_STAGING:	$AX_INSTALL_FILES_LIST
	@echo \"copying $AX_STAGING to $AX_PKG_STAGING\"
	cp -r \"$AX_STAGING\" \"$AX_PKG_STAGING\"
	@echo \"changing ownsership of $AX_PKG_STAGING to root:wheel\"
	sudo chown -R root:wheel \"$AX_PKG_STAGING\"

clean_pkgstaging:
	@echo \"removing $AX_PKG_STAGING\"
	sudo rm -rf \"$AX_PKG_STAGING\"

ifdef CLEANFILES
    CLEANFILES += \$(top_builddir)/$PACKAGE-$VERSION.pkg \$(top_builddir)/$PACKAGE-$VERSION.dmg
else
    CLEANFILES = \$(top_builddir)/$PACKAGE-$VERSION.pkg \$(top_builddir)/$PACKAGE-$VERSION.dmg
endif

dist-dmg: dmg

dmg: \$(top_builddir)/$PACKAGE-$VERSION.dmg

pkg: \$(top_builddir)/$PACKAGE-$VERSION.pkg

\$(top_builddir)/$PACKAGE-$VERSION.pkg:	\$(top_builddir)/$AX_PKG_INFO_FILE \$(top_builddir)/$AX_PKG_DESC_FILE $AX_PKG_STAGING
	$PACKAGE_MAKER -build -p $PACKAGE-$VERSION.pkg -f \"$AX_PKG_STAGING/\$(prefix)\" -ds -v -i \"\$(top_builddir)/$AX_PKG_INFO_FILE\" -d \"\$(top_builddir)/$AX_PKG_DESC_FILE\" 
	\$(MAKE) clean_pkgstaging

\$(top_builddir)/$PACKAGE-$VERSION.dmg: \$(top_builddir)/$PACKAGE-$VERSION.pkg
	$HDIUTIL create -srcfolder \"\$(top_builddir)/$PACKAGE-$VERSION.pkg\" \"\$(top_builddir)/$PACKAGE-$VERSION.dmg\"
	$HDIUTIL internet-enable -yes \"\$(top_builddir)/$PACKAGE-$VERSION.dmg\"

]])

  AX_ADD_EXTRA_BIN_DIST([dmg])

  AX_ADD_BIN_UPLOAD([dmg],[$PACKAGE-$VERSION.dmg])
else
  AC_MSG_NOTICE([dmg support disabled... hdiutil was not found])
fi
else
  AC_MSG_NOTICE([dmg support disabled... packagemaker was not found])
fi
])# AX_DIST_DMG
