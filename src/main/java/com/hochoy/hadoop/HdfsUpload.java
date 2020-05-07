package com.hochoy.hadoop;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class HdfsUpload {

    public static void main(String[] args) throws Exception{
        System.setProperty("HADOOP_USER_NAME", "ubas");

        String file = "C:\\Users\\COBUB\\Desktop\\explain.txt";
        new HdfsUpload().hdfsUpload(file);
    }
    public void hdfsUpload(String srcPath) throws IOException, URISyntaxException {
        Configuration conf = new Configuration();
        FileSystem fs = null;
//        conf.set("hadoop.security.authentication", "kerberos");
//        String userName = "hochoy";//   kerberos 认证的username，可配置在配置文件中        Connection2hbase.getHbaseConfig().get("kerberos.username");
//        String keytab = "/usr/lib/hochoy.keytab";//     kerberos 认证的keytab，配置在配置文件中，存放于具体目录     Connection2hbase.getHbaseConfig().get("kerberos.keytab");
        URI urlHdfs = new URI("hdfs://nameservice1:8020");
        String url17monipdb = "/user/ubas/test";

//        UserGroupInformation.setConfiguration(conf);
//        UserGroupInformation.loginUserFromKeytab(userName,keytab);   //kerberos 认证
        fs = FileSystem.get(urlHdfs,conf);
//        if (fs.exists(new Path(url17monipdb + "/17monipdb.dat"))){
//            //rename 及linux中的cp ，文件拷贝
//            fs.rename(new Path(url17monipdb + "/17monipdb.dat"),new Path(url17monipdb + "/17monipdb.dat"+".bak"+new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date())));
//        }
        //调用API上传文件
        fs.copyFromLocalFile(false,true,new Path(srcPath),new Path(url17monipdb+"/17monipdb.dat"));

        fs.close();
    }

}
