#!/bin/bash
basePath=../../../..
systemPath=$basePath/system

ant clean
ant build
mv $systemPath/dist/SimpleGridWorld.jar ../JARS