@echo off

set project_home=./

setlocal enabledelayedexpansion
set classpath=
for %%f in (./lib/*.jar) do (
    set onefile=%%f
    set classpath=!classpath!;%project_home%/lib/!onefile!
)

@echo off

echo %classpath%

set p_classpath=%CLASSPATH%;%project_home%\bin;%classpath%
java -classpath %p_classpath% cliperDeploy.Main testreadjar
pause;
