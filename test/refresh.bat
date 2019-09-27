@echo off

@echo "Installing BlueSeer.... "
@echo ""
@echo ""

cd %~dp0

set "DB=bsdb"

@echo "creating bsconfig file...."
@echo DBTYPE=sqlite>>bsconfig
@echo DB=data\bsdb.db>>bsconfig
@echo USER=bs_user>>bsconfig
@echo PASS=bspasswd>>bsconfig
@echo IP=localhost>>bsconfig
@echo PORT=3306>>bsconfig
@echo DRIVER=org.sqlite.JDBC>>bsconfig

copy ..\.patch %~dp0\
cd data

if exist bsdb.db del bsdb.db

@echo "creating database schema...."
sqlite3.exe bsdb.db <blueseer.sqlite
sqlite3.exe bsdb.db <sq.txt


@echo ""
@echo ""
@echo ""
@echo "finished install..."

cd ..

