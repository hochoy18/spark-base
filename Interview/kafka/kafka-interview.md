## [32 道常见的 Kafka 面试题你都会吗？附答案](https://www.iteblog.com/archives/2605.html)

- kafka 的特点
   - 高吞吐、低延迟
      - 高吞（page cache + 磁盘顺序写） + 高吐(零拷贝 + [page cache --> 网卡buffer（数据）+socket（描述符）--> 客户端])
      - 页缓存技术 + 磁盘顺序写（数据追加到文件的末尾，不是在文件的随机位置来修改数据。）
      - 零拷贝 ：通过零拷贝技术，就不需要把os cache里的数据拷贝到应用缓存，再从应用缓存拷贝到Socket缓存了，两次拷贝都省略了，所以叫做零拷贝
      - kafka每秒可以处理几十万条消息，它的延迟最低只有几毫秒，每个topic可以分多个partition, consumer group 对partition进行consume操作
   - 可扩展性 : kafka集群支持热扩展
   - 持久性、可靠性:消息被持久化到本地磁盘，并且支持数据备份防止数据丢失
   - 容错性:允许集群中节点失败（若副本数量为n,则允许n-1个节点失败）
   - 高并发: 支持数千个客户端同时读写
   
   
- Kafka 使用场景
   - 日志收集 ： 收集各种服务日志
   - 消息系统 ： 解耦和生产者和消费者、缓存消息等。
   - 用户活动跟踪：用户行为分析
   - 运营指标：记录运营监控数据
   - 流式处理：spark 、flink
   
   
- Kafka 的设计架构：Kafka 的架构分为以下几个部分：
   - Producer：向kafka broker 发送（生产）消息的客户端
   - Consumer ： 向kafka broker拉取（消费） 的客户端
   - Topic：类似一个队列，是一类消息
   - Consumer Group：kafka用来实现一个topic消息的广播和单播的手段，一个topic 可被多个Consumer group 消费。
   - Broker：一台kafka服务器就是一个broker，一个集群由多个broker组成。
   - Partition & Offset：为了实现扩展，一个非常大的topic可以分布到多个broker上，每个partition是一个有序的队列。partition 中的每条消息都会被分配一个有序的id（offset）。
   将消息发给 consumer，kafka 只保证按一个 partition 中的消息的顺序，不保证一个 topic 的整体（多个 partition 间）的顺序。


- Kafka 分区的目的：
   - 负载均衡
   - 提高消费的并发度和消费效率
   
- kafka如何做到消息的有序性
   - kafka 中的每个 partition 中的消息在写入时都是有序的，而且单独一个 partition 只能由一个消费者去消费，可以在里面保证消息的顺序性。但是分区之间的消息是不保证有序的。

