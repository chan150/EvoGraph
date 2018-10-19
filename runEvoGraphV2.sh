#!/usr/bin/env bash
which spark-submit > /dev/null
if test $? -ne 0 ; then
    echo "[ERROR] Spark must be set in your environment (spark-submit must be accessible)"
    exit -1
fi

which hadoop > /dev/null
if test $? -ne 0 ; then
    echo "[ERROR] Hadoop must be set in your environment (in order to use HDFS)"
    exit -1
fi

MASTER=`hostname -f`
PORT=7077
HDFS_HOME=`hadoop fs -df | grep hdfs | awk '{print $1}'`/user/$USER/

# Set your environment
NUMCORE=120

spark-submit --master spark://$MASTER:$PORT --class kr.acon.ApplicationMain \
 --conf spark.network.timeout=20000000ms \
 --conf spark.hadoop.dfs.replication=1 \
 --conf spark.hadoop.mapreduce.fileoutputcommitter.algorithm.version=2 \
 --jars `ls lib/* | xargs echo | tr ' ' ,` \
 EvoGraph.jar EvoGraphV2 -gs.input toy -gs.sf 2 -format tsv -machine $NUMCORE -hdfs $HDFS_HOME $@