package com.hochoy.design.pattern.stratege;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Describe:
 *
 * @author hochoy <hochoy18@sina.com>
 * @version V1.0.0
 * @date 2019/4/26
 */
public class PriceFactory {
    static final String PACKAGE =
            PriceFactory.class.getPackage().getName();


    List<Class<? extends ICallPriceStratege>> list = new ArrayList<>(    );

    public PriceFactory(){
        try {
            getList();
        }catch (Exception e){

        }
    }

    private static ICallPriceStratege accept(Class<? extends ICallPriceStratege> clazz) {
        PriceRegion pr = clazz.getAnnotation(PriceRegion.class);

        if (pr.max() == 0) {
            try {
                ICallPriceStratege iCallPriceStratege = clazz.newInstance();
                return iCallPriceStratege;
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    List<Class<? extends ICallPriceStratege>> getList(){
        String path = System.getProperty("user.dir")+ File.separator + "src" +
                File.separator + "main" + File.separator + "java" + File.separator + ""
                + PACKAGE.replace(".", File.separator) + File.separator;
        File file = new File(path);
        String[] files = file.list();
        for (int i = 0; i < files.length; i++) {
            System.out.println("........"+files[i]);
        }

        try {
            for (String str : files) {
                String name = PACKAGE + "." + str.replace(".java","");
                Class<? extends ICallPriceStratege> clzzs =
                        (Class<? extends ICallPriceStratege>)Class.forName(name);
                if (clzzs.isAnnotationPresent(PriceRegion.class)){
                    list.add(clzzs);
                }
            }
        }catch (Exception e ){
            e.printStackTrace();
        }



        return null;
    }



    ICallPriceStratege getCallPrice(double p){

        list.forEach(PriceFactory::accept);
        return null;
    }




//    public ICallPriceStratege getCall(double price){
//        if(price < 10000) {
//            return new OrdinaryPlayer();
//        } else if(price >= 10000 && price < 20000) {
//            return new Member();
//        } else if(price >= 20000 && price < 30000) {
//            return new SuperMember();
//        } else {
//            return new GoldMember();
//        }
//    }
//    public static PriceFactory getPriceFactory(){
//        return  new PriceFactory();
//    }
}
