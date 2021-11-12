@echo off

@echo "Installing BlueSeer.... "
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
@echo LANGUAGE=%1>>bs.cfg
@echo COUNTRY=%2>>bs.cfg

cd data/%1

@echo "Current Directory: " %cd%
if exist bsdb.db del bsdb.db

rem chcp 65001

@echo "creating database schema...."
if "%computername%" == "CLTSLCDXM0G2" (
@echo "work around for group policy bs"
c:\NGCSof~1\sqlite\sqlite3.exe bsdb.db <..\blueseer.sqlite
c:\NGCSof~1\sqlite\sqlite3.exe bsdb.db <..\sq.txt
) else (
sqlite3.exe bsdb.db <..\blueseer.sqlite
sqlite3.exe bsdb.db <..\sq.txt
)

copy bsdb.db ..\

@echo ""
@echo ""
@echo ""
@echo "finished install..."

cd ..

