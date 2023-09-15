package com.chua.proxy.support.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * api操作序列更新事件
 *
 * @author CH
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApiOpSeqUpdateEvent implements Event {

    private long value;

}
