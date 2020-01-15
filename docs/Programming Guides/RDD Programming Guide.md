# RDD Programming Guidev


## Overview


## Initializing Spark

The first thing a Spark program must do is to create a SparkContext object, which tells Spark how to access a cluster. To create a SparkContext you first need to build a SparkConf object that contains information about your application.

Only one SparkContext may be active per JVM. You must stop() the active SparkContext before creating a new one.
~~~
val conf = new SparkConf().setAppName(appName).setMaster(master)
new SparkContext(conf)
~~~
The appName parameter is a name for your application to show on the cluster UI. master is a [Spark, Mesos or YARN cluster URL](http://spark.apache.org/docs/latest/submitting-applications.html#master-urls),
or a special “local” string to run in local mode.In practice, when running on a cluster, you will not want to hardcode master in the program, 
but rather [launch the application with spark-submit](http://spark.apache.org/docs/latest/submitting-applications.html) and receive it there. 
However, for local testing and unit tests, you can pass “local” to run Spark in-process.


## Using the Shell
```
$ ./bin/spark-shell --master local[4]
```
```
$ ./bin/spark-shell --master local[4] --jars code.jar
```
```
$ ./bin/spark-shell --master local[4] --packages "org.example:example:0.1"
```


## Resilient Distributed Datasets (RDDs)
Spark revolves around the concept of a resilient distributed dataset (RDD), which is a fault-tolerant collection of elements that can be operated on in parallel. 
There are two ways to create RDDs: 
parallelizing an existing collection in your driver program, or referencing a dataset in an external storage system, such as a shared filesystem, HDFS, HBase, or any data source offering a Hadoop InputFormat.


### Parallelized Collections
Parallelized collections are created by calling SparkContext’s parallelize method on an existing collection in your driver program (a Scala Seq). 
The elements of the collection are copied to form a distributed dataset that can be operated on in parallel. For example, here is how to create a parallelized collection holding the numbers 1 to 5:
```
val data = Array(1, 2, 3, 4, 5)
val distData = sc.parallelize(data)
```



### External Datasets
```
    scala> val logData = spark.read.textFile("file:///home/cobub3/spark-2.1.1-bin-hadoop2.4/README.md").cache
    logData: org.apache.spark.sql.Dataset[String] = [value: string]
```
Some notes on reading files with Spark:


### RDD Operations


### Basics




### Passing Functions to Spark



### Understanding closures



### Example



### Local vs. cluster modes


### Printing elements of an RDD



### Working with Key-Value Pairs

### Transformations

### Actions

### Shuffle operations


### Background


### Performance Impact



## RDD Persistence


### Which Storage Level to Choose?

### Removing Data



## Shared Variables

### Broadcast Variables


### Accumulators

## Deploying to a Cluster


## Launching Spark jobs from Java / Scala

## Unit Testing

## Where to Go from Here
