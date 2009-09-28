#  This is meant to be sourced by a Java project that has:
#
#  - $PROJECTNAME variable set.  
#	This will partially determine the name of the file we will create.
#	Something like MountainCar-Java is good
#
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
#   - $PROJECTTYPE variable set
#	This should be one of: environment, agent, experiment, package
#   - $LANGUAGE variable set
#	This should be one of: Java/C/CPP/Matlab/Python/Lisp/etc

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

	if [ -z "$LANGUAGE" ]
	then
		echo 
		echo "   ERROR: You Must set the LANGUAGE variable."
		echo 
	  exit 1
	fi


#This has the modifieds and stuff in it, I prefer this one:
#	VERSION=$(svnversion -n)
	VERSION=$(svn info . |grep Revision: | awk '{print $2}')
	DISTNAME=$PROJECTNAME-R$VERSION
	DISTFILENAME=$DISTNAME.tar.gz


#Variables for replacing
	WIKIURL=http://code.google.com/p/rl-library/wiki/$WIKIPAGENAME

	#URL on Google Code to Download
	DISTFILEURL=http://rl-library.googlecode.com/files/$DISTFILENAME
	#URL on Google Code for File Details
	DISTFILEINFOURL=http://code.google.com/p/rl-library/downloads/detail?name=$DISTFILENAME

	echo Initializing $DISTNAME
#Remove if we already have it
	rm $DISTNAME.tar.gz
	
	DISTDIR=distbuild
	THISPROJECTDISTDIR=$DISTDIR/$DISTNAME
	COMMONPATH=$SYSTEMPATH/common
	COMMONLIBS=$COMMONPATH/libs
	RLGLUEJAVAJARPATH=$COMMONLIBS/rl-glue-java/JavaRLGlueCodec.jar
	rm -Rf $DISTDIR
	mkdir $DISTDIR
	mkdir $THISPROJECTDISTDIR

	svn export --quiet src $THISPROJECTDISTDIR/src
	mkdir -p $DISTDIR/system/common/libs/rl-glue-java/
	#These Two aren't needed, but they come in handy
	mkdir -p $DISTDIR/system/common/libs/rl-viz
	mkdir -p $DISTDIR/products

	mkdir -p $DISTDIR/system/scripts
	mkdir -p $DISTDIR/system/bin
	svn export --quiet $RLGLUEJAVAJARPATH $DISTDIR/system/common/libs/rl-glue-java/JavaRLGlueCodec.jar
	svn export --quiet $SYSTEMPATH/scripts/rl-library-includes.sh $DISTDIR/system/scripts/rl-library-includes.sh
	svn export --quiet $SYSTEMPATH/bin/pkill $DISTDIR/system/bin/pkill

	#Going to use sed to change the relative path to the system directory in the run.bash file
	#We will use bar | as a delimiter
	#Looks for something like: basePath=../../../77s_5/../../../../blargl"
	#And changes it to: basePath=..
	sed 's|basePath=[a-z,A-Z,0-9,.,/,-,_]*$|basePath=..|'<run.bash >$THISPROJECTDISTDIR/run.bash

}

#This will go into $DISTDIR, build the project, remove the build directory, back out
#tar and gzip the directory, and then delete the directory.
javaDistributionBuildJarAndGzip(){
	pushd $DISTDIR
	tar -cf ../$DISTNAME.tar *
	popd
	gzip $DISTNAME.tar
	echo Successfully created $DISTFILENAME
}

javaDistributionUploadFile(){
	echo -n "  Uploading file to google code..."
	python $COMMONPATH/scripts/googlecode_upload.py -f $SVNPASSWORDFILE -s "$PROJECTNAME $VERSION" -p rl-library --labels=Type-Archive,OpSys-All,Language-Java,RLType-$PROJECTTYPE $DISTFILENAME
	echo "File uploaded."
}
javaDistributionUpdateWiki(){
	echo -n "  Updating Wiki..."
	PYTHONPATH=$COMMONPATH/libs python $COMMONPATH/scripts/wiki_update.py --wikipasswordfile=$WIKIPASSWORDFILE --jarname="NONE" --filename="$DISTFILENAME" --wikipage="$WIKIPAGENAME" --projectdir="$DISTNAME" 
	echo "Wiki updated."
}

#Delete $DISTDIR and $DISTFILENAME
javaDistributionCleanup(){
	echo "  Cleaning up..."
	echo "     Deleting $DISTDIR"
	rm -Rf $DISTDIR
	echo "     Deleting $DISTFILENAME"
	rm -Rf $DISTFILENAME
	echo "     Deleting temporary wiki checkout"
	rm -Rf wikis
	echo "  Done cleanup."
}

