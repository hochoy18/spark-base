[如果你在准备面试，好好看看这130道题](https://mp.weixin.qq.com/s?__biz=MzU3MzgwNTU2Mg==&mid=2247486986&idx=1&sn=422d1a3c11c72ff97b32cc01142839f4&chksm=fd3d489fca4ac1895242ab94b932b12c65dc57b5f3a16acc7084dc8a189e9026290245a64c4f&mpshare=1&scene=1&srcid=&sharer_sharetime=1579174252390&sharer_shareid=345c18b24b01f8311961001c70cf35b3&key=f91b344e81f23c9af14d8e7354de003ff6fbbd565bc3b0e5b3ba446506de018fb701383dd19de608dde4bef87e0e3a59e5754139013965febd896922ca71d6b5473514b5f5a7d5f0ad6d69edb624535d&ascene=1&uin=MjM4MjczMTEwOA%3D%3D&devicetype=Windows+10&version=62080079&lang=en&exportkey=Aw2kh%2FXGVBHi2iVJUqa2WIY%3D&pass_ticket=ZulPYShl4bJ10cURsZaoessTcFyWeQhM9e8i8cSX5tauZi%2BZCWWZJ3QGDMzQjtUN)

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






[剑谱总纲 | 大数据方向学习面试知识图谱](https://mp.weixin.qq.com/s/mi7ZhIpbgqGi9yu0_nuVTA)
[独孤九剑-Spark面试80连击(上)](https://mp.weixin.qq.com/s/i1ZkCbhUM7Dcygvn2CrTSw)
[独孤九剑-Spark面试80连击(下)](https://mp.weixin.qq.com/s/5YhDK0T3JUHySVCW13bv2Q)
[Flink面试通关手册](https://mp.weixin.qq.com/s/xRqrojjFITuhswtjNJo7OQ)
