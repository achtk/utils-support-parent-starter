package com.chua.common.support.monitor;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Description
 *
 * @author CH
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class DescriptionNotifyMessage extends NotifyMessage {

    private String serverVersion;

    private String checksumType;

    public DescriptionNotifyMessage(NotifyType notifyType, String message) {
        super(notifyType, message);
    }
}
