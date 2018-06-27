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

# Set your environment
MASTER=`hostname -f`
PORT=7077
HDFS_HOME=`hadoop fs -df | grep hdfs | awk '{print $1}'`/user/$USER/

spark-submit --master spark://$MASTER:$PORT --class kr.acon.EvoGraph \
 --conf spark.network.timeout=20000000ms \
 --conf spark.hadoop.dfs.replication=1 \
 --conf spark.driver.userClassPathFirst=true \
 --conf spark.executor.userClassPathFirst=true \
 --conf spark.hadoop.mapreduce.fileoutputcommitter.algorithm.version=2 \
 --jars lib/fastutil-8.1.1.jar,lib/dsiutils-2.4.2.jar EvoGraph.jar \
 -gs.input toy -format tsv -hdfs $HDFS_HOME $@
