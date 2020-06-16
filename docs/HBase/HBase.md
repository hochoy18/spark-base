
# 1.HBase 概述

## 1.1 [HBase 数据模型](http://hbase.apache.org/book.html#datamodel)

### 1.1.1 HBase 逻辑视图

- NameSpace :命名空间，类似于关系型数据库的 DatabBase 概念，每个命名空间下有多个表。HBase 有两个自带的命名空间，分别是 hbase 和 default，hbase 中存放的是 HBase 内置的表，default 表是用户默认使用的命名空间。

- Region: 类似于关系型数据库的表概念。不同的是，HBase 定义表时只需要声明列族即可，不需要声明具体的列。这意味着，往 HBase 写入数据时，字段可以动态、按需指定。因此，和关系型数据库相比，HBase 能够轻松应对字段变更的场景。

- table：表，一个表包含多行数据。

- row：行，一行数据包含一个唯一标识rowkey、多个column以及对应的值。在HBase中，一张表中所有row都按照rowkey的字典序由小到大排序。

- column：列，与关系型数据库中的列不同，HBase中的column由column family（列簇）以及qualifier（列名）两部分组成，两者中间使用":"相连。
  比如contents:html，其中contents为列簇，html为列簇下具体的一列。column family在表创建的时候需要指定，用户不能随意增减。
  一个column family下可以设置任意多个qualifier，因此可以理解为HBase中的列可以动态增加，理论上甚至可以扩展到上百万列。

- timestamp：时间戳，每个cell在写入HBase的时候都会默认分配一个时间戳作为该cell的版本，当然，用户也可以在写入的时候自带时间戳。
  HBase支持多版本特性，即同一rowkey、column下可以有多个value存在，这些value使用timestamp作为版本号，版本越大，表示数据越新。

- cell ，由五元组（row, column, timestamp, type, value） 唯一确定的单元，其中type表示Put/Delete这样的操作类型，timestamp代表这个cell的版本。
  这个结构在数据库中实际是以KV结构存储的，其中（row, column, timestamp, type）是K，value字段对应KV结构的V。

  

   <img></img>


### 1.1.2 多维稀疏排序Map

- 稀疏：稀疏性是HBase一个突出特点。 在其他数据库中，对于空值的处理一般都会填充null，而对于HBase，空值不需要任何填充。
  这个特性为什么重要？因为HBase的列在理论上是允许无限扩展的，对于成百万列的表来说，通常都会存在大量的空值，如果使用填充null的策略，势必会造成大量空间的浪费。因此稀疏性是HBase的列可以无限扩展的一个重要条件。

- 排序：构成HBase的KV在同一个文件中都是有序的，但规则并不是仅仅按照rowkey排序，而是按照KV中的key进行排序——先比较rowkey，rowkey小的排在前面；
  如果rowkey相同，再比较column，即column family:qualifier，column小的排在前面;
  如果column还相同，再比较时间戳timestamp，即版本信息，timestamp大的排在前面。这样的多维元素排序规则对于提升HBase的读取性能至关重要。

- 分布式：很容易理解，构成HBase的所有Map并不集中在某台机器上，而是分布在整个集群中。


### 1.1.4 物理视图 

 <img></img>

## 1.2. HBase体系结构

- Client :HBase客户端访问数据行之前，首先需要通过元数据表定位目标数据所在RegionServer，之后才会发送请求到该RegionServer。同时这些元数据会被缓存在客户端本地，以方便之后的请求访问。如果集群RegionServer发生宕机或者执行了负载均衡等，从而导致数据分片发生迁移，客户端需要重新请求最新的元数据并缓存在本地。

- ZooKeeper: 在HBase系统中，ZooKeeper扮演着非常重要的角色。

  - 实现Master高可用：通常情况下系统中只有一个Master工作，一旦Active Master由于异常宕机，ZooKeeper会检测到该宕机事件，并通过一定机制选举出新的Master，保证系统正常运转。
  - 管理系统核心元数据：比如，管理当前系统中正常工作的RegionServer集合，保存系统元数据表hbase:meta所在的RegionServer地址等。
  - 参与RegionServer宕机恢复：ZooKeeper通过心跳可以感知到RegionServer是否宕机，并在宕机后通知Master进行宕机处理。
  - 实现分布式表锁：HBase中对一张表进行各种管理操作（比如alter操作）需要先加表锁，防止其他用户对同一张表进行管理操作，造成表状态不一致。和其他RDBMS表不同，HBase中的表通常都是分布式存储，ZooKeeper可以通过特定机制实现分布式表锁。

