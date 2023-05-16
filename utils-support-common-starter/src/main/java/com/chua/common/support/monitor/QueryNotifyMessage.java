package com.chua.common.support.monitor;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * description
 *
 * @author CH
 * @since 2022-05-19
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class QueryNotifyMessage extends NotifyMessage {

    @Getter
    private String root;
    private long threadId;

    private String checksumType;

    public QueryNotifyMessage(NotifyType notifyType, String message) {
        super(notifyType, message);
    }
}
