common folder
-------------
Last updated September 2009

The common folder holds things that are useful not only for the RL-Library
specifically, but also for other projects that want to use aspects of the 
RL-Library.  By exporting a copy of the common directory (or even better, 
using subversion to make an "externals" link to it), others can get the 
benefit of these resources.

Current resources:

common/libs/
	- rl-glue java codec
	- rl-viz libraries
	- junit.jar
	- additional ant tasks

common/ant/
	- various ant scripts that can be included to make it easy to build
	building RL-Glue and RL-Viz compatible projects

common/scripts/
	- scripts for uploading files to google code, updating the RL-Library
	wiki, etc.
