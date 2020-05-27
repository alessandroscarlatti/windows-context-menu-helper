@if "%DEBUG%"=="" echo off
setlocal enabledelayedexpansion

@rem ###############################################################
@rem Sync all context menu items
@rem ###############################################################
pushd "%~dp0"

:SYNC
java -Dcmh.project.task="generate" -Dcmh.project.dir="%~dp0." -jar ContextMenuHelper.jar
call :CHECK_ERROR %ERRORLEVEL%
popd
exit /b %ERRORLEVEL%

:CHECK_ERROR
if not [%1]==[0] ( echo Sync Failed. && pause )
exit /b %1