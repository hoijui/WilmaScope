@echo off
if %JAVA_HOME%a==a goto JAVA_HOME_NOTSET_ERROR
java -cp %JAVA_HOME%\lib\tools.jar;lib\ant.jar;lib\jaxp.jar;lib\crimson.jar;lib\xalan.jar org.apache.tools.ant.Main %1
goto DONE
:JAVA_HOME_NOTSET_ERROR
echo You must set the JAVA_HOME variable, eg: set JAVA_HOME=c:\jdk1.3.1
goto DONE
:DONE
echo done