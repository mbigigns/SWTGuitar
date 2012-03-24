#!/bin/sh

base_dir=`dirname $0`
jvm_args=""

#Check the OS
if [ `uname -s | grep -i cygwin | wc -c` -gt 0 ]
then
    osname="windows"	
elif [ `uname -s` = "Linux" ]
then
    osname="linux"
else
    osname="mac"
    jvm_args="-d32 -XstartOnFirstThread"
fi

#add osname to build.properties file for ant compilation
echo "swt.jar.name=swt-${osname}.jar" > build.properties

ant jar

#Uncomment to run the jar file
#java_cmd="java ${jvm_args} -jar Vis.jar" 
#echo ${java_cmd}
#${java_cmd}
