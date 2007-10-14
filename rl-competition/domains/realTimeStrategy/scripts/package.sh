#!/bin/sh
TMPDIR=/tmp/ortslite

PWD=`pwd`
CURDIR=`basename $PWD`

if [ "$CURDIR" != "ortslite" ]
then
  echo "Error: Must be in ortslite main directory when using this script."
  exit
fi

rm -rf $TMPDIR
mkdir -p $TMPDIR

cp -r . $TMPDIR

cd $TMPDIR

rm -rf `find | egrep "\.svn"`

make clean

cd ..

tar -czvf ortslite.tar.gz ortslite

echo "File package: /tmp/ortslite.tar.gz"


