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
package com.chua.common.support.file.univocity.parsers.csv;

import com.chua.common.support.file.univocity.parsers.common.BaseCommonParserSettings;
import com.chua.common.support.file.univocity.parsers.common.BaseFormat;
import com.chua.common.support.file.univocity.parsers.common.TextParsingException;
import com.chua.common.support.file.univocity.parsers.common.input.CharAppender;
import com.chua.common.support.file.univocity.parsers.common.input.DefaultCharAppender;
import com.chua.common.support.file.univocity.parsers.common.input.ExpandingCharAppender;

import java.util.Arrays;
import java.util.Map;

/**
 * This is the configuration class used by the CSV parser ({@link CsvParser})
 *
 * <p>In addition to the configuration options provided by {@link BaseCommonParserSettings}, the CSVParserSettings include:
 *
 * <ul>
 * <li><b>emptyValue <i>(defaults to null)</i>:</b> Defines a replacement string to signify an empty value (which is not a null value)
 * <p>When reading, if the parser does not read any character from the input, and the input is within quotes, the empty is used instead of an empty string</li>
 * </ul>
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see com.chua.common.support.file.univocity.parsers.csv.CsvParser
 * @see com.chua.common.support.file.univocity.parsers.csv.CsvFormat
 * @see BaseCommonParserSettings
 */
public class CsvParserSettings extends BaseCommonParserSettings<CsvFormat> {

    private String emptyValue = null;
    private boolean parseUnescapedQuotes = true;
    private boolean parseUnescapedQuotesUntilDelimiter = true;
    private boolean escapeUnquotedValues = false;
    private boolean keepEscapeSequences = false;
    private boolean keepQuotes = false;
    private boolean normalizeLineEndingsWithinQuotes = true;

    private boolean ignoreTrailingWhitespacesInQuotes = false;
    private boolean ignoreLeadingWhitespacesInQuotes = false;

    private boolean delimiterDetectionEnabled = false;
    private boolean quoteDetectionEnabled = false;
    private UnescapedQuoteHandling unescapedQuoteHandling = null;
    private char[] delimitersForDetection = null;
    private int formatDetectorRowSampleCount = 20;

    /**
     * Returns the String representation of an empty value (defaults to null)
     *
     * <p>When reading, if the parser does not read any character from the input, and the input is within quotes, the empty is used instead of an empty string
     *
     * @return the String representation of an empty value
     */
    public String getEmptyValue() {
        return emptyValue;
    }

    /**
     * Sets the String representation of an empty value (defaults to null)
     *
     * <p>When reading, if the parser does not read any character from the input, and the input is within quotes, the empty is used instead of an empty string
     *
     * @param emptyValue the String representation of an empty value
     */
    public void setEmptyValue(String emptyValue) {
        this.emptyValue = emptyValue;
    }

    /**
     * Returns an instance of CharAppender with the configured limit of maximum characters per column and the default value used to represent an empty value
     * (when the String parsed from the input, within quotes, is empty)
     *
     * <p>This overrides the parent's version because the CSV parser does not rely on the appender to identify null values, but on the other hand, the appender
     * is required to identify empty values.
     *
     * @return an instance of CharAppender with the configured limit of maximum characters per column and the default value used to represent an empty value
     * (when the String parsed from the input, within quotes, is empty)
     */
    @Override
    protected CharAppender newCharAppender() {
        int chars = getMaxCharsPerColumn();
        if (chars != -1) {
            return new DefaultCharAppender(chars, emptyValue, getWhitespaceRangeStart());
        } else {
            return new ExpandingCharAppender(emptyValue, getWhitespaceRangeStart());
        }
    }

    /**
     * Returns the default CsvFormat configured to handle CSV inputs compliant to the <a href="http://tools.ietf.org/html/rfc4180">RFC4180</a> standard.
     *
     * @return and instance of CsvFormat configured to handle CSV inputs compliant to the <a href="http://tools.ietf.org/html/rfc4180">RFC4180</a> standard.
     */
    @Override
    protected CsvFormat createDefaultFormat() {
        return new CsvFormat();
    }

