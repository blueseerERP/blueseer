#!/bin/bash



echo ''
echo ''
echo ''
echo "NOTE: This script loads patch related changes into "
echo "your MySQL instance of the BlueSeer database bsdb. "
echo ""
echo ""
echo ""
echo -n "Enter the IP addr of database server: 'localhost': "
read IP


echo -n "Enter the administrator password for the MySQL Database: "
read PASS

ROOT=root
DB=bsdb

MYSQL_PWD=$PASS
export MYSQL_PWD

echo ""
echo ""
echo "updating BlueSeer Schema....."
mysql $DB -u $ROOT <.patchsqlv6.5


echo ''
echo ''
echo ''
echo 'Finished Patch install!   '
echo ''
echo ''
echo ''

unset MYSQL_PWD
