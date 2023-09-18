package com.chua.common.support.mysql.binlog.event.deserialization;


import com.chua.common.support.mysql.binlog.event.EventHeader;

import java.io.IOException;

/**
 * @author <a href="mailto:stanley.shyiko@gmail.com">Stanley Shyiko</a>
 */
public class EventDataDeserializationException extends IOException {

    private EventHeader eventHeader;

    public EventDataDeserializationException(EventHeader eventHeader, Throwable cause) {
        super("Failed to deserialize data of " + eventHeader, cause);
        this.eventHeader = eventHeader;
    }

    public EventHeader getEventHeader() {
        return eventHeader;
    }
}
