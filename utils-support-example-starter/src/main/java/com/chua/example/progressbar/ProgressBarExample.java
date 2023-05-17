package com.chua.example.progressbar;


import com.chua.common.support.lang.process.ProgressBar;
import com.chua.common.support.utils.ThreadUtils;

/**
 * @author CH
 */
public class ProgressBarExample {

    public static void main(String[] args) {
        int max = 100000;
        ProgressBar consoleProgressBar = new ProgressBar(max);

        int i = 0;
        while (i < max) {
            consoleProgressBar.stepBy(10);
            ThreadUtils.sleepMillisecondsQuietly(1);
            i += 10;
        }
        consoleProgressBar.close();
    }
}
