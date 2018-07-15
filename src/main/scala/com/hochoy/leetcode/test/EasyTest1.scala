package com.hochoy.leetcode.test

import scala.collection.Iterator
import scala.collection.immutable.HashSet


/**
  * Created by Cobub on 2018/7/15.
  */
object EasyTest1 {

  def main(args: Array[String]) {
    //    val result = twoSum(Array(2, 7, 11, 15), 9)
    //    result.foreach(println)
    //    val result = reverse(123)
    //    println(result)
//    val result = isPalindrome(-123)
//    println(result)

//    val result = removeDuplicates(Array(1,2,3,44,1,2,3,555,6,6))
val result = removeDuplicates(Array(1,1,2))
    println(result)

  }

  def removeDuplicates(nums: Array[Int]): Int = {
    val set =  scala.collection.mutable.Set(0)

    set.remove(0)
    for(num <- nums){
      set.add(num)
    }
    set.size
  }


  /**
    * Determine whether an integer is a palindrome. An integer is a palindrome when it reads the same backward as forward.

    * Example 1:

    * Input: 121
    * Output: true
    * Example 2:

    * Input: -121
    * Output: false
    * Explanation: From left to right, it reads -121. From right to left, it becomes 121-. Therefore it is not a palindrome.
    * Example 3:

    * Input: 10
    * Output: false
    * Explanation: Reads 01 from right to left. Therefore it is not a palindrome.
    *
    * @param x
    * @return
    */
  def isPalindrome(x: Int): Boolean = {
    var flat = false
    val str1 = x.toString
    val str2 = str1.reverse
    if (str1.equals(str2))
      flat = true
    flat
  }


  /**
    * Given a 32-bit signed integer, reverse digits of an integer.
    * Example 1:			* Example 2:       		* Example 3:
    * Input: 123			* Input: -123			    * Input: 120
    * Output: 321			* Output: -321		 	  * Output: 21
    *
    * @param x
    * @return
    */
  def reverse(x: Int): Int = {
    var rev = 0
    var y = x
    while (y != 0) {
      val pop = y % 10
      y = y / 10
      if (rev > Integer.MAX_VALUE / 10 || (rev == Integer.MAX_VALUE / 10 && pop > 7)) 0
      if (rev > Integer.MAX_VALUE / 10 || (rev == Integer.MIN_VALUE / 10 && pop < -8)) 0
      rev = rev * 10 + pop
    }
    rev
  }

  /**
    * Given an array of integers, return indices of the two numbers such that they add up to a specific target.
    * You may assume that each input would have exactly one solution, and you may not use the same element twice.
    * Example:
    * Given nums = [2, 7, 11, 15], target = 9,
    * Because nums[0] + nums[1] = 2 + 7 = 9,
    * return [0, 1].
    *
    * @param nums
    * @param target
    * @return
    */
  def twoSum(nums: Array[Int], target: Int): Array[Int] = {
    var i = -1
    val tp = nums.map(x => {
      i += 1
      (x, i)
    })
    var result = Array[Int]()
    for (x <- tp) {
      for (y <- tp) {
        if (x._1 + y._1 == target && x._2 != y._2) {
          result = Array[Int](x._2, y._2)
        }
      }
    }
    result.sorted
  }

}
