package com.chua.common.support.file.univocity.parsers.csv;

import com.chua.common.support.file.univocity.parsers.common.BaseCommonParserSettings;
import com.chua.common.support.file.univocity.parsers.common.BaseParser;
import com.chua.common.support.file.univocity.parsers.common.TextParsingException;
import com.chua.common.support.file.univocity.parsers.common.input.*;

import java.io.Reader;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_BLANK_CHAR;
import static com.chua.common.support.constant.CommonConstant.SYMBOL_NULL_CHAR;
import static com.chua.common.support.file.univocity.parsers.csv.UnescapedQuoteHandling.*;

/**
 * A very fast CSV parser implementation.
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see CsvFormat
 * @see CsvParserSettings
 * @see CsvWriter
 * @see BaseParser
 */
public final class CsvParser extends BaseParser<CsvParserSettings> {

    private boolean parseUnescapedQuotes;
    private boolean parseUnescapedQuotesUntilDelimiter;
    private boolean backToDelimiter;
    private final boolean doNotEscapeUnquotedValues;
    private final boolean keepEscape;
    private final boolean keepQuotes;

    private boolean unescaped;
    private char prev;
    private char delimiter;
    private char[] multiDelimiter;
    private char quote;
    private char quoteEscape;
    private char escapeEscape;
    private char newLine;
    private final DefaultCharAppender whitespaceAppender;
    private final boolean normalizeLineEndingsInQuotes;
    private UnescapedQuoteHandling quoteHandling;
    private final String nullValue;
    private final int maxColumnLength;
    private final String emptyValue;
    private final boolean trimQuotedLeading;
    private final boolean trimQuotedTrailing;
    private char[] delimiters;
    private int match = 0;
    private int formatDetectorRowSampleCount;

    /**
     * The CsvParser supports all settings provided by {@link CsvParserSettings}, and requires this configuration to be properly initialized.
     *
     * @param settings the parser configuration
     */
    public CsvParser(CsvParserSettings settings) {
        super(settings);
        parseUnescapedQuotes = settings.isParseUnescapedQuotes();
        parseUnescapedQuotesUntilDelimiter = settings.isParseUnescapedQuotesUntilDelimiter();
        doNotEscapeUnquotedValues = !settings.isEscapeUnquotedValues();
        keepEscape = settings.isKeepEscapeSequences();
        keepQuotes = settings.getKeepQuotes();
        normalizeLineEndingsInQuotes = settings.isNormalizeLineEndingsWithinQuotes();
        nullValue = settings.getNullValue();
        emptyValue = settings.getEmptyValue();
        maxColumnLength = settings.getMaxCharsPerColumn();
        trimQuotedTrailing = settings.getIgnoreTrailingWhitespacesInQuotes();
        trimQuotedLeading = settings.getIgnoreLeadingWhitespacesInQuotes();
        formatDetectorRowSampleCount = settings.getFormatDetectorRowSampleCount();
        updateFormat(settings.getFormat());

        whitespaceAppender = new ExpandingCharAppender(10, "", whitespaceRangeStart);

        this.quoteHandling = settings.getUnescapedQuoteHandling();
        if (quoteHandling == null) {
            if (parseUnescapedQuotes) {
                if (parseUnescapedQuotesUntilDelimiter) {
                    quoteHandling = STOP_AT_DELIMITER;
                } else {
                    quoteHandling = STOP_AT_CLOSING_QUOTE;
                }
            } else {
                quoteHandling = RAISE_ERROR;
            }
        } else {
            backToDelimiter = quoteHandling == BACK_TO_DELIMITER;
            parseUnescapedQuotesUntilDelimiter = quoteHandling == STOP_AT_DELIMITER || quoteHandling == SKIP_VALUE || backToDelimiter;
            parseUnescapedQuotes = quoteHandling != RAISE_ERROR;
        }
    }


    @Override
    protected final void parseRecord() {
        if (multiDelimiter == null) {
            parseSingleDelimiterRecord();
        } else {
            parseMultiDelimiterRecord();
        }
    }

