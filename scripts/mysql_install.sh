#!/bin/bash

###  This script essentially does three things :
###  1) creates database schema
###  2) creates user 'bs_user' with password
###  3) loads some default data from
###  comma delimited (and some semicolon delimited) files found in the data/* directory
###
###
###  The database schema can be found in data/blueseer.schema (See below 
###  where it attempts to create this
###  User is a simple 'create' statement....the bs.cfg file created below has 
###  the USER and PASS variables set with 'bs_user' and 'bspasswd' respectively.
###  new versions of mysql have stricter requirements for the password.   
###  You should obviously change this in a production environment anyway.  If you do,
###  make sure you change it in the bs.cfg file as well...for each client user
###  NOTE!!!  newer version of MySQL set security variables by default to
###  prevent loading data from local directories.  You will have to adjust the 
###  Global variable 'local_infile' in your MySQL instance.  Something like:
###  'show global variables like 'local_infile';
###  'set global local_infile = 'ON';

defaulthost=localhost
defaultlang=en

echo ''
echo ''
echo "NOTE: This script file loads data from files in the data dir into "
echo "your MySQL instance.  Newer versions of MySQL have a security  "
echo "feature which disables loading using 'local data infile'.  "
echo "You may need to ensure this is enabled before loading the data. "
echo "Read the comments in this script for determining this setting "
echo ""
echo ""

echo -n "Previous Data will be lost... are you sure you want to install database? (enter: yes or no): "
read var
if [[ "$var" != "yes" ]]; then
  exit 0
fi

echo -n "Enter the IP addr of database server ex: 10.0.0.2 or localhost (default: localhost): "
read IP
if [[ "$IP" == "" ]]; then
	IP=$defaulthost
fi

echo -n "Enter the administrator password for the MySQL Database: "
read PASS

echo -n "Enter database name (default: bsdb): "
read DB
if [[ "$DB" == "" ]]; then
	DB="bsdb"
fi

echo -n "Enter the two character language code (default: en): "
read LANG

if [ ${#LANG} -ge 3 ]; then echo "you entered more than two characters...try again"; exit
else
	echo "you enter $LANG ...proceeding"
fi

if [[ "$LANG" == "" ]]; then
	LANG=$defaultlang
fi

LANG=$(echo $LANG | tr '[:upper:]' '[:lower:]')

COUNTRY="US"
if [[ "$LANG" == "es" ]]; then
	COUNTRY="ES"
fi
if [[ "$LANG" == "fr" ]]; then
	COUNTRY="FR"
fi
if [[ "$LANG" == "tr" ]]; then
	COUNTRY="TR"
fi
if [[ "$LANG" == "de" ]]; then
	COUNTRY="DE"
fi
if [[ "$LANG" == "ro" ]]; then
  COUNTRY="RO"
fi
if [[ "$LANG" == "it" ]]; then
  COUNTRY="IT"
fi

echo "creating blueseer config file...."
echo "DBTYPE=mysql" >bs.cfg
echo "DB=$DB" >>bs.cfg
echo "USER=bs_user" >>bs.cfg
echo "PASS=bspasswd" >>bs.cfg
echo "IP=$IP" >>bs.cfg
echo "PORT=3306" >>bs.cfg
echo "DRIVER=com.mysql.cj.jdbc.Driver" >>bs.cfg
echo "LANGUAGE=$LANG" >>bs.cfg
echo "COUNTRY=$COUNTRY" >>bs.cfg

cd data

echo "Creating temp my.cnf..."
if [ -f my.cnf ]; then
  rm -f my.cnf
fi
echo "[client]" >my.cnf
echo "user = root" >>my.cnf
echo "password = $PASS" >>my.cnf
echo "host = $IP" >>my.cnf
echo "local_infile = ON" >>my.cnf

echo "creating database schema for database $DB...."
mysql --defaults-extra-file=my.cnf -e "drop database if exists $DB;" 
mysql --defaults-extra-file=my.cnf -e "create database if not exists $DB character set utf8mb4 collate utf8mb4_unicode_ci;"
mysql --defaults-extra-file=my.cnf -e "drop user if exists 'bs_user'@'%' ;" 
mysql --defaults-extra-file=my.cnf -e "create user if not exists 'bs_user'@'%' identified by 'bspasswd';" 
mysql --defaults-extra-file=my.cnf -e "grant select,insert,delete,update on bsdb.* to 'bs_user'@'%';" 
mysql --defaults-extra-file=my.cnf -e "set global local_infile = 'ON';" 

#  The next line loads the database and table definitions
mysql --defaults-extra-file=my.cnf --local-infile=1 $DB <blueseer.schema 

echo "Loading some data....."
cd $LANG
mysql --defaults-extra-file=../my.cnf --local-infile=1 $DB <sq_mysql.txt
cd ..

rm -f my.cnf

echo ''
echo 'Finished install!'
echo ''
echo 'you can launch by running ./login.sh in the parent directory'
echo 'you may have to set perms to 755 on login.sh'
echo ''
echo 'Optionally...you can launch by typing the following at the command line: '
echo 'NOTE:  make sure you are in the parent blueseer directory!! '
echo ''
echo 'jre17/bin/java -cp ".:dist/*" bsmf.MainFrame'
echo ''
echo 'NOTE: login and password are admin and admin respectively'
echo ''
