[cmdletbinding()]
param (
[string] $file,
[string] $file2
)

###  This script will compare two sqlite databases for tables with different field counts
###  dump both databases into files with the following command in sqlite prompt
###   open 1st database with sqlite
###  .output junk1
###  .fullschema --indent
###  .quit 

###   open 2nd database with sqlite
###  .output junk2
###  .fullschema --indent
###  .quit 


$mf = get-content $file
$array1 = [System.Collections.ArrayList]::new()
$tablecount = 0
$i = 0
$tablename = ""
foreach ($line in $mf) {
$i++
     if ($line.startsWith("CREATE TABLE"))  {
	$tablecount++
        if ($tablecount -gt 1) {
        $message = ($tablecount - 1).ToString() + " " + $mytable  + " " + $fieldcount.ToString()
        write-output $message
        $item = $mytable + "=" + $fieldcount.ToString()
        [void]$array1.Add($item)
        }
        $arr = $line.Split(" ")
        $tablename = $arr[5]
        if ($tablecount -eq 1) {
          $mytable = $arr[5]
        }
        $fieldcount = 0
        $mytable = $tablename
     }
     if ($line.startsWith(" ")) {
        $fieldcount++
     }
} # foreach
        $message = $tablecount.ToString() + " " + $mytable  + " " + $fieldcount.ToString()
        write-output $message
        $item = $mytable + "=" + $fieldcount.ToString()
        [void]$array1.Add($item)
$databasecount1 = $tablecount

### now table 2

$mf = get-content $file2
$array2 = [System.Collections.ArrayList]::new()
$tablecount = 0
$i = 0
$tablename = ""
foreach ($line in $mf) {
$i++
     if ($line.startsWith("CREATE TABLE"))  {
	$tablecount++
        if ($tablecount -gt 1) {
        $message = ($tablecount - 1).ToString() + " " + $mytable  + " " + $fieldcount.ToString()
        write-output $message
        $item = $mytable + "=" + $fieldcount.ToString()
        [void]$array2.Add($item)
        }
        $arr = $line.Split(" ")
        $tablename = $arr[5]
        if ($tablecount -eq 1) {
          $mytable = $arr[5]
        }
        $fieldcount = 0
        $mytable = $tablename
     }
     if ($line.startsWith(" ")) {
        $fieldcount++
     }
} # foreach
        $message = $tablecount.ToString() + " " + $mytable  + " " + $fieldcount.ToString()
        write-output $message
        $item = $mytable + "=" + $fieldcount.ToString()
        [void]$array2.Add($item)
$databasecount2 = $tablecount

$arrayx = $array1 | Sort-Object 
$arrayz = $array2 | Sort-Object 
for($i=0; $i -lt $arrayx.Count; $i++) {
    for($j=0; $j -lt $arrayz.Count; $j++) {
    if ($arrayz[$j].startsWith($arrayx[$i].substring(0,10))) {
    $arrx = $arrayx[$i].Split("=")
    $arrz = $arrayz[$j].Split("=")
    $summary = $arrayx[$i] + "  ---> " + $arrayz[$j]
    if ($arrx[1] -ne $arrz[1]) {
    write-output $summary
    }
    break
    }
    }
}

$outstring = "Total Table count for database 1: " + $databasecount1
write-output $outstring
$outstring = "Total Table count for database 2: " + $databasecount2
write-output $outstring

$tablenames1 = echo "select name from sqlite_master where type = 'table' order by name;" |sqlite3 bsdb.db
$tablenames2 = echo "select name from sqlite_master where type = 'table' order by name;" |sqlite3 other.db
write-output $tablenames1.count
write-output $tablenames2.count
foreach ($t1 in $tablenames1) {
   if ($tablenames2 -notcontains $t1) {
   write-output $t1
   } 
}