    /**
     * 解析单个记录分隔符
     */
    private final void parseSingleDelimiterRecord() {
        if (ch <= SYMBOL_BLANK_CHAR && ignoreLeadingWhitespace && whitespaceRangeStart < ch) {
            ch = input.skipWhitespace(ch, delimiter, quote);
        }

        while (ch != newLine) {
            if (ch <= ' ' && ignoreLeadingWhitespace && whitespaceRangeStart < ch) {
                ch = input.skipWhitespace(ch, delimiter, quote);
            }

            if (ch == delimiter || ch == newLine) {
                output.emptyParsed();
            } else {
                unescaped = false;
                prev = '\0';
                if (ch == quote) {
                    input.enableNormalizeLineEndings(normalizeLineEndingsInQuotes);
                    int len = output.appender.length();
                    if (len == 0) {
                        String value = input.getQuotedString(quote, quoteEscape, escapeEscape, maxColumnLength, delimiter, newLine, keepQuotes, keepEscape, trimQuotedLeading, trimQuotedTrailing);
                        if (value != null) {
                            output.valueParsed(value == "" ? emptyValue : value);
                            input.enableNormalizeLineEndings(true);
                            try {
                                ch = input.nextChar();
                                if (ch == delimiter) {
                                    try {
                                        ch = input.nextChar();
                                        if (ch == newLine) {
                                            output.emptyParsed();
                                        }
                                    } catch (EofException e) {
                                        output.emptyParsed();
                                        return;
                                    }
                                }
                            } catch (EofException e) {
                                return;
                            }
                            continue;
                        }
                    } else if (len == -1 && input.skipQuotedString(quote, quoteEscape, delimiter, newLine)) {
                        output.valueParsed();
                        try {
                            ch = input.nextChar();
                            if (ch == delimiter) {
                                try {
                                    ch = input.nextChar();
                                    if (ch == newLine) {
                                        output.emptyParsed();
                                    }
                                } catch (EofException e) {
                                    output.emptyParsed();
                                    return;
                                }
                            }
                        } catch (EofException e) {
                            return;
                        }
                        continue;
                    }
                    output.trim = trimQuotedTrailing;
                    parseQuotedValue();
                    input.enableNormalizeLineEndings(true);
                    if (!(unescaped && quoteHandling == BACK_TO_DELIMITER && output.appender.length() == 0)) {
                        output.valueParsed();
                    }
                } else if (doNotEscapeUnquotedValues) {
                    String value = null;
                    int len = output.appender.length();
                    if (len == 0) {
                        value = input.getString(ch, delimiter, ignoreTrailingWhitespace, nullValue, maxColumnLength);
                    }
                    if (value != null) {
                        output.valueParsed(value);
                        ch = input.getChar();
                    } else {
                        if (len != -1) {
                            output.trim = ignoreTrailingWhitespace;
                            ch = output.appender.appendUntil(ch, input, delimiter, newLine);
                        } else {
                            if (input.skipString(ch, delimiter)) {
                                ch = input.getChar();
                            } else {
                                ch = output.appender.appendUntil(ch, input, delimiter, newLine);
                            }
                        }
                        output.valueParsed();
                    }
                } else {
                    output.trim = ignoreTrailingWhitespace;
                    parseValueProcessingEscape();
                    output.valueParsed();
                }
            }
            if (ch != newLine) {
                ch = input.nextChar();
                if (ch == newLine) {
                    output.emptyParsed();
                }
            }
        }
    }

    private void skipValue() {
        output.appender.reset();
        output.appender = NoopCharAppender.getInstance();
        if (multiDelimiter == null) {
            ch = NoopCharAppender.getInstance().appendUntil(ch, input, delimiter, newLine);
        } else {
            for (; match < multiDelimiter.length && ch != newLine; ch = input.nextChar()) {
                if (multiDelimiter[match] == ch) {
                    match++;
                } else {
                    match = 0;
                }
            }
        }
    }

    private void handleValueSkipping(boolean quoted) {
        switch (quoteHandling) {
            case SKIP_VALUE:
                skipValue();
                break;
            case RAISE_ERROR:
                throw new TextParsingException(context, "Unescaped quote character '" + quote
                        + "' inside " + (quoted ? "quoted" : "") + " value of CSV field. To allow unescaped quotes, set 'parseUnescapedQuotes' to 'true' in the CSV parser settings. Cannot parse CSV input.");
            default:
        }
    }

