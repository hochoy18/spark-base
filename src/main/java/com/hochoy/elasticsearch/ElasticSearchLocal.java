package com.hochoy.elasticsearch;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import shaded.parquet.org.slf4j.Logger;
import shaded.parquet.org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class ElasticSearchLocal {
    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchLocal.class);
    private static Random r = new Random();
    static int[] typeConstant = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
    static String[] roomTypeNameConstant = new String[]{"标准大床房", "标准小床房", "豪华大房", "主题情侣房间"};

    public static void main(String[] agre) throws Exception {
        //http://bj1.lc.data.sankuai.com/ test 80 online 9300
        // on startup
        //初始化client实列 连接本机的es 9300端口
        TransportClient client = TransportClient.builder().build()
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9300));
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            //上传数据第一个参数为索引，第二个为type，source是文本
            IndexResponse response = client.prepareIndex("hotel", "room").setSource(getEsDataString()).get();
        }
        logger.info(" run 1000 index consume time : " + (System.currentTimeMillis() - startTime));
    }

    public static XContentBuilder getEsDataString() throws Exception {
        SimpleDateFormat sp = new SimpleDateFormat("yyyy-MM-dd");
        Date d = new Date();
        int offset = r.nextInt(15);
        //es的原生api 提供json数据的转换 jsonBuilder.field(key,value).endObject();
        XContentBuilder object = XContentFactory.jsonBuilder()
                .startObject()
                .field("gmtCreate", (System.currentTimeMillis() - (864000008 * offset)) + "")
                .field("gmtModified", (System.currentTimeMillis() - (864000008 * offset)) + "")
                .field("sourceType", typeConstant[r.nextInt(10)] + "")
                .field("partnerId", r.nextInt(999999999) + "")
                .field("poiId", r.nextInt(999999999) + "")
                .field("roomType", r.nextInt(999999999) + "")
                .field("roomName", roomTypeNameConstant[r.nextInt(4)])
                .field("bizDay", r.nextInt(999999999) + "")
                .field("status", typeConstant[r.nextInt(10)] + "")
                .field("freeCount", r.nextInt(99999) + "")
                .field("soldPrice", r.nextInt(99999) + "")
                .field("marketPrice", r.nextInt(99999) + "")
                .field("ratePlanId", r.nextInt(99999) + "")
                .field("accessCode", r.nextInt(999999999) + "")
                .field("basePrice", r.nextInt(999999999) + "")
                .field("memPrice", r.nextInt(999999999) + "")
                .field("priceCheck", typeConstant[r.nextInt(10)] + "")
                .field("shardPart", typeConstant[r.nextInt(10)] + "")
                .field("sourceCode", typeConstant[r.nextInt(10)] + "")
                .field("realRoomType", r.nextInt(999999999) + "")
                .field("typeLimitValue", typeConstant[r.nextInt(10)] + "")
                .field("openInventoryByAccessCodeList", "")
                .field("closeInventoryByAccessCodeList", "")
                .field("openOrClose", "1")
                .field("openInventoryByAccessCodeListSize", r.nextInt(999999999) + "")
                .field("openInventoryByAccessCodeListIterator", r.nextInt(999999999) + "")
                .field("closeInventoryByAccessCodeListSize", r.nextInt(999999999) + "")
                .field("closeInventoryByAccessCodeListIterator", r.nextInt(999999999) + "")
                .field("datetime", sp.format(d))
                .endObject();
        return object;
    }
}
