#Note BOOST doesn't like being on a path with spaces in it, it won't build right
#Note BOOST take's a while to build (20 mins on my macbook), once its started, go get a coffee

supportDirectory="$PWD"
basedir=`basename $supportDirectory`

# When building boost, this will point to rl-competition/system
# note: path has no spaces, as written. It might work as a trick.. 
DEST_DIR_FROM_HERE="../../system/boost"
DEST_DIR_FROM_BOOST_DIR="../../$DEST_DIR_FROM_HERE"

if [ "$basedir" != "support" ]
then
  echo "Sorry, you must run this script from the rl-competition/domains/support directory"
  echo "eg.   ./install-boost.bash"
  echo "or    bash install-boost.bash"
  exit -1; 
fi

mkdir -p $DEST_DIR_FROM_HERE

echo "Installing boost .."
cd misc
#unpack boost
tar xjvf boost_1_34_1.tar.bz2
cd boost_1_34_1/
#Run configure without Boost.Regex support, because OS X doesn't have it.  Do we need it?
./configure --prefix=$DEST_DIR_FROM_BOOST_DIR --without-libraries=regex
make
make install
cd $DEST_DIR_FROM_BOOST_DIR/include
ln -s boost-1_34_1/boost

echo "Building and running the boost test .. "
#OK that all works, go make the test
cd $supportDirectory/misc/boost_test
make
./boost_test

echo "Done. "

