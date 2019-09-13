rm blueseer.patch.ver.4.2.zip
compress-archive -path ..\dist\blueseer.jar -destinationpath blueseer.patch.ver.4.2.zip
compress-archive -update -path .patch -destinationpath blueseer.patch.ver.4.2.zip

rm blueseer.mysql.win.v43.zip
compress-archive -path ..\sf\zebra -destinationpath blueseer.mysql.win.v43.zip
compress-archive -update -path ..\sf\temp,..\sf\jasper,..\sf\data,..\sf\edi,..\sf\images -destinationpath blueseer.mysql.win.v43.zip
compress-archive -update -path ..\dist -destinationpath blueseer.mysql.win.v43.zip
compress-archive -update -path mysql_install.bat -destinationpath blueseer.mysql.win.v43.zip
compress-archive -update -path login.bat -destinationpath blueseer.mysql.win.v43.zip
compress-archive -update -path bslogging.properties -destinationpath blueseer.mysql.win.v43.zip
compress-archive -update -path .patch -destinationpath blueseer.mysql.win.v43.zip

rm blueseer.mysql.linux.v43.zip
compress-archive -path ..\sf\zebra -destinationpath blueseer.mysql.linux.v43.zip
compress-archive -update -path ..\sf\temp,..\sf\jasper,..\sf\data,..\sf\edi,..\sf\images -destinationpath blueseer.mysql.linux.v43.zip
compress-archive -update -path ..\dist -destinationpath blueseer.mysql.linux.v43.zip
compress-archive -update -path bslogging.properties -destinationpath blueseer.mysql.linux.v43.zip
compress-archive -update -path .patch -destinationpath blueseer.mysql.linux.v43.zip
compress-archive -update -path mysql_install.sh -destinationpath blueseer.mysql.linux.v43.zip
compress-archive -update -path controlM.sh -destinationpath blueseer.mysql.linux.v43.zip
