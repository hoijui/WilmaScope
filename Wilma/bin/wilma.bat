@echo off
if not exist "%JAVA_HOME%\bin\java.exe" goto JAVA_HOME_NOTSET_ERROR
if not exist "%WILMA_HOME%\bin\Wilma.bat" goto WILMA_HOME_NOTSET_ERROR
set ARG=%1
cd "%WILMA_HOME%\lib"
"%JAVA_HOME%\bin\java" -Xmx512m -cp .;..\classes;Jama-1.0.1.jar org.wilmascope.control.WilmaMain %ARG%
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
echo You must ensure that the WILMA_HOME environment variable is set to the
echo directory where you installed Wilma
echo   use: Start- Settings- Control Panel- System-Advanced- Environment Variables
pause
:DONE
echo done
