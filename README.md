# [Spark](http://spark.apache.org "spark")

## Spark Overview





## Programming Guides
### [Quick Start](http://spark.apache.org/docs/2.3.3/quick-start.html  "quick-start.html")
### [RDD Programming Guide](http://spark.apache.org/docs/2.3.3/rdd-programming-guide.html "RDD Programming Guide")
### [Spark SQL, DataFrames and Datasets Guide](http://spark.apache.org/docs/2.3.3/sql-programming-guide.html "Spark SQL, DataFrames and Datasets Guide")



### [Spark Streaming Programming Guide](http://spark.apache.org/docs/2.3.3/streaming-programming-guide.html)
#### [Overview](http://spark.apache.org/docs/2.3.3/streaming-programming-guide.html#overview)

<img src="http://spark.apache.org/docs/2.3.3/img/streaming-arch.png" width="70%" bgcolor="white">
<img src="http://spark.apache.org/docs/2.3.3/img/streaming-flow.png"  width="70%" bgcolor="white">

#### [A Quick Example](http://spark.apache.org/docs/2.3.3/streaming-programming-guide.html#a-quick-example)

#### [Basic Concepts](http://spark.apache.org/docs/2.3.3/streaming-programming-guide.html#basic-concepts)
##### [Linking](http://spark.apache.org/docs/2.3.3/streaming-programming-guide.html#linking)
##### [Initializing StreamingContext](http://spark.apache.org/docs/2.3.3/streaming-programming-guide.html#initializing-streamingcontext)

***StreamingContext 常用的创建方式***
```
 new StreamingContext(new SparkContext(),Seconds(1))
 new StreamingContext(new SparkContext(), Durations.seconds(1))
 new StreamingContext(new SparkConf(),Seconds(10))
```
***要点***
- Once a context has been started, no new streaming computations can be set up or added to it.
- Once a context has been stopped, it cannot be restarted.
- Only one StreamingContext can be active in a JVM at the same time.
- stop() on StreamingContext also stops the SparkContext. To stop only the StreamingContext, set the optional parameter of stop() called stopSparkContext to false.
- A SparkContext can be re-used to create multiple StreamingContexts, as long as the previous StreamingContext is stopped (without stopping the SparkContext) before the next StreamingContext is created.

##### [Discretized Streams (DStreams)](http://spark.apache.org/docs/2.3.3/streaming-programming-guide.html#discretized-streams-dstreams)


## Deploying
###[Cluster Mode Overview](http://spark.apache.org/docs/2.3.3/cluster-overview.html)
本文档简要概述了Spark如何在群集上运行，以使您更容易理解所涉及的组件。 通读[应用程序提交指南](http://spark.apache.org/docs/2.3.3/submitting-applications.html)，
可以了解有关在集群上启动应用程序的信息。

### [Components](http://spark.apache.org/docs/2.3.3/cluster-overview.html#components)


###[Submitting Applications](http://spark.apache.org/docs/2.3.3/submitting-applications.html)

###[Spark Standalone Mode](http://spark.apache.org/docs/2.3.3/spark-standalone.html "Spark Standalone Mode")

###[Running Spark on YARN](http://spark.apache.org/docs/2.3.3/running-on-yarn.html)
#### [Launching Spark on YARN](http://spark.apache.org/docs/2.3.3/running-on-yarn.html#launching-spark-on-yarn)
Ensure that HADOOP_CONF_DIR or YARN_CONF_DIR points to the directory which contains the (client side) configuration files for the Hadoop cluster. 
***These configs are used to write to HDFS and connect to the YARN ResourceManager. The configuration contained in this directory will be 
distributed to the YARN cluster so that all containers used by the application use the same configuration.*** 







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
```
    Java serialization :
    Kryo serialization :  
        conf.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer") 
        conf.set("spark.serializer", classOf[KryoSerializer].getName)
```

#### [Memory Tuning](http://spark.apache.org/docs/2.3.3/tuning.html#memory-tuning)
>[Spark调优秘诀](http://www.mamicode.com/info-detail-2164217.html)  

***内存调优需要考虑的三个方面：***
* 对象使用的内存大小
* 访问对象的内存开销
* 垃圾回收的开销 

***java对象的访问快速但是比其他领域的原始数据消耗的空间大 2 ~ 5倍，原因如下：*** 
- java对象的对象头（object header）：其大小大约16B，包含指针等的信息
- java字符串：由于字符串是由字符（char)构成的数组，它需要保存一些诸如长度length之类的数据，由于String内部使用UTF-16编码，因此Java字符串将每个字符存储为两个字节。
- 常用集合类：像HashMap 、 LinkedList这些用链接数据结构包装每一个实体（entry)的类，这个集合对象不止有一个对象头，集合中的每个实体（entry）对象还有指向下一个对象的指针（大小通常为 8 Byte）
- 以封装（boxed）对象的形式存储java.lang.Integer 等基本数据类型的集合

