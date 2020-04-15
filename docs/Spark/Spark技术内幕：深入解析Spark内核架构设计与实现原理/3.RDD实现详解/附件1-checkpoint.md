
## Spark checkpoint 详述

 *本文基于 spark2.4.4 版本进行分析，如有刊误望告之，求指教*

### 使用
 
 
 - step1 设置checkpoint 目录  
```
sc.setCheckpointDir("hdfs://hadoop01:8020/tmp/hc")
```
 
- step2 调用 RDD#checkpoint()方法  
```
  val rdd1 = sc.parallelize(score,2).groupByKey().map(e=>(e._1,e._2.sum / e._2.size))  
  rdd1.checkpoint()
```
 
- step3 触发action
```
 rdd1.foreach(println)
```
 
 
 ### 原理
 触发 action 时会调用 rdd.doCheckpoint() 函数
 ```
  /**
   * Run a function on a given set of partitions in an RDD and pass the results to the given
   * handler function. This is the main entry point for all actions in Spark.
   *
   * @param rdd target RDD to run tasks on
   * @param func a function to run on each partition of the RDD
   * @param partitions set of partitions to run on; some jobs may not want to compute on all
   * partitions of the target RDD, e.g. for operations like `first()`
   * @param resultHandler callback to pass each result to
   */
  def runJob[T, U: ClassTag](
      rdd: RDD[T],
      func: (TaskContext, Iterator[T]) => U,
      partitions: Seq[Int],
      resultHandler: (Int, U) => Unit): Unit = {
    if (stopped.get()) {
      throw new IllegalStateException("SparkContext has been shutdown")
    }
    val callSite = getCallSite
    val cleanedFunc = clean(func)
    logInfo("Starting job: " + callSite.shortForm)
    if (conf.getBoolean("spark.logLineage", false)) {
      logInfo("RDD's recursive dependencies:\n" + rdd.toDebugString)
    }
    dagScheduler.runJob(rdd, cleanedFunc, partitions, callSite, resultHandler, localProperties.get)
    progressBar.foreach(_.finishAll())
    rdd.doCheckpoint()
  }

```

主要看 RDD#doCheckpoint() 中的  checkpointData.get.checkpoint() ,  checkpointData.get 返回一个 RDDCheckpointData对象，该对象有两个子类 LocalRDDCheckpointData 和  ReliableRDDCheckpointData  

再来看一下 RDDCheckpointData 类 及其两个子类：LocalRDDCheckpointData 和  ReliableRDDCheckpointData


#### RDDCheckpointData

RDDCheckpointData：此类包含与RDD检查点相关的所有信息。 这个类的每个实例都与一个RDD相关联。 它管理相关RDD的检查点过程，并通过提供更新的分区来管理检查点后的状态，检查点RDD的迭代器和首选位置。  
通俗的说就是保存 要 checkpoint 的RDD 的checkpoint 元数据信息  
其主要属性和方法如下：  
cpState ：关联RDD的checkpoint 状态  
cpRDD ：显然，这个就是被 Checkpoint 的 RDD 的数据  
checkpoint()：物化RDD 以及持久化RDD的内容，并且在RDD 首次触发action 之后就会调用。checkpoint完成的最后，标记cpStatus为Checkpointed，并在markCheckpointed中清除依赖关系  
```
  final def checkpoint(): Unit = {
  
    ......
  
    // Update our state and truncate the RDD lineage
    RDDCheckpointData.synchronized {
      cpRDD = Some(newRDD)
      cpState = Checkpointed
      rdd.markCheckpointed()
    }
  }
```

doCheckpoint(): CheckpointRDD[T] ：物化RDD 和持久化RDD 内容的具体实现

```
/**
 * This class contains all the information related to RDD checkpointing. Each instance of this
 * class is associated with an RDD. It manages process of checkpointing of the associated RDD,
 * as well as, manages the post-checkpoint state by providing the updated partitions,
 * iterator and preferred locations of the checkpointed RDD.
 */
private[spark] abstract class RDDCheckpointData[T: ClassTag](@transient private val rdd: RDD[T])
  extends Serializable {
    ...
    
      // The checkpoint state of the associated RDD.
      protected var cpState = Initialized
    
      // The RDD that contains our checkpointed data
      private var cpRDD: Option[CheckpointRDD[T]] = None
    
    ...
     
       /**
       
       
         /**
          * Materialize this RDD and persist its content.
          * This is called immediately after the first action invoked on this RDD has completed.
          */
         final def checkpoint(): Unit = {
           // Guard against multiple threads checkpointing the same RDD by
           // atomically flipping the state of this RDDCheckpointData
           RDDCheckpointData.synchronized {
             if (cpState == Initialized) {
               cpState = CheckpointingInProgress
             } else {
               return
             }
           }
       
           val newRDD = doCheckpoint()
       
           // Update our state and truncate the RDD lineage
           RDDCheckpointData.synchronized {
             cpRDD = Some(newRDD)
             cpState = Checkpointed
             rdd.markCheckpointed()
           }
         }
       
        * Materialize this RDD and persist its content.
        *
        * Subclasses should override this method to define custom checkpointing behavior.
        * @return the checkpoint RDD created in the process.
        */
       protected def doCheckpoint(): CheckpointRDD[T]
     
     ...
  }
```

