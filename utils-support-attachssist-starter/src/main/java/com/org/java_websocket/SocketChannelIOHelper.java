/*
 * Copyright (c) 2010-2020 Nathan Rajlich
 *
 *  Permission is hereby granted, free of charge, to any person
 *  obtaining a copy of this software and associated documentation
 *  files (the "Software"), to deal in the Software without
 *  restriction, including without limitation the rights to use,
 *  copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the
 *  Software is furnished to do so, subject to the following
 *  conditions:
 *
 *  The above copyright notice and this permission notice shall be
 *  included in all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 *  EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 *  OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 *  HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 *  WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 *  OTHER DEALINGS IN THE SOFTWARE.
 */

package com.org.java_websocket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;

import com.org.java_websocket.WebSocketImpl;
import com.org.java_websocket.WrappedByteChannel;
import com.org.java_websocket.enums.Role;

public class SocketChannelIOHelper {

  private SocketChannelIOHelper() {
    throw new IllegalStateException("Utility class");
  }

  public static boolean read(final ByteBuffer buf, com.org.java_websocket.WebSocketImpl ws, ByteChannel channel)
      throws IOException {
    buf.clear();
    int read = channel.read(buf);
    buf.flip();

    if (read == -1) {
      ws.eot();
      return false;
    }
    return read != 0;
  }

  /**
   * @param buf     The ByteBuffer to read from
   * @param ws      The WebSocketImpl associated with the channels
   * @param channel The channel to read from
   * @return returns Whether there is more data left which can be obtained via {@link
   * com.org.java_websocket.WrappedByteChannel#readMore(ByteBuffer)}
   * @throws IOException May be thrown by {@link com.org.java_websocket.WrappedByteChannel#readMore(ByteBuffer)}#
   * @see com.org.java_websocket.WrappedByteChannel#readMore(ByteBuffer)
   **/
  public static boolean readMore(final ByteBuffer buf, com.org.java_websocket.WebSocketImpl ws, com.org.java_websocket.WrappedByteChannel channel)
      throws IOException {
    buf.clear();
    int read = channel.readMore(buf);
    buf.flip();

    if (read == -1) {
      ws.eot();
      return false;
    }
    return channel.isNeedRead();
  }

  /**
   * Returns whether the whole outQueue has been flushed
   *
   * @param ws          The WebSocketImpl associated with the channels
   * @param sockchannel The channel to write to
   * @return returns Whether there is more data to write
   * @throws IOException May be thrown by {@link com.org.java_websocket.WrappedByteChannel#writeMore()}
   */
  public static boolean batch(WebSocketImpl ws, ByteChannel sockchannel) throws IOException {
    if (ws == null) {
      return false;
    }
    ByteBuffer buffer = ws.outQueue.peek();
    com.org.java_websocket.WrappedByteChannel c = null;

    if (buffer == null) {
      if (sockchannel instanceof com.org.java_websocket.WrappedByteChannel) {
        c = (com.org.java_websocket.WrappedByteChannel) sockchannel;
        if (c.isNeedWrite()) {
          c.writeMore();
        }
      }
    } else {
      do {
        // FIXME writing as much as possible is unfair!!
        /*int written = */
        sockchannel.write(buffer);
        if (buffer.remaining() > 0) {
          return false;
        } else {
          ws.outQueue.poll(); // Buffer finished. Remove it.
          buffer = ws.outQueue.peek();
        }
      } while (buffer != null);
    }

    if (ws.outQueue.isEmpty() && ws.isFlushAndClose() && ws.getDraft() != null
        && ws.getDraft().getRole() != null && ws.getDraft().getRole() == Role.SERVER) {
      ws.closeConnection();
    }
    return c == null || !((WrappedByteChannel) sockchannel).isNeedWrite();
  }
}
