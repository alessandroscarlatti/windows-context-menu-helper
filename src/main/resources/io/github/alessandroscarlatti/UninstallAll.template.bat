@if "%DEBUG%"=="" echo off
setlocal enabledelayedexpansion

@rem ###############################################################
@rem Uninstall all context menu items
@rem ###############################################################
set "CHECK_ERROR=if not [!ERRORLEVEL!]==[0] ( echo Install Failed. && pause )"
if "%1"=="/uninstall" goto :UNINSTALL

@rem Request Elevated Privilegese
powershell "Start-Process cmd -ArgumentList @('/c', 'cd', '%~dp0', '&&', 'cmd', '/c', '%~dp0%~n0', '/uninstall') -Wait -verb runas"
%CHECK_ERROR%
exit /b %ERRORLEVEL%

:UNINSTALL
${UNINSTALL_BATS}
exit /b %ERRORLEVEL%
