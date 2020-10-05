#!/usr/bin/env bash
set -e

.github/scripts/cs-setup.sh

mkdir -p bin
export PATH="$(pwd)/bin:$PATH"
echo "::add-path::$(pwd)/bin"

eval "$(cs java --jvm 8 --env)"
echo "::set-env name=JAVA_HOME::$JAVA_HOME"
echo "::add-path::$JAVA_HOME/bin"

./cs install --install-dir bin sbt-launcher:1.2.22

rm -f cs
