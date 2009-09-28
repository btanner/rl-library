#summary This is the page for download information and details about the Java Epsilon Greedy Fourier Basis Sarsa Lambda Agent Version 1.
#labels Language-Java,Type-Agent,Page-Download
#sidebar AgentSidebar

<wiki:comment>
This README.txt is also the Wiki page that is hosted online at:
Agent

So, it is in Wiki Syntax.  It's still pretty easy to read.
</wiki:comment>

<wiki:toc />


= RL-Library Java Version of Epsilon Greedy Fourier Basis Sarsa Lambda Agent =

The Fourier Basis is described in the following paper:<BR>
 G.D. Konidaris and S. Osentoski. Value Function Approximation in Reinforcement 
Learning using the Fourier Basis. Technical Report UM-CS-2008-19, 
Department of Computer Science, University of Massachusetts Amherst, June 2008. 

For full details, please visit:
[http://library.rl-community.org/agents/epsilongreedyfourierbasissarsalambdajava]

Download Link: [http://rl-library.googlecode.com/files/EpsilonGreedyFourierBasisSarsaLambda-Java.tar.gz EpsilonGreedyFourierBasisSarsaLambda-Java.tar.gz] [http://code.google.com/p/rl-library/downloads/detail?name=EpsilonGreedyFourierBasisSarsaLambda-Java.tar.gz (Details)]

This project is licensed under the Apache 2.0 License.
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

#If you are on Linux, you can use wget which will download EpsilonGreedyFourierBasisSarsaLambda-Java.tar.gz for you
wget http://rl-library.googlecode.com/files/EpsilonGreedyFourierBasisSarsaLambda-Java.tar.gz

#Copy the download to your local rl-library folder (whatever it is called)
cp EpsilonGreedyFourierBasisSarsaLambda-Java.tar.gz rl-library/
cd rl-library

#This will add any project-specific things necessary to system and products folders
#It will also create a folder for this particular project
tar -zxf EpsilonGreedyFourierBasisSarsaLambda-Java.tar.gz

#Clean up
rm EpsilonGreedyFourierBasisSarsaLambda-Java.tar.gz
}}}

== Compiling This Project ==
You must have ANT installed to build this project using these instructions.

You don't have to compile this, because the JAR file has been compiled
and placed into the products directory already. However, if you want to 
make changes and recompile, you can simply type:
{{{
>$ cd EpsilonGreedyFourierBasisSarsaLambda-Java
>$ ant clean

#this will update ../products/EpsilonGreedyFourierBasisSarsaLambda.jar
>$ ant build
}}}

== Running This Project ==
You can run this project by typing:
{{{
>$ java -jar products/EpsilonGreedyFourierBasisSarsaLambda.jar
}}}
You can also use it in conjunction with RL-Library by putting the JAR file
products/EpsilonGreedyFourierBasisSarsaLambda.jar in the appropriate directory, as long as the 
RL-Viz library jar file is in the appropriate relative location from
where you put EpsilonGreedyTileCodingSarsaLambda.jar.  The location is:
../system/common/libs/rl-viz/RLVizLib.jar

== Getting Help ==
Please send all questions to either the current maintainer (below) or to the 
[http://groups.google.com/group/rl-library RL-Library mailing list].


== Current Maintainer ==
 * [http://www-all.cs.umass.edu/~gdk/ George Konidaris]
 * gdk@cs.umass.edu
 * [http://www-all.cs.umass.edu/~gdk/]




