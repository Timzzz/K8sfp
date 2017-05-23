
display_usage() { 
        echo -e "\nUsage: <Program> <FOLDER>" 
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
rm ./extract/combined.csv
touch ./extract/combined.csv

for dir in $1/*/*.csv
do
    dir=${dir%*/}
    file=${dir##*/}
    echo "reversing $dir"
    cat $dir | awk '1 { last = NR; line[last] = $0; } END { print line[1]; for (i = last; i > 1; i--) { print line[i]; } }' > temp.dat && mv temp.dat $dir
    #sh extractCsvRow.sh $rowExtract $dir > "extract/"$file".extract.csv"
done

rm temp.dat

