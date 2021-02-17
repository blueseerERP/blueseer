@echo off


@echo "BlueSeer PATCH NOTE: this patch is applicable to Version 4.3" 

@echo ""
@echo ""

@echo "BlueSeer PATCH NOTE:  cd to current directory"
cd %~dp0

@echo "BlueSeer PATCH NOTE:  Check if blueseer patch directory structure is correct"
set result=false
set a=%~dp0
set b=blueseer\\patches\\
echo "%a%" | FINDSTR /I /C:"%b%" && (set result=true)

if "%result%" == "true" (
@echo "BlueSeer PATCH NOTE:  BlueSeer directory structure is correct...continuing"
) else (
@echo "BlueSeer PATCH NOTE:  blueseer patch structure is incorrect...exiting"
@echo "BlueSeer PATCH NOTE:  patch must be unzipped and placed in blueseer\patches folder as blueseer\patches\<newpatchdir>"
exit
)


@echo "BlueSeer PATCH NOTE:  copying jasper directory to installation folder"
copy %~dp0\jasper ..\..\jasper
@echo "BlueSeer PATCH NOTE:  copying zebra directory to installation folder"
copy %~dp0\zebra ..\..\zebra
@echo "BlueSeer PATCH NOTE:  copying jar to dist folder"
copy %~dp0\blueseer.jar ..\..\dist\
@echo "BlueSeer PATCH NOTE:  copying .patch file to root folder"
copy %~dp0\.patch ..\..\


@echo "BlueSeer PATCH NOTE:  executing sql schema updates"
..\..\data\sqlite3.exe ..\..\data\bsdb.db <.patchsqliteV50


@echo "BlueSeer PATCH NOTE:  done"
