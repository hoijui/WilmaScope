"%JAVA_HOME%"\bin\jar cvfm "%WILMA_HOME%"\lib\wilma.jar "%WILMA_HOME%"\META-INF\MANIFEST.MF -C "%WILMA_HOME%"\classes .
"%JAVA_HOME%"\bin\jar uvf "%WILMA_HOME%"\lib\wilma.jar -C "%WILMA_HOME%"\lib WILMA_CONSTANTS.properties
"%JAVA_HOME%"\bin\jar uvf "%WILMA_HOME%"\lib\wilma.jar -C "%WILMA_HOME%"\lib org
