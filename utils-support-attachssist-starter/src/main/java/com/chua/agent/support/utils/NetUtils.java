package com.chua.agent.support.utils;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.BitSet;

/**
 * net
 * @author CH
 */
public class NetUtils {
    /**
     * store the used port.
     * the set used only on the synchronized method.
     */
    private static final BitSet USED_PORT = new BitSet(65536);
    private static final int MIN_PORT = 1;
    private static final int MAX_PORT = 65535;


    /**
     * 获取可用端口
     *
     * @param port 起始端口
     * @return 端口
     */
    public static synchronized int getAvailablePort(int port) {
        if (port < MIN_PORT) {
            return MIN_PORT;
        }

        for (int i = port; i < MAX_PORT; i++) {
            if (USED_PORT.get(i)) {
                continue;
            }
            try (ServerSocket ignored = new ServerSocket(i)) {
                USED_PORT.set(i);
                port = i;
                break;
            } catch (IOException e) {
                // continue
            }
        }
        return port;
    }

}
