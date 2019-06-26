package com.hochoy.cobub3_test;


import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class OptimizeUtil {
    /**
     * Optimize mapreduce job which read and write hbase table 
     * @param conf
     * @param scan
     * @param job
     */
    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(OptimizeUtil.class);

    private OptimizeUtil() {
    }

    /**
     * md5hash字符串
     * @param md
     * @param str
     * @return
     */
    public static String md5Hashing(MessageDigest md, String str) {
        MessageDigest md5 = md;
        if (md5 != null) {
            md5.reset();
        } else {
            try {
                 md5 = MessageDigest.getInstance("MD5");
            } catch (NoSuchAlgorithmException e) {
                LOGGER.error("An Exception was caught :"+e);
                return null;
            } 
        }

        md5.update(str.getBytes());
        byte[] byteData = md5.digest();
        // convert the byte to hex format method 1
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16)
                    .substring(1));
        }
        return sb.toString();
    }

}