    /**
     * Indicates whether the CSV parser should accept unescaped quotes inside quoted values and parse them normally. Defaults to {@code true}.
     *
     * @return a flag indicating whether or not the CSV parser should accept unescaped quotes inside quoted values.
     *  use {@link #getUnescapedQuoteHandling()} instead. The configuration returned by {@link #getUnescapedQuoteHandling()} will override this
     * setting if not null.
     */
    public boolean isParseUnescapedQuotes() {
        return parseUnescapedQuotes || (unescapedQuoteHandling != null && unescapedQuoteHandling != UnescapedQuoteHandling.RAISE_ERROR);
    }

    /**
     * Configures how to handle unescaped quotes inside quoted values. If set to {@code true}, the parser will parse the quote normally as part of the value.
     * If set the {@code false}, a {@link TextParsingException} will be thrown. Defaults to {@code true}.
     *
     * @param parseUnescapedQuotes indicates whether or not the CSV parser should accept unescaped quotes inside quoted values.
     * use {@link #setUnescapedQuoteHandling(UnescapedQuoteHandling)} instead. The configuration returned by {@link #getUnescapedQuoteHandling()}
     * will override this setting if not null.
     */
    public void setParseUnescapedQuotes(boolean parseUnescapedQuotes) {
        this.parseUnescapedQuotes = parseUnescapedQuotes;
    }

    /**
     * When parsing unescaped quotes, indicates the parser should stop accumulating characters and consider the value parsed when a delimiter is found.
     * (defaults to {@code true})
     *
     * @return a flag indicating that the parser should stop accumulating values when a field delimiter character is
     * found when parsing unquoted and unescaped values.
     *  use {@link #getUnescapedQuoteHandling()} instead. The configuration returned by {@link #getUnescapedQuoteHandling()} will override this
     * setting if not null.
     */
    public boolean isParseUnescapedQuotesUntilDelimiter() {
        return (parseUnescapedQuotesUntilDelimiter && isParseUnescapedQuotes()) || (unescapedQuoteHandling == UnescapedQuoteHandling.STOP_AT_DELIMITER || unescapedQuoteHandling == UnescapedQuoteHandling.SKIP_VALUE);
    }

    /**
     * Configures the parser to process values with unescaped quotes, and stop accumulating characters and consider the value parsed when a delimiter is found.
     * (defaults to {@code true})
     *
     * @param parseUnescapedQuotesUntilDelimiter a flag indicating that the parser should stop accumulating values when a field delimiter character is
     *                                           found when parsing unquoted and unescaped values.
     *  use {@link #setUnescapedQuoteHandling(UnescapedQuoteHandling)} instead. The configuration returned by {@link #getUnescapedQuoteHandling()}
     * will override this setting if not null.
     */
    public void setParseUnescapedQuotesUntilDelimiter(boolean parseUnescapedQuotesUntilDelimiter) {
        if (parseUnescapedQuotesUntilDelimiter) {
            parseUnescapedQuotes = true;
        }
        this.parseUnescapedQuotesUntilDelimiter = parseUnescapedQuotesUntilDelimiter;
    }

    /**
     * Indicates whether escape sequences should be processed in unquoted values. Defaults to {@code false}.
     *
     * <p>By default, this is disabled and if the input is {@code A""B,C}, the resulting value will be
     * {@code [A""B] and [C]} (i.e. the content is read as-is). However, if the parser is configured
     * to process escape sequences in unquoted values, the result will be {@code [A"B] and [C]}</p>
     *
     * @return true if escape sequences should be processed in unquoted values, otherwise false
     */
    public boolean isEscapeUnquotedValues() {
        return escapeUnquotedValues;
    }

