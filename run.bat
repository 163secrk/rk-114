@echo off
chcp 65001 >nul
cd /d "%~dp0"
if not exist "bin" mkdir bin
echo Compiling...
javac -encoding UTF-8 -d bin src\model\*.java src\algorithm\*.java src\ui\*.java src\Main.java
if %errorlevel% neq 0 (
    echo Compilation failed!
    pause
    exit /b 1
)
echo Compilation successful!
echo Running...
java -cp bin Main
pause
