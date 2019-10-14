# [Spark](http://spark.apache.org,"spark")

## Spark Overview





## Programming Guides
### [Quick Start](http://spark.apache.org/docs/2.3.3/quick-start.html  "quick-start.html")
### [RDD Programming Guide](http://spark.apache.org/docs/2.3.3/rdd-programming-guide.html "RDD Programming Guide")
### [Spark SQL, DataFrames and Datasets Guide](http://spark.apache.org/docs/2.3.3/sql-programming-guide.html "Spark SQL, DataFrames and Datasets Guide")
### [Spark Streaming Programming Guide](http://spark.apache.org/docs/2.3.3/streaming-programming-guide.html "Spark Streaming Programming Guide")






## Deploying
###[Cluster Mode Overview](http://spark.apache.org/docs/2.3.3/cluster-overview.html "Cluster Mode Overview")
###[Submitting Applications](http://spark.apache.org/docs/2.3.3/submitting-applications.html  "Submitting Applications")
###[Spark Standalone Mode](http://spark.apache.org/docs/2.3.3/spark-standalone.html "Spark Standalone Mode")
###[Running Spark on YARN](http://spark.apache.org/docs/2.3.3/running-on-yarn.html  "Running Spark on YARN")









## More
### [Spark Configuration]( http://spark.apache.org/docs/2.3.3/configuration.html "Spark Configuration")
#### Spark Properties
##### [Dynamically Loading Spark Properties](http://spark.apache.org/docs/2.3.3/configuration.html#dynamically-loading-spark-properties "Dynamically Loading Spark Properties")
```
spark 配置优先级：
    SparkConf >  spark-submit /spark-shell > spark-defaults.conf
    
spark 配置的两种类型：
    1.部署相关：只能通过  spark-submit 等命令行设置 .e.g: “spark.driver.memory”, “spark.executor.instances”
    2.运行时控制相关：可以通过  spark-submit 或者 SparkConf. e.g: “spark.task.maxFailures”
```


##### [Viewing Spark Properties](http://spark.apache.org/docs/2.3.3/configuration.html#viewing-spark-properties "Viewing Spark Properties")


#####[Available Properties](http://spark.apache.org/docs/2.3.3/configuration.html#available-properties "Available Properties")
###### [Application Properties](http://spark.apache.org/docs/2.3.3/configuration.html#viewing-spark-properties "Application Properties")
```
    spark.app.name 
    spark.driver.cores
    spark.driver.memory
    spark.executor.memory
    spark.master
    spark.submit.deployMode
    spark.driver.supervise
```
###### [Runtime Environment]( http://spark.apache.org/docs/2.3.3/configuration.html#runtime-environment "Runtime Environment")
```
    spark.driver.extraJavaOptions
    spark.driver.userClassPathFirst
    spark.executor.extraClassPath
    spark.files
    spark.jars
    spark.jars.packages
    spark.jars.excludes
    
```

###### [Shuffle Behavior](http://spark.apache.org/docs/2.3.3/configuration.html#shuffle-behavior "Shuffle Behavior")
```
    spark.shuffle.compress
    spark.shuffle.file.buffer
    spark.shuffle.service.enabled
    
```
###### [Compression and Serialization](http://spark.apache.org/docs/2.3.3/configuration.html#compression-and-serialization "Compression and Serialization")
```
    spark.broadcast.compress
    spark.io.compression.codec
    spark.rdd.compress
    spark.serializer
    spark.serializer.objectStreamReset
```
###### [Memory Management](http://spark.apache.org/docs/2.3.3/configuration.html#memory-management "Memory Management")








### [Monitoring and Instrumentation]( http://spark.apache.org/docs/2.3.3/monitoring.html "Monitoring and Instrumentation")

### [Tuning Spark]( http://spark.apache.org/docs/2.3.3/tuning.html "Tuning Spark")
#### [Data Serialization](http://spark.apache.org/docs/2.3.3/tuning.html#data-serialization "Data Serialization")

#### [Memory Tuning](http://spark.apache.org/docs/2.3.3/tuning.html#memory-tuning "Memory Tuning")

#### [Other Considerations](http://spark.apache.org/docs/2.3.3/tuning.html#other-considerations "Other Considerations")

### [Job Scheduling](http://spark.apache.org/docs/2.3.3/job-scheduling.html "Job Scheduling")
### [Hardware Provisioning](http://spark.apache.org/docs/2.3.3/hardware-provisioning.html "Hardware Provisioning")







