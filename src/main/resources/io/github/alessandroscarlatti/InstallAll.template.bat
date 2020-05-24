@if "%DEBUG%"=="" echo off
setlocal enabledelayedexpansion

@rem ###############################################################
@rem Install all context menu items
@rem ###############################################################
if "%1"=="/install" goto :INSTALL

@rem Request Elevated Privileges
pushd "%~dp0"
powershell "Start-Process cmd -ArgumentList @('/c', 'pushd', '%~dp0', '&&', 'cmd', '/c', '%~dp0%~nx0', '/install') -Wait -verb runas"
call :CHECK_ERROR %ERRORLEVEL%
exit /b %ERRORLEVEL%

:INSTALL
${INSTALL_BATS}
if not "%DEBUG%"=="" pause
call :CHECK_ERROR %ERRORLEVEL%
exit /b %ERRORLEVEL%

:CHECK_ERROR
if not [%1]==[0] ( echo Sync Failed. && pause )
exit /b %1