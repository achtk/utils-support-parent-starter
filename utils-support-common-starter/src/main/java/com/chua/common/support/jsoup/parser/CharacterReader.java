package com.chua.common.support.jsoup.parser;

import com.chua.common.support.jsoup.UncheckedException;
import com.chua.common.support.jsoup.helper.Validate;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Locale;

/**
 * CharacterReader consumes tokens off a string. Used internally by jsoup. API subject to changes.
 *
 * @author Administrator
 */
public final class CharacterReader {
    static final char EOF = (char) -1;
    private static final int MAX_STRING_CACHE_LEN = 12;
    static final int MAX_BUFFER_LEN = 1024 * 32;
    static final int READ_AHEAD_LIMIT = (int) (MAX_BUFFER_LEN * 0.75);
    /**
     * the minimum mark length supported. No HTML entities can be larger than this.
     */
    private static final int MIN_READ_AHEAD_LEN = 1024;

    private char[] charBuf;
    private Reader reader;
    private int bufLength;
    private int bufSplitPoint;
    private int bufPos;
    private int readerPos;
    private int bufMark = -1;
    private static final int STRING_CACHE_SIZE = 512;
    /**
     * holds reused strings in this doc, to lessen garbage
     */
    private String[] stringCache = new String[STRING_CACHE_SIZE];
    /**
     * optionally track the pos() position of newlines - scans during bufferUp()
     */
    
    private ArrayList<Integer> newlinePositions = null;
    /**
     * line numbers start at 1; += newlinePosition[indexof(pos)]
     */
    private int lineNumberOffset = 1;

    public CharacterReader(Reader input, int sz) {
        Validate.notNull(input);
        Validate.isTrue(input.markSupported());
        reader = input;
        charBuf = new char[Math.min(sz, MAX_BUFFER_LEN)];
        bufferUp();
    }

    public CharacterReader(Reader input) {
        this(input, MAX_BUFFER_LEN);
    }

    public CharacterReader(String input) {
        this(new StringReader(input), input.length());
    }

    public void close() {
        if (reader == null) {
            return;
        }
        try {
            reader.close();
        } catch (IOException ignored) {
        } finally {
            reader = null;
            charBuf = null;
            stringCache = null;
        }
    }

    /**
     * if the underlying stream has been completely read, no value in further buffering
     */
    private boolean readFully;
        
    private String lastIcSeq;

    /**
     * Gets the position currently read to in the content. Starts at 0.
     *
     * @return current position
     */
    public int pos() {
        return readerPos + bufPos;
    }
    private int lastIcIndex;

    /**
     * Check if the tracking of newlines is enabled.
     *
     * @return the current newline tracking state
     * @since 1.14.3
     */
    public boolean isTrackNewlines() {
        return newlinePositions != null;
    }

    /**
     * Get the current line number (that the reader has consumed to). Starts at line #1.
     *
     * @return the current line number, or 1 if line tracking is not enabled.
     * @see #trackNewlines(boolean)
     * @since 1.14.3
     */
    public int lineNumber() {
        return lineNumber(pos());
    }

    /**
     * Caches short strings, as a flyweight pattern, to reduce GC load. Just for this doc, to prevent leaks.
     * <p/>
     * Simplistic, and on hash collisions just falls back to creating a new string, vs a full HashMap with Entry list.
     * That saves both having to create objects as hash keys, and running through the entry list, at the expense of
     * some more duplicates.
     */
    private static String cacheString(final char[] charBuf, final String[] stringCache, final int start, final int count) {
        if (count > MAX_STRING_CACHE_LEN) {
            return new String(charBuf, start, count);
        }
        if (count < 1) {
            return "";
        }

        int hash = 0;
        for (int i = 0; i < count; i++) {
            hash = 31 * hash + charBuf[start + i];
        }

        final int index = hash & STRING_CACHE_SIZE - 1;
        String cached = stringCache[index];

        if (cached != null && rangeEquals(charBuf, start, count, cached)) {
            return cached;
        } else {
            cached = new String(charBuf, start, count);
            stringCache[index] = cached;
        }

        return cached;
    }

