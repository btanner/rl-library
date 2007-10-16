#!/bin/sh

OPTS="-fg grey -bg black -sl 5000"

xterm $OPTS -e bin/rlglue & 
sleep 1
xterm $OPTS -e bin/rlgenv & 
xterm $OPTS -e bin/rlgagent & 
xterm $OPTS -e bin/rlgexp &

