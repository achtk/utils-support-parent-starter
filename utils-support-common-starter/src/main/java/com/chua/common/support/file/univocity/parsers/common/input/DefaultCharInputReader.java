/*******************************************************************************
 * Copyright 2014 Univocity Software Pty Ltd
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

import com.chua.common.support.file.univocity.parsers.common.BaseFormat;

import java.io.IOException;
import java.io.Reader;

/**
 * A default CharInputReader which only loads batches of characters when requested by the {@link AbstractCharInputReader} through the {@link DefaultCharInputReader#reloadBuffer} method.
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 */
public class DefaultCharInputReader extends AbstractCharInputReader {

	private Reader reader;
	private boolean unwrapping = false;

	/**
	 * Creates a new instance with the mandatory characters for handling newlines transparently. Line separators will be detected automatically.
	 *
	 * @param normalizedLineSeparator the normalized newline character (as defined in {@link BaseFormat#getNormalizedNewline()}) that is used to replace any lineSeparator sequence found in the input.
	 * @param bufferSize              the buffer size used to store characters read from the input.
	 * @param whitespaceRangeStart    starting range of characters considered to be whitespace.
	 * @param closeOnStop             indicates whether to automatically close the input when {@link #stop()} is called
	 */
	public DefaultCharInputReader(char normalizedLineSeparator, int bufferSize, int whitespaceRangeStart, boolean closeOnStop) {
		super(normalizedLineSeparator, whitespaceRangeStart, closeOnStop);
		super.buffer = new char[bufferSize];
	}

	/**
	 * Creates a new instance with the mandatory characters for handling newlines transparently.
	 *
	 * @param lineSeparator           the sequence of characters that represent a newline, as defined in {@link BaseFormat#getLineSeparator()}
	 * @param normalizedLineSeparator the normalized newline character (as defined in {@link BaseFormat#getNormalizedNewline()}) that is used to replace any lineSeparator sequence found in the input.
	 * @param bufferSize              the buffer size used to store characters read from the input.
	 * @param whitespaceRangeStart    starting range of characters considered to be whitespace.
	 * @param closeOnStop             indicates whether to automatically close the input when {@link #stop()} is called
	 */
	public DefaultCharInputReader(char[] lineSeparator, char normalizedLineSeparator, int bufferSize, int whitespaceRangeStart, boolean closeOnStop) {
		super(lineSeparator, normalizedLineSeparator, whitespaceRangeStart, closeOnStop);
		super.buffer = new char[bufferSize];
	}

	@Override
	public void stop() {
		try {
			if (!unwrapping && closeOnStop && reader != null) {
				reader.close();
			}
		} catch (IOException e) {
			throw new IllegalStateException("Error closing input", e);
		}
	}

	@Override
	protected void setReader(Reader reader) {
		this.reader = reader;
		unwrapping = false;
	}

	/**
	 * Copies a sequence of characters from the input into the {@link DefaultCharInputReader#buffer}, and updates the {@link DefaultCharInputReader#length} to the number of characters read.
	 */
	@Override
	public void reloadBuffer() {
		try {
			super.length = reader.read(buffer, 0, buffer.length);
		} catch (IOException e) {
			throw new IllegalStateException("Error reading from input", e);
		} catch (BomInput.BytesProcessedNotificationException notification) {
			unwrapping = true;
			unwrapInputStream(notification);
		}
	}
}
