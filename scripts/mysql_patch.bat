@echo off


@echo "NOTE:  This patch bat file is only for MySQL versions of BlueSeer...press control-c to cancel."

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


rem cd %~dp0


@echo "creating temp mybs.cnf..."
del mybs.cnf >nul 2>&1
@echo [client]>>mybs.cnf
@echo user = root>>mybs.cnf
@echo password = %pass%>>mybs.cnf
@echo host = %IP%>>mybs.cnf 
@echo local_infile = ON >>mybs.cnf 


@echo "updating schema....."
mysql --defaults-extra-file=mybs.cnf -D bsdb -e "source .patchsqlv50;" 
@echo "completed schema changes....."
ping -n 5 127.0.0.1 > nul


rem clean up mybs.cnf
del mybs.cnf >nul 2>&1

@echo "finished mysql patch install..."

ping -n 5 127.0.0.1 > nul

