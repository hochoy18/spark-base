package com.hochoy.scalatest.other

import java.text.SimpleDateFormat
import java.util.Date

import scala.util.matching.Regex

/**
  * Created by Cobub on 2018/8/11.
  */
object RegexTest {

  def main(args: Array[String]) {
    regexTest02
    exit()
    regexTest1
    println(StringIsNumerical("12342 323"))
    println(DateFormat(0))
  }

  def StringIsNumerical(str: String): Boolean = {
    var flag = false
    val regex = """^\d+$""".r
    flag = regex.findFirstMatchIn(str) != None
    println(".........." + flag)
    flag
  }

  def DateFormat(time: Long): String = {
    val sdf: SimpleDateFormat = new SimpleDateFormat("yyyyMMddHHmm")
    sdf.format(new Date((time)))
  }

  def regexTest1(): Unit = {
    val pattern = new Regex("(S|s)cala")
    // if   val pattern = new Regex("S|scala")
    // output : S,scala
    val str = "Scala is scalable and cool"
    println(pattern.findAllIn(str).mkString(","))
  }


  def regexTest02(): Unit = {
    /**
      * [^...]  反向字符集。匹配未包含的任何字符。例如，"[^abc]"匹配"plain"中"p"，"l"，"i"，"n"。
      */
    val patternAppCode = new Regex("(appCode[^@]*@)")
    val patternTabCode = new Regex("(tabCode[^@]*@)")
    val patternMenuCode = new Regex("(menuCode[^@]*@)")
    val patternPageCode = new Regex("(pageCode[^@]*@)")
    val patternRequestCode = new Regex("(requestCode[^@]*@)")
    val patternLogKey = new Regex("(logKey[^@]*@)")
    val patternLevelId = new Regex("(levelId[^@]*@)")
    val patternTime = new Regex("([^@]*@)")

    val str = "@clsPath~cubeLog:96@txNo~35001201712290000251000350000000@branchSeq~@txCode~getRandom@sysCode~456@nodeId~access-gateway_01@sendOrgId~@orgId~@cost~@custId~@acctNo~@levelId~request@logType~gateway@appCode~04FA4828-63F5-44CC-8611-70E434371801@tabCode~04FA4828-63F5-44CC-8611-70E434371802@menuCode~04FA4828-63F5-44CC-8611-70E434371803@pageCode~04FA4828-63F5-44CC-8611-70E434371804@requestCode~04FA4828-63F5-44CC-8611-70E434371805@logKey~login@replaceType~image@replacePath~[imageInfo, videoInfo]@filters~@ 从客户端获得的上行报文：{\"$ACCESS_HEADER$\":{\"accessBrowser\":\"Chrome[56.0.2924.87]\",\"accessIp\":\"197.3.109.214:51122\",\"accessOs\":\"Windows 7\",\"accessTransId\":\"getRandom\",\"channelId\":\"cube_m\",\"domain\":\"350\",\"jnlNo\":\"35001201712290000251000350000000\",\"locale\":{\"country\":\"CN\",\"displayCountry\":\"China\",\"displayLanguage\":\"Chinese\",\"displayName\":\"Chinese (China)\",\"displayScript\":\"\",\"displayVariant\":\"\",\"extensionKeys\":[],\"iSO3Country\":\"CHN\",\"iSO3Language\":\"zho\",\"language\":\"zh\",\"script\":\"\",\"unicodeLocaleAttributes\":[],\"unicodeLocaleKeys\":[],\"variant\":\"\"},\"startTime\":1514516688454},\"$FF_HEADER$\":{\"appId\":\"456\",\"appVersion\":\"1.21\",\"device\":{\"isBreakOut\":\"0\",\"model\":\"iPhone\",\"osType\":\"01\",\"osVersion\":\"10.3\",\"uuid\":\"04FA4828-63F5-44CC-8611-70E43437180A\"},\"log\":{\"appCode\":\"04FA4828-63F5-44CC-8611-70E434371801\",\"logKey\":\"login\",\"menuCode\":\"04FA4828-63F5-44CC-8611-70E434371803\",\"pageCode\":\"04FA4828-63F5-44CC-8611-70E434371804\",\"replacePath\":[\"imageInfo\",\"videoInfo\"],\"replaceType\":\"image\",\"requestCode\":\"04FA4828-63F5-44CC-8611-70E434371805\",\"tabCode\":\"04FA4828-63F5-44CC-8611-70E434371802\"},\"net\":{\"ip\":\"195.216.160.72\"},\"reqSeq\":\"0\",\"securityVersion\":{\"algorithm\":\"3.2.1.1\",\"fido\":\"\",\"passGuard\":\"5.3.0.2\",\"scap\":\"3.3.3.4\",\"sensitive\":\"3.2.1.1\",\"swipeLock\":\"4.0.2.3\"},\"transId\":\"getRandom\"},\"ckey\":\"ILEDLFXLPWNHOYVJ\",\"header\":{\"domain\":\"350\",\"jnlNo\":\"35001201712290000251000350000000\"}}"

    val appCode = patternAppCode.findAllIn(str).mkString(",")
    println(appCode)
    val tabCode = patternTabCode.findAllIn(str).mkString(",")
    println(tabCode)
    val menuCode = patternMenuCode.findAllIn(str).mkString(",")
    println(menuCode)
    val pageCode = patternPageCode.findAllIn(str).mkString(",")
    println(pageCode)
    val requestCode = patternRequestCode.findAllIn(str).mkString(",")
    println(requestCode)
    val logKey = patternLogKey.findAllIn(str).mkString(",")
    println(logKey)
    val levelId = patternLevelId.findAllIn(str).mkString(",")
    println(levelId)
    val time = patternTime.findAllIn(str).mkString(",")
    println(time)

  }
}
