#!/bin/bash

ARCH=`uname -m`

cd ./bin/


if [ "$ARCH" == 'x86_64' ]; then
  java -cp :${CLASSPATH}:../lib/swt-3.5.2-gtk-am64.jar:../FuzzyArithmetic.jar rs.ac.ns.ftn.tmd.fuzzy.test.FuzzyNumberDraw

elif [ "$ARCH" == 'i686' ]; then
  java -cp :${CLASSPATH}:../lib/swt-3.5.2-gtk-x86.jar:../FuzzyArithmetic.jar rs.ac.ns.ftn.tmd.fuzzy.test.FuzzyNumberDraw

else 
  echo "Unknown platform."

fi

