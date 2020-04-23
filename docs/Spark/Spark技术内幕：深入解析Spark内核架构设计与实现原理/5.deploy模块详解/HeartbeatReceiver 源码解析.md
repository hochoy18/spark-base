### HeartbeatReceiver 源码解析

#### HeartbeatReceiver 是干嘛的
 Driver 中的心跳接收器，仅被 Driver（SparkContext）持有 (Lives in the driver to receive heartbeats from executors..) ,负责接收、处理各个Executor的心跳消息（HeartBeat），
 以及监控各个Executor的“生死”状态。
 

#### HeartbeatReceiver 


##### 接收和处理的 信息类型
- TaskSchedulerIsSet ：由 SparkContext 发送``` _heartbeatReceiver.ask[Boolean](TaskSchedulerIsSet)```，```HeartbeatReceiver#receiveAndReply``` 接收和处理,
  该消息的含义是TaskScheduler已经生成并准备好，在SparkContext初始化过程中会发送此消息。
  
```
    SparkContext{
      ...
      _heartbeatReceiver = env.rpcEnv.setupEndpoint(HeartbeatReceiver.ENDPOINT_NAME, new HeartbeatReceiver(this))
      ...
      _heartbeatReceiver.ask[Boolean](TaskSchedulerIsSet)
      ...
    }
```
- Heartbeat ：这就是Executor向Driver发送来的心跳信号，
  由 Executor 发送```Executor#reportHeartBeat``` ，```HeartbeatReceiver#receiveAndReply``` 接收和处理
```
    val message = Heartbeat(executorId, accumUpdates.toArray, env.blockManager.blockManagerId)
    try {
      val response = heartbeatReceiverRef.askSync[HeartbeatResponse](
          message, new RpcTimeout(HEARTBEAT_INTERVAL_MS.millis, EXECUTOR_HEARTBEAT_INTERVAL.key))
          ... 
    }
```
  
- ExecutorRegistered ：将Executor ID与通过SystemClock获取的当前时间戳加入executorLastSeen映射中，并回复true。该消息由父类```SparkListener#onExecutorAdded```触发而发送，
  被```HeartbeatReceiver#receiveAndReply```接收和处理。
- ExecutorRemoved  ：从executorLastSeen映射中删除Executor ID对应的条目，并回复true。该消息由父类```SparkListener#onExecutorRemoved``` 触发，被```HeartbeatReceiver#receiveAndReply```接收和处理。
- ExpireDeadHosts ：该消息的含义是清理那些由于太久没发送心跳而超时的Executor，会调用expireDeadHosts()方法并回复true。该消息由父类```ThreadSafeRpcEndpoint#onStart```触发，
  被```HeartbeatReceiver#receiveAndReply```接收和处理。
 

 



 


##### 成员变量说明

```
  // executor ID -> timestamp of when the last heartbeat from this executor was received
  private val executorLastSeen = new mutable.HashMap[String, Long]
  ...
  // "eventLoopThread" is used to run some pretty fast actions. The actions running in it should not
  // block the thread for a long time.
  private val eventLoopThread =
    ThreadUtils.newDaemonSingleThreadScheduledExecutor("heartbeat-receiver-event-loop-thread")

  private val killExecutorThread = ThreadUtils.newDaemonSingleThreadExecutor("kill-executor-thread")

```
- executorLastSeen: 用于维护Executor的身份标识与HeartbeatReceiver最后一次收到Executor的心跳（HeartBeat）消息的时间戳之间的映射关系。
  以 executor id 为key, 接收到此执行器的最后一个心跳的时间戳 为value 的Map
- eventLoopThread ：类型为ScheduledThreadPoolExecutor，用于执行心跳接收器的超时检查任务，eventLoopThread是一个只包含一个单守护线程的调度线程池，
  此线程以heartbeat-receiver-event-loop-thread作为名称,是整个HeartbeatReceiver的事件处理线程。
- killExecutorThread ：以Executors.newSingleThreadExecutor方式创建的Executor-Service，运行的单线程用于异步“杀死”（kill）Executor，此线程以kill-executor-thread作为名称。
  















