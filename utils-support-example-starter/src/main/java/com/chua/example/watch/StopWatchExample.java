package com.chua.example.watch;

import com.chua.common.support.lang.StopWatch;
import com.chua.common.support.utils.ThreadUtils;

/**
 * @author CH
 */
public class StopWatchExample {

    public static void main(String[] args) {

        StopWatch stopWatch = new StopWatch("1");
        stopWatch.start("测试");
        stopWatch.stop();

        stopWatch.start("测试1");
        ThreadUtils.sleepSecondsQuietly(1);

        stopWatch.stop();


        System.out.println(stopWatch.prettyPrint());
    }
}
