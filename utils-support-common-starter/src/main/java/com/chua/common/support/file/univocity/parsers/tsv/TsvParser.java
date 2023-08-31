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
package com.chua.common.support.file.univocity.parsers.tsv;

import com.chua.common.support.file.univocity.parsers.common.AbstractParser;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_SPACE_CHAR;
import static com.chua.common.support.constant.CommonConstant.SYMBOL_T_CHAR;

/**
 * A very fast TSV parser implementation.
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see TsvFormat
 * @see TsvParserSettings
 * @see TsvWriter
 * @see AbstractParser
 */
public class TsvParser extends AbstractParser<TsvParserSettings> {

	private final boolean joinLines;

	private final char newLine;
	private final char escapeChar;
	private final char escapedTabChar;

	/**
	 * The TsvParser supports all settings provided by {@link TsvParserSettings}, and requires this configuration to be properly initialized.
	 *
	 * @param settings the parser configuration
	 */
	public TsvParser(TsvParserSettings settings) {
		super(settings);
		joinLines = settings.isLineJoiningEnabled();

		TsvFormat format = settings.getFormat();
		newLine = format.getNormalizedNewline();
		escapeChar = settings.getFormat().getEscapeChar();
		escapedTabChar = format.getEscapedTabChar();
	}

	@Override
	protected void initialize() {
		output.trim = ignoreTrailingWhitespace;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void parseRecord() {
		if (ignoreLeadingWhitespace && ch != SYMBOL_T_CHAR && ch <= SYMBOL_SPACE_CHAR && whitespaceRangeStart < ch) {
			ch = input.skipWhitespace(ch, SYMBOL_T_CHAR, escapeChar);
		}

		while (ch != newLine) {
			parseField();
			if (ch != newLine) {
				ch = input.nextChar();
				if (ch == newLine) {
					output.emptyParsed();
				}
			}
		}
	}

	private void parseField() {
		if (ignoreLeadingWhitespace && ch != SYMBOL_T_CHAR && ch <= SYMBOL_SPACE_CHAR && whitespaceRangeStart < ch) {
			ch = input.skipWhitespace(ch, SYMBOL_T_CHAR, escapeChar);
		}

		if (ch == SYMBOL_T_CHAR) {
			output.emptyParsed();
		} else {
			while (ch != SYMBOL_T_CHAR && ch != newLine) {
				if (ch == escapeChar) {
					ch = input.nextChar();
					if (ch == 't' || ch == escapedTabChar) {
						output.appender.append(SYMBOL_T_CHAR);
					} else if (ch == 'n') {
						output.appender.append('\n');
					} else if (ch == '\\') {
						output.appender.append('\\');
					} else if (ch == 'r') {
						output.appender.append('\r');
					} else if (ch == newLine && joinLines) {
						output.appender.append(newLine);
					} else {
						output.appender.append(escapeChar);
						if (ch == newLine || ch == '\t') {
							break;
						}
						output.appender.append(ch);
					}
					ch = input.nextChar();
				} else {
					ch = output.appender.appendUntil(ch, input, '\t', escapeChar, newLine);
				}
			}

			output.valueParsed();
		}
	}
}