    /**
     * Configures the parser to process escape sequences in unquoted values. Defaults to {@code false}.
     *
     * <p>By default, this is disabled and if the input is {@code A""B,C}, the resulting value will be
     * {@code [A""B] and [C]} (i.e. the content is read as-is). However, if the parser is configured
     * to process escape sequences in unquoted values, the result will be {@code [A"B] and [C]}</p>
     *
     * @param escapeUnquotedValues a flag indicating whether escape sequences should be processed in unquoted values
     */
    public void setEscapeUnquotedValues(boolean escapeUnquotedValues) {
        this.escapeUnquotedValues = escapeUnquotedValues;
    }

    /**
     * Indicates whether the parser should keep any escape sequences if they are present in the input (i.e. a quote escape sequence such as two double quotes
     * {@code ""} won't be replaced by a single double quote {@code "}).
     * <p>This is disabled by default</p>
     *
     * @return a flag indicating whether escape sequences should be kept (and not replaced) by the parser.
     */
    public final boolean isKeepEscapeSequences() {
        return keepEscapeSequences;
    }

    /**
     * Configures the parser to keep any escape sequences if they are present in the input (i.e. a quote escape sequence such as 2 double quotes {@code ""}
     * won't be replaced by a single double quote {@code "}).
     * <p>This is disabled by default</p>
     *
     * @param keepEscapeSequences the flag indicating whether escape sequences should be kept (and not replaced) by the parser.
     */
    public final void setKeepEscapeSequences(boolean keepEscapeSequences) {
        this.keepEscapeSequences = keepEscapeSequences;
    }

    /**
     * Returns a flag indicating whether the parser should analyze the input to discover the column delimiter character.
     * <p>Note that the detection process is not guaranteed to discover the correct column delimiter. In this case the delimiter provided by {@link
     * CsvFormat#getDelimiter()} will be used</p>
     *
     * @return a flag indicating whether the parser should analyze the input to discover the column delimiter character.
     */
    public final boolean isDelimiterDetectionEnabled() {
        return delimiterDetectionEnabled;
    }

    /**
     * Configures the parser to analyze the input before parsing to discover the column delimiter character.
     * <p>Note that the detection process is not guaranteed to discover the correct column delimiter.
     * The first character in the list of delimiters allowed for detection will be used, if available, otherwise
     * the delimiter returned by {@link CsvFormat#getDelimiter()} will be used.</p>
     *
     * @param separatorDetectionEnabled the flag to enable/disable discovery of the column delimiter character.
     *                                  to {@code true}, in order of priority.
     */
    public final void setDelimiterDetectionEnabled(boolean separatorDetectionEnabled) {
        this.setDelimiterDetectionEnabled(separatorDetectionEnabled, new char[0]);
    }

    /**
     * Configures the parser to analyze the input before parsing to discover the column delimiter character.
     * <p>Note that the detection process is not guaranteed to discover the correct column delimiter.
     * The first character in the list of delimiters allowed for detection will be used, if available, otherwise
     * the delimiter returned by {@link CsvFormat#getDelimiter()} will be used.</p>
     *
     * @param separatorDetectionEnabled the flag to enable/disable discovery of the column delimiter character.
     * @param delimitersForDetection    possible delimiters for detection when {@link #isDelimiterDetectionEnabled()} evaluates
     *                                  to {@code true}, in order of priority.
     */
    public final void setDelimiterDetectionEnabled(boolean separatorDetectionEnabled, char... delimitersForDetection) {
        this.delimiterDetectionEnabled = separatorDetectionEnabled;
        this.delimitersForDetection = delimitersForDetection;
    }

