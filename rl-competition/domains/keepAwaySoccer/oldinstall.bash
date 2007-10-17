#/bin/bash

#Install instructions for robocup soccer
export BOOST_ROOT=$HOME
export BASE_DIR=$PWD
export SOCCER_DIR=$BASE_DIR/oldSoccer
export SRC_DIR=archived_source

export RCSSBASE_VERSION=rcssbase-9.4.5
export RCSSBASE_FILE=$RCSSBASE_VERSION.tar.bz2

export RCSSSERVER_VERSION=rcssserver-9.4.5
export RCSSSERVER_FILE=$RCSSSERVER_VERSION.tar.bz2

export RCSSMONITOR_VERSION=rcssmonitor-9.3.7
export RCSSMONITOR_FILE=$RCSSMONITOR_VERSION.tar.bz2

export KEEPAWAY_VERSION=keepaway-0.6
export KEEPAWAY_FILE=$KEEPAWAY_VERSION.tar.gz

export RCSSJAVA_VERSION=rcssjava-0.1
export RCSSJAVA_FILE=$RCSSJAVA_VERSION.tar.gz 

export RLGLUEKWY_VERSION=RL-GlueKWY
export RLGLUEKWY_FILE=$RLGLUEKWY_VERSION.tar.gz 

BASE_INSTALLED=0
SERVER_INSTALLED=0
MONITOR_INSTALLED=0
KEEPAWAY_INSTALLED=0
RCSSJAVA_INSTALLED=0
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

cd $RCSSBASE_VERSION

################# MAKE RCSSBASE ##############
./configure --prefix=$SOCCER_DIR
make
make install

export RCSSBASE=$SOCCER_DIR

fi
exit(1)

################# UNPACK RCSSSERVER ##############
cd $BASE_DIR
cp archived_source/$RCSSSERVER_FILE $SOCCER_DIR

cd $SOCCER_DIR
tar xjf $RCSSSERVER_FILE
rm -f $RCSSSERVER_FILE

cd $RCSSSERVER_VERSION
################# MAKE RCSSSERVER ##############
#./configure --with-boost=$BOOST_ROOT --prefix=$SOCCER_DIR
#make
#make
#make install

################# UNPACK RCSSMONITOR ##############
cd $BASE_DIR
cp archived_source/$RCSSMONITOR_FILE $SOCCER_DIR

cd $SOCCER_DIR
tar xjf $RCSSMONITOR_FILE
rm -f $RCSSMONITOR_FILE

cd $RCSSSERVER_VERSION
################# MAKE RCSSSERVER ##############




################# UNPACK KEEPAWAY ##############
cd $BASE_DIR
cp archived_source/$KEEPAWAY_FILE $SOCCER_DIR

cd $SOCCER_DIR
tar zxf $KEEPAWAY_FILE
rm -f $KEEPAWAY_FILE

################# UNPACK RCSSJAVA ##############
cd $BASE_DIR
cp archived_source/$RCSSJAVA_FILE $SOCCER_DIR

cd $SOCCER_DIR
tar zxf $RCSSJAVA_FILE
rm -f $RCSSJAVA_FILE
