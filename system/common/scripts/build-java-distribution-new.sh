#  This is meant to be sourced by a Java project that has:
#
#  - $PROJECTNAME variable set.  
#	This will partially determine the name of the file we will create.
#	Something like MountainCar-Java is good
#  - $SYSTEMPATH variable set.  
#	This should be the relative path from where you are sourcing 
#	from to the system dir. Something like ../../../system
#  - $WIKIPAGENAME variable set and README.txt file.
#	README.txt should be a wiki formatted file that will have tokens
#		* FILENAME
#		* FILELINK
#		* FILEDETAILSLINK
#		* VERSION
#	These tokens will be replaced by the actual details of the file
#	that will be uploaded to google code.
#	This page will be uploaded as a wiki document to $WIKIPAGENAME.wiki\
#	And then visible at http://rl-librar.googlecode.com/wiki/$WIKIPAGENAME
#   - $SVNPASSWORDFILE variable set
#	This should be a path to a file on your disk that has 1 line (NO NEWLINE) 
#	With your google username and password.  You can make it like this:
#		 echo -n "brian@tannerpages.com MYPASSWORD" > ~/rl-library-svn-password
#	Then just set SVNPASSWORDFILE=~/rl-library-svn-password
#   - $WIKIPASSWORDFILE variable set
#	This should be a path to a file on your disk that has 1 line (NO NEWLINE) 
#	With your RL-Library wiki bot's username and password.  You can make it like this:
#		 echo -n "btannerbot MYPASSWORD" > ~/rl-library-wiki-password
#	Then just set WIKIPASSWORDFILE=~/rl-library-wiki-password
#   - $PROJECTTYPE variable set
#	This should be one of: environment, agent, experiment, package
#   - $LANGUAGE variable set
#	This should be one of: Java/C/CPP/Matlab/Python/Lisp/etc
#   - $JARNAME variable set
#	This should be the name of the JAR that is built.  Like MountainCar.jar
#   - $ANTBUILDTARGET (this is optional - if you don't want to build with >$ ant build
#	give the name of an alternate target
#
#  - A build.xml with target "build" that puts a jar in products/
#
#  	The build.xml must actually follow the common build system in here, 
#	we expect an entry like:
#    	<property name="baseLibraryDir" value="../../.."/>
#  
#	We will be changing that property automatically to point to the 
#	distribution system directory.
#
#   - Subversion must be installed and this script should be sourced
#	from a subversion checkout. 
#   




