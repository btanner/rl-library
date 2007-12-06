# @synopsis AX_DIST_RPM
# ---------------------------------------------------------
# Author: Tom Howard <tomhoward@users.sf.net>
# Version: 1.2
# Copyright (C) 2004, 2005, Tom Howard
#
# Copying and distribution of this file, with or without
# modification, are permitted in any medium without
# royalty provided the copyright notice and this notice
# are preserved.
#
# Desc: Adds support for a rpm dist target within your Makefile
#
# See Also: AX_ADD_MK_MACRO, AX_RPM_GEN_SPEC, AX_RPM_CUSTOM_SPEC,
#     	    AX_RPM_EXPAND_MACRO
#
# ChangeLog:
#
# 2005-06-29	Tom Howard	<tomhoward@users.sf.net>
#
#	* Removed spec file from DISTCLEANFILES 
#
# 2005-06-20	Tom Howard	<tomhoward@users.sf.net>
#
#	* Moved spec functionality into ax_rpm_gen_spec and ax_rpm_custom_spec
#	* Added support for reading `rpm --showrc` (via ax_rpm_expand_macro)
#         rather than reading ~/.rpmmaccros directly
#	* Added support for building rpms when non-default prefixes are
#	  specified during configure
#	* Removed need to specify a platform suffix.  Instead the
#	  target_platform specified by `rpm --showrc` is used.
#	* Applied patch from John Vandenberg which fixed a bug in the CLEANFILES
#         specification, problems with dependencies that resulting in the
#	  install_files list and tar-ball used to create the rpm laging behind
#	  the current build environment, install_files failing due to a missing
#	  directory and the initial fix for handling non-default prefixes.
#	  Thanks John.
#
AC_DEFUN([AX_DIST_RPM],
[
AC_MSG_NOTICE([adding rpm support])
AC_REQUIRE([AX_INSTALL_FILES])
# check for spec
if test "x$AX_RPM_SPEC_FILE" = "x"; then
  AC_MSG_ERROR([rpm spec file not set.  Use either ax_rpm_gen_spec or ax_rpm_custom_spec before calling ax_dist_rpm])
fi

AC_REQUIRE([AC_PROG_AWK])
if test "x$AWK" != "x"; then
  AX_RPM_INSTALL_FILES="\$(top_builddir)/RPMinstall_files"
  AC_SUBST(AX_RPM_INSTALL_FILES)
  
  AC_ARG_VAR(RPM, [rpm executable to use])
  AC_CHECK_PROGS(RPMBUILD,[rpmbuild rpm])
  if test "x$RPMBUILD" != "x"; then
    AX_RPM_FIND_DIR([AX_RPM_SOURCEDIR],[sourcedir])
    AX_RPM_FIND_DIR([AX_RPM_RPMDIR],[rpmdir])
    AX_RPM_FIND_DIR([AX_RPM_SRCRPMDIR],[srcrpmdir])
    if test "x$target" = "x"; then
      AX_RPM_EXPAND_MACRO([AX_RPM_TARGET_PLATFORM],[target_platform])
    else
      AX_RPM_TARGET_PLATFORM="$target"
    fi
    AX_RPM_BUILD_ARCH=`echo $AX_RPM_TARGET_PLATFORM | $AWK -F '-' '{ print @S|@1; }'`
    if test "x$AX_RPM_SOURCEDIR" != "x"; then
      if test "x$AX_RPM_RPMDIR" != "x"; then
        if test "x$AX_RPM_SRCRPMDIR" != "x"; then
          if test "x$AX_RPM_BUILD_ARCH" != "x"; then
            if test "x$AX_RPM_TARGET_PLATFORM" != "x"; then
              AX_RPM_CONFIGURE_ARGS=${ac_configure_args}
              AC_SUBST(AX_RPM_CONFIGURE_ARGS)
              AX_ADD_MK_MACRO([[

ifdef CLEANFILES
  CLEANFILES += $AX_RPM_INSTALL_FILES
else
  CLEANFILES = $AX_RPM_INSTALL_FILES
endif

\$(top_builddir)/RPMinstall_files: $AX_INSTALL_FILES_LIST
	PREFIX_ES=\`echo \"\$(prefix)\" | sed 's|\\\/|\\\\\\\\\\/|g'\`; \\
	EXEC_PREFIX_ES=\`echo \"\$(prefix)\" | sed 's|\\\/|\\\\\\\\\\/|g'\`; \\
	BINDIR_ES=\`echo \"\$(bindir)\" | sed 's|\\\/|\\\\\\\\\\/|g'\`; \\
	SBINDIR_ES=\`echo \"\$(sbindir)\" | sed 's|\\\/|\\\\\\\\\\/|g'\`; \\
	LIBEXECDIR_ES=\`echo \"\$(libexecdir)\" | sed 's|\\\/|\\\\\\\\\\/|g'\`; \\
	DATADIR_ES=\`echo \"\$(datadir)\" | sed 's|\\\/|\\\\\\\\\\/|g'\`; \\
	SYSCONFDIR_ES=\`echo \"\$(sysconfdir)\" | sed 's|\\\/|\\\\\\\\\\/|g'\`; \\
	SHAREDSTATEDIR_ES=\`echo \"\$(sharedstatedir)\" | sed 's|\\\/|\\\\\\\\\\/|g'\`; \\
	LOCALSTATEDIR_ES=\`echo \"\$(localstatedir)\" | sed 's|\\\/|\\\\\\\\\\/|g'\`; \\
	LIBDIR_ES=\`echo \"\$(libdir)\" | sed 's|\\\/|\\\\\\\\\\/|g'\`; \\
	INCLUDEDIR_ES=\`echo \"\$(includedir)\" | sed 's|\\\/|\\\\\\\\\\/|g'\`; \\
	OLDINCLUDEDIR_ES=\`echo \"\$(oldincludedir)\" | sed 's|\\\/|\\\\\\\\\\/|g'\`; \\
	INFODIR_ES=\`echo \"\$(infodir)\" | sed 's|\\\/|\\\\\\\\\\/|g'\`; \\
	MANDIR_ES=\`echo \"\$(mandir)\" | sed 's|\\\/|\\\\\\\\\\/|g'\`; \\
	$AWK -v prefix=\$\$PREFIX_ES \\
	     -v exec_prefix=\$\$EXEC_PREFIX_ES \\
	     -v bindir=\$\$BINDIR_ES \\
	     -v sbindir=\$\$SBINDIR_ES \\
	     -v libexecdir=\$\$LIBEXECDIR_ES \\
	     -v datadir=\$\$DATADIR_ES \\
	     -v sysconfdir=\$\$SYSCONFDIR_ES \\
	     -v sharedstatedir=\$\$SHAREDSTATEDIR_ES \\
	     -v localstatedir=\$\$LOCALSTATEDIR_ES \\
	     -v libdir=\$\$LIBDIR_ES \\
	     -v includedir=\$\$INCLUDEDIR_ES \\
	     -v oldincludedir=\$\$OLDINCLUDEDIR_ES \\
	     -v infodir=\$\$INFODIR_ES \\
	     -v mandir=\$\$MANDIR_ES \\
	    \' \\
	    BEGIN { print \"%%defattr(-,root,root)\"; } \\
	    { \\
	      if ( match( ${AX_DOLLAR}${AX_DOLLAR}0, mandir ) ) { print substr( ${AX_DOLLAR}${AX_DOLLAR}0, 1, RSTART-2 ) \"%%{_mandir}\" substr( ${AX_DOLLAR}${AX_DOLLAR}0, RSTART + RLENGTH ); } \\
	      else if ( match( ${AX_DOLLAR}${AX_DOLLAR}0, infodir ) ) { print substr( ${AX_DOLLAR}${AX_DOLLAR}0, 1, RSTART-2 ) \"%%{_infodir}\" substr( ${AX_DOLLAR}${AX_DOLLAR}0, RSTART + RLENGTH ); } \\
	      else if ( match( ${AX_DOLLAR}${AX_DOLLAR}0, includedir ) ) { print substr( ${AX_DOLLAR}${AX_DOLLAR}0, 1, RSTART-2 ) \"%%{_includedir}\" substr( ${AX_DOLLAR}${AX_DOLLAR}0, RSTART + RLENGTH ); } \\
	      else if ( match( ${AX_DOLLAR}${AX_DOLLAR}0, oldincludedir ) ) { print substr( ${AX_DOLLAR}${AX_DOLLAR}0, 1, RSTART-2 ) \"%%{_oldincludedir}\" substr( ${AX_DOLLAR}${AX_DOLLAR}0, RSTART + RLENGTH ); } \\
	      else if ( match( ${AX_DOLLAR}${AX_DOLLAR}0, libdir ) ) { print substr( ${AX_DOLLAR}${AX_DOLLAR}0, 1, RSTART-2 ) \"%%{_libdir}\" substr( ${AX_DOLLAR}${AX_DOLLAR}0, RSTART + RLENGTH ); } \\
	      else if ( match( ${AX_DOLLAR}${AX_DOLLAR}0, localstatedir ) ) { print substr( ${AX_DOLLAR}${AX_DOLLAR}0, 1, RSTART-2 ) \"%%{_localstatedir}\" substr( ${AX_DOLLAR}${AX_DOLLAR}0, RSTART + RLENGTH ); } \\
	      else if ( match( ${AX_DOLLAR}${AX_DOLLAR}0, sharedstatedir ) ) { print substr( ${AX_DOLLAR}${AX_DOLLAR}0, 1, RSTART-2 ) \"%%{_sharedstatedir}\" substr( ${AX_DOLLAR}${AX_DOLLAR}0, RSTART + RLENGTH ); } \\
	      else if ( match( ${AX_DOLLAR}${AX_DOLLAR}0, sysconfdir ) ) { print substr( ${AX_DOLLAR}${AX_DOLLAR}0, 1, RSTART-2 ) \"%%{_sysconfdir}\" substr( ${AX_DOLLAR}${AX_DOLLAR}0, RSTART + RLENGTH ); } \\
	      else if ( match( ${AX_DOLLAR}${AX_DOLLAR}0, datadir ) ) { print substr( ${AX_DOLLAR}${AX_DOLLAR}0, 1, RSTART-2 ) \"%%{_datadir}\" substr( ${AX_DOLLAR}${AX_DOLLAR}0, RSTART + RLENGTH ); } \\
	      else if ( match( ${AX_DOLLAR}${AX_DOLLAR}0, libexecdir ) ) { print substr( ${AX_DOLLAR}${AX_DOLLAR}0, 1, RSTART-2 ) \"%%{_libexecdir}\" substr( ${AX_DOLLAR}${AX_DOLLAR}0, RSTART + RLENGTH ); } \\
	      else if ( match( ${AX_DOLLAR}${AX_DOLLAR}0, sbindir ) ) { print substr( ${AX_DOLLAR}${AX_DOLLAR}0, 1, RSTART-2 ) \"%%{_sbindir}\" substr( ${AX_DOLLAR}${AX_DOLLAR}0, RSTART + RLENGTH ); } \\
	      else if ( match( ${AX_DOLLAR}${AX_DOLLAR}0, bindir ) ) { print substr( ${AX_DOLLAR}${AX_DOLLAR}0, 1, RSTART-2 ) \"%%{_bindir}\" substr( ${AX_DOLLAR}${AX_DOLLAR}0, RSTART + RLENGTH ); } \\
	      else if ( match( ${AX_DOLLAR}${AX_DOLLAR}0, exec_prefix ) ) { print substr( ${AX_DOLLAR}${AX_DOLLAR}0, 1, RSTART-2 ) \"%%{_exec_prefix}\" substr( ${AX_DOLLAR}${AX_DOLLAR}0, RSTART + RLENGTH ); } \\
	      else if ( match( ${AX_DOLLAR}${AX_DOLLAR}0, prefix ) ) { print substr( ${AX_DOLLAR}${AX_DOLLAR}0, 1, RSTART-2 ) \"%%{_prefix}\" substr( ${AX_DOLLAR}${AX_DOLLAR}0, RSTART + RLENGTH ); } \\
	      else { print; } \\
	}\' \"$AX_INSTALL_FILES_LIST\" > ${AX_DOLLAR}@

CLEANFILES += \$(top_builddir)/$PACKAGE-$VERSION.spec

spec: \$(top_builddir)/$PACKAGE-$VERSION.spec

\$(top_builddir)/$PACKAGE-$VERSION.spec:	\$(top_builddir)/$AX_RPM_SPEC_FILE
	@cp \"\$(top_builddir)/$AX_RPM_SPEC_FILE\" \"\$(top_builddir)/$PACKAGE-$VERSION.spec\"

CLEANFILES += \$(top_builddir)/*.rpm  

dist-rpm: rpm
dist-srpm: srpm

rpm: $PACKAGE-$VERSION-0.$AX_RPM_TARGET_PLATFORM.rpm
srpm: $PACKAGE-$VERSION-0.src.rpm

\$(top_builddir)/$PACKAGE-$VERSION-0.$AX_RPM_TARGET_PLATFORM.rpm:	\$(top_builddir)/$PACKAGE-$VERSION.spec \$(top_builddir)/$PACKAGE-$VERSION.tar.gz
	@cp \"\$(top_builddir)/$PACKAGE-$VERSION.tar.gz\" \"${AX_RPM_SOURCEDIR}/.\"
	@$RPMBUILD -bb --rmsource --target $AX_RPM_TARGET_PLATFORM \$(top_builddir)/$PACKAGE-$VERSION.spec
	@mv \"$AX_RPM_RPMDIR/$AX_RPM_BUILD_ARCH/$PACKAGE-$VERSION-0.$AX_RPM_BUILD_ARCH.rpm\" \"\$(top_builddir)/$PACKAGE-$VERSION-0.$AX_RPM_TARGET_PLATFORM.rpm\"

\$(top_builddir)/$PACKAGE-$VERSION.tar.gz: \$(DISTFILES)
	@cd \"\$(top_builddir)\" && \$(MAKE) dist-gzip

$PACKAGE-$VERSION-0.src.rpm:	\$(top_builddir)/$PACKAGE-$VERSION.spec \$(top_builddir)/$PACKAGE-$VERSION.tar.gz
	@cp \"\$(top_builddir)/$PACKAGE-$VERSION.tar.gz\" \"$AX_RPM_SOURCEDIR/.\"
	@$RPMBUILD -bs --rmsource \$(top_builddir)/$PACKAGE-$VERSION.spec
	@mv \"$AX_RPM_SRCRPMDIR/$PACKAGE-$VERSION-0.src.rpm\" \"\$(top_builddir)/.\"; 
	
]])
              AX_ADD_EXTRA_SRC_DIST([srpm])
              AX_ADD_EXTRA_BIN_DIST([rpm])

              AX_ADD_SRC_UPLOAD([srpm],[$PACKAGE-$VERSION-0.src.rpm])
              AX_ADD_BIN_UPLOAD([rpm],[$PACKAGE-$VERSION-0.$AX_RPM_TARGET_PLATFORM.rpm])

            else
	      AC_MSG_WARN([rpm support disabled... could not determine target platform])
            fi
          else
            AC_MSG_NOTICE([rpm support disabled... could not determine build_arch])
          fi
        else
          AC_MSG_NOTICE([rpm support disabled... could not determine srcrpmdir])
        fi
      else
        AC_MSG_NOTICE([rpm support disabled... could not determine rpmdir])
      fi
    else
      AC_MSG_NOTICE([rpm support disabled... could not determine sourcesdir])
    fi
  else
    AC_MSG_NOTICE([rpm support disabled... neither rpmbuild or rpm was found])
  fi
else 
  AC_MSG_NOTICE([rpm support disabled... awk not available])
fi
])# AX_DIST_RPM
