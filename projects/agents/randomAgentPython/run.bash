#!/bin/bash
basePath=../../..
systemPath=$basePath/system
pythonCodecPath=$systemPath/libs/PythonCodec/rlglue
PYTHONPATH=$pythonCodecPath:./src python src/RandomAgent.py
