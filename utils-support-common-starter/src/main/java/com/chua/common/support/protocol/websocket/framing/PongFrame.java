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

package com.chua.common.support.protocol.websocket.framing;

import com.chua.common.support.protocol.constant.Opcode;

/**
 * Class to represent a pong frame
 *
 * @author Administrator
 */
public class PongFrame extends ControlFrame {

  /**
   * constructor which sets the opcode of this frame to pong
   */
  public PongFrame() {
    super(Opcode.PONG);
  }

  /**
   * constructor which sets the opcode of this frame to ping copying over the payload of the ping
   *
   * @param pingFrame the PingFrame which payload is to copy
   */
  public PongFrame(PingFrame pingFrame) {
    super(Opcode.PONG);
    setPayload(pingFrame.getPayloadData());
  }
}
