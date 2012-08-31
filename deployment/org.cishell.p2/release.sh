#!/bin/bash
set -e

USAGE="Usage: $0 release_version"
if [[ $# != 1 ]]; then 
    echo "$USAGE"
    exit 1
fi

VERSION=$1

VERSION_REGEX="^[0-9]+\.[0-9]+\.[0-9]+$"
if [[ ! "$VERSION" =~ $VERSION_REGEX ]]; then
    echo "Normal release versions look like 1.0.13"
    read -e -p "Are you sure '$VERSION' is what you want? (y/n):" REPLY

    if [[ "$REPLY" != "y" ]]; then
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
