package com.chua.common.support.modularity;

import com.chua.common.support.lang.profile.Profile;
import lombok.Data;

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
    private Profile profile;
}
