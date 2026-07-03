#!/bin/bash

echo "refreshing BlueSeer test instance"

currdir=$(basename "$PWD")
if [[ $currdir != "test" ]];
then
        echo "must be executed from test directory"
        exit
fi

if [[ -d "data" ]];
then
        echo "removing old data directory..."
        rm -fR data
fi
echo "adding data directory..."
cp -R ../sf/data data

if [[ -d "edi" ]];
then
        echo "removing old edi directory..."
        rm -fR edi
fi
echo "adding edi directory..."
cp -R ../sf/edi edi

if [[ -d "jasper" ]];
then
        echo "removing old jasper directory..."
        rm -fR jasper
fi
echo "adding jasper directory..."
cp -R ../sf/jasper jasper

if [[ -d "zebra" ]];
then
        echo "removing old zebra directory..."
        rm -fR zebra
fi
echo "adding zebra directory..."
cp -R ../sf/zebra zebra

if [[ -d "images" ]];
then
        echo "removing old images directory..."
        rm -fR images
fi
echo "adding images directory..."
cp -R ../sf/images images

if [[ -d "logs" ]];
then
        echo "removing old logs directory..."
        rm -fR logs
fi
echo "adding logs directory..."
cp -R ../sf/logs logs

if [[ -d "temp" ]];
then
        echo "removing old temp directory..."
        rm -fR temp
fi
echo "adding temp directory..."
cp -R ../sf/temp temp

if [[ -d "conf" ]];
then
        echo "removing old conf directory..."
        rm -fR conf
fi
echo "adding conf directory..."
cp -R ../sf/conf conf


echo "creating bs.cfg file for sqlite data connection..."
cp ../scripts/bs.cfg.sqlite.linux bs.cfg

echo "creating .patch file..."
cp ../scripts/.patch .patch

echo ""
echo ""
echo "NOTE!!  you will need to adjust the property name 'JDK' in the build.xml file "
echo " for your JDK path. "
echo "Assign the path to the parameter as (example) /some/path/to/jdk  "
