package com.hochoy.designpattern;

/**
 * Describe:
 *
 * @author hochoy <hochoy18@sina.com>
 * @version V1.0.0
 * @date 2019/4/4
 */
public class AdapterDemo {

    public static void main(String[] args) {
        PowerA a = new PowerAImp();
        PowerB b = new PowerBImp();

        start(a);


        // a 有自己的insert方法，可以直接调用自己的insert方法
        //a.insert();

        // 实现PowerA 调用 PowerB的方法,使用适配器调用PowerB的方法
        PowerAAdapter aAdapter = new PowerAAdapter(b);
//        aAdapter.insert();
        start(aAdapter);
    }

    public static void start(PowerA powerA) {
        System.out.println("....一些重复的代码.....");
        powerA.insert();
        System.out.println("....一些重复的代码.....\n");
    }

}

//PowerA want to use method of PowerB
class PowerAAdapter implements PowerA {

    PowerB powerB;

    public PowerAAdapter(PowerB powerB) {
        this.powerB = powerB;
    }

    public void insert() {
        powerB.connect();
    }
}


//https://www.cnblogs.com/smyhvae/p/3930271.html
interface PowerA {
    void insert();
}

class PowerAImp implements PowerA {
    @Override
    public void insert() {
        System.out.println("power A inserted ,work start.......");
    }
}

interface PowerB {
    void connect();
}

class PowerBImp implements PowerB {
    @Override
    public void connect() {
        System.out.println("power B connected ,work start.......");
    }
}



class Main{
    /**
     * 想让Target 调用到Adaptee 的 a()方法,加适配器
     *
     */

    public static void main(String[] args) {
        ClassAdapter adapter = new ClassAdapter();
        /** 客户可以通过适配器调用 {@link Adaptee#a() }
         * 也可以通过适配器调用自己的 t() {@link Target#t()}
         *
         */
        adapter.a();
        adapter.t();

        Adaptee adaptee = new Adaptee();
        ObjectAdapter objectAdapter = new ObjectAdapter(adaptee);
        objectAdapter.a();
    }
}

class ObjectAdapter{
    private Adaptee adaptee ;

    public ObjectAdapter(Adaptee adaptee) {
        this.adaptee = adaptee;
    }

    void a(){
        // 通过委派的adatpee 调用到了 Adaptee 的a() 方法
        adaptee.a();
    }


}



class ClassAdapter extends Adaptee implements Target {
    @Override
    void a() {
        super.a();
    }

    @Override
    public void t() {
        System.out.println("ClassAdapter implements Target#t()........");
    }
}

interface Target{
    void t();
}
class Adaptee{
    void a(){
        System.out.println("Adaptee#a() ........");
    }
}

//////////////////////////////////////////////////

class  Client{
    public static void main(String[] args) {
        SmallPort smallPort = new SmallPort() {
            @Override
            public void userSmallPort() {
                System.out.println("使用的是电脑的小口。。。。。。。。。。。。");
            }
        };
        BigPort bigPort = new PortAdapter(smallPort);
        /**
         * 目标是使用适配器调用小口方法
         */
        bigPort.userBigPort();
    }
}

/**
 * 适配器需要一个小口作为参数实现
 */
class PortAdapter implements BigPort{

    private SmallPort smallPort;

    public PortAdapter(SmallPort smallPort) {
        this.smallPort = smallPort;
    }

    @Override
    public void userBigPort() {
        smallPort.userSmallPort();
    }
}

/**
 * 投影仪支持的大口
 */
interface BigPort{
    void userBigPort();
}

/**
 * 电脑的小口
 */
interface SmallPort{
    void userSmallPort();
}

