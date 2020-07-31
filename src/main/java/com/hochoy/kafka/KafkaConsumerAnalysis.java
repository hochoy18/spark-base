package com.hochoy.kafka;

import com.hochoy.utils.HochoyUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class KafkaConsumerAnalysis {
    static String TOPIC = "partitions3-topic";

    public static void main(String[] args) {

        for (int i = 0; i < 4; i++) {
            String consumerName = KafkaConsumerAnalysis.class.getSimpleName() + "------" + i;
            Thread t = new Thread(KafkaConsumerAnalysis::consumer, consumerName);
            t.start();
            HochoyUtils.sleep(3000);
        }

    }

    public static void consumer() {
        String threadName = Thread.currentThread().getName();

        Properties props = HochoyUtils.getProperties("consumer.properties");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);

        consumer.subscribe(Collections.singletonList(TOPIC));


        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(5));

            for (ConsumerRecord<String, String> record : records) {
                long offset = record.offset();
                int partition = record.partition();
                String value = record.value();
                String topic = record.topic();
                System.out.printf("threadName : %s  topic :  %s ,  partition : %d ,  offset : %d , value  :  %s%n", threadName, topic, partition, offset, value);
            }
        }


    }
}
