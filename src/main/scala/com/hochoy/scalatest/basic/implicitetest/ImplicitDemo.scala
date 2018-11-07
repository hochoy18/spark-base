package com.hochoy.scalatest.basic.implicitetest

/**
  * @note :  TODO
  * @author :  hochoy
  * @since :  Date : 2018年11月07日 15:00
  * @version :  V1.0
  */
class ImplicitDemo {

}
class SwimmingType {
  def wantLearned(sw : String ) = println("兔子已经学会了 "+sw)
}
object swimming {
  implicit def leaningTypeConvert(s : AnimalType) = new SwimmingType
}
class AnimalType
object AnimalType extends App {
  import swimming._
  val rabbit = new AnimalType
  /**
    * 编译器在rabbit兑现调用时会返现对象上并没有wantLearned方法，此时编译器就会在作用域范围内
    * 查找能使其编译通过的隐式视图，找到 leaningTypeConvert 方法之后，编译器通过隐式转换
    * 将对象转换成具有这个方法对象，之后调用wantLearned方法可以将隐式转换函数定义在伴生对象
    * 中，在使用时，导入隐式视图到作用域中即可（如 leaningTypeConvert ）
    *
    */
  rabbit.wantLearned("breast stroke ")

}