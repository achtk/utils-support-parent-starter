package com.chua.common.support.mysql.binlog.network.protocol.command;


import com.chua.common.support.mysql.binlog.io.ByteArrayOutputStream;

import java.io.IOException;

/**
 * @author <a href="mailto:stanley.shyiko@gmail.com">Stanley Shyiko</a>
 */
public class QueryCommand implements Command {

    private final String sql;

    public QueryCommand(String sql) {
        this.sql = sql;
    }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        buffer.writeInteger(CommandType.QUERY.ordinal(), 1);
        buffer.writeString(this.sql);
        return buffer.toByteArray();
    }

}
