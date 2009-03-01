#summary This is the page for download information and details about the Java Helicopter Domain, Version rVERSION.
#labels Language-Java,Type-Environment,Page-Download
#sidebar Downloads


<wiki:comment>
This README.txt is also the Wiki page that is hosted online at:
http://code.google.com/p/rl-library/wiki/HelicopterJava

So, it is in Wiki Syntax.  It's still pretty easy to read.
</wiki:comment>

<wiki:toc />


= RL-Library Java Version of Helicopter =

For full details, please visit:
[http://library.rl-community.org/environments/helicopter]

Download Link: [FILELINK FILENAME] [FILEDETAILSLINK (Details)]

This environment is licensed under the Apache 2.0 License.
See LICENSE.txt for details.

== Using This Download ==
Before diving into this, you may want to check out [GettingStarted the getting started guide].

This download can be used to augment your existing local RL-Library (if you have one), or as the basis to start a new one.

=== This Is Your First Project ===
{{{
#Create a directory for your rl-library. Call it whatever you like.
mkdir rl-library
}}}

That's all you have to do special for the *first time* you download a rl-library component.  Continue on now
to the next section.

=== Adding To An Existing RL-Library Download ===

{{{
#First, download the file.  Depending on your platform, you might have to do this manually with a web browser. 

#If you are on Linux, you can use wget which will download FILENAME for you
wget FILELINK

#Copy the download to your local rl-library folder (whatever it is called)
cp FILENAME rl-library/
cd rl-library

#This will add any project-specific things necessary to system and products folders
#It will also create a folder for this particular project
tar -zxf FILENAME

#Clean up
rm FILENAME
}}}

== Compiling This Environment ==
You must have ANT installed to build this environment using these instructions.

You don't have to compile this, because the JAR file has been compiled
and placed into the products directory already. However, if you want to 
make changes and recompile, you can simply type:
{{{
>$ ant clean
>$ ant build
}}}

== Running This Environment ==
You can run this environment by typing:
{{{
>$ java -jar products/Helicopter.jar
}}}

You can also use it in conjunction with RL-Library by putting the JAR file
products/Helicopter.jar in the appropriate directory, as long as the 
RL-Viz library jar file is in the appropriate relative location from
where you put Helicopter.jar.  The location is:
../system/common/libs/rl-viz/RLVizLib.jar

== Getting Help ==
Please send all questions to either the current maintainer (below) or to the 
[http://groups.google.com/group/rl-library RL-Library mailing list].

== Authors ==
Various, over the years, including:

 * [http://www.cs.berkeley.edu/%7Epabbeel Pieter Abbeel]
 * [http://www.stanford.edu/%7Eacoates Adam Coates]
 * [http://robotics.stanford.edu/%7Eang/ Andrew Y. Ng]
 * Mark Lee
 * Matt Radkie
 * [http://research.tannerpages.com Brian Tanner]

=== Current Maintainer (looking for replacement) ===
 * [http://research.tannerpages.com Brian Tanner]
 * btanner@rl-community.org
 * [http://research.tannerpages.com]