    /**
     * Returns a flag indicating whether the parser should analyze the input to discover the quote character. The quote escape will also be detected as part of
     * this process.
     * <p> Note that the detection process is not guaranteed to discover the correct quote &amp; escape.
     * In this case the characters provided by {@link CsvFormat#getQuote()} and {@link CsvFormat#getQuoteEscape()} will be used </p>
     *
     * @return a flag indicating whether the parser should analyze the input to discover the quote character. The quote escape will also be detected as part of
     * this process.
     */
    public final boolean isQuoteDetectionEnabled() {
        return quoteDetectionEnabled;
    }

    /**
     * Configures the parser to analyze the input before parsing to discover the quote character. The quote escape will also be detected as part of this
     * process.
     * <p> Note that the detection process is not guaranteed to discover the correct quote &amp; escape.
     * In this case the characters provided by {@link CsvFormat#getQuote()} and {@link CsvFormat#getQuoteEscape()} will be used </p>
     *
     * @param quoteDetectionEnabled the flag to enable/disable discovery of the quote character. The quote escape will also be detected as part of this process.
     */
    public final void setQuoteDetectionEnabled(boolean quoteDetectionEnabled) {
        this.quoteDetectionEnabled = quoteDetectionEnabled;
    }

    /**
     * Convenience method to turn on all format detection features in a single method call, namely:
     * <ul>
     * <li>{@link #setDelimiterDetectionEnabled(boolean, char[])} </li>
     * <li>{@link #setQuoteDetectionEnabled(boolean)} </li>
     * <li>{@link #setLineSeparatorDetectionEnabled(boolean)} </li>
     * </ul>
     */
    public final void detectFormatAutomatically() {
        this.detectFormatAutomatically(new char[0]);
    }

    /**
     * Convenience method to turn on all format detection features in a single method call, namely:
     * <ul>
     * <li>{@link #setDelimiterDetectionEnabled(boolean, char[])} </li>
     * <li>{@link #setQuoteDetectionEnabled(boolean)} </li>
     * <li>{@link #setLineSeparatorDetectionEnabled(boolean)} </li>
     * </ul>
     *
     * @param delimitersForDetection possible delimiters for detection, in order of priority.
     */
    public final void detectFormatAutomatically(char... delimitersForDetection) {
        this.setDelimiterDetectionEnabled(true, delimitersForDetection);
        this.setQuoteDetectionEnabled(true);
        this.setLineSeparatorDetectionEnabled(true);
    }

    /**
     * Flag indicating whether the parser should replace line separators, specified in {@link BaseFormat#getLineSeparator()}
     * by the normalized line separator character specified in {@link BaseFormat#getNormalizedNewline()}, even on quoted values.
     * <p>
     * This is enabled by default and is used to ensure data be read on any platform without introducing unwanted blank lines.
     * <p>
     * For example, consider the quoted value {@code "Line1 \r\n Line2"}. If this is parsed using {@code "\r\n"} as
     * the line separator sequence, and the normalized new line is set to {@code '\n'} (the default), the output will be:
     * <p>
     * {@code [Line1 \n Line2]}
     * <p>
     * However, if the value is meant to be kept untouched, and the original line separator should be maintained, set
     * the {@link #normalizeLineEndingsWithinQuotes} to {@code false}. This will make the parser read the value as-is, producing:
     * <p>
     * {@code [Line1 \r\n Line2]}
     *
     * @return {@code true} if line separators in quoted values will be normalized, {@code false} otherwise
     */
    public boolean isNormalizeLineEndingsWithinQuotes() {
        return normalizeLineEndingsWithinQuotes;
    }

