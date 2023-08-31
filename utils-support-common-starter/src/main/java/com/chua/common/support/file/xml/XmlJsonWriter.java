package com.chua.common.support.file.xml;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

import static com.chua.common.support.constant.CommonConstant.*;

/*
Public Domain.
*/

/**
 * XmlJsonWriter provides a quick and convenient way of producing JSON text.
 * The texts produced strictly conform to JSON syntax rules. No whitespace is
 * added, so the results are ready for transmission or storage. Each instance of
 * XmlJsonWriter can produce one JSON text.
 * <p>
 * A XmlJsonWriter instance provides a <code>value</code> method for appending
 * values to the
 * text, and a <code>key</code>
 * method for adding keys before values in objects. There are <code>array</code>
 * and <code>endArray</code> methods that make and bound array values, and
 * <code>object</code> and <code>endObject</code> methods which make and bound
 * object values. All of these methods return the XmlJsonWriter instance,
 * permitting a cascade style. For example, <pre>
 * new XmlJsonWriter(myWriter)
 *     .object()
 *         .key("JSON")
 *         .value("Hello, World!")
 *     .endObject();</pre> which writes <pre>
 * {"JSON":"Hello, World!"}</pre>
 * <p>
 * The first method called must be <code>array</code> or <code>object</code>.
 * There are no methods for adding commas or colons. XmlJsonWriter adds them for
 * you. Objects and arrays can be nested up to 200 levels deep.
 * <p>
 * This can sometimes be easier than using a JSONObject to build a string.
 *
 * @author JSON.org
 * @version 2016-08-08
 */
public class XmlJsonWriter {
    private static final int MAX_DEPTH = 200;

    /**
     * The comma flag determines if a comma should be output before the next
     * value.
     */
    private boolean comma;

    /**
     * The current mode. Values:
     * 'a' (array),
     * 'd' (done),
     * 'i' (initial),
     * 'k' (key),
     * 'o' (object).
     */
    protected char mode;

    /**
     * The object/array stack.
     */
    private final XmlToJsonObject[] stack;

    /**
     * The stack top index. A value of 0 indicates that the stack is empty.
     */
    private int top;

    /**
     * The writer that will receive the output.
     */
    protected Appendable writer;

    /**
     * Make a fresh XmlJsonWriter. It can be used to build one JSON text.
     *
     * @param w an appendable object
     */
    public XmlJsonWriter(Appendable w) {
        this.comma = false;
        this.mode = 'i';
        this.stack = new XmlToJsonObject[MAX_DEPTH];
        this.top = 0;
        this.writer = w;
    }

    /**
     * Append a value.
     *
     * @param string A string value.
     * @return this
     * @throws XmlJsonException If the value is out of sequence.
     */
    private XmlJsonWriter append(String string) throws XmlJsonException {
        if (string == null) {
            throw new XmlJsonException("Null pointer");
        }
        if (this.mode == LETTER_LOWERCASE_O || this.mode == LETTER_LOWERCASE_A) {
            try {
                if (this.comma && this.mode == LETTER_LOWERCASE_A) {
                    this.writer.append(',');
                }
                this.writer.append(string);
            } catch (IOException e) {
                // Android as of API 25 does not support this exception constructor
                // however we won't worry about it. If an exception is happening here
                // it will just throw a "Method not found" exception instead.
                throw new XmlJsonException(e);
            }
            if (this.mode == LETTER_LOWERCASE_O) {
                this.mode = 'k';
            }
            this.comma = true;
            return this;
        }
        throw new XmlJsonException("Value out of sequence.");
    }

