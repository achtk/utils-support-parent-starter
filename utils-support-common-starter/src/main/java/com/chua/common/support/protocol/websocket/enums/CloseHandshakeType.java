package com.chua.common.support.protocol.websocket.enums;

/**
 * Enum which represents type of handshake is required for a close
 *
 * @author Administrator
 */
public enum CloseHandshakeType {
  /**
   * none
   */
  NONE,
  /**
   * one
   */
  ONEWAY,
  /**
   * twe
   */
  TWOWAY
}