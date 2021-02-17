#!/bin/bash

## creation of generic zip for both sqlite and mysql versions
bszip="blueseer.generic.linux.v50.zip"
rm -f $bszip
cd ../sf
zip ../scripts/$bszip jasper/*
zip -u ../scripts/$bszip zebra/*
zip -u ../scripts/$bszip patches/*
zip -u ../scripts/$bszip temp/*
zip -u ../scripts/$bszip data/*
zip -u ../scripts/$bszip edi/*
zip -u ../scripts/$bszip images/*
cd ../scripts
zip -u $bszip ../dist/*
zip -u $bszip bsconfig
zip -u $bszip bsconfig.sqlite
zip -u $bszip bsconfig.mysql
zip -u $bszip bslogging.properties
zip -u $bszip .patch
zip -u $bszip login.sh
zip -u $bszip mysql_install.sh
zip -u $bszip controlM.sh
cd /tmp
zip -ru bs/blueseer/scripts/$bszip jre8/*

