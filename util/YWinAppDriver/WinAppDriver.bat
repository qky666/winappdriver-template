echo off
set arg1=%1
cd /D "%~dp0"
WinAppDriver.exe --urls http://127.0.0.1:%arg1:~0,4% --basepath %arg1:~4%