markCheckpointed:清除依赖关系

```
  private[spark] def markCheckpointed(): Unit = {
    clearDependencies()
    partitions_ = null
    deps = null    // Forget the constructor argument for dependencies too
  }
```



#### LocalRDDCheckpointData


- LocalRDDCheckpointData：
   - 首先要求必须支持disk存储（其实在 RDDlocalCheckpoint() 就已经被强制设置成disk了）
   - 其次囿于某些action的限制，例如take等，并没有触发所有分区的转换。这样对于那些未经计算的RDD分区需要重新生成。（待定TODO）
   最终交付的是一个根据该RDD新生成的 LocalCheckpointRDD。


- 触发：

```

    sc.setCheckpointDir("hdfs://hadoop01:8020/tmp/hochoy")
    val rdd1 = sc.parallelize(score,2).groupByKey()
    rdd1.localCheckpoint()
    rdd1.foreach(println)

```


- LocalRDDCheckpointData#doCheckpoint()：没有写到 文件系统的 操作  

```

  /**
   * Ensure the RDD is fully cached so the partitions can be recovered later.
   */
  protected override def doCheckpoint(): CheckpointRDD[T] = {
    val level = rdd.getStorageLevel

    // Assume storage level uses disk; otherwise memory eviction may cause data loss
    assume(level.useDisk, s"Storage level $level is not appropriate for local checkpointing")

    // Not all actions compute all partitions of the RDD (e.g. take). For correctness, we
    // must cache any missing partitions. TODO: avoid running another job here (SPARK-8582).
    val action = (tc: TaskContext, iterator: Iterator[T]) => Utils.getIteratorSize(iterator)
    val missingPartitionIndices = rdd.partitions.map(_.index).filter { i =>
      !SparkEnv.get.blockManager.master.contains(RDDBlockId(rdd.id, i))
    }
    if (missingPartitionIndices.nonEmpty) {
      rdd.sparkContext.runJob(rdd, action, missingPartitionIndices)
    }

    new LocalCheckpointRDD[T](rdd)
  }



```

- RDD#localCheckpoint() 代码  
其中 有 persist 的操作

```

  /**
   * Mark this RDD for local checkpointing using Spark's existing caching layer.
   *
   * This method is for users who wish to truncate RDD lineages while skipping the expensive
   * step of replicating the materialized data in a reliable distributed file system. This is
   * useful for RDDs with long lineages that need to be truncated periodically (e.g. GraphX).
   *
   * Local checkpointing sacrifices fault-tolerance for performance. In particular, checkpointed
   * data is written to ephemeral local storage in the executors instead of to a reliable,
   * fault-tolerant storage. The effect is that if an executor fails during the computation,
   * the checkpointed data may no longer be accessible, causing an irrecoverable job failure.
   *
   * This is NOT safe to use with dynamic allocation, which removes executors along
   * with their cached blocks. If you must use both features, you are advised to set
   * `spark.dynamicAllocation.cachedExecutorIdleTimeout` to a high value.
   *
   * The checkpoint directory set through `SparkContext#setCheckpointDir` is not used.
   */
  def localCheckpoint(): this.type = RDDCheckpointData.synchronized {
    if (conf.getBoolean("spark.dynamicAllocation.enabled", false) &&
        conf.contains("spark.dynamicAllocation.cachedExecutorIdleTimeout")) {
      logWarning("Local checkpointing is NOT safe to use with dynamic allocation, " +
        "which removes executors along with their cached blocks. If you must use both " +
        "features, you are advised to set `spark.dynamicAllocation.cachedExecutorIdleTimeout` " +
        "to a high value. E.g. If you plan to use the RDD for 1 hour, set the timeout to " +
        "at least 1 hour.")
    }

    // Note: At this point we do not actually know whether the user will call persist() on
    // this RDD later, so we must explicitly call it here ourselves to ensure the cached
    // blocks are registered for cleanup later in the SparkContext.
    //
    // If, however, the user has already called persist() on this RDD, then we must adapt
    // the storage level he/she specified to one that is appropriate for local checkpointing
    // (i.e. uses disk) to guarantee correctness.

    if (storageLevel == StorageLevel.NONE) {
      persist(LocalRDDCheckpointData.DEFAULT_STORAGE_LEVEL)
    } else {
      persist(LocalRDDCheckpointData.transformStorageLevel(storageLevel), allowOverride = true)
    }

    // If this RDD is already checkpointed and materialized, its lineage is already truncated.
    // We must not override our `checkpointData` in this case because it is needed to recover
    // the checkpointed data. If it is overridden, next time materializing on this RDD will
    // cause error.
    if (isCheckpointedAndMaterialized) {
      logWarning("Not marking RDD for local checkpoint because it was already " +
        "checkpointed and materialized")
    } else {
      // Lineage is not truncated yet, so just override any existing checkpoint data with ours
      checkpointData match {
        case Some(_: ReliableRDDCheckpointData[_]) => logWarning(
          "RDD was already marked for reliable checkpointing: overriding with local checkpoint.")
        case _ =>
      }
      checkpointData = Some(new LocalRDDCheckpointData(this))
    }
    this
  }


