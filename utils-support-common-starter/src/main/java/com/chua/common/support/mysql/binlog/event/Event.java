package com.chua.common.support.mysql.binlog.event;

import java.io.Serializable;

/**
 * @author <a href="mailto:stanley.shyiko@gmail.com">Stanley Shyiko</a>
 */
public class Event implements Serializable {

    private final EventHeader header;
    private final EventData data;

    public Event(EventHeader header, EventData data) {
        this.header = header;
        this.data = data;
    }

    @SuppressWarnings("unchecked")
    public <T extends EventHeader> T getHeader() {
        return (T) header;
    }

    @SuppressWarnings("unchecked")
    public <T extends EventData> T getData() {
        return (T) data;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("Event");
        sb.append("{header=").append(header);
        sb.append(", data=").append(data);
        sb.append('}');
        return sb.toString();
    }
}
