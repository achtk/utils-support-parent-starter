package com.chua.common.support.mapping.invoke.hik;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ArtemisHttpUtil {
    private static final Logger logger = LoggerFactory.getLogger(ArtemisHttpUtil.class);


    /**
     * 调用网关成功的标志,标志位
     */
    private final static String SUCC_PRE = "2";

    /**
     * 调用网关重定向的标志,标志位
     */
    private final static String REDIRECT_PRE = "3";


}
