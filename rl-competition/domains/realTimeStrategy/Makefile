# $Id: makefile 5984 2007-10-13 14:42:40Z lanctot $
#  
# This is an ORTSLITE file (c) Michael Buro, licensed under the GPL

# This make file works for systems which use gcc
#
# - shell variable OSTYPE must be set in your shell rc file as follows:
# 
#   if LINUX matches uppercase($OSTYPE) => Linux
#   if DARWIN matches                   => Mac OS X
#   if CYGWIN matches                   => Cygwin (gcc on Windows)
#   if MSYS   matches                   => MinGW (gcc on Windows)
#
#   check that value with echo $OSTYPE and set it in your shell resource
#   file if above words don't match. Don't forget export OSTYPE when using sh or bash
#
# - ORTS_MTUNE = i686 athlon-xp pentium-m etc.
#     if set, -mtune=$(ORTS_MTUNE) is added to compiler options
#
# - set GCC_PRECOMP to 1 if you want to use precompiled header files (gcc >= 3.4)
#
# - adjust SDL/X paths if necessary
#
# - for non-gcc compilers try -DHAS_TYPEOF=0 if typeof causes trouble
#
# - make MDBG=1 ...    displays the entire compiler/linker invocation lines
#
# - make clean         removes make generated files
# - make cleanobj      removes all .o files
#
# - make [MODE=dbg]    compiles targets in debug mode (no STL debug!)
#
# - make MODE=opt      compiles with optimizations
#
# - make MODE=bp       compiles with bprof info
#
# - make MODE=gprof    compiles with gprof info
#
# - make MODE=stldbg   compiles optimized stl debug version
#
# - make <app>         creates application bin/<app>  (omit prefix bin/ !)
#                      use periods in <app> in place of slashes for applications
#                      within subdirectories (e.g. "make rtscomp06.game2.simtest")
#
# - make list          displays a list of all registered applications
#
# - make init          creates necessary dep+bin+obj directories and links
#
# - make               creates all applications
#
# - shell variable ORTS_MODE defines the default compile mode
#   if not set, the passed MODE= parameter or dbg will be used, if MODE is not passed
#   otherwise ORTS_MODE will be used
#
# issues: 
#
#  - all include files are used (only using library includes would be better)
#
# new:
#
# applications and libraries can now name their individual external library dependencies
# (libs + headers)     
# see: apps/*/src/app.mk and libs/*/src/lib.mk, esp. libs/gfxclient/src/lib.mk
#
# todo: adjust mac/cygwin to new libary setting


# supported substrings of OSTYPE
# The following words must be uppercase
# If they are found in the uppercased OSTYPE variable
# the particular O/S matches
OSTYPE_LINUX  := LINUX
OSTYPE_MAC    := DARWIN
OSTYPE_CYGWIN := CYGWIN
OSTYPE_MINGW  := MSYS

# convert OSTYPE to uppercase
OSTYPE := $(shell echo -n "$(OSTYPE)" | tr a-z A-Z )
#$(warning OSTYPE=$(OSTYPE))
#OSTYPE := LINUX

# -DOS_LINUX or -DOS_MAC or -DOS_CYGWIN is passed to g++
# also set: OS_LINUX, OS_CYGWIN, OS_MAC := 1 resp.
# MinGW uses OS_LINUX for now.

# Enable GUI (eg. compile+link with SDL) or not? 
# Enabled = 1, disabled = 0
ENABLE_GUI=0

# tar file name
PROJ := ortslite

# directories to be excluded from snapshot tar file

# no longer needed SNAPSHOT_EXCLUDE := libs/mapabstr apps/mapabs apps/polypath apps/hpastar

# special targets
EXCL_INC := clean init cleanobj tar dep rmdeps list tar snapshot rpm

# libraries
#LIB_DIRS := kernel network serverclient gfxclient pathfind osl mapabstr path ortsnet ai/low dcdt/se dcdt/sr newpath
#LIB_DIRS := $(wildcard libs/*) libs/dcdt/se libs/dcdt/sr libs/ai/low
LIB_DIRS := $(wildcard libs/*/src) $(wildcard libs/*/*/src)
#$(warning LIBS $(LIB_DIRS))

