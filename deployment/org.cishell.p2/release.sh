#!/bin/bash
set -e

USAGE="Usage: $0 release_version [--stable]"
if [[ $# < 1 || $# > 2 ]]
then 
    echo "$USAGE"
    exit 1
fi

VERSION=$1

if [[ "$2" ]]
then
    if [[ "$2" != "--stable" ]]
    then
	echo "$USAGE"
	exit 1
    fi
fi

VERSION_REGEX="^[0-9]+\.[0-9]+\.[0-9]+$"
if [[ ! "$VERSION" =~ $VERSION_REGEX ]];
then 
    echo "Normal release versions look like 1.0.13"
    read -e -p "Are you sure '$VERSION' is what you want? (y/n):" REPLY

    if [[ "$REPLY" != "y" ]] 
    then
	exit 1
    fi
fi

SOURCE="./target/repository/*"
DEST_BASE="/projects/cishell/www/htdocs/p2"
DEST="$DEST_BASE/$VERSION/"

mkdir -p $DEST_BASE
mkdir "$DEST"
cp -R $SOURCE -t "$DEST"
echo "Copied the files from $SOURCE to $DEST"

if [[ "$2" ]]
then
    if [[ -e "$DEST_BASE/stable" ]]
    then
        rm "$DEST_BASE/stable"
    fi
    
    ln -s "$DEST" "$DEST_BASE/stable"
    echo "Stable build link updated"
fi