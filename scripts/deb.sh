#!/bin/bash

# script to create directory structure for deb packaging

fromdir="/home/vaughnte/bs/blueseer/sf"
bs="blueseer-4.1.5"
bsshare="$bs/usr/share/blueseer"

rm -fR $bs

echo "making directories..."
mkdir $bs
mkdir $bs/DEBIAN
mkdir $bs/usr
mkdir $bs/usr/bin
mkdir $bs/usr/share
mkdir $bs/usr/share/blueseer

echo "Package: blueseer" >$bs/DEBIAN/control
echo "Version: 4.1.5" >>$bs/DEBIAN/control
echo "Architecture: all" >>$bs/DEBIAN/control
echo "Maintainer: Terry Vaughn <vaughnte2222gmail.com>" >>$bs/DEBIAN/control
echo "Homepage: http://www.blueseer.com" >>$bs/DEBIAN/control
echo "Description:  Free ERP for manufacturing" >>$bs/DEBIAN/control


echo "creating blueseer config file...."
echo "DBTYPE=sqlite" >$bsshare/bsconfig
echo "DB=data/bsdb.db" >>$bsshare/bsconfig
echo "USER=bs_user" >>$bsshare/bsconfig
echo "PASS=bspasswd" >>$bsshare/bsconfig
echo "IP=localhost" >>$bsshare/bsconfig
echo "PORT=3306" >>$bsshare/bsconfig
echo "DRIVER=org.sqlite.JDBC" >>$bsshare/bsconfig

cp -R ../dist $bsshare/
cp -R $fromdir/data $bsshare/
cp -R $fromdir/zebra $bsshare/
cp -R $fromdir/edi $bsshare/
cp -R $fromdir/temp $bsshare/
cp -R $fromdir/jasper $bsshare/
cp -R $fromdir/images $bsshare/

rm -f $bs/usr/bin/blueseer
echo "cd /usr/share/blueseer; java -cp \".:dist/*\" bsmf.MainFrame" >>$bs/usr/bin/blueseer

chmod -R 0777 $bs


#echo -n "Enter the administrator password for the MySQL Database: "
#read PASS


dpkg -b $bs

echo "you're done"

