package com.chua.common.support.monitor;

import lombok.Data;
import lombok.EqualsAndHashCode;
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
public class TableNotifyMessage extends NotifyMessage {

    private long tableId;
    private String database;
    private String table;

    public TableNotifyMessage(NotifyType notifyType, String message) {
        super(notifyType, message);
    }
}
