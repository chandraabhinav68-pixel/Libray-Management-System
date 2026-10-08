@echo off
echo ========================================================
echo Building Library Management System Java Project...
echo ========================================================

if not exist bin mkdir bin

javac -cp "lib/sqlite-jdbc-3.45.1.0.jar" -d bin src\com\library\interfaces\*.java src\com\library\exceptions\*.java src\com\library\model\*.java src\com\library\db\*.java src\com\library\service\*.java src\com\library\ui\*.java src\com\library\main\*.java

if %ERRORLEVEL% EQU 0 (
    echo ========================================================
    echo Build Successful! Class files generated in bin/
    echo Run 'run.bat' to launch the application.
    echo ========================================================
) else (
    echo ========================================================
    echo Build Failed! Check compilation errors above.
    echo ========================================================
)
pause