- Master:Master主要负责HBase系统的各种管理工作：

  - 处理用户的各种管理请求，包括建表、修改表、权限操作、切分表、合并数据分片以及Compaction等。
  - 管理集群中所有RegionServer，包括RegionServer中Region的负载均衡、RegionServer的宕机恢复以及Region的迁移等。
  - 清理过期日志以及文件，Master会每隔一段时间检查HDFS中HLog是否过期、HFile是否已经被删除，并在过期之后将其删除。

- RegionServer:RegionServer主要用来响应用户的IO请求，由WAL(HLog)、BlockCache以及多个Region构成。

  -  WAL(HLog)：HLog在HBase中有两个核心作用——其一，用于实现数据的高可靠性，HBase数据随机写入时，并非直接写入HFile数据文件，而是先写入缓存，再异步刷新落盘。为了防止缓存数据丢失，数据写入缓存之前需要首先顺序写入HLog。其二，用于实现HBase集群间主从复制，通过回放主集群推送过来的HLog日志实现主从复制。
  -  BlockCache：HBase系统中的读缓存。客户端从磁盘读取数据之后通常会将数据缓存到系统内存中，后续访问同一行数据可以直接从内存中获取而不需要访问磁盘。
  -  Region：数据表的一个分片，当数据表大小超过一定阈值就会“水平切分”，分裂为两个Region。Region是集群负载均衡的基本单位。通常一张表的Region会分布在整个集群的多台RegionServer上，一个RegionServer上会管理多个Region，当然，这些Region一般来自不同的数据表

- HDFS：HBase底层依赖HDFS组件存储实际数据，包括用户数据文件、HLog日志文件等最终都会写入HDFS落盘。HDFS是Hadoop生态圈内最成熟的组件之一，数据默认三副本存储策略可以有效保证数据的高可靠性。HBase内部封装了一个名为DFSClient的HDFS客户端组件，负责对HDFS的实际数据进行读写访问。

  <img></img>

# 2. HBase 依赖服务

## 2.1 Zookeeper 

一个分布式HBase集群的部署运行强烈依赖于ZooKeeper，在当前的HBase系统实现中，ZooKeeper扮演了非常重要的角色。
首先，在安装HBase集群时需要在配置文件conf/hbase-site.xml中配置与ZooKeeper相关的几个重要配置项，如下所示：

```
  <property>
    <name>hbase.rootdir</name>
    <value>hdfs://nameservice1:8020/user/hbase</value>
  </property>

  <property>
    <name>hbase.zookeeper.quorum</name>
    <value>$ZK_NODES</value>
  </property>
  <property>
    <name>hbase.superuser</name>
    <value>hbase,admin</value>
  </property>


  <property>
    <name>hbase.security.authorization</name>
    <value>true</value>
  </property>

  <property>
    <name>hbase.rpc.engine</name>
    <value>org.apache.hadoop.hbase.ipc.SecureRpcEngine</value>
  </property>
  <property>
    <name>hbase.coprocessor.region.classes</name>
    <value>org.apache.hadoop.hbase.security.access.AccessController</value>
  </property>

  <property>
    <name>hbase.coprocessor.master.classes</name>
    <value>org.apache.hadoop.hbase.security.access.AccessController</value>
  </property>
  
  <property>
    <name>zookeeper.znode.parent</name>
    <value>/hbase</value>
  </property>
  
  

```

HBase在ZooKeeper(${zookeeper.znode.parent} 所在zk目录下)上都存储的信息  

