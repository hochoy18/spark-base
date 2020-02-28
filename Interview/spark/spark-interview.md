
### spark 在处理数据时，处理出现内存溢出的方法有哪些

### Spark如何调优 

### 简述Spark集群运行的几种模式 

### Spark Task 运行原理

### 讲讲Spark的stage 

### spark mkrdd和Parrallilaze函数区别 

### Spark checkpoint 过程 


### Spark优化方法举例 


### 哪些 Hive sql 不能在 Spark sql 上运行，看这里：https://spark.apache.org/docs/2.2.0/sql-programming-guide.html#unsupported-hive-functionality 


### Spark静态内存和动态内存 


### spark sql 的执行过程 



### Spark：原理、部署、优化 

### spark的函数式编程 


### Spark映射，RDD

### Spark 优化方法举例 

### spark oom处理方法 

### Spark task原理 

### 状态管理

   1. updateStateByKey 不适合大数据量下 的计算，可以采用Redis 或者alluxio，
   2. key超时，如何清空，来节约内存
        
   3. 初始状态

对于状态的算子一定要开启checkpoint，即指定checkpoint 目录即可，
```
ssc.checkpoint("/opt/checkpoint")
```
checkpoint 的频率
5 - 10 个滑动窗口，进行一次checkpoint，


