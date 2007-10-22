#!/bin/sh

OPTS="-fg grey -bg black -sl 5000"

xterm $OPTS -e "bin/rlglue > /tmp/rlglue.stdout" & 
sleep 1
xterm $OPTS -e "bin/rlgenv > /tmp/rlgenv.stdout" & 
xterm $OPTS -e "bin/rlgagent > /tmp/rlgagent.stdout" & 
xterm $OPTS -e "bin/rlgexp > /tmp/rlgexp.stdout" &

