package com.chua.common.support.jsoup.nodes;

import com.chua.common.support.jsoup.SerializationException;
import com.chua.common.support.jsoup.helper.Validate;
import com.chua.common.support.jsoup.nodes.Document.OutputSettings;
import com.chua.common.support.jsoup.parser.CharacterReader;
import com.chua.common.support.jsoup.parser.Parser;
import com.chua.common.support.utils.StringUtils;

import java.io.IOException;
import java.nio.charset.CharsetEncoder;
import java.util.Arrays;
import java.util.HashMap;

import static com.chua.common.support.jsoup.nodes.Document.OutputSettings.Syntax;
import static com.chua.common.support.utils.StringUtils.borrowBuilder;

/**
 * HTML entities, and escape routines. Source: <a href="http://www.w3.org/TR/html5/named-character-references.html#named-character-references">W3C
 * HTML named character references</a>.
 *
 * @author Administrator
 */
public class Entities {
    private static final int EMPTY = -1;
    private static final String EMPTY_NAME = "";
    static final int CODEPOINT_RADIX = 36;
    private static final char[] CODE_DELIMS = {',', ';'};
    /**
     * name -> multiple character references
     */
    private static final HashMap<String, String> MULTI_POINTS = new HashMap<>();
    private static final OutputSettings DEFAULT_OUTPUT = new OutputSettings();

    /**
     * HTML escape an input string. That is, {@code <} is returned as {@code &lt;}
     *
     * @param string the un-escaped string to escape
     * @param out the output settings to use
     * @return the escaped string
     */
    public static String escape(String string, OutputSettings out) {
        if (string == null) {
            return "";
        }
        StringBuilder accum = borrowBuilder();
        try {
            escape(accum, string, out, false, false, false, false);
        } catch (IOException e) {
            throw new SerializationException(e);
        }
        return accum.toString();
    }

    private Entities() {
    }

    /**
     * Check if the input is a known named entity
     *
     * @param name the possible entity name (e.g. "lt" or "amp")
     * @return true if a known named entity
     */
    public static boolean isNamedEntity(final String name) {
        return EscapeMode.extended.codepointForName(name) != EMPTY;
    }

    /**
     * Check if the input is a known named entity in the base entity set.
     *
     * @param name the possible entity name (e.g. "lt" or "amp")
     * @return true if a known named entity in the base set
     * @see #isNamedEntity(String)
     */
    public static boolean isBaseNamedEntity(final String name) {
        return EscapeMode.base.codepointForName(name) != EMPTY;
    }

    /**
     * Get the character(s) represented by the named entity
     *
     * @param name entity (e.g. "lt" or "amp")
     * @return the string value of the character(s) represented by this entity, or "" if not defined
     */
    public static String getByName(String name) {
        String val = MULTI_POINTS.get(name);
        if (val != null) {
            return val;
        }
        int codepoint = EscapeMode.extended.codepointForName(name);
        if (codepoint != EMPTY) {
            return new String(new int[]{codepoint}, 0, 1);
        }
        return EMPTY_NAME;
    }

    public static int codepointsForName(final String name, final int[] codepoints) {
        String val = MULTI_POINTS.get(name);
        if (val != null) {
            codepoints[0] = val.codePointAt(0);
            codepoints[1] = val.codePointAt(1);
            return 2;
        }
        int codepoint = EscapeMode.extended.codepointForName(name);
        if (codepoint != EMPTY) {
            codepoints[0] = codepoint;
            return 1;
        }
        return 0;
    }

    static void escape(Appendable accum, String string, OutputSettings out,
                       boolean inAttribute, boolean normaliseWhite, boolean stripLeadingWhite, boolean trimTrailing) throws IOException {

        boolean lastWasWhite = false;
        boolean reachedNonWhite = false;
        final EscapeMode escapeMode = out.escapeMode();
        final CharsetEncoder encoder = out.encoder();
        final CoreCharset coreCharset = out.coreCharset;
        final int length = string.length();

        int codePoint;
        boolean skipped = false;
        for (int offset = 0; offset < length; offset += Character.charCount(codePoint)) {
            codePoint = string.codePointAt(offset);

            if (normaliseWhite) {
                if (StringUtils.isWhitespace(codePoint)) {
                    if (stripLeadingWhite && !reachedNonWhite) {
                        continue;
                    }
                    if (lastWasWhite) {
                        continue;
                    }
                    if (trimTrailing) {
                        skipped = true;
                        continue;
                    }
                    accum.append(' ');
                    lastWasWhite = true;
                    continue;
                } else {
                    lastWasWhite = false;
                    reachedNonWhite = true;
                    if (skipped) {
                        accum.append(' ');
                        skipped = false;
                    }
                }
            }
            if (codePoint < Character.MIN_SUPPLEMENTARY_CODE_POINT) {
                final char c = (char) codePoint;
                switch (c) {
                    case '&':
                        accum.append("&amp;");
                        break;
                    case 0xA0:
                        if (escapeMode != EscapeMode.xhtml) {
                            accum.append("&nbsp;");
                        } else {
                            accum.append("&#xa0;");
                        }
                        break;
                    case '<':
                        if (!inAttribute || escapeMode == EscapeMode.xhtml || out.syntax() == Syntax.xml) {
                            accum.append("&lt;");
                        } else {
                            accum.append(c);
                        }
                        break;
                    case '>':
                        if (!inAttribute) {
                            accum.append("&gt;");
                        } else {
                            accum.append(c);
                        }
                        break;
                    case '"':
                        if (inAttribute) {
                            accum.append("&quot;");
                        } else {
                            accum.append(c);
                        }
                        break;
                    // we escape ascii control <x20 (other than tab, line-feed, carriage return)  for XML compliance (required) and HTML ease of reading (not required) - https://www.w3.org/TR/xml/#charsets
                    case 0x9:
                    case 0xA:
                    case 0xD:
                        accum.append(c);
                        break;
                    default:
                        if (c < 0x20 || !canEncode(coreCharset, c, encoder)) {
                            appendEncoded(accum, escapeMode, codePoint);
                        } else {
                            accum.append(c);
                        }
                }
            } else {
                final String c = new String(Character.toChars(codePoint));
                if (encoder.canEncode(c)) {
                    accum.append(c);
                } else {
                    appendEncoded(accum, escapeMode, codePoint);
                }
            }
        }
    }

