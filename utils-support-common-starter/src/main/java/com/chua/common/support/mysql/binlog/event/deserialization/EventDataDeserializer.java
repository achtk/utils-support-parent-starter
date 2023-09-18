package com.chua.common.support.mysql.binlog.event.deserialization;


import com.chua.common.support.mysql.binlog.event.EventData;
import com.chua.common.support.mysql.binlog.io.ByteArrayInputStream;

import java.io.IOException;

/**
 * @param <T> event data this deserializer is responsible for
 * @author <a href="mailto:stanley.shyiko@gmail.com">Stanley Shyiko</a>
 */
public interface EventDataDeserializer<T extends EventData> {

    /**
     * 反序列化
     *
     * @param inputStream 输入流
     * @return {@link T}
     * @throws IOException IOException
     */
    T deserialize(ByteArrayInputStream inputStream) throws IOException;
}
