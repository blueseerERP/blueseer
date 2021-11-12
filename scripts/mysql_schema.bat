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

@echo "creating bs.cfg file...."
@echo DBTYPE=mysql>>bs.cfg
@echo DB=%DB%>>bs.cfg
@echo USER=bs_user>>bs.cfg
@echo PASS=bspasswd>>bs.cfg
@echo IP=%IP%>>bs.cfg
@echo PORT=3306>>bs.cfg
@echo DRIVER=com.mysql.jdbc.Driver>>bs.cfg




cd data

@echo "creating temp my.cnf..."
@echo [client]>>my.cnf
@echo user = root>>my.cnf
@echo password = %pass%>>my.cnf
@echo host = localhost>>my.cnf 


@echo "creating database schema...."
mysql --defaults-extra-file=my.cnf -e "drop database if exists %DB%;"  
mysql --defaults-extra-file=my.cnf -e "create database if not exists %DB%;"
mysql --defaults-extra-file=my.cnf %DB%  <blueseer.schema 

mysql --defaults-extra-file=my.cnf %DB%  <menu_tree.dat
mysql --defaults-extra-file=my.cnf %DB%  <shift_mstr.dat
mysql --defaults-extra-file=my.cnf %DB%  <clock_code.dat
mysql --defaults-extra-file=my.cnf %DB%  <ov_ctrl.dat
mysql --defaults-extra-file=my.cnf %DB%  <counter.dat
mysql --defaults-extra-file=my.cnf %DB%  <code_mstr.dat
mysql --defaults-extra-file=my.cnf %DB%  <label_zebra.dat
mysql --defaults-extra-file=my.cnf %DB%  <edi_mstr.dat
mysql --defaults-extra-file=my.cnf %DB%  <editp_mstr.dat

mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'panelmstr.csv' replace into table panel_mstr fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'menumstr.csv' replace into table menu_mstr fields terminated by ',' ignore 0 Lines; show errors;" %DB% 


@echo "loading user account...."
mysql --defaults-extra-file=my.cnf -e "grant usage on *.* to 'bs_user'@'localhost';" %DB% 
mysql --defaults-extra-file=my.cnf -e "drop user 'bs_user'@'localhost';" %DB% 
mysql --defaults-extra-file=my.cnf -e "create user 'bs_user'@'localhost' identified by 'bspasswd';" %DB% 
mysql --defaults-extra-file=my.cnf -e "grant select,insert,delete,update on *.* to 'bs_user'@'localhost'" %DB% 
mysql --defaults-extra-file=my.cnf  --local-infile -e "update user_mstr set user_id = 'admin', user_passwd = 'admin', user_email = 'someone@acme.com', user_lname = 'one', user_fname = 'some'  ;" %DB% 
