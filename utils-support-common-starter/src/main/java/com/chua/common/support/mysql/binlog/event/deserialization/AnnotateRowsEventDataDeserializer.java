package com.chua.common.support.mysql.binlog.event.deserialization;

import com.chua.common.support.mysql.binlog.event.AnnotateRowsEventData;
import com.chua.common.support.mysql.binlog.io.ByteArrayInputStream;

import java.io.IOException;

/**
 * Mariadb ANNOTATE_ROWS_EVENT Fields
 * <pre>
 *  string&lt;EOF&gt; The SQL statement (not null-terminated)
 * </pre>
 *
 * @author <a href="mailto:winger2049@gmail.com">Winger</a>
 */
public class AnnotateRowsEventDataDeserializer implements EventDataDeserializer<AnnotateRowsEventData> {

    @Override
    public AnnotateRowsEventData deserialize(ByteArrayInputStream inputStream) throws IOException {
        AnnotateRowsEventData event = new AnnotateRowsEventData();
        event.setRowsQuery(inputStream.readString(inputStream.available()));
        return event;
    }
}