- /hbase/**meta-region-server**  
  存储HBase集群hbase:meta元数据表所在的RegionServer访问地址。客户端读写数据首先会从此节点读取hbase:meta元数据的访问地址，将部分元数据加载到本地，根据元数据进行数据路由。

- /hbase/**master** 
  ActiveMasterManager 会在ZK中创建/hbase/master短暂节点，master将其信息记录到这个节点下， 如果是备份的master会在这里阻塞，直到这个节点为空  

- /hbase/**backup-masters** ： 
  通常来说生产线环境要求所有组件节点都避免单点服务，HBase使用ZooKeeper的相关特性实现了Master的高可用功能。其中Master节点是集群中对外服务的管理服务器，
  backup-masters下的子节点是集群中的备份节点，一旦对外服务的主Master节点发生了异常，备Master节点可以通过选举切换成主Master，继续对外服务。需要注意的是备Master节点可以是一个，也可以是多个。


- /hbase/**table** ： 集群中所有表信息。

- /hbase/**region-in-transition** ：  
  在当前HBase系统实现中，迁移Region是一个非常复杂的过程。
  首先对这个Region执行unassign操作，将此Region从open状态变为off line状态（中间涉及PENDING_CLOSE、CLOSING以及CLOSED等过渡状态），再在目标RegionServer上执行assign操作，将此Region从off line状态变成open状态。  
  这个过程需要在Master上记录此Region的各个状态。
  目前，RegionServer将这些状态通知给Master是通过ZooKeeper实现的，RegionServer会在region-in-transition中变更Region的状态，Master监听ZooKeeper对应节点，以便在Region状态发生变更之后立马获得通知，得到通知后Master再去更新Region在hbase:meta中的状态和在内存中的状态。

- /hbase/**table-lock** ：  
  HBase系统使用ZooKeeper相关机制实现分布式锁。HBase中一张表的数据会以Region的形式存在于多个RegionServer上，因此对一张表的DDL操作（创建、删除、更新等操作）通常都是典型的分布式操作。
  每次执行DDL操作之前都需要首先获取相应表的表锁，防止多个DDL操作之间出现冲突，这个表锁就是分布式锁。

- /hbase/**online-snapshot**：  
  用来实现在线snapshot操作。表级别在线snapshot同样是一个分布式操作，需要对目标表的每个Region都执行snapshot，全部成功之后才能返回成功。
  Master作为控制节点给各个相关RegionServer下达snapshot命令，对应RegionServer对目标Region执行snapshot，成功后通知Master。Master下达snapshot命令、RegionServer反馈snapshot结果都是通过ZooKeeper完成的。

- /hbase/**replication** ： 用来实现HBase副本功能。

- /hbase/**rs** ： 集群中所有运行的RegionServer

## 2.2 HDFS

### 2.2.1 HDFS在HBase系统中扮演的角色  

HBase使用HDFS存储所有数据文件，从HDFS的视角看，HBase就是它的客户端。这样的架构有几点需要说明：  

- HBase本身并不存储文件，它只规定文件格式以及文件内容，实际文件存储由HDFS实现。 
- HBase不提供机制保证存储数据的高可靠，数据的高可靠性由HDFS的多副本机制保证。
- HBase-HDFS体系是典型的计算存储分离架构。这种轻耦合架构的好处是，一方面可以非常方便地使用其他存储替代HDFS作为HBase的存储方案；另一方面对于云上服务来说，
  计算资源和存储资源可以独立扩容缩容，给云上用户带来了极大的便利。


### 2.2.2 HBase在HDFS中的文件布局

- .hbase-snapshot：snapshot文件存储目录。用户执行snapshot后，相关的snapshot元数据文件存储在该目录。
- .tmp：临时文件目录，主要用于HBase表的创建和删除操作。表创建的时候首先会在tmp目录下执行，执行成功后再将tmp目录下的表信息移动到实际表目录下。
  表删除操作会将表目录移动到tmp目录下，一定时间过后再将tmp目录下的文件真正删除。

- MasterProcWALs：存储Master Procedure过程中的WAL文件。Master Procedure功能主要用于可恢复的分布式DDL操作。
  在早期HBase版本中，分布式DDL操作一旦在执行到中间某个状态发生宕机等异常的情况时是没有办法回滚的，这会导致集群元数据不一致。
  Master Procedure功能使用WAL记录DDL执行的中间状态，在异常发生之后可以通过WAL回放明确定位到中间状态点，继续执行后续操作以保证整个DDL操作的完整性。

- WALs：存储集群中所有RegionServer的HLog日志文件。

- archive：文件归档目录。这个目录主要会在以下几个场景下使用。
  - 所有对HFile文件的删除操作都会将待删除文件临时放在该目录。
  - 进行Snapshot或者升级时使用到的归档目录。
  - Compaction删除HFile的时候，也会把旧的HFile移动到这里。

- corrupt：存储损坏的HLog文件或者HFile文件。

- data：存储集群中所有Region的HFile数据。

# 3. RegionServer的核心模块

RegionServer是HBase系统中最核心的组件，主要负责用户数据写入、读取等基础操作。RegionServer组件实际上是一个综合体系，包含多个各司其职的核心模块：HLog、MemStore、HFile以及BlockCache。

## 3.1 RegionServer内部结构

一个RegionServer由一个（或多个）HLog、一个BlockCache以及多个Region组成。

其中，HLog用来保证数据写入的可靠性；BlockCache可以将数据块缓存在内存中以提升数据读取性能；Region是HBase中数据表的一个数据分片，一个RegionServer上通常会负责多个Region的数据读写。一个Region由多个Store组成，每个Store存放对应列簇的数据，比如一个表中有两个列簇，这个表的所有Region就都会包含两个Store。每个Store包含一个MemStore和多个HFile，用户数据写入时会将对应列簇数据写入相应的MemStore，一旦写入数据的内存大小超过设定阈值，系统就会将MemStore中的数据落盘形成HFile文件。HFile存放在HDFS上，是一种定制化格式的数据存储文件，方便用户进行数据读取。



## 3.2 HLog

### HLog 文件结构

1. 每个RegionServer拥有一个或多个HLog（默认只有1个，1.1版本可以开启MultiWAL功能，允许多个HLog）。每个HLog是多个Region共享的，图中Region A、Region B和Region C共享一个HLog文件。

2. HLog中，日志单元WALEntry（图中小方框）表示一次行级更新的最小追加单元，它由HLogKey和WALEdit两部分组成，其中HLogKey由table name、region name以及sequenceid等字段构成。



 <img></img>

### HLog 文件存储

/hbase/WALs存储当前还未过期的日志；/hbase/oldWALs存储已经过期的日志。

/hbase/WALs目录下通常会有多个子目录，每个子目录代表一个对应的RegionServer。以hbase17.xj.bjbj.org,60020,1505980274300为例，hbase17.xj.bjbj.org表示对应的RegionServer域名，60020为端口号，1505980274300为目录生成时的时间戳。每个子目录下存储该RegionServer内的所有HLog文件

### HLog 生命周期

<img></img>

- HLog构建：HBase的任何写入（更新、删除）操作都会先将记录追加写入到HLog文件中。
- HLog滚动：HBase后台启动一个线程，每隔一段时间（由参数'hbase.regionserver.logroll.period'决定，默认1小时）进行日志滚动。日志滚动会新建一个新的日志文件，接收新的日志数据。日志滚动机制主要是为了方便过期日志数据能够以文件的形式直接删除。
- HLog失效：写入数据一旦从MemStore中落盘，对应的日志数据就会失效。为了方便处理，HBase中日志失效删除总是以文件为单位执行。查看某个HLog文件是否失效只需确认该HLog文件中所有日志记录对应的数据是否已经完成落盘，如果日志中所有日志记录已经落盘，则可以认为该日志文件失效。一旦日志文件失效，就会从WALs文件夹移动到oldWALs文件夹。注意此时HLog并没有被系统删除。
- HLog删除：Master后台会启动一个线程，每隔一段时间（参数'hbase.master.cleaner.interval'，默认1分钟）检查一次文件夹oldWALs下的所有失效日志文件，确认是否可以删除，确认可以删除之后执行删除操作。确认条件主要有两个：
  - 该HLog文件是否还在参与主从复制。对于使用HLog进行主从复制的业务，需要继续确认是否该HLog还在应用于主从复制。
  - 该HLog文件是否已经在OldWALs目录中存在10分钟。为了更加灵活地管理HLog生命周期，系统提供了参数设置日志文件的TTL（参数'hbase.master.logcleaner.ttl'，默认10分钟），默认情况下oldWALs里面的HLog文件最多可以再保存10分钟。

## 3.3 MemStore

HBase系统中一张表会被水平切分成多个Region，每个Region负责自己区域的数据读写请求。水平切分意味着每个Region会包含所有的列簇数据，HBase将不同列簇的数据存储在不同的Store中，每个Store由一个MemStore和一系列HFile组成

<img title='Region 结构组成'></img>

## 3.4 HFile



## 3.5 BlockCache



# 4. HBase 读写流程

## 4.1 写流程



## 4.2 BulkLoad 功能



## 4.3 读取流程



## 4.4 Coprocessor



# 5 Compaction

## 5.1 Compaction 基本原理



## 5.2 Compaction 高级策略



# 6 负载均衡

## 6.1 Region 迁移

## 6.2 Region 合并

## 6.3 Region 分裂

## 6.4 HBase负载均衡的应用



# 7. HBase 高级

## 7.1 Flush 流程

## 7.2 Compact 流程

## 7.3 Split 流程



## 7.4 高可用

## 7.5 预分区

## 7.6 rowkey 设计

