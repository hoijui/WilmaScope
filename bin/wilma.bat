@echo off
if "%JAVA_HOME%a"=="a" goto JAVA_HOME_NOTSET_ERROR
if "%WILMA_HOME%a"=="a" goto WILMA_HOME_NOTSET_ERROR
cd "%WILMA_HOME%\lib"
"%JAVA_HOME%\bin\java" -cp wilma.jar;jaxp.jar;crimson.jar;xalan.jar wilmatoo.control.Test %1
goto DONE
:JAVA_HOME_NOTSET_ERROR
echo You must set the JAVA_HOME environment variable
echo   use: Start- Settings- Control Panel- System- Advanced- Environment Variables
echo   possible values might be: 
echo     c:\jdk1.3.1 - if you installed the full J2SDK (JDK) V1.3.1
echo     c:\Program Files\JavaSoft\JRE\1.3.1
echo       - if you installed the run time environment
pause
goto DONE
:WILMA_HOME_NOTSET_ERROR
echo You must set the WILMA_HOME environment variable
echo   use: Start- Settings- Control Panel- System-Advanced- Environment Variables
pause
:DONE
echo done
