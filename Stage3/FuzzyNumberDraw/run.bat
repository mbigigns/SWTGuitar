@echo off
SET CLASSPATH=%CLASSPATH%;%CD%/lib/swt-3.5.2-win32-x86.jar;%CD%/FuzzyArithmetic.jar
cd ./bin/
java rs.ac.ns.ftn.tmd.fuzzy.test.FuzzyNumberDraw
cd ..