    private void handleUnescapedQuoteInValue() {
        switch (quoteHandling) {
            case BACK_TO_DELIMITER:
            case STOP_AT_CLOSING_QUOTE:
            case STOP_AT_DELIMITER:
                output.appender.append(quote);
                prev = ch;
                parseValueProcessingEscape();
                break;
            default:
                handleValueSkipping(false);
                break;
        }
    }

    private int nextDelimiter() {
        if (multiDelimiter == null) {
            return output.appender.indexOfAny(delimiters, 0);
        } else {
            int lineEnd = output.appender.indexOf(newLine, 0);
            int delimiter = output.appender.indexOf(multiDelimiter, 0);

            return lineEnd != -1 && lineEnd < delimiter ? lineEnd : delimiter;
        }
    }

    private boolean handleUnescapedQuote() {
        unescaped = true;
        switch (quoteHandling) {
            case BACK_TO_DELIMITER:
                int pos;
                int lastPos = 0;
                while ((pos = nextDelimiter()) != -1) {
                    lastPos = pos;
                    String value = output.appender.substring(0, pos);
                    if (keepQuotes && output.appender.charAt(pos - 1) == quote) {
                        value += quote;
                    }
                    output.valueParsed(value);
                    if (output.appender.charAt(pos) == newLine) {
                        output.pendingRecords.add(output.rowParsed());
                        output.appender.remove(0, pos + 1);
                        continue;
                    }
                    if (multiDelimiter == null) {
                        output.appender.remove(0, pos + 1);
                    } else {
                        output.appender.remove(0, pos + multiDelimiter.length);
                    }
                }
                if (keepQuotes && input.lastIndexOf(quote) > lastPos) {
                    output.appender.append(quote);
                }
                output.appender.append(ch);
                prev = '\0';
                if (multiDelimiter == null) {
                    parseQuotedValue();
                } else {
                    parseQuotedValueMultiDelimiter();
                }
                return true;
            case STOP_AT_CLOSING_QUOTE:
            case STOP_AT_DELIMITER:
                output.appender.append(quote);
                output.appender.append(ch);
                prev = ch;
                if (multiDelimiter == null) {
                    parseQuotedValue();
                } else {
                    parseQuotedValueMultiDelimiter();
                }
                return true;
            default:
                handleValueSkipping(true);
                return false;
        }
    }

    private void processQuoteEscape() {
        if (ch == quoteEscape && prev == escapeEscape && escapeEscape != SYMBOL_NULL_CHAR) {
            if (keepEscape) {
                output.appender.append(escapeEscape);
            }
            output.appender.append(quoteEscape);
            ch = '\0';
        } else if (prev == quoteEscape) {
            if (ch == quote) {
                if (keepEscape) {
                    output.appender.append(quoteEscape);
                }
                output.appender.append(quote);
                ch = '\0';
            } else {
                output.appender.append(prev);
            }
        } else if (ch == quote && prev == quote) {
            output.appender.append(quote);
        } else if (prev == quote) {
            handleUnescapedQuoteInValue();
        }
    }

    private void parseValueProcessingEscape() {
        while (ch != delimiter && ch != newLine) {
            if (ch != quote && ch != quoteEscape) {
                if (prev == quote) {
                    handleUnescapedQuoteInValue();
                    return;
                }
                output.appender.append(ch);
            } else {
                processQuoteEscape();
            }
            prev = ch;
            ch = input.nextChar();
        }
    }

