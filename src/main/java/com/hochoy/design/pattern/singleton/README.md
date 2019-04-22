单例模式的概念：
单例对象（Singleton）是一种常用的设计模式。在Java应用中，单例对象能保证在一个JVM中，该对象只有一个实例存在。
这样的模式有几个好处：
    1、某些类创建比较频繁，对于一些大型的对象，这是一笔很大的系统开销。
    2、省去了new操作符，降低了系统内存的使用频率，减轻GC压力。
    3、有些类如交易所的核心交易引擎，控制着交易流程，如果该类可以创建多个的话，系统完全乱了。
    （比如一个军队出现了多个司令员同时指挥，肯定会乱成一团），所以只有使用单例模式，才能保证核心交易服务器独立控制整个流程。
而且自行实例化并向整个系统提供这个实例。这个类称为单例类。
DCL :Double Check Lock
<DCL>   [https://www.cnblogs.com/codingmengmeng/p/9846131.html][https://www.cnblogs.com/codingmengmeng/p/9846131.html]   </DCL>

关键点：

1）一个类只有一个实例       这是最基本的
2）它必须自行创建这个实例
3）它必须自行向整个系统提供这个实例



懒汉模式（类加载时不初始化）
https://www.cnblogs.com/codingmengmeng/p/9846131.html
1. 构造函数定义为私有----不能在别的类中来获取该类的对象，只能在类自身中得到自己的对象
2. 成员变量为static的，没有初始化----类加载快，但访问类的唯一实例慢，static保证在自身类中获取自身对象
3. 公开访问点getInstance： public和synchronized的-----public保证对外公开，同步保证多线程时的正确性（因为类变量不是在加载时初始化的）
*** 如果没有实例就创建并返回，会有线程安全问题，简单加synchronized解决，但效率不高，升级版本是DCL ***
双重锁定检查（DCL,Double Check Lock）。




饿汉式单例模式（在类加载时就完成了初始化，所以类加载较慢，但获取对象的速度快）
1. 私有构造函数
2. 静态私有成员--在类加载时已初始化
3. 公开访问点getInstance-----不需要同步，因为在类加载时已经初始化完毕，也不需要判断null，直接返回
 
http://www.cnblogs.com/maowang1991/archive/2013/04/15/3023236.html
[https://www.cnblogs.com/codingmengmeng/p/9846131.html]: https://www.cnblogs.com/codingmengmeng/p/9846131.html
