@echo off

set mypath=%cd%

cd %~dp0

start jre11\bin\javaw -D"java.util.logging.config.file=bslogging.properties" -cp "dist\*" bsmf.MainFrame
rem example of Localization ...Sri Lanka as an example
rem start jre11\bin\javaw -D"java.util.logging.config.file=bslogging.properties"  -D"user.language=ta" -D"user.country=LK" -cp "dist\*" bsmf.MainFrame


