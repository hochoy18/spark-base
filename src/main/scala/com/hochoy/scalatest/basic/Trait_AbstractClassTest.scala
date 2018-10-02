package com.hochoy.scalatest.basic

/**
  * Created by Cobub on 2018/10/2.
  */
object Trait_AbstractClassTest {
  def main(args: Array[String]) {
    val human = new Human
    println(human.fight)
    println(human.run)
  }
}

trait Flyable {
  val distance: Int

  def fight: String

  def fly: Unit = {
    println("I can fly")
  }
}

abstract class Animal {
  val name: String

  def run(): String

  def climb: String = {
    "I can climb"
  }
}

class Human extends Animal with Flyable {
  override val name: String = "张三"

  override def run(): String = {
    "I can run"
  }

  override def fight: String = {
    "with fight"
  }

  override val distance: Int =1000
  override def fly:Unit = print("over fly")
}
