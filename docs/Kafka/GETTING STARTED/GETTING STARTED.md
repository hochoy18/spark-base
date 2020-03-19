##1.1 简介

- Apache Kafka是一个分布式的流平台，
   - 流平台具有三个关键功能
      - 发布和订阅记录流，类似于消息队列或企业消息传递系统
      - 以容错的持久方式存储记录流。
      - 处理记录流。
   - Kafka通常用于两大类应用程序
      - 建立实时流数据管道，以可靠地在系统或应用程序之间获取数据
      - 构建实时流应用程序，以转换或响应数据流
      
   - 首先几个概念
      - Kafka在一个或多个可以跨越多个数据中心的服务器上作为集群运行。
      - Kafka集群将记录流存储在称为主题的类别中。
      - 每个记录由一个键，一个值和一个时间戳组成。
   - Kafka has four core APIs:
      -  Producer API
      -  Consumer API 
      -  Streams API
      -  Connector API


-   Topics and Logs

    首先，让我们深入探讨Kafka提供的记录主题的核心抽象 。主题是将记录发布到的类别或订阅源名称。Kafka中的主题始终是多用户的；也就是说，一个主题可以有零个，一个或多个消费者来订阅写入该主题的数据。
    
    对于每个主题，Kafka集群都会维护一个分区日志，如下所示
    <img src = "http://kafka.apache.org/24/images/log_anatomy.png" >
    每个分区都是有序的，不变的记录序列，这些记录连续地附加到结构化的提交日志中。
    分别为分区中的记录分配了一个顺序ID号，称为偏移号，该ID号唯一标识分区中的每个记录。
    
    Kafka集群使用可配置的保留期限持久保留所有已发布的记录（无论是否已使用它们）。
    例如，如果将保留策略设置为两天，则在发布记录后的两天内，该记录可供使用，之后将被丢弃以释放空间。
    Kafka的性能相对于数据大小实际上是恒定的，因此长时间存储数据不是问题。
    <img src = "http://kafka.apache.org/24/images/log_consumer.png">
    
    实际上，基于每个消费者保留的唯一元数据是该消费者在日志中的偏移量或位置。
    此偏移量由consumer控制：通常，使用者在读取记录时会线性地推进其偏移量，
    但是实际上，由于位置是由使用者控制的，因此它可以按喜欢的任何顺序使用记录。
    例如，使用者可以重置到较旧的偏移量以重新处理过去的数据，或者跳到最近的记录并从“现在”开始使用。
    

-  Distribution

    日志的分区分布在Kafka群集中的服务器上，每台服务器处理数据并要求共享分区.
    
    
-  Producers
   
   生产者将数据发布到他们选择的主题。
   
   
   
   
   
##1.3 Quick Start
- Step 1: download :zookeeper + kafka
- Step 2: Start the server
```
> bin/zookeeper-server-start.sh config/zookeeper.properties
> bin/kafka-server-start.sh config/server.properties
```
- Step 3: Create a topic
```
> bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic test
> bin/kafka-topics.sh --list --bootstrap-server localhost:9092
```
- Step 4: Send some messages
 ```
> bin/kafka-console-producer.sh --broker-list localhost:9092 --topic test
This is a message
This is another message
```
- Step 5: Start a consumer
```
> bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic test --from-beginning
This is a message
This is another message
```
- Step 6: Setting up a multi-broker cluster
