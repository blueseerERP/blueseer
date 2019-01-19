#!/bin/bash

# script to create directory structure for deb packaging

bs="blueseer-4.1.5"
bsshare="$bs/usr/share/blueseer"

echo "creating blueseer config file...."
echo "DBTYPE=sqlite" >$bsshare/bsconfig
echo "DB=data/bsdb.db" >>$bsshare/bsconfig
echo "USER=bs_user" >>$bsshare/bsconfig
echo "PASS=bspasswd" >>$bsshare/bsconfig
echo "IP=localhost" >>$bsshare/bsconfig
echo "PORT=3306" >>$bsshare/bsconfig
echo "DRIVER=org.sqlite.JDBC" >>$bsshare/bsconfig

cp -R dist $bsshare/
cp -R data $bsshare/
cp -R zebra $bsshare/
cp -R edi $bsshare/
cp -R temp $bsshare/
cp -R jasper $bsshare/
cp -R images $bsshare/

rm -f $bs/usr/bin/blueseer
echo "cd /usr/share/blueseer; java -cp \".:dist/*\" bsmf.MainFrame" >>$bs/usr/bin/blueseer

chmod -R 0755 $bs
chmod -R 0777 $bsshare/data
chmod -R 0777 $bsshare/data/bsdb.db


#echo -n "Enter the administrator password for the MySQL Database: "
#read PASS


dpkg -b $bs

echo "you're done"

