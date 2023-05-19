package com.chua.server.support.server.file;


import com.chua.common.support.annotations.Spi;
import com.chua.common.support.protocol.server.Server;
import com.chua.common.support.protocol.server.ServerOption;
import com.chua.common.support.protocol.server.ServerProvider;

/**
 * 文件服务器
 *
 * @author CH
 */
@Spi("file")
public class FileVfsServerProvider implements ServerProvider {
    @Override
    public Server create(ServerOption option, String... args) {
        return new FileVfsServer(option, args);
    }
}
