#!/bin/bash
set -e

JAVA_HOME=$(/usr/libexec/java_home -v 17)
export JAVA_HOME
export PATH="$JAVA_HOME/bin:$PATH"

echo "JAVA_HOME set for build phase:"
echo "$JAVA_HOME"
java -version
