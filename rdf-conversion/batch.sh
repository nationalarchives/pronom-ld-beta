#!/usr/bin/env bash

SCRIPTS=${1:-r2rml/*.ttl}
R2RML=r2rml-f/r2rml.jar
OUT=out
CONNECTION_URL="jdbc:sqlserver://dev.verinote.net:10433;user=sa;"
PASSWORD=$PASSWORD

mkdir -p $OUT

for file in $SCRIPTS; do if [ -f "$file" ] ; then
    echo "Processing $file"
    java -jar $R2RML -m $file -o "$OUT/$(basename $file)" -c $CONNECTION_URL -p $PASSWORD
fi;
done