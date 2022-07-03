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

echo "creating bs.cfg file for sqlite data connection"
cp ../scripts/bs.cfg bs.cfg

echo "NOTE!!  you will need to adjust the property name 'JDK17dir' in the build.xml file for your JDK17 path"
echo "assign the path to the parameter as (example) /some/path/to/jdk-17  "
