@if "%DEBUG%"=="" echo off
setlocal enabledelayedexpansion

@rem ###############################################################
@rem Restore all context menu items
@rem ###############################################################
if "%1"=="/restore" goto :RESTORE

@rem Request Elevated Privileges
pushd "%~dp0"
powershell "Start-Process cmd -ArgumentList @('/c', 'pushd', '%~dp0', '&&', 'cmd', '/c', '%~dp0%~nx0', '/restore') -Wait -verb runas"
call :CHECK_ERROR %ERRORLEVEL%
exit /b %ERRORLEVEL%

:RESTORE
${RESTORE_BATS}
if not "%DEBUG%"=="" pause
call :CHECK_ERROR %ERRORLEVEL%
exit /b %ERRORLEVEL%

:CHECK_ERROR
if not [%1]==[0] ( echo Sync Failed. && pause )
exit /b %1