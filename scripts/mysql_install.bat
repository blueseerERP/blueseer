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
@echo DRIVER=com.mysql.jdbc.Driver>>bsconfig




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


@echo "loading 'some' default records for tables....."
mysql --defaults-extra-file=my.cnf  -e "insert into car_mstr (car_code,car_type) values ('ACME','carrier') ;" %DB% 
mysql --defaults-extra-file=my.cnf  -e "insert into cust_term (cut_code, cut_desc, cut_days) values ('N30','NET 30','30') ;" %DB% 
mysql --defaults-extra-file=my.cnf  -e "insert into cust_term (cut_code, cut_desc, cut_days) values ('N00','Due Now','0') ;" %DB% 
mysql --defaults-extra-file=my.cnf  -e "insert into frt_mstr (frt_code) values ('PICKUP') ;" %DB% 
mysql --defaults-extra-file=my.cnf  -e "insert into task_mstr (task_id, task_desc) values ('100','A Sample Task List') ;" %DB% 
mysql --defaults-extra-file=my.cnf  -e "insert into task_det (taskd_id, taskd_owner, taskd_desc, taskd_sequence) values ('100','admin', 'Sample Task Action 1', '1') ;" %DB% 
mysql --defaults-extra-file=my.cnf  -e "insert into task_det (taskd_id, taskd_owner, taskd_desc, taskd_sequence) values ('100','admin', 'Sample Task Action 2', '2') ;" %DB% 
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'sitemstr.csv' replace into table site_mstr fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
mysql --defaults-extra-file=my.cnf  --local-infile -e "update user_mstr set user_id = 'admin', user_passwd = 'admin', user_email = 'someone@acme.com', user_lname = 'one', user_fname = 'some'  ;" %DB% 
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'glcal.csv' replace into table gl_cal fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'acctmstr.csv' replace into table ac_mstr fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'uommstr.csv' replace into table uom_mstr fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'curmstr.csv' replace into table cur_mstr fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'pos_ctrl.csv' replace into table pos_ctrl fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'edictrl.csv' replace into table edi_ctrl fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'cmmstr.csv' replace into table cm_mstr fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'vdmstr.csv' replace into table vd_mstr fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'vprmstr.csv' replace into table vpr_mstr fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'vdpmstr.csv' replace into table vdp_mstr fields terminated by ',' lines terminated by '\n' ignore 0 Lines; show errors;" %DB% 
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'deptmstr.csv' replace into table dept_mstr fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'cmsdet.csv' replace into table cms_det fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'cmedimstr.csv' replace into table cmedi_mstr fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'plmstr.csv' replace into table pl_mstr fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'itemmstr.csv' replace into table item_mstr fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'itemcostmstr.csv' replace into table item_cost fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'itemcostrmstr.csv' replace into table itemr_cost fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'pbmmstr.csv' replace into table pbm_mstr fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'cprmstr.csv' replace into table cpr_mstr fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'cupmstr.csv' replace into table cup_mstr fields terminated by ',' lines terminated by '\n' ignore 0 Lines; show errors;" %DB% 
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'wfmstr.csv' replace into table wf_mstr fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'wcmstr.csv' replace into table wc_mstr fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'bankmstr.csv' replace into table bk_mstr fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'whmstr.csv' replace into table wh_mstr fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'locmstr.csv' replace into table loc_mstr fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'apctrl.csv' replace into table ap_ctrl fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'arctrl.csv' replace into table ar_ctrl fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'shipctrl.csv' replace into table ship_ctrl fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'ordctrl.csv' replace into table order_ctrl fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'ovmstr.csv' replace into table ov_mstr fields terminated by ',' ignore 0 Lines; show errors;" %DB% 

mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'glctrl.csv' replace into table gl_ctrl fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'glicdef.csv' replace into table glic_def fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'empmstr.csv' replace into table emp_mstr fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'usrmstr.csv' replace into table user_mstr fields terminated by ';' ignore 0 Lines; show errors;" %DB% 
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'permmstr.csv' replace into table perm_mstr fields terminated by ';' ignore 0 Lines; show errors;" %DB% 
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'permwmstr.csv' replace into table perm_mstr fields terminated by ';' ignore 0 Lines; show errors;" %DB% 
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'mockmstr.csv' replace into table mock_mstr fields terminated by ';' ignore 0 Lines; show errors;" %DB% 

@echo "finished install..."
@echo "BlueSeer should start now..."
@echo "Login = 'admin' and Password = 'admin'"
@echo "If it does not start...click the 'login.bat' file in the blueseer dir"

ping -n 5 127.0.0.1 > nul

cd ..

call login.bat
