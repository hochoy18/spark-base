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

Spark SQL 的原理和运行机制

Catalyst 的整体架构

Spark SQL 的 DataFrame


Spark SQL 的优化策略：内存列式存储和内存缓存表、列存储压缩、逻辑查询优化、Join 的优化


Spark的UDF

### Spark：原理、部署、优化 

### spark的函数式编程 


### Spark映射，RDD

### Spark 优化方法举例 

### spark oom处理方法 

### Spark task原理 

### [spark 数据倾斜](https://mp.weixin.qq.com/s/lqMu6lfk-Ny1ZHYruEeBdA)
https://www.cnblogs.com/vivotech/p/12106029.html


### Spark数据本地化

### [Spark内存调优](https://www.cnblogs.com/frankdeng/p/9301783.html)
https://blog.51cto.com/14048416/2338730
[Apache Spark 统一内存管理模型详解](https://www.iteblog.com/archives/2342.html)
spark.memory.offHeap.size

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





### [Spark 中 reparation 和 coalesce 的用法和区别](https://mp.weixin.qq.com/s?__biz=MzA3MDY0NTMxOQ==&mid=2247487893&idx=1&sn=a004ecea5a0a4e0c8b8bdb0714d1e0c3&chksm=9f38f2bda84f7bab14ee8ceb8d52ea242084397abf652d42c0d9ae7419532876ecfb8cabd02a&scene=126&sessionid=1585881131&key=3ad268967871374a25fe0814da992c03d4d978ebb05395660a0ca0535ebdc69354dac047a9f90a0b34e17f970518c6fb968f6859339a04861f5cb6fb790b3579b05ebbe5efb44bbf9fb9f4022419e70b&ascene=1&uin=MjM4MjczMTEwOA%3D%3D&devicetype=Windows+10&version=62080079&lang=en&exportkey=A65pyzU4mQrz3G2WbgIo9xE%3D&pass_ticket=oKNsyt75%2BtncoPHe0vyVGrkjqohk3yaYxaM1j4z5a9%2BPuSf68KUQ4g2uf4ESA92r)
- 源码可以看出两者的区别：coalesce()方法的参数**shuffle默认设置为false**，repartition()方法就是coalesce()方法**shuffle为true**的情况。
- 使用情景:假设RDD有N个分区，需要重新划分成M个分区：
   - **N < M(增加分区数)**: 一般情况下N个分区有数据分布不均匀的状况，利用HashPartitioner函数将数据重新分区为M个，这时需要将**shuffle设置为true**。因为**重分区前后相当于宽依赖**，
   会发生shuffle过程，此时可以使用 ``` coalesce(num, shuffle=true) ```，或者直接使用```repartition(num)```。
   - **N > M并且N和M相差不多**(假如N是1000，M是100): 那么就可以将N个分区中的若干个分区合并成一个新的分区，最终合并为M个分区，这是**前后是窄依赖关系**，可以使用```coalesce(num,shuffle=false)```。
   - **N> M并且两者相差悬殊**: 这时如果将shuffle设置为false，父子ＲＤＤ是窄依赖关系，他们同处在一个Stage中，就可能造成spark程序的并行度不够，
   从而影响性能，如果在M为1的时候，为了使coalesce之前的操作有更好的并行度，可以将shuffle设置为true。



[剑谱总纲 | 大数据方向学习面试知识图谱](https://mp.weixin.qq.com/s/mi7ZhIpbgqGi9yu0_nuVTA)
[独孤九剑-Spark面试80连击(上)](https://mp.weixin.qq.com/s/i1ZkCbhUM7Dcygvn2CrTSw)
[独孤九剑-Spark面试80连击(下)](https://mp.weixin.qq.com/s/5YhDK0T3JUHySVCW13bv2Q)
[Flink面试通关手册](https://mp.weixin.qq.com/s/xRqrojjFITuhswtjNJo7OQ)
