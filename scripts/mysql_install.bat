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
@echo [client]>>my.cnf
@echo user = root>>my.cnf
@echo password = %pass%>>my.cnf
@echo host = localhost>>my.cnf 


@echo "creating database schema...."
mysql --defaults-extra-file=my.cnf -e "drop database if exists %DB%;"  
mysql --defaults-extra-file=my.cnf -e "create database if not exists %DB%;"
mysql --defaults-extra-file=my.cnf %DB%  <blueseer.schema 


@echo "Loading panel_mstr class schema...."
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'panelmstr.csv' replace into table panel_mstr fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
@echo "Loading menu_mstr class schema...."
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'menumstr.csv' replace into table menu_mstr fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
@echo "Loading menu_tree class schema...."
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'menutree.csv' replace into table menu_tree fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
@echo "Loading shift_mstr class schema...."
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'shiftmstr.csv' replace into table shift_mstr fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
@echo "Loading clock_code class schema...."
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'clockcode.csv' replace into table clock_code fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
@echo "Loading ov_ctrl class schema...."
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'ovctrl.csv' replace into table ov_ctrl fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
@echo "Loading counter class schema...."
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'counter.csv' replace into table counter fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
@echo "Loading code_mstr class schema...."
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'codemstr.csv' replace into table code_mstr fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
@echo "Loading label_zebra class schema...."
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'labelzebra.csv' replace into table label_zebra fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
@echo "Loading edi_mstr class schema...."
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'edimstr.csv' replace into table edi_mstr fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
@echo "Loading editp_mstr class schema...."
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'editpmstr.csv' replace into table editp_mstr fields terminated by ',' ignore 0 Lines; show errors;" %DB% 


@echo "loading user account...."
mysql --defaults-extra-file=my.cnf -e "grant usage on *.* to 'bs_user'@'localhost';" %DB% 
mysql --defaults-extra-file=my.cnf -e "drop user 'bs_user'@'localhost';" %DB% 
mysql --defaults-extra-file=my.cnf -e "create user 'bs_user'@'localhost' identified by 'bspasswd';" %DB% 
mysql --defaults-extra-file=my.cnf -e "grant select,insert,delete,update on *.* to 'bs_user'@'localhost'" %DB% 


@echo "loading 'some' default records for tables....."
mysql --defaults-extra-file=my.cnf  -e "insert into car_mstr (car_code, car_Desc, car_scac, car_type) values ('USPS','US POSTAL SERVICE','USPS','carrier') ;" %DB% 
mysql --defaults-extra-file=my.cnf  -e "insert into cust_term (cut_code, cut_desc, cut_days) values ('N30','NET 30','30') ;" %DB% 
mysql --defaults-extra-file=my.cnf  -e "insert into cust_term (cut_code, cut_desc, cut_days) values ('N00','Due Now','0') ;" %DB% 
mysql --defaults-extra-file=my.cnf  -e "insert into frt_mstr (frt_code) values ('PICKUP') ;" %DB% 
@echo "Loading site_mstr class schema...."
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'sitemstr.csv' replace into table site_mstr fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
@echo "Loading gl_cal class schema...."
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'glcal.csv' replace into table gl_cal fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
@echo "Loading ac_mstr class schema...."
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'acctmstr.csv' replace into table ac_mstr fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
@echo "Loading bk_mstr class schema...."
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'bankmstr.csv' replace into table bk_mstr fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
@echo "Loading cm_mstr class schema...."
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'cmmstr.csv' replace into table cm_mstr fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
@echo "Loading cms_det class schema...."
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'cmsdet.csv' replace into table cms_det fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
@echo "Loading ov_mstr class schema...."
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'uommstr.csv' replace into table uom_mstr fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
@echo "Loading cur_mstr class schema...."
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'curmstr.csv' replace into table cur_mstr fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
@echo "Loading pos_ctrl class schema...."
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'pos_ctrl.csv' replace into table pos_ctrl fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
@echo "Loading edi_ctrl class schema...."
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'edictrl.csv' replace into table edi_ctrl fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
@echo "Loading dept_mstr class schema...."
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'deptmstr.csv' replace into table dept_mstr fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
@echo "Loading pl_mstr class schema...."
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'plmstr.csv' replace into table pl_mstr fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
@echo "Loading ap_ctrl class schema...."
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'apctrl.csv' replace into table ap_ctrl fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
@echo "Loading ar_ctrl class schema...."
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'arctrl.csv' replace into table ar_ctrl fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
@echo "Loading pay_ctrl class schema...."
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'payctrl.csv' replace into table pay_ctrl fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
@echo "Loading ship_ctrl class schema...."
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'shipctrl.csv' replace into table ship_ctrl fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
@echo "Loading order_ctrl class schema...."
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'ordctrl.csv' replace into table order_ctrl fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
@echo "Loading cm_ctrl class schema...."
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'cmctrl.csv' replace into table cm_ctrl fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
@echo "Loading vd_ctrl class schema...."
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'vdctrl.csv' replace into table vd_ctrl fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
@echo "Loading inv_ctrl class schema...."
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'invctrl.csv' replace into table inv_ctrl fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
@echo "Loading ov_mstr class schema...."
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'ovmstr.csv' replace into table ov_mstr fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
@echo "Loading gl_ctrl class schema...."
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'glctrl.csv' replace into table gl_ctrl fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
@echo "Loading glic_def class schema...."
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'glicdef.csv' replace into table glic_def fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
@echo "Loading emp_mstr class schema...."
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'empmstr.csv' replace into table emp_mstr fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
@echo "Loading time_clock class schema...."
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'timeclock.csv' replace into table time_clock fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
@echo "Loading pay_profile class schema...."
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'profilemstr.csv' replace into table pay_profile fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
@echo "Loading pay_profdet class schema...."
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'profiledet.csv' replace into table pay_profdet fields terminated by ',' ignore 0 Lines; show errors;" %DB% 
@echo "Loading user_mstr class schema...."
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'usrmstr.csv' replace into table user_mstr fields terminated by ';' ignore 0 Lines; show errors;" %DB% 
@echo "Loading perm_mstr class schema...."
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'permmstr.csv' replace into table perm_mstr fields terminated by ';' ignore 0 Lines; show errors;" %DB% 
@echo "Loading perm_mstr 2 class schema...."
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'permwmstr.csv' replace into table perm_mstr fields terminated by ';' ignore 0 Lines; show errors;" %DB% 
@echo "Loading mock_mstr class schema...."
mysql --defaults-extra-file=my.cnf  --local-infile -e "load data local infile 'mockmstr.csv' replace into table mock_mstr fields terminated by ';' ignore 0 Lines; show errors;" %DB% 

@echo "finished install..."
@echo "Login = 'admin' and Password = 'admin'"
@echo "Click the login.bat file in the BlueSeer Directory to log in"

ping -n 5 127.0.0.1 > nul

cd ..

REM call login.bat
