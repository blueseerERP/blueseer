@echo off

@echo "Creating bsdb.db in language specific directory.... "
@echo ""
@echo ""

cd %~dp0
cd c:\bs\blueseer\sf\data\%1

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

rem copy bsdb.db ..\

@echo ""
@echo ""
@echo ""
@echo "finished install..."

cd ..

