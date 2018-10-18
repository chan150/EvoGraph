#!/usr/bin/env bash
SPARK=spark-2.3.2

./compile.sh
if [ -d $SPARK ] ; then
    echo $SPARK is already installed
else
    wget https://archive.apache.org/dist/spark/$SPARK/$SPARK-bin-hadoop2.7.tgz
    tar xzf $SPARK-bin-hadoop2.7.tgz
    mv $SPARK-bin-hadoop2.7 $SPARK
fi