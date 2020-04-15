### 浅析 Broadcast

- 主要有三种对象 BroadcastManager、BroadcastFactory 和 Broadcast  
   - [BroadcastManager](#2) 负责Broadcast的全局管理
   - [BroadcastFactory](#3) 负责创建或取消Broadcast
   - [Broadcast](#1) 为实际的一次广播操作  
 
 
- BroadcastManager 是 BroadcastFactory 的封装，负责了BroadcastFactory从初始化到 stop 的整个生命周期。
   - 初始化阶段，会初始化一个 TorrentBroadcastFactory 工厂对象， 并initialized 设置为 true
   - 在运行期间，它会调用BroadcastFactory的```newBroadcast```方法和```unbroadcast```方法来控制变量的广播，每次广播有递增的唯一ID```nextBroadcastId```
   - 最后它还负责了 BroadcastFactory 的关闭。


####  <span id = "1"> Broadcast</span>
- TorrentBroadcast 的原理
```
/**
 * A BitTorrent-like implementation of [[org.apache.spark.broadcast.Broadcast]].
 *
 * The mechanism is as follows:
 *
 * The driver divides the serialized object into small chunks and
 * stores those chunks in the BlockManager of the driver.
 *
 * On each executor, the executor first attempts to fetch the object from its BlockManager. If
 * it does not exist, it then uses remote fetches to fetch the small chunks from the driver and/or
 * other executors if available. Once it gets the chunks, it puts the chunks in its own
 * BlockManager, ready for other executors to fetch from.
 *
 * This prevents the driver from being the bottleneck in sending out multiple copies of the
 * broadcast data (one per executor).
 *
 * When initialized, TorrentBroadcast objects read SparkEnv.get.conf.
 *
 * @param obj object to broadcast
 * @param id A unique identifier for the broadcast variable.
 */
```
- 机制如下
   - Driver 将序列化对象划分为小块，并将这些小块存储在 Driver 的BlockManager中。  
   - 在每个 executor 上，executor首先尝试从其BlockManager获取被广播对象。如果不存在，则使用远程抓取从Driver和/或其他executor（如果可用）中获取广播对象。一旦获取了广播对象，它就会将块放在自己的BlockManager中，准备好让其他executor从中获取。


- 这段注释说明了TorrentBroadcast实现的原理，其中关键的部分在于利用BlockManager的分布式结构来储存和获取数据块。  
>>  1.Driver把序列化后的对象(即value)分为很多块，并且把这些块存到Driver的BlockManager里。<br>
  2.在 Executor端，Executor首先试图从自己的BlockManager中获取被broadcast变量的块，如果它不存在，就使用远程抓取从 driver 以及/或者 其它的 
  executor上获取这个块。当executor获取了一个块，它就把这个块放在自己的BlockManager里，以使得其它的 Executor 可以抓取它。<br>
  3.这防止了被广播的数据只从 Driver端被拷贝，这样当要拷贝的次数很多的时候(每个Executor都会拷贝一次)，Driver端容易成为瓶颈 .

- driver端把数据分块，每个块做为一个block存进driver端的BlockManager，每个executor会试图获取所有的块，来组装成一个被broadcast的变量。
“获取块”的方法是首先从executor自身的BlockManager中获取，如果自己的BlockManager中没有这个块，就从别的BlockManager中获取。
这样最初的时候，driver是获取这些块的唯一的源，但是随着各个BlockManager从driver端获取了不同的块(TorrentBroadcast会有意避免各个executor以同样的顺序获取这些块)，
“块”的源就多了起来，每个executor就可能从多个源中的一个,包括driver和其它executor的BlockManager中获取块，这要就使得流量在整个集群中更均匀，而不是由driver作为唯一的源。
原理就是这样啦，但是TorrentBroadcast的实现有很多有意思的细节，可以仔细分析一下。



- Broadcast 就是将数据从一个节点发送到其他各个节点上去 [](http://ddrv.cn/a/247828)  
   - Driver 端：    
      - Driver 先把 data 序列化到 byteArray，然后切割成 BLOCK_SIZE（由 ```spark.broadcast.blockSize = 4MB```设置）大小的 data block。
      - 完成分块切割后，就将分块信息（称为 meta 信息）存放到 driver 自己的 blockManager 里面，StorageLevel 为内存＋磁盘(MEMORY_AND_DISK)，
      - 同时会通知 driver 自己的 blockManagerMaster 说 meta 信息已经存放好。
      - **通知 blockManagerMaster 这一步很重要，因为 blockManagerMaster 可以被 driver 和所有 executor 访问到，信息被存放到 blockManagerMaster 就变成了全局信息。** 
      - 之后将每个分块 data block 存放到 driver 的 blockManager 里面，StorageLevel 为内存＋磁盘。存放后仍然通知 blockManagerMaster 说 blocks 已经存放好。到这一步，driver 的任务已经完成。  

   - Executor 端：
      - executor 收到 serialized task 后，先反序列化 task，这时候会反序列化 serialized task 中包含的数据类型是 TorrentBroadcast，也就是去调用 ```TorrentBroadcast.readBroadcastBlock()```。
      - 先询问所在的 executor 里的 blockManager 是会否包含 data，包含就直接从本地 blockManager 读取 data。
      - 否则，就通过本地 blockManager 去连接 driver 的 blockManagerMaster 获取 data 分块的 meta 信息，获取信息后，就开始了 BT 过程。




####  <span id = "2"> BroadcastManager </span>
BroadcastManager用于将配置信息和序列化后的RDD、Job及ShuffleDependency等信息在本地存储。如果为了容灾，也会复制到其他节点上。创建BroadcastManager的代码实现如下。
~~~
    // BroadcastManager是用来管理Broadcast，该对象在SparkEnv中创建
    val broadcastManager = new BroadcastManager(isDriver, conf, securityManager)
~~~

BroadcastManager除了构造器定义的三个成员属性外，BroadcastManager内部还有三个成员，分别如下。
- initialized : 表示BroadcastManager**是否初始化完成的状态**。
- broadcastFactory : 广播工厂实例。
- nextBroadcastId : 一个广播对象的广播ID，类型为AtomicLong。

BroadcastManager在其初始化的过程中就会调用自身的initialize方法，当initialize执行完毕，BroadcastManager就正式生效。
```
  // Called by SparkContext or Executor before using Broadcast
  private def initialize() {
    synchronized {
      if (!initialized) {
        broadcastFactory = new TorrentBroadcastFactory
        broadcastFactory.initialize(isDriver, conf, securityManager)
        initialized = true
      }
    }
  }
```
上述代码说明：
- initialize方法首先判断 BroadcastManager 是否已经初始化，以保证BroadcastManager只被初始化一次。
- 新建```TorrentBroadcastFactory```作为BroadcastManager的广播工厂实例。之后调用TorrentBroadcastFactory的initialize方法对TorrentBroadcastFactory进行初始化。
- 最后将BroadcastManager自身标记为初始化完成状态。

BroadcastManager中的三个方法

```
  def stop() {
    broadcastFactory.stop()
  }

  def newBroadcast[T: ClassTag](value_ : T, isLocal: Boolean): Broadcast[T] = {
    broadcastFactory.newBroadcast[T](value_, isLocal, nextBroadcastId.getAndIncrement())
  }

  def unbroadcast(id: Long, removeFromDriver: Boolean, blocking: Boolean) {
    broadcastFactory.unbroadcast(id, removeFromDriver, blocking)
  }

```

BroadcastManager的三个方法都分别代理了TorrentBroadcastFactory的对应方法






####  <span id = "3">BroadcastFactory</span>

BroadcastFactory 作为一个工厂类 在 BroadcastManager 中被初始化，目前只有 TorrentBroadcastFactory 一个实现类。
 
BroadcastFactory 在 BroadcastManager 中 以成员变量的方式被声明
```
    private var broadcastFactory: BroadcastFactory = null
```
在 BroadcastManager#initialize()中以 TorrentBroadcastFactory被初始化，可参见 BroadcastManager 的[initialize()](#2) 方法
 
```
  private def initialize() {
    ...
        broadcastFactory = new TorrentBroadcastFactory
    ...
  }
```

trait BroadcastFactory 有 四个方法，其功能分别是：
- 初始化(initialize)
- 广播一个新的变量(newBroadcast)
- 删除一个已有的变量(unbroadcast) 
- 关闭BroadcastFactory (关闭)

```
private[spark] trait BroadcastFactory {

  def initialize(isDriver: Boolean, conf: SparkConf, securityMgr: SecurityManager): Unit

  /**
   * Creates a new broadcast variable.
   *
   * @param value value to broadcast
   * @param isLocal whether we are in local mode (single JVM process)
   * @param id unique id representing this broadcast variable
   */
  def newBroadcast[T: ClassTag](value: T, isLocal: Boolean, id: Long): Broadcast[T]

  def unbroadcast(id: Long, removeFromDriver: Boolean, blocking: Boolean): Unit

  def stop(): Unit
}

```
