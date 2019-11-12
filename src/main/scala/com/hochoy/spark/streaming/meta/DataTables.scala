package com.hochoy.spark.streaming.meta

/**
  * Created by IntelliJ IDEA.
  * Meta info of data tables in HBase.
  * Time:   6/9/15 7:04 AM
  *
  * @author jianghe.cao
  */
case class DataTables(nameSpace: String) extends Serializable {

  object Families extends Serializable {
    val data = "f"
    val index = "i"
  }

  object TestUserIdentifier extends Serializable {
    val name = s"$nameSpace:user_identifier_test"
  }

  object CobubUsers extends Serializable {
    val name = s"$nameSpace:cobub_users"
  }

  object UserIdentifier extends Serializable {
    val name = s"$nameSpace:user_identifier"
  }

  object DeviceIdentifier extends Serializable {
    val name = s"$nameSpace:device_identifier"
  }

  // Original tables
  object ClientData extends Serializable {
    val name = s"$nameSpace:clientdata"
  }


  object Event extends Serializable {
    val name = s"$nameSpace:event"
  }


  object UsingLog extends Serializable {
    val name = s"$nameSpace:usinglog"
  }


  object Error extends Serializable {
    val name = s"$nameSpace:error"
  }


  object Tag extends Serializable {
    val name = s"$nameSpace:tag"
  }


  object AppInfo extends Serializable {
    val name = s"$nameSpace:appinfo"
  }


  // Constant tables
  object ProductChannel extends Serializable {
    val name = s"$nameSpace:product_channel"
  }


  // Internal tables
  object ProductDevice extends Serializable {
    val name = s"$nameSpace:F_Product_Device"
  }


  object ActiveDevice extends Serializable {
    val name = s"$nameSpace:active_device"
  }


  object EventDevice extends Serializable {
    val name = s"$nameSpace:event_device"
  }

  // Internal tables
  object APPKEY extends Serializable {
    val name = s"$nameSpace:appkey"
  }

  // Result tables
  object SumStatistic extends Serializable {
    val name = s"$nameSpace:Sum_Statistic"
  }


  object SumEvent extends Serializable {
    val name = s"$nameSpace:sumevent"
  }


  object AccessLevel extends Serializable {
    val name = s"$nameSpace:accesslevel"
  }


  object DimDurationSegment extends Serializable {
    val name = s"$nameSpace:dimdurationsegment"
  }


  object DimSessionSegment extends Serializable {
    val name = s"$nameSpace:dimsessionsegment"
  }


  object DurationProduct extends Serializable {
    val name = s"$nameSpace:durationproduct"
  }
  object DurationVersion extends Serializable {
    val name = s"$nameSpace:durationversion"
  }

  object EventReponseDuration extends Serializable {
    val name = s"$nameSpace:event_response_duration"
  }

  object UserOnlineRealtime extends Serializable {
    val name = s"$nameSpace:user_online_realtime"
  }

  object UserOnlineResult extends Serializable {
    val name = s"$nameSpace:user_online_result"
  }

  object UserOnlineResultTmp extends Serializable {
    val name = s"$nameSpace:user_online_result_tmp"
  }
  object UserOnlineResult2 extends Serializable {
    val name = s"$nameSpace:user_online_result2"
  }


  object DurationChannel extends Serializable {
    val name = s"$nameSpace:durationchannel"

    override def toString = name

    def getBytes = name.getBytes
  }


  object FreqProduct extends Serializable {
    val name = s"$nameSpace:frequencyproduct"
  }


  object FreqVersion extends Serializable {
    val name = s"$nameSpace:frequencyversion"
  }


  object FreqChannel extends Serializable {
    val name = s"$nameSpace:frequencychannel"

    override def toString = name

    def getBytes = name.getBytes
  }


  object Device extends Serializable {
    private val name = s"$nameSpace:data.device"

    override lazy val toString: String = name

    def apply(): String = name

    def getBytes = name.getBytes
  }

  object Funnel extends DataTable {
    val f = "f"

    override val families = Array(f.getBytes)

    override protected val name = s"$nameSpace:res.funnel"
  }


  trait DataTable extends Serializable {

    protected val name: String

    override def toString: String = name

    def bytes: Array[Byte] = toString.getBytes

    val families: Array[Array[Byte]]

  }
  object CobubRetentionReport extends Serializable {
    val name = s"$nameSpace:cobub_retention_report"
  }
  object CobubActionReport extends Serializable {
    val name = s"$nameSpace:cobub_action_report"
  }

}
