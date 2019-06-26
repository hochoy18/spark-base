package com.hochoy.cobub3_test;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.security.UserGroupInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/** 
 * @author zhouli
 * Version 1.0
 */
public class KerberosUtil {
    private KerberosUtil() {
    }

    private static Logger logger = LoggerFactory.getLogger(KerberosUtil.class);
    
    public static boolean kinit(Configuration hbaseConfig,
                                String userName, String keyTabFile) {
        boolean flag = true;
        hbaseConfig.set("hadoop.security.authentication", "kerberos");
        UserGroupInformation.setConfiguration(hbaseConfig);
        try {
            UserGroupInformation.loginUserFromKeytab(userName,keyTabFile);
            logger.info("Connected to hbase with kerberos:" + userName + "," + keyTabFile);
        } catch (IOException e) {
            logger.error("kerberos init error", e);
            flag = false;
        }
        return flag;
    }
    
    public static boolean kinit(Configuration conf) {

        boolean flag = false;
        String kerberosEnabled = conf.get("kerberos.enable");
        //Needn't kinit
        if (kerberosEnabled == null || "false".equalsIgnoreCase(kerberosEnabled)) {
            flag = true;
        } else {
            String name =  conf.get("kerberos.username");
            String keytab =  conf.get("kerberos.keytab");
            flag = kinit(conf,name,keytab);
        }
        return flag;
    }
}