#Sets up the most of the paths and creates the distribution directory
#Copies the default set of jars and stuff to the appropriate directories.
#After you call this, you should copy whatever other files you need to $DISTDIR
javaDistributionInit(){
#First make sure they gave us a username and password so we can do the upload...
	if [ -z "$SYSTEMPATH" ]
	then
		echo 
		echo "   ERROR: You Must set the SYSTEMPATH variable."
		echo 
	  exit 1
	fi

	if [ -z "$PROJECTTYPE" ]
	then
		echo 
		echo "   ERROR: You Must set the PROJECTTYPE variable."
		echo 
	  exit 1
	fi

	if [ -z "$PROJECTNAME" ]
	then
		echo 
		echo "   ERROR: You Must set the PROJECTNAME variable."
		echo 
	  exit 1
        fi


	if [ -z "$WIKIPAGENAME" ]
	then
		echo 
		echo "   ERROR: You Must set the WIKIPAGENAME variable."
		echo 
	  exit 1
	fi

	if [ -z "$SVNPASSWORDFILE" ]
	then
		echo 
		echo "   ERROR: You Must set the SVNPASSWORDFILE variable."
		echo 
	  exit 1
	fi

	if [ -z "$WIKIPASSWORDFILE" ]
	then
		echo 
		echo "   ERROR: You Must set the WIKIPASSWORDFILE variable."
		echo 
	  exit 1
	fi


	if [ -z "$LANGUAGE" ]
	then
		echo 
		echo "   ERROR: You Must set the LANGUAGE variable."
		echo 
	  exit 1
	fi

	if [ -z "$JARNAME" ]
	then
		echo 
		echo "   ERROR: You Must set the JARNAME variable."
		echo 
	  exit 1
	fi

	if [ -z "$ANTBUILDTARGET" ]
	then
		ANTBUILDTARGET=build
	fi

#This has the modifieds and stuff in it, I prefer this one:
	VERSION=$(svn info . |grep Revision: | awk '{print $2}')
	DISTNAME=$PROJECTNAME-R$VERSION
	DISTFILENAME=$DISTNAME.tar.gz

	echo Initializing $DISTNAME
#Remove if we already have it
	rm $DISTNAME.tar.gz
	
	DISTDIR=distbuild
	THISPROJECTDISTDIR=$DISTDIR/$DISTNAME
	COMMONPATH=$SYSTEMPATH/common
	COMMONLIBS=$COMMONPATH/libs
	ANTSCRIPTS=$COMMONPATH/ant
	RLVIZPATH=$COMMONPATH/libs/rl-viz
	RLVIZJAR=$RLVIZPATH/RLVizLib.jar
	rm -Rf $DISTDIR
	mkdir $DISTDIR
	mkdir $THISPROJECTDISTDIR

	svn export --quiet src $THISPROJECTDISTDIR/src
	mkdir -p $DISTDIR/system/common/libs/rl-viz
	svn export --quiet $ANTSCRIPTS $DISTDIR/system/common/ant
	svn export --quiet $COMMONLIBS/ant-contrib-1.0b3.jar  $DISTDIR/system/common/libs/ant-contrib-1.0b3.jar
	svn export --quiet $RLVIZJAR $DISTDIR/system/common/libs/rl-viz/RLVizLib.jar
	svn export --quiet $RLVIZPATH/libs $DISTDIR/system/common/libs/rl-viz/libs

	#Going to use sed to change the relative path to the system directory in the build.xml file
	#We will use bar | as a delimiter
	#Looks for something like: name="baseLibraryDir" value="../77s_5/../../../../blargl"
	#And changes it to: name="baseLibraryDir" value=".."
	sed 's|name="baseLibraryDir" value="\([a-z,A-Z,0-9,.,/,-,_]*\)"|name="baseLibraryDir" value=".."|' <build.xml > $THISPROJECTDISTDIR/build.xml

}

#This will go into $DISTDIR, build the project, remove the build directory, back out
#tar and gzip the directory, and then delete the directory.
javaDistributionBuildJarAndGzip(){
	pushd $THISPROJECTDISTDIR
	ant -quiet ${ANTBUILDTARGET}
	rm -Rf build
	popd
	pushd $DISTDIR
	tar -cf ../$DISTNAME.tar *
	popd
	gzip $DISTNAME.tar
	echo Successfully created $DISTFILENAME
}

javaDistributionUploadFile(){
	echo -n "  Uploading file to google code..."
	python $COMMONPATH/scripts/googlecode_upload.py --svnpasswordfile=$SVNPASSWORDFILE --summary="$PROJECTNAME $VERSION" --project=rl-library --labels=Type-Archive,OpSys-All,Language-$LANGUAGE,RLType-$PROJECTTYPE $DISTFILENAME
	echo "File uploaded."
}
javaDistributionUpdateWiki(){
	echo -n "  Updating Wiki..."
	PYTHONPATH=$COMMONPATH/libs python $COMMONPATH/scripts/wiki_update.py --wikipasswordfile=$WIKIPASSWORDFILE --jarname="$JARNAME" --filename="$DISTFILENAME" --wikipage="$WIKIPAGENAME" --projectdir="$DISTNAME" 
	echo "Wiki updated."
}

#Delete $DISTDIR and $DISTFILENAME
javaDistributionCleanup(){
	echo "  Cleaning up..."
	echo "     Deleting $DISTDIR"
	rm -Rf $DISTDIR
	echo "     Deleting $DISTFILENAME"
	rm -Rf $DISTFILENAME
	echo "  Done cleanup."
}

