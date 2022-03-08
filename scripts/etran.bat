@echo off
rem -if, -of, -id, -od, -m, -x
set javapath=C:\BlueSeer\jre17\bin
%javapath%\java -cp ".;c:\bs\blueseer\scripts\test;c:\bs\blueseer\dist\*" com.blueseer.edi.EDIbs %*