    /**
     * HTML escape an input string, using the default settings (UTF-8, base entities). That is, {@code <} is returned as
     * {@code &lt;}
     *
     * @param string the un-escaped string to escape
     * @return the escaped string
     */
    public static String escape(String string) {
        return escape(string, DEFAULT_OUTPUT);
    }

    public enum EscapeMode {
        /**
         * Restricted entities suitable for XHTML output: lt, gt, amp, and quot only.
         */
        xhtml(EntitiesData.XML_POINTS, 4),
        /**
         * Default HTML output entities.
         */
        base(EntitiesData.BASE_POINTS, 106),
        /**
         * Complete HTML entities.
         */
        extended(EntitiesData.FULL_POINTS, 2125);

        private String[] nameKeys;
        private int[] codeVals;
        private int[] codeKeys;
        private String[] nameVals;

        EscapeMode(String file, int size) {
            load(this, file, size);
        }

        int codepointForName(final String name) {
            int index = Arrays.binarySearch(nameKeys, name);
            return index >= 0 ? codeVals[index] : EMPTY;
        }

        String nameForCodepoint(final int codepoint) {
            final int index = Arrays.binarySearch(codeKeys, codepoint);
            if (index >= 0) {
                return (index < nameVals.length - 1 && codeKeys[index + 1] == codepoint) ?
                        nameVals[index + 1] : nameVals[index];
            }
            return EMPTY_NAME;
        }

        private int size() {
            return nameKeys.length;
        }
    }

    private static void appendEncoded(Appendable accum, EscapeMode escapeMode, int codePoint) throws IOException {
        final String name = escapeMode.nameForCodepoint(codePoint);
        if (!EMPTY_NAME.equals(name)) {
            accum.append('&').append(name).append(';');
        } else {
            accum.append("&#x").append(Integer.toHexString(codePoint)).append(';');
        }
    }

    /**
     * Un-escape an HTML escaped string. That is, {@code &lt;} is returned as {@code <}.
     *
     * @param string the HTML string to un-escape
     * @return the unescaped string
     */
    public static String unescape(String string) {
        return unescape(string, false);
    }

    /**
     * Unescape the input string.
     *
     * @param string to un-HTML-escape
     * @param strict if "strict" (that is, requires trailing ';' char, otherwise that's optional)
     * @return unescaped string
     */
    static String unescape(String string, boolean strict) {
        return Parser.unescapeEntities(string, strict);
    }

    /**
     * Provides a fast-path for Encoder.canEncode, which drastically improves performance on Android post JellyBean.
     * After KitKat, the implementation of canEncode degrades to the point of being useless. For non ASCII or UTF,
     * performance may be bad. We can add more encoders for common character sets that are impacted by performance
     * issues on Android if required.
     *
     * Benchmarks:     *
     * OLD toHtml() impl v New (fastpath) in millis
     * Wiki: 1895, 16
     * CNN: 6378, 55
     * Alterslash: 3013, 28
     * Jsoup: 167, 2
     */
    private static boolean canEncode(final CoreCharset charset, final char c, final CharsetEncoder fallback) {
        switch (charset) {
            case ASCII:
                return c < 0x80;
            case UTF:
                return true;
            default:
                return fallback.canEncode(c);
        }
    }

    enum CoreCharset {
        /**
         * ascii
         */
        ASCII,
        /**
         * utf
         */
        UTF,
        /**
         * back
         */
        FALLBACK;

        static CoreCharset byName(final String name) {
            if ("US-ASCII".equals(name)) {
                return ASCII;
            }
            if (name.startsWith("UTF-")) {
                return UTF;
            }
            return FALLBACK;
        }
    }

    private static void load(EscapeMode e, String pointsData, int size) {
        e.nameKeys = new String[size];
        e.codeVals = new int[size];
        e.codeKeys = new int[size];
        e.nameVals = new String[size];

        int i = 0;
        CharacterReader reader = new CharacterReader(pointsData);
        try {
            while (!reader.isEmpty()) {

                final String name = reader.consumeTo('=');
                reader.advance();
                final int cp1 = Integer.parseInt(reader.consumeToAny(CODE_DELIMS), CODEPOINT_RADIX);
                final char codeDelim = reader.current();
                reader.advance();
                final int cp2;
                if (codeDelim == ',') {
                    cp2 = Integer.parseInt(reader.consumeTo(';'), CODEPOINT_RADIX);
                    reader.advance();
                } else {
                    cp2 = EMPTY;
                }
                final String indexS = reader.consumeTo('&');
                final int index = Integer.parseInt(indexS, CODEPOINT_RADIX);
                reader.advance();

                e.nameKeys[i] = name;
                e.codeVals[i] = cp1;
                e.codeKeys[index] = cp1;
                e.nameVals[index] = name;

                if (cp2 != EMPTY) {
                    MULTI_POINTS.put(name, new String(new int[]{cp1, cp2}, 0, 2));
                }
                i++;
            }

            Validate.isTrue(i == size, "Unexpected count of entities loaded");
        } finally {
            reader.close();
        }
    }
}
