
display_usage() { 
        echo -e "\nUsage: <Program> <ROW TO EXTRACT>" 
}

if [  $# -le 0 ]; then 
        display_usage
        exit 1
fi
if [[ $# == "--help" || $# == "-h" ]]; then 
        display_usage
        exit 0
fi

rowExtract=$1
mkdir -p extract/
rm ./extract/*extract.csv
touch ./extract/combined.csv
#touch ./extract/left.csv

for dir in ./results/*/*.csv
do
    dir=${dir%*/}
    file=${dir##*/}
    echo "$file"
    sh extractCsvRow.sh $rowExtract $dir > "extract/"$file".extract.csv"
done

for dir in ./extract/*extract.csv
do
    dir=${dir%*/}
    file=${dir##*/}
    echo "$file"
    python combinecsv.py $dir ./extract/combined.csv 
    mv combined.csv ./extract/combined.csv
done