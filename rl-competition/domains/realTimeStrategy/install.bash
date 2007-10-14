#Note BOOST doesn't like being on a path with spaces in it, it won't build right
#Maybe we should have them install BOOST to the rl-competition/support folder or something, so that it doesn't pollute their systems.
#Note BOOST take's a while to build (20 mins on my macbook), once its started, go get a coffee

$realTimeStrategyDirectory=$PWD

cd misc
#unpack boost
tar xjvf boost-1_34_1.tar.bz2
cd boost_1_34_1/
#Run configure without Boost.Regex support, because OS X doesn't have it.  Do we need it?
./configure --prefix=$HOME --without-libraries=regex
make
make install
cd $HOME/include
ln -s boost-1_34_1/boost

#OK that all works, go make the test
cd $realTimeStrategyDirectory/misc/boost_test
make
./boost_test 

#Some other notes
#-------------------
#There are somewhat detailed explanations about how to update the RL-Glue files inside this project.  We can probably write a script to automatically pluck those files, and cut it from the help.
#Also, there are extraneous targets, like the experiment program and the RL_glue executable.  We can just use the standard ones, right? (Anything to simplify the non-standard build process)