@if "%DEBUG%"=="" echo off
setlocal enabledelayedexpansion
set command={ launchApp -self '%~dp0' }
powershell -command "ipmo ./Palette.psm1; runScriptBlock %command%"
if not [%ERRORLEVEL%]==[0] ( pause )
exit /b %ERRORLEVEL%
