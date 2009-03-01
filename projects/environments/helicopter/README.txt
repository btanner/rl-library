#summary This is the page for download information and details about the Java Helicopter Domain.
#labels Language-Java,Type-Environment,Page-Download
#sidebar Downloads


<wiki:comment>
This README.txt is also the Wiki page that is hosted online at:
http://code.google.com/p/rl-library/wiki/HelicopterJava

So, it is in Wiki Syntax.  It's still pretty easy to read.
</wiki:comment>


= RL-Library Java Version of Helicopter =

For full details, please visit:
[http://library.rl-community.org/environments/helicopter]

Download Link: [FILELINK FILENAME] [FILEDETAILSLINK (Details)]

This environment is licensed under the Apache 2.0 License.
See LICENSE.txt for details.

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
<a href="/" rel="nofollow"></a>, <a href="http://www.stanford.edu/%7Eacoates/" rel="nofollow"></a>, <a href="http://robotics.stanford.edu/%7Eang/" rel="nofollow">Andrew Y. Ng</a>, Stanford University.  Ported by Mark Lee and <a href="http://research.tannerpages.com/" rel="nofollow">Brian Tanner</a> from C++ to Java for the<a href="http://rl-competition.org/" rel="nofollow"> 2008 RL-Competition</a> and beyond.

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



