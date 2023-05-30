package com.chua.example.timeunit;

import com.chua.common.support.unit.TimeSize;
import com.chua.common.support.unit.TimeUnit;

/**
 * @author CH
 */
public class TimeUnitExample {

    public static void main(String[] args) {
        TimeSize timeSize = TimeSize.of("2MIN1S");
        timeSize.toSecond();
        System.out.println();
    }
}
