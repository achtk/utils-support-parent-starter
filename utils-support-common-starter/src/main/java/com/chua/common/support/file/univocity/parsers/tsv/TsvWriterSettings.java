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

import com.chua.common.support.file.univocity.parsers.common.AbstractCommonWriterSettings;

import java.util.Map;

/**
 * This is the configuration class used by the TSV writer ({@link TsvWriter})
 *
 * <p>It does not offer additional configuration options on top of the ones provided by the {@link AbstractCommonWriterSettings}</p>
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see com.chua.common.support.file.univocity.parsers.tsv.TsvWriter
 * @see com.chua.common.support.file.univocity.parsers.tsv.TsvFormat
 * @see AbstractCommonWriterSettings
 */
public class TsvWriterSettings extends AbstractCommonWriterSettings<TsvFormat> {


	private boolean lineJoiningEnabled = false;

	/**
	 * Identifies whether values containing line endings should have the line separator written using
	 * the escape character (defined by {@link TsvFormat#getEscapeChar()} followed by the actual line separator character
	 * instead of other characters such as the standard letters 'n' and 'r'
	 * <p>
	 * When line joining is disabled (the default), the {@link TsvWriter} will convert new line characters into
	 * sequences containing the escape character (typically '\') followed by characters 'n' or 'r'.
	 * No matter how many line separators the values written contain, the will be escaped and the entire output
	 * of a record will be written into a single line of text. For example, '\n' and '\r' characters will be
	 * written as: {@code '\'+'n'} and {@code '\'+'r'}.
	 * <p>
	 * If line joining is enabled, the {@link TsvWriter} will convert line new line characters into sequences
	 * containing the escape character, followed by characters '\n', '\r' or both.
	 * A new line of text will be generated for each line separator found in the value to be written, "marking" the end
	 * of each line with the escape character to indicate the record continues on the next line. For example, '\n' and '\r'
	 * characters will be written as: {@code '\'+'\n'} and {@code '\'+'\r'}.
	 *
	 * @return {@code true} if line joining is enabled, otherwise {@code false}
	 */
	public boolean isLineJoiningEnabled() {
		return lineJoiningEnabled;
	}

	/**
	 * Defines how the writer should handle the escaping of line separators.
	 * Values containing line endings should be escaped and the line separator characters can be written using
	 * the escape character (defined by {@link TsvFormat#getEscapeChar()} followed by the actual line separator character
	 * instead of other characters such as the standard letters 'n' and 'r'
	 * <p>
	 * When line joining is disabled (the default), the {@link TsvWriter} will convert new line characters into
	 * sequences containing the escape character (typically '\') followed by characters 'n' or 'r'.
	 * No matter how many line separators the values written contain, the will be escaped and the entire output
	 * of a record will be written into a single line of text. For example, '\n' and '\r' characters will be
	 * written as: {@code '\'+'n'} and {@code '\'+'r'}.
	 * <p>
	 * If line joining is enabled, the {@link TsvWriter} will convert line new line characters into sequences
	 * containing the escape character, followed by characters '\n', '\r' or both.
	 * A new line of text will be generated for each line separator found in the value to be written, "marking" the end
	 * of each line with the escape character to indicate the record continues on the next line. For example, '\n' and '\r'
	 * characters will be written as: {@code '\'+'\n'} and {@code '\'+'\r'}.
	 *
	 * @param lineJoiningEnabled a flag indicating whether or not to enable line joining.
	 */
	public void setLineJoiningEnabled(boolean lineJoiningEnabled) {
		this.lineJoiningEnabled = lineJoiningEnabled;
	}

	/**
	 * Returns the default TsvFormat.
	 *
	 * @return and instance of TsvFormat configured to produce TSV outputs.
	 */
	@Override
	protected TsvFormat createDefaultFormat() {
		return new TsvFormat();
	}

	@Override
	protected void addConfiguration(Map<String, Object> out) {
		super.addConfiguration(out);
	}

	@Override
	public final TsvWriterSettings clone() {
		return (TsvWriterSettings) super.clone();
	}

	@Override
	public final TsvWriterSettings clone(boolean clearInputSpecificSettings) {
		return (TsvWriterSettings) super.clone(clearInputSpecificSettings);
	}
}
