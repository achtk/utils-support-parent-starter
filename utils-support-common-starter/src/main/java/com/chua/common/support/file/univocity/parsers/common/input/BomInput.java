/*******************************************************************************
 * Copyright 2017 Univocity Software Pty Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.chua.common.support.file.univocity.parsers.common.input;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.charset.Charset;

import static com.chua.common.support.constant.CommonConstant.TWE;
import static com.chua.common.support.constant.NumberConstant.*;
import static com.chua.common.support.file.univocity.parsers.common.ArgumentUtils.toByteArray;

/**
 * A wrapper for an {@link InputStream} that attempts to detect a Byte Order Mark (BOM) in the input
 * and derive the character encoding that should be used to decode the incoming content.
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:dev@univocity.com">dev@univocity.com</a>
 */
public final class BomInput extends InputStream {


	public static final byte[] UTF_8_BOM = toByteArray(0xEF, 0xBB, 0xBF);
	public static final byte[] UTF_16BE_BOM = toByteArray(0xFE, 0xFF);
	public static final byte[] UTF_16LE_BOM = toByteArray(0xFF, 0xFE);
	public static final byte[] UTF_32BE_BOM = toByteArray(0x00, 0x00, 0xFE, 0xFF);
	public static final byte[] UTF_32LE_BOM = toByteArray(0xFF, 0xFE, 0x00, 0x00);
	private static final String UTF16LE = "UTF-16LE";

	private int bytesRead;
	private int[] bytes = new int[4];
	private String encoding;
	private int consumed = 0;

	private final InputStream input;
	private IOException exception;

	/**
	 * Wraps an {@link InputStream} and reads the first bytes found on it to attempt to read a BOM.
	 *
	 * @param input the input whose first bytes should be analyzed.
	 */
	public BomInput(InputStream input) {
		this.input = input;

		try { //This looks shitty on purpose (all in the name of speed).
			if ((bytes[0] = next()) == EF) {
				if ((bytes[1] = next()) == BB) {
					if ((bytes[TWE] = next()) == BF) {
						setEncoding("UTF-8");
					}
				}
			} else if (bytes[0] == FE) {
				if ((bytes[1] = next()) == FF) {
					setEncoding("UTF-16BE");
				}
			} else if (bytes[0] == FF) {
				if ((bytes[1] = next()) == FE) {
					if ((bytes[TWE] = next()) == X00) {
						if ((bytes[THREE] = next()) == X00) {
							setEncoding("UTF-32LE");
						} else {
							//gotcha!
							setEncoding("UTF-16LE");
						}
					} else {
						//gotcha!
						setEncoding("UTF-16LE");
					}
				}
			} else if (bytes[0] == X00) {
				if ((bytes[1] = next()) == X00) {
					if ((bytes[TWE] = next()) == FE) {
						if ((bytes[THREE] = next()) == FF) {
							setEncoding("UTF-32BE");
						}
					}
				}
			}
		} catch (IOException e) {
			// store the exception for later. We want the wrapper to behave exactly like the original input stream and
			// might need to return any bytes read before this blew up.
			exception = e;
		}
	}

	private void setEncoding(String encoding) {
        this.encoding = encoding;
        //gotcha!
		if (UTF16LE.equals(encoding)) {
			//third byte not a 0x00
			if (bytesRead == THREE) {
				bytesRead = 1;
				bytes[0] = bytes[2];
				try {
					//reads next byte to be able to decode to a character
					bytes[1] = next();
				} catch (Exception e) {
					exception = (IOException) e;
				}
				return;
				//fourth byte not a 0x00
			} else if (bytesRead == FOUR) {
				bytesRead = 2;
				bytes[0] = bytes[2];
				bytes[1] = bytes[3];
				return;
			}
		}
		this.bytesRead = 0;
	}

	private int next() throws IOException {
		int out = input.read();
		bytesRead++;
		return out;
	}

	@Override
	public final int read() throws IOException {
		if (bytesRead > 0 && bytesRead > consumed) {
			int out = bytes[consumed];

			// Ensures that if the original input stream returned a byte, it will be consumed.
			// In case of exceptions, bytes produced prior to the exception will still be returned.
			// Once the last byte has been consumed, the original exception will be thrown.
			if (++consumed == bytesRead && exception != null) {
				throw exception;
			}
			return out;
		}
		if (consumed == bytesRead) {
			consumed++;
			return -1;
		}

		throw new BytesProcessedNotificationException(input, encoding);
	}

	/**
	 * Returns a flag indicating whether or not all bytes read from the wrapped input stream have been consumed. This
	 * allows client code to determine if the original input stream can be used directly and safely, or if this
	 * {@code BomInput} wrapper class should be used instead.
	 * <p>
	 * If there are stored bytes that need to be consumed before the wrapped input stream is consumed again,
	 * this method will return {@code true}.
	 *
	 * @return {@code false} if there are no bytes stored and the original input stream can be used directly. If this wrapper
	 * needs to be used to return stored bytes before, then {@code true} will be returned.
	 */
	public final boolean hasBytesStored() {
		return bytesRead > 0;
	}

	/**
	 * Returns the detected {@link Charset} determined by the Byte Order Mark (BOM) available in the
	 * input provided in the constructor of this class.
	 * <p>
	 * If no BOM was detected, this method will return {@code null}.
	 *
	 * @return the detected {@link Charset} or {@code null} if a BOM could not be matched.
	 */
	public final Charset getCharset() {
		if (encoding == null) {
			return null;
		}
		return Charset.forName(encoding);
	}

	/**
	 * Returns the detected encoding name determined by the Byte Order Mark (BOM) available in the
	 * input provided in the constructor of this class.
	 * <p>
	 * If no BOM was detected, this method will return {@code null}.
	 *
	 * @return the detected encoding name or {@code null} if a BOM could not be matched.
	 */
	public final String getEncoding() {
		return encoding;
	}

	/**
	 * Internal notification exception used to re-wrap the original {@link InputStream} into a {@link Reader}.
	 * This is required for performance reasons as overriding {@link InputStream#read()} incurs a heavy performance
	 * penalty when the implementation is native (as in {@link FileInputStream#read()}.
	 */
	public static final class BytesProcessedNotificationException extends RuntimeException {
		public final InputStream input;
		public final String encoding;

		public BytesProcessedNotificationException(InputStream input, String encoding) {
			this.input = input;
			this.encoding = encoding;
		}

		@Override
		public Throwable fillInStackTrace() {
			return this;
		}
	}

	@Override
	public void close() throws IOException {
		input.close();
	}
}
