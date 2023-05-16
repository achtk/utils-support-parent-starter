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
public class UpdateNotifyMessage extends NotifyMessage {

    private BitSet column;
    private List<Serializable[]> modifyData;
    private List<Serializable[]> beforeData;
    private TableNotifyMessage session;

    public UpdateNotifyMessage(NotifyType notifyType, String message) {
        super(notifyType, message);
    }
}
