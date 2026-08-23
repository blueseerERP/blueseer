#!/bin/bash


if (( $# == 0 )); then
    echo "Warning: No parameters provided." >&2
    echo "Usage: $0 must pass patch number as first parameter" >&2
    exit 1
fi

jaronly="blueseer.jaronly.8.0.$1.zip"
## creation of generic zip for both sqlite and mysql versions
bsdir=$(pwd)
bszip="blueseer.generic.linux.v8.0.zip"
rm -f $bszip
cd ../sf
zip ../scripts/$bszip jasper/*
zip -u ../scripts/$bszip zebra/*
zip -u ../scripts/$bszip patches/*
zip -u ../scripts/$bszip temp/*
zip -u ../scripts/$bszip logs/*
zip -u ../scripts/$bszip attachments/*
zip -u ../scripts/$bszip conf/*
zip -u ../scripts/$bszip custom/*
zip -r ../scripts/$bszip data/*
zip -r ../scripts/$bszip edi/*
zip -u ../scripts/$bszip images/*
cd ../scripts
cp bs.cfg.sqlite.linux bs.cfg
zip -u $bszip ../dist/*
zip -u $bszip bs.cfg
zip -u $bszip bs.cfg.sqlite
zip -u $bszip bs.cfg.mysql
zip -u $bszip bslogging.properties
zip -u $bszip .patch
zip -u $bszip login.sh
zip -u $bszip mysql_install.sh
zip -u $bszip langconvert.sh
zip -u $bszip controlM.sh
zip -u $bszip bsapi.service
zip -u $bszip bsapi.service.sh

zip -j $jaronly /home/vcs/bs/blueseer/dist/blueseer.jar /home/vcs/bs/blueseer/dist/bsmf.jar

cd /home/vcs
zip -ru $bsdir/$bszip jre26/*

