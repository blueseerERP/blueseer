@echo off

if exist .update set /P PATCH=<.update

set "ARGS=%*"

if exist .bspem (
set "ARGS=%ARGS% -sshkey"
)


@echo "using args: %ARGS%"

set PATCHDIR="%~dp0\patches\%PATCH%"

if exist .update (
call :patchInstall
) else (
@echo "no patch file...proceeding"
)

cd %~dp0
IF "%1"=="-debug" (
jre17\bin\java -D"java.util.logging.config.file=bslogging.properties" -cp "custom\*;dist\*" bsmf.MainFrame %ARGS%
) else (
start jre17\bin\javaw -D"java.util.logging.config.file=bslogging.properties" -cp "custom\*;dist\*" bsmf.MainFrame %ARGS%
)

goto :eof

:patchInstall
@echo "patch trigger found..." >patchlog.txt
if exist %PATCHDIR%\dist (
   xcopy %PATCHDIR%\dist dist /E /I /Y /Q 2>&1
   @echo "copying blueseer.jar to dist folder" >>patchlog.txt
) 
if exist %PATCHDIR%\.patch (
   copy %PATCHDIR%\.patch .patch 2>&1
   @echo "copying .patch file" >>patchlog.txt
) 
if exist %PATCHDIR%\jasper (
   xcopy %PATCHDIR%\jasper jasper /E /I /Y /Q 2>&1
   @echo "copying jasper folder content" >>patchlog.txt
) 
if exist %PATCHDIR%\zebra (
   xcopy %PATCHDIR%\zebra zebra /E /I /Y /Q 2>&1
   @echo "copying zebra folder content" >>patchlog.txt
) 
@echo "done with patchInstall..." >>patchlog.txt
del /F /Q .update
exit /b




