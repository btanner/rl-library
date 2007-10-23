#Note BOOST doesn't like being on a path with spaces in it, it won't build right
#Note BOOST take's a while to build (20 mins on my macbook), once its started, go get a coffee

realTimeStrategyDirectory="$PWD"
basedir=`basename $realTimeStrategyDirectory`

# When building boost, this will point to rl-competition/system
# note: path has no spaces, as written. It might work as a trick.. 
DEST_DIR="../../../../system"

if [ "$basedir" != "realTimeStrategy" ]
then
  echo "Sorry, you must run this script from the main ortlite directory (domains/realTimeStrategy)"
  echo "eg.   scripts/install-boost.bash"
  exit -1; 
fi

echo "Installing boost .."
cd misc
#unpack boost
tar xjvf boost_1_34_1.tar.bz2
cd boost_1_34_1/
#Run configure without Boost.Regex support, because OS X doesn't have it.  Do we need it?
./configure --prefix=$DEST_DIR --without-libraries=regex
make
make install
cd $DEST_DIR/include
ln -s boost-1_34_1/boost

echo "Building and running the boost test .. "
#OK that all works, go make the test
cd $realTimeStrategyDirectory/misc/boost_test
make
./boost_test

echo "Done. "

#Some other notes
#-------------------
#There are somewhat detailed explanations about how to update the RL-Glue files inside this project.  We can probably write a script to automatically pluck those files, and cut it from the help.
#Also, there are extraneous targets, like the experiment program and the RL_glue executable.  We can just use the standard ones, right? (Anything to simplify the non-standard build process)
