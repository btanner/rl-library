# $Id: app.mk 5982 2007-10-13 13:52:51Z lanctot $

APP_DIR  := ol
APP_LIBS := kernel minigame ai 

APP_EXT_HD   += 
APP_EXT_LIBS := 

APP := $(APP_DIR)
include config/app.rules
