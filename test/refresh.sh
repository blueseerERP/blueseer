!/bin/bash

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

echo "creating bs.cfg file for sqlite data connection..."
cp ../scripts/bs.cfg.sqlite.linux bs.cfg

echo "creating .patch file..."
cp ../scripts/.patch .patch

echo ""
echo ""
echo "NOTE!!  you will need to adjust the property name 'JDK17dir' in the build.xml file "
echo " for your JDK17 path. "
echo "Assign the path to the parameter as (example) /some/path/to/jdk-17  "
