package com.example.ann_butterknife;

/**
 * Created by maple on 2019/7/24 15:00
 *
 */
public class ButterKnife {
    public static void bind(Object clazz){
        String name = clazz.getClass().getName()+"_ViewBinding";
        try {
            Class<?> aClass= Class.forName(name);
            IBinder binder = (IBinder) aClass.newInstance();
            binder.bind(clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
