#!/bin/bash


### For those brave (or desperate) enough to adjust the script...here's some helpful info.
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
###  the USER and PASS variables set with 'bs_user' and 'bsPasswd' respectively.
###  new versions of mysql have stricter requirements for the password.   
###  You should obviously change this in a production environment anyway.  If you do,
###  make sure you change it in the bs.cfg file as well...for each client user
###  NOTE!!!  newer version of MySQL set security variables by default to
###  prevent loading data from local directories.  You will have to adjust the 
###  Global variable 'local_infile' in your MySQL instance.  Something like:
###  'show global variables like 'local_infile';
###  'set global local_infile = 'ON';

defaultlang=en


echo ''
echo ''
echo ''
echo "NOTE: This script will convert the bs.cfg file to the language option you select"
echo ""
echo ""
echo ""
echo "choose your two character language code from the options below..."
echo "en=english"
echo "fr=french"
echo "es=spanish"
echo "tr=turkish"
echo "de=german"
echo "pt=portuguese"
echo "ru=russian"
echo -n "Enter the two character language code (en, es, fr, etc): "
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
if [[ "$LANG" == "pt" ]]; then
	COUNTRY="PT"
fi
if [[ "$LANG" == "ru" ]]; then
	COUNTRY="RU"
fi
if [[ "$LANG" == "de" ]]; then
	COUNTRY="DE"
fi

while IFS= read -r line; do
	if [[ $line == DBTYPE* ]] ; then
		vDBTYPE=$line
	fi
	if [[ $line == DB=* ]] ; then
		vDB="DB=data/" + $LANG + "/bsdb.db"
	fi
	if [[ $line == USER* ]] ; then
		vUSER=$line
	fi
	if [[ $line == PASS* ]] ; then
		vPASS=$line
	fi
	if [[ $line == PORT* ]] ; then
		vPORT=$line
	fi
	if [[ $line == IP* ]] ; then
		vIP=$line
	fi
	if [[ $line == DRIVER* ]] ; then
		vDRIVER=$line
	fi
done < bs.cfg


echo ""
echo ""
echo "creating blueseer config file...."
echo $vDBTYPE >bs.cfg
echo $vDB >>bs.cfg
echo $vUSER >>bs.cfg
echo $vPASS >>bs.cfg
echo $vIP >>bs.cfg
echo $vPORT >>bs.cfg
echo $vDRIVER >>bs.cfg
echo "LANGUAGE=$LANG" >>bs.cfg
echo "COUNTRY=$COUNTRY" >>bs.cfg

echo 'conversion of bs.cfg complete...'
echo ''