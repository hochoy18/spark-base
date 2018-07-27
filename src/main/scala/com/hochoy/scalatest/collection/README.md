Scala中的集合：Iterator、BitSet、Set、Map、Stack、Vector、List、Array
http://www.bubuko.com/infodetail-412698.html
https://www.cnblogs.com/cbscan/articles/4147709.html

List(https://www.cnblogs.com/miqi1992/p/5635599.html)

ListBuffer
TreeSet
JavaConverters
Queue
Map
List与Array的区别：
1、List一旦创建，已有元素的值不能改变，可以使用添加元素或删除元素生成一个新的集合返回。
如前面的nums，改变其值的话，编译器就会报错。而Array就可以成功
2、List具有递归结构(Recursive Structure),例如链表结构
List类型和气他类型集合一样，它具有协变性(Covariant),即对于类型S和T，如果S是T的子类型，则List[S]也是List[T]的子类型。
例如:

List操作
    ::和:::操作符介绍
    1)List中常用'::',发音为"cons"。Cons把一个新元素组合到已有元素的最前端，然后返回结果List。
    2)上面表达式"1::twoThree"中，::是右操作数，列表twoThree的方法。可能会有疑惑。表达式怎么是右边参数的方法，这是Scala语言的一个例外的情况:如果一个方法操作符标注，如a * b,那么方法被左操作数调用，就像a.* (b)--除非方法名以冒号结尾。这种情况下，方法被右操作数调用。
List有个方法叫":::"，用于实现叠加两个列表。
    类List没有提供append操作，因为随着列表变长append的耗时将呈线性增长，而使用::做前缀则仅花费常量时间。如果你想通过添加元素来构造列表，你的选择是把它们前缀进去，
当你完成之后再调用reverse；或使用ListBuffer，一种提供append操作的可变列表，当你完成之后调用toList

