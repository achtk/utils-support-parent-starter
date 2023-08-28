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

package com.chua.common.support.protocol.websocket.extensions;

import com.chua.common.support.protocol.websocket.exceptions.InvalidDataException;
import com.chua.common.support.protocol.websocket.exceptions.InvalidFrameException;
import com.chua.common.support.protocol.websocket.framing.AbstractControlFrame;
import com.chua.common.support.protocol.websocket.framing.DataFrame;
import com.chua.common.support.protocol.websocket.framing.Framedata;

/**
 * Implementation for a compression extension specified by https://tools.ietf.org/html/rfc7692
 *
 * @author Administrator
 * @since 1.3.5
 */
public abstract class BaseCompressionExtension extends DefaultExtension {

  @Override
  public void isFrameValid(Framedata inputFrame) throws InvalidDataException {
      boolean b = (inputFrame instanceof DataFrame) && (inputFrame.isRsv2() || inputFrame.isRsv3());
      if (b) {
          throw new InvalidFrameException(
                  "bad rsv RSV1: " + inputFrame.isRsv1() + " RSV2: " + inputFrame.isRsv2() + " RSV3: "
                          + inputFrame.isRsv3());
      }
      b = (inputFrame instanceof AbstractControlFrame) && (inputFrame.isRsv1() || inputFrame.isRsv2()
              || inputFrame.isRsv3());
      if (b) {
          throw new InvalidFrameException(
                  "bad rsv RSV1: " + inputFrame.isRsv1() + " RSV2: " + inputFrame.isRsv2() + " RSV3: "
                          + inputFrame.isRsv3());
      }
  }
}
