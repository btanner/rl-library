#summary This is the page for download information and details about the Java Random Agent, Version rVERSION.
#labels Language-Java,Type-Agent,Page-Download
#sidebar Downloads


<wiki:comment>
This README.txt is also the Wiki page that is hosted online at:
http://code.google.com/p/rl-library/wiki/RandomAgentJava

So, it is in Wiki Syntax.  It's still pretty easy to read.
</wiki:comment>


= RL-Library Java Version of a Random Agent =
This Java agent is meant to select random actions for any RL-Glue domain whose actions are properly characterized by a valid task spec.  

It can handle multi-dimensional discrete and continuous actions. This agent does not learn.


For full details, please visit:
[http://library.rl-community.org/agents/random-agent-java]

Download Link: [FILELINK FILENAME] [FILEDETAILSLINK (Details)]

This agent is licensed under the Apache 2.0 License.
See LICENSE.txt for details.

== Using This Download ==
Before diving into this, you may want to check out [GettingStarted the getting started guide].

This download can be used to augment your existing local RL-Library (if you have one), or as the basis to start a new one.

=== This Is Your First Project ===
{{{
#Create a directory for your rl-library. Call it whatever you like.
mkdir rl-library

#Copy this new download into rl-library
cp FILENAME rl-library/
cd rl-library

#This will create folders system and products, which are used by all projects.
#It will also create a folder for this particular project
tar -zxf FILENAME

#Clean up
rm FILENAME
}}}

=== Adding To An Existing RL-Library Download ===
These directions are almost identical to above, but you don't create the rl-library folder.
{{{

#Copy the download to your local rl-library folder (whatever it is called)
cp FILENAME rl-library/
cd rl-library

#This will add any project-specific things necessary to system and products folders
#It will also create a folder for this particular project
tar -zxf FILENAME

#Clean up
rm FILENAME
}}}

== Compiling This Agent ==
You must have ANT installed to build this agent using these instructions.

You don't have to compile this, because the JAR file has been compiled
and placed into the products directory already. However, if you want to 
make changes and recompile, you can simply type:
{{{
>$ ant clean
>$ ant build
}}}

== Running This Agent ==
You can run this agent by typing:
{{{
>$ java -jar products/RandomAgent.jar
}}}

You can also use it in conjunction with RL-Library by putting the JAR file
products/RandomAgent.jar in the appropriate directory, as long as the 
RL-Viz library jar file is in the appropriate relative location from
where you put RandomAgent.jar.  The location is:
../system/common/libs/rl-viz/RLVizLib.jar

== Getting Help ==
Please send all questions to either the current maintainer (below) or to the 
[http://groups.google.com/group/rl-library RL-Library mailing list].

== Authors ==
Various, over the years, including:
 * Mark Lee
 * Leah Hackman
 * [http://research.tannerpages.com Brian Tanner]

=== Current Maintainer ===
 * [http://research.tannerpages.com Brian Tanner]
 * btanner@rl-community.org
 * [http://research.tannerpages.com]




