@if "%DEBUG%"=="" echo off
setlocal enabledelayedexpansion

@rem ###############################################################
@rem Restore all context menu items
@rem ###############################################################
set "CHECK_ERROR=if not [!ERRORLEVEL!]==[0] ( echo Install Failed. && pause )"
if "%1"=="/restore" goto :RESTORE

@rem Request Elevated Privilegese
powershell "Start-Process cmd -ArgumentList @('/c', 'cd', '%~dp0', '&&', 'cmd', '/c', '%~dp0%~n0', '/restore') -Wait -verb runas"
%CHECK_ERROR%
exit /b %ERRORLEVEL%

:RESTORE
${RESTORE_BATS}
exit /b %ERRORLEVEL%