    /**
     * Configures the parser to replace line separators, specified in {@link BaseFormat#getLineSeparator()}
     * by the normalized line separator character specified in {@link BaseFormat#getNormalizedNewline()}, even on quoted values.
     * <p>
     * This is enabled by default and is used to ensure data be read on any platform without introducing unwanted blank lines.
     * <p>
     * For example, consider the quoted value {@code "Line1 \r\n Line2"}. If this is parsed using {@code "\r\n"} as
     * the line separator sequence, and the normalized new line is set to {@code '\n'} (the default), the output will be:
     * <p>
     * {@code [Line1 \n Line2]}
     * <p>
     * However, if the value is meant to be kept untouched, and the original line separator should be maintained, set
     * the {@link #normalizeLineEndingsWithinQuotes} to {@code false}. This will make the parser read the value as-is, producing:
     * <p>
     * {@code [Line1 \r\n Line2]}
     *
     * @param normalizeLineEndingsWithinQuotes flag indicating whether line separators in quoted values should be replaced by
     *                                         the the character specified in {@link BaseFormat#getNormalizedNewline()} .
     */
    public void setNormalizeLineEndingsWithinQuotes(boolean normalizeLineEndingsWithinQuotes) {
        this.normalizeLineEndingsWithinQuotes = normalizeLineEndingsWithinQuotes;
    }

    /**
     * Configures the handling of values with unescaped quotes.
     * Defaults to {@code null}, for backward compatibility with {@link #isParseUnescapedQuotes()} and {@link #isParseUnescapedQuotesUntilDelimiter()}.
     * If set to a non-null value, this setting will override the configuration of {@link #isParseUnescapedQuotes()} and {@link
     * #isParseUnescapedQuotesUntilDelimiter()}.
     *
     * @param unescapedQuoteHandling the handling method to be used when unescaped quotes are found in the input.
     */
    public void setUnescapedQuoteHandling(UnescapedQuoteHandling unescapedQuoteHandling) {
        this.unescapedQuoteHandling = unescapedQuoteHandling;
    }

    /**
     * Returns the method of handling values with unescaped quotes.
     * Defaults to {@code null}, for backward compatibility with {@link #isParseUnescapedQuotes()} and {@link #isParseUnescapedQuotesUntilDelimiter()}
     * If set to a non-null value, this setting will override the configuration of {@link #isParseUnescapedQuotes()} and {@link
     * #isParseUnescapedQuotesUntilDelimiter()}.
     *
     * @return the handling method to be used when unescaped quotes are found in the input, or {@code null} if not set.
     */
    public UnescapedQuoteHandling getUnescapedQuoteHandling() {
        return this.unescapedQuoteHandling;
    }


    /**
     * Flag indicating whether the parser should keep enclosing quote characters in the values parsed from the input.
     * <p>Defaults to {@code false}</p>
     *
     * @return a flag indicating whether enclosing quotes should be maintained when parsing quoted values.
     */
    public boolean getKeepQuotes() {
        return keepQuotes;
    }

    /**
     * Configures the parser to keep enclosing quote characters in the values parsed from the input.
     * <p>Defaults to {@code false}</p>
     *
     * @param keepQuotes flag indicating whether enclosing quotes should be maintained when parsing quoted values.
     */
    public void setKeepQuotes(boolean keepQuotes) {
        this.keepQuotes = keepQuotes;
    }

    @Override
    protected void addConfiguration(Map<String, Object> out) {
        super.addConfiguration(out);
        out.put("Empty value", emptyValue);
        out.put("Unescaped quote handling", unescapedQuoteHandling);
        out.put("Escape unquoted values", escapeUnquotedValues);
        out.put("Keep escape sequences", keepEscapeSequences);
        out.put("Keep quotes", keepQuotes);
        out.put("Normalize escaped line separators", normalizeLineEndingsWithinQuotes);
        out.put("Autodetect column delimiter", delimiterDetectionEnabled);
        out.put("Autodetect quotes", quoteDetectionEnabled);
        out.put("Delimiters for detection", Arrays.toString(delimitersForDetection));
        out.put("Ignore leading whitespaces in quotes", ignoreLeadingWhitespacesInQuotes);
        out.put("Ignore trailing whitespaces in quotes", ignoreTrailingWhitespacesInQuotes);
    }

    @Override
    public final CsvParserSettings clone() {
        return (CsvParserSettings) super.clone();
    }

