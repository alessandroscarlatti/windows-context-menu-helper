@if "%DEBUG%"=="" echo off
setlocal enabledelayedexpansion

@rem ###############################################################
@rem Install all context menu items
@rem ###############################################################
set "CHECK_ERROR=if not [!ERRORLEVEL!]==[0] ( echo Install Failed. && pause )"
if "%1"=="/install" goto :INSTALL

@rem Request Elevated Privilegese
powershell "Start-Process cmd -ArgumentList @('/c', 'cd', '%~dp0', '&&', 'cmd', '/c', '%~dp0%~n0', '/install') -Wait -verb runas"
%CHECK_ERROR%
exit /b %ERRORLEVEL%

:INSTALL
${INSTALL_BATS}
exit /b %ERRORLEVEL%