# sub-projects
ifeq ("$(MAKECMDGOALS)","")
APP_DIRS := ol#  built by default (do not edit)
else
APP_DIRS := $(MAKECMDGOALS)# build the passed on application
endif

FILT := $(filter $(EXCL_INC),$(MAKECMDGOALS))

ifneq ("$(FILT)","")
APP_DIRS := 
endif

#MAKECMDGOALS := compile_gch link_gch $(MAKECMDGOALS)
#$(warning making $(MAKECMDGOALS) ...)

# $(warning making $(APP_DIRS) ...)

.PHONY: all ALWAYS $(EXCL_INC) $(APP_DIRS) init

# where dependencies are stored (change also in dependency command (***) below)
#
DEP := dep

#$(error $(vp))

# make libraries and applications, make and link gch file first
all: all_end

# create tar file of the entire project
tar:
	@ $(MAKE) clean; cd ..; tar cfz ../$(PROJ).tgz $(PROJ)


# daily SVN snapshot
SVNROOT=svn://mburo@bodo1.cs.ualberta.ca:/all/pubsoft
snapshot:
	rm -rf ortslite
	mkdir ortslite
	svn export $(SVNROOT)/ortslite/trunk ortslite/trunk
	tar cfz $(PROJ).tgz ortslite
	rm -rf orts

CC        := g++
CC_OPTS   := -pipe -felide-constructors -fstrict-aliasing
# -fno-merge-constants

#WARN      := -Wall -W -Wundef 
#WARN      := -Wall -W 
# took out -W because it causes a LOT of warnings in the rlglue code
WARN      := -Wall 
#-Wold-style-cast

#ifneq ("$(OSTYPE)", "$(OSTYPE_MINGW)") 
#removed below because causes problems on MacOSX with boost
#ifeq (,$(findstring $(OSTYPE_MINGW),$(OSTYPE)))
#WARN += -Wundef 
#endif 

OPT:=-O3

ifneq ("$(ORTS_MTUNE)","") 
  OPT:=$(OPT) -mtune=$(ORTS_MTUNE)
endif

OPT_FRAME := -fomit-frame-pointer 

# default 
AWK := gawk
EXE_SUFFIX :=

# default include paths
INC_OPTS=-I$(HOME)/include -I/usr/local/include -I../../support/include

#ifeq ("$(OSTYPE)", "$(OSTYPE_MAC)") 
ifneq (,$(findstring $(OSTYPE_MAC),$(OSTYPE)))

################### Mac OS X 

OS_MAC := 1

ifeq ("$(ENABLE_GUI)", "1")
SDL_INC := -I$(USR_FRAME)/SDL.framework/Headers -I$(USR_FRAME)/SDL_net.framework/Headers -I/usr/include/GL
SDL_LIBS := -framework SDL -framework SDL_net
endif

MACROS    := -DGCC -DOS_MAC -DHAS_TYPEOF=1 -DTRANSFORM_MOUSE_Y=0 -DMAC_OS_X_VERSION_MIN_REQUIRED=1040 -DTARGET_API_MAC_CARBON=1
USR_FRAME := /Library/Frameworks
SYS_FRAME := /System/Library/Frameworks
INC_OPTS += $(SDL_INC) \
            -L$(USR_FRAME) -L$(SYS_FRAME) 

SHARED_LIBS0 := -lc -lz  -lpthread \
               $(SDL_LIBS) \
               -framework Foundation \
               -framework AppKit

AWK := awk
WARN += -Wno-long-double

ifeq ("$(CPU)", "G5")
OPT += -mcpu=970 -mpowerpc64
endif

else

################### MinGW

#ifeq ("$(OSTYPE)", "$(OSTYPE_MINGW)")
ifneq (,$(findstring $(OSTYPE_MINGW),$(OSTYPE)))

OS_LINUX := 1

ifeq ("$(ENABLE_GUI)", "1")
SDL_INC := -I/mingw/include/GL
SDL_LIBS := -lSDLmain -lSDL -lSDL_net
endif

MACROS   := -DOS_LINUX -DGCC -D_WIN32=1 -DTRANSFORM_MOUSE_Y=1 -DHAS_TYPEOF=1
MACROS   += -DGLUT_NO_LIB_PRAGMA -DGLUT_NO_WARNING_DISABLE
INC_OPTS += $(SDL_INC) -I/local/include 
SHARED_LIBS0  := -lm -lzlib1 -lmingw32 $(SDL_LIBS) -lpthreadGC2 -lstdc++

