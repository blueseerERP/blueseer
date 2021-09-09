@echo off

@echo "Installing BlueSeer.... "
@echo ""
@echo ""

cd %~dp0

set "DB=bsdb"

@echo "creating bsconfig file...."
@echo DBTYPE=sqlite>>bsconfig
@echo DB=data\bsdb.db>>bsconfig
@echo USER=bs_user>>bsconfig
@echo PASS=bspasswd>>bsconfig
@echo IP=localhost>>bsconfig
@echo PORT=3306>>bsconfig
@echo DRIVER=org.sqlite.JDBC>>bsconfig

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

