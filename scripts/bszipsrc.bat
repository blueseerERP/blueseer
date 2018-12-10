echo off
powershell.exe -command "rm src.zip"
powershell.exe -command "compress-archive -path .\sf -destinationpath src.zip"
powershell.exe -command "compress-archive -update -path nbproject -destinationpath src.zip"
powershell.exe -command "compress-archive -update -path src -destinationpath src.zip"
powershell.exe -command "compress-archive -update -path edi -destinationpath src.zip"
powershell.exe -command "compress-archive -update -path mylib -destinationpath src.zip"
powershell.exe -command "compress-archive -update -path login.bat -destinationpath src.zip"
powershell.exe -command "compress-archive -update -path bsconfig -destinationpath src.zip"