else

################### Linux | Cygwin

ifeq ("$(ENABLE_GUI)", "1")
SDL_INC := -I$(HOME)/include/GL -I/usr/include/SDL -I/usr/local/include/SDL -I$(HOME)/usr/local/include/GL
endif

MACROS   := -DGCC -DTRANSFORM_MOUSE_Y=1 -DHAS_TYPEOF=1
INC_OPTS += -I$(HOME)/include -I/usr/include $(SDL_INC)

#ifeq ("$(OSTYPE)", "$(OSTYPE_LINUX)")
ifneq (,$(findstring $(OSTYPE_LINUX),$(OSTYPE)))

################### Linux 

OS_LINUX := 1

ifeq ("$(ENABLE_GUI)", "1")
SDL_LIBS := -lSDLmain -lSDL -lSDL_net 
endif

MACROS += -DOS_LINUX
#SHARED_LIBS0  += -L/usr/lib -L/usr/local/lib -L/usr/X11R6/lib -lm -lz -lSDLmain -lSDL -lSDL_net -lpthread -lGL -lGLU -lXi -lXmu -lglut -lGLEW $(MODEL_LIBS) -lstdc++
SHARED_LIBS0  += -L$(HOME)/lib -lm -lz $(SDL_LIBS) -lpthread -lstdc++
# -lefence

else 

#ifeq ("$(OSTYPE)", "$(OSTYPE_CYGWIN)")
ifneq (,$(findstring $(OSTYPE_CYGWIN),$(OSTYPE)))

################## Cygwin

OS_CYGWIN := 1

ifeq ("$(ENABLE_GUI)", "1")
SDL_LIBS := -lSDL -lSDL_net 
endif

EXE_SUFFIX:=.exe
MACROS += -DOS_CYGWIN -DSTDC_HEADERS=1 -DGLUT_IS_PRESENT=1 
#-mno-cygwin
#INC_OPTS += -I/usr/include/mingw   -I/usr/include/mingw/GL -I/usr/include
#$(warning "INC_OPTS=" $(INC_OPTS))
# fixme: /lib/SDL_main.o -> -lSDLmain (didn't get SDL to compile on Cygwin)
# adjust paths if necessary
SHARED_LIBS0 := $(SDL_LIBS) -lpthread  -lz -lstdc++ 

# GLEW not checked under cygwin!

else 

# unknown OSTYPE

$(error "!!!!! unknown OSTYPE=$(OSTYPE) !!!!")

endif
endif
endif
endif


# default mode; if ORTS_MODE set, use it
# otherwise use MODE if passed, or dbg otherwise

ifeq ("$(ORTS_MODE)", "") 
  MODE := dbg
else
  MODE := $(ORTS_MODE)
endif

OBJ_DIR := obj
CONFIG  := config

SHARED_PROF_LIBS := $(SHARED_LIBS0)
SHARED_BP_LIBS := ~/lib/bmon.o $(SHARED_LIBS0)

OBJ_OPT := $(OBJ_DIR)/opt
OBJ_DBG := $(OBJ_DIR)/dbg
OBJ_SDBG := $(OBJ_DIR)/stldbg
OBJ_PROF:= $(OBJ_DIR)/prof
OBJ_BP  := $(OBJ_DIR)/bp

OFLAGS  := $(WARN) $(OPT) $(OPT_FRAME) # !!! -g added for 2007 competition
DFLAGS  := $(WARN) -g -ggdb # -ftrapv
SDFLAGS := $(WARN) -g -ggdb -O -D_GLIBCXX_DEBUG # STL debug mode is SLOW!
PFLAGS  := $(WARN) $(OPT) -pg -O
BPFLAGS := $(WARN) -g -ggdb -O2

ifeq ("$(MODE)", "opt")
  FLAGS := $(OFLAGS) -DNDEBUG -Wuninitialized 
#-DSCRIPT_DEBUG
  STRIP := strip
  OBJ   := $(OBJ_OPT)
  SHARED_LIBS := $(SHARED_LIBS0)
else 
  ifeq ("$(MODE)", "gprof")
  FLAGS := $(PFLAGS) -DNDEBUG -Wuninitialized
  STRIP := echo
  OBJ   := $(OBJ_PROF)
  SHARED_LIBS := $(SHARED_PROF_LIBS)
