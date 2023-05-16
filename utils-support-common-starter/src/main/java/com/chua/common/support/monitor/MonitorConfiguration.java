package com.chua.common.support.monitor;

import com.chua.common.support.spi.ServiceProvider;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;
import lombok.experimental.Accessors;

import java.util.concurrent.ExecutorService;

/**
 * 监听配置
 *
 * @author CH
 */
@Data
@Accessors(fluent = true)
@With
@AllArgsConstructor
@NoArgsConstructor(staticName = "newBuilder")
public class MonitorConfiguration {
    /**
     * 间隔时间
     */
    private int interval;
    /**
     * 线程
     */
    private ExecutorService executorService;
    /**
     * 地址
     */
    private String url;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;

    /**
     * 数据库
     */
    private String database = "0";
    /**
     * 超时时间
     */
    private int timeout;

    /**
     * 创建监听
     *
     * @param type 类型
     * @return 监听
     */
    public Monitor create(String type) {
        Monitor monitor = ServiceProvider.of(Monitor.class).getNewExtension(type);
        monitor.configuration(this);
        return monitor;
    }

}