    /**
     * Get the current column number (that the reader has consumed to). Starts at column #1.
     *
     * @return the current column number
     * @see #trackNewlines(boolean)
     * @since 1.14.3
     */
    public int columnNumber() {
        return columnNumber(pos());
    }

    int columnNumber(int pos) {
        if (!isTrackNewlines()) {
            return pos + 1;
        }

        int i = lineNumIndex(pos);
        if (i == -1) {
            return pos + 1;
        }
        return pos - newlinePositions.get(i) + 1;
    }

    /**
     * Get a formatted string representing the current line and cursor positions. E.g. <code>5:10</code> indicating line
     * number 5 and column number 10.
     *
     * @return line:col position
     * @see #trackNewlines(boolean)
     * @since 1.14.3
     */
    String cursorPos() {
        return lineNumber() + ":" + columnNumber();
    }

    private int lineNumIndex(int pos) {
        if (!isTrackNewlines()) {
            return 0;
        }
        int i = Collections.binarySearch(newlinePositions, pos);
        if (i < -1) {
            i = Math.abs(i) - 2;
        }
        return i;
    }

    private void bufferUp() {
        if (readFully || bufPos < bufSplitPoint) {
            return;
        }

        final int pos;
        final int offset;
        if (bufMark != -1) {
            pos = bufMark;
            offset = bufPos - bufMark;
        } else {
            pos = bufPos;
            offset = 0;
        }

        try {
            final long skipped = reader.skip(pos);
            reader.mark(MAX_BUFFER_LEN);
            int read = 0;
            while (read <= MIN_READ_AHEAD_LEN) {
                int thisRead = reader.read(charBuf, read, charBuf.length - read);
                if (thisRead == -1) {
                    readFully = true;
                }
                if (thisRead <= 0) {
                    break;
                }
                read += thisRead;
            }
            reader.reset();
            if (read > 0) {
                Validate.isTrue(skipped == pos);
                bufLength = read;
                readerPos += pos;
                bufPos = offset;
                if (bufMark != -1) {
                    bufMark = 0;
                }
                bufSplitPoint = Math.min(bufLength, READ_AHEAD_LIMIT);
            }
        } catch (IOException e) {
            throw new UncheckedException(e);
        }
        scanBufferForNewlines();
        lastIcSeq = null;
    }

    /**
     * Tests if all the content has been read.
     *
     * @return true if nothing left to read.
     */
    public boolean isEmpty() {
        bufferUp();
        return bufPos >= bufLength;
    }

    private boolean isEmptyNoBufferUp() {
        return bufPos >= bufLength;
    }

    /**
     * Get the char at the current position.
     *
     * @return char
     */
    public char current() {
        bufferUp();
        return isEmptyNoBufferUp() ? EOF : charBuf[bufPos];
    }

    char consume() {
        bufferUp();
        char val = isEmptyNoBufferUp() ? EOF : charBuf[bufPos];
        bufPos++;
        return val;
    }

    /**
     * Enables or disables line number tracking. By default, will be <b>off</b>.Tracking line numbers improves the
     * legibility of parser error messages, for example. Tracking should be enabled before any content is read to be of
     * use.
     *
     * @param track set tracking on|off
     * @since 1.14.3
     */
    public void trackNewlines(boolean track) {
        if (track && newlinePositions == null) {
            newlinePositions = new ArrayList<>(MAX_BUFFER_LEN / 80);
            scanBufferForNewlines();
        } else if (!track) {
            newlinePositions = null;
        }
    }

    /**
     * Moves the current position by one.
     */
    public void advance() {
        bufPos++;
    }

    void mark() {
        if (bufLength - bufPos < MIN_READ_AHEAD_LEN) {
            bufSplitPoint = 0;
        }

        bufferUp();
        bufMark = bufPos;
    }

    void unmark() {
        bufMark = -1;
    }

    void rewindToMark() {
        if (bufMark == -1) {
            throw new UncheckedException(new IOException("Mark invalid"));
        }

        bufPos = bufMark;
        unmark();
    }

