cd sf\data
@echo "creating database schema...."
@echo "this may take a few minutes...."
sqlite3.exe bsdb.db <blueseer.sqlite

@echo "loading panel_mstr..."
sqlite3.exe bsdb.db <panel_mstr.sqlite
@echo "loading menu mstr..."
sqlite3.exe bsdb.db <menu_mstr.sqlite
@echo "loading menu tree..."
sqlite3.exe bsdb.db <menu_tree.sqlite
@echo "loading remaining control data..."
sqlite3.exe bsdb.db <shift_mstr.sqlite
sqlite3.exe bsdb.db <clock_code.sqlite
sqlite3.exe bsdb.db <ov_ctrl.sqlite
sqlite3.exe bsdb.db <counter.sqlite
sqlite3.exe bsdb.db <code_mstr.sqlite
sqlite3.exe bsdb.db <label_zebra.sqlite
sqlite3.exe bsdb.db <edi_mstr.sqlite
sqlite3.exe bsdb.db <editp_mstr.sqlite

sqlite3.exe bsdb.db <sq.txt
