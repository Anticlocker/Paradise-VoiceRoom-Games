@echo off
set "JAVA_HOME=C:\Users\Admin\.jdks\temurin-17"
set "PATH=C:\Users\Admin\.jdks\temurin-17\bin;%PATH%"
set "GRADLE_USER_HOME=c:\Users\Admin\OneDrive\Desktop\final weplay\.gradle_home"
call gradlew.bat --gradle-user-home "c:\Users\Admin\OneDrive\Desktop\final weplay\.gradle_home" testDebugUnitTest
