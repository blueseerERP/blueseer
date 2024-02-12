# program exports tables of a specified database name/path to csv formatted files in a specified output directory
# output files have table.csv name nomenclature in output directory
# typical usage:  ./exportsqlite.ps1 sometargetdatabase sometargetoutdir

param (
[string] $dbdir,
[string] $outdir
)

if (-not($dbdir)) { throw "You must supply a target directory/databasename" }
if (-not($outdir)) { throw "You must supply an out directory for csv table files " }



$tables = echo 'SELECT name FROM sqlite_master where type="table";' |sqlite3.exe $dbdir
foreach ($table in $tables) {
$outfile = $outdir + "/" + $table + ".csv"
$cmd = "select * from " + $table + ";"
sqlite3.exe -csv $dbdir $cmd | out-file -encoding utf8 $outfile
write-output $table
}


