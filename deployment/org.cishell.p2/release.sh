#!/bin/bash
set -euvx

USAGE="Usage: $0 release_version"
if [[ $# != 1 ]]; then 
    echo "$USAGE"
    exit 1
fi

VERSION=$1

SOURCE="deployment/org.cishell.p2/target/repository"
DEST_BASE="/projects/cishell/www/htdocs/p2"
DEST="$DEST_BASE/$VERSION/"

VERSION_REGEX="^[0-9]+\.[0-9]+\.[0-9]+$"
if [[ ! "$VERSION" =~ $VERSION_REGEX ]]; then
    if [[ "$VERSION" == "dev" ]]; then
    	if [ -d "$DEST" ]; then
    		echo "Version is dev: removing already-existing dev p2 repo"
    		rm -r "$DEST"
    	fi
    else
    	# exit codes: see http://tldp.org/LDP/abs/html/exitcodes.html#EXITCODESREF
    	echo "Fatal: Bad version number (not 0.0.0 or dev)"
    	exit 64
    fi
fi

echo "Going to copy $SOURCE to $DEST"

if [ -d "$DEST" ]; then
	echo "Fatal: $DEST already exists (and version is not dev)"
	exit 65
fi

mkdir -p "$DEST_BASE"
cp -R "$SOURCE" "$DEST"
echo "Copied the files from $SOURCE to $DEST"
