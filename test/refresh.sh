#!/bin/bash

echo "Installing BlueSeer.... "
echo ""
echo ""

DB=bsdb

echo "creating bsconfig file...."
echo "DBTYPE=sqlite" >>bsconfig
echo "DB=data/bsdb.db" >>bsconfig
echo "USER=bs_user" >>bsconfig
echo "PASS=bspasswd" >>bsconfig
echo "IP=localhost" >>bsconfig
echo "PORT=3306" >>bsconfig
echo "DRIVER=org.sqlite.JDBC" >>bsconfig

cd data

if [ -f bsdb.db ]; then
rm -f bsdb.db
fi


echo "creating database schema...."
sqlite3 bsdb.db <blueseer.sqlite

sqlite3 bsdb.db <sq.txt

echo ""
echo ""
echo ""
echo "finished install..."

cd ..

