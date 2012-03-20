#!/bin/bash
cd ../../..
application_list+=" rs.ac.ns.ftn.tmd.fuzzy.test.FuzzyNumberDraw"
application_list+=" demo.EmbeddedDemo"
application_list+=" org.eclipse.swt.examples.addressbook.AddressBook"
application_list+=" org.eclipse.swt.examples.clipboard.ClipboardExample"
application_list+=" org.eclipse.swt.examples.controlexample.ControlExample"
application_list+=" org.eclipse.swt.examples.dnd.DNDExample"
application_list+=" org.eclipse.swt.examples.imageanalyzer.ImageAnalyzer"
application_list+=" org.eclipse.swt.examples.hoverhelp.HoverHelp"
application_list+=" org.eclipse.swt.examples.javaviewer.JavaViewer"
application_list+=" org.eclipse.swt.examples.layoutexample.LayoutExample"
application_list+=" org.eclipse.swt.examples.browserexample.BrowserExample"
application_list+=" org.eclipse.swt.examples.paint.PaintExample"

guitar_dir= ./guitar/dist/guitar

source ./basic_workflow.conf

if [ $fault_num -gt 20 ]
then
    fault_num=20
fi

if [ `uname -s | grep -i cygwin | wc -c` -gt 0 ]
then
	osname="windows"	
elif [ `uname -s` = "Linux" ]
then
	osname="linux"
else
	osname="macosx"
fi

rm ${guitar_dir}/jars/swt-*.jar
cp ${guitar_dir}/jars/swt-3.7.1-$osname-$system_type.jar.ignore ${guitar_dir}/jars/swt-3.7.1-$osname-$system_type.jar

getTestFileName()
{
	testfile_path="$app_folder_name/resources/test_input_file"
	if [ -d $testfile_path ]
	then
		current_path=`pwd`
		testfile_name=`ls ./$testfile_path | tail -1`
		testfile_full_path=$current_path/$testfile_path/$testfile_name

		if [ -f $testfile_full_path ]
		then
			if [ `uname -s | grep -i cygwin | wc -c` -gt 0 ]
			then
				testfile_full_path=`cygpath -wp $testfile_full_path`
			elif [ `uname -s` = "Linux" ]
			then
				testfile_full_path=/$testfile_full_path
			fi
		else
			testfile_full_path="null"
		fi
	else
		testfile_full_path="null"
	fi
}

getFolderName()
{
	defaultIFS=$IFS
	IFS=.
	for token in $1
	do
		echo $token
	done
	IFS=$defaultIFS
	app_folder_name=$token
}

moveFileAndBuild()
{
	defaultIFS=$IFS
	IFS=.
	source_path="./$app_folder_name/src"
	class_path="./$app_folder_name/bin"
	for token in $1
	do
		echo $token
		source_path+="/$token"
		class_path+="/$token"
	done
	source_path+=".java"
	class_path+=".class"
	source_file="$token.java"
	IFS=$defaultIFS
	echo "$source_file $source_path"
	cp $2/$source_file $source_path	
	rm $class_path
	ant -f ./$app_folder_name/build.xml build
	cp $source_path $2/$source_file
}

runCobertura()
{
	defaultIFS=$IFS
	IFS=.
	for token1 in $1
	do
		echo $token1
	done

	inst_path=""
	for token2 in $1
	do
		if [ $token2 != $token1 ]
		then
			inst_path+="$token2/"
		fi
	done
	IFS=$defaultIFS
	echo "$inst_path"
	rm ./$app_folder_name/cobertura.ser
	#ant -f ./$app_folder_name/build.xml -Dargs="-c $app_name -g ./results/GUITAR-Default.GUI -e ./results/GUITAR-Default.EFG -t ./results/test.tst -gs ./results/GUITAR-Default.STA" -Dinst_path="$inst_path" test

	if [ $testfile_full_path != "null" ]
	then
		ant -f ./$app_folder_name/build.xml -Dargs="-c $app_name -g $result_dir/GUITAR-Default.GUI -e $result_dir/GUITAR-Default.EFG -t $result_dir/test.tst -gs $result_dir/GUITAR-Default.STA -tf $testfile_full_path" -Dinst_path="$inst_path" test
	else
	ant -f ./$app_folder_name/build.xml -Dargs="-c $app_name -g $result_dir/GUITAR-Default.GUI -e $result_dir/GUITAR-Default.EFG -t $result_dir/test.tst -gs $result_dir/GUITAR-Default.STA" -Dinst_path="$inst_path" test
	fi

	ant -f ./$app_folder_name/build.xml html-report
	ant -f ./$app_folder_name/build.xml xml-report
	mv $result_dir/cobertura/html $result_dir/cobertura/$token2-tc$2-html-report
	mv $result_dir/cobertura/xml $result_dir/cobertura/$token2-tc$2-xml-report

	flag=false
	if [ $2 -eq 1 ]
	then
		echo -ne "Coverage Matrix for $app_name\n\n" > $result_dir/CoverageMatrix.data
		flag=true
	fi
	ant -f ./$app_folder_name/build.xml -Dargs="./results/cobertura/$token2-tc$2-xml-report/coverage.xml ./results/CoverageMatrix.data $2 $flag" CoverageParser
}

for app_name in $application_list
do
	getFolderName $app_name
	result_dir=./$app_folder_name/results
	faulted_source_dir=./$app_folder_name/faulted_src
	echo $app_name $app_folder_name $result_dir

	rm -rf $result_dir
	mkdir -p $result_dir
	fault_matrix_file=$result_dir/FaultMatrix.data
	echo -ne "Fault Matrix for $app_name\n\n" > $fault_matrix_file
	for((i=1;i<=$fault_num;i++))
	do
		echo -ne "\t$i" >> $fault_matrix_file
	done
	echo "" >> $fault_matrix_file

	moveFileAndBuild $app_name $faulted_source_dir/$app_name/original
	ant -f ./$app_folder_name/build.xml -Dargs="-c $app_name -g ./results/GUITAR-Default.GUI" SitarRipper

	${guitar_dir}/gui2efg.sh -g $result_dir/GUITAR-Default.GUI -e $result_dir/GUITAR-Default.EFG
	${guitar_dir}/tc-gen-sq.sh -e $result_dir/GUITAR-Default.EFG -l $testcase_length -m $testcase_num -d $result_dir/testcases

	index=1

#	for test_case in `find $result_dir/testcases -name '*.tst'`
#	do
#		echo $test_case
#		cp $test_case $result_dir/test.tst
#		getTestFileName
#		if [ $testfile_full_path != "null" ]
#		then
#			ant -f ./$app_folder_name/build.xml -Dargs="-c $app_name -g ./results/GUITAR-Default.GUI -e ./results/GUITAR-Default.EFG -t ./results/test.tst -gs ./results/GUITAR-Default.STA -tf $testfile_full_path" SitarReplayer
#		else
#			ant -f ./$app_folder_name/build.xml -Dargs="-c $app_name -g ./results/GUITAR-Default.GUI -e ./results/GUITAR-Default.EFG -t ./results/test.tst -gs ./results/GUITAR-Default.STA" SitarReplayer
#		fi
#		#runCobertura $app_name $index
#		mv $result_dir/GUITAR-Default.STA $result_dir/original_GUITAR-Default.STA
#		echo -ne "tc$index" >> $fault_matrix_file
#		index=`expr $index + 1`
#	done

done

rm ./*.log

