rem zip for linux mysql
powershell.exe -command "rm blueseer.install.linux.zip"
powershell.exe -command "compress-archive -path .\sf\zebra -destinationpath blueseer.install.linux.zip"
powershell.exe -command "compress-archive -update -path .\sf\temp,.\sf\jasper,.\sf\data,.\sf\edi,.\sf\images -destinationpath blueseer.install.linux.zip"
powershell.exe -command "compress-archive -update -path dist -destinationpath blueseer.install.linux.zip"
powershell.exe -command "compress-archive -update -path bs_install.sh -destinationpath blueseer.install.linux.zip"
powershell.exe -command "compress-archive -update -path login.bat -destinationpath blueseer.install.linux.zip"
