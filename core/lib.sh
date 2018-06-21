shopt -s extglob
for f in */ 
do
  if [[ -d $f ]] 
    then
    cd $f
    echo $f
      srcCount=$(find -maxdepth 1 -name "src" -type d | wc -l)
      if [[ $srcCount -eq 1 ]]
      then
        rm -rf  src/main/resources/META-INF  
      fi 
    cd ..
  fi
done


