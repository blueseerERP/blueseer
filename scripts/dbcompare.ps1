# program compares a target database schema against a current bsdb schema.  Both databases should be located in the same server instance. 
# The admin password is required for the server instance.
# typical usage:  ./dbcompare.ps1 sometargetdatabasename mysqladminpassword >somefile.txt

param (
[string] $db,
[string] $passwd
)

if (-not($db)) { throw "You must supply a target databasename" }
if (-not($passwd)) { throw "You must supply a database admin password" }

$passwd = "-p" + $passwd.trim()
$fc_x = ""
$fc_y = ""

$tables = echo 'show tables;' |mysql -u root $passwd bsdb
foreach ($table in $tables) {
$cmd = "describe " + $table + ";"
$fc_command = "SELECT count(*) as 'fieldcount' from information_schema.columns where table_name = " + "'" + $table + "'" + " and table_schema = database();"

$fc_x = echo $fc_command |mysql -u root $passwd bsdb 2>$null
$fc_y = echo $fc_command |mysql -u root $passwd $db 2>$null

$fc_x_string = $fc_x -join ","
$fc_y_string = $fc_y -join ","

$x = echo $cmd |mysql -u root $passwd bsdb 2>$null
$y = echo $cmd |mysql -u root $passwd $db 2>$null

$b = "true";
$messg = "processing table: " + $table

if ($fc_x_string -ne $fc_y_string)  {
$fcmessg = "field count is different: " + $fc_x + " <-> " + $fc_y;
write-output $fcmessg
}

$fcmessg = "";

write-output $messg

for ($i = 0; $i -lt $x.length; $i++) {
for ($j = 0; $j -lt $y.length; $j++) {
  if (($i -eq $j) -and ($x[$i] -ne $y[$j])) {
  $b = "false";
  $arr = $x[$i].split();
  $diff = $x[$i] + " <---> " + $y[$j]
  write-output $diff
  if ($arr.count -gt 1  -and ($arr[1] -Match "int.*" -or $arr[1] -Match "decimal.*")) {
   $default = "'0'";
  } else {
   $default = "''";
  }
  $text = "field1: " + $arr[0] + " field2: " + $arr[1]
  $commandtext = "alter table " + $table + " modify " + $arr[0] + " " + $arr[1] + " NOT NULL DEFAULT " + $default + ";"
  write-output $text
  write-output $commandtext
  }
}
}
$ans = "Table name: " + $table + "  "  + $b
#if ($b -eq "false") {
# write-output $ans
#}
}

