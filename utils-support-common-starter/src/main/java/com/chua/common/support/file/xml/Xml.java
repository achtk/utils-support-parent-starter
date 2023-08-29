package com.chua.common.support.file.xml;

/*
Public Domain.
*/

import com.chua.common.support.json.JsonObject;

import java.io.Reader;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Iterator;

import static com.chua.common.support.constant.CommonConstant.*;


/**
 * This provides static methods to convert an XML text into a JSONObject, and to
 * covert a JSONObject into an XML text.
 *
 * @author JSON.org
 * @version 2016-08-10
 */
@SuppressWarnings("boxing")
public class Xml {

    /**
     * The Character '&amp;'.
     */
    public static final Character AMP = '&';

    /**
     * The Character '''.
     */
    public static final Character APOS = '\'';

    /**
     * The Character '!'.
     */
    public static final Character BANG = '!';

    /**
     * The Character '='.
     */
    public static final Character EQ = '=';

    /**
     * The Character <pre>{@code '>'. }</pre>
     */
    public static final Character GT = '>';

    /**
     * The Character '&lt;'.
     */
    public static final Character LT = '<';

    /**
     * The Character '?'.
     */
    public static final Character QUEST = '?';

    /**
     * The Character '"'.
     */
    public static final Character QUOT = '"';

    /**
     * The Character '/'.
     */
    public static final Character SLASH = '/';

    /**
     * Null attribute name
     */
    public static final String NULL_ATTR = "xsi:nil";

    public static final String TYPE_ATTR = "xsi:type";

