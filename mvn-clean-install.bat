@echo off
mvn clean install
echo.
echo Exit code: %ERRORLEVEL%
pause