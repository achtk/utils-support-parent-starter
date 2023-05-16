package com.chua.common.support.monitor;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.BitSet;
import java.util.List;

/**
 * write
 *
 * @author CH
 * @since 2022-05-19
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class WriteNotifyMessage extends NotifyMessage {

    private BitSet column;
    private List<Serializable[]> modifyData;
    private TableNotifyMessage session;

    public WriteNotifyMessage(NotifyType notifyType, String message) {
        super(notifyType, message);
    }
}