    /**
     * Returns the number of characters between the current position and the next instance of the input char
     *
     * @param c scan target
     * @return offset between current position and next instance of target. -1 if not found.
     */
    int nextIndexOf(char c) {
        bufferUp();
        for (int i = bufPos; i < bufLength; i++) {
            if (c == charBuf[i]) {
                return i - bufPos;
            }
        }
        return -1;
    }

    int lineNumber(int pos) {
        if (!isTrackNewlines()) {
            return 1;
        }

        int i = lineNumIndex(pos);
        if (i == -1) {
            return lineNumberOffset;
        }
        return i + lineNumberOffset + 1;
    }

    /**
     * Reads characters up to the specific char.
     *
     * @param c the delimiter
     * @return the chars read
     */
    public String consumeTo(char c) {
        int offset = nextIndexOf(c);
        if (offset != -1) {
            String consumed = cacheString(charBuf, stringCache, bufPos, offset);
            bufPos += offset;
            return consumed;
        } else {
            return consumeToEnd();
        }
    }

    String consumeTo(String seq) {
        int offset = nextIndexOf(seq);
        if (offset != -1) {
            String consumed = cacheString(charBuf, stringCache, bufPos, offset);
            bufPos += offset;
            return consumed;
        } else if (bufLength - bufPos < seq.length()) {
            return consumeToEnd();
        } else {
            int endPos = bufLength - seq.length() + 1;
            String consumed = cacheString(charBuf, stringCache, bufPos, endPos - bufPos);
            bufPos = endPos;
            return consumed;
        }
    }

    /**
     * Read characters until the first of any delimiters is found.
     *
     * @param chars delimiters to scan for
     * @return characters read up to the matched delimiter.
     */
    public String consumeToAny(final char... chars) {
        bufferUp();
        int pos = bufPos;
        final int start = pos;
        final int remaining = bufLength;
        final char[] val = charBuf;
        final int charLen = chars.length;
        int i;

        OUTER:
        while (pos < remaining) {
            for (i = 0; i < charLen; i++) {
                if (val[pos] == chars[i]) {
                    break OUTER;
                }
            }
            pos++;
        }

        bufPos = pos;
        return pos > start ? cacheString(charBuf, stringCache, start, pos - start) : "";
    }

    String consumeToAnySorted(final char... chars) {
        bufferUp();
        int pos = bufPos;
        final int start = pos;
        final int remaining = bufLength;
        final char[] val = charBuf;

        while (pos < remaining) {
            if (Arrays.binarySearch(chars, val[pos]) >= 0) {
                break;
            }
            pos++;
        }
        bufPos = pos;
        return bufPos > start ? cacheString(charBuf, stringCache, start, pos - start) : "";
    }

    String consumeData() {
        int pos = bufPos;
        final int start = pos;
        final int remaining = bufLength;
        final char[] val = charBuf;

        OUTER:
        while (pos < remaining) {
            switch (val[pos]) {
                case '&':
                case '<':
                case TokeniserState.nullChar:
                    break OUTER;
                default:
                    pos++;
            }
        }
        bufPos = pos;
        return pos > start ? cacheString(charBuf, stringCache, start, pos - start) : "";
    }

    String consumeAttributeQuoted(final boolean single) {
        int pos = bufPos;
        final int start = pos;
        final int remaining = bufLength;
        final char[] val = charBuf;

        OUTER:
        while (pos < remaining) {
            switch (val[pos]) {
                case '&':
                case TokeniserState.nullChar:
                    break OUTER;
                case '\'':
                    if (single) {
                        break OUTER;
                    }
                case '"':
                    if (!single) {
                        break OUTER;
                    }
                default:
                    pos++;
            }
        }
        bufPos = pos;
        return pos > start ? cacheString(charBuf, stringCache, start, pos - start) : "";
    }


    String consumeRawData() {
        int pos = bufPos;
        final int start = pos;
        final int remaining = bufLength;
        final char[] val = charBuf;

        OUTER:
        while (pos < remaining) {
            switch (val[pos]) {
                case '<':
                case TokeniserState.nullChar:
                    break OUTER;
                default:
                    pos++;
            }
        }
        bufPos = pos;
        return pos > start ? cacheString(charBuf, stringCache, start, pos - start) : "";
    }

