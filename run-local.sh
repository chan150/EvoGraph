# Set your environment
NUMCORE_MAC=`sysctl hw.ncpu | awk '{print $2}' 2>/dev/null`
NUMCORE_LINUX=`grep -c processor /proc/cpuinfo 2>/dev/null`
NUMCORE=`echo $NUMCORE_MAC $NUMCORE_LINUX | awk '{print $1}'`
MASTER=local[$NUMCORE]
SPARK_HOME=spark-2.3.1
HDFS_HOME=./

$SPARK_HOME/bin/spark-submit --master $MASTER --class kr.acon.EvoGraph --jars lib/fastutil-8.1.1.jar,lib/dsiutils-2.4.2.jar EvoGraph.jar -gs.input toy -format tsv -hdfs $HDFS_HOME $@