#!/bin/bash
set -eu

USAGE="Usage: $0 release_version"
if [[ $# != 1 ]]; then 
    echo "$USAGE"
    exit 1
fi

VERSION=$1

SOURCE="deployment/org.cishell.p2/target/repository/*"
DEST_BASE="/projects/cishell/www/htdocs/p2"
DEST="$DEST_BASE/$VERSION/"

VERSION_REGEX="^[0-9]+\.[0-9]+\.[0-9]+$"
if [[ ! "$VERSION" =~ $VERSION_REGEX ]]; then
    if [[ "$VERSION" == "dev" ]]; then
    	if [ -d "$DEST" ]; then 
    		rm -r "$DEST"
    	fi
    else
    	exit 1
    fi
fi

echo "Going to copy $SOURCE to $DEST"

mkdir -p "$DEST_BASE"
mkdir "$DEST"
cp -R "$SOURCE" -t "$DEST"
echo "Copied the files from $SOURCE to $DEST"
