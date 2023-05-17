package com.chua.common.support.protocol.websocket.enums;

/**
 * Enum which represents the states a handshake may be in
 *
 * @author Administrator
 */
public enum HandshakeState {
  /**
   * Handshake matched this Draft successfully
   */
  MATCHED,
  /**
   * Handshake is does not match this Draft
   */
  NOT_MATCHED
}