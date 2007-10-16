#!/bin/sh

echo "make rlglue" && make rlglue && \
echo "make rlgenv" && make rlgenv && \
echo "make rlgagent" && make rlgagent && \
echo "make rlgexp" && make rlgexp 

