SPARK=spark-2.3.1

./compile.sh
wget https://archive.apache.org/dist/spark/$SPARK/$SPARK-bin-hadoop2.7.tgz
tar xzf $SPARK-bin-hadoop2.7.tgz
mv $SPARK-bin-hadoop2.7 $SPARK

cp conf/log4j.properties $SPARK/conf
#echo >>$SPARK/conf/log4j.properties
#echo log4j.logger.org.apache.hadoop.util.NativeCodeLoader=ERROR >>$SPARK/conf/log4j.properties
#echo log4j.rootCategory=ERROR, console >>$SPARK/conf/log4j.properties
