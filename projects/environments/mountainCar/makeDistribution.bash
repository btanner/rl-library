#!/bin/bash

#Make the distribution for Mountain Car.
#This relies on a fairly general script that will work for most Java environments and agents.
#Requirements
#	this should be done with code that is in a subversion checkout
#	you should have a build.xml file that follows the "rl-library build system"
#	I'll add more as I think of it.

#Maybe this should be done in ANT.  Not quite sure.

#Set these variables for this specific project
PROJECTNAME=MountainCar-Java
SYSTEMPATH=../../../system
WIKIPAGENAME=MountainCarJava
SVNPASSWORDFILE=~/rl-library-svn-password

#Get all of the build functions
source $SYSTEMPATH/common/scripts/build-java-distribution.sh

#Sets up the most of the paths and creates the distribution directory
#Copies the default set of jars and stuff to the appropriate directories.
#After you call this, you should copy whatever other files you need to $DISTDIR
javaDistributionInit


#
#  Extra files you want to distribute go here
#
cp LICENSE.txt $DISTDIR/

#This will go into $DISTDIR, build the project, remove the build directory, back out
#tar and gzip the directory, and then delete the directory.
javaDistributionBuildJarAndGzip

javaDistributionUploadFile

javaDistributionUpdateWiki

#Delete $DISTDIR and $DISTFILENAME
javaDistributionCleanup
