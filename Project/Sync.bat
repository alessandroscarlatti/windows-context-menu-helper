@if "%DEBUG%"=="" echo off
setlocal enabledelayedexpansion

@rem ###############################################################
@rem Sync all context menu items
@rem ###############################################################
if "%1"=="/sync" goto :SYNC

@rem Request Elevated Privileges
pushd "%~dp0"
powershell -command "Start-Process cmd -ArgumentList @('/c', 'pushd', '%~dp0', '&&', 'cmd', '/c', '%~dp0%~nx0', '/sync') -Wait -verb runas; exit $lastExitCode"
call :CHECK_ERROR %ERRORLEVEL%
popd
exit /b %ERRORLEVEL%

:SYNC
java -Dcmh.project.task="sync" -Dcmh.project.dir="%~dp0." -jar ContextMenuHelper.jar
call :CHECK_ERROR %ERRORLEVEL%
exit /b %ERRORLEVEL%

:CHECK_ERROR
if not [%1]==[0] ( echo Sync Failed. && pause )
exit /b %1