else 
  ifeq ("$(MODE)", "bp")
  FLAGS := $(BPFLAGS) -DNDEBUG -Wuninitialized
  STRIP := echo
  OBJ   := $(OBJ_BP)
  SHARED_LIBS := $(SHARED_BP_LIBS)
else 
  ifeq ("$(MODE)", "dbg")
  FLAGS := $(DFLAGS) 
  STRIP := echo
  OBJ   := $(OBJ_DBG)
  SHARED_LIBS := $(SHARED_LIBS0)
else
  ifeq ("$(MODE)", "stldbg")
  FLAGS := $(SDFLAGS) 
  STRIP := echo
  OBJ   := $(OBJ_SDBG)
  SHARED_LIBS := $(SHARED_LIBS0)
else 

# unknown MODE

$(error "!!!!! unknown MODE=$(MODE) !!!!")

endif
endif
endif
endif
endif

ifeq ("$(ENABLE_GUI)", "1")
MACROS += -DSDL
endif


CCOPTS  = $(CC_OPTS) $(MACROS) $(FLAGS) $(INC_OPTS)

LD      := $(CC) $(CCOPTS) 
LDOPTS  := $(FLAGS)


# line prefix

ifeq ("$(MDBG)", "") 
VERBOSE=@
else
VERBOSE=
endif

# how to generate precompiled header file if GCC_PRECOMP=1
# otherwise, create dummy file

ALLH       := All.H
ALLGCH     := $(ALLH).gch
ALLGCHMODE := $(ALLGCH).$(MODE)

ifeq ("$(GCC_PRECOMP)", "1")

$(CONFIG)/$(ALLGCHMODE) : $(CONFIG)/$(ALLH)
	@ rm -rf  $(CONFIG)/$(ALLGCH)
	@ echo "compile gch file"
	$(VERBOSE) $(CC) $(CCOPTS) -c -o xxx1 $<
	@ mv xxx1 $@
	@ echo "link gch file"
	$(VERBOSE) cd $(CONFIG); ln -sf $(ALLGCHMODE) $(ALLGCH)
else

$(CONFIG)/$(ALLGCHMODE) : $(CONFIG)/$(ALLH)
	@ echo "DUMMY GCH FILE"
	$(VERBOSE) touch $@

endif


# link gch file to gchmode file
# depends on compiled header file

ifeq ("$(GCC_PRECOMP)", "1")

link_gch: 
	@ echo "link gch file"
	$(VERBOSE) cd $(CONFIG); ln -sf $(ALLGCHMODE) $(ALLGCH)

else

link_gch:
	@ echo "remove gch file"
	@ cd config ; rm -f $(ALLGCHMODE) $(ALLGCH)
endif

#===================================================================

# how to create dependency files
# add depfile (.d) as dependent file
# (***) (can't use $(DEP) in sed command because it contains / - so if DEP changes edit this line!
# also prepend object path for .o file
#

DEP_EXEC = $(VERBOSE) set -e; $(CC) -MM $(CCOPTS) $(INC_OPTS) $< | sed 's/$*\.o[ :]*/$(subst /,.,$(basename $<)).o dep\/$(@F) : /g' | $(AWK) '{ if (index($$1,".o") > 0) { print "$$(OBJ)/" $$0 ; } else { print $$0; } }' > $@; [ -s $@ ] || rm -f $@OB

# how to compile source files
#

COMP_EXEC = $(VERBOSE) $(CC) $(CCOPTS) -c -o $@ `pwd`/$<

#-------------------------------------

define create_sublib_rules2
$$(OBJ)/libs.$(subst /,.,$1).src.%.o : libs/$1/src/%.$2
	@ echo "comp($$(MODE)):" $$<
	$$(COMP_EXEC)

$$(DEP)/libs.$(subst /,.,$1).src.%.d : libs/$1/src/%.$2
	@ echo dep: $$<
	$$(DEP_EXEC)
endef

define create_sublib_rules
$(foreach ext,C c cpp m,$(eval $(call create_sublib_rules2,$1,$(ext))))
endef

#-------------------------------------

define create_subapp_rules2
$$(OBJ)/apps.$1.src.%.o : apps/$(subst .,/,$1)/src/%.$2
	@ echo "comp($$(MODE)):" $$<
	$$(COMP_EXEC)

