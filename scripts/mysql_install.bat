@echo off

@echo "NOTE:  if at any time you receive an error such as "mysql unknown or unrecognized executable....then make sure you have mysql installed AND your PATH variable in enviromental settings has the mysql bin directory included."

@echo ""
@echo ""

@echo "NOTE!!!:  This script should only be executed ONCE.   This script will blow away the previous database and data and install a clean schema with default data."

set /p IP=Previous Data will be lost...are you sure you want to install database? (enter: yes or no):%=%
if /I not "%IP%"=="yes" (
@echo "Exiting..."
exit /b
) 


set /p IP=Enter the database server IP addr (ex: 10.15.2.2 or localhost):%=%

set /p pass=Enter the administrator password for the MySQL Database:%=%

set /p DB=Enter the database name (default: bsdb ):%=%

set /p lang=Enter the two character language code (en,de,fr,es,tr,etc):%=%

if not defined lang (
rem @echo "must pass the two character lang parameter"
rem pause 2
rem exit /b 
set "lang=en"
)

set "country=US"

if "%lang%"=="fr" (
set "country=FR"
)
if "%lang%"=="es" (
set "country=ES"
)
if "%lang%"=="tr" (
set "country=TR"
)
if "%lang%"=="de" (
set "country=DE"
)
if "%lang%"=="ro" (
set "country=RO"
)

if not defined DB (
set "DB=bsdb"
)

set "ROOT=root"

cd %~dp0

@echo "creating bs.cfg file for database %DB%...."
@echo DBTYPE=mysql>bs.cfg
@echo DB=%DB%>>bs.cfg
@echo USER=bs_user>>bs.cfg
@echo PASS=bspasswd>>bs.cfg
@echo IP=%IP%>>bs.cfg
@echo PORT=3306>>bs.cfg
@echo DRIVER=com.mysql.cj.jdbc.Driver>>bs.cfg
@echo LANGUAGE=%lang%>>bs.cfg
@echo COUNTRY=%country%>>bs.cfg




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
mysql --defaults-extra-file=my.cnf -e "create database if not exists %DB% character set utf8mb4 collate utf8mb4_unicode_ci;"
mysql --defaults-extra-file=my.cnf %DB%  <blueseer.schema 


@echo "loading user account...."
mysql --defaults-extra-file=my.cnf -e "drop user if exists 'bs_user'@'%' ;"  
mysql --defaults-extra-file=my.cnf -e "create user if not exists 'bs_user'@'%' identified by 'bspasswd';"  
mysql --defaults-extra-file=my.cnf -e "grant select,insert,delete,update on %DB%.* to 'bs_user'@'%';" 


cd "%lang%"
@echo "loading 'some' default records for tables from language pack %lang%....."
mysql --defaults-extra-file=..\my.cnf -D %DB% -e "source sq_mysql.txt;" 

cd ..
rem clean up my.cnf
del my.cnf >nul 2>&1

@echo "finished install..."
@echo "Login = 'admin' and Password = 'admin'"
@echo "Click the login.bat file in the BlueSeer Directory to log in"

ping -n 5 127.0.0.1 > nul

cd ..

REM call login.bat
