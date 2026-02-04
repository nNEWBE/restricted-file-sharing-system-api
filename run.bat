@echo off
echo Starting Restricted File Sharing System with Java 25...
echo (Forcing --enable-native-access=ALL-UNNAMED)

rem We use --args just in case, but usually system properties need -D passed to JVM
rem The bootRun task is configured in build.gradle to use the jvmArgs.

call gradlew.bat bootRun --no-daemon
pause
