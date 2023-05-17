package com.chua.example.ansi;

import com.chua.common.support.ansi.AnsiColor;
import com.chua.common.support.ansi.AnsiOutput;

/**
 * @author CH
 */
public class AnsiExample {

    public static void main(String[] args) {
        AnsiOutput.help();
        AnsiOutput.setEnabled(AnsiOutput.Enabled.ALWAYS);
        String test = AnsiOutput.toString(AnsiColor.RED, "test");
        System.out.println(test);
    }
}
