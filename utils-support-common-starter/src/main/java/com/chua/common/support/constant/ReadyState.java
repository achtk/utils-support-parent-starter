package com.chua.common.support.protocol.constant;

/**
 * Enum which represents the state a websocket may be in
 *
 * @author Administrator
 */
public enum ReadyState {
    /**
     * NOT_YET_CONNECTED
     */
    NOT_YET_CONNECTED,
    /**
     * OPEN
     */
    OPEN,
    /**
     * CLOSING
     */
    CLOSING,
    /**
     * CLOSED
     */
    CLOSED
}