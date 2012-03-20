##

if [[ "$1" = "" || "$2" = "" ]]; then
	echo ""
	echo "For visualization: ./runner.sh <path/fileName.EFG> <path/fileName.GUI>"
	echo ""	
	echo "For visualization with test case: ./runner.sh <path/fileName.EFG> <path/fileName.GUI> <path/fileName.tst>"
	echo ""
else
	if ["$3" = ""]; then
		java -jar Vis.jar $1 $2
	else
		cp $3 GUITAR-Default.tst
		java -jar Vis.jar $1 $2 $3
	fi
fi
