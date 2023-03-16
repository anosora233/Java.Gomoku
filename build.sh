#!/bin/bash
# build class
javac src/*.java

# generate executed jar
jar cvfe Gomoku.jar src.MainFrame src
