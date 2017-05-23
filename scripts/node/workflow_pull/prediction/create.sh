#!/bin/bash
function analyze {
	dir=$1
	dir=${dir%*/}
    file=${dir##*/}
	filen=$file
	#sh extractCsvRow.sh 2 $filen
	#filen=$filen".extract.csv"
	cp $dir data	
	filen="data/"$filen								# adjust filename
	sed '/null/d' $filen > temp && mv temp $filen 
	tail -n +2 "$filen" > temp && mv temp $filen	# remove header
	#tac $filen > temp && mv temp $filen				# reverse
	Rscript holtwinters.r $filen					# analyze file
}

mkdir -p data
for dir in $1
do
	analyze $dir #"eventlog_middletier.csv"
done

