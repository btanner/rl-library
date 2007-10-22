#!/bin/sh

OPTS="-fg grey -bg black -sl 5000"

xterm $OPTS -e "bin/rlglue > /dev/null 2>/dev/null" & 
sleep 1
xterm $OPTS -e "bin/rlgenv > /dev/null 2>/dev/null" & 
xterm $OPTS -e "bin/rlgagent > /dev/null 2>/dev/null" & 
xterm $OPTS -e "bin/rlgexp > /dev/null 2>/dev/null" &

