@echo off
:build class
javac.exe src\*.java -encoding utf-8

:generate executed jar
jar.exe cvfe Gomoku.jar src.MainFrame src
