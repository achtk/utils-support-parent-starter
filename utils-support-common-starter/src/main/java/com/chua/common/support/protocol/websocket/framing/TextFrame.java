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
import com.chua.common.support.protocol.websocket.util.Charsetfunctions;

/**
 * Class to represent a text frames
 *
 * @author Administrator
 */
public class TextFrame extends BaseDataFrame {

  /**
   * constructor which sets the opcode of this frame to text
   */
  public TextFrame() {
    super(Opcode.TEXT);
  }

  @Override
  public void isValid() throws InvalidDataException {
    super.isValid();
    if (!Charsetfunctions.isValidUtf8(getPayloadData())) {
      throw new InvalidDataException(CloseFrame.NO_UTF8, "Received text is no valid utf8 string!");
    }
  }
}
