#!/bin/bash

#Make the distribution for mountain car

VERSION=$(svnversion -n)

DISTNAME=MountainCar-Java-$VERSION
#Remove if we already have it
rm $DISTNAME.tar.gz
DISTDIR=$DISTNAME


BASEPATH=../../..
SYSTEMPATH=$BASEPATH/system
COMMONPATH=$SYSTEMPATH/common
COMMONLIBS=$COMMONPATH/libs
ANTSCRIPTS=$COMMONPATH/ant
RLVIZPATH=$COMMONPATH/libs/rl-viz
RLVIZJAR=$RLVIZPATH/RLVizLib.jar
rm -Rf $DISTDIR
mkdir $DISTDIR

cp README.txt $DISTDIR/
svn export src $DISTDIR/src
mkdir -p $DISTDIR/system/common/libs/rl-viz
svn export $ANTSCRIPTS $DISTDIR/system/common/ant
svn export $COMMONLIBS/ant-contrib-1.0b3.jar  $DISTDIR/system/common/libs/ant-contrib-1.0b3.jar
svn export $RLVIZJAR $DISTDIR/system/common/libs/rl-viz/RLVizLib.jar
svn export $RLVIZPATH/libs $DISTDIR/system/common/libs/rl-viz/libs

#Going to use sed to change the relative path to the system directory in the build.xml file
#We will use bar | as a delimiter
#Looks for something like: name="baseLibraryDir" value="../77s_5/../../../../blargl"
#And changes it to: name="baseLibraryDir" value="."
sed 's|name="baseLibraryDir" value="\([a-z,A-Z,0-9,.,/,-,_]*\)"|name="baseLibraryDir" value="."|' <build.xml > $DISTDIR/build.xml

cd $DISTDIR
ant build
rm -RF build
cd ..
tar -cf $DISTNAME.tar $DISTDIR
gzip $DISTNAME.tar
rm -Rf $DISTDIR
echo Successfully created $DISTNAME.tar.gz

