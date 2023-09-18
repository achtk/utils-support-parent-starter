package com.chua.common.support.mysql.binlog.event.deserialization;


import com.chua.common.support.mysql.binlog.event.EventData;
import com.chua.common.support.mysql.binlog.io.ByteArrayInputStream;

import java.io.IOException;

/**
 * @author <a href="mailto:stanley.shyiko@gmail.com">Stanley Shyiko</a>
 */
public class NullEventDataDeserializer implements EventDataDeserializer {

    @Override
    public EventData deserialize(ByteArrayInputStream inputStream) throws IOException {
        return null;
    }
}
