//package com.hochoy.cobub3_test;
//
//
//import org.apache.hadoop.hbase.util.Bytes;
//import org.apache.hadoop.io.IntWritable;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.security.MessageDigest;
//import java.text.ParseException;
//import java.util.Calendar;
//import java.util.Date;
//import java.util.UUID;
//
///**
// * Date and time formatters.
// *
// * @author jianghe.cao
// */
//public class Formatter {
//  private Formatter() { }
//
//  private static final Logger LOGGER = LoggerFactory.getLogger(Formatter.class);
//  private static final String ERROR = "An error was caught";
//  private static final String YMD = "yyyyMMdd";
//  /**
//   * Convert a hex string to byte array.
//   *
//   * @param s The string to be converted.
//   * @return The byte array converted from s.
//   */
//  public static byte[] hexStringToByteArray(String s) {
//    int len = s.length();
//    byte[] data = new byte[len / 2];
//    for (int i = 0; i < len; i += 2) {
//      data[i / 2] =
//        (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
//    }
//    return data;
//  }
//
//  /**
//   * Generate row key for original HTables
//   *
//   * @return rowkey format: UUID-yyyyMMddHH
//   * @throws Exception
//   */
//  public static byte[] genRowKey(MessageDigest md, String dateHour) {
//    String uuid = UUID.randomUUID().toString().replace("-", "");
//    String millis = String.valueOf(System.currentTimeMillis());
//    String prefix = SparkJobsOptimizeUtil.sjmd5Hashing(md, millis).substring(0, 3);
//    String rowKey = stringConnector("", prefix, uuid, "-", dateHour);
//    return Bytes.toBytes(rowKey);
//  }
//
//
//  /**
//   * Generate row key for HTable "appinfo".
//   *
//   * @param productID
//   * @param deviceID
//   * @param appName
//   * @return Row key generated based on {@code productID}, {@code deviceID} and
//   * {@code appName}. Format: md5-productid-deviceid-appname
//   */
//  public static byte[] genAppInfoRowKey(MessageDigest md, String productID, String deviceID, String appName) {
//    String body = stringConnector("-", productID, deviceID, appName);
//    String millis = String.valueOf(System.currentTimeMillis());
//    String prefix = SparkJobsOptimizeUtil.sjmd5Hashing(md, millis).substring(0, 3);
//    return Bytes.toBytes(prefix + "-" + body);
//  }
//
//  /**
//   * Generate row key with md5 prefix for HTable.
//   *
//   * @param body
//   * @return Result in format: md5(<code>body</code>).substring(0,
//   * <code>prefixLen</code>)-body
//   */
//  public static String genMD5RowKey(MessageDigest md, String conn, String body, int prefixLen) {
//    String prefix = SparkJobsOptimizeUtil.sjmd5Hashing(md, body).substring(0, prefixLen);
//    return stringConnector(conn, prefix, body);
//  }
//
//  /**
//   * Change byte[] to date string with short format (yyyyMMdd)
//   *
//   * @param byteDate byte[] date with format: "yyyy-MM-dd HH:mm:ss".
//   * @return Day of date: yyyyMMdd. If byteDate is null or ParseException is caught, return null.
//   */
//  private static ThreadSafeSDF formatshort = new ThreadSafeSDF(YMD);
//  private static ThreadSafeSDF formatlong = new ThreadSafeSDF("yyyy-MM-dd HH:mm:ss");
//  public static String bytes2DateDay(byte[] byteDate) {
//    if (byteDate == null) {
//      return null;
//    }
//    try {
//      String strDate = new String(byteDate);
//      Date date = null;
//      date = formatlong.parse(strDate);
//      return formatshort.format(date);
//    } catch (ParseException e) {
//      return null;
//    }
//
//  }
//
//  /**
//   * Convert bytes to {@code IntWritable}.
//   *
//   * @param byteInt Integer in the form of bytes.
//   * @return
//   */
//  public static IntWritable bytes2IntWritable(byte[] byteInt) {
//    if (byteInt == null) {
//      return new IntWritable(0);
//    }
//
//    String byteStr = Bytes.toString(byteInt);
//
//    if ("".equals(byteStr)) {
//      return new IntWritable(Integer.valueOf(0));
//    }
//
//    return new IntWritable(Integer.valueOf(byteStr));
//  }
//
//  /**
//   * Connect multiple strings with specific string connector.
//   *
//   * @param conn    String connector.
//   * @param collect Strings separated by comma or String[].
//   * @return The combined result.
//   */
//  public static String stringConnector(String conn, String... collect) {
//    StringBuilder sb = new StringBuilder(100);
//    for (String str : collect) {
//      sb.append(str).append(conn);
//    }
//    for (int i = 0; i < conn.length(); i++) {
//      sb.deleteCharAt(sb.length() - 1);
//    }
//    return sb.toString();
//  }
//
//
//
//  /**
//   * Get tomorrow string according to {@code currentDay}.
//   *
//   * @param currentDay Current day string: yyyyMMdd.
//   * @return Tomorrow string: yyyyMMdd.
//   * @throws ParseException
//   */
//  public static String getNextDay(String currentDay) throws ParseException {
//    Date tdate = null;
//    Calendar cal = Calendar.getInstance();
//    ThreadSafeSDF format = new ThreadSafeSDF(YMD);
//    try {
//      tdate = format.parse(currentDay);
//
//      cal.setTime(tdate);
//      cal.add(Calendar.DATE, 1);
//    } catch (ParseException e) {
//      LOGGER.error(ERROR, e);
//    }
//
//    return format.format(cal.getTime());
//  }
//
//  /**
//   * Get one hour before current time for later start time. Output format:
//   * yyyyMMddHH
//   *
//   * @param input
//   * @return
//   */
//  public static String getPrevHour(String input) {
//
//    Calendar cal = Calendar.getInstance();
//    Date date = null;
//    ThreadSafeSDF format = new ThreadSafeSDF("yyyyMMddHH");
//    try {
//      date = format.parse(input);
//    } catch (ParseException e) {
//      LOGGER.error("ParseException was caught", e);
//    }
//
//    cal.setTime(date);
//
//    cal.add(Calendar.HOUR_OF_DAY, -1);
//    return format.format(cal.getTime());
//
//  }
//
//
//
//
///**
//   * Get one hour before current time for later start time. Output format:
//   * yyyyMMddHH
//   *
//   * @param input
//   * @return
//   */
//  public static String getNextHour(String input) {
//
//    Calendar cal = Calendar.getInstance();
//    Date date = null;
//    ThreadSafeSDF format = new ThreadSafeSDF("yyyyMMddHH");
//    try {
//      date = format.parse(input);
//    } catch (ParseException e) {
//      LOGGER.error("ParseException was caught", e);
//    }
//
//    cal.setTime(date);
//
//    cal.add(Calendar.HOUR_OF_DAY, 1);
//    return format.format(cal.getTime());
//
//  }
//  /**
//   * Get previous day in format: yyyyMMdd.
//   *
//   * @param curDayStr
//   * @return
//   */
//  public static String getPrevDay(String curDayStr) {
//    String prevDayStr = null;
//    ThreadSafeSDF format = new ThreadSafeSDF(YMD);
//    Date date = null;
//    try {
//      date = format.parse(curDayStr);
//      Calendar cal = Calendar.getInstance();
//      cal.setTime(date);
//      cal.add(Calendar.DATE, -1);
//      prevDayStr = format.format(cal.getTime());
//    } catch (ParseException e) {
//      LOGGER.error(ERROR, e);
//    }
//    return prevDayStr;
//  }
//
//  /**
//   * Generate prefix of row key for HTables: *_Product_Device.
//   *
//   * @param key
//   * @param length
//   * @return
//   */
//  public static String genPrefix(MessageDigest md, String key, int length) {
//    String rowKey = null;
//    String md5Key;
//    try {
//      md5Key = SparkJobsOptimizeUtil.sjmd5Hashing(md, key);
//      rowKey = md5Key.substring(0, length - 1) + "_" + key;
//    } catch (Exception e) {
//      LOGGER.error(ERROR, e);
//    }
//    return rowKey;
//  }
//
//  /**
//   * Get the last day in the month which date specified.
//   *
//   * @param date
//   * @return The last day in the month which date specified.
//   */
//  public static int getMonthDays(Date date) {
//    Calendar c = Calendar.getInstance();
//    c.setTime(date);
//    return c.getActualMaximum(Calendar.DAY_OF_MONTH);
//  }
//
//  /**
//   * Get previous month in specific format.
//   *
//   * @param dateDay Date format: yyyyMMdd
//   * @param fmtOut  Date format of result.
//   * @return Previous month in format of <code>fmtOut</code>.
//   * @throws ParseException
//   */
//  public static String getPrevMonth(String dateDay, String fmtOut) throws ParseException {
//    final ThreadSafeSDF sdfIn = new ThreadSafeSDF(YMD);
//    final ThreadSafeSDF sdfOut = new ThreadSafeSDF(fmtOut);
//    Calendar cal = Calendar.getInstance();
//    cal.setTime(sdfIn.parse(dateDay));
//    cal.add(Calendar.MONTH, -1);
//    return sdfOut.format(cal.getTime());
//  }
//
//  /**
//   * Compares two version strings.
//   * <p/>
//   * Use this instead of String.compareTo() for a non-lexicographical comparison
//   * that works for version strings. e.g. "1.10".compareTo("1.6").
//   *
//   * @param version1 a string of ordinal numbers separated by decimal points.
//   * @param version2 a string of ordinal numbers separated by decimal points.
//   * @return The result is a negative integer if version1 is _numerically_ less
//   * than version2. The result is a positive integer if version1 is
//   * _numerically_ greater than version2. The result is zero if the
//   * strings are _numerically_ equal.
//   * @note It does not work if "1.10" is supposed to be equal to "1.10.0".
//   */
//  public static Integer versionCompare(String version1, String version2) {
//    String ver1 = version1.replaceAll("[^0-9.]", "");
//    String ver2 = version2.replaceAll("[^0-9.]", "");
//    String[] vals1 = ver1.split("\\.");
//    String[] vals2 = ver2.split("\\.");
//    int i = 0;
//    // set index to first non-equal ordinal or length of shortest version string
//    while (i < vals1.length && i < vals2.length && vals1[i].equals(vals2[i])) {
//      i++;
//    }
//    // compare first non-equal ordinal number
//    if (i < vals1.length && i < vals2.length) {
//      int diff = Integer.valueOf(vals1[i]).compareTo(Integer.valueOf(vals2[i]));
//      return Integer.signum(diff);
//    } else {
//      return Integer.signum(vals1.length - vals2.length);
//    }
//  }
//  public static void main(String[] args) {
//
//
//    String deviceRkBody = "10037873_c743142d148f09d96ad14675adc12e49460fad92";
//
//    String deviceRk = "";
//    try{
//      String tmpPrefixStr = Formatter.genPrefix(MessageDigest.getInstance("MD5"), deviceRkBody, 4);
//      if (tmpPrefixStr != null && tmpPrefixStr.trim().length() >= 3){
//        deviceRk = tmpPrefixStr.substring(0, 3) + "_" + deviceRkBody;
//      }
//    }catch (Exception e){
//      throw new NullPointerException(e.getMessage());
//    }
//    LOGGER.info(deviceRkBody);
//    LOGGER.info(deviceRk);
//
//
//  }
//}
