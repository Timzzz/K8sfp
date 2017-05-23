
display_usage() { 
        echo -e "\nUsage: <Program> <ROW TO EXTRACT> <FILES>" 
}

if [  $# -le 1 ]; then 
        display_usage
        exit 1
fi
if [[ $# == "--help" || $# == "-h" ]]; then 
        display_usage
        exit 0
fi

rowExtract=$1
files=$2
mkdir -p extract/
rm ./extract/*extract.csv
rm ./extract/combined.csv
touch ./extract/combined.csv
#touch ./extract/left.csv
count=0

for dir in $files
do
    dir=${dir%*/}
    file=${dir##*/}
    echo "$file"
    sh extractCsvRow.sh $rowExtract $dir > "extract/"$file".extract.csv"
    #python extractCsvRow.py $dir $rowExtract "extract/"$file".extract.csv"
    count=$(($count+1))
done

for dir in ./extract/*extract.csv
do
    dir=${dir%*/}
    file=${dir##*/}
    echo "$file"
    python combinecsv.py $dir ./extract/combined.csv 
    mv combined.csv ./extract/combined.csv
done

echo "Count: $count"
