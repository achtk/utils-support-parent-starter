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

import com.chua.common.support.file.univocity.parsers.common.AbstractWriter;
import com.chua.common.support.file.univocity.parsers.common.BaseFormat;

import java.io.IOException;
import java.io.Writer;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_NULL_CHAR;
import static com.chua.common.support.constant.NumberConstant.NUM_2;

/**
 * Extension of the {@link DefaultCharAppender} class to include facilities for writing to an output. Used by writers extending  {@link AbstractWriter}.
 *
 * <p> This class introduces the handling of the normalized newline character defined in {@link BaseFormat#getNormalizedNewline()} and converts it to the newline sequence in {@link BaseFormat#getLineSeparator()}
 * <p> It also introduces methods to write to an instance of  {@link Writer} directly to avoid unnecessary String instantiations.
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see BaseFormat
 * @see AbstractWriter
 */
public class WriterCharAppender extends ExpandingCharAppender {

	private final char lineSeparator1;
	private final char lineSeparator2;
	private final char newLine;
	private boolean denormalizeLineEndings = true;

	/**
	 * Creates a WriterCharAppender with:
	 * <ul>
	 * <li>a maximum limit of characters to append</li>
	 * <li>the default value to return when no characters have been accumulated.</li>
	 * <li>the basic {@link BaseFormat} specification for handling newlines</li>
	 * </ul>
	 * <p>
	 * The padding character is defaulted to a whitespace character ' '.
	 *
	 * @param maxLength            maximum limit of characters to append
	 * @param emptyValue           default value to return when no characters have been accumulated
	 * @param whitespaceRangeStart starting range of characters considered to be whitespace.
	 * @param format               output format specification used for newline handling
	 */
	public WriterCharAppender(int maxLength, String emptyValue, int whitespaceRangeStart, BaseFormat format) {
		super(maxLength == -1 ? 8192 : maxLength, emptyValue, whitespaceRangeStart);

		char[] lineSeparator = format.getLineSeparator();

		this.lineSeparator1 = lineSeparator[0];
		this.lineSeparator2 = lineSeparator.length > 1 ? lineSeparator[1] : '\0';

		newLine = format.getNormalizedNewline();
	}


	/**
	 * Appends the given character and marks it as ignored if it is a whitespace ({@code ch <= ' '})
	 *
	 * <p>If the given character is equal to {@link BaseFormat#getNormalizedNewline()}, then the character sequence returned by {@link BaseFormat#getLineSeparator()} is going to be appended.
	 *
	 * @param ch character to append
	 */
	@Override
	public final void appendIgnoringWhitespace(char ch) {
		if (ch == newLine && denormalizeLineEndings) {
			super.appendIgnoringWhitespace(lineSeparator1);
			if (lineSeparator2 != SYMBOL_NULL_CHAR) {
				super.appendIgnoringWhitespace(lineSeparator2);
			}
		} else {
			super.appendIgnoringWhitespace(ch);
		}
	}

	/**
	 * Appends the given character and marks it as ignored if it is a padding character
	 *
	 * <p>If the given character is equal to {@link BaseFormat#getNormalizedNewline()}, then the character sequence returned by {@link BaseFormat#getLineSeparator()} is going to be appended.
	 *
	 * @param ch      character to append
	 * @param padding the padding character
	 */
	@Override
	public final void appendIgnoringPadding(char ch, char padding) {
		if (ch == newLine && denormalizeLineEndings) {
			super.appendIgnoringPadding(lineSeparator1, padding);
			if (lineSeparator2 != SYMBOL_NULL_CHAR) {
				super.appendIgnoringPadding(lineSeparator2, padding);
			}
		} else {
			super.appendIgnoringPadding(ch, padding);
		}
	}

	/**
	 * Appends the given character and marks it as ignored if it is a whitespace ({@code ch <= ' '}) or a padding character
	 *
	 * <p>If the given character is equal to {@link BaseFormat#getNormalizedNewline()}, then the character sequence returned by {@link BaseFormat#getLineSeparator()} is going to be appended.
	 *
	 * @param ch      character to append
	 * @param padding the padding character
	 */
	@Override
	public final void appendIgnoringWhitespaceAndPadding(char ch, char padding) {
		if (ch == newLine && denormalizeLineEndings) {
			super.appendIgnoringWhitespaceAndPadding(lineSeparator1, padding);
			if (lineSeparator2 != SYMBOL_NULL_CHAR) {
				super.appendIgnoringWhitespaceAndPadding(lineSeparator2, padding);
			}
		} else {
			super.appendIgnoringWhitespaceAndPadding(ch, padding);
		}
	}

	/**
	 * Appends the given character.
	 *
	 * <p>If the given character is equal to {@link BaseFormat#getNormalizedNewline()}, then the character sequence returned by {@link BaseFormat#getLineSeparator()} is going to be appended.
	 *
	 * @param ch the character to append
	 */
	@Override
	public final void append(char ch) {
		if (ch == newLine && denormalizeLineEndings) {
			appendNewLine();
		} else {
			super.append(ch);
		}
	}

	/**
	 * Writes the accumulated value to the {@link Writer}, discarding any trailing whitespace characters identified when using
	 * {@link WriterCharAppender#appendIgnoringWhitespace(char)}, {@link WriterCharAppender#appendIgnoringPadding(char, char)} or {@link WriterCharAppender#appendIgnoringWhitespaceAndPadding(char, char)}
	 * <p> The internal accumulated value is discarded after invoking this method (as in {@link DefaultCharAppender#reset()})
	 * <p> If the accumulated value is empty (i.e. no characters were appended, or all appended characters where ignored as whitespace or padding), then the written value will be the {@link DefaultCharAppender#emptyValue} attribute defined in the constructor of this class.
	 *
	 * @param writer the output writer
	 * @throws IOException if an error occurs while writing to the output.
	 */
	public final void writeCharsAndReset(Writer writer) throws IOException {
		if (index - whitespaceCount > 0) {
			writer.write(chars, 0, index - whitespaceCount);
		} else if (emptyChars != null) {
			writer.write(emptyChars, 0, emptyChars.length);
		}
		index = 0;
		whitespaceCount = 0;
	}

	/**
	 * Appends the newline character sequence specified in {@link BaseFormat#getLineSeparator()}
	 */
	public final void appendNewLine() {
		if (index + NUM_2 >= chars.length) {
			expand();
		}
		chars[index++] = lineSeparator1;
		if (lineSeparator2 != SYMBOL_NULL_CHAR) {
			chars[index++] = lineSeparator2;
		}
	}

	/**
	 * Configures the appender to allow line ending sequences to be appended as-is, without replacing them by the
	 * normalized line separator character.
	 *
	 * @param enableDenormalizedLineEndings flag indicating whether denormalized line endings are allowed. The writer
	 *                                      won't convert line separators automatically.
	 */
	public final void enableDenormalizedLineEndings(boolean enableDenormalizedLineEndings) {
		this.denormalizeLineEndings = enableDenormalizedLineEndings;
	}

}
