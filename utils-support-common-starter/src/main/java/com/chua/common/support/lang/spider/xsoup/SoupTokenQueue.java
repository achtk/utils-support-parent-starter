package com.chua.common.support.lang.spider.xsoup;

import com.chua.common.support.jsoup.parser.TokenQueue;
import com.chua.common.support.utils.Preconditions;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import static com.chua.common.support.constant.CommonConstant.*;

/**
 * A character queue with parsing helpers.
 * <br>
 * Most code borrowed from {@link TokenQueue}
 *
 * @author Jonathan Hedley
 * @see TokenQueue
 */
public class SoupTokenQueue {
    private static final char ESC = '\\'; 
    private static final String[] QUOTES = {"\"", "'"};
    private static final char SINGLE_QUOTE = '\'';
    private static final char DOUBLE_QUOTE = '"';
    private String queue;
    private int pos = 0;

    /**
     * Create a new TokenQueue.
     *
     * @param data string of data to back queue.
     */
    public SoupTokenQueue(String data) {
        Preconditions.notNull(data);
        queue = data;
    }

    /**
     * Unescaped a \ escaped string.
     *
     * @param in backslash escaped string
     * @return unescaped string
     */
    public static String unescape(String in) {
        StringBuilder out = new StringBuilder();
        char last = 0;
        for (char c : in.toCharArray()) {
            if (c == ESC) {
                if (last != 0 && last == ESC) {
                    out.append(c);
                }
            } else {
                out.append(c);
            }
            last = c;
        }
        return out.toString();
    }

    public static String trimQuotes(String str) {
        Preconditions.isTrue(str != null && str.length() > 0);
        String quote = str.substring(0, 1);
        if (in(quote, SYMBOL_QUOTE, SYMBOL_SINGLE_QUOTATION_MARK)) {
            Preconditions.isTrue(str.endsWith(quote), "Quote" + " for " + str + " is incomplete!");
            str = str.substring(1, str.length() - 1);
        }
        return str;
    }

    public static List<String> trimQuotes(List<String> strs) {
        Preconditions.isTrue(strs != null);
        List<String> list = new ArrayList<String>();
        for (String str : strs) {
            list.add(trimQuotes(str));
        }
        return list;
    }

    public static List<String> parseFuncionParams(String paramStr) {
        SoupTokenQueue tq = new SoupTokenQueue(paramStr);
        return tq.parseFuncionParams();
    }

    /**
     * Is the queue empty?
     *
     * @return true if no data left in queue.
     */
    public boolean isEmpty() {
        return remainingLength() == 0;
    }

    private int remainingLength() {
        return queue.length() - pos;
    }

    /**
     * Retrieves but does not remove the first character from the queue.
     *
     * @return First character, or 0 if empty.
     */
    public char peek() {
        return isEmpty() ? 0 : queue.charAt(pos);
    }

    /**
     * Add a character to the start of the queue (will be the next character retrieved).
     *
     * @param c character to add
     */
    public void addFirst(Character c) {
        addFirst(c.toString());
    }

    /**
     * Add a string to the start of the queue.
     *
     * @param seq string to add.
     */
    public void addFirst(String seq) {
        
        queue = seq + queue.substring(pos);
        pos = 0;
    }

    /**
     * Tests if the next characters on the queue match the sequence. Case insensitive.
     *
     * @param seq String to check queue for.
     * @return true if the next characters match.
     */
    public boolean matches(String seq) {
        return queue.regionMatches(true, pos, seq, 0, seq.length());
    }

    public boolean matchesRegex(String seq) {
        return Pattern.matches(seq, queue.substring(pos));
    }

    /**
     * Case sensitive match test.
     *
     * @param seq string to case sensitively check for
     * @return true if matched, false if not
     */
    public boolean matchesCs(String seq) {
        return queue.startsWith(seq, pos);
    }