```






#### ReliableRDDCheckpointData

ReliableRDDCheckpointData：就是把 RDD Checkpoint 到可依赖的文件系统，言下之意就是 Driver 重启的时候也可以从失败的时间点进行恢复，无需再走一次 RDD 的转换过程。


RDD#doCheckpoint()

```
  /**
   * Performs the checkpointing of this RDD by saving this. It is called after a job using this RDD
   * has completed (therefore the RDD has been materialized and potentially stored in memory).
   * doCheckpoint() is called recursively on the parent RDDs.
   */
  private[spark] def doCheckpoint(): Unit = {
    RDDOperationScope.withScope(sc, "checkpoint", allowNesting = false, ignoreParent = true) {
      if (!doCheckpointCalled) {
        doCheckpointCalled = true
        if (checkpointData.isDefined) {
          if (checkpointAllMarkedAncestors) {
            // TODO We can collect all the RDDs that needs to be checkpointed, and then checkpoint
            // them in parallel.
            // Checkpoint parents first because our lineage will be truncated after we
            // checkpoint ourselves
            dependencies.foreach(_.rdd.doCheckpoint())
          }
          checkpointData.get.checkpoint()
        } else {
          dependencies.foreach(_.rdd.doCheckpoint())
        }
      }
    }
  }
```

checkpointData.get.checkpoint()

```
  /**
   * Materialize this RDD and persist its content.
   * This is called immediately after the first action invoked on this RDD has completed.
   */
  final def checkpoint(): Unit = {
    // Guard against multiple threads checkpointing the same RDD by
    // atomically flipping the state of this RDDCheckpointData
    RDDCheckpointData.synchronized {
      if (cpState == Initialized) {
        cpState = CheckpointingInProgress
      } else {
        return
      }
    }

    val newRDD = doCheckpoint()

    // Update our state and truncate the RDD lineage
    RDDCheckpointData.synchronized {
      cpRDD = Some(newRDD)
      cpState = Checkpointed
      rdd.markCheckpointed()
    }
  }
```

在改方法中我们主要看  *val newRDD = doCheckpoint()*,以及当 RDDCheckpointData 是ReliableRDDCheckpointData时，该方法的实际实现，
方法中第一行及为将 rdd 以文件形式写入 checkpoint 目录。
ReliableCheckpointRDD.writeRDDToCheckpointDirectory() 方法的注释：
*Write RDD to checkpoint files and return a ReliableCheckpointRDD representing the RDD.*


ReliableRDDCheckpointData#doCheckpoint()  

```
  /**
   * Materialize this RDD and write its content to a reliable DFS.
   * This is called immediately after the first action invoked on this RDD has completed.
   */
  protected override def doCheckpoint(): CheckpointRDD[T] = {
    val newRDD = ReliableCheckpointRDD.writeRDDToCheckpointDirectory(rdd, cpDir)

    // Optionally clean our checkpoint files if the reference is out of scope
    if (rdd.conf.getBoolean("spark.cleaner.referenceTracking.cleanCheckpoints", false)) {
      rdd.context.cleaner.foreach { cleaner =>
        cleaner.registerRDDCheckpointDataForCleanup(newRDD, rdd.id)
      }
    }

    logInfo(s"Done checkpointing RDD ${rdd.id} to $cpDir, new parent is RDD ${newRDD.id}")
    newRDD
  }
```


#### 调用栈

以 take 算子为例(其他算子一样)：  

- ReliableRDDCheckpointData  的调用栈 

```
RDD#checkpoint()
    checkpointData = Some(new ReliableRDDCheckpointData(this))
RDD#runJob
	RDD#doCheckpoint()
		RDDCheckpointData#checkpoint()
			RDDCheckpointData#doCheckpoint()
				ReliableRDDCheckpointData#doCheckpoint()
					val newRDD = ReliableCheckpointRDD.writeRDDToCheckpointDirectory(rdd, cpDir)

```
			
	
	
	
- LocalRDDCheckpointData 的调用栈  

```
RDD#localCheckpoint()
	RDD#persist(LocalRDDCheckpointData.DEFAULT_STORAGE_LEVEL)
	checkpointData = Some(new LocalRDDCheckpointData(this))
RDD#runJob
	RDD#doCheckpoint()
		RDDCheckpointData#checkpoint()
			LocalRDDCheckpointData#doCheckpoint()
				new LocalCheckpointRDD[T](rdd)
   				
```



### 参考文献
- [1] [spark checkpoint流程分析](https://zhuanlan.zhihu.com/p/87115691)  
- [2] [Spark之localCheckpoint](https://zhuanlan.zhihu.com/p/87983748)  
- [3] [深入浅出Spark的Checkpoint机制](https://blog.csdn.net/m0_37803704/article/details/86243241?depth_1-utm_source=distribute.pc_relevant.none-task-blog-BlogCommendFromBaidu-3&utm_source=distribute.pc_relevant.none-task-blog-BlogCommendFromBaidu-3)
 