- [kafka 如何保证数据的可靠性和一致性](https://www.iteblog.com/archives/2560.html)
   - 可靠性：
      - Topic 的分区副本  
         - 创建topic时用replication-factor指定或者在Broker级别设置 default.replication.factor。Kafka 可以保证单个分区里的事件是有序的，分区可以在线（可用），也可以离线（不可用）。
         在众多的分区副本里面有一个副本是 Leader，其余的副本是 follower，所有的读写操作都是经过 Leader 进行的，同时 follower 会定期地去 leader 上的复制数据。
         当 Leader 挂了的时候，其中一个 follower 会重新成为新的 Leader。通过分区副本，引入了数据冗余，同时也提供了 Kafka 的数据可靠性。
         - Kafka 的分区多副本架构是 Kafka 可靠性保证的核心，把消息写入多个副本可以使 Kafka 在发生崩溃时仍能保证消息的持久性。
      
      - Producer 往 Broker 发送消息
         - acks 配置（0,1,-1/all）
      - Leader 选举： unclean.leader.election.enable=false  
     综上所述，了保证数据的可靠性，我们最少需要配置一下几个参数：
 ```
            producer 级别：acks=all（或者 request.required.acks=-1），同时发生模式为同步 producer.type=sync
            topic 级别：设置 replication.factor>=3，并且 min.insync.replicas>=2；
            broker 级别：关闭不完全的 Leader 选举，即 unclean.leader.election.enable=false；
```
   
   - 一致性：数据一致性主要是说不论是老的 Leader 还是新选举的 Leader，Consumer 都能读到一样的数据
      - HW:只有 High Water Mark 以上的消息才支持 Consumer 读取，而 High Water Mark 取决于 ISR 列表里面偏移量最小的分区


-  Kafka 是如何做到消息的有序性
   - kafka 中的每个 partition 中的消息在写入时都是有序的，而且单独一个 partition 只能由一个消费者去消费，可以在里面保证消息的顺序性。但是分区之间的消息是不保证有序的。


- ISR、OSR、AR 是什么
   - ISR：In-Sync Replicas 副本同步队列
   - OSR：Out-of-Sync Replicas
   - AR：Assigned Replicas 所有副本  
    ISR是由leader维护，follower从leader同步数据有一些延迟（具体可以参见 图文了解 Kafka 的副本复制机制），超过相应的阈值会把 follower 剔除出 ISR, 存入OSR（Out-of-Sync Replicas ）列表，新加入的follower也会先存放在OSR中。AR=ISR+OSR。


- LEO、HW、LSO、LW等分别代表什么
   - LEO：是 LogEndOffset 的简称，代表当前日志文件中下一条
   - HW： 水位或水印（watermark）一词，也可称为高水位(high watermark)，通常被用在流式处理领域（比如Apache Flink、Apache Spark等），以表征元素或事件在基于时间层面上的进度。在Kafka中，水位的概念反而与时间无关，而是与位置信息相关。严格来说，它表示的就是位置信息，即位移（offset）。取 partition 对应的 ISR中 最小的 LEO 作为 HW，consumer 最多只能消费到 HW 所在的位置上一条信息。
   - LSO：是 LastStableOffset 的简称，对未完成的事务而言，LSO 的值等于事务中第一条消息的位置(firstUnstableOffset)，对已完成的事务而言，它的值同 HW 相同
   - LW： Low Watermark 低水位, 代表 AR 集合中最小的 logStartOffset 值。



 -  在什么情况下会出现消息丢失
   - auto.commit.enable=true，消费端自动提交offset设置为true，当消费者拉到消息之后，还没有处理完 commit interval 提交间隔就到了，提交了offersets。这时consummer又挂了，重启后，从下一个offersets开始消费，之前的消息丢失了。
   - 网络负载高、磁盘很忙，写入失败，又没有设置消息重试，导致数据丢失。
   - 磁盘坏了已落盘数据丢失。
   - 单 批 数 据 的 长 度 超 过 限 制 会 丢 失 数 据 ， 报kafka.common.Mess3.ageSizeTooLargeException异常


- Kafka 数据丢失分析和解决方案
https://blog.csdn.net/dec_sun/article/details/89075211


- 消费者和消费者组的关系：


- Kafka 的每个分区只能被一个消费者线程，如何做到多个线程同时消费一个分区？
https://www.iteblog.com/archives/2551.html

- Kafka 数据传输的事务级别
   - at-most-once：消息不会被重复发送，最多被传输一次，但也有可能一次不传输
   - at-least-once：消息不会被漏发送，最少被传输一次，但也有可能被重复传输.
   - exactly-once：不会漏传输也不会重复传输,每个消息都被传输



- Kafka 是否可以消费指定分区的消息



- Kafka 幂等性介绍 TODO
   - 幂等性的定义：对接口的多次调用所产生的结果和调用一次是一致的。幂等可以保证上生产者发送的消息，不会丢失，而且不会重复
   - 如何实现幂等  
   实现幂等的关键点就是服务端可以区分请求是否重复，过滤掉重复的请求。要区分请求是否重复的有两点：
      - 唯一标识：要想区分请求是否重复，请求中就得有唯一标识。例如支付请求中，订单号就是唯一标识
      - 记录下已处理过的请求标识：光有唯一标识还不够，还需要记录下哪些请求是已经处理过的，这样当收到新的请求时，用新请求中的标识和处理记录进行比较，
      如果处理记录中有相同的标识，说明是重复交易，拒绝掉。
   - 幂等的实现原理  
   为了实现Producer的幂等性，Kafka引入了Producer ID（即PID）和Sequence Number
      - PID。每个新的Producer在初始化的时候会被分配一个唯一的PID，这个PID对用户是不可见的。
      - Sequence Number ：对于每个PID，该Producer发送数据的每个<Topic, Partition>都对应一个从0开始单调递增的Sequence Number  
   Kafka可能存在多个生产者，会同时产生消息，但对Kafka来说，只需要保证每个生产者内部的消息幂等就可以了，所有引入了PID来标识不同的生产者。  
   对于Kafka来说，要解决的是生产者发送消息的幂等问题。也即需要区分每条消息是否重复。
   Kafka通过为每条消息增加一个Sequence Number，通过Sequence Number来区分每条消息。每条消息对应一个分区，不同的分区产生的消息不可能重复。
   所有Sequence Number对应每个分区
   Broker端在缓存中保存了这seq number，对于接收的每条消息，如果其序号比Broker缓存中序号大于1则接受它，否则将其丢弃。这样就可以实现了消息重复提交了。
   但是，只能保证单个Producer对于同一个<Topic, Partition>的Exactly Once语义。不能保证同一个Producer一个topic不同的partition幂等。
   
    



- [对 Kafka 事务的理解](http://www.jasongj.com/kafka/transaction/)
   - 提供事务机制的必要性：
      - exactly-once
      - 操作的原子性：多个操作要么全部成功要么全部失败，不存在部分成功部分失败的可能
         - 操作结果更可控，有助于提升数据一致性
         - 便于故障恢复。因为操作是原子的，从故障中恢复时只需要重试该操作（如果原操作失败）或者直接跳过该操作（如果原操作成功），
         而不需要记录中间状态，更不需要针对中间状态作特殊处理
      - 有状态操作的可恢复性
   - 实现事务机制的步骤
      - 幂等性发送
      - 事务性的保证
      
   - 事务机制的原理       


- Offsets and Consumer Position  and committed offset
对于分区中的每条记录，kafka维护一个数值偏移量。这个偏移量是分区中一条记录的唯一标识，同时也是消费者在分区中的位置。例如，一个消费者在分区中的position是5，表示  
   - 它已经消费了偏移量从0到4的记录，
   - 接下来它将消费偏移量为5的记录。  
   相对于消费者用户来说，这里实际上有两个位置的概念。
消费者的position表示下一条将要消费的记录的offset。每次消费者通过调用poll(long)接收消息的时候这个position会自动增加。
committed position表示已经被存储的最后一个偏移量。
   - 消费者可以自动的周期性提交offsets，
   - 也可以通过调用提交API(e.g. commitSync and commitAsync)手动的提交position。  
committed offset :表示已经提交过的消费位移





Config:

- **min.insync.replicas**:当producer设置request.required.acks为-1时，min.insync.replicas指定replicas的最小数目（必须确认每一个repica的写数据都是成功的），
如果这个数目没有达到，producer会产生异常。  
**min.insync.replicas**: When a producer sets acks to "all" (or "-1"), min.insync.replicas specifies the minimum number of replicas that must acknowledge a write for the write to be considered successful. If this minimum cannot be met, then the producer will raise an exception (either NotEnoughReplicas or NotEnoughReplicasAfterAppend).
When used together, min.insync.replicas and acks allow you to enforce greater durability guarantees. A typical scenario would be to create a topic with a replication factor of 3, set min.insync.replicas to 2, and produce with acks of "all". This will ensure that the producer raises an exception if a majority of replicas do not receive a write.
Type: intDefault: 1Valid Values: \[1,...]Importance: highUpdate Mode: cluster-wide

- **unclean.leader.election.enable** ：指明了是否能够使不在ISR中replicas设置用来作为leader
**unclean.leader.election.enable**: Indicates whether to enable replicas not in the ISR set to be elected as leader as a last resort, even though doing so may result in data loss
(指示是否启用不在ISR集中的副本以选作领导者，即使这样做可能会导致数据丢失)
Type: booleanDefault: falseValid Values: Importance: highUpdate Mode: cluster-wide