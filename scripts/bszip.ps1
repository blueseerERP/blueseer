param(
[string] $version,
[string] $patch,
[string] $onlyjar
)

if (-not($version)) { throw "You must supply a version:  6.4, 6.5, etc" }


$wip = "c:\bs\wip"
# $patchvar = ".patch" + $version
#$patchsqlvar = ".patchsqlv" + $version

# get latest patch number
#$filecontent = get-content $patchvar
#foreach ($line in $filecontent) {
#     if ($line.startsWith("patch=")) {
#     $e = $line -split '='
#     $pc = [int]$e[1]
#     } 
#}


if ($onlyjar) {

# create jar only patch zip file
$jaronly = "blueseer.jaronly." + $version + "." + $patch  + ".zip"
rm $wip\$jaronly
compress-archive -update -path ..\dist\blueseer.jar -destinationpath $wip\$jaronly
compress-archive -update -path ..\dist\bsmf.jar -destinationpath $wip\$jaronly
compress-archive -update -path instructions.txt -destinationpath $wip\$jaronly

exit

}

# create jar only patch zip file
$jaronly = "blueseer.jaronly." + $version + "." + $patch  + ".zip"
rm $wip\$jaronly
compress-archive -update -path ..\dist\blueseer.jar -destinationpath $wip\$jaronly
compress-archive -update -path ..\dist\bsmf.jar -destinationpath $wip\$jaronly
compress-archive -update -path instructions.txt -destinationpath $wip\$jaronly

# create jar and jasper patch zip file
$jaronly = "blueseer.base.patch.zip"
$jaronly = "blueseer.base.patch." + $version + "." + $patch  + ".zip"
rm $wip\$jaronly
compress-archive -update -path ..\dist\blueseer.jar -destinationpath $wip\$jaronly
compress-archive -update -path ..\sf\jasper -destinationpath $wip\$jaronly


rm blueseer.patch.*.zip
$patchdir = "patchV" + $version + "P" + $pc
$patchmain = "blueseer.patch.ver." + $version + "." + "zip"
mkdir $patchdir
mkdir $patchdir\dist
cp ..\.patch $patchdir\
cp .patchsqlv80 $patchdir\
cp patch_install.bat $patchdir\
cp patch_install.sh $patchdir\
cp ..\jre26\lib\security\jssecacerts $patchdir\
cp ..\sf\patches\patch_instructions.pdf $patchdir\
cp ..\sf\jasper $patchdir\ -recurse
cp ..\sf\zebra $patchdir\ -recurse
cp ..\sf\edi\maps $patchdir\ -recurse
# cp ..\dist $patchdir\ -recurse
cp ..\dist\bsmf.jar $patchdir\dist\bsmf.jar
cp ..\dist\blueseer.jar $patchdir\dist\blueseer.jar
compress-archive -force -path $patchdir -destinationpath $wip\$patchmain
rmdir $patchdir -force -recurse

$myzip = "blueseer.server.mysql.win.v" + $version + ".zip"
rm $wip\$myzip
compress-archive -path ..\sf\zebra -destinationpath $wip\$myzip
compress-archive -update -path ..\sf\conf,..\sf\attachments,..\sf\logs,..\sf\patches,..\sf\temp,..\sf\custom,..\sf\jasper,..\sf\data,..\sf\edi,..\sf\images -destinationpath $wip\$myzip
compress-archive -update -path ..\dist -destinationpath $wip\$myzip
compress-archive -update -path mysql_install.bat -destinationpath $wip\$myzip
compress-archive -update -path login.bat -destinationpath $wip\$myzip
compress-archive -update -path sclnk.vbs -destinationpath $wip\$myzip
compress-archive -update -path bslogging.properties -destinationpath $wip\$myzip
compress-archive -update -path ..\.patch -destinationpath $wip\$myzip
compress-archive -update -path ..\jre26 -destinationpath $wip\$myzip

$myzip = "blueseer.sqlite.win.v" + $version + ".zip"
rm $wip\$myzip
compress-archive -path ..\sf\zebra -destinationpath $wip\$myzip
compress-archive -update -path ..\sf\conf,..\sf\attachments,..\sf\logs,..\sf\patches,..\sf\temp,..\sf\custom,..\sf\jasper,..\sf\data,..\sf\edi,..\sf\images -destinationpath $wip\$myzip
compress-archive -update -path ..\dist -destinationpath $wip\$myzip
compress-archive -update -path bs.cfg -destinationpath $wip\$myzip
compress-archive -update -path login.bat -destinationpath $wip\$myzip
compress-archive -update -path sclnk.vbs -destinationpath $wip\$myzip
compress-archive -update -path bslogging.properties -destinationpath $wip\$myzip
compress-archive -update -path ..\.patch -destinationpath $wip\$myzip
compress-archive -update -path ..\jre26 -destinationpath $wip\$myzip
#$zip = [System.IO.Compression.ZipFile]::Open("$wip\$myzip", [System.IO.Compression.ZipArchiveMode]::Update)
#$zip.CreateEntry("edi/in")
#$zip.Dispose()