    /**
     * 解析引用值
     */
    private void parseQuotedValue() {
        if (prev != SYMBOL_NULL_CHAR && parseUnescapedQuotesUntilDelimiter) {
            if (quoteHandling == SKIP_VALUE) {
                skipValue();
                return;
            }
            if (!keepQuotes) {
                output.appender.prepend(quote);
            }
            ch = input.nextChar();
            output.trim = ignoreTrailingWhitespace;
            ch = output.appender.appendUntil(ch, input, delimiter, newLine);
        } else {
            if (keepQuotes && prev == SYMBOL_NULL_CHAR) {
                output.appender.append(quote);
            }
            ch = input.nextChar();

            if (trimQuotedLeading && ch <= SYMBOL_BLANK_CHAR && output.appender.length() == 0) {
                while ((ch = input.nextChar()) <= SYMBOL_BLANK_CHAR) {
                    ;
                }
            }

            while (true) {
                boolean b = prev == quote && (ch <= SYMBOL_BLANK_CHAR && whitespaceRangeStart < ch || ch == delimiter || ch == newLine);
                if (b) {
                    break;
                }

                if (ch != quote && ch != quoteEscape) {
                    if (prev == quote) {
                        if (handleUnescapedQuote()) {
                            if (quoteHandling == SKIP_VALUE) {
                                break;
                            } else {
                                return;
                            }
                        } else {
                            return;
                        }
                    }
                    if (prev == quoteEscape && quoteEscape != '\0') {
                        output.appender.append(quoteEscape);
                    }
                    ch = output.appender.appendUntil(ch, input, quote, quoteEscape, escapeEscape);
                    prev = ch;
                    ch = input.nextChar();
                } else {
                    processQuoteEscape();
                    prev = ch;
                    ch = input.nextChar();
                    boolean b1 = unescaped && (ch == delimiter || ch == newLine);
                    if (b1) {
                        return;
                    }
                }
            }

            // handles whitespaces after quoted value: whitespaces are ignored. Content after whitespaces may be parsed if 'parseUnescapedQuotes' is enabled.
            boolean b2 = ch != delimiter && ch != newLine && ch <= ' ' && whitespaceRangeStart < ch;
            if (b2) {
                whitespaceAppender.reset();
                do {
                    whitespaceAppender.append(ch);
                    ch = input.nextChar();
                    if (ch == newLine) {
                        if (keepQuotes) {
                            output.appender.append(quote);
                        }
                        return;
                    }
                } while (ch <= ' ' && whitespaceRangeStart < ch && ch != delimiter);
                if (ch != delimiter && parseUnescapedQuotes) {
                    if (output.appender instanceof DefaultCharAppender) {
                        output.appender.append(quote);
                        ((DefaultCharAppender) output.appender).append(whitespaceAppender);
                    }

                    boolean b3 = parseUnescapedQuotesUntilDelimiter || ch != quote && ch != quoteEscape;
                    if (b3) {
                        output.appender.append(ch);
                    }

                    prev = ch;
                    parseQuotedValue();
                } else if (keepQuotes) {
                    output.appender.append(quote);
                }
            } else if (keepQuotes) {
                output.appender.append(quote);
            }

            if (ch != delimiter && ch != newLine) {
                throw new TextParsingException(context, "Unexpected character '" + ch + "' following quoted value of CSV field. Expecting '" + delimiter + "'. Cannot parse CSV input.");
            }
        }
    }

    @Override
    protected final InputAnalysisProcess getInputAnalysisProcess() {
        if (settings.isDelimiterDetectionEnabled() || settings.isQuoteDetectionEnabled()) {
            return new AbstractCsvFormatDetector(formatDetectorRowSampleCount, settings, whitespaceRangeStart) {
                @Override
                protected void apply(char delimiter, char quote, char quoteEscape) {
                    if (settings.isDelimiterDetectionEnabled()) {
                        CsvParser.this.delimiter = delimiter;
                        CsvParser.this.delimiters[0] = delimiter;

                    }
                    if (settings.isQuoteDetectionEnabled()) {
                        CsvParser.this.quote = quote;
                        CsvParser.this.quoteEscape = quoteEscape;
                    }
                }
            };
        }
        return null;
    }

    /**
     * Returns the CSV format detected when one of the following settings is enabled:
     * <ul>
     * <li>{@link BaseCommonParserSettings#isLineSeparatorDetectionEnabled()}</li>
     * <li>{@link CsvParserSettings#isDelimiterDetectionEnabled()}</li>
     * <li>{@link CsvParserSettings#isQuoteDetectionEnabled()}</li>
     * </ul>
     * <p>
     * The detected format will be available once the parsing process is initialized (i.e. when {@link BaseParser#beginParsing(Reader) runs}.
     *
     * @return the detected CSV format, or {@code null} if no detection has been enabled or if the parsing process has not been started yet.
     */
    public final CsvFormat getDetectedFormat() {
        CsvFormat out = null;
        if (settings.isDelimiterDetectionEnabled()) {
            out = settings.getFormat().clone();
            out.setDelimiter(this.delimiter);
        }
        if (settings.isQuoteDetectionEnabled()) {
            out = out == null ? settings.getFormat().clone() : out;
            out.setQuote(quote);
            out.setQuoteEscape(quoteEscape);
        }
        if (settings.isLineSeparatorDetectionEnabled()) {
            out = out == null ? settings.getFormat().clone() : out;
            out.setLineSeparator(input.getLineSeparator());
        }
        return out;
    }

