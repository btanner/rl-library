#!/bin/sh

OPTS="-fg grey -bg black -sl 5000"

xterm $OPTS -e "bin/rlglue > /tmp/rlglue.stdout 2>/tmp/rlglue.stderr" & 
sleep 1
xterm $OPTS -e "bin/rlgenv > /tmp/rlgenv.stdout 2>/tmp/rlgenv.stderr" & 
xterm $OPTS -e "bin/rlgagent > /tmp/rlgagent.stdout 2>/tmp/rlgagent.stderr" & 
xterm $OPTS -e "bin/rlgexp > /tmp/rlgexp.stdout 2>/tmp/rlgexp.stderr" &

