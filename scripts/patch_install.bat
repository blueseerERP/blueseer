@echo off

set /a "errors=0"
echo "patch log report" >patch.log

cls
@echo ""
@echo ""
@echo "BlueSeer V6.1 Patch/Upgrade Delivery Script" 
@echo ""
@echo ""

set /p PACKAGE=choose the number beside the patch type and press enter (1=clientonly  2=databaseonly 3=client+DB)%=%
if /I "%PACKAGE%"=="1" (
@echo "You have chosen clientonly patch update..."
@echo "choice of patch type = clientonly" >>patch.log 2>&1
goto libinstall
) 

if /I "%PACKAGE%"=="2" (
@echo "You have chosen databaseonly patch update..."
@echo "choice of patch type = databaseonly" >>patch.log 2>&1
goto dbinstall
) 

if /I "%PACKAGE%"=="3" (
@echo "You have chosen client + database patch update..."
@echo "choice of patch type = client+DB" >>patch.log 2>&1
goto libinstall
) 

goto eof




:libinstall
cd %~dp0
@echo "Checking if blueseer patch directory structure is correct"
if exist ..\..\dist (
@echo "BlueSeer directory structure is correct...continuing"
ping -n 7 127.0.0.1 > nul
) else (
@echo "blueseer patch structure is incorrect...exiting"
@echo "patch must be unzipped and placed in blueseer\patches folder as blueseer\patches\<newpatchdir>"
@echo "blueseer patch structure is incorrect...exiting" >>patch.log 2>&1
ping -n 7 127.0.0.1 > nul
exit
)

@echo "copying jasper directory to installation folder"
if exist %~dp0\jasper (
copy %~dp0\jasper ..\..\jasper >>patch.log 2>&1
) else (
@echo "ERROR: source dir jasper does not exist"
@echo "ERROR: source dir jasper does not exist" >>patch.log 2>&1
set /a "errors=%errors%+1"
)

@echo "copying zebra directory to installation folder"
if exist %~dp0\zebra (
copy %~dp0\zebra ..\..\zebra >>patch.log 2>&1
) else (
@echo "ERROR: source dir zebra does not exist"
@echo "ERROR: source dir zebra does not exist" >>patch.log 2>&1
set /a "errors=%errors%+1"
)

@echo "copying blueseer.jar to dist folder"
if exist %~dp0\blueseer.jar (
copy %~dp0\blueseer.jar ..\..\dist\ >>patch.log 2>&1
) else (
@echo "ERROR: source file blueseer.jar does not exist" 
@echo "ERROR: source file blueseer.jar does not exist" >>patch.log 2>&1
set /a "errors=%errors%+1"
)

@echo "copying bsmf.jar to dist folder "
if exist %~dp0\bsmf.jar (
copy %~dp0\bsmf.jar ..\..\dist\ >>patch.log 2>&1
) else (
@echo "ERROR: source file bsmf.jar does not exist "
@echo "ERROR: source file bsmf.jar does not exist " >>patch.log 2>&1
set /a "errors=%errors%+1"
)

@echo "copying .patch file to root folder"
if exist %~dp0\.patch (
copy %~dp0\.patch ..\..\ >>patch.log 2>&1
) else (
@echo "ERROR: source file .patch does not exist"
@echo "ERROR: source file .patch does not exist" >>patch.log 2>&1
set /a "errors=%errors%+1"
)

if /I "%PACKAGE%"=="3" (
goto dbinstall
)

goto eof



:dbinstall
@echo "Entering database update routine..." >>patch.log 2>&1
set /p DBTYPE=choose the number beside your database type and press enter (1=sqlite  2=mysql)%=%
if /I "%DBTYPE%"=="1" (
@echo "This is a patch install for sqlite type database..."
@echo "DatabaseType = sqlite" >>patch.log 2>&1
) else (
@echo "This is a patch install for mysql type database..."
@echo "DatabaseType = mysql" >>patch.log 2>&1
)
if /I "%DBTYPE%"=="1" (
@echo "  executing sqlite sql schema updates"
..\..\data\sqlite3.exe ..\..\data\bsdb.db <.patchsqlv61 >>patch.log 2>&1
ping -n 7 127.0.0.1 > nul
@echo "return code: %ERRORLEVEL%" >>patch.log 2>&1
) else (
goto mysqlinstall
pause
)
goto eof


:mysqlinstall
@echo ""
@echo ""
set /p IP=Enter the database server IP addr (ex: 10.15.2.2 or localhost):%=%
if /I "%IP%"=="10.17.2.5" (
@echo "cannot use this IP address..failsafe"
pause 2
exit /b ) 
set /p pass=Enter the administrator password for the MySQL Database:%=%
set "ROOT=root"
set "DB=bsdb"
@echo "creating temp mybs.cnf..."
del mybs.cnf >nul 2>&1
@echo [client]>>mybs.cnf
@echo user = root>>mybs.cnf
@echo password = %pass%>>mybs.cnf
@echo host = %IP%>>mybs.cnf 
@echo local_infile = ON >>mybs.cnf 
@echo "updating schema....."
@echo "updating schema....." >>patch.log 
mysql --defaults-extra-file=mybs.cnf -D bsdb -e "source .patchsqlv61;" >>patch.log 2>&1
if %errorlevel% NEQ 0 set /a "errors=%errors%+1"
@echo "done with mysql changes....."
ping -n 7 127.0.0.1 > nul
rem clean up mybs.cnf
del mybs.cnf >nul 2>&1
goto eof


:eof
@echo ""
@echo ""
@echo ""
@echo "  patch update completed with %errors% error"
ping -n 7 127.0.0.1 > nul
@echo ""
@echo ""
@echo ""
exit /b %ERRORLEVEL%


