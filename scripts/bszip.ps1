rm blueseer.patch.ver.4.2.zip
compress-archive -path ..\dist\blueseer.jar -destinationpath blueseer.patch.ver.4.2.zip
rm blueseer.sqlite.deb.zip
compress-archive -path ..\sf\zebra -destinationpath blueseer.sqlite.deb.zip
compress-archive -update -path ..\sf\temp,..\sf\jasper,..\sf\data,..\sf\edi,..\sf\images -destinationpath blueseer.sqlite.deb.zip
compress-archive -update -path ..\dist -destinationpath blueseer.sqlite.deb.zip
compress-archive -update -path ..\sf\linux\blueseer-4.1.5 -destinationpath blueseer.sqlite.deb.zip
compress-archive -update -path bsconfig -destinationpath blueseer.sqlite.deb.zip
compress-archive -update -path bslogging.properties -destinationpath blueseer.sqlite.deb.zip
compress-archive -update -path sqlite_install.bat -destinationpath blueseer.sqlite.deb.zip
compress-archive -update -path debprep.sh -destinationpath blueseer.sqlite.deb.zip
compress-archive -update -path controlM.sh -destinationpath blueseer.sqlite.deb.zip
rm blueseer.mysql.win.v42.zip
compress-archive -path ..\sf\zebra -destinationpath blueseer.mysql.win.v42.zip
compress-archive -update -path ..\sf\temp,..\sf\jasper,..\sf\data,..\sf\edi,..\sf\images -destinationpath blueseer.mysql.win.v42.zip
compress-archive -update -path ..\dist -destinationpath blueseer.mysql.win.v42.zip
compress-archive -update -path mysql_install.bat -destinationpath blueseer.mysql.win.v42.zip
compress-archive -update -path login.bat -destinationpath blueseer.mysql.win.v42.zip
compress-archive -update -path bslogging.properties -destinationpath blueseer.mysql.win.v42.zip
rm blueseer.mysql.linux.v42.zip
compress-archive -path ..\sf\zebra -destinationpath blueseer.mysql.linux.v42.zip
compress-archive -update -path ..\sf\temp,..\sf\jasper,..\sf\data,..\sf\edi,..\sf\images -destinationpath blueseer.mysql.linux.v42.zip
compress-archive -update -path ..\dist -destinationpath blueseer.mysql.linux.v42.zip
compress-archive -update -path bslogging.properties -destinationpath blueseer.mysql.linux.v42.zip
compress-archive -update -path mysql_install.sh -destinationpath blueseer.mysql.linux.v42.zip
compress-archive -update -path controlM.sh -destinationpath blueseer.mysql.linux.v42.zip
