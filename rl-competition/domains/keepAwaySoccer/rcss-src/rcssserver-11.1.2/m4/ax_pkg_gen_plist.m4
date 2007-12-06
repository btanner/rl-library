# @synopsis AX_PKG_GEN_PLIST([INFO_PLIST], [DESC_PLIST], 
#                            [ORGANISATION], [IDENTIFIER])
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
# Desc: Generates an Info.plist and Description.plist file for an OS X
#       PackageMaker dist target
#
# See Also: AX_PRINT_TO_FILE, AX_PKG_CUSTOM_INFO, AX_PKG_GEN_DESC,
#           AX_PKG_CUSTOM_DESC, AX_DIST_PKG, AX_ADD_MK_MACRO
#
# ChangeLog:
#
# 2005-06-29	Tom Howard	<tomhoward@users.sf.net>
#
#	* Fixed bug which prevented DESC_PLIST from being created 
#
AC_DEFUN([AX_PKG_GEN_PLIST],
[
AC_MSG_NOTICE([creating packagemaker info file $srcdir/$1])
AC_SUBST(AX_PKG_INFO_FILE, [$1]) 
AC_SUBST(AX_PKG_DESC_FILE, [$2]) 
AX_ADD_MK_MACRO([[

ifdef DISTCLEANFILES
  DISTCLEANFILES += \$(top_builddir)/$AX_PKG_INFO_FILE \$(top_builddir)/$AX_PKG_DESC_FILE
else
  DISTCLEANFILES = \$(top_builddir)/$AX_PKG_INFO_FILE \$(top_builddir)/$AX_PKG_DESC_FILE
endif

]])

AX_PKG_ORGANISATION="$3"
AX_PKG_IDENTIFIER="$4"

AX_PRINT_TO_FILE([$AX_PKG_INFO_FILE],
[[<?Xml version=\"1.0\" encoding=\"UTF-8\"?>
<!DOCTYPE plist PUBLIC \"-//Apple Computer//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">
<plist version=\"1.0\">
<dict>
        <key>CFBundleGetInfoString</key>
        <string>$AX_PKG_ORGANISATION</string>
        <key>CFBundleIdentifier</key>
        <string>$AX_PKG_IDENTIFIER</string>
        <key>CFBundleShortVersionString</key>
        <string>$VERSION</string>
        <key>IFPkgFlagAllowBackRev</key>
        <true/>
        <key>IFPkgFlagAuthorizationAction</key>
        <string>RootAuthorization</string>
        <key>IFPkgFlagBackgroundAlignment</key>
        <string>topleft</string>
        <key>IFPkgFlagBackgroundScaling</key>
        <string>none</string>
        <key>IFPkgFlagDefaultLocation</key>
        <string>/usr/local</string>
        <key>IFPkgFlagFollowLinks</key>
        <true/>
        <key>IFPkgFlagInstallFat</key>
        <false/>
        <key>IFPkgFlagInstalledSize</key>
        <integer>0</integer>
        <key>IFPkgFlagIsRequired</key>
        <false/>
        <key>IFPkgFlagOverwritePermissions</key>
        <false/>
        <key>IFPkgFlagRelocatable</key>
        <true/>
        <key>IFPkgFlagRestartAction</key>
        <string>NoRestart</string>
        <key>IFPkgFlagRootVolumeOnly</key>
        <false/>
        <key>IFPkgFlagUpdateInstalledLanguages</key>
        <false/>
        <key>IFPkgFormatVersion</key>
        <real>0.10000000149011612</real>
</dict>
</plist>
]])
AX_PRINT_TO_FILE([$AX_PKG_DESC_FILE],
[[<?xml version=\"1.0\" encoding=\"UTF-8\"?>
<!DOCTYPE plist PUBLIC \"-//Apple Computer//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">
<plist version=\"1.0\">
<dict>
        <key>IFPkgDescriptionDescription</key>
        <string></string>
        <key>IFPkgDescriptionTitle</key>
        <string>$PACKAGE</string>
</dict>
</plist>
]])
])# AX_PKG_GEN_PLIST
