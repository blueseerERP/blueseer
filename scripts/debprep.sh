#!/bin/bash

# script to create directory structure for deb packaging

bs="blueseer.sqlite.linux.v61"
bsshare="$bs/usr/share/blueseer"
rm -fR blueseer.sqlite.linux.v61
rm -f blueseer.sqlite.linux.v61.deb
cp -R ../sf/linux/blueseer.sqlite.linux.v61 $bs
mkdir $bs/usr
mkdir $bs/usr/share
mkdir $bs/usr/share/blueseer
mkdir $bs/usr/bin

echo "creating blueseer config file...."
echo "DBTYPE=sqlite" >$bsshare/bs.cfg
echo "DB=data/en/bsdb.db" >>$bsshare/bs.cfg
echo "USER=bs_user" >>$bsshare/bs.cfg
echo "PASS=bspasswd" >>$bsshare/bs.cfg
echo "IP=localhost" >>$bsshare/bs.cfg
echo "PORT=3306" >>$bsshare/bs.cfg
echo "DRIVER=org.sqlite.JDBC" >>$bsshare/bs.cfg
echo "LANGUAGE=en" >>$bsshare/bs.cfg
echo "COUNTRY=US" >>$bsshare/bs.cfg

cp bslogging.properties $bsshare/
cp ../.patch $bsshare/
cp -R ../dist $bsshare/
cp -R ../sf/data $bsshare/
cp -R ../sf/zebra $bsshare/
cp -R ../sf/edi $bsshare/
cp -R ../sf/temp $bsshare/
cp -R ../sf/patches $bsshare/
cp -R ../sf/jasper $bsshare/
cp -R ../sf/images $bsshare/
cp -R /home/vcs/jre17 $bsshare/

rm -f $bs/usr/bin/blueseer
echo "cd /usr/share/blueseer; jre17/bin/java -D\"java.util.logging.config.file=bslogging.properties\" -cp \".:dist/*\" bsmf.MainFrame" >>$bs/usr/bin/blueseer

chmod -R 0755 $bs
chmod -R 0777 $bsshare/data
chmod -R 0777 $bsshare/zebra
chmod -R 0777 $bsshare/edi
chmod -R 0777 $bsshare/temp
chmod -R 0777 $bsshare/patches
chmod -R 0777 $bsshare/jasper
chmod -R 0777 $bsshare/images
chmod -R 0777 $bsshare/data/en/bsdb.db


#echo -n "Enter the administrator password for the MySQL Database: "
#read PASS


dpkg -b $bs

echo "you're done"
