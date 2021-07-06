; Script generated by the Inno Setup Script Wizard.
; SEE THE DOCUMENTATION FOR DETAILS ON CREATING INNO SETUP SCRIPT FILES!

#define MyAppName "BlueSeer"
#define MyAppVersion "5.2"
#define MyAppPublisher "VCSCode"
#define MyAppURL "http://www.blueseer.com/"
#define MyAppExeName "javaw"
#define bsconfig "bsconfig"

[Setup]
; NOTE: The value of AppId uniquely identifies this application.
; Do not use the same AppId value in installers for other applications.
; (To generate a new GUID, click Tools | Generate GUID inside the IDE.)
AppId={{1053C404-0E47-4C71-903B-7AB81B8157A0}
AppName={#MyAppName}
AppVersion={#MyAppVersion}
;AppVerName={#MyAppName} {#MyAppVersion}
AppPublisher={#MyAppPublisher}
AppPublisherURL={#MyAppURL}
AppSupportURL={#MyAppURL}
AppUpdatesURL={#MyAppURL}
UsePreviousAppDir=no
DefaultDirName={sd}\{#MyAppName}
DisableProgramGroupPage=yes
OutputBaseFilename=blueseer.sqlite.win.v52
Compression=lzma
SolidCompression=yes
PrivilegesRequired=poweruser
DisableDirPage=no
OutputDir=c:\bs\wip

[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Tasks]
Name: "desktopicon"; Description: "{cm:CreateDesktopIcon}"; GroupDescription: "{cm:AdditionalIcons}"; 

[Files]
Source: "C:\bs\blueseer\scripts\login.bat"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\bs\blueseer\scripts\bslogging.properties"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\bs\blueseer\.patch"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\bs\blueseer\scripts\bsconfig.sqlite"; DestDir: "{app}"; DestName: "{#bsconfig}"; Flags: ignoreversion
Source: "C:\bs\bsmf\documentation\documentation.pdf"; DestDir: "{app}"; Flags: ignoreversion
Source: "C:\bs\blueseer\sf\data\*"; DestDir: "{app}\data"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "C:\bs\blueseer\dist\*"; DestDir: "{app}\dist"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "C:\bs\blueseer\sf\temp\*"; DestDir: "{app}\temp"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "C:\bs\blueseer\sf\patches\*"; DestDir: "{app}\patches"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "C:\bs\blueseer\sf\edi\*"; DestDir: "{app}\edi"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "C:\bs\blueseer\sf\jasper\*"; DestDir: "{app}\jasper"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "C:\bs\blueseer\sf\zebra\*"; DestDir: "{app}\zebra"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "C:\bs\blueseer\sf\images\*"; DestDir: "{app}\images"; Flags: ignoreversion recursesubdirs createallsubdirs
Source: "C:\java\jre11\*"; DestDir: "{app}\jre11"; Flags: ignoreversion recursesubdirs createallsubdirs
; NOTE: Don't use "Flags: ignoreversion" on any shared system files

[Icons]
;Name: "{commonprograms}\{#MyAppName}"; Filename: "{app}\{#MyAppExeName}" ; WorkingDir: "{app}";
Name: "{commonprograms}\{#MyAppName}"; Filename: "{app}\jre11\bin\javaw.exe"; WorkingDir: "{app}"; Parameters: "-Djava.util.logging.config.file=bslogging.properties -cp dist/* bsmf.MainFrame" ; Tasks: desktopicon ; IconFilename: "{app}\images\bs.ico"
Name: "{commondesktop}\{#MyAppName}"; Filename: "{app}\jre11\bin\javaw.exe"; WorkingDir: "{app}"; Parameters: "-Djava.util.logging.config.file=bslogging.properties -cp dist/* bsmf.MainFrame" ; Tasks: desktopicon ; IconFilename: "{app}\images\bs.ico"

[Run]
;Filename: "{app}\{#MyAppExeName}"; Description: "{cm:LaunchProgram,{#StringChange(MyAppName, '&', '&&')}}"; Flags: shellexec postinstall skipifsilent

[Code]
function GetJavawExe(Param: string):string;
var
   Path: string;
  begin
  Result := '';
  Path := FileSearch('javaw.exe', GetEnv('PATH'));
  if Path = '' then
  begin
    Log('Java not found in PATH');
  end
    else
  begin
    Path := ExtractFileDir(Path);
    Log(Format('Java is in "%s"', [Path]));
    Result := Path + '\javaw.exe';
  end;
  end;

function CutJavaVersionPart(var V: string): Integer;
var
  S: string;
  P: Integer;
begin
  if Length(V) = 0 then
  begin
    Result := 0;
  end
    else
  begin
    P := Pos('.', V);
    if P = 0 then P := Pos('_', V);

    if P > 0 then
    begin
      S := Copy(V, 1, P - 1);
      Delete(V, 1, P);
    end
      else
    begin
      S := V;
      V := '';
    end;
    Result := StrToIntDef(S, 0);
  end;
end;

function MaxJavaVersion(V1, V2: string): string;
var
  Part1, Part2: Integer;
  Buf1, Buf2: string;
begin
  Buf1 := V1;
  Buf2 := V2;
  Result := '';
  while (Result = '') and
        ((Buf1 <> '') or (Buf2 <> '')) do
  begin
    Part1 := CutJavaVersionPart(Buf1);
    Part2 := CutJavaVersionPart(Buf2);
    if Part1 > Part2 then Result := V1
      else
    if Part2 > Part1 then Result := V2;
  end;
end;

function GetJavaVersion(): string;
var
  TempFile: string;
  ResultCode: Integer;
  S: AnsiString;
  P: Integer;
  Path: string;
begin
  TempFile := ExpandConstant('{tmp}\javaversion.txt');
  if (not ExecAsOriginalUser(
            ExpandConstant('{cmd}'), '/c java -version 2> "' + TempFile + '"', '',
            SW_HIDE, ewWaitUntilTerminated, ResultCode)) or
     (ResultCode <> 0) then
  begin
    Log('Failed to execute java -version');
  end
    else
  if not LoadStringFromFile(TempFile, S) then
  begin
    Log(Format('Error reading file %s', [TempFile]));
  end
    else
  if Copy(S, 1, 14) <> 'java version "' then
  begin
    Log('Output of the java -version not as expected');
  end
    else
  begin
    Delete(S, 1, 14);
    P := Pos('"', S);
    if P = 0 then
    begin
      Log('Output of the java -version not as expected');
    end
      else
    begin
      SetLength(S, P - 1);
      Result := S;
    end;
  end;

  

   
	
  DeleteFile(TempFile);
end;

function HasJava1Dot7OrNewer: Boolean;
begin
  Result := (MaxJavaVersion('1.6.9', GetJavaVersion) <> '1.6.9');
end;

function InitializeSetup(): Boolean;
var
  ErrorCode: Integer;
begin
 

  Result := true;
//  if not Result then
//  begin
//    Result := MsgBox(ExpandConstant('{cm:JavaRequired}'), mbConfirmation, MB_YESNO) = idYes;
//    if Result then
//    begin
//      ShellExec(
 //       'open', 'https://www.java.com/getjava/', '', '', SW_SHOWNORMAL, ewNoWait, ErrorCode);
//    end;
//  end;

end;
