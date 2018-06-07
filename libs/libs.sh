#!/bin/bash

shopt -s extglob
for f in */ 
do
  if [[ -d $f ]] 
    then
    cd $f
    targetCount=$(find -maxdepth 1 -name "target" -type d | wc -l)
    if [[ $targetCount -eq 1 ]]
    then
        sed -i 's/<packaging>eclipse-plugin<\/packaging>//' pom.xml
        touch bnd.bnd
    fi
    # for f1 in */
    # do
    #   if [[ -d $f1 ]] 
    #   then
    #     echo $f1

    #     if [[ $f1 = "target/" ]]
    #       then
    #         mkdir -p src/main/java
    #         mkdir -p src/main/resources
    #         mv src/* src/main/java
    #         mv 
    #       break
    #     fi
    #   fi   
    # done
    cd ..
  fi
done


