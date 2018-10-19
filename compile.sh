#!/usr/bin/env bash

if [ -d ~/.sbt/0.13/staging/*/trilliong ] ; then
    cd ~/.sbt/0.13/staging/*/trilliong
    git pull
    ./compile.sh
    cd -
fi

sbt/bin/sbt package
cp target/scala*/*.jar EvoGraph.jar
cp ~/.sbt/0.13/staging/*/trilliong/TrillionG.jar lib/
