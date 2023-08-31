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
import com.chua.common.support.protocol.websocket.exceptions.InvalidDataException;
import com.chua.common.support.protocol.websocket.exceptions.InvalidFrameException;

/**
 * Abstract class to represent control frames
 *
 * @author Administrator
 */
public abstract class AbstractControlFrame extends BaseFramedataImpl1 {

  /**
   * Class to represent a control frame
   *
   * @param opcode the opcode to use
   */
  public AbstractControlFrame(Opcode opcode) {
    super(opcode);
  }

  @Override
  public void isValid() throws InvalidDataException {
    if (!isFin()) {
      throw new InvalidFrameException("Control frame can't have fin==false set");
    }
    if (isRsv1()) {
      throw new InvalidFrameException("Control frame can't have rsv1==true set");
    }
    if (isRsv2()) {
      throw new InvalidFrameException("Control frame can't have rsv2==true set");
    }
    if (isRsv3()) {
      throw new InvalidFrameException("Control frame can't have rsv3==true set");
    }
  }
}
