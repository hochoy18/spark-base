### HeartbeatReceiver 源码解析

#### HeartbeatReceiver 是干嘛的
 Driver 中的心跳接收器，仅被 Driver（SparkContext）持有 (Lives in the driver to receive heartbeats from executors..) ,负责接收、处理各个Executor的心跳消息（HeartBeat），
 以及监控各个Executor的“生死”状态。
 

#### HeartbeatReceiver 


##### 接收和处理的 信息类型
- TaskSchedulerIsSet ：由 SparkContext 发送``` _heartbeatReceiver.ask[Boolean](TaskSchedulerIsSet)```，```HeartbeatReceiver#receiveAndReply``` 接收和处理
  该消息的含义是TaskScheduler已经生成并准备好，在SparkContext初始化过程中会发送此消息
- Heartbeat ：这就是Executor向Driver发送来的心跳信号，
  由 Executor 发送```Executor#reportHeartBeat``` ，```HeartbeatReceiver#receiveAndReply``` 接收和处理
- ExecutorRegistered ：将Executor ID与通过SystemClock获取的当前时间戳加入executorLastSeen映射中，并回复true。
- ExecutorRemoved  ：从executorLastSeen映射中删除Executor ID对应的条目，并回复true。
- ExpireDeadHosts ：该消息的含义是清理那些由于太久没发送心跳而超时的Executor，会调用expireDeadHosts()方法并回复true。
 

由 SparkContext 发送``` _heartbeatReceiver.ask[Boolean](TaskSchedulerIsSet)```  的代码

```
SparkContext{
    ...
    _heartbeatReceiver = env.rpcEnv.setupEndpoint(HeartbeatReceiver.ENDPOINT_NAME, new HeartbeatReceiver(this))
    ...
    _heartbeatReceiver.ask[Boolean](TaskSchedulerIsSet)
    ...
}
```

由 Executor 发送 ```Executor#reportHeartBeat``` 的代码

```
val message = Heartbeat(executorId, accumUpdates.toArray, env.blockManager.blockManagerId)
try {
  val response = heartbeatReceiverRef.askSync[HeartbeatResponse](
      message, new RpcTimeout(HEARTBEAT_INTERVAL_MS.millis, EXECUTOR_HEARTBEAT_INTERVAL.key))
      ... 
}
```

##### 成员变量说明

```
  // "eventLoopThread" is used to run some pretty fast actions. The actions running in it should not
  // block the thread for a long time.
  private val eventLoopThread =
    ThreadUtils.newDaemonSingleThreadScheduledExecutor("heartbeat-receiver-event-loop-thread")

  private val killExecutorThread = ThreadUtils.newDaemonSingleThreadExecutor("kill-executor-thread")

```

- eventLoopThread ： 一个单守护线程的调度线程池，其名称为heartbeat-receiver-event-loop-thread，是整个HeartbeatReceiver的事件处理线程。
- killExecutorThread ： 一个单守护线程的普通线程池，其名称为kill-executor-thread，用来异步执行杀掉Executor的任务。















