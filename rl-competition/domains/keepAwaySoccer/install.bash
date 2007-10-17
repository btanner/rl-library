#/bin/bash

#Install instructions for robocup soccer
export BOOST_ROOT=$HOME
export BASE_DIR=$PWD
export SOCCER_DIR=$BASE_DIR/soccer
export PATH=$SOCCER_DIR/bin:$PATH

export SRC_DIR=archived_source

export RCSSBASE_VERSION=rcssbase-11.1.0
export RCSSBASE_FILE=$RCSSBASE_VERSION.tar.bz2

export RCSSSERVER_VERSION=rcssserver-11.1.2
export RCSSSERVER_FILE=$RCSSSERVER_VERSION.tar.bz2

export RCSSMONITOR_VERSION=rcssmonitor-11.1.1
export RCSSMONITOR_FILE=$RCSSMONITOR_VERSION.tar.bz2

export KEEPAWAY_VERSION=keepaway-0.6
export KEEPAWAY_FILE=$KEEPAWAY_VERSION.tar.gz

export RCSSJAVA_VERSION=rcssjava-0.1
export RCSSJAVA_FILE=$RCSSJAVA_VERSION.tar.gz 

export RLGLUEKWY_VERSION=RL-GlueKWY
export RLGLUEKWY_FILE=$RLGLUEKWY_VERSION.tar.gz 

BASE_INSTALLED=1
SERVER_INSTALLED=1
MONITOR_INSTALLED=1
KEEPAWAY_INSTALLED=1
RCSSJAVA_INSTALLED=1
GLUEKWY_INSTALLED=0

if [ $BASE_INSTALLED -eq 0 ]; then
echo "----------------------------"
echo "----------------------------"
echo "----------------------------"
echo "--------BASE----------------"
echo "----------------------------"
echo "----------------------------"
echo "----------------------------"
################# UNPACK RCSSBASE ##############
cp archived_source/$RCSSBASE_FILE $SOCCER_DIR
cd $SOCCER_DIR
tar xjf $RCSSBASE_FILE
rm -f $RCSSBASE_FILE

################# MAKE RCSSBASE ##############
cd $SOCCER_DIR/$RCSSBASE_VERSION

./configure --with-boost=$BOOST_ROOT --prefix=$SOCCER_DIR
make
make install

fi
export RCSSBASEDIR=$SOCCER_DIR

if [ $SERVER_INSTALLED -eq 0 ]; then
echo "----------------------------"
echo "----------------------------"
echo "----------------------------"
echo "--------SERVER--------------"
echo "----------------------------"
echo "----------------------------"
echo "----------------------------"

################# UNPACK RCSSSERVER ##############
cd $BASE_DIR
cp archived_source/$RCSSSERVER_FILE $SOCCER_DIR

cd $SOCCER_DIR
tar xjf $RCSSSERVER_FILE
rm -f $RCSSSERVER_FILE

################# MAKE RCSSSERVER ##############
cd $SOCCER_DIR/$RCSSSERVER_VERSION
export LDFLAGS="-L$RCSSBASEDIR/lib"
export CXXFLAGS="-I$RCSSBASEDIR/include"
export LLIMPORTER_PATH="$RCSSBASEDIR/bin"

./configure --prefix=$RCSSBASEDIR --with-boost=$BOOST_ROOT
make
make install

fi

if [ $MONITOR_INSTALLED -eq 0 ]; then

echo "----------------------------"
echo "----------------------------"
echo "----------------------------"
echo "--------MONITOR-------------"
echo "----------------------------"
echo "----------------------------"
echo "----------------------------"
################# UNPACK RCSSMONITOR ##############

cd $BASE_DIR
cp archived_source/$RCSSMONITOR_FILE $SOCCER_DIR

cd $SOCCER_DIR
tar xjf $RCSSMONITOR_FILE
rm -f $RCSSMONITOR_FILE


################# MAKE RCSSMONITOR ##############
##OS X
x11Path=/usr/X11R6
cd $SOCCER_DIR/$RCSSMONITOR_VERSION
./configure --prefix=$RCSSBASEDIR --with-boost=$BOOST_ROOT LDFLAGS="-L/usr/X11R6/lib" --includedir=$x11Path/include --libdir=$x11Path/lib

make
make install

fi


if [ $KEEPAWAY_INSTALLED -eq 0 ]; then
echo "----------------------------"
echo "----------------------------"
echo "----------------------------"
echo "--------KEEPAWAY------------"
echo "----------------------------"
echo "----------------------------"
echo "----------------------------"

################# UNPACK KEEPAWAY ##############
cd $BASE_DIR
cp archived_source/$KEEPAWAY_FILE $SOCCER_DIR


cd $SOCCER_DIR
tar zxf $KEEPAWAY_FILE
rm -f $KEEPAWAY_FILE

################# MAKE KEEPAWAY ##############
cd $SOCCER_DIR/$KEEPAWAY_VERSION
cd player

make depend
make
cd ../tools
make

mkdir $SOCCER_DIR/bin
cp hist killserver kunzip kwyzipper kzip monitor winsum $SOCCER_DIR/bin

fi

if [ $RCSSJAVA_INSTALLED -eq 0 ]; then
echo "----------------------------"
echo "----------------------------"
echo "----------------------------"
echo "--------RCSSJAVA------------"
echo "----------------------------"
echo "----------------------------"
echo "----------------------------"
################# UNPACK RCSSJAVA ##############
cd $BASE_DIR
cp archived_source/$RCSSJAVA_FILE $SOCCER_DIR

cd $SOCCER_DIR
tar zxf $RCSSJAVA_FILE
rm -f $RCSSJAVA_FILE

fi

################# UNPACK RLGLUE PLAYER ##############

if [ $GLUEKWY_INSTALLED -eq 0 ]; then
echo "----------------------------"
echo "----------------------------"
echo "----------------------------"
echo "--------RLGLUEKWY------------"
echo "----------------------------"
echo "----------------------------"
echo "----------------------------"
cd $BASE_DIR
mv $SOCCER_DIR/$KEEPAWAY_VERSION/keepaway.sh $SOCCER_DIR/$KEEPAWAY_VERSION/keepaway_orig.sh 
cp archived_source/$RLGLUEKWY_FILE $SOCCER_DIR/$KEEPAWAY_VERSION/player

cd $SOCCER_DIR/$KEEPAWAY_VERSION/player
tar zxf $RLGLUEKWY_FILE
rm -f $RLGLUEKWY_FILE

cd $SOCCER_DIR/$KEEPAWAY_VERSION
cd player

cp $BASE_DIR/archived_source/Makefile.brian ./Makefile.brian

make depend -f Makefile.brian
make -f Makefile.brian

cp $BASE_DIR/archived_source/keepaway.sh $SOCCER_DIR/$KEEPAWAY_VERSION/keepaway.sh 


fi