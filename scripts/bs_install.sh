#!/bin/bash

#program assumes you are in directory "blueseer"...where you 'should' have
#extracted the tar file contents to.   A sub dir 'data' should exist

#echo -n "Enter the username for this workstation: "
#read username


echo -n "Enter the IP addr of database server: ex:  '161.23.44.23' or 'localhost': "
read IP

if [ $IP = "10.17.2.5" ]; then
echo -n "Cannot use this IP...failsafe"
pause 1
exit
fi


echo -n "Enter the administrator password for the MySQL Database: "
read PASS

ROOT=root
DB=bsdb

MYSQL_PWD=$PASS
export MYSQL_PWD

echo "creating blueseer config file...."
echo "DBTYPE=mysql" >>bs.cfg
echo "DB=$DB" >>bs.cfg
echo "USER=bs_user" >>bs.cfg
echo "PASS=bspasswd" >>bs.cfg
echo "IP=$IP" >>bs.cfg
echo "PORT=3306" >>bs.cfg
echo "DRIVER=com.mysql.jdbc.Driver" >>bs.cfg

cd data

echo "creating database schema...."
mysql -e "drop database if exists $DB;" -u $ROOT  
mysql -e "create database if not exists $DB;" -u $ROOT  
mysql $DB -u $ROOT  <blueseer.schema 

mysql $DB -u $ROOT  <panel_mstr.dat
mysql $DB -u $ROOT  <menu_mstr.dat
mysql $DB -u $ROOT  <menu_tree.dat
mysql $DB -u $ROOT  <shift_mstr.dat
mysql $DB -u $ROOT  <clock_code.dat
mysql $DB -u $ROOT  <ov_ctrl.dat
mysql $DB -u $ROOT  <counter.dat
mysql $DB -u $ROOT  <code_mstr.dat
mysql $DB -u $ROOT  <label_zebra.dat
mysql $DB -u $ROOT  <edi_mstr.dat
mysql $DB -u $ROOT  <editp_mstr.dat


echo "loading user account...."
# the next line is a workaround to stop the error of no user when dropped
mysql -e "grant usage on *.* to 'bs_user'@'localhost';" $DB -u $ROOT ;

