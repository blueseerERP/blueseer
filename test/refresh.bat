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
if exist edi rmdir edi
mkdir edi
if exist jasper rmdir jasper
mkdir jasper
if exist conf rmdir conf
mkdir conf
if exist logs rmdir logs
mkdir logs
if exist images rmdir images
mkdir images
if exist ..\.patch copy ..\.patch %~dp0\
if exist ..\sf\data\en copy ..\sf\data\en\* %~dp0\data\
if exist ..\sf\edi xcopy ..\sf\edi %~dp0\edi /s /e /y
if exist ..\sf\jasper xcopy ..\sf\jasper %~dp0\jasper /s /e /y
if exist ..\sf\conf xcopy ..\sf\conf %~dp0\conf /s /e /y
if exist ..\sf\logs xcopy ..\sf\logs %~dp0\logs /s /e /y
if exist ..\sf\images xcopy ..\sf\images %~dp0\images /s /e /y
if exist ..\sf\data\bsdbdev.db copy ..\sf\data\bsdbdev.db %~dp0\data\bsdb.db


@echo ""
@echo ""
@echo ""
@echo "finished install..."

cd ..

