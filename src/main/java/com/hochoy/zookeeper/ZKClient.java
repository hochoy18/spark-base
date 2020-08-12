package com.hochoy.zookeeper;

import com.hochoy.utils.HochoyUtils;
import kafka.common.TopicAndPartition;
import kafka.utils.ZKGroupTopicDirs;
import kafka.utils.ZkUtils;
import org.apache.spark.streaming.kafka.OffsetRange;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import org.I0Itec.zkclient.ZkClient;

/**
 * zookeeper原生客户端
 */
public class ZKClient {
    static String path = "/zk";

    public static void main(String[] args) throws IOException, KeeperException, InterruptedException {
//        zookeeperTest();

        zkClient();
    }
    public static void zookeeperTest() throws IOException, KeeperException, InterruptedException {
        //由于连接zk需要时间，所以这里使用 countDownLatch
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        ZooKeeper client = new ZooKeeper("192.168.1.168:2181", 10000, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                if (Watcher.Event.KeeperState.SyncConnected.equals(event.getState())){
                    System.out.println("连接成功" + event);
                    countDownLatch.countDown();
                }
            }
        });
        if (ZooKeeper.States.CONNECTING.equals(client.getState())){
            System.out.println("连接中");
            countDownLatch.await();
        }

        Stat stat = new Stat();

        Stat exists = client.exists(path, new Watcher() {
            @Override
            public void process(WatchedEvent event) {
                System.out.println("exits==================> " + event);
                //System.out.printf("State : %s\ttype : %s\tpath : %s\tWrapper : %s%n ", event.getState(), event.getType(), event.getPath(), event.getWrapper());

            }
        });
        System.out.println("=========创建节点===========");

        if (exists == null){
            String str = client.create(path, "zk data".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            System.out.println("str ; " + str);
        }

        System.out.println("=============查看节点是否安装成功===============");
        System.out.println(new String(client.getData(path, false, stat)));

        HochoyUtils.sleep(4000);
        System.out.println("=========修改节点的数据==========");
        String data = "zNode2xxxxxxxxxxxxxxxxxxxxxxxxxxxxx===========================================";
        client.setData("/zk", data.getBytes(), -1);// -1 当版本号设置为-1时，忽略节点的版本号。

        System.out.println("========查看修改的节点是否成功=========");
        System.out.println(new String(client.getData(path, false, stat)));

        HochoyUtils.sleep(4000);
        System.out.println("=======删除节点==========");
        client.delete("/zk", -1); // -1 当版本号设置为-1时，忽略节点的版本号。



        System.out.println("==========查看节点是否被删除============");
        System.out.println("节点状态：" + client.exists(path, false));

//        client.getData(path, new Watcher() {
//            @Override
//            public void process(WatchedEvent event) {
//                if(Event.EventType.NodeDataChanged.equals(event.getType())){
//                    System.out.println("数据改变了");
//                }
//            }
//        }, stat);

        client.close();

    }


    public static void zkClient(){
        ZkClient zkClient = new ZkClient("192.168.1.168:2181");

        ZKGroupTopicDirs topicDirs = new ZKGroupTopicDirs("cobub3consumer", "cobub3_actions");
        String ownerDir = topicDirs.consumerOwnerDir();
        System.out.println(ownerDir);

        String zkTopicPath = topicDirs.consumerOffsetDir();
        System.out.println("groupid-zkTopic-path  --》   " +zkTopicPath );

        int  children = zkClient.countChildren(zkTopicPath);
        Map<TopicAndPartition,Long> fromOffsets = new HashMap<>();//Map[TopicAndPartition, Long] = Map()
        if (children > 0 ){

            for (int i = 0; i < children; i++) {
                Object o = zkClient.readData(zkTopicPath + "/" + i);
                TopicAndPartition tp = new  TopicAndPartition("cobub3_actions", i);
                fromOffsets.put(tp,Long.parseLong(o.toString()));
            }

            for (Map.Entry<TopicAndPartition, Long> entry : fromOffsets.entrySet()) {
                TopicAndPartition key = entry.getKey();
                Long value = entry.getValue();
                System.out.println(key + " ==================> " + value);
            }
        }

        List<OffsetRange> offsetRanges = new ArrayList<>();
//        for (OffsetRange o : offsetRanges) {
//
//            String  zkPath = topicDirs.consumerOffsetDir() + "/";
//            ZkUtils.updatePersistentPath(zkClient, zkPath,String.valueOf( o.untilOffset()));
//        }

    }
}