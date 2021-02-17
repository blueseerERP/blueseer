@echo off


@echo "NOTE:  if at any time you receive an error such as "mysql unknown or unrecognized executable....then make sure you have mysql installed AND your PATH variable in enviromental settings has the mysql bin directory included."

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


cd %~dp0

@echo "creating bsconfig file...."
@echo DBTYPE=mysql>>bsconfig
@echo DB=%DB%>>bsconfig
@echo USER=bs_user>>bsconfig
@echo PASS=bspasswd>>bsconfig
@echo IP=%IP%>>bsconfig
@echo PORT=3306>>bsconfig
@echo DRIVER=com.mysql.cj.jdbc.Driver>>bsconfig




cd data

@echo "creating temp my.cnf..."
del my.cnf >nul 2>&1
@echo [client]>>my.cnf
@echo user = root>>my.cnf
@echo password = %pass%>>my.cnf
@echo host = %IP%>>my.cnf 
@echo local_infile = ON >>my.cnf 


@echo "creating database schema...."
mysql --defaults-extra-file=my.cnf -e "drop database if exists %DB%;"  
mysql --defaults-extra-file=my.cnf -e "create database if not exists %DB%;"
mysql --defaults-extra-file=my.cnf %DB%  <blueseer.schema 


@echo "loading user account...."
mysql --defaults-extra-file=my.cnf -e "drop user if exists 'bs_user'@'%' ;"  
mysql --defaults-extra-file=my.cnf -e "create user if not exists 'bs_user'@'%' identified by 'bspasswd';"  
mysql --defaults-extra-file=my.cnf -e "grant select,insert,delete,update on bsdb.* to 'bs_user'@'%';" 


@echo "loading 'some' default records for tables....."
mysql --defaults-extra-file=my.cnf -D bsdb -e "source sq_mysql.txt;" 


rem clean up my.cnf
del my.cnf >nul 2>&1

@echo "finished install..."
@echo "Login = 'admin' and Password = 'admin'"
@echo "Click the login.bat file in the BlueSeer Directory to log in"

ping -n 5 127.0.0.1 > nul

cd ..

REM call login.bat
