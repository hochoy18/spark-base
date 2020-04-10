package com.hochoy.kafka;

import com.hochoy.utils.HochoyConstants;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;


public class KafkaProducerAnalysis {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerAnalysis.class);





    public static Properties initConfig(){
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, HochoyConstants.KafkaConstants.BROKER_LIST);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.CLIENT_ID_CONFIG,"producer.client.id.demo");
        return props;

    }

    public static void main(String[] args) {

        Properties props = initConfig();
        Random random = new Random();
        KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        int j = 0;
        while (j < 200){
            j++;
            int i = random.nextInt();
            ProducerRecord<String, String> record = new ProducerRecord<>(HochoyConstants.KafkaConstants.TOPIC, random.nextInt(3), i+"", "{\"data\":[{\"country\":\"局域网\",\"request\":\"POST /action HTTP/1.1\",\"productid\":\"1\",\"city\":\"\",\"latitude\":\"31.1716369000\",\"ip\":\"192.168.1.243\",\"useragent\":\"Python-urllib/2.7\",\"servertime\":\"2020-03-04 18:30:03.000\",\"sessionid\":\"57c3b7ac4e5f4b22b710b3d06ee9faff\",\"lib_version\":\"1.0.0\",\"userid\":\"20000178769\",\"deviceid\":\"iOS_DEVICEID_4362806746292507\",\"uuid\":\"e6bc377f84c450767e2730e0a70653f1\",\"actionattach\":{},\"action\":\"$exitPage\",\"appkey\":\"23924869c35a4108b2539412fdf9c6f8\",\"clienttime\":\"2020-03-03 18:30:03.971\",\"region\":\"局域网\",\"properties\":{\"screen_width\":136,\"utm_campaign\":\"国庆节大促\",\"utm_medium\":\"TopBanner\",\"language\":\"lt_LT\",\"version\":\"2.1\",\"platform\":\"ios\",\"network\":\"WIFI\",\"manufacturer\":\"apple\",\"duration\":80087,\"screen_height\":1024,\"mccmnc\":\"46003\",\"is_new_device\":\"false\",\"refer\":\"\",\"is_update\":\"false\",\"model\":\"iPad4,1\",\"pagetitle\":\"PageTitle_897156978712\",\"page\":\"RegistviewController\",\"osversion\":\"3.1.2\",\"channelid\":\"baidu\",\"utm_content\":\"0815-method\",\"utm_source\":\"百度\"},\"longitude\":\"121.3368797000\"}]}");
            Future<RecordMetadata> future = producer.send(record, new Callback() {
                @Override
                public void onCompletion(RecordMetadata metadata, Exception exception) {
                    if (exception != null){
                        exception.printStackTrace();
                    }else {
                        System.out.printf("topic : %s%n partition : %s%n offset : %s%n key : %s%n value : %s%n------------------------------------------------",
                                metadata.topic() ,
                                metadata.partition() ,
                                metadata.offset(),
                                record.key(),
                                record.value());
                    }
                }
            });
            try {
                RecordMetadata metadata = future.get();
                String topic = metadata.topic();
                int partition = metadata.partition();
                long offset = metadata.offset();
                System.out.println(topic);
                System.out.println(partition);
                System.out.println(offset);
                System.out.println("--------------------");
                Thread.sleep(1000);
            }catch (InterruptedException| ExecutionException e ){
                logger.error("e:{}",e);
            }finally {
                producer.flush();
            }


        }

    }


}
