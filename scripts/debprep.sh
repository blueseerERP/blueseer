#!/bin/bash

# script to create directory structure for deb packaging

bs="blueseer.sqlite.linux.v42"
bsshare="$bs/usr/share/blueseer"

cp -R ../sf/linux/blueseer.sqlite.linux.v42 $bs
mkdir $bs/usr
mkdir $bs/usr/share
mkdir $bs/usr/share/blueseer
mkdir $bs/usr/bin

echo "creating blueseer config file...."
echo "DBTYPE=sqlite" >$bsshare/bsconfig
echo "DB=data/bsdb.db" >>$bsshare/bsconfig
echo "USER=bs_user" >>$bsshare/bsconfig
echo "PASS=bspasswd" >>$bsshare/bsconfig
echo "IP=localhost" >>$bsshare/bsconfig
echo "PORT=3306" >>$bsshare/bsconfig
echo "DRIVER=org.sqlite.JDBC" >>$bsshare/bsconfig

cp bslogging.properties $bsshare/
cp -R ../dist $bsshare/
cp -R ../sf/data $bsshare/
cp -R ../sf/zebra $bsshare/
cp -R ../sf/edi $bsshare/
cp -R ../sf/temp $bsshare/
cp -R ../sf/jasper $bsshare/
cp -R ../sf/images $bsshare/

rm -f $bs/usr/bin/blueseer
echo "cd /usr/share/blueseer; java -D\"java.util.logging.config.file=bslogging.properties\" -cp \".:dist/*\" bsmf.MainFrame" >>$bs/usr/bin/blueseer

chmod -R 0755 $bs
chmod -R 0777 $bsshare/data
chmod -R 0777 $bsshare/zebra
chmod -R 0777 $bsshare/edi
chmod -R 0777 $bsshare/temp
chmod -R 0777 $bsshare/jasper
chmod -R 0777 $bsshare/images
chmod -R 0777 $bsshare/data/bsdb.db


#echo -n "Enter the administrator password for the MySQL Database: "
#read PASS


dpkg -b $bs

echo "you're done"
