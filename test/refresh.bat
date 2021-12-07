@echo off

@echo "Refresh a BlueSeer test instance.... "
@echo ""
@echo ""

cd %~dp0

set "DB=bsdb"

@echo "creating bs.cfg file...."
@echo DBTYPE=sqlite>>bs.cfg
@echo DB=data\bsdb.db>>bs.cfg
@echo USER=bs_user>>bs.cfg
@echo PASS=bspasswd>>bs.cfg
@echo IP=localhost>>bs.cfg
@echo PORT=3306>>bs.cfg
@echo DRIVER=org.sqlite.JDBC>>bs.cfg
@echo LANGUAGE=en>>bs.cfg
@echo COUNTRY=US>>bs.cfg

if exist data rmdir data
mkdir data
if exist ..\.patch copy ..\.patch %~dp0\
if exist ..\sf\data\en copy ..\sf\data\en\* %~dp0\data\
if exist ..\sf\data\bsdbdev.db copy ..\sf\data\bsdbdev.db %~dp0\data\bsdb.db


@echo ""
@echo ""
@echo ""
@echo "finished install..."

cd ..

