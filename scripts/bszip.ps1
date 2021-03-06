param(
[string] $version
)

if (-not($version)) { throw "You must supply a version:  51, 52, etc" }


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



rm blueseer.patch.*.zip
$patchdir = "patchV" + $version + "P" + $pc
$patchzip = "blueseer.patch.ver." + $version + "." + $pc + ".zip"
mkdir $patchdir
cp $patchvar $patchdir\.patch
cp $patchvar ..\.patch
cp $patchsqlvar $patchdir\
cp patch_install.bat $patchdir\
cp patch_install.sh $patchdir\
cp ..\sf\patches\patch_instructions.pdf $patchdir\
cp ..\dist\blueseer.jar $patchdir\
cp ..\dist\bsmf.jar $patchdir\
cp ..\sf\jasper $patchdir\ -recurse
cp ..\sf\zebra $patchdir\ -recurse
compress-archive -force -path $patchdir -destinationpath $wip\$patchzip
rmdir $patchdir -force -recurse

$myzip = "blueseer.mysql.win.v" + $version + ".zip"
rm $wip\$myzip
compress-archive -path ..\sf\zebra -destinationpath $wip\$myzip
compress-archive -update -path ..\sf\patches,..\sf\temp,..\sf\jasper,..\sf\data,..\sf\edi,..\sf\images -destinationpath $wip\$myzip
compress-archive -update -path ..\dist -destinationpath $wip\$myzip
compress-archive -update -path mysql_install.bat -destinationpath $wip\$myzip
compress-archive -update -path login.bat -destinationpath $wip\$myzip
compress-archive -update -path bslogging.properties -destinationpath $wip\$myzip
compress-archive -update -path ..\.patch -destinationpath $wip\$myzip
compress-archive -update -path ..\jre11 -destinationpath $wip\$myzip


