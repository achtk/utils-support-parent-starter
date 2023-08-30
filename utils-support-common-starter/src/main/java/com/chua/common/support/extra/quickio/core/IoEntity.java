package com.chua.common.support.extra.quickio.core;
/**
 * 配置
 * @author CH
 */
public class IoEntity {

    long id;
    long createdAt;


    public final long objectId() {
        return id;
    }


    public final long createdAt() {
        return createdAt;
    }


    public final String toJson() {
        return Plugin.toJson(this);
    }


    public final void printJson() {
        Plugin.printJson(this);
    }

}