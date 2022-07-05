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


# create jar only patch zip file
$jaronly = "blueseer.jaronly.zip"
rm $wip\$jaronly
compress-archive -update -path ..\dist\blueseer.jar -destinationpath $wip\$jaronly
compress-archive -update -path ..\dist\bsmf.jar -destinationpath $wip\$jaronly
compress-archive -update -path ..\.patch -destinationpath $wip\$jaronly
compress-archive -update -path instructions.txt -destinationpath $wip\$jaronly
compress-archive -update -path ..\dist\icepdf-core-6.2.2.jar -destination $wip\$jaronly
compress-archive -update -path ..\dist\icepdf-viewer-6.2.2.jar -destination $wip\$jaronly
compress-archive -update -path ..\dist\commons-dbcp2-2.7.0.jar -destination $wip\$jaronly
compress-archive -update -path ..\dist\eddsa-0.3.0.jar -destination $wip\$jaronly
compress-archive -update -path ..\dist\sshj-0.32.0.jar -destination $wip\$jaronly
compress-archive -update -path ..\dist\sshd-core-2.8.0.jar -destination $wip\$jaronly
compress-archive -update -path ..\dist\bcpg-jdk18on-171.jar -destination $wip\$jaronly
compress-archive -update -path ..\dist\bcmail-jdk18on-171.jar -destination $wip\$jaronly
compress-archive -update -path ..\dist\bcprov-jdk18on-171.jar -destination $wip\$jaronly
compress-archive -update -path ..\dist\bcutil-jdk18on-171.jar -destination $wip\$jaronly
compress-archive -update -path ..\dist\bctls-jdk18on-171.jar -destination $wip\$jaronly
compress-archive -update -path ..\dist\bcpkix-jdk18on-171.jar -destination $wip\$jaronly



rm blueseer.patch.*.zip
$patchdir = "patchV" + $version + "P" + $pc
$patchzip = "blueseer.patch.ver." + $version + "." + ".zip"
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
cp ..\dist\icepdf-core-6.2.2.jar $patchdir\dist\icepdf-core-6.2.2.jar
cp ..\dist\icepdf-viewer-6.2.2.jar $patchdir\dist\icepdf-viewer-6.2.2.jar
cp ..\dist\commons-dbcp2-2.7.0.jar $patchdir\dist\commons-dbcp2-2.7.0.jar
cp ..\dist\eddsa-0.3.0.jar $patchdir\dist\eddsa-0.3.0.jar
cp ..\dist\sshj-0.32.0.jar $patchdir\dist\sshj-0.32.0.jar
cp ..\dist\sshd-core-2.8.0.jar $patchdir\dist\sshd-core-2.8.0.jar
cp ..\dist\bcpg-jdk18on-171.jar $patchdir\dist\bcpg-jdk18on-171.jar
cp ..\dist\bcmail-jdk18on-171.jar $patchdir\dist\bcmail-jdk18on-171.jar
cp ..\dist\bcprov-jdk18on-171.jar $patchdir\dist\bcprov-jdk18on-171.jar
cp ..\dist\bcutil-jdk18on-171.jar $patchdir\dist\bcutil-jdk18on-171.jar
cp ..\dist\bctls-jdk18on-171.jar $patchdir\dist\bctls-jdk18on-171.jar
cp ..\dist\bcpkix-jdk18on-171.jar $patchdir\dist\bcpkix-jdk18on-171.jar
compress-archive -force -path $patchdir -destinationpath $wip\$patchzip
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


