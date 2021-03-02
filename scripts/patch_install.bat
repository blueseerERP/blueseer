@echo off


@echo "BlueSeer PATCH NOTE: this patch is applicable to Version 4.3" 

@echo ""
@echo ""

set /p DBTYPE=choose the number beside your database type and press enter (1=sqlite  2=mysql)%=%

@echo ""
@echo ""
if /I "%DBTYPE%"=="1" (
@echo "This is a patch install for sqlite type database..."
) else (
@echo "This is a patch install for mysql type database..."
)
set /p junk=press any key to continue...or control-c to quit%=%

cd %~dp0

@echo "BlueSeer PATCH NOTE:  Check if blueseer patch directory structure is correct"
set result=false
set a=%~dp0
set b=patches\\
echo "%a%" | FINDSTR /I /C:"%b%" && (set result=true)

if "%result%" == "true" (
@echo "BlueSeer PATCH NOTE:  BlueSeer directory structure is correct...continuing"
ping -n 7 127.0.0.1 > nul
) else (
@echo "BlueSeer PATCH NOTE:  blueseer patch structure is incorrect...exiting"
@echo "BlueSeer PATCH NOTE:  patch must be unzipped and placed in blueseer\patches folder as blueseer\patches\<newpatchdir>"
ping -n 5 127.0.0.1 > nul
exit
)


@echo "BlueSeer PATCH NOTE:  copying jasper directory to installation folder"
copy %~dp0\jasper ..\..\jasper
@echo "BlueSeer PATCH NOTE:  copying zebra directory to installation folder"
copy %~dp0\zebra ..\..\zebra
@echo "BlueSeer PATCH NOTE:  copying jars to dist folder"
copy %~dp0\blueseer.jar ..\..\dist\
copy %~dp0\bsmf.jar ..\..\dist\
@echo "BlueSeer PATCH NOTE:  copying .patch file to root folder"
copy %~dp0\.patch ..\..\


if /I "%DBTYPE%"=="1" (
@echo "BlueSeer PATCH NOTE:  executing sqlite sql schema updates"
..\..\data\sqlite3.exe ..\..\data\bsdb.db <.patchsqlv50
) else (
@echo "Calling MySQL schema script....please wait"
ping -n 5 127.0.0.1 > nul
call mysql_patch.bat
)


@echo ""
@echo ""
@echo ""
@echo "BlueSeer PATCH NOTE:  patch update complete!"
@echo ""
@echo ""
@echo ""
