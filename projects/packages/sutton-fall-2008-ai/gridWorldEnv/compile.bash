#!/bin/bash
basePath=../../../..
systemPath=$basePath/system

ant build
mv $systemPath/dist/SimpleGridWorld.jar ../JARS