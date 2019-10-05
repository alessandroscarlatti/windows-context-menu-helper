@if "%DEBUG%"=="" echo off
setlocal enabledelayedexpansion

@rem ###############################################################
@rem Sync all context menu items
@rem ###############################################################
set "CHECK_ERROR=if not [!ERRORLEVEL!]==[0] ( echo Sync Failed. && pause )"
if "%1"=="/sync" goto :SYNC

@rem Request Elevated Privilegese
powershell "Start-Process cmd -ArgumentList @('/c', 'cd', '%~dp0', '&&', 'cmd', '/c', '%~dp0%~n0', '/sync') -Wait -verb runas"
%CHECK_ERROR%
exit /b %ERRORLEVEL%

:SYNC
java -jar WindowsContextMenuHelper.jar
exit /b %ERRORLEVEL%
