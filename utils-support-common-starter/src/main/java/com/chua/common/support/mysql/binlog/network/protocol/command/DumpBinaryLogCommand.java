package com.chua.common.support.mysql.binlog.network.protocol.command;

import com.chua.common.support.mysql.binlog.io.ByteArrayOutputStream;

import java.io.IOException;

/**
 * @author <a href="mailto:stanley.shyiko@gmail.com">Stanley Shyiko</a>
 */
public class DumpBinaryLogCommand implements Command {

    public static final int BINLOG_SEND_ANNOTATE_ROWS_EVENT = 2;
    private long serverId;
    private String binlogFilename;
    private long binlogPosition;
    private boolean sendAnnotateRowsEvent;

    public DumpBinaryLogCommand(long serverId, String binlogFilename, long binlogPosition) {
        this.serverId = serverId;
        this.binlogFilename = binlogFilename;
        this.binlogPosition = binlogPosition;
    }

    public DumpBinaryLogCommand(long serverId, String binlogFilename, long binlogPosition, boolean sendAnnotateRowsEvent) {
        this(serverId, binlogFilename, binlogPosition);
        this.sendAnnotateRowsEvent = sendAnnotateRowsEvent;
    }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        buffer.writeInteger(CommandType.BINLOG_DUMP.ordinal(), 1);
        buffer.writeLong(this.binlogPosition, 4);
        int binlogFlags = 0;
        if (sendAnnotateRowsEvent) {
            binlogFlags |= BINLOG_SEND_ANNOTATE_ROWS_EVENT;
        }
        buffer.writeInteger(binlogFlags, 2); // flag
        buffer.writeLong(this.serverId, 4);
        buffer.writeString(this.binlogFilename);
        return buffer.toByteArray();
    }

}
