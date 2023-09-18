package com.chua.common.support.mysql.binlog.network.protocol.command;


import com.chua.common.support.mysql.binlog.io.ByteArrayOutputStream;
import com.chua.common.support.mysql.binlog.network.ClientCapabilities;

import java.io.IOException;

import static com.chua.common.support.constant.NumberConstant.TWENTY_THREE;

/**
 * sslequest命令
 *
 * @author CH
 */
public class SSLRequestCommand implements Command {

    private int clientCapabilities;
    private int collation;

    public void setClientCapabilities(int clientCapabilities) {
        this.clientCapabilities = clientCapabilities;
    }

    public void setCollation(int collation) {
        this.collation = collation;
    }

    @Override
    public byte[] toByteArray() throws IOException {
        try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            int clientCapabilities = this.clientCapabilities;
            if (clientCapabilities == 0) {
                clientCapabilities = ClientCapabilities.LONG_FLAG |
                        ClientCapabilities.PROTOCOL_41 |
                        ClientCapabilities.SECURE_CONNECTION |
                        ClientCapabilities.PLUGIN_AUTH;
            }
            clientCapabilities |= ClientCapabilities.SSL;
            buffer.writeInteger(clientCapabilities, 4);
            buffer.writeInteger(0, 4);
            buffer.writeInteger(collation, 1);
            for (int i = 0; i < TWENTY_THREE; i++) {
                buffer.write(0);
            }
            return buffer.toByteArray();
        }
    }

}
