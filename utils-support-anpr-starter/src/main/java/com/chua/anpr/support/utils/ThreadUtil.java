package com.chua.anpr.support.utils;

public class ThreadUtil {

    public static void run(Runnable runnable){
        new Thread(runnable).start();
    }

}
