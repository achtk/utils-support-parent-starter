package com.chua.common.support.monitor.session;

import com.chua.common.support.monitor.NotifyMessage;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * delete
 *
 * @author CH
 * @since 2022-05-19
 */
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
public class SessionNotifyMessage extends NotifyMessage {
    /**
     * 会话
     */
    private Session session;

}