    @Override
    public final CsvParserSettings clone(boolean clearInputSpecificSettings) {
        return (CsvParserSettings) super.clone(clearInputSpecificSettings);
    }

    /**
     * Returns the sequence of possible delimiters for detection when {@link #isDelimiterDetectionEnabled()} evaluates
     * to {@code true}, in order of priority.
     *
     * @return the possible delimiter characters, in order of priority.
     */
    public final char[] getDelimitersForDetection() {
        return this.delimitersForDetection;
    }

    /**
     * Returns whether or not trailing whitespaces from within quoted values should be skipped  (defaults to false)
     * <p>
     * Note: if {@link #keepQuotes} evaluates to {@code true}, values won't be trimmed.
     *
     * @return true if trailing whitespaces from quoted values should be skipped, false otherwise
     */
    public boolean getIgnoreTrailingWhitespacesInQuotes() {
        return ignoreTrailingWhitespacesInQuotes;
    }

    /**
     * Defines whether or not trailing whitespaces from quoted values should be skipped  (defaults to false)
     * <p>
     * Note: if {@link #keepQuotes} evaluates to {@code true}, values won't be trimmed.
     *
     * @param ignoreTrailingWhitespacesInQuotes whether trailing whitespaces from quoted values should be skipped
     */
    public void setIgnoreTrailingWhitespacesInQuotes(boolean ignoreTrailingWhitespacesInQuotes) {
        this.ignoreTrailingWhitespacesInQuotes = ignoreTrailingWhitespacesInQuotes;
    }

    /**
     * Returns whether or not leading whitespaces from quoted values should be skipped  (defaults to false)
     * <p>
     * Note: if {@link #keepQuotes} evaluates to {@code true}, values won't be trimmed.
     *
     * @return true if leading whitespaces from quoted values should be skipped, false otherwise
     */
    public boolean getIgnoreLeadingWhitespacesInQuotes() {
        return ignoreLeadingWhitespacesInQuotes;
    }

    /**
     * Defines whether or not leading whitespaces from quoted values should be skipped  (defaults to false)
     * <p>
     * Note: if {@link #keepQuotes} evaluates to {@code true}, values won't be trimmed.
     *
     * @param ignoreLeadingWhitespacesInQuotes whether leading whitespaces from quoted values should be skipped
     */
    public void setIgnoreLeadingWhitespacesInQuotes(boolean ignoreLeadingWhitespacesInQuotes) {
        this.ignoreLeadingWhitespacesInQuotes = ignoreLeadingWhitespacesInQuotes;
    }

    /**
     * Configures the parser to trim any whitespaces around values extracted from within quotes. Shorthand for
     * {@link #setIgnoreLeadingWhitespacesInQuotes(boolean)} and {@link #setIgnoreTrailingWhitespacesInQuotes(boolean)}
     * <p>
     * Note: if {@link #keepQuotes} evaluates to {@code true}, values won't be trimmed.
     *
     * @param trim a flag indicating whether whitespaces around values extracted from a quoted field should be removed
     */
    public final void trimQuotedValues(boolean trim) {
        setIgnoreTrailingWhitespacesInQuotes(trim);
        setIgnoreLeadingWhitespacesInQuotes(trim);
    }

    /**
     * Returns the number of sample rows used in the CSV format auto-detection process (defaults to 20)
     *
     * @return the number of sample rows used in the CSV format auto-detection process
     */
    public int getFormatDetectorRowSampleCount() {
        return formatDetectorRowSampleCount;
    }

    /**
     * Updates the number of sample rows used in the CSV format auto-detection process (defaults to 20)
     *
     * @param formatDetectorRowSampleCount the number of sample rows used in the CSV format auto-detection process
     */
    public void setFormatDetectorRowSampleCount(int formatDetectorRowSampleCount) {
        this.formatDetectorRowSampleCount = formatDetectorRowSampleCount <= 0 ? 20 : formatDetectorRowSampleCount;
    }
}
