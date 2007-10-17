#!/bin/sh

MAKE="make -j2"

echo "make rlglue" && $MAKE rlglue && \
echo "make rlgenv" && $MAKE rlgenv && \
echo "make rlgagent" && $MAKE rlgagent && \
echo "make rlgexp" && $MAKE rlgexp 

