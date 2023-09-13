#!/bin/bash

echo "Patch Installation for BlueSeer Version 6.7" 

echo ""
echo ""
echo -n "Enter the number beside the database type and press enter (1=sqlite  2=mysql):"
read dbtype


currdir=$(pwd)

cd ../../data
datadir=$(pwd)

cd $currdir
cp .patchsqlv6.7 /$datadir/

if [[ -d "../../jasper" ]];
then
	echo "matched directory for patch install "
	sleep 3
	echo "copying jasper directory..."
	sleep 2
	cp -r $currdir/jasper ../../jasper
	echo "copying zebra directory..."
	sleep 2
	cp -r $currdir/zebra ../../zebra
	echo "copying jar files..."
	sleep 2
	cp -r $currdir/dist/blueseer.jar ../../dist/
	cp -r $currdir/dist/bsmf.jar ../../dist/
	echo "copying patch number..."
	sleep 2
	cp -r $currdir/.patch ../../
else
	echo "patch is not being executed from proper directory"
	echo "patch needs to be ran from blueseer parentdir/patches/<patchdir>"
	sleep 5
fi

if [[ dbtype == "1" ]];
then
	echo "loading sqlite relevant schema changes ..."
	sleep 2
	cd $datadir
	./sqlite3 bsdb.db <.patchsqlv6.7
	cd $currdir
else
	echo "loading mysql relevant schema changes ..."
	sleep 2
	./mysql_patch.sh
fi

echo ""
echo ""
echo "Patch Install complete!"
echo ""
echo ""
