#!/bin/bash

echo "Installing BlueSeer.... "
echo ""
echo ""

DB=bsdb

echo "creating bs.cfg file...."
echo "DBTYPE=sqlite" >>bs.cfg
echo "DB=data/bsdb.db" >>bs.cfg
echo "USER=bs_user" >>bs.cfg
echo "PASS=bspasswd" >>bs.cfg
echo "IP=localhost" >>bs.cfg
echo "PORT=3306" >>bs.cfg
echo "DRIVER=org.sqlite.JDBC" >>bs.cfg

cd data/$1

if [ -f bsdb.db ]; then
rm -f bsdb.db
fi


echo "creating database schema...."
sqlite3 bsdb.db <../blueseer.sqlite

sqlite3 bsdb.db <../sq.txt

cp bsdb.db ../

echo ""
echo ""
echo ""
echo "finished install..."

cd ..