##### [Memory Management Overview](http://spark.apache.org/docs/2.3.3/tuning.html#memory-management-overview )
***
***spark 内存方面的使用大部分都归属于以下两类：执行内存和存储内存（execution and storage）：***
+ 执行内存是指用作shuffle，join，sort，aggregate 等计算的内存，而存储内存是指内部数据在集群间的缓存和传输。
+ spark中，执行内存和存储内存共享统一的数据区域（M）。 execution memory 不在使用时，storage memory 可以使用所有的可用内存,反之亦然。
+ 在必要的情况下，execution memory 可以驱逐。但只在总存储内存（total storage memory）低于某一特定的阈值（R）。
    换句话说，在高速缓存块（cached blocks）未被逐出时，R 是 M 以内的子区域。但是，由于实施的复杂性，storage memory 却无法驱逐 execution memory。
***
***spark的这种内存设计保证了其以下几种特性：***
+ 对于不使用缓存的 application ，可以将整个内存空间用于execution（计算等），这也避免了不必要的磁盘溢出。
+ 那些使用缓存的 application 在其数据块不被驱逐前提下可以保留最小的存储空间（R）。
+ 这种方法提供出的现成合理的性能，适用于各种工作负载，而无需对如何存储在内部的划分的用户专业知识。

***
虽然有两个相关的配置，普通用户不应该需要调整它们的默认值适用于大多数工作负载
   + [spark.memory.fraction](http://spark.apache.org/docs/2.3.3/configuration.html#memory-management)
      + 用于执行和存储的比例，该值越低，溢出和缓存数据清理越频繁
   + [spark.memory.storageFraction](http://spark.apache.org/docs/2.3.3/configuration.html#memory-management)


##### [Determining Memory Consumption](http://spark.apache.org/docs/2.3.3/tuning.html#determining-memory-consumption)
确定一个数据集（dataset）需要消耗多少内存的最佳方法是：创建一个RDD，并将它缓存，然后在web UI界面查看 Storage 页面。该页面将告诉您RDD占用了多少内存。
使用 SizeEstimator#estimate()方法可以估算特定对象的内存消耗。


##### [Tuning Data Structures](http://spark.apache.org/docs/2.3.3/tuning.html#tuning-data-structures "Tuning Data Structures")
- 数据结构尽可能的使用对象的数组，基本数据类型，而尽可能少的使用诸如HashMap等的标准的Java或者Scala集合类。
[fastutil](http://fastutil.di.unimi.it) 库提供方便的集合类基本类型是与Java标准库兼容。
- 尽可能避免使用小对象、指针的嵌套结构
- 使用数字类型的ID或枚举对象，而不是字符串。
- 如果您的 RAM 少于32 GB，设置JVM 参数 -XX：+ UseCompressedOops 使指针是四个字节而不是8。您可以在spark-env.sh添加这些选项。


##### [Serialized RDD Storage](http://spark.apache.org/docs/2.3.3/tuning.html#serialized-rdd-storage "Serialized RDD Storage")
##### [Garbage Collection Tuning](http://spark.apache.org/docs/2.3.3/tuning.html#garbage-collection-tuning "Garbage Collection Tuning")




#### [Other Considerations](http://spark.apache.org/docs/2.3.3/tuning.html#other-considerations "Other Considerations")

##### [Level of Parallelism](http://spark.apache.org/docs/2.3.3/tuning.html#level-of-parallelism "Level of Parallelism")
##### [Memory Usage of Reduce Tasks](http://spark.apache.org/docs/2.3.3/tuning.html#memory-usage-of-reduce-tasks "Memory Usage of Reduce Tasks")
##### [Broadcasting Large Variables](http://spark.apache.org/docs/2.3.3/tuning.html#broadcasting-large-variables "Broadcasting Large Variables")
##### [Data Locality](http://spark.apache.org/docs/2.3.3/tuning.html#data-locality "Data Locality")



### [Job Scheduling](http://spark.apache.org/docs/2.3.3/job-scheduling.html "Job Scheduling")
### [Hardware Provisioning](http://spark.apache.org/docs/2.3.3/hardware-provisioning.html "Hardware Provisioning")







