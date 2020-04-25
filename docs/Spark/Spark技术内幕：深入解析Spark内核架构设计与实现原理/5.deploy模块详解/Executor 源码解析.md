### Executor 源码解析


#### Executor 是干嘛的
处理任务的执行器，是一个JVM进程 ，是一个以线程池实现的运行 Task 的进程。看一下官网和代码注释对 Executor 的说明  

 - 官网介绍
>A process launched for an application on a worker node, that runs tasks and keeps data in memory or disk storage across them. Each application has its own executors.

 - Executor 代码注释：Spark执行器，由线程池支持以运行任务。可以与Mesos、YARN和 standalone 调度程序一起使用。内部RPC接口可以与驱动程序通信，但不支持 Mesos细粒度模式
>Spark executor, backed by a threadpool to run tasks.
This can be used with Mesos, YARN, and the standalone scheduler.
An internal RPC interface is used for communication with the driver,except in the case of Mesos fine-grained mode.

    	

#### Executor


##### 主要调用栈
 - CoarseGrainedExecutorBackend 
   - *CoarseGrainedExecutorBackend#receive()* :由 LaunchTask 类型的消息触发，以及 KillTask 类型的消息触发
        
    ```
      override def receive: PartialFunction[Any, Unit] = {
        case LaunchTask(data) =>
          if (executor == null) {
            exitExecutor(1, "Received LaunchTask command but executor was null")
          } else {
            val taskDesc = TaskDescription.decode(data.value)
            logInfo("Got assigned task " + taskDesc.taskId)
            executor.launchTask(this, taskDesc)
          }
    
        case KillTask(taskId, _, interruptThread, reason) =>
          if (executor == null) {
            exitExecutor(1, "Received KillTask command but executor was null")
          } else {
            executor.killTask(taskId, interruptThread, reason)
          }
      }
    ```

 - LocalEndpoint
   - *LocalEndpoint#receive* ： 由 KillTask 类型的消息触发  
   
    ```
        case KillTask(taskId, interruptThread, reason) =>
             executor.killTask(taskId, interruptThread, reason)
    ```
    
   - *LocalEndpoint#receiveAndReply*  : 由 StopExecutor 类型的消息触发
   
    ```
      override def receiveAndReply(context: RpcCallContext): PartialFunction[Any, Unit] = {
        case StopExecutor =>
          executor.stop()
          context.reply(true)
      }
    ```
    
   - *LocalEndpoint#reviveOffers*
    
    ```
      def reviveOffers() {
        val offers = IndexedSeq(new WorkerOffer(localExecutorId, localExecutorHostname, freeCores,
          Some(rpcEnv.address.hostPort)))
        for (task <- scheduler.resourceOffers(offers).flatten) {
          freeCores -= scheduler.CPUS_PER_TASK
          executor.launchTask(executorBackend, task)
        }
      }
    ```


##### 关键函数说明
- launchTask ：启动 Task  
    ```
      def launchTask(context: ExecutorBackend, taskDescription: TaskDescription): Unit = {
        val tr = new TaskRunner(context, taskDescription)
        runningTasks.put(taskDescription.taskId, tr)
        threadPool.execute(tr)
      }
    ```


- killTask

- startDriverHeartbeater :启动心跳,调用 reportHeartBeat() 函数， 在初始化的时候被调用
    ```
      /**
       * Schedules a task to report heartbeat and partial metrics for active tasks to driver.
       */
      private def startDriverHeartbeater(): Unit = {
        val intervalMs = HEARTBEAT_INTERVAL_MS
    
        // Wait a random interval so the heartbeats don't end up in sync
        val initialDelay = intervalMs + (math.random * intervalMs).asInstanceOf[Int]
    
        val heartbeatTask = new Runnable() {
          override def run(): Unit = Utils.logUncaughtExceptions(reportHeartBeat())
        }
        heartbeater.scheduleAtFixedRate(heartbeatTask, initialDelay, intervalMs, TimeUnit.MILLISECONDS)
      }
    
    ```

- reportHeartBeat
    ```
      /** Reports heartbeat and metrics for active tasks to the driver. */
      private def reportHeartBeat(): Unit = {
        // list of (task id, accumUpdates) to send back to the driver
        val accumUpdates = new ArrayBuffer[(Long, Seq[AccumulatorV2[_, _]])]()
        val curGCTime = computeTotalGcTime()
    
        for (taskRunner <- runningTasks.values().asScala) {
          if (taskRunner.task != null) {
            taskRunner.task.metrics.mergeShuffleReadMetrics()
            taskRunner.task.metrics.setJvmGCTime(curGCTime - taskRunner.startGCTime)
            accumUpdates += ((taskRunner.taskId, taskRunner.task.metrics.accumulators()))
          }
        }
    
        val message = Heartbeat(executorId, accumUpdates.toArray, env.blockManager.blockManagerId)
        try {
          val response = heartbeatReceiverRef.askSync[HeartbeatResponse](
              message, new RpcTimeout(HEARTBEAT_INTERVAL_MS.millis, EXECUTOR_HEARTBEAT_INTERVAL.key))
          if (response.reregisterBlockManager) {
            logInfo("Told to re-register on heartbeat")
            env.blockManager.reregister()
          }
          heartbeatFailures = 0
        } catch {
          case NonFatal(e) =>
            logWarning("Issue communicating with driver in heartbeater", e)
            heartbeatFailures += 1
            if (heartbeatFailures >= HEARTBEAT_MAX_FAILURES) {
              logError(s"Exit as unable to send heartbeats to driver " +
                s"more than $HEARTBEAT_MAX_FAILURES times")
              System.exit(ExecutorExitCode.HEARTBEAT_FAILURE)
            }
        }
      }
    
    ```


- computeTotalGcTime

##### 成员变量说明

- threadPool : 使用 ```Executors.newCachedThreadPool ```方式创建的 ThreadPoolExecutor，用此线程池运行以"Executor task launch worker"为前缀的 TaskRunner 线程

- taskReaperPool：使用 ```Executors.newCachedThreadPool``` 方式创建的 ThreadPoolExecutor，此线程池执行的线程用于监督 Task 的 kill 和 cancel 。

- runningTasks : 用于维护正在运行的Task的身份标识(taskId)与TaskRunner之间的映射关系。

- heartbeater : 只有一个线程的 ScheduledThreadPoolExecutor（线程池调度器），此线程池运行以```driver-heartbeater```作为名称的线程。该调度器 以 ```spark.executor.heartbeatInterval``` 
  毫秒的频率定时被调起 执行 ```reportHeartBeat() ```，将 active状态 task 的 心跳 (heartbeat)和 度量 ( metrics ) 向 driver 汇报

- heartbeatReceiverRef : HeartbeatReceiver 的 RpcEndpointRef，通过调用RpcEnv的setupEndpointRef方法获得。


- executorId：当前Executor的身份标识

##### 内部类说明： 
- TaskRunner 

- TaskReaper