#!/bin/bash
set -e

USAGE="Usage: $0 release_version"
if [[ $# != 1 ]]
then 
    echo "$USAGE"
    exit 1
fi

VERSION=$1

VERSION_REGEX="^[0-9]+\.[0-9]+\.[0-9]+$"
if [[ ! "$VERSION" =~ $VERSION_REGEX ]];
then
	exit 1
fi

SOURCE="./target/repository/*"
DEST_BASE="/projects/cishell/www/htdocs/p2"
DEST="$DEST_BASE/$VERSION/"

mkdir -p $DEST_BASE
mkdir "$DEST"
cp -R $SOURCE -t "$DEST"
echo "Copied the files from $SOURCE to $DEST"