$$(DEP)/apps.$1.src.%.d : apps/$(subst .,/,$1)/src/%.$2
	@ echo dep: $$<
	$$(DEP_EXEC)
endef

define create_subapp_rules
$(foreach ext,C c cpp,$(eval $(call create_subapp_rules2,$1,$(ext))))
endef

#===================================================================

# collect all source files, replace suffix by .o, and filter out *_main.o

# input:  FILES
# output: O_FILES

define create_O_FILES
#FILES     := $$(notdir $$(FILES))
C_FILES   := $$(filter %.C, $$(FILES))
c_FILES   := $$(filter %.c, $$(FILES))
cpp_FILES := $$(filter %.cpp, $$(FILES))

m_FILES   := 
ifeq ("$(OSTYPE)","$(OSTYPE_MAC)")
#ifneq (,$(findstring $(OSTYPE_MAC),$(OSTYPE)))
m_FILES   := $$(filter %.m, $$(FILES))
endif

O_FILES   := $$(C_FILES:.C=.o) $$(c_FILES:.c=.o) $$(cpp_FILES:.cpp=.o) $$(m_FILES:.m=.o)
O_FILES   := $$(subst /,.,$$(O_FILES))

O_FILES := $$(filter-out %_main.o, $$(O_FILES))
endef


# include all applications and dependencies (uses SHARED_LIBS for linking)

APP_SDIRS := $(subst .,/,$(APP_DIRS))

ifneq ("$(APP_DIRS)","")
#include $(patsubst %, apps/%/src/app.mk, $(APP_DIRS))
include $(patsubst %, apps/%/src/app.mk, $(APP_SDIRS))
endif

#$(warning lib_dirs="$(LIB_DIRS)")

vp := $(patsubst %, apps/%/src, $(APP_SDIRS)) \
      $(patsubst %, libs/%/src, $(APP_LIBS)) \
#     $(LIB_DIRS)

#vp := $(patsubst %, %/src, $(LIB_DIRS)) \
#      $(patsubst %, apps/%/src, $(APP_DIRS)) 

INC_OPTS += -Iconfig $(addprefix -I, $(vp))

# new
INC_OPTS += $(LIB_EXT_HD) $(APP_EXT_HD)

# $(warning INC_OPTS= $(INC_OPTS))

# where source files are searched

#$(warning vp="$(vp)")

vpath %.C   $(vp)
vpath %.c   $(vp)
vpath %.cpp $(vp)
vpath %.m   $(vp)

all_end: link_gch $(APP_DIRS)

cleanobj:
	rm -f $(OBJ_OPT)/*
	rm -f $(OBJ_DBG)/*
	rm -f $(OBJ_SDBG)/*
	rm -f $(OBJ_PROF)/*
	rm -f $(OBJ_BP)/*

clean:
	( rm -f bin/*; exit 0)
	rm -f $(DEP)/*
	rm -f $(OBJ_OPT)/*
	rm -f $(OBJ_DBG)/*
	rm -f $(OBJ_SDBG)/*
	rm -f $(OBJ_PROF)/*
	rm -f $(OBJ_BP)/*
	rm -f $(CONFIG)/*.gch*
	rm -rf misc/doxygen/html
	rm -f misc/rlgjava/*.class
	find . -name "*.bprof" -exec rm -f '{}' \;
	find . -name ".ui" -exec rm -f '{}'/* \;
	find . -name ".moc" -exec rm -f '{}'/* \;

rmdeps:
	@ rm -f $(DEP)/

rpm:
	cd misc/pkgs/fc7; ./makerpm; cd ../../..

# create dependency, object directories
# doesn't touch links (pointing to fast local partitions)
 
init:
	@ echo "create directories"
	@ config/create_dir bin
	@ config/create_dir $(DEP)
	@ config/create_dir $(OBJ_DIR)
	@ config/create_dir $(OBJ_OPT)
	@ config/create_dir $(OBJ_DBG)
	@ config/create_dir $(OBJ_SDBG)
	@ config/create_dir $(OBJ_PROF)
	@ config/create_dir $(OBJ_BP)
#	@ (cd libs/dcdt; ./makelinks; exit 0) > /dev/null 2>&1