    @Override
    protected final boolean consumeValueOnEof() {
        if (ch == quote) {
            if (prev == quote) {
                if (keepQuotes) {
                    output.appender.append(quote);
                }
                return true;
            } else {
                if (!unescaped) {
                    output.appender.append(quote);
                }
            }
        }
        boolean out = prev != '\0' && ch != delimiter && ch != newLine && ch != comment;
        ch = prev = '\0';
        if (match > 0) {
            saveMatchingCharacters();
            return true;
        }
        return out;
    }

    /**
     * Allows changing the format of the input on the fly.
     *
     * @param format the new format to use.
     */
    public final void updateFormat(CsvFormat format) {
        newLine = format.getNormalizedNewline();
        multiDelimiter = format.getDelimiterString().toCharArray();
        if (multiDelimiter.length == 1) {
            multiDelimiter = null;
            delimiter = format.getDelimiter();
            delimiters = new char[]{delimiter, newLine};
        } else {
            delimiters = new char[]{multiDelimiter[0], newLine};
        }
        quote = format.getQuote();
        quoteEscape = format.getQuoteEscape();
        escapeEscape = format.getCharToEscapeQuoteEscaping();
    }

    private void skipWhitespace() {
        while (ch <= SYMBOL_BLANK_CHAR && match < multiDelimiter.length && ch != newLine && ch != quote && whitespaceRangeStart < ch) {
            ch = input.nextChar();
            if (multiDelimiter[match] == ch) {
                if (matchDelimiter()) {
                    output.emptyParsed();
                    ch = input.nextChar();
                }
            }
        }

        saveMatchingCharacters();
    }

    private void saveMatchingCharacters() {
        if (match > 0) {
            if (match < multiDelimiter.length) {
                output.appender.append(multiDelimiter, 0, match);
            }
            match = 0;
        }
    }

    private boolean matchDelimiter() {
        while (ch == multiDelimiter[match]) {
            match++;
            if (match == multiDelimiter.length) {
                break;
            }
            ch = input.nextChar();
        }

        if (multiDelimiter.length == match) {
            match = 0;
            return true;
        }

        if (match > 0) {
            saveMatchingCharacters();
        }

        return false;
    }

    private boolean matchDelimiterAfterQuote() {
        while (ch == multiDelimiter[match]) {
            match++;
            if (match == multiDelimiter.length) {
                break;
            }
            ch = input.nextChar();
        }

        if (multiDelimiter.length == match) {
            match = 0;
            return true;
        }

        return false;
    }

    private void parseMultiDelimiterRecord() {
        if (ch <= SYMBOL_BLANK_CHAR && ignoreLeadingWhitespace && whitespaceRangeStart < ch) {
            skipWhitespace();
        }

        while (ch != newLine) {
            if (ch <= SYMBOL_BLANK_CHAR && ignoreLeadingWhitespace && whitespaceRangeStart < ch) {
                skipWhitespace();
            }

            if (ch == newLine || matchDelimiter()) {
                output.emptyParsed();
            } else {
                unescaped = false;
                prev = '\0';
                if (ch == quote && output.appender.length() == 0) {
                    input.enableNormalizeLineEndings(normalizeLineEndingsInQuotes);
                    output.trim = trimQuotedTrailing;
                    parseQuotedValueMultiDelimiter();
                    input.enableNormalizeLineEndings(true);
                    if (!(unescaped && quoteHandling == BACK_TO_DELIMITER && output.appender.length() == 0)) {
                        output.valueParsed();
                    }
                } else if (doNotEscapeUnquotedValues) {
                    appendUntilMultiDelimiter();
                    if (ignoreTrailingWhitespace) {
                        output.appender.updateWhitespace();
                    }
                    output.valueParsed();
                } else {
                    output.trim = ignoreTrailingWhitespace;
                    parseValueProcessingEscapeMultiDelimiter();
                    output.valueParsed();
                }
            }
            if (ch != newLine) {
                ch = input.nextChar();
                if (ch == newLine) {
                    output.emptyParsed();
                }
            }
        }
    }

