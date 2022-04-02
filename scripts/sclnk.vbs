Set WshShell = CreateObject("WScript.Shell") 
WshShell.Run chr(34) & "login.bat" & Chr(34), 0
Set WshShell = Nothing
