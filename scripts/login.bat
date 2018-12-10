@echo off

set mypath=%cd%

cd %~dp0

set jver="0"

if exist "%PROGRAMFILES(X86)%" (set bit="x64") else (set bit="x86")

for /f tokens^=2-5^ delims^=.-_^" %%j in ('java -fullversion 2^>^&1') do set "jver=%%j%%k%%l%%m"

echo "java version is " %jver%
echo "bit is " %bit%


if %jver%=="0" (
     goto :nojava
     ) else (
     goto :startapp
     )


:nojava
  echo "java is not available" 
  goto :EOF


:startapp
 start javaw -cp "dist\*" bsmf.MainFrame
 goto :EOF



goto :EOF








