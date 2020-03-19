## [32 道常见的 Kafka 面试题你都会吗？附答案](https://www.iteblog.com/archives/2605.html)

- [kafka 如何保证数据的可靠性和一致性](https://www.iteblog.com/archives/2560.html)
   - 可靠性：
      - topic 的分区副本
         - 创建topic时用replication-factor指定或者在Broker级别设置 default.replication.factor。Kafka 可以保证单个分区里的事件是有序的，分区可以在线（可用），也可以离线（不可用）。在众多的分区副本里面有一个副本是 Leader，其余的副本是 follower，所有的读写操作都是经过 Leader 进行的，同时 follower 会定期地去 leader 上的复制数据。当 Leader 挂了的时候，其中一个 follower 会重新成为新的 Leader。通过分区副本，引入了数据冗余，同时也提供了 Kafka 的数据可靠性。
         - 
      - Producer 往 Broker 发送消息
         - acks 配置（0,1,-1/all）
      - Leader 选举： unclean.leader.election.enable=false  
     综上所述，了保证数据的可靠性，我们最少需要配置一下几个参数：
 ```
            producer 级别：acks=all（或者 request.required.acks=-1），同时发生模式为同步 producer.type=sync
            topic 级别：设置 replication.factor>=3，并且 min.insync.replicas>=2；
            broker 级别：关闭不完全的 Leader 选举，即 unclean.leader.election.enable=false；
```
   
   - 一致性：
      - HW


-  Kafka 是如何做到消息的有序性
   - kafka 中的每个 partition 中的消息在写入时都是有序的，而且单独一个 partition 只能由一个消费者去消费，可以在里面保证消息的顺序性。但是分区之间的消息是不保证有序的。

- ISR、OSR、AR 是什么
   - ISR：In-Sync Replicas 副本同步队列
   - OSR：Out-of-Sync Replicas
   - AR：Assigned Replicas 所有副本  
    ISR是由leader维护，follower从leader同步数据有一些延迟（具体可以参见 图文了解 Kafka 的副本复制机制），超过相应的阈值会把 follower 剔除出 ISR, 存入OSR（Out-of-Sync Replicas ）列表，新加入的follower也会先存放在OSR中。AR=ISR+OSR。
- LEO、HW、LSO、LW等分别代表什么
   - LEO：是 LogEndOffset 的简称，代表当前日志文件中下一条
   - HW：水位或水印（watermark）一词，也可称为高水位(high watermark)，通常被用在流式处理领域（比如Apache Flink、Apache Spark等），以表征元素或事件在基于时间层面上的进度。在Kafka中，水位的概念反而与时间无关，而是与位置信息相关。严格来说，它表示的就是位置信息，即位移（offset）。取 partition 对应的 ISR中 最小的 LEO 作为 HW，consumer 最多只能消费到 HW 所在的位置上一条信息。
   - LSO：是 LastStableOffset 的简称，对未完成的事务而言，LSO 的值等于事务中第一条消息的位置(firstUnstableOffset)，对已完成的事务而言，它的值同 HW 相同
   - LW：Low Watermark 低水位, 代表 AR 集合中最小的 logStartOffset 值。

