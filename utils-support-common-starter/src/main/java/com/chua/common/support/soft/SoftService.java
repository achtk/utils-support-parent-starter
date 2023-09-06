package com.chua.common.support.soft;

/**
 * 服务
 * @author CH
 */
public interface SoftService extends Runnable{
    /**
     * 开始
     */
    void start();

    /**
     * 检测程序是否运行
     * @return 检测程序是否运行
     */
    boolean check();

    /**
     * pid
     * @return pid
     */
    String pid();


    /**
     * 停止
     *
     * @return boolean
     */
    boolean stop();
}