    /**
     * Begin appending a new array. All values until the balancing
     * <code>endArray</code> will be appended to this array. The
     * <code>endArray</code> method must be called to mark the array's end.
     *
     * @return this
     * @throws XmlJsonException If the nesting is too deep, or if the object is
     *                       started in the wrong place (for example as a key or after the end of the
     *                       outermost array or object).
     */
    public XmlJsonWriter array() throws XmlJsonException {
        if (this.mode == LETTER_LOWERCASE_I || this.mode == LETTER_LOWERCASE_O || this.mode == LETTER_LOWERCASE_A) {
            this.push(null);
            this.append("[");
            this.comma = false;
            return this;
        }
        throw new XmlJsonException("Misplaced array.");
    }

    /**
     * End something.
     *
     * @param m Mode
     * @param c Closing character
     * @return this
     * @throws XmlJsonException If unbalanced.
     */
    private XmlJsonWriter end(char m, char c) throws XmlJsonException {
        if (this.mode != m) {
            throw new XmlJsonException(m == 'a'
                    ? "Misplaced endArray."
                    : "Misplaced endObject.");
        }
        this.pop(m);
        try {
            this.writer.append(c);
        } catch (IOException e) {
            // Android as of API 25 does not support this exception constructor
            // however we won't worry about it. If an exception is happening here
            // it will just throw a "Method not found" exception instead.
            throw new XmlJsonException(e);
        }
        this.comma = true;
        return this;
    }

    /**
     * End an array. This method most be called to balance calls to
     * <code>array</code>.
     *
     * @return this
     * @throws XmlJsonException If incorrectly nested.
     */
    public XmlJsonWriter endArray() throws XmlJsonException {
        return this.end('a', ']');
    }

    /**
     * End an object. This method most be called to balance calls to
     * <code>object</code>.
     *
     * @return this
     * @throws XmlJsonException If incorrectly nested.
     */
    public XmlJsonWriter endObject() throws XmlJsonException {
        return this.end('k', '}');
    }

    /**
     * Append a key. The key will be associated with the next value. In an
     * object, every value must be preceded by a key.
     *
     * @param string A key string.
     * @return this
     * @throws XmlJsonException If the key is out of place. For example, keys
     *                       do not belong in arrays or if the key is null.
     */
    public XmlJsonWriter key(String string) throws XmlJsonException {
        if (string == null) {
            throw new XmlJsonException("Null key.");
        }
        if (this.mode == LETTER_LOWERCASE_K) {
            try {
                XmlToJsonObject topObject = this.stack[this.top - 1];
                // don't use the built in putOnce method to maintain Android support
                if (topObject.has(string)) {
                    throw new XmlJsonException("Duplicate key \"" + string + "\"");
                }
                topObject.put(string, true);
                if (this.comma) {
                    this.writer.append(',');
                }
                this.writer.append(XmlToJsonObject.quote(string));
                this.writer.append(':');
                this.comma = false;
                this.mode = 'o';
                return this;
            } catch (IOException e) {
                // Android as of API 25 does not support this exception constructor
                // however we won't worry about it. If an exception is happening here
                // it will just throw a "Method not found" exception instead.
                throw new XmlJsonException(e);
            }
        }
        throw new XmlJsonException("Misplaced key.");
    }


    /**
     * Begin appending a new object. All keys and values until the balancing
     * <code>endObject</code> will be appended to this object. The
     * <code>endObject</code> method must be called to mark the object's end.
     *
     * @return this
     * @throws XmlJsonException If the nesting is too deep, or if the object is
     *                       started in the wrong place (for example as a key or after the end of the
     *                       outermost array or object).
     */
    public XmlJsonWriter object() throws XmlJsonException {
        if (this.mode == LETTER_LOWERCASE_I) {
            this.mode = 'o';
        }
        if (this.mode == LETTER_LOWERCASE_O || this.mode == LETTER_LOWERCASE_A) {
            this.append("{");
            this.push(new XmlToJsonObject());
            this.comma = false;
            return this;
        }
        throw new XmlJsonException("Misplaced object.");

    }


