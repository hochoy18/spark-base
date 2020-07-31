package com.hochoy.kafka;

import com.hochoy.utils.HochoyUtils;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class KafkaProducerAnalysis {


    static int MSG_SIZE = (int)1e4;
    static String TOPIC = "partitions3-topic";

    public static void main(String[] args) {

        Properties props = HochoyUtils.getProperties("producer.properties");

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);

        String value = "{\"data\":[{\"country\":\"局域网\",\"request\":\"POST /action HTTP/1.1\",\"productid\":\"1\",\"city\":\"\",\"latitude\":\"31.1716369000\",\"ip\":\"192.168.1.243\",\"useragent\":\"Python-urllib/2.7\",\"servertime\":\"2020-03-04 18:30:03.000\",\"sessionid\":\"57c3b7ac4e5f4b22b710b3d06ee9faff\",\"lib_version\":\"1.0.0\",\"userid\":\"20000178769\",\"deviceid\":\"iOS_DEVICEID_4362806746292507\",\"uuid\":\"e6bc377f84c450767e2730e0a70653f1\",\"actionattach\":{},\"action\":\"$exitPage\",\"appkey\":\"23924869c35a4108b2539412fdf9c6f8\",\"clienttime\":\"2020-03-03 18:30:03.971\",\"region\":\"局域网\",\"properties\":{\"screen_width\":136,\"utm_campaign\":\"国庆节大促\",\"utm_medium\":\"TopBanner\",\"language\":\"lt_LT\",\"version\":\"2.1\",\"platform\":\"ios\",\"network\":\"WIFI\",\"manufacturer\":\"apple\",\"duration\":80087,\"screen_height\":1024,\"mccmnc\":\"46003\",\"is_new_device\":\"false\",\"refer\":\"\",\"is_update\":\"false\",\"model\":\"iPad4,1\",\"pagetitle\":\"PageTitle_897156978712\",\"page\":\"RegistviewController\",\"osversion\":\"3.1.2\",\"channelid\":\"baidu\",\"utm_content\":\"0815-method\",\"utm_source\":\"百度\"},\"longitude\":\"121.3368797000\"}]}";

        for (int i = 0 ; i < MSG_SIZE; i ++ ){

//            ProducerRecord<String, String> record = new ProducerRecord<>( TOPIC, random.nextInt(3), i+"", "{\"data\":[{\"country\":\"局域网\",\"request\":\"POST /action HTTP/1.1\",\"productid\":\"1\",\"city\":\"\",\"latitude\":\"31.1716369000\",\"ip\":\"192.168.1.243\",\"useragent\":\"Python-urllib/2.7\",\"servertime\":\"2020-03-04 18:30:03.000\",\"sessionid\":\"57c3b7ac4e5f4b22b710b3d06ee9faff\",\"lib_version\":\"1.0.0\",\"userid\":\"20000178769\",\"deviceid\":\"iOS_DEVICEID_4362806746292507\",\"uuid\":\"e6bc377f84c450767e2730e0a70653f1\",\"actionattach\":{},\"action\":\"$exitPage\",\"appkey\":\"23924869c35a4108b2539412fdf9c6f8\",\"clienttime\":\"2020-03-03 18:30:03.971\",\"region\":\"局域网\",\"properties\":{\"screen_width\":136,\"utm_campaign\":\"国庆节大促\",\"utm_medium\":\"TopBanner\",\"language\":\"lt_LT\",\"version\":\"2.1\",\"platform\":\"ios\",\"network\":\"WIFI\",\"manufacturer\":\"apple\",\"duration\":80087,\"screen_height\":1024,\"mccmnc\":\"46003\",\"is_new_device\":\"false\",\"refer\":\"\",\"is_update\":\"false\",\"model\":\"iPad4,1\",\"pagetitle\":\"PageTitle_897156978712\",\"page\":\"RegistviewController\",\"osversion\":\"3.1.2\",\"channelid\":\"baidu\",\"utm_content\":\"0815-method\",\"utm_source\":\"百度\"},\"longitude\":\"121.3368797000\"}]}");

            value = "kkkkkkkkkkkk" + i;
            System.out.println(value);
            ProducerRecord<String, String> record = new ProducerRecord<>(TOPIC,value);
            Future<RecordMetadata> future = producer.send(record, (metadata, exception) -> {
                if (exception == null) {
                    System.out.println("Msg send success !!!! ");
                    long offset = metadata.offset();
                    int partition = metadata.partition();
                    System.out.printf("partition : %d , offset : %d  %n", partition, offset);
                } else {
                    exception.printStackTrace();
                }

            });
            RecordMetadata recordMetadata = null;
            try {
                recordMetadata = future.get();
                long offset = recordMetadata.offset();
                System.out.println("=========================="+ offset);
//                if (i % 1000 == 0){
                    HochoyUtils.sleep(500);
//                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }



        }
//        try {
//            countDownLatch.await();
//
//        }catch (InterruptedException e ){
//            e.printStackTrace();
//        }


//        int j = 0;
//        while (j < 1e6){
//            j++;
//            int i = random.nextInt();
//            ProducerRecord<String, String> record = new ProducerRecord<>(HochoyConstants.KafkaConstants.TOPIC, random.nextInt(3), i+"", "{\"data\":[{\"country\":\"局域网\",\"request\":\"POST /action HTTP/1.1\",\"productid\":\"1\",\"city\":\"\",\"latitude\":\"31.1716369000\",\"ip\":\"192.168.1.243\",\"useragent\":\"Python-urllib/2.7\",\"servertime\":\"2020-03-04 18:30:03.000\",\"sessionid\":\"57c3b7ac4e5f4b22b710b3d06ee9faff\",\"lib_version\":\"1.0.0\",\"userid\":\"20000178769\",\"deviceid\":\"iOS_DEVICEID_4362806746292507\",\"uuid\":\"e6bc377f84c450767e2730e0a70653f1\",\"actionattach\":{},\"action\":\"$exitPage\",\"appkey\":\"23924869c35a4108b2539412fdf9c6f8\",\"clienttime\":\"2020-03-03 18:30:03.971\",\"region\":\"局域网\",\"properties\":{\"screen_width\":136,\"utm_campaign\":\"国庆节大促\",\"utm_medium\":\"TopBanner\",\"language\":\"lt_LT\",\"version\":\"2.1\",\"platform\":\"ios\",\"network\":\"WIFI\",\"manufacturer\":\"apple\",\"duration\":80087,\"screen_height\":1024,\"mccmnc\":\"46003\",\"is_new_device\":\"false\",\"refer\":\"\",\"is_update\":\"false\",\"model\":\"iPad4,1\",\"pagetitle\":\"PageTitle_897156978712\",\"page\":\"RegistviewController\",\"osversion\":\"3.1.2\",\"channelid\":\"baidu\",\"utm_content\":\"0815-method\",\"utm_source\":\"百度\"},\"longitude\":\"121.3368797000\"}]}");
//            Future<RecordMetadata> future = producer.send(record, new Callback() {
//                @Override
//                public void onCompletion(RecordMetadata metadata, Exception exception) {
//                    if (exception != null){
//                        exception.printStackTrace();
//                    }else {
//                        System.out.printf("topic : %s%n partition : %s%n offset : %s%n key : %s%n value : %s%n------------------------------------------------",
//                                metadata.topic() ,
//                                metadata.partition() ,
//                                metadata.offset(),
//                                record.key(),
//                                record.value());
//                    }
//                }
//            });
//            try {
//                RecordMetadata metadata = future.get();
//                String topic = metadata.topic();
//                int partition = metadata.partition();
//                long offset = metadata.offset();
//                System.out.println(topic);
//                System.out.println(partition);
//                System.out.println(offset);
//                System.out.println("--------------------");
////                Thread.sleep(1000 / 100);
//            }catch (InterruptedException| ExecutionException e ){
//                logger.error("e:{}",e);
//            }finally {
//                producer.flush();
//            }
//
//
//        }

    }


 }


















