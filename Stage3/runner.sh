##

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


if [[ "$1" = "" || "$2" = "" ]]; then
	echo ""
	echo "For visualization: ./runner.sh <path/fileName.EFG> <path/fileName.GUI>"
	echo ""	
	echo "For visualization with test case: ./runner.sh <path/fileName.EFG> <path/fileName.GUI> <path/fileName.tst>"
	echo ""
else
	if ["$3" = ""]; then
		java $jvm_args -jar Vis.jar $1 $2
	else
		cp $3 GUITAR-Default.tst
		java $jvm_args -jar Vis.jar $1 $2 $3
	fi
fi
