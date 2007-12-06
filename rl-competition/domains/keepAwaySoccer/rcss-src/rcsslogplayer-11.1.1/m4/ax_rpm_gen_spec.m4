# @synopsis AX_RPM_GEN_SPEC([SPEC], [PACKAGE_SUMMARY], [COPYRIGHT], [GROUP], [URL], [SRC_URL], [DESC])
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
# Desc: Creates a rpm spec file for the project, which can then be used
#       by AX_DIST_ROM
#
# See Also: AX_MK_MACROS, AX_DIST_RPM
#
AC_DEFUN([AX_RPM_GEN_SPEC],
[
AC_MSG_NOTICE([creating rpm spec file $srcdir/$1])
AC_SUBST(AX_RPM_SPEC_FILE, [$1]) 
AX_ADD_MK_MACRO([[

ifdef DISTCLEANFILES
  DISTCLEANFILES += \$(top_builddir)/$AX_RPM_SPEC_FILE
else
  DISTCLEANFILES = \$(top_builddir)/$AX_RPM_SPEC_FILE
endif

]])
AX_RPM_SUMMARY="$2"
AX_RPM_COPYRIGHT="$3"
AX_RPM_GROUP="$4"
AX_RPM_URL="$5"
AX_RPM_SRC_URL="$6"
AX_RPM_DESC="$7"

AX_PRINT_TO_FILE([$AX_RPM_SPEC_FILE], 
[[Summary: $AX_RPM_SUMMARY
Name: $PACKAGE
Version: $VERSION
Release: 0
Copyright: $AX_RPM_COPYRIGHT
Group: $AX_RPM_GROUP
Source0: $AX_RPM_SRC_URL%%{name}-%%{version}.tar.gz
URL: $AX_RPM_URL
BuildRoot: %%{_tmppath}/%%{name}-root
Prefix: %%{_prefix}

%%description
$AX_RPM_DESC

%%prep
%%setup

%%build
%%configure
make
make RPMinstall_files

%%install
%%makeinstall

%%clean
rm -rf \$RPM_BUILD_ROOT

%%files -f RPMinstall_files

%%doc AUTHORS COPYING ChangeLog INSTALL NEWS README
]])
])# AX_RPM_GEN_SPEC