    /**
     * Pop an array or object scope.
     *
     * @param c The scope to close.
     * @throws XmlJsonException If nesting is wrong.
     */
    private void pop(char c) throws XmlJsonException {
        if (this.top <= 0) {
            throw new XmlJsonException("Nesting error.");
        }
        char m = this.stack[this.top - 1] == null ? 'a' : 'k';
        if (m != c) {
            throw new XmlJsonException("Nesting error.");
        }
        this.top -= 1;
        this.mode = this.top == 0
                ? 'd'
                : this.stack[this.top - 1] == null
                ? 'a'
                : 'k';
    }

    /**
     * Push an array or object scope.
     *
     * @param jo The scope to open.
     * @throws XmlJsonException If nesting is too deep.
     */
    private void push(XmlToJsonObject jo) throws XmlJsonException {
        if (this.top >= MAX_DEPTH) {
            throw new XmlJsonException("Nesting too deep.");
        }
        this.stack[this.top] = jo;
        this.mode = jo == null ? 'a' : 'k';
        this.top += 1;
    }

    /**
     * @param value The value to be serialized.
     * @return string
     * @throws XmlJsonException If the value is or contains an invalid number.
     */
    public static String valueToString(Object value) throws XmlJsonException {
        if (value == null || value.equals(null)) {
            return "null";
        }
        if (value instanceof XmlJsonString) {
            String object;
            try {
                object = ((XmlJsonString) value).toJsonString();
            } catch (Exception e) {
                throw new XmlJsonException(e);
            }
            if (object != null) {
                return object;
            }
            throw new XmlJsonException("Bad value from toJSONString: " + object);
        }
        if (value instanceof Number) {
            // not all Numbers may match actual JSON Numbers. i.e. Fractions or Complex
            final String numberAsString = XmlToJsonObject.numberToString((Number) value);
            if (XmlToJsonObject.NUMBER_PATTERN.matcher(numberAsString).matches()) {
                // Close enough to a JSON number that we will return it unquoted
                return numberAsString;
            }
            // The Number value is not a valid JSON number.
            // Instead we will quote it as a string
            return XmlToJsonObject.quote(numberAsString);
        }
        if (value instanceof Boolean || value instanceof XmlToJsonObject
                || value instanceof XmlJsonArray) {
            return value.toString();
        }
        if (value instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) value;
            return new XmlToJsonObject(map).toString();
        }
        if (value instanceof Collection) {
            Collection<?> coll = (Collection<?>) value;
            return new XmlJsonArray(coll).toString();
        }
        if (value.getClass().isArray()) {
            return new XmlJsonArray(value).toString();
        }
        if (value instanceof Enum<?>) {
            return XmlToJsonObject.quote(((Enum<?>) value).name());
        }
        return XmlToJsonObject.quote(value.toString());
    }

    /**
     * Append either the value <code>true</code> or the value
     * <code>false</code>.
     *
     * @param b A boolean.
     * @return this
     * @throws XmlJsonException if a called function has an error
     */
    public XmlJsonWriter value(boolean b) throws XmlJsonException {
        return this.append(b ? "true" : "false");
    }

    /**
     * Append a double value.
     *
     * @param d A double.
     * @return this
     * @throws XmlJsonException If the number is not finite.
     */
    public XmlJsonWriter value(double d) throws XmlJsonException {
        return this.value(Double.valueOf(d));
    }

    /**
     * Append a long value.
     *
     * @param l A long.
     * @return this
     * @throws XmlJsonException if a called function has an error
     */
    public XmlJsonWriter value(long l) throws XmlJsonException {
        return this.append(Long.toString(l));
    }


    /**
     * Append an object value.
     *
     * @param object The object to append. It can be null, or a Boolean, Number,
     *               String, JSONObject, or XmlJsonArray, or an object that implements JSONString.
     * @return this
     * @throws XmlJsonException If the value is out of sequence.
     */
    public XmlJsonWriter value(Object object) throws XmlJsonException {
        return this.append(valueToString(object));
    }
}
