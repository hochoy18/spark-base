package com.hochoy.concurrent;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Describe:
 *
 * @author hochoy <hochoy18@sina.com>
 * @version V1.0.0
 * @date 2019/3/28
 */
abstract  public class BaseHealthChecker implements Runnable {
    private CountDownLatch latch;
    private String serviceName;
    private boolean serviceUp;

    public BaseHealthChecker(CountDownLatch latch, String serviceName ) {
        super();
        this.latch = latch;
        this.serviceName = serviceName;
    }

    public void run() {
        try {
            verifyService();
            serviceUp = true;
        } catch (Throwable t) {
            t.printStackTrace();
            serviceUp = false;
        } finally {
            if (latch != null) {
                latch.countDown();
            }
        }
    }


    public abstract void verifyService();

    public CountDownLatch getLatch() {
        return latch;
    }

    public String getServiceName() {
        return serviceName;
    }

    public boolean isServiceUp() {
        return serviceUp;
    }


}
class NetworkHealthChecker extends BaseHealthChecker{
    public NetworkHealthChecker(CountDownLatch latch) {
        super(latch, "NetworkHealthChecker..........");
    }

    @Override
    public void verifyService() {
        System.out.println("checking " + this.getServiceName());
        try {
            Thread.sleep(10 * 1000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        System.out.println(this.getServiceName() + " is up ");
    }
}
class DatabaseHealthChecker extends BaseHealthChecker{
    public DatabaseHealthChecker(CountDownLatch latch) {
        super(latch, "DatabaseHealthChecker<<<<<<<<<<..........");
    }

    @Override
    public void verifyService() {
        System.out.println("checking " + this.getServiceName());
        try {
            Thread.sleep(3 * 1000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        System.out.println(this.getServiceName() + " is up ");
    }
}

class CacheHealthChecker extends BaseHealthChecker{
    public CacheHealthChecker(CountDownLatch latch) {
        super(latch, "CacheHealthChecker||||||||..........");
    }

    @Override
    public void verifyService() {
        System.out.println("checking " + this.getServiceName());
        try {
            Thread.sleep(7 * 1000);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        System.out.println(this.getServiceName() + " is up ");
    }
}
class ApplicationStartupUtil{
    private static List<BaseHealthChecker> services;
    private static CountDownLatch latch;

    private ApplicationStartupUtil() {
    }
    private final static ApplicationStartupUtil INSTANCE = new ApplicationStartupUtil();

    public static ApplicationStartupUtil getInstance(){
        return INSTANCE;
    }
    public static boolean checkExternalServices() throws Exception{
        latch = new CountDownLatch(3);
        services = Arrays.asList(
                new NetworkHealthChecker(latch),
                new CacheHealthChecker(latch),
                new DatabaseHealthChecker(latch),new DatabaseHealthChecker(latch)
        );
        final Executor executor = Executors.newFixedThreadPool(services.size());

        services.forEach(x -> executor.execute(x));
        latch.await();

        for (final BaseHealthChecker v:services) {
            if (!v.isServiceUp()){
                return false;
            }
        }
        return true;


    }
}
class Main{
    public static void main(String[] args) {
        boolean result = false;
        try {
            result = ApplicationStartupUtil.checkExternalServices();
        }catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("8888********+"+ result);
    }
}
