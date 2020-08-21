package com.hochoy.kafka;

import com.hochoy.utils.HochoyUtils;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

import java.sql.Connection;
import java.time.Duration;
import java.util.*;

public class KafkaConsumerAnalysis {
    static String TOPIC = "partitions3-topic";
    static Properties props = HochoyUtils.getProperties("consumer.properties");
    public static void main(String[] args) {

        for (int i = 0; i < 3; i++) {
            String consumerName = KafkaConsumerAnalysis.class.getSimpleName() + "------" + i;
            Thread t = new Thread(KafkaConsumerAnalysis::consumer, consumerName);
            t.start();
        }

    }

    public static void consumer() {
        Connection conn = HochoyUtils.getMySQLConn("mysql.properties");

        String threadName = Thread.currentThread().getName();

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        consumer.subscribe(Collections.singletonList(TOPIC), new ConsumerRebalanceListener() {
            // rebalance 之前将记录进行保存
            @Override
            public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
                for (TopicPartition partition : partitions) {
                    // 获取分区
                    int part = partition.partition();
                    // 对应分区的偏移量
                    long offset = consumer.position(partition);
                   int update =  HochoyUtils.update(
                            conn,
                            "update mysql_offset set fromoffset = ? where topic = ? and  groupid = ? and partitions = ? ",
                            Long.toString(offset),
                            TOPIC,
                            props.getProperty("group.id"),
                            Integer.toString(part));
                    System.out.println("onPartitionsRevoked >>>>    " + threadName + "   "+update);
                }

            }

            // rebalance之后读取之前的消费记录，继续消费
            @Override
            public void onPartitionsAssigned(Collection<TopicPartition> partitions) {

                for (TopicPartition partition : partitions) {
                    int part  = partition.partition();
                    Map<String, Object> offsetMap = HochoyUtils.findOne(
                            conn,
                            "select fromoffset from mysql_offset  where groupid =? and topic = ? and partitions = ? ",
                            props.getProperty("group.id"),
                            TOPIC,String.valueOf(part)
                    );
                    if (offsetMap.isEmpty()){
                        consumer.seekToEnd(Collections.singletonList(partition));
                    }else{
                        long fromoffset = Long.parseLong(offsetMap.get("fromoffset").toString());
                        System.out.println("partition = " + partition + "offset = " + offsetMap.get("fromoffset"));
                        // 定位到最近提交的offset位置继续消费
                        consumer.seek(partition, fromoffset);
                    }
                }

            }
        });


        try{
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(5));

                List<Offset> offsets = new ArrayList<>();
                for (ConsumerRecord<String, String> record : records) {
                    long offset = record.offset();
                    int partition = record.partition();
                    String value = record.value();
                    String topic = record.topic();
                    offsets.add(new Offset(offset,partition, props.getProperty("group.id"),TOPIC));

                    System.out.printf("threadName : %s  topic :  %s ,  partition : %d ,  offset : %d , value  :  %s%n", threadName, topic, partition, offset, value);
                }
                for (Offset offset : offsets) {
                   long count = HochoyUtils.getNums(
                            conn,
                            "select count(*) from mysql_offset where topic = ? and groupid = ? and partitions = ? ",
                            offset.getTopic(), offset.getGroupid(), String.valueOf(offset.getPartition())
                    );
                    int update;
                    if (count == 0){
                        update = HochoyUtils.update(
                                conn,
                                "insert into mysql_offset (topic,groupid,partitions,fromoffset) values ( ?,?,?,?)",
                                offset.getTopic(),offset.getGroupid(),offset.getPartition(),offset.getOffset());
                    }else {
                        update = HochoyUtils.update(
                                conn,
                                "update mysql_offset set fromoffset = ? where topic = ? and groupid = ? and partitions = ? ",
                                offset.getOffset(),offset.getTopic(), offset.getGroupid(), offset.getPartition());
                    }

                    System.out.println("commit >>>>>>>>>>   " + update);
                }
                consumer.commitAsync();
            }
        }finally {
            System.out.println("finally...");
            if (conn != null){
                HochoyUtils.close(conn);
            }
        }


    }
    static class Offset{
        private long offset;
        private int partition;
        private String groupid;
        private String topic;

        public Offset(long offset, int partition, String groupid, String topic) {
            this.offset = offset;
            this.partition = partition;
            this.groupid = groupid;
            this.topic = topic;
        }

        public long getOffset() {
            return offset;
        }

        public void setOffset(long offset) {
            this.offset = offset;
        }

        public int getPartition() {
            return partition;
        }

        public void setPartition(int partition) {
            this.partition = partition;
        }

        public String getGroupid() {
            return groupid;
        }

        public void setGroupid(String groupid) {
            this.groupid = groupid;
        }

        public String getTopic() {
            return topic;
        }

        public void setTopic(String topic) {
            this.topic = topic;
        }
    }
}

