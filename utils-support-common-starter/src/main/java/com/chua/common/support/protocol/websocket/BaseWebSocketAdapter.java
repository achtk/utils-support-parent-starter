package com.chua.common.support.protocol.websocket;

import com.chua.common.support.protocol.websocket.drafts.Draft;
import com.chua.common.support.protocol.websocket.framing.Framedata;
import com.chua.common.support.protocol.websocket.framing.PingFrame;
import com.chua.common.support.protocol.websocket.framing.PongFrame;
import com.chua.common.support.protocol.websocket.handshake.ClientHandshake;
import com.chua.common.support.protocol.websocket.handshake.HandshakeImpl1Server;
import com.chua.common.support.protocol.websocket.handshake.ServerHandshake;
import com.chua.common.support.protocol.websocket.handshake.ServerHandshakeBuilder;

/**
 * This class default implements all methods of the WebSocketListener that can be overridden
 * optionally when advances functionalities is needed.<br>
 *
 * @author Administrator
 */
public abstract class BaseWebSocketAdapter implements WebSocketListener {

  private PingFrame pingFrame;

  /**
   * This default implementation does not do anything. Go ahead and overwrite it.
   *
   * @see WebSocketListener#onWebsocketHandshakeReceivedAsServer(WebSocket,
   * Draft, ClientHandshake)
   */
  @Override
  public ServerHandshakeBuilder onWebsocketHandshakeReceivedAsServer(WebSocket conn, Draft draft,
                                                                     ClientHandshake request) {
    return new HandshakeImpl1Server();
  }

  @Override
  public void onWebsocketHandshakeReceivedAsClient(WebSocket conn, ClientHandshake request,
                                                   ServerHandshake response) {
    //To overwrite
  }

  /**
   * This default implementation does not do anything which will cause the connections to always
   * progress.
   *
   * @see WebSocketListener#onWebsocketHandshakeSentAsClient(WebSocket,
   * ClientHandshake)
   */
  @Override
  public void onWebsocketHandshakeSentAsClient(WebSocket conn, ClientHandshake request) {
    //To overwrite
  }

  /**
   * This default implementation will send a pong in response to the received ping. The pong frame
   * will have the same payload as the ping frame.
   *
   * @see WebSocketListener#onWebsocketPing(WebSocket, Framedata)
   */
  @Override
  public void onWebsocketPing(WebSocket conn, Framedata f) {
    conn.sendFrame(new PongFrame((PingFrame) f));
  }

  /**
   * This default implementation does not do anything. Go ahead and overwrite it.
   *
   * @see WebSocketListener#onWebsocketPong(WebSocket, Framedata)
   */
  @Override
  public void onWebsocketPong(WebSocket conn, Framedata f) {
    //To overwrite
  }

  /**
   * Default implementation for onPreparePing, returns a (cached) PingFrame that has no application
   * data.
   *
   * @param conn The <tt>WebSocket</tt> connection from which the ping frame will be sent.
   * @return PingFrame to be sent.
   * @see WebSocketListener#onPreparePing(WebSocket)
   */
  @Override
  public PingFrame onPreparePing(WebSocket conn) {
    if (pingFrame == null) {
      pingFrame = new PingFrame();
    }
    return pingFrame;
  }
}
