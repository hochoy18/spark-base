### Executor 源码解析


#### Executor 是干嘛的
处理任务的执行器，是一个jvm进程 ，是一个以线程池实现的运行Task的进程。
官网介绍： 
>A process launched for an application on a worker node, that runs tasks and keeps data in memory or disk storage across them. Each application has its own executors.

#### Executor


##### 主要调用栈
 1.CoarseGrainedExecutorBackend 
    CoarseGrainedExecutorBackend#receive() :接受来自 的 LaunchTask 消息时触发，
    
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
2. LocalEndpoint
    LocalEndpoint#receive
    
```
    case KillTask(taskId, interruptThread, reason) =>
         executor.killTask(taskId, interruptThread, reason)
```

    LocalEndpoint#receiveAndReply

```
  override def receiveAndReply(context: RpcCallContext): PartialFunction[Any, Unit] = {
    case StopExecutor =>
      executor.stop()
      context.reply(true)
  }
```
    
    LocalEndpoint#reviveOffers
    
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
- launchTask ：启动Task

- killTask

- startDriverHeartbeater

- reportHeartBeat

- computeTotalGcTime

##### 成员变量说明

- threadPool : 使用 Executors.newCachedThreadPool 方式创建的 ThreadPoolExecutor，用此线程池运行的线程将以"Executor task launch worker"为前缀

- taskReaperPool：使用Executors.newCachedThreadPool方式创建的ThreadPoolExecutor，此线程池执行的线程用于监督Task的kill和取消。

- runningTasks : 用于维护正在运行的Task的身份标识与TaskRunner之间的映射关系。

- heartbeater : 只有一个线程的ScheduledThreadPoolExecutor，此线程池运行的线程以driver-heartbeater作为名称。

- heartbeatReceiverRef : HeartbeatReceiver 的 RpcEndpointRef，通过调用RpcEnv的setupEndpointRef方法（见代码清单5-42）获得。


- executorId：当前Executor的身份标识

##### 内部类说明： 
- TaskRunner 

- TaskReaper