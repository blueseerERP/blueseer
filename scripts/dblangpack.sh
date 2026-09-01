#!/bin/bash

if [ $# -eq 0 ]; then
    echo "Error: No parameters provided."
    echo "Usage: $0 en or specific lang code ..."
    exit 1  # Exit with a non-zero status to indicate a failure
fi

echo "Creating new default bsdb.db.... "
echo ""
echo ""

DB=bsdb


cd ../sf/data

if [ -f $1/bsdb.db ]; then
rm -f $1/bsdb.db
echo "removing old $1/bsdb.db..."
fi


echo "creating database schema...."
cd $1
sqlite3 bsdb.db <../blueseer.sqlite
sqlite3 bsdb.db <../sq.txt


echo ""
echo ""
echo ""
echo "finished..."

cd ..
