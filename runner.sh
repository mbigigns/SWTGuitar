cp $1 GUITAR-Default.EFG
cp $2 GUITAR-Default.GUI

if ["$3" = ""]; then
	java -jar Vis.jar
else
	cp $3 testcase.tst
	java -jar Vis.jar $3
fi
