param(
[string] $version,
[string] $patch
)

if (-not($version)) { throw "You must supply a version:  6.3, 6.4, etc" }
if (-not($patch)) { throw "You must supply a 2nd parameter patch nbr:  1, 2, etc" }


$wip = "c:\bs\wip"
$patchvar = ".patch" + $version
$patchsqlvar = ".patchsqlv" + $version

# get latest patch number
$filecontent = get-content $patchvar
foreach ($line in $filecontent) {
     if ($line.startsWith("patch=")) {
     $e = $line -split '='
     $pc = [int]$e[1]
     } 
}


# create jar only patch zip file
$jaronly = "blueseer.jaronly.zip"
rm $wip\$jaronly
compress-archive -update -path ..\dist\blueseer.jar -destinationpath $wip\$jaronly
compress-archive -update -path ..\dist\bsmf.jar -destinationpath $wip\$jaronly
compress-archive -update -path ..\.patch -destinationpath $wip\$jaronly
compress-archive -update -path instructions.txt -destinationpath $wip\$jaronly



rm blueseer.patch.*.zip
$patchdir = "patchV" + $version + "P" + $pc
$patchzip = "blueseer.patch.ver." + $version + "." + $patch + "." + "zip"
$patchmain = "blueseer.patch.ver." + $version + "." + "zip"
mkdir $patchdir
mkdir $patchdir\dist
cp $patchvar $patchdir\.patch
cp $patchvar ..\.patch
cp $patchsqlvar $patchdir\
cp patch_install.bat $patchdir\
cp patch_install.sh $patchdir\
cp ..\sf\patches\patch_instructions.pdf $patchdir\
cp ..\sf\jasper $patchdir\ -recurse
cp ..\sf\zebra $patchdir\ -recurse
# cp ..\dist $patchdir\ -recurse
cp ..\dist\bsmf.jar $patchdir\dist\bsmf.jar
cp ..\dist\blueseer.jar $patchdir\dist\blueseer.jar
compress-archive -force -path $patchdir -destinationpath $wip\$patchzip
## patchmain will always be latest patch number (for app auto patch purposes)
cp $wip\$patchzip $wip\$patchmain
rmdir $patchdir -force -recurse

$myzip = "blueseer.mysql.win.v" + $version + ".zip"
rm $wip\$myzip
compress-archive -path ..\sf\zebra -destinationpath $wip\$myzip
compress-archive -update -path ..\sf\patches,..\sf\temp,..\sf\custom,..\sf\jasper,..\sf\data,..\sf\edi,..\sf\images -destinationpath $wip\$myzip
compress-archive -update -path ..\dist -destinationpath $wip\$myzip
compress-archive -update -path mysql_install.bat -destinationpath $wip\$myzip
compress-archive -update -path login.bat -destinationpath $wip\$myzip
compress-archive -update -path sclnk.vbs -destinationpath $wip\$myzip
compress-archive -update -path bslogging.properties -destinationpath $wip\$myzip
compress-archive -update -path ..\.patch -destinationpath $wip\$myzip
compress-archive -update -path ..\jre17 -destinationpath $wip\$myzip