    String consumeTagName() {
        bufferUp();
        int pos = bufPos;
        final int start = pos;
        final int remaining = bufLength;
        final char[] val = charBuf;

        OUTER:
        while (pos < remaining) {
            switch (val[pos]) {
                case '\t':
                case '\n':
                case '\r':
                case '\f':
                case ' ':
                case '/':
                case '>':
                case '<':
                    break OUTER;
                default:
            }
            pos++;
        }

        bufPos = pos;
        return pos > start ? cacheString(charBuf, stringCache, start, pos - start) : "";
    }

    String consumeToEnd() {
        bufferUp();
        String data = cacheString(charBuf, stringCache, bufPos, bufLength - bufPos);
        bufPos = bufLength;
        return data;
    }

    String consumeLetterSequence() {
        bufferUp();
        int start = bufPos;
        while (bufPos < bufLength) {
            char c = charBuf[bufPos];
            boolean exp = (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || Character.isLetter(c);
            if (exp) {
                bufPos++;
            } else {
                break;
            }
        }

        return cacheString(charBuf, stringCache, start, bufPos - start);
    }

    String consumeLetterThenDigitSequence() {
        bufferUp();
        int start = bufPos;
        while (bufPos < bufLength) {
            char c = charBuf[bufPos];
            boolean exp = (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || Character.isLetter(c);
            if (exp) {
                bufPos++;
            } else {
                break;
            }
        }
        while (!isEmptyNoBufferUp()) {
            char c = charBuf[bufPos];
            if (c >= '0' && c <= '9') {
                bufPos++;
            } else {
                break;
            }
        }

        return cacheString(charBuf, stringCache, start, bufPos - start);
    }

    String consumeHexSequence() {
        bufferUp();
        int start = bufPos;
        while (bufPos < bufLength) {
            char c = charBuf[bufPos];
            boolean exp = (c >= '0' && c <= '9') || (c >= 'A' && c <= 'F') || (c >= 'a' && c <= 'f');
            if (exp) {
                bufPos++;
            } else {
                break;
            }
        }
        return cacheString(charBuf, stringCache, start, bufPos - start);
    }

    String consumeDigitSequence() {
        bufferUp();
        int start = bufPos;
        while (bufPos < bufLength) {
            char c = charBuf[bufPos];
            if (c >= '0' && c <= '9') {
                bufPos++;
            } else {
                break;
            }
        }
        return cacheString(charBuf, stringCache, start, bufPos - start);
    }

    boolean matches(char c) {
        return !isEmpty() && charBuf[bufPos] == c;

    }

    boolean matches(String seq) {
        bufferUp();
        int scanLength = seq.length();
        if (scanLength > bufLength - bufPos) {
            return false;
        }

        for (int offset = 0; offset < scanLength; offset++) {
            if (seq.charAt(offset) != charBuf[bufPos + offset]) {
                return false;
            }
        }
        return true;
    }

    boolean matchesIgnoreCase(String seq) {
        bufferUp();
        int scanLength = seq.length();
        if (scanLength > bufLength - bufPos) {
            return false;
        }

        for (int offset = 0; offset < scanLength; offset++) {
            char upScan = Character.toUpperCase(seq.charAt(offset));
            char upTarget = Character.toUpperCase(charBuf[bufPos + offset]);
            if (upScan != upTarget) {
                return false;
            }
        }
        return true;
    }

    boolean matchesAny(char... seq) {
        if (isEmpty()) {
            return false;
        }

        bufferUp();
        char c = charBuf[bufPos];
        for (char seek : seq) {
            if (seek == c) {
                return true;
            }
        }
        return false;
    }

    boolean matchesAnySorted(char[] seq) {
        bufferUp();
        return !isEmpty() && Arrays.binarySearch(seq, charBuf[bufPos]) >= 0;
    }

    boolean matchesLetter() {
        if (isEmpty()) {
            return false;
        }
        char c = charBuf[bufPos];
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || Character.isLetter(c);
    }

    /**
     * Checks if the current pos matches an ascii alpha (A-Z a-z) per https://infra.spec.whatwg.org/#ascii-alpha
     *
     * @return if it matches or not
     */
    boolean matchesAsciiAlpha() {
        if (isEmpty()) {
            return false;
        }
        char c = charBuf[bufPos];
        return (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z');
    }

    boolean matchesDigit() {
        if (isEmpty()) {
            return false;
        }
        char c = charBuf[bufPos];
        return (c >= '0' && c <= '9');
    }

    boolean matchConsume(String seq) {
        bufferUp();
        if (matches(seq)) {
            bufPos += seq.length();
            return true;
        } else {
            return false;
        }
    }

    boolean matchConsumeIgnoreCase(String seq) {
        if (matchesIgnoreCase(seq)) {
            bufPos += seq.length();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Scans the buffer for newline position, and tracks their location in newlinePositions.
     */
    private void scanBufferForNewlines() {
        if (!isTrackNewlines()) {
            return;
        }

        if (newlinePositions.size() > 0) {
            int index = lineNumIndex(readerPos);
            if (index == -1) {
                index = 0;
            }
            int linePos = newlinePositions.get(index);
            lineNumberOffset += index;
            newlinePositions.clear();
            newlinePositions.add(linePos);
        }

        for (int i = bufPos; i < bufLength; i++) {
            if (charBuf[i] == '\n') {
                newlinePositions.add(1 + readerPos + i);
            }
        }
    }

    /**
     * Unconsume one character (bufPos--). MUST only be called directly after a consume(), and no chance of a bufferUp.
     */
    void unconsume() {
        if (bufPos < 1) {
            throw new UncheckedException(new IOException("WTF: No buffer left to unconsume."));
        }

        bufPos--;
    }

    /**
     * Returns the number of characters between the current position and the next instance of the input sequence
     *
     * @param seq scan target
     * @return offset between current position and next instance of target. -1 if not found.
     */
    int nextIndexOf(CharSequence seq) {
        bufferUp();
        char startChar = seq.charAt(0);
        for (int offset = bufPos; offset < bufLength; offset++) {
            if (startChar != charBuf[offset]) {
                while (++offset < bufLength && startChar != charBuf[offset]) {
                }
            }
            int i = offset + 1;
            int last = i + seq.length() - 1;
            if (offset < bufLength && last <= bufLength) {
                for (int j = 1; i < last && seq.charAt(j) == charBuf[i]; i++, j++) {
                }
                if (i == last) {
                    return offset - bufPos;
                }
            }
        }
        return -1;
    }

    @Override
    public String toString() {
        if (bufLength - bufPos < 0) {
            return "";
        }
        return new String(charBuf, bufPos, bufLength - bufPos);
    }

    /**
     * Used to check presence of </title>, </style> when we're in RCData and see a <xxx. Only finds consistent case.
     */
    boolean containsIgnoreCase(String seq) {
        if (seq.equals(lastIcSeq)) {
            if (lastIcIndex == -1) {
                return false;
            }
            if (lastIcIndex >= bufPos) {
                return true;
            }
        }
        lastIcSeq = seq;

        String loScan = seq.toLowerCase(Locale.ENGLISH);
        int lo = nextIndexOf(loScan);
        if (lo > -1) {
            lastIcIndex = bufPos + lo;
            return true;
        }

        String hiScan = seq.toUpperCase(Locale.ENGLISH);
        int hi = nextIndexOf(hiScan);
        boolean found = hi > -1;
        lastIcIndex = found ? bufPos + hi : -1;
        return found;
    }

    /**
     * Check if the value of the provided range equals the string.
     */
    static boolean rangeEquals(final char[] charBuf, final int start, int count, final String cached) {
        if (count == cached.length()) {
            int i = start;
            int j = 0;
            while (count-- != 0) {
                if (charBuf[i++] != cached.charAt(j++)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    boolean rangeEquals(final int start, final int count, final String cached) {
        return rangeEquals(charBuf, start, count, cached);
    }
}
