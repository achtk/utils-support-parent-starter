/*******************************************************************************
 * Copyright 2015 Univocity Software Pty Ltd
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

import com.chua.common.support.file.univocity.parsers.common.ArgumentUtils;
import com.chua.common.support.file.univocity.parsers.common.input.InputAnalysisProcess;

import java.util.*;
import java.util.Map.Entry;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_BLANK_CHAR;

/**
 * An {@link InputAnalysisProcess} to detect column delimiters, quotes and quote escapes in a CSV input.
 *
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 */
public abstract class AbstractCsvFormatDetector implements InputAnalysisProcess {

    private final int MAX_ROW_SAMPLES;
    private final char comment;
    private final char suggestedDelimiter;
    private final char normalizedNewLine;
    private final int whitespaceRangeStart;
    private final char suggestedQuote;
    private final char suggestedQuoteEscape;
    private char[] allowedDelimiters;
    private char[] delimiterPreference;

    /**
     * Builds a new {@code CsvFormatDetector}
     *
     * @param maxRowSamples        the number of row samples to collect before analyzing the statistics
     * @param settings             the configuration provided by the user with potential defaults in case the detection is unable to discover the proper column
     *                             delimiter or quote character.
     * @param whitespaceRangeStart starting range of characters considered to be whitespace.
     */
    public AbstractCsvFormatDetector(int maxRowSamples, CsvParserSettings settings, int whitespaceRangeStart) {
        this.MAX_ROW_SAMPLES = maxRowSamples;
        this.whitespaceRangeStart = whitespaceRangeStart;
        allowedDelimiters = settings.getDelimitersForDetection();

        if (allowedDelimiters != null && allowedDelimiters.length > 0) {
            suggestedDelimiter = allowedDelimiters[0];
            delimiterPreference = allowedDelimiters.clone();
            Arrays.sort(allowedDelimiters);
        } else {
            String delimiter = settings.getFormat().getDelimiterString();
            suggestedDelimiter = delimiter.length() > 1 ? ',' : settings.getFormat().getDelimiter();
            allowedDelimiters = new char[0];
            delimiterPreference = allowedDelimiters;
        }

        normalizedNewLine = settings.getFormat().getNormalizedNewline();
        comment = settings.getFormat().getComment();
        suggestedQuote = settings.getFormat().getQuote();
        suggestedQuoteEscape = settings.getFormat().getQuoteEscape();

    }

    protected Map<Character, Integer> calculateTotals(List<Map<Character, Integer>> symbolsPerRow) {
        Map<Character, Integer> out = new LinkedHashMap<Character, Integer>();

        for (Map<Character, Integer> rowStats : symbolsPerRow) {
            for (Entry<Character, Integer> symbolStats : rowStats.entrySet()) {
                Character symbol = symbolStats.getKey();
                Integer count = symbolStats.getValue();

                Integer total = out.get(symbol);
                if (total == null) {
                    total = 0;
                }
                out.put(symbol, total + count);
            }
        }

        return out;
    }

