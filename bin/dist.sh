#!/bin/sh

# Creates a schell script with an attached binary
# @see: https://coderwall.com/p/ssuaxa

TARGET=../target
OF=${TARGET}/props

cat props.sh ${TARGET}/java-props-*.jar > ${OF}
chmod +x ${OF}
echo "New executable at $OF"
