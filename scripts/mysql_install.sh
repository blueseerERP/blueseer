#!/bin/bash


echo -n "Enter the IP addr of database server: 'localhost': "
read IP


echo -n "Enter the administrator password for the MySQL Database: "
read PASS

ROOT=root
DB=bsdb

MYSQL_PWD=$PASS
export MYSQL_PWD

echo "creating blueseer config file...."
echo "DBTYPE=mysql" >>bsconfig
echo "DB=$DB" >>bsconfig
echo "USER=bs_user" >>bsconfig
echo "PASS=bspasswd" >>bsconfig
echo "IP=$IP" >>bsconfig
echo "PORT=3306" >>bsconfig
echo "DRIVER=com.mysql.jdbc.Driver" >>bsconfig

cd data

echo "creating database schema...."
mysql -e "drop database if exists $DB;" -u $ROOT  
mysql -e "create database if not exists $DB;" -u $ROOT  
mysql $DB -u $ROOT  <blueseer.schema 

echo "loading additional schema class...."
mysql --local-infile -e "load data local infile 'panelmstr.csv' replace into table panel_mstr fields terminated by ',' ignore 0 Lines; show errors;" $DB -u $ROOT ;
mysql --local-infile -e "load data local infile 'menumstr.csv' replace into table menu_mstr fields terminated by ',' ignore 0 Lines; show errors;" $DB -u $ROOT ;
mysql --local-infile -e "load data local infile 'menutree.csv' replace into table menu_tree fields terminated by ',' ignore 0 Lines; show errors;" $DB -u $ROOT ;
mysql --local-infile -e "load data local infile 'shiftmstr.csv' replace into table shift_mstr fields terminated by ',' ignore 0 Lines; show errors;" $DB -u $ROOT ;
mysql --local-infile -e "load data local infile 'clockcode.csv' replace into table clock_code fields terminated by ',' ignore 0 Lines; show errors;" $DB -u $ROOT ;
mysql --local-infile -e "load data local infile 'ovctrl.csv' replace into table ov_ctrl fields terminated by ',' ignore 0 Lines; show errors;" $DB -u $ROOT ;
mysql --local-infile -e "load data local infile 'counter.csv' replace into table counter fields terminated by ',' ignore 0 Lines; show errors;" $DB -u $ROOT ;
mysql --local-infile -e "load data local infile 'codemstr.csv' replace into table code_mstr fields terminated by ',' ignore 0 Lines; show errors;" $DB -u $ROOT ;
mysql --local-infile -e "load data local infile 'labelzebra.csv' replace into table label_zebra fields terminated by ',' ignore 0 Lines; show errors;" $DB -u $ROOT ;
mysql --local-infile -e "load data local infile 'edimstr.csv' replace into table edi_mstr fields terminated by ',' ignore 0 Lines; show errors;" $DB -u $ROOT ;
mysql --local-infile -e "load data local infile 'editpmstr.csv' replace into table editp_mstr fields terminated by ',' ignore 0 Lines; show errors;" $DB -u $ROOT ;

echo "loading user account...."
# the next line is a workaround to stop the error of no user when dropped
mysql -e "grant usage on *.* to 'bs_user'@'localhost';" $DB -u $ROOT ;
# now drop and create user
mysql -e "drop user  'bs_user'@'localhost';" $DB -u $ROOT  ;
mysql -e "create user 'bs_user'@'localhost' identified by 'bspasswd';" $DB -u $ROOT  ;
mysql -e "grant select,insert,delete,update on *.* to 'bs_user'@'localhost'" $DB -u $ROOT ;

echo "loading some default records for tables....."
mysql -e "insert into car_mstr (car_code, car_desc, car_scac, car_type) values ('USPS','US POSTAL SERVICE','USPS','carrier') ;" $DB -u $ROOT ;
mysql -e "insert into cust_term (cut_code, cut_desc, cut_days) values ('N30','NET 30','30') ;" $DB -u $ROOT ;
mysql -e "insert into cust_term (cut_code, cut_desc, cut_days) values ('N00','Due Now','0') ;" $DB -u $ROOT ;
mysql -e "insert into frt_mstr (frt_code) values ('PICKUP') ;" $DB -u $ROOT ;
mysql --local-infile -e "load data local infile 'sitemstr.csv' replace into table site_mstr fields terminated by ',' ignore 0 Lines; show errors;" $DB -u $ROOT ;
mysql --local-infile -e "load data local infile 'glcal.csv' replace into table gl_cal fields terminated by ',' ignore 0 Lines; show errors;" $DB -u $ROOT ;
mysql --local-infile -e "load data local infile 'acctmstr.csv' replace into table ac_mstr fields terminated by ',' ignore 0 Lines; show errors;" $DB -u $ROOT ;
mysql --local-infile -e "load data local infile 'bankmstr.csv' replace into table bk_mstr fields terminated by ',' ignore 0 Lines; show errors;" $DB -u $ROOT ;
mysql --local-infile -e "load data local infile 'cmmstr.csv' replace into table cm_mstr fields terminated by ',' ignore 0 Lines; show errors;" $DB -u $ROOT ;
mysql --local-infile -e "load data local infile 'ovmstr.csv' replace into table ov_mstr fields terminated by ',' ignore 0 Lines; show errors;" $DB -u $ROOT ;
mysql --local-infile -e "load data local infile 'uommstr.csv' replace into table uom_mstr fields terminated by ',' ignore 0 Lines; show errors;" $DB -u $ROOT ;
mysql --local-infile -e "load data local infile 'curmstr.csv' replace into table cur_mstr fields terminated by ',' ignore 0 Lines; show errors;" $DB -u $ROOT ;
mysql --local-infile -e "load data local infile 'pos_ctrl.csv' replace into table pos_ctrl fields terminated by ',' ignore 0 Lines; show errors;" $DB -u $ROOT ;
mysql --local-infile -e "load data local infile 'edictrl.csv' replace into table edi_ctrl fields terminated by ',' ignore 0 Lines; show errors;" $DB -u $ROOT ;
mysql --local-infile -e "load data local infile 'deptmstr.csv' replace into table dept_mstr fields terminated by ',' ignore 0 Lines; show errors;" $DB -u $ROOT ;
mysql --local-infile -e "load data local infile 'plmstr.csv' replace into table pl_mstr fields terminated by ',' ignore 0 Lines; show errors;" $DB -u $ROOT ;
mysql --local-infile -e "load data local infile 'apctrl.csv' replace into table ap_ctrl fields terminated by ',' ignore 0 Lines; show errors;" $DB -u $ROOT ;
mysql --local-infile -e "load data local infile 'arctrl.csv' replace into table ar_ctrl fields terminated by ',' ignore 0 Lines; show errors;" $DB -u $ROOT ;
mysql --local-infile -e "load data local infile 'payctrl.csv' replace into table pay_ctrl fields terminated by ',' ignore 0 Lines; show errors;" $DB -u $ROOT ;
mysql --local-infile -e "load data local infile 'shipctrl.csv' replace into table ship_ctrl fields terminated by ',' ignore 0 Lines; show errors;" $DB -u $ROOT ;
mysql --local-infile -e "load data local infile 'ordctrl.csv' replace into table order_ctrl fields terminated by ',' ignore 0 Lines; show errors;" $DB -u $ROOT ;
mysql --local-infile -e "load data local infile 'cmctrl.csv' replace into table cm_ctrl fields terminated by ',' ignore 0 Lines; show errors;" $DB -u $ROOT ;
mysql --local-infile -e "load data local infile 'vdctrl.csv' replace into table vd_ctrl fields terminated by ',' ignore 0 Lines; show errors;" $DB -u $ROOT ;
mysql --local-infile -e "load data local infile 'invctrl.csv' replace into table inv_ctrl fields terminated by ',' ignore 0 Lines; show errors;" $DB -u $ROOT ;
mysql --local-infile -e "load data local infile 'glctrl.csv' replace into table gl_ctrl fields terminated by ',' ignore 0 Lines; show errors;" $DB -u $ROOT ;
mysql --local-infile -e "load data local infile 'glicdef.csv' replace into table glic_def fields terminated by ',' ignore 0 Lines; show errors;" $DB -u $ROOT ;
mysql --local-infile -e "load data local infile 'empmstr.csv' replace into table emp_mstr fields terminated by ',' ignore 0 Lines; show errors;" $DB -u $ROOT ;
mysql --local-infile -e "load data local infile 'timeclock.csv' replace into table time_clock fields terminated by ',' ignore 0 Lines; show errors;" $DB -u $ROOT ;
mysql --local-infile -e "load data local infile 'profilemstr.csv' replace into table pay_profile fields terminated by ',' ignore 0 Lines; show errors;" $DB -u $ROOT ;
mysql --local-infile -e "load data local infile 'profiledet.csv' replace into table pay_profdet fields terminated by ',' ignore 0 Lines; show errors;" $DB -u $ROOT ;

# now semicolon delimiter
mysql --local-infile -e "load data local infile 'usrmstr.csv' replace into table user_mstr fields terminated by ';' ignore 0 Lines; show errors;" $DB -u $ROOT ;
mysql --local-infile -e "load data local infile 'permmstr.csv' replace into table perm_mstr fields terminated by ';' ignore 0 Lines; show errors;" $DB -u $ROOT ;
mysql --local-infile -e "load data local infile 'permwmstr.csv' replace into table permw_mstr fields terminated by ';' ignore 0 Lines; show errors;" $DB -u $ROOT ;
mysql --local-infile -e "load data local infile 'mockmstr.csv' replace into table mock_mstr fields terminated by ';' ignore 0 Lines; show errors;" $DB -u $ROOT ;



echo 'finished install...'
echo 'you can launch by typing the following at the command line: '
echo 'java -cp ".:dist/*" bsmf.MainFrame'
echo ''
echo 'NOTE:'
echo 'login and password are admin and admin respectively'
echo 'if you receive a 'headless' java error... '
echo ' ...you may have to install the full JRE...not just headless'
echo ' ...some linux vendors provide a smaller size JRE as their default'

unset MYSQL_PWD