    /**
     * Creates an iterator for navigating Code Points in a string instead of
     * characters. Once Java7 support is dropped, this can be replaced with
     * <code>
     * string.codePoints()
     * </code>
     * which is available in Java8 and above.
     *
     * @see <a href=
     * "http://stackoverflow.com/a/21791059/6030888">http:
     */
    private static Iterable<Integer> codePointIterator(final String string) {
        return new Iterable<Integer>() {
            @Override
            public Iterator<Integer> iterator() {
                return new Iterator<Integer>() {
                    private int nextIndex = 0;
                    private int length = string.length();

                    @Override
                    public boolean hasNext() {
                        return this.nextIndex < this.length;
                    }

                    @Override
                    public Integer next() {
                        int result = string.codePointAt(this.nextIndex);
                        this.nextIndex += Character.charCount(result);
                        return result;
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    /**
     * Replace special characters with XML escapes:
     *
     * <pre>{@code
     * &amp; (ampersand) is replaced by &amp;amp;
     * &lt; (less than) is replaced by &amp;lt;
     * &gt; (greater than) is replaced by &amp;gt;
     * &quot; (double quote) is replaced by &amp;quot;
     * &apos; (single quote / apostrophe) is replaced by &amp;apos;
     * }</pre>
     *
     * @param string The string to be escaped.
     * @return The escaped string.
     */
    public static String escape(String string) {
        StringBuilder sb = new StringBuilder(string.length());
        for (final int cp : codePointIterator(string)) {
            switch (cp) {
                case '&':
                    sb.append("&amp;");
                    break;
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case '"':
                    sb.append("&quot;");
                    break;
                case '\'':
                    sb.append("&apos;");
                    break;
                default:
                    if (mustEscape(cp)) {
                        sb.append("&#x");
                        sb.append(Integer.toHexString(cp));
                        sb.append(';');
                    } else {
                        sb.appendCodePoint(cp);
                    }
            }
        }
        return sb.toString();
    }

    /**
     * @param cp code point to test
     * @return true if the code point is not valid for an XML
     */
    private static boolean mustEscape(int cp) {
        /* Valid range from https://www.w3.org/TR/REC-xml/#charsets
         *
         * #x9 | #xA | #xD | [#x20-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]
         *
         * any Unicode character, excluding the surrogate blocks, FFFE, and FFFF.
         */
        return (Character.isISOControl(cp)
                && cp != 0x9
                && cp != 0xA
                && cp != 0xD
        ) || !(
                (cp >= 0x20 && cp <= 0xD7FF)
                        || (cp >= 0xE000 && cp <= 0xFFFD)
                        || (cp >= 0x10000 && cp <= 0x10FFFF)
        )
                ;
    }

    /**
     * Removes XML escapes from the string.
     *
     * @param string string to remove escapes from
     * @return string with converted entities
     */
    public static String unescape(String string) {
        StringBuilder sb = new StringBuilder(string.length());
        for (int i = 0, length = string.length(); i < length; i++) {
            char c = string.charAt(i);
            if (c == '&') {
                final int semic = string.indexOf(';', i);
                if (semic > i) {
                    final String entity = string.substring(i + 1, semic);
                    sb.append(XmlTokenizer.unescapeEntity(entity));
                    i += entity.length() + 1;
                } else {
                    sb.append(c);
                }
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * Throw an exception if the string contains whitespace. Whitespace is not
     * allowed in tagNames and attributes.
     *
     * @param string A string.
     * @throws XmlJsonException Thrown if the string contains whitespace or is empty.
     */
    public static void noSpace(String string) throws XmlJsonException {
        int i, length = string.length();
        if (length == 0) {
            throw new XmlJsonException("Empty string.");
        }
        for (i = 0; i < length; i += 1) {
            if (Character.isWhitespace(string.charAt(i))) {
                throw new XmlJsonException("'" + string
                        + "' contains a space character.");
            }
        }
    }

    /**
     * Scan the content following the named tag, attaching it to the context.
     *
     * @param x       The XMLTokener containing the source string.
     * @param context The JSONObject that will include the new material.
     * @param name    The tag name.
     * @return true if the close tag is processed.
     * @throws XmlJsonException
     */
    private static boolean parse(XmlTokenizer x, XmlToJsonObject context, String name, XmlParserConfiguration config)
            throws XmlJsonException {
        char c;
        int i;
        XmlToJsonObject jsonObject = null;
        String string;
        String tagName;
        Object token;
        XmlXsiTypeConverter<?> xmlXsiTypeConverter;

        // <!-- ... -->
        // <! ... >
        // <![ ... ]]>
        // <? ... ?>

        token = x.nextToken();


        if (token == BANG) {
            c = x.next();
            if (c == '-') {
                if (x.next() == '-') {
                    x.skipPast("-->");
                    return false;
                }
                x.back();
            } else if (c == '[') {
                token = x.nextToken();
                if ("CDATA".equals(token)) {
                    if (x.next() == '[') {
                        string = x.nextCDATA();
                        if (string.length() > 0) {
                            context.accumulate(config.getcDataTagName(), string);
                        }
                        return false;
                    }
                }
                throw x.syntaxError("Expected 'CDATA['");
            }
            i = 1;
            do {
                token = x.nextMeta();
                if (token == null) {
                    throw x.syntaxError("Missing '>' after '<!'.");
                } else if (token == LT) {
                    i += 1;
                } else if (token == GT) {
                    i -= 1;
                }
            } while (i > 0);
            return false;
        } else if (token == QUEST) {

            x.skipPast("?>");
            return false;
        } else if (token == SLASH) {


            token = x.nextToken();
            if (name == null) {
                throw x.syntaxError("Mismatched close tag " + token);
            }
            if (!token.equals(name)) {
                throw x.syntaxError("Mismatched " + name + " and " + token);
            }
            if (x.nextToken() != GT) {
                throw x.syntaxError("Misshaped close tag");
            }
            return true;

        } else if (token instanceof Character) {
            throw x.syntaxError("Misshaped tag");


        } else {
            tagName = (String) token;
            token = null;
            jsonObject = new XmlToJsonObject();
            boolean nilAttributeFound = false;
            xmlXsiTypeConverter = null;
            for (; ; ) {
                if (token == null) {
                    token = x.nextToken();
                }
                if (token instanceof String) {
                    string = (String) token;
                    token = x.nextToken();
                    if (token == EQ) {
                        token = x.nextToken();
                        if (!(token instanceof String)) {
                            throw x.syntaxError("Missing value");
                        }

                        if (config.isConvertNilAttributeToNull()
                                && NULL_ATTR.equals(string)
                                && Boolean.parseBoolean((String) token)) {
                            nilAttributeFound = true;
                        } else if (config.getXsiTypeMap() != null && !config.getXsiTypeMap().isEmpty()
                                && TYPE_ATTR.equals(string)) {
                            xmlXsiTypeConverter = config.getXsiTypeMap().get(token);
                        } else if (!nilAttributeFound) {
                            jsonObject.accumulate(string,
                                    config.isKeepStrings()
                                            ? ((String) token)
                                            : stringToValue((String) token));
                        }
                        token = null;
                    } else {
                        jsonObject.accumulate(string, "");
                    }


                } else if (token == SLASH) {
                    // Empty tag <.../>
                    if (x.nextToken() != GT) {
                        throw x.syntaxError("Misshaped tag");
                    }
                    if (config.getForceList().contains(tagName)) {
                        if (nilAttributeFound) {
                            context.append(tagName, XmlToJsonObject.NULL);
                        } else if (jsonObject.length() > 0) {
                            context.append(tagName, jsonObject);
                        } else {
                            context.put(tagName, new XmlJsonArray());
                        }
                    } else {
                        if (nilAttributeFound) {
                            context.accumulate(tagName, XmlToJsonObject.NULL);
                        } else if (jsonObject.length() > 0) {
                            context.accumulate(tagName, jsonObject);
                        } else {
                            context.accumulate(tagName, "");
                        }
                    }
                    return false;

                } else if (token == GT) {
                    // Content, between <...> and </...>
                    for (; ; ) {
                        token = x.nextContent();
                        if (token == null) {
                            if (tagName != null) {
                                throw x.syntaxError("Unclosed tag " + tagName);
                            }
                            return false;
                        } else if (token instanceof String) {
                            string = (String) token;
                            if (string.length() > 0) {
                                if (xmlXsiTypeConverter != null) {
                                    jsonObject.accumulate(config.getcDataTagName(),
                                            stringToValue(string, xmlXsiTypeConverter));
                                } else {
                                    jsonObject.accumulate(config.getcDataTagName(),
                                            config.isKeepStrings() ? string : stringToValue(string));
                                }
                            }

                        } else if (token == LT) {
                            if (parse(x, jsonObject, tagName, config)) {
                                if (config.getForceList().contains(tagName)) {
                                    if (jsonObject.length() == 0) {
                                        context.put(tagName, new XmlJsonArray());
                                    } else if (jsonObject.length() == 1
                                            && jsonObject.opt(config.getcDataTagName()) != null) {
                                        context.append(tagName, jsonObject.opt(config.getcDataTagName()));
                                    } else {
                                        context.append(tagName, jsonObject);
                                    }
                                } else {
                                    if (jsonObject.length() == 0) {
                                        context.accumulate(tagName, "");
                                    } else if (jsonObject.length() == 1
                                            && jsonObject.opt(config.getcDataTagName()) != null) {
                                        context.accumulate(tagName, jsonObject.opt(config.getcDataTagName()));
                                    } else {
                                        context.accumulate(tagName, jsonObject);
                                    }
                                }

                                return false;
                            }
                        }
                    }
                } else {
                    throw x.syntaxError("Misshaped tag");
                }
            }
        }
    }

    /**
     * This method tries to convert the given string value to the target object
     *
     * @param string        String to convert
     * @param typeConverter value converter to convert string to integer, boolean e.t.c
     * @return JSON value of this string or the string
     */
    public static Object stringToValue(String string, XmlXsiTypeConverter<?> typeConverter) {
        if (typeConverter != null) {
            return typeConverter.convert(string);
        }
        return stringToValue(string);
    }

    /**
     * This method is the same as {@link XmlToJsonObject#stringToValue(String)}.
     *
     * @param string String to convert
     * @return JSON value of this string or the string
     */
    public static Object stringToValue(String string) {
        if ("".equals(string)) {
            return string;
        }

        if ("true".equalsIgnoreCase(string)) {
            return Boolean.TRUE;
        }
        if ("false".equalsIgnoreCase(string)) {
            return Boolean.FALSE;
        }
        if ("null".equalsIgnoreCase(string)) {
            return XmlToJsonObject.NULL;
        }

        /*
         * If it might be a number, try converting it. If a number cannot be
         * produced, then the value will just be a string.
         */

        char initial = string.charAt(0);
        boolean b = (initial >= '0' && initial <= '9') || initial == '-';
        if (b) {
            try {
                return stringToNumber(string);
            } catch (Exception ignore) {
            }
        }
        return string;
    }

    /**
     * direct copy of {@link XmlToJsonObject#stringToNumber(String)} to maintain Android support.
     */
    private static Number stringToNumber(final String val) throws NumberFormatException {
        char initial = val.charAt(0);
        boolean b = (initial >= '0' && initial <= '9') || initial == '-';
        if (b) {
            if (isDecimalNotation(val)) {
                // representation. BigDecimal doesn't support -0.0, ensure we
                try {
                    BigDecimal bd = new BigDecimal(val);
                    if (initial == '-' && BigDecimal.ZERO.compareTo(bd) == 0) {
                        return Double.valueOf(-0.0);
                    }
                    return bd;
                } catch (NumberFormatException retryAsDouble) {
                    try {
                        Double d = Double.valueOf(val);
                        if (d.isNaN() || d.isInfinite()) {
                            throw new NumberFormatException("val [" + val + "] is not a valid number.");
                        }
                        return d;
                    } catch (NumberFormatException ignore) {
                        throw new NumberFormatException("val [" + val + "] is not a valid number.");
                    }
                }
            }
            // block items like 00 01 etc. Java number parsers treat these as Octal.
            if (initial == LETTER_ZERO && val.length() > 1) {
                char at1 = val.charAt(1);
                if (at1 >= LETTER_ZERO && at1 <= LETTER_NIGHT) {
                    throw new NumberFormatException("val [" + val + "] is not a valid number.");
                }
            } else if (initial == SYMBOL_MINUS_CHAR && val.length() > 2) {
                char at1 = val.charAt(1);
                char at2 = val.charAt(2);
                if (at1 == LETTER_ZERO && at2 >= LETTER_ZERO && at2 <= LETTER_NIGHT) {
                    throw new NumberFormatException("val [" + val + "] is not a valid number.");
                }
            }

            // only what they need. i.e. Less runtime overhead if the value is
            BigInteger bi = new BigInteger(val);
            if (bi.bitLength() <= 31) {
                return bi.intValue();
            }
            if (bi.bitLength() <= 63) {
                return bi.longValue();
            }
            return bi;
        }
        throw new NumberFormatException("val [" + val + "] is not a valid number.");
    }

    /**
     * direct copy of {@link XmlToJsonObject#isDecimalNotation(String)} to maintain Android support.
     */
    private static boolean isDecimalNotation(final String val) {
        return val.indexOf('.') > -1 || val.indexOf('e') > -1
                || val.indexOf('E') > -1 || "-0".equals(val);
    }


    /**
     * Convert a well-formed (but not necessarily valid) XML string into a
     * JSONObject. Some information may be lost in this transformation because
     * JSON is a data format and XML is a document format. XML uses elements,
     * attributes, and content text, while JSON uses unordered collections of
     * name/value pairs and arrays of values. JSON does not does not like to
     * distinguish between elements and attributes. Sequences of similar
     * elements are represented as JSONArrays. Content text may be placed in a
     * "content" member. Comments, prologs, DTDs, and <pre>{@code
     * &lt;[ [ ]]>}</pre>
     * are ignored.
     *
     * @param string The source string.
     * @return A JSONObject containing the structured data from the XML string.
     * @throws XmlJsonException Thrown if there is an errors while parsing the string
     */
    public static JsonObject toJsonObject(String string) throws XmlJsonException {
        return toJsonObject(string, XmlParserConfiguration.ORIGINAL);
    }

    /**
     * Convert a well-formed (but not necessarily valid) XML into a
     * JSONObject. Some information may be lost in this transformation because
     * JSON is a data format and XML is a document format. XML uses elements,
     * attributes, and content text, while JSON uses unordered collections of
     * name/value pairs and arrays of values. JSON does not does not like to
     * distinguish between elements and attributes. Sequences of similar
     * elements are represented as JSONArrays. Content text may be placed in a
     * "content" member. Comments, prologs, DTDs, and <pre>{@code
     * &lt;[ [ ]]>}</pre>
     * are ignored.
     *
     * @param reader The XML source reader.
     * @return A JSONObject containing the structured data from the XML string.
     * @throws XmlJsonException Thrown if there is an errors while parsing the string
     */
    public static JsonObject toJsonObject(Reader reader) throws XmlJsonException {
        return toJsonObject(reader, XmlParserConfiguration.ORIGINAL);
    }

    /**
     * Convert a well-formed (but not necessarily valid) XML into a
     * JSONObject. Some information may be lost in this transformation because
     * JSON is a data format and XML is a document format. XML uses elements,
     * attributes, and content text, while JSON uses unordered collections of
     * name/value pairs and arrays of values. JSON does not does not like to
     * distinguish between elements and attributes. Sequences of similar
     * elements are represented as JSONArrays. Content text may be placed in a
     * "content" member. Comments, prologs, DTDs, and <pre>{@code
     * &lt;[ [ ]]>}</pre>
     * are ignored.
     * <p>
     * All values are converted as strings, for 1, 01, 29.0 will not be coerced to
     * numbers but will instead be the exact value as seen in the XML document.
     *
     * @param reader      The XML source reader.
     * @param keepStrings If true, then values will not be coerced into boolean
     *                    or numeric values and will instead be left as strings
     * @return A JSONObject containing the structured data from the XML string.
     * @throws XmlJsonException Thrown if there is an errors while parsing the string
     */
    public static JsonObject toJsonObject(Reader reader, boolean keepStrings) throws XmlJsonException {
        if (keepStrings) {
            return toJsonObject(reader, XmlParserConfiguration.KEEP_STRINGS);
        }
        return toJsonObject(reader, XmlParserConfiguration.ORIGINAL);
    }

    /**
     * Convert a well-formed (but not necessarily valid) XML into a
     * JSONObject. Some information may be lost in this transformation because
     * JSON is a data format and XML is a document format. XML uses elements,
     * attributes, and content text, while JSON uses unordered collections of
     * name/value pairs and arrays of values. JSON does not does not like to
     * distinguish between elements and attributes. Sequences of similar
     * elements are represented as JSONArrays. Content text may be placed in a
     * "content" member. Comments, prologs, DTDs, and <pre>{@code
     * &lt;[ [ ]]>}</pre>
     * are ignored.
     * <p>
     * All values are converted as strings, for 1, 01, 29.0 will not be coerced to
     * numbers but will instead be the exact value as seen in the XML document.
     *
     * @param reader The XML source reader.
     * @param config Configuration options for the parser
     * @return A JSONObject containing the structured data from the XML string.
     * @throws XmlJsonException Thrown if there is an errors while parsing the string
     */
    public static JsonObject toJsonObject(Reader reader, XmlParserConfiguration config) throws XmlJsonException {
        XmlToJsonObject jo = new XmlToJsonObject();
        XmlTokenizer x = new XmlTokenizer(reader);
        while (x.more()) {
            x.skipPast("<");
            if (x.more()) {
                parse(x, jo, null, config);
            }
        }
        return jo;
    }

    /**
     * Convert a well-formed (but not necessarily valid) XML string into a
     * JSONObject. Some information may be lost in this transformation because
     * JSON is a data format and XML is a document format. XML uses elements,
     * attributes, and content text, while JSON uses unordered collections of
     * name/value pairs and arrays of values. JSON does not does not like to
     * distinguish between elements and attributes. Sequences of similar
     * elements are represented as JSONArrays. Content text may be placed in a
     * "content" member. Comments, prologs, DTDs, and <pre>{@code
     * &lt;[ [ ]]>}</pre>
     * are ignored.
     * <p>
     * All values are converted as strings, for 1, 01, 29.0 will not be coerced to
     * numbers but will instead be the exact value as seen in the XML document.
     *
     * @param string      The source string.
     * @param keepStrings If true, then values will not be coerced into boolean
     *                    or numeric values and will instead be left as strings
     * @return A JSONObject containing the structured data from the XML string.
     * @throws XmlJsonException Thrown if there is an errors while parsing the string
     */
    public static JsonObject toJsonObject(String string, boolean keepStrings) throws XmlJsonException {
        return toJsonObject(new StringReader(string), keepStrings);
    }

    /**
     * Convert a well-formed (but not necessarily valid) XML string into a
     * JSONObject. Some information may be lost in this transformation because
     * JSON is a data format and XML is a document format. XML uses elements,
     * attributes, and content text, while JSON uses unordered collections of
     * name/value pairs and arrays of values. JSON does not does not like to
     * distinguish between elements and attributes. Sequences of similar
     * elements are represented as JSONArrays. Content text may be placed in a
     * "content" member. Comments, prologs, DTDs, and <pre>{@code
     * &lt;[ [ ]]>}</pre>
     * are ignored.
     * <p>
     * All values are converted as strings, for 1, 01, 29.0 will not be coerced to
     * numbers but will instead be the exact value as seen in the XML document.
     *
     * @param string The source string.
     * @param config Configuration options for the parser.
     * @return A JSONObject containing the structured data from the XML string.
     * @throws XmlJsonException Thrown if there is an errors while parsing the string
     */
    public static JsonObject toJsonObject(String string, XmlParserConfiguration config) throws XmlJsonException {
        return toJsonObject(new StringReader(string), config);
    }

    /**
     * Convert a JSONObject into a well-formed, element-normal XML string.
     *
     * @param object A JSONObject.
     * @return A string.
     * @throws XmlJsonException Thrown if there is an error parsing the string
     */
    public static String toString(Object object) throws XmlJsonException {
        return toString(object, null, XmlParserConfiguration.ORIGINAL);
    }

    /**
     * Convert a JSONObject into a well-formed, element-normal XML string.
     *
     * @param object  A JSONObject.
     * @param tagName The optional name of the enclosing tag.
     * @return A string.
     * @throws XmlJsonException Thrown if there is an error parsing the string
     */
    public static String toString(final Object object, final String tagName) {
        return toString(object, tagName, XmlParserConfiguration.ORIGINAL);
    }

    /**
     * Convert a JSONObject into a well-formed, element-normal XML string.
     *
     * @param object  A JSONObject.
     * @param tagName The optional name of the enclosing tag.
     * @param config  Configuration that can control output to XML.
     * @return A string.
     * @throws XmlJsonException Thrown if there is an error parsing the string
     */
    public static String toString(final Object object, final String tagName, final XmlParserConfiguration config)
            throws XmlJsonException {
        return toString(object, tagName, config, 0, 0);
    }

    /**
     * Convert a JSONObject into a well-formed, element-normal XML string,
     * either pretty print or single-lined depending on indent factor.
     *
     * @param object       A JSONObject.
     * @param tagName      The optional name of the enclosing tag.
     * @param config       Configuration that can control output to XML.
     * @param indentFactor The number of spaces to add to each level of indentation.
     * @param indent       The current ident level in spaces.
     * @return
     * @throws XmlJsonException
     */
    private static String toString(final Object object, final String tagName, final XmlParserConfiguration config, int indentFactor, int indent)
            throws XmlJsonException {
        StringBuilder sb = new StringBuilder();
        XmlJsonArray ja;
        XmlToJsonObject jo;
        String string;

        if (object instanceof XmlToJsonObject) {

            if (tagName != null) {
                sb.append(indent(indent));
                sb.append('<');
                sb.append(tagName);
                sb.append('>');
                if (indentFactor > 0) {
                    sb.append("\n");
                    indent += indentFactor;
                }
            }

            jo = (XmlToJsonObject) object;
            for (final String key : jo.keySet()) {
                Object value = jo.opt(key);
                if (value == null) {
                    value = "";
                } else if (value.getClass().isArray()) {
                    value = new XmlJsonArray(value);
                }

                if (key.equals(config.getcDataTagName())) {
                    if (value instanceof XmlJsonArray) {
                        ja = (XmlJsonArray)  value;
                        int jaLength = ja.length();
                        for (int i = 0; i < jaLength; i++) {
                            if (i > 0) {
                                sb.append('\n');
                            }
                            Object val = ja.opt(i);
                            sb.append(escape(val.toString()));
                        }
                    } else {
                        sb.append(escape(value.toString()));
                    }


                } else if (value instanceof XmlJsonArray) {
                    ja = (XmlJsonArray) value;
                    int jaLength = ja.length();
                    for (int i = 0; i < jaLength; i++) {
                        Object val = ja.opt(i);
                        if (val instanceof XmlJsonArray) {
                            sb.append('<');
                            sb.append(key);
                            sb.append('>');
                            sb.append(toString(val, null, config, indentFactor, indent));
                            sb.append("</");
                            sb.append(key);
                            sb.append('>');
                        } else {
                            sb.append(toString(val, key, config, indentFactor, indent));
                        }
                    }
                } else if ("".equals(value)) {
                    sb.append(indent(indent));
                    sb.append('<');
                    sb.append(key);
                    sb.append("/>");
                    if (indentFactor > 0) {
                        sb.append("\n");
                    }


                } else {
                    sb.append(toString(value, key, config, indentFactor, indent));
                }
            }
            if (tagName != null) {

                sb.append(indent(indent - indentFactor));
                sb.append("</");
                sb.append(tagName);
                sb.append('>');
                if (indentFactor > 0) {
                    sb.append("\n");
                }
            }
            return sb.toString();

        }

        boolean b = object != null && (object instanceof XmlJsonArray || object.getClass().isArray());
        if (b) {
            if (object.getClass().isArray()) {
                ja = new XmlJsonArray(object);
            } else {
                ja = (XmlJsonArray) object;
            }
            int jaLength = ja.length();
            for (int i = 0; i < jaLength; i++) {
                Object val = ja.opt(i);
                sb.append(toString(val, tagName == null ? "array" : tagName, config, indentFactor, indent));
            }
            return sb.toString();
        }


        string = (object == null) ? "null" : escape(object.toString());

        if (tagName == null) {
            return indent(indent) + "\"" + string + "\"" + ((indentFactor > 0) ? "\n" : "");
        } else if (string.length() == 0) {
            return indent(indent) + "<" + tagName + "/>" + ((indentFactor > 0) ? "\n" : "");
        } else {
            return indent(indent) + "<" + tagName
                    + ">" + string + "</" + tagName + ">" + ((indentFactor > 0) ? "\n" : "");
        }
    }

    /**
     * Convert a JSONObject into a well-formed, pretty printed element-normal XML string.
     *
     * @param object       A JSONObject.
     * @param indentFactor The number of spaces to add to each level of indentation.
     * @return A string.
     * @throws XmlJsonException Thrown if there is an error parsing the string
     */
    public static String toString(Object object, int indentFactor) {
        return toString(object, null, XmlParserConfiguration.ORIGINAL, indentFactor);
    }

    /**
     * Convert a JSONObject into a well-formed, pretty printed element-normal XML string.
     *
     * @param object       A JSONObject.
     * @param tagName      The optional name of the enclosing tag.
     * @param indentFactor The number of spaces to add to each level of indentation.
     * @return A string.
     * @throws XmlJsonException Thrown if there is an error parsing the string
     */
    public static String toString(final Object object, final String tagName, int indentFactor) {
        return toString(object, tagName, XmlParserConfiguration.ORIGINAL, indentFactor);
    }

    /**
     * Convert a JSONObject into a well-formed, pretty printed element-normal XML string.
     *
     * @param object       A JSONObject.
     * @param tagName      The optional name of the enclosing tag.
     * @param config       Configuration that can control output to XML.
     * @param indentFactor The number of spaces to add to each level of indentation.
     * @return A string.
     * @throws XmlJsonException Thrown if there is an error parsing the string
     */
    public static String toString(final Object object, final String tagName, final XmlParserConfiguration config, int indentFactor)
            throws XmlJsonException {
        return toString(object, tagName, config, indentFactor, 0);
    }

    /**
     * Return a String consisting of a number of space characters specified by indent
     *
     * @param indent The number of spaces to be appended to the String.
     * @return
     */
    private static final String indent(int indent) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indent; i++) {
            sb.append(' ');
        }
        return sb.toString();
    }
}