    @Override
    public void execute(char[] characters, int length) {
        Set<Character> allSymbols = new HashSet<>();
        Map<Character, Integer> symbols = new LinkedHashMap<>();
        Map<Character, Integer> escape = new LinkedHashMap<>();
        List<Map<Character, Integer>> symbolsPerRow = new ArrayList<>();
        int doubleQuoteCount = 0, singleQuoteCount = 0;
        int i;
        char inQuote = '\0';
        boolean afterNewLine = true;
        for (i = 0; i < length; i++) {
            char ch = characters[i];
            if (afterNewLine && ch == comment) {
                while (++i < length) {
                    ch = characters[i];
                    if (ch == '\r' || ch == '\n' || ch == normalizedNewLine) {
                        if (ch == '\r' && i + 1 < characters.length && characters[i + 1] == '\n') {
                            i++;
                        }
                        break;
                    }
                }
                continue;
            }
            if (ch == '"' || ch == '\'') {
                if (inQuote == ch) {
                    if (ch == '"') {
                        doubleQuoteCount++;
                    } else {
                        singleQuoteCount++;
                    }

                    if (i + 1 < length) {
                        char next = characters[i + 1];
                        boolean exp = Character.isLetterOrDigit(next) || (next <= ' ' && whitespaceRangeStart < next && next != '\n' && next != '\r');
                        if (exp) {
                            char prev = characters[i - 1];
                            if (!Character.isLetterOrDigit(prev) && prev != '\n' && prev != '\r') {
                                increment(escape, prev);
                            }
                        }
                    }
                    inQuote = '\0';
                } else if (inQuote == '\0') {
                    char prev = '\0';
                    int j = i;
                    while (prev <= SYMBOL_BLANK_CHAR && --j >= 0) {
                        prev = characters[j];
                    }
                    if (j < 0 || !Character.isLetterOrDigit(prev)) {
                        inQuote = ch;
                    }
                }
                continue;
            }

            if (inQuote != '\0') {
                continue;
            }

            afterNewLine = false;

            boolean exp = (ch == '\r' || ch == '\n' || ch == normalizedNewLine) && symbols.size() > 0;
            if (isSymbol(ch)) {
                allSymbols.add(ch);
                increment(symbols, ch);
            } else if (exp) {
                afterNewLine = true;
                symbolsPerRow.add(symbols);
                if (symbolsPerRow.size() == MAX_ROW_SAMPLES) {
                    break;
                }
                symbols = new LinkedHashMap<Character, Integer>();
            }
        }

        if (symbols.size() > 0 && length < characters.length) {
            symbolsPerRow.add(symbols);
        }

        if (length >= characters.length && i >= length && symbolsPerRow.size() > 1) {
            symbolsPerRow.remove(symbolsPerRow.size() - 1);
        }

        Map<Character, Integer> totals = calculateTotals(symbolsPerRow);

        Map<Character, Integer> sums = new LinkedHashMap<Character, Integer>();
        Set<Character> toRemove = new HashSet<Character>();

        for (Map<Character, Integer> prev : symbolsPerRow) {
            for (Map<Character, Integer> current : symbolsPerRow) {
                for (Character symbol : allSymbols) {
                    Integer previousCount = prev.get(symbol);
                    Integer currentCount = current.get(symbol);
                    if (previousCount == null && currentCount == null) {
                        toRemove.add(symbol);
                    }

                    if (previousCount == null || currentCount == null) {
                        continue;
                    }
                    increment(sums, symbol, Math.abs(previousCount - currentCount));
                }
            }
        }

        if (toRemove.size() == sums.size()) {
            Map<Character, Integer> lineCount = new LinkedHashMap<Character, Integer>();
            for (i = 0; i < symbolsPerRow.size(); i++) {
                for (Character symbolInRow : symbolsPerRow.get(i).keySet()) {
                    Integer count = lineCount.get(symbolInRow);
                    if (count == null) {
                        count = 0;
                    }
                    lineCount.put(symbolInRow, count + 1);
                }
            }

            Integer highestLineCount = null;
            for (Entry<Character, Integer> e : lineCount.entrySet()) {
                if (highestLineCount == null || highestLineCount < e.getValue()) {
                    highestLineCount = e.getValue();
                }
            }

            Character bestCandidate = null;
            for (Entry<Character, Integer> e : lineCount.entrySet()) {
                if (e.getValue().equals(highestLineCount)) {
                    if (bestCandidate == null) {
                        bestCandidate = e.getKey();
                    } else {
                        bestCandidate = null;
                        break;
                    }
                }
            }

            if (bestCandidate != null) {
                toRemove.remove(bestCandidate);
            }
        }

        sums.keySet().removeAll(toRemove);

        if (allowedDelimiters.length > 0) {
            Set<Character> toRetain = new HashSet<Character>();
            for (char c : allowedDelimiters) {
                toRetain.add(c);
            }
            sums.keySet().retainAll(toRetain);
        }

        char delimiter = pickDelimiter(sums, totals);

        char quote;
        if (doubleQuoteCount == 0 && singleQuoteCount == 0) {
            quote = suggestedQuote;
        } else {
            quote = doubleQuoteCount >= singleQuoteCount ? '"' : '\'';
        }

        escape.remove(delimiter);
        char quoteEscape = doubleQuoteCount == 0 && singleQuoteCount == 0 ? suggestedQuoteEscape : max(escape, totals, quote);
        apply(delimiter, quote, quoteEscape);
    }

