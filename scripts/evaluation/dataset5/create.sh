#!/bin/bash
function analyze {
	filen=$1
	sh extractCsvRow.sh 3 $filen
	filen=$filen".extract.csv"
	mv $filen data	
	filen="data/"$filen								# adjust filename
	tail -n +2 "$filen" > temp && mv temp $filen	# remove header
	tac $filen > temp && mv temp $filen				# reverse
	Rscript holtwinters.r $filen					# analyze file
}

mkdir -p data
analyze "eventlog_middletier.csv"

