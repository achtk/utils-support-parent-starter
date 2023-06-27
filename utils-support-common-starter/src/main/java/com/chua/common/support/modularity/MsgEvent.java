package com.chua.common.support.modularity;

import com.chua.common.support.lang.profile.Profile;
import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 消息事件
 *
 * @author CH
 */
@Data
public class MsgEvent {

    /**
     * 名称
     */
    private String name;
    /**
     * 参数
     */
    private Object args;

    private boolean isRunning;
    /**
     * 額外參數
     */
    private Map<String, ModularityResult> param = new ConcurrentHashMap<>();
}
