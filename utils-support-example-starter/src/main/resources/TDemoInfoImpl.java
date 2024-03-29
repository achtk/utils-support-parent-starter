package com.chua.tools.example.entity;

import com.chua.example.dynamic.TDemoInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * @author CH
 * @version 1.0.0
 * @since 2020/10/28
 */
public class TDemoInfoImpl implements TDemoInfo {
    private static final Logger log = LoggerFactory.getLogger(TDemoInfoImpl.class);
    public Integer id;
    public Date time;
    public String name;
    public String title;
    public String demoTitle;
    public String test;
    private String uuid;


    @Override
    public String getUuid() throws Exception{
        String value = "java文件执行方法[getUuid]:12S";
        log.info(value);
        return value;
    }

    @Override
    public String getId() {
        return "java文件执行方法[getId]:2";
    }
}