    protected char pickDelimiter(Map<Character, Integer> sums, Map<Character, Integer> totals) {
        char delimiterMax = max(sums, totals, suggestedDelimiter);
        char delimiterMin = min(sums, totals, suggestedDelimiter);

        if (delimiterMin == SYMBOL_BLANK_CHAR || delimiterMax == SYMBOL_BLANK_CHAR) {
            boolean hasOtherDelimiters = false;
            for (Entry<Character, Integer> e : sums.entrySet()) {
                if (e.getValue() == 0 && e.getKey() != SYMBOL_BLANK_CHAR) {
                    hasOtherDelimiters = true;
                    break;
                }
            }

            if (hasOtherDelimiters) {
                totals.remove(' ');
                delimiterMax = max(sums, totals, suggestedDelimiter);
                delimiterMin = min(sums, totals, suggestedDelimiter);
            }
        }

        char delimiter;
        out:
        if (delimiterMax != delimiterMin) {
            if (sums.get(delimiterMin) == 0 && sums.get(delimiterMax) != 0) {
                delimiter = delimiterMin;
                break out;
            }

            for (char c : delimiterPreference) {
                if (c == delimiterMin) {
                    delimiter = delimiterMin;
                    break out;
                } else if (c == delimiterMax) {
                    delimiter = delimiterMax;
                    break out;
                }
            }

            if (totals.get(delimiterMin) > totals.get(delimiterMax)) {
                delimiter = delimiterMin;
                break out;
            }
            delimiter = delimiterMax;
        } else {
            delimiter = delimiterMax;
        }
        return delimiter;
    }

    /**
     * Increments the number associated with a character in a map by 1
     *
     * @param map    the map of characters and their numbers
     * @param symbol the character whose number should be increment
     */
    protected void increment(Map<Character, Integer> map, char symbol) {
        increment(map, symbol, 1);
    }

    /**
     * Increments the number associated with a character in a map
     *
     * @param map           the map of characters and their numbers
     * @param symbol        the character whose number should be increment
     * @param incrementSize the size of the increment
     */
    protected void increment(Map<Character, Integer> map, char symbol, int incrementSize) {
        Integer count = map.get(symbol);
        if (count == null) {
            count = 0;
        }
        map.put(symbol, count + incrementSize);
    }

    /**
     * Returns the character with the lowest associated number.
     *
     * @param map         the map of characters and their numbers
     * @param defaultChar the default character to return in case the map is empty
     * @return the character with the lowest number associated.
     */
    protected char min(Map<Character, Integer> map, Map<Character, Integer> totals, char defaultChar) {
        return getChar(map, totals, defaultChar, true);
    }

    /**
     * Returns the character with the highest associated number.
     *
     * @param map         the map of characters and their numbers
     * @param defaultChar the default character to return in case the map is empty
     * @return the character with the highest number associated.
     */
    protected char max(Map<Character, Integer> map, Map<Character, Integer> totals, char defaultChar) {
        return getChar(map, totals, defaultChar, false);
    }

    /**
     * Returns the character with the highest or lowest associated number.
     *
     * @param map         the map of characters and their numbers
     * @param defaultChar the default character to return in case the map is empty
     * @param min         a flag indicating whether to return the character associated with the lowest number in the map.
     *                    If {@code false} then the character associated with the highest number found will be returned.
     * @return the character with the highest/lowest number associated.
     */
    protected char getChar(Map<Character, Integer> map, Map<Character, Integer> totals, char defaultChar, boolean min) {
        int val = min ? Integer.MAX_VALUE : Integer.MIN_VALUE;
        for (Entry<Character, Integer> e : map.entrySet()) {
            int sum = e.getValue();
            boolean exp = (min && sum <= val) || (!min && sum >= val);
            if (exp) {
                char newChar = e.getKey();


                if (val == sum) {
                    Integer currentTotal = totals.get(defaultChar);
                    Integer newTotal = totals.get(newChar);

                    if (currentTotal != null && newTotal != null) {
                        boolean b = (min && newTotal > currentTotal) || (!min && newTotal > currentTotal);
                        if (currentTotal.equals(newTotal)) {
                            int defIndex = ArgumentUtils.indexOf(delimiterPreference, defaultChar, 0);
                            int newIndex = ArgumentUtils.indexOf(delimiterPreference, newChar, 0);
                            if (defIndex != -1 && newIndex != -1) {
                                defaultChar = defIndex < newIndex ? defaultChar : newChar;
                            }
                        } else if (b) {
                            defaultChar = newChar;
                        }
                    } else if (isSymbol(newChar)) {
                        defaultChar = newChar;
                    }
                } else {
                    val = sum;
                    defaultChar = newChar;
                }
            }
        }
        return defaultChar;
    }

    protected boolean isSymbol(char ch) {
        return isAllowedDelimiter(ch) || ch != comment && !Character.isLetterOrDigit(ch) && (ch == '\t' || ch >= ' ');
    }

    protected boolean isAllowedDelimiter(char ch) {
        return Arrays.binarySearch(allowedDelimiters, ch) >= 0;
    }

    /**
     * Applies the discovered CSV format elements to the {@link CsvParser}
     *
     * @param delimiter   the discovered delimiter character
     * @param quote       the discovered quote character
     * @param quoteEscape the discovered quote escape character.
     */
    protected abstract void apply(char delimiter, char quote, char quoteEscape);
}
