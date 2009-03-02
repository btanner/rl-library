#  This is meant to be sourced by a Java project that has:
#
#  - $PROJECTNAME variable set.  
#	This will partially determine the name of the file we will create.
#	Something like MountainCar-Java is good
#
#  - $PROJECTTITLE variable set.  
#	Regular speech name of environment.  Mountain Car for example.
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
#   - $HOMEURL variable set
#	This should be a the public homepage for this environment/agent
#   - $JARNAME variable set
#	This should be the name of the JAR that is built.  Like MountainCar.jar
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
	if [ -z $SYSTEMPATH ]
	then
		echo 
		echo "   ERROR: You Must set the SYSTEMPATH variable."
		echo 
	  exit 1
	fi

	if [ -z $PROJECTTYPE ]
	then
		echo 
		echo "   ERROR: You Must set the PROJECTTYPE variable."
		echo 
	  exit 1
	fi

	if [ -z $PROJECTNAME ]
	then
		echo 
		echo "   ERROR: You Must set the PROJECTNAME variable."
		echo 
	  exit 1
        fi

	if [ -z $PROJECTTITLE ]
	then
		echo 
		echo "   ERROR: You Must set the PROJECTTITLE variable."
		echo 
	  exit 1
	fi

	if [ -z $WIKIPAGENAME ]
	then
		echo 
		echo "   ERROR: You Must set the WIKIPAGENAME variable."
		echo 
	  exit 1
	fi

	if [ -z $SVNPASSWORDFILE ]
	then
		echo 
		echo "   ERROR: You Must set the SVNPASSWORDFILE variable."
		echo 
	  exit 1
	fi

	if [ -z $LANGUAGE ]
	then
		echo 
		echo "   ERROR: You Must set the LANGUAGE variable."
		echo 
	  exit 1
	fi

	if [ -z $HOMEURL ]
	then
		echo 
		echo "   ERROR: You Must set the HOMEURL variable."
		echo 
	  exit 1
	fi

	if [ -z $JARNAME ]
	then
		echo 
		echo "   ERROR: You Must set the HOMEURL variable."
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
	#And changes it to: name="baseLibraryDir" value="."
	sed 's|name="baseLibraryDir" value="\([a-z,A-Z,0-9,.,/,-,_]*\)"|name="baseLibraryDir" value=".."|' <build.xml > $THISPROJECTDISTDIR/build.xml

	#First, strip all of the comments out of README.txt
	sed -e '/^##/D' <README.txt > README.out

	#Now, fill in the template sections
	sed -i  '/\$USINGTHISDOWNLOADTEMPLATE\$/ {
		r '${SYSTEMPATH}'/common/scripts/templates/using-this-download.template
		d
	}
		/\$WIKICOMMENTTEMPLATE\$/ {
		r '${SYSTEMPATH}'/common/scripts/templates/wiki-comment.template
		d
	}
		/\$INTRODUCTIONTEMPLATE\$/ {
		r '${SYSTEMPATH}'/common/scripts/templates/introduction.template
		d
	}
		/\$COMPILINGTEMPLATE\$/ {
		r '${SYSTEMPATH}'/common/scripts/templates/compiling.template
		d
	}
		/\$HELPTEMPLATE\$/ {
		r '${SYSTEMPATH}'/common/scripts/templates/help.template
		d
	}
	' README.out


	#Use sed to also splice the variables into README.out
	#README.out has some placeholders for FILENAME FILELINK and FILEDETAILSLINK
	#This is sortof gross multiline syntax, but whatever.
	sed -i '
		s|\$FILENAME\$|'"${DISTFILENAME}"'|
		s|\$FILELINK\$|'"${DISTFILEURL}"'|
		s|\$FILEDETAILSLINK\$|'"${DISTFILEINFOURL}"'|
		s|\$VERSION\$|'"${VERSION}"'|
		s|\$LANGUAGE\$|'"${LANGUAGE}"'|
		s|\$PROJECTTITLE\$|'"${PROJECTTITLE}"'|
		s|\$PROJECTNAME\$|'"${PROJECTNAME}"'|
		s|\$PROJECTTYPE\$|'"${PROJECTTYPE}"'|
		s|\$HOMEURL\$|'"${HOMEURL}"'|
		s|\$JARNAME\$|'"${JARNAME}"'|
		s|\$WIKIURL\$|'"${PROJECTTYPE}"'|
		s|\$PROJECTTYPE\$|'"${PROJECTTYPE}"'|
		s|\$PROJECTDIR\$|'"${DISTDIR}"'|
	' README.out

	mv README.out $THISPROJECTDISTDIR/README.txt

}

#This will go into $DISTDIR, build the project, remove the build directory, back out
#tar and gzip the directory, and then delete the directory.
javaDistributionBuildJarAndGzip(){
	pushd $THISPROJECTDISTDIR
	ant -quiet build
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
	python $COMMONPATH/scripts/googlecode_upload.py -f $SVNPASSWORDFILE -s "$PROJECTNAME $VERSION" -p rl-library --labels=Type-Archive,OpSys-All,Language-Java,RLType-$PROJECTTYPE $DISTFILENAME
	echo "File uploaded."
}
javaDistributionUpdateWiki(){
	echo -n "  Updating Wiki..."
	SVNUSERNAME=$(cat $SVNPASSWORDFILE | awk '{print $1}')
	SVNPASSWORD=$(cat $SVNPASSWORDFILE | awk '{print $2}')
	svn co --quiet --username $SVNUSERNAME --password $SVNPASSWORD https://rl-library.googlecode.com/svn/wiki wikis 
	cp $THISPROJECTDISTDIR/README.txt wikis/$WIKIPAGENAME.wiki
	svn commit wikis/$WIKIPAGENAME.wiki -m "Automatic update of Wiki page when doing release of $PROJECTNAME"
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

