@echo off
if exist "img-*.png" del /F /Q "img-*.png"
set in=%~1
if not defined in set in=test.png
set args= -Din="%in%"
for %%J in ( *.java ) do call :java %%~nJ
goto :eof
:java
  if exist "%~1.class" del "%~1.class"
  if exist "%~1.class" goto :run
  echo compiling "%~1.java" ...
  javac "%~1.java"
:run
  if not exist "%~1.class" goto :error
  echo runing "%~1" %args% ...
  java -classpath .  %args% "%~1" >"%~1.hta"
:view
  if not exist "%~1.hta" goto :error
  start "" "%~1.hta"
:end 
  echo .
goto :eof
:error
  