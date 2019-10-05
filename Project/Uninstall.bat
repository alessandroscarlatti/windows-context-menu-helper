@if "%DEBUG%"=="" echo off
setlocal enabledelayedexpansion

@rem ###############################################################
@rem Uninstall all context menu items
@rem ###############################################################
if "%1"=="/uninstall" goto :UNINSTALL

@rem Request Elevated Privileges
pushd "%~dp0"
powershell "Start-Process cmd -ArgumentList @('/c', 'pushd', '%~dp0', '&&', 'cmd', '/c', '%~dp0%~nx0', '/uninstall') -Wait -verb runas"
call :CHECK_ERROR %ERRORLEVEL%
exit /b %ERRORLEVEL%

:UNINSTALL
java -Dcmh.project.task="uninstall" -Dcmh.project.dir="%~dp0." -jar ContextMenuHelper.jar
call :CHECK_ERROR %ERRORLEVEL%
exit /b %ERRORLEVEL%

:CHECK_ERROR
if not [%1]==[0] ( echo Sync Failed. && pause )
exit /b %1