@echo off
rem ------------------------------------------------------------------------------
rem Gradle startup script for Windows
rem ------------------------------------------------------------------------------

set DIR=%~dp0
set CLASSPATH=%DIR%gradle\wrapper\gradle-wrapper.jar

java -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*