    private void appendUntilMultiDelimiter() {
        while (match < multiDelimiter.length && ch != newLine) {
            if (multiDelimiter[match] == ch) {
                match++;
                if (match == multiDelimiter.length) {
                    break;
                }
            } else {
                if (match > 0) {
                    saveMatchingCharacters();
                    continue;
                }
                output.appender.append(ch);
            }
            ch = input.nextChar();
        }
        saveMatchingCharacters();
    }

    /**
     * 解析引用值多分隔符
     */
    private void parseQuotedValueMultiDelimiter() {
        if (prev != SYMBOL_NULL_CHAR && parseUnescapedQuotesUntilDelimiter) {
            if (quoteHandling == SKIP_VALUE) {
                skipValue();
                return;
            }
            if (!keepQuotes) {
                output.appender.prepend(quote);
            }
            ch = input.nextChar();
            output.trim = ignoreTrailingWhitespace;
            appendUntilMultiDelimiter();
        } else {
            if (keepQuotes && prev == SYMBOL_NULL_CHAR) {
                output.appender.append(quote);
            }
            ch = input.nextChar();

            if (trimQuotedLeading && ch <= SYMBOL_BLANK_CHAR && output.appender.length() == 0) {
                while ((ch = input.nextChar()) <= SYMBOL_BLANK_CHAR) {
                    ;
                }
            }

            while (true) {
                boolean b = prev == quote && (ch <= SYMBOL_BLANK_CHAR && whitespaceRangeStart < ch || ch == newLine);
                if (b) {
                    break;
                }

                if (prev == quote && matchDelimiter()) {
                    if (keepQuotes) {
                        output.appender.append(quote);
                    }
                    return;
                }

                if (ch != quote && ch != quoteEscape) {
                    if (prev == quote) {
                        if (handleUnescapedQuote()) {
                            if (quoteHandling == SKIP_VALUE) {
                                break;
                            } else {
                                return;
                            }
                        } else {
                            return;
                        }
                    }
                    if (prev == quoteEscape && quoteEscape != '\0') {
                        output.appender.append(quoteEscape);
                    }
                    ch = output.appender.appendUntil(ch, input, quote, quoteEscape, escapeEscape);
                    prev = ch;
                    ch = input.nextChar();
                } else {
                    processQuoteEscape();
                    prev = ch;
                    ch = input.nextChar();
                    boolean b1 = unescaped && (ch == newLine || matchDelimiter());
                    if (b1) {
                        return;
                    }
                }
            }
        }

        boolean b1 = keepQuotes && (!unescaped || quoteHandling == STOP_AT_CLOSING_QUOTE);
        // handles whitespaces after quoted value: whitespaces are ignored. Content after whitespaces may be parsed if 'parseUnescapedQuotes' is enabled.
        if (ch != newLine && ch <= SYMBOL_BLANK_CHAR && whitespaceRangeStart < ch && !matchDelimiterAfterQuote()) {
            whitespaceAppender.reset();
            do {
                whitespaceAppender.append(ch);
                ch = input.nextChar();
                if (ch == newLine) {
                    if (keepQuotes) {
                        output.appender.append(quote);
                    }
                    return;
                }
                if (matchDelimiterAfterQuote()) {
                    return;
                }
            } while (ch <= ' ' && whitespaceRangeStart < ch);
            if (parseUnescapedQuotes && !matchDelimiterAfterQuote()) {
                if (output.appender instanceof DefaultCharAppender) {
                    output.appender.append(quote);
                    ((DefaultCharAppender) output.appender).append(whitespaceAppender);
                }

                boolean b = parseUnescapedQuotesUntilDelimiter || ch != quote && ch != quoteEscape;
                if (b) {
                    output.appender.append(ch);
                }

                prev = ch;
                parseQuotedValue();
            } else if (keepQuotes) {
                output.appender.append(quote);
            }
        } else if (b1) {
            output.appender.append(quote);
        }

    }

    private void parseValueProcessingEscapeMultiDelimiter() {
        while (ch != newLine && !matchDelimiter()) {
            if (ch != quote && ch != quoteEscape) {
                if (prev == quote) {
                    handleUnescapedQuoteInValue();
                    return;
                }
                output.appender.append(ch);
            } else {
                processQuoteEscape();
            }
            prev = ch;
            ch = input.nextChar();
        }
    }
}
