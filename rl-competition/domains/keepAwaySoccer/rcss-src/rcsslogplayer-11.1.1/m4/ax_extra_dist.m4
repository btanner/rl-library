# AX_EXTRA_DIST
# @synopsis AX_EXTRA_DIST()
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
# Desc: Adds support for custom dist targets.  
#
#       To add custom dist targets, you must create a dist-<TYPE> target
#	within your Makefile.am/in files or via a mk macro, where <TYPE>
#       is the name of the dist and then add <TYPE> to EXTRA_SRC_DISTS
#       or EXTRA_BIN_DISTS.  For example
#
#         dist-foobar: 
#    	      <rules for making the foobar dist>
#
#         EXTRA_BIN_DISTS += foobar
#
#       You can then build all the src dist targets by running
#
#         make dist-src
#
#       You can build all the binary dist targets by running
#
#         make dist-bin
#
#       and you can build both the src and dist targets by running
#
#         make all-dist
#
AC_DEFUN([AX_EXTRA_DIST],
[
AC_MSG_NOTICE([adding custom dist support])
USING_AX_EXTRA_DIST=true
AX_ADD_MK_MACRO([[
EXTRA_SRC_DISTS = 
EXTRA_BIN_DISTS = 
dist-src-extra: 
	@echo \"Making custom src targets...\"
	@cd \$(top_builddir); \\
	list='\$(EXTRA_SRC_DISTS)'; \\
	for dist in \$\$list; do \\
	    \$(MAKE) \$\$dist; \\
	done 

dist-src: 
	@cd \$(top_builddir); \\
	\$(MAKE) dist-all dist-src-extra


dist-bin: 
	@echo \"Making custom binary targets...\"
	@cd \$(top_builddir); \\
	list='\$(EXTRA_BIN_DISTS)'; \\
	for dist in \$\$list; do \\
	    \$(MAKE) \$\$dist; \\
	done 

all-dist dist2 dist-all2: dist-src dist-bin

all-dist-check dist2-check dist-all-check: dist-check dist-src-extra dist-bin
]])
])
