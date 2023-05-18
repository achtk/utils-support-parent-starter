package com.chua.common.support.extra.el.baseutil;

import java.util.regex.Pattern;

public class NumberUtil
{
    final static Pattern numberPattern = Pattern.compile("-?\\d+\\.?\\d*");

    public static boolean isNumeric(String str)
    {
        return numberPattern.matcher(str).matches();
    }
}
