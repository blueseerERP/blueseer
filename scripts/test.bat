del /F /Q c:\bs\wip\test\edi\out\*.*
cd c:\bs\wip\test
java -cp ".;c:\bs\blueseer\dist\*" com.blueseer.edi.EDILoad
cd c:\bs\blueseer\scripts
