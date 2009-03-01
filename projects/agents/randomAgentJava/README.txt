#summary This is the page for download information and details about the Java Random Agent.
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
[http://library.rl-community.org/agents/randomagent]

Download Link: [FILELINK FILENAME] [FILEDETAILSLINK (Details)]

This agent is licensed under the Apache 2.0 License.
See LICENSE.txt for details.

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




