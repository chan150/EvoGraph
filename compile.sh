#!/usr/bin/env bash

if [ -d ~/.sbt/0.13/staging/0886723d2637b5cba6f8/trilliong ] ; then
    cd ~/.sbt/0.13/staging/0886723d2637b5cba6f8/trilliong
    git reset --hard HEAD
    git pull
    ./compile.sh
    cd -
fi

sbt/bin/sbt package
cp target/scala*/*.jar EvoGraph.jar
cp ~/.sbt/0.13/staging/*/trilliong/TrillionG.jar lib/
