$wip = "c:\bs\wip"

# get latest patch number
$filecontent = get-content .patch
foreach ($line in $filecontent) {
     if ($line.startsWith("patch=")) {
     $e = $line -split '='
     $pc = [int]$e[1]
     } 
}



rm blueseer.patch.*.zip
$patchdir = "patchV51P" + $pc
$patchzip = "blueseer.patch.ver.51." + $pc + ".zip"
mkdir $patchdir
cp .patch $patchdir\
cp .patch ..\
cp .patchsqlv51 $patchdir\
cp patch_install.bat $patchdir\
cp patch_install.sh $patchdir\
cp ..\sf\patches\patch_instructions.pdf $patchdir\
cp ..\dist\blueseer.jar $patchdir\
cp ..\dist\bsmf.jar $patchdir\
cp ..\sf\jasper $patchdir\ -recurse
cp ..\sf\zebra $patchdir\ -recurse
compress-archive -force -path $patchdir -destinationpath $wip\$patchzip
rmdir $patchdir -force -recurse

rm $wip\blueseer.mysql.win.v51.zip
compress-archive -path ..\sf\zebra -destinationpath $wip\blueseer.mysql.win.v51.zip
compress-archive -update -path ..\sf\patches,..\sf\temp,..\sf\jasper,..\sf\data,..\sf\edi,..\sf\images -destinationpath $wip\blueseer.mysql.win.v51.zip
compress-archive -update -path ..\dist -destinationpath $wip\blueseer.mysql.win.v51.zip
compress-archive -update -path mysql_install.bat -destinationpath $wip\blueseer.mysql.win.v51.zip
compress-archive -update -path login.bat -destinationpath $wip\blueseer.mysql.win.v51.zip
compress-archive -update -path bslogging.properties -destinationpath $wip\blueseer.mysql.win.v51.zip
compress-archive -update -path .patch -destinationpath $wip\blueseer.mysql.win.v51.zip
compress-archive -update -path ..\jre8 -destinationpath $wip\blueseer.mysql.win.v51.zip


