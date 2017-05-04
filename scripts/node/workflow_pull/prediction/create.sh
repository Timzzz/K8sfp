#!/bin/bash
function analyze {
	filen=$1
	#sh extractCsvRow.sh 2 $filen
	#filen=$filen".extract.csv"
	cp $filen data	
	filen="data/"$filen								# adjust filename
	sed '/null/d' $filen > temp && mv temp $filen 
	tail -n +2 "$filen" > temp && mv temp $filen	# remove header
	tac $filen > temp && mv temp $filen				# reverse
	Rscript holtwinters.r $filen					# analyze file
}

mkdir -p data
analyze $1 #"eventlog_middletier.csv"

