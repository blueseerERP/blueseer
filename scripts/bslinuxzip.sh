#!/bin/bash

## creation of generic zip for both sqlite and mysql versions
bsdir=$(pwd)
bszip="blueseer.generic.linux.v52.zip"
rm -f $bszip
cd ../sf
zip ../scripts/$bszip jasper/*
zip -u ../scripts/$bszip zebra/*
zip -u ../scripts/$bszip patches/*
zip -u ../scripts/$bszip temp/*
zip -r ../scripts/$bszip data/*
zip -r ../scripts/$bszip edi/*
zip -u ../scripts/$bszip images/*
cd ../scripts
cp bsconfig.sqlite.linux bsconfig
zip -u $bszip ../dist/*
zip -u $bszip bsconfig
zip -u $bszip bsconfig.sqlite
zip -u $bszip bsconfig.mysql
zip -u $bszip bslogging.properties
zip -u $bszip .patch
zip -u $bszip login.sh
zip -u $bszip mysql_install.sh
zip -u $bszip controlM.sh
cd /home/vcs
zip -ru $bsdir/$bszip jre11/*

