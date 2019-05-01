package com.hochoy.design.pattern.stratege;


import javax.sql.DataSource;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.EnumMap;

/**
 * 假设有3种会员，分别为会员，超级会员以及金牌会员和普通顾客，针对不同类别的会员，有不同的打折方式，
 * 并且一个顾客每消费10000就增加一个级别
 * 以上四种会员分别采用原价（普通顾客），九折（会员），八折（超级会员）和七折（金牌会员）的折扣方式。
 */
public class Settlement {

    /**
     * 总价
     */
    private double totalPrice = 0;
    /**
     * 单次消费的金额
     */
    private double amount = 0;

    /**
     * 策略类
     */
    private ICallPriceStratege callPrice;

    /**
     * 购买的方法
     *
     * @param amount
     * @return double
     */
//    public double buy(double amount) throws Exception {
//        this.amount = amount;
//        this.totalPrice += this.amount;
//
//        callPrice = PriceFactory.getPriceFactory().getCall(amount);
//        return callPrice.callPrice(this.amount);
//    }
}


@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@interface PriceRegion  {
    int max() default Integer.MAX_VALUE;
    int min() default Integer.MIN_VALUE;
    DBType type() default DBType.DB2;

}

enum DBType {
//

        ORACLE("oracle....."),
        MYSQL("mysql....."),
        DB2("db2.....");

        private String type;

        DBType(String type) {
            this.type = type;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }
    }

 class T{
     public static void main(String[] args) throws Exception{
         System.out.println(DBType.ORACLE.getType());
         T t =new T();
         Thread.sleep(20 * 1000);
         ICallPriceStratege st = t.getUrl("SQLSERVER");
         Thread.sleep(20 * 1000);
         System.out.println(st.callPrice(20000));


     }
     EnumMap<DataBaseType,ICallPriceStratege> url = new EnumMap(DataBaseType.class);
     T (){
         url.put(DataBaseType.MYSQL,new OrdinaryPlayer());
         url.put(DataBaseType.ORACLE, new GoldMember());
         url.put(DataBaseType.DB2,new Member());
         url.put(DataBaseType.SQLSERVER,new SuperMember());
     }
     ICallPriceStratege getUrl(String  type){

         DataBaseType t = DataBaseType.valueOf(DataBaseType.class,type);
         return this.url.get(t);
     }
 }
 enum DataBaseType{
    MYSQL("mysql"),ORACLE("oracle"),DB2("db2"),SQLSERVER("sqlserver");
    private String type;

     public void setType(String type) {
         this.type = type;
     }

     public String getType() {
         return type;
     }

     DataBaseType(String type) {
         this.type = type;
     }
 }
