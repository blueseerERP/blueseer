@echo off

@echo "Creating bsdb.db in language specific directory.... "
@echo ""
@echo ""

rem cd %~dp0

set back=%cd%
for /d %%i in (C:\bs\blueseer\sf\data\*) do (
cd "%%i"
if "%%i" NEQ "C:\bs\blueseer\sf\data\sampledir" (
echo current directory: %%i
cd %%i
if exist bsdb.db del bsdb.db
if "%computername%" == "CLTSLCDXM0G2" (
c:\NGCSof~1\sqlite\sqlite3.exe bsdb.db <..\blueseer.sqlite
c:\NGCSof~1\sqlite\sqlite3.exe bsdb.db <..\sq.txt
) else (
sqlite3.exe bsdb.db <..\blueseer.sqlite
sqlite3.exe bsdb.db <..\sq.txt
)
rem pause
)
)

@echo ""
@echo ""
@echo ""
@echo "finished creating lang specific bsdb.db..."

cd ..

