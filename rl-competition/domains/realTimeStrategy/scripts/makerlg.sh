#!/bin/sh

MAKE="make -j2"
OPTS=$@

echo "make rlglue" && $MAKE $OPTS rlglue && \
echo "make rlgenv" && $MAKE $OPTS rlgenv && \
echo "make rlgagent" && $MAKE $OPTS rlgagent && \
echo "make rlgexp" && $MAKE $OPTS rlgexp 