mysql -e "drop user  'bs_user'@'localhost';" $DB -u $ROOT  ;
mysql -e "create user 'bs_user'@'localhost' identified by 'bspasswd';" $DB -u $ROOT  ;
mysql -e "grant select,insert,delete,update on *.* to 'bs_user'@'localhost'" $DB -u $ROOT ;
echo "loading default records for tables....."
mysql -e "insert into car_mstr (car_code, car_type) values ('FEDX', 'carrier') ;" $DB -u $ROOT ;
mysql -e "insert into car_mstr (car_code, car_type) values ('ACME', 'carrier') ;" $DB -u $ROOT ;
mysql -e "insert into cust_term (cut_code, cut_desc, cut_days) values ('N30','NET 30','30') ;" $DB -u $ROOT ;
mysql -e "insert into cust_term (cut_code, cut_desc, cut_days) values ('N00','Due At Sale','0') ;" $DB -u $ROOT ;
mysql -e "insert into frt_mstr (frt_code) values ('PICKUP') ;" $DB -u $ROOT ;
mysql -e "insert into cur_mstr (cur_id) values ('USD') ;" $DB -u $ROOT ;
mysql -e "insert into loc_mstr values ('FG','FG AREA','1000','WH1','1') ;" $DB -u $ROOT ;
mysql -e "insert into loc_mstr values ('RAW','FG AREA','1000','WH1','1') ;" $DB -u $ROOT ;
mysql -e "insert into uom_mstr values ('EA','Each') ;" $DB -u $ROOT ;
mysql -e "insert into uom_mstr values ('PC','Pieces') ;" $DB -u $ROOT ;
mysql -e "insert into uom_mstr values ('LB','Pounds') ;" $DB -u $ROOT ;
mysql -e "insert into uom_mstr values ('GA','Gallons') ;" $DB -u $ROOT ;
mysql -e "insert into wh_mstr values ('WH1','1000','MAIN WAREHOUSE','410 boulevard South','Suite 1A', 'Florence', 'SC', '29581', 'USA') ;" $DB -u $ROOT ;
mysql -e "insert into task_mstr (task_id, task_desc) values ('100','Sample Task') ;" $DB -u $ROOT ;
mysql -e "insert into task_det (taskd_id, taskd_owner, taskd_desc, taskd_sequence) values ('100','admin', 'Sample Task Action 1', '1') ;" $DB -u $ROOT ;
mysql -e "insert into task_det (taskd_id, taskd_owner, taskd_desc, taskd_sequence) values ('100','admin', 'Sample Task Action 2', '2') ;" $DB -u $ROOT ;
echo "loading bulk tables from csv files....."
mysql --local-infile -e "load data local infile 'usrmstr.csv' replace into table user_mstr fields terminated by ';' ignore 0 Lines; show errors;" $DB -u $ROOT ;
mysql --local-infile -e "load data local infile 'permmstr.csv' replace into table perm_mstr fields terminated by ';' ignore 0 Lines; show errors;" $DB -u $ROOT ;
mysql --local-infile -e "load data local infile 'sitemstr.csv' replace into table site_mstr fields terminated by ',' ignore 0 Lines; show errors;" $DB -u $ROOT ;
mysql --local-infile -e "load data local infile 'empmstr.csv' replace into table emp_mstr fields terminated by ',' ignore 0 Lines; show errors;" $DB -u $ROOT ;
mysql --local-infile -e "load data local infile 'plmstr.csv' replace into table pl_mstr fields terminated by ',' ignore 0 Lines; show errors;" $DB -u $ROOT ;
mysql --local-infile -e "load data local infile 'wcmstr.csv' replace into table wc_mstr fields terminated by ',' ignore 0 Lines; show errors;" $DB -u $ROOT ;
mysql --local-infile -e "load data local infile 'wfmstr.csv' replace into table wf_mstr fields terminated by ',' ignore 0 Lines; show errors;" $DB -u $ROOT ;
mysql --local-infile -e "load data local infile 'bankmstr.csv' replace into table bk_mstr fields terminated by ',' ignore 0 Lines; show errors;" $DB -u $ROOT ;
mysql --local-infile -e "load data local infile 'arctrl.csv' replace into table ar_ctrl fields terminated by ',' ignore 0 Lines; show errors;" $DB -u $ROOT ;
mysql --local-infile -e "load data local infile 'ovmstr.csv' replace into table ov_mstr fields terminated by ',' ignore 0 Lines; show errors;" $DB -u $ROOT ;
mysql --local-infile -e "load data local infile 'shipctrl.csv' replace into table ship_ctrl fields terminated by ',' ignore 0 Lines; show errors;" $DB -u $ROOT ;
mysql --local-infile -e "load data local infile 'deptmstr.csv' replace into table dept_mstr fields terminated by ',' ignore 0 Lines; show errors;" $DB -u $ROOT ;
mysql --local-infile -e "load data local infile 'glcal.csv' replace into table gl_cal fields terminated by ',' ignore 0 Lines; show errors;" $DB -u $ROOT ;
mysql --local-infile -e "load data local infile 'acctmstr.csv' replace into table ac_mstr fields terminated by ',' ignore 0 Lines; show errors;" $DB -u $ROOT ;
mysql --local-infile -e "load data local infile 'pos_ctrl.csv' replace into table pos_ctrl fields terminated by ',' ignore 0 Lines; show errors;" $DB -u $ROOT ;
mysql --local-infile -e "load data local infile 'edictrl.csv' replace into table edi_ctrl fields terminated by ',' ignore 0 Lines; show errors;" $DB -u $ROOT ;
mysql --local-infile -e "load data local infile 'cmmstr.csv' replace into table cm_mstr fields terminated by ',' ignore 0 Lines; show errors;" $DB -u $ROOT ;
mysql --local-infile -e "load data local infile 'vdmstr.csv' replace into table vd_mstr fields terminated by ',' ignore 0 Lines; show errors;" $DB -u $ROOT ;
mysql --local-infile -e "load data local infile 'cmsdet.csv' replace into table cms_det fields terminated by ',' ignore 0 Lines; show errors;" $DB -u $ROOT ;
mysql -local-infile -e "load data local infile 'cmedimstr.csv' replace into table cmedi_mstr fields terminated by ',' ignore 0 Lines; show errors;" $DB -u $ROOT ;

echo 'done with install'
echo 'you can launch by typing the following at the command line: '
echo 'java -jar dist/blueseer.jar'
echo ''
echo 'NOTE:'
echo 'login and password are admin and admin respectively'
echo 'if you receive a 'headless' java error... '
echo ' ...you may have to install the full JRE...not just headless'
echo ' ...some linux vendors provide a smaller size JRE as their default'

unset MYSQL_PWD