    /**
     * Tests if the next characters match any of the sequences. Case insensitive.
     *
     * @param seq list of strings to case insensitively check for
     * @return true of any matched, false if none did
     */
    public boolean matchesAny(String... seq) {
        for (String s : seq) {
            if (matches(s)) {
                return true;
            }
        }
        return false;
    }

    public boolean matchesAny(char... seq) {
        if (isEmpty()) {
            return false;
        }

        for (char c : seq) {
            if (queue.charAt(pos) == c) {
                return true;
            }
        }
        return false;
    }

    public boolean matchesStartTag() {
        
        return (remainingLength() >= 2 && queue.charAt(pos) == '<' && Character.isLetter(queue.charAt(pos + 1)));
    }

    /**
     * Tests if the queue matches the sequence (as with match), and if they do, removes the matched string from the
     * queue.
     *
     * @param seq String to search for, and if found, remove from queue.
     * @return true if found and removed, false if not found.
     */
    public boolean matchChomp(String seq) {
        if (matches(seq)) {
            pos += seq.length();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Tests if queue starts with a whitespace character.
     *
     * @return if starts with whitespace
     */
    public boolean matchesWhitespace() {
        return !isEmpty() && isWhitespace(queue.charAt(pos));
    }

    /**
     * Test if the queue matches a word character (letter or digit).
     *
     * @return if matches a word character
     */
    public boolean matchesWord() {
        return !isEmpty() && Character.isLetterOrDigit(queue.charAt(pos));
    }

    /**
     * Drops the next character off the queue.
     */
    public void advance() {
        if (!isEmpty()) {
            pos++;
        }
    }

    /**
     * Consume one character off queue.
     *
     * @return first character on queue.
     */
    public char consume() {
        return queue.charAt(pos++);
    }

    /**
     * Consumes the supplied sequence of the queue. If the queue does not start with the supplied sequence, will
     * throw an illegal state exception -- but you should be running match() against that condition.
     * <br>
     * Case insensitive.
     *
     * @param seq sequence to remove from head of queue.
     */
    public void consume(String seq) {
        if (!matches(seq)) {
            throw new IllegalStateException("Queue did not match expected sequence");
        }
        int len = seq.length();
        if (len > remainingLength()) {
            throw new IllegalStateException("Queue not long enough to consume sequence");
        }

        pos += len;
    }

    /**
     * Pulls a string off the queue, up to but exclusive of the match sequence, or to the queue running out.
     *
     * @param seq String to end on (and not include in return, but leave on queue). <b>Case sensitive.</b>
     * @return The matched data consumed from queue.
     */
    public String consumeTo(String seq) {
        int offset = queue.indexOf(seq, pos);
        if (offset != -1) {
            String consumed = queue.substring(pos, offset);
            pos += consumed.length();
            return consumed;
        } else {
            return remainder();
        }
    }

    public String consumeToIgnoreCase(String seq) {
        int start = pos;
        String first = seq.substring(0, 1);
        boolean canScan = first.toLowerCase().equals(first.toUpperCase()); 
        while (!isEmpty()) {
            if (matches(seq)) {
                break;
            }

            if (canScan) {
                int skip = queue.indexOf(first, pos) - pos;
                if (skip == 0) 
                {
                    pos++;
                } else if (skip < 0) 
                {
                    pos = queue.length();
                } else {
                    pos += skip;
                }
            } else {
                pos++;
            }
        }

        String data = queue.substring(start, pos);
        return data;
    }

    /**
     * Consumes to the first sequence provided, or to the end of the queue. Leaves the terminator on the queue.
     *
     * @param seq any number of terminators to consume to. <b>Case insensitive.</b>
     * @return consumed string
     */
    
    
    public String consumeToAny(String... seq) {
        int start = pos;
        while (!isEmpty() && !matchesAny(seq)) {
            pos++;
        }

        String data = queue.substring(start, pos);
        return data;
    }

    public String consumeAny(String... seq) {
        for (String s : seq) {
            if (matches(s)) {
                pos += s.length();
                return s;
            }
        }
        return "";
    }

    /**
     * Pulls a string off the queue (like consumeTo), and then pulls off the matched string (but does not return it).
     * <br>
     * If the queue runs out of characters before finding the seq, will return as much as it can (and queue will go
     * isEmpty() == true).
     *
     * @param seq String to match up to, and not include in return, and to pull off queue. <b>Case sensitive.</b>
     * @return Data matched from queue.
     */
    public String chompTo(String seq) {
        String data = consumeTo(seq);
        matchChomp(seq);
        return data;
    }

    public String chompToIgnoreCase(String seq) {
        String data = consumeToIgnoreCase(seq); 
        matchChomp(seq);
        return data;
    }

    public String chompBalancedQuotes() {
        String quote = consumeAny(QUOTES);
        if (quote.length() == 0) {
            return "";
        }
        StringBuilder accum = new StringBuilder(quote);
        accum.append(consumeToUnescaped(quote));
        accum.append(consume());
        return accum.toString();
    }

    public String chompBalancedNotInQuotes(char open, char close) {
        StringBuilder accum = new StringBuilder();
        int depth = 0;
        char last = 0;
        boolean inQuotes = false;
        Character quote = null;

        do {
            if (isEmpty()) {
                break;
            }
            Character c = consume();
            if (last == 0 || last != ESC) {
                if (!inQuotes) {
                    if (c.equals(SINGLE_QUOTE) || c.equals(DOUBLE_QUOTE)) {
                        inQuotes = true;
                        quote = c;
                    } else if (c.equals(open)) {
                        depth++;
                    } else if (c.equals(close)) {
                        depth--;
                    }
                } else {
                    if (c.equals(quote)) {
                        inQuotes = false;
                    }
                }
            }

            if (depth > 0 && last != 0) {
                accum.append(c); 
            }
            last = c;
        } while (depth > 0);
        return accum.toString();
    }

    /**
     * Pulls a balanced string off the queue. E.g. if queue is "(one (two) three) four", (,) will return "one (two) three",
     * and leave " four" on the queue. Unbalanced openers and closers can be escaped (with \). Those escapes will be left
     * in the returned string, which is suitable for regexes (where we need to preserve the escape), but unsuitable for
     * contains text strings; use unescape for that.
     *
     * @param open  opener
     * @param close closer
     * @return data matched from the queue
     */
    public String chompBalanced(char open, char close) {
        StringBuilder accum = new StringBuilder();
        int depth = 0;
        char last = 0;

        do {
            if (isEmpty()) {
                break;
            }
            Character c = consume();
            if (last == 0 || last != ESC) {
                if (c.equals(open)) {
                    depth++;
                } else if (c.equals(close)) {
                    depth--;
                }
            }

            if (depth > 0 && last != 0) {
                accum.append(c); 
            }
            last = c;
        } while (depth > 0);
        return accum.toString();
    }

    /**
     * Pulls the next run of whitespace characters of the queue.
     *
     * @return seen
     */
    public boolean consumeWhitespace() {
        boolean seen = false;
        while (matchesWhitespace()) {
            pos++;
            seen = true;
        }
        return seen;
    }

    /**
     * Retrieves the next run of word type (letter or digit) off the queue.
     *
     * @return String of word characters from queue, or empty string if none.
     */
    public String consumeWord() {
        int start = pos;
        while (matchesWord()) {
            pos++;
        }
        return queue.substring(start, pos);
    }

    /**
     * Consume an tag name off the queue (word or :, _, -)
     *
     * @return tag name
     */
    public String consumeTagName() {
        int start = pos;
        while (!isEmpty() && (matchesWord() || matchesAny(SYMBOL_COLON_CHAR, SYMBOL_DASH_CHAR, SYMBOL_MINUS_CHAR))) {
            pos++;
        }

        return queue.substring(start, pos);
    }

    /**
     * Consume a CSS element selector (tag name, but | instead of : for namespaces, to not conflict with :pseudo selects).
     *
     * @return tag name
     */
    public String consumeElementSelector() {
        int start = pos;
        while (!isEmpty() && (matchesWord() || matchesAny(SYMBOL_PIPE_CHAR, SYMBOL_DASH_CHAR, SYMBOL_MINUS_CHAR))) {
            pos++;
        }

        return queue.substring(start, pos);
    }

    public void unConsume(int length) {
        Preconditions.isTrue(length <= pos, "length " + length + " is larger than consumed chars " + pos);
        pos -= length;
    }

    public void unConsume(String word) {
        unConsume(word.length());
    }

    /**
     * Consume a CSS identifier (ID or class) off the queue (letter, digit, -, _)
     * http:
     *
     * @return identifier
     */
    public String consumeCssIdentifier() {
        int start = pos;
        while (!isEmpty() && (matchesWord() || matchesAny(SYMBOL_MINUS_CHAR, SYMBOL_DASH_CHAR))) {
            pos++;
        }

        return queue.substring(start, pos);
    }

    /**
     * Consume an attribute key off the queue (letter, digit, -, _, :")
     *
     * @return attribute key
     */
    public String consumeAttributeKey() {
        int start = pos;
        while (!isEmpty() && (matchesWord() || matchesAny(SYMBOL_MINUS_CHAR, SYMBOL_DASH_CHAR, SYMBOL_COLON_CHAR))) {
            pos++;
        }

        return queue.substring(start, pos);
    }

    /**
     * Consume and return whatever is left on the queue.
     *
     * @return remained of queue.
     */
    public String remainder() {
        StringBuilder accum = new StringBuilder();
        while (!isEmpty()) {
            accum.append(consume());
        }
        return accum.toString();
    }

    @Override
    public String toString() {
        return queue.substring(pos);
    }

    public boolean containsAny(String... seq) {
        for (String s : seq) {
            if (queue.contains(s)) {
                return true;
            }
        }
        return false;
    }

    public String consumeToUnescaped(String str) {
        String s = consumeToAny(str);
        if (s.length() > 0 && s.charAt(s.length() - 1) == SYMBOL_RIGHT_SLASH_CHAR) {
            s += consume();
            s += consumeToUnescaped(str);
        }
        Preconditions.isTrue(pos < queue.length(), "Unclosed quotes! " + queue);
        return s;
    }

    public List<String> parseFuncionParams() {
        List<String> params = new ArrayList<String>();
        StringBuilder accum = new StringBuilder();
        while (!isEmpty()) {
            consumeWhitespace();
            if (matchChomp(",")) {
                params.add(accum.toString());
                accum = new StringBuilder();
            } else if (matchesAny(QUOTES)) {
                String quoteUsed = consumeAny(QUOTES);
                accum.append(quoteUsed);
                accum.append(consumeToUnescaped(quoteUsed));
                accum.append(consume());
            } else {
                accum.append(consumeToAny("\"", "'", ","));
            }
        }
        if (accum.length() > 0) {
            params.add(accum.toString());
        }
        return params;
    }

    /**
     * Tests if a code point is "whitespace" as defined in the HTML spec. Used for output HTML.
     * Copied from jsoup's org.jsoup.internal.StringUtil.
     *
     * @param c code point to test
     * @return true if code point is whitespace, false otherwise
     */
    private static boolean isWhitespace(int c) {
        return c == ' ' || c == '\t' || c == '\n' || c == '\f' || c == '\r';
    }

    
    private static boolean in(final String needle, final String... haystack) {
        final int len = haystack.length;
        for (int i = 0; i < len; i++) {
            if (haystack[i].equals(needle)) {
                return true;
            }
        }
        return false;
    }
}
