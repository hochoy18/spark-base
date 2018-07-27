Scala中的集合：Iterator、BitSet、Set、Map、Stack、Vector、List、Array
http://www.bubuko.com/infodetail-412698.html
https://www.cnblogs.com/cbscan/articles/4147709.html

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