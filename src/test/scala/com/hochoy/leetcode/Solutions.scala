package com.hochoy.leetcode

import org.scalatest.{FlatSpec, FunSuite, Suite}


class Solutions extends  FlatSpec {
  "An empty Set" should "have size 0" in {
    assert(Set.empty.size == 0)
  }
  it should "produce NoSuchElementException when head is invoked" in {
    intercept[NoSuchElementException] {
      Set.empty.head
    }
  }

}

object Solutions {

  def Add(a:Int,b:Int):Int = a + b

}
