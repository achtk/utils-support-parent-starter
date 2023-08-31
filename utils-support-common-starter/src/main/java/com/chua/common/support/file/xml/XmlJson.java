package com.chua.common.support.file.xml;

/*
Public Domain.
*/

import static com.chua.common.support.file.xml.Xml.*;

/**
 * This provides static methods to convert an XML text into a XmlJsonArray or
 * JSONObject, and to covert a XmlJsonArray or JSONObject into an XML text using
 * the JsonML transform.
 *
 * @author JSON.org
 * @version 2016-01-30
 */
public class XmlJson {
    /**
     * Parse XML values and store them in a XmlJsonArray.
     *
     * @param x           The XmlTokenizer containing the source string.
     * @param arrayForm   true if array form, false if object form.
     * @param ja          The XmlJsonArray that is containing the current tag or null
     *                    if we are at the outermost level.
     * @param keepStrings Don't type-convert text nodes and attribute values
     * @return A XmlJsonArray if the value is the outermost tag, otherwise null.
     * @throws XmlJsonException if a parsing error occurs
     */
    private static Object parse(
            XmlTokenizer x,
            boolean arrayForm,
            XmlJsonArray ja,
            boolean keepStrings
    ) throws XmlJsonException {
        String attribute;
        char c;
        String closeTag = null;
        int i;
        XmlJsonArray newja = null;
        XmlToJsonObject newjo = null;
        Object token;
        String tagName = null;

// Test for and skip past these forms:
//      <!-- ... -->
//      <![  ... ]]>
//      <!   ...   >
//      <?   ...  ?>

        while (true) {
            if (!x.more()) {
                throw x.syntaxError("Bad XML");
            }
            token = x.nextContent();
            if (token == LT) {
                token = x.nextToken();
                if (token instanceof Character) {
                    if (token == SLASH) {

// Close tag </

                        token = x.nextToken();
                        if (!(token instanceof String)) {
                            throw new XmlJsonException(
                                    "Expected a closing name instead of '" +
                                            token + "'.");
                        }
                        if (x.nextToken() != GT) {
                            throw x.syntaxError("Misshaped close tag");
                        }
                        return token;
                    } else if (token == BANG) {

// <!

                        c = x.next();
                        if (c == '-') {
                            if (x.next() == '-') {
                                x.skipPast("-->");
                            } else {
                                x.back();
                            }
                        } else if (c == '[') {
                            token = x.nextToken();
                            if ("CDATA".equals(token) && x.next() == '[') {
                                if (ja != null) {
                                    ja.put(x.nextCharData());
                                }
                            } else {
                                throw x.syntaxError("Expected 'CDATA['");
                            }
                        } else {
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
                        }
                    } else if (token == QUEST) {

// <?

                        x.skipPast("?>");
                    } else {
                        throw x.syntaxError("Misshaped tag");
                    }

// Open tag <

                } else {
                    if (!(token instanceof String)) {
                        throw x.syntaxError("Bad tagName '" + token + "'.");
                    }
                    tagName = (String) token;
                    newja = new XmlJsonArray();
                    newjo = new XmlToJsonObject();
                    if (arrayForm) {
                        newja.put(tagName);
                        if (ja != null) {
                            ja.put(newja);
                        }
                    } else {
                        newjo.put("tagName", tagName);
                        if (ja != null) {
                            ja.put(newjo);
                        }
                    }
                    token = null;
                    for (; ; ) {
                        if (token == null) {
                            token = x.nextToken();
                        }
                        if (token == null) {
                            throw x.syntaxError("Misshaped tag");
                        }
                        if (!(token instanceof String)) {
                            break;
                        }

                        attribute = (String) token;
                        boolean b = !arrayForm && ("tagName".equals(attribute) || "childNode".equals(attribute));
                        if (b) {
                            throw x.syntaxError("Reserved attribute.");
                        }
                        token = x.nextToken();
                        if (token == EQ) {
                            token = x.nextToken();
                            if (!(token instanceof String)) {
                                throw x.syntaxError("Missing value");
                            }
                            newjo.accumulate(attribute, keepStrings ? ((String) token) : stringToValue((String) token));
                            token = null;
                        } else {
                            newjo.accumulate(attribute, "");
                        }
                    }
                    if (arrayForm && newjo.length() > 0) {
                        newja.put(newjo);
                    }

// Empty tag <.../>

                    if (token == SLASH) {
                        if (x.nextToken() != GT) {
                            throw x.syntaxError("Misshaped tag");
                        }
                        if (ja == null) {
                            if (arrayForm) {
                                return newja;
                            }
                            return newjo;
                        }

// Content, between <...> and </...>

                    } else {
                        if (token != GT) {
                            throw x.syntaxError("Misshaped tag");
                        }
                        closeTag = (String) parse(x, arrayForm, newja, keepStrings);
                        if (closeTag != null) {
                            if (!closeTag.equals(tagName)) {
                                throw x.syntaxError("Mismatched '" + tagName +
                                        "' and '" + closeTag + "'");
                            }
                            tagName = null;
                            if (!arrayForm && newja.length() > 0) {
                                newjo.put("childNodes", newja);
                            }
                            if (ja == null) {
                                if (arrayForm) {
                                    return newja;
                                }
                                return newjo;
                            }
                        }
                    }
                }
            } else {
                if (ja != null) {
                    ja.put(token instanceof String
                            ? keepStrings ? unescape((String) token) : stringToValue((String) token)
                            : token);
                }
            }
        }
    }


    /**
     * Convert a well-formed (but not necessarily valid) XML string into a
     * XmlJsonArray using the JsonML transform. Each XML tag is represented as
     * a XmlJsonArray in which the first element is the tag name. If the tag has
     * attributes, then the second element will be JSONObject containing the
     * name/value pairs. If the tag contains children, then strings and
     * JSONArrays will represent the child tags.
     * Comments, prologs, DTDs, and <pre>{@code &lt;[ [ ]]>}</pre> are ignored.
     *
     * @param string The source string.
     * @return A XmlJsonArray containing the structured data from the XML string.
     * @throws XmlJsonException Thrown on error converting to a XmlJsonArray
     */
    public static XmlJsonArray toJsonArray(String string) throws XmlJsonException {
        return (XmlJsonArray) parse(new XmlTokenizer(string), true, null, false);
    }


    /**
     * Convert a well-formed (but not necessarily valid) XML string into a
     * XmlJsonArray using the JsonML transform. Each XML tag is represented as
     * a XmlJsonArray in which the first element is the tag name. If the tag has
     * attributes, then the second element will be JSONObject containing the
     * name/value pairs. If the tag contains children, then strings and
     * JSONArrays will represent the child tags.
     * As opposed to toJsonArray this method does not attempt to convert
     * any text node or attribute value to any type
     * but just leaves it as a string.
     * Comments, prologs, DTDs, and <pre>{@code &lt;[ [ ]]>}</pre> are ignored.
     *
     * @param string      The source string.
     * @param keepStrings If true, then values will not be coerced into boolean
     *                    or numeric values and will instead be left as strings
     * @return A XmlJsonArray containing the structured data from the XML string.
     * @throws XmlJsonException Thrown on error converting to a XmlJsonArray
     */
    public static XmlJsonArray toJsonArray(String string, boolean keepStrings) throws XmlJsonException {
        return (XmlJsonArray) parse(new XmlTokenizer(string), true, null, keepStrings);
    }


    /**
     * Convert a well-formed (but not necessarily valid) XML string into a
     * XmlJsonArray using the JsonML transform. Each XML tag is represented as
     * a XmlJsonArray in which the first element is the tag name. If the tag has
     * attributes, then the second element will be JSONObject containing the
     * name/value pairs. If the tag contains children, then strings and
     * JSONArrays will represent the child content and tags.
     * As opposed to toJsonArray this method does not attempt to convert
     * any text node or attribute value to any type
     * but just leaves it as a string.
     * Comments, prologs, DTDs, and <pre>{@code &lt;[ [ ]]>}</pre> are ignored.
     *
     * @param x           An XmlTokenizer.
     * @param keepStrings If true, then values will not be coerced into boolean
     *                    or numeric values and will instead be left as strings
     * @return A XmlJsonArray containing the structured data from the XML string.
     * @throws XmlJsonException Thrown on error converting to a XmlJsonArray
     */
    public static XmlJsonArray toJsonArray(XmlTokenizer x, boolean keepStrings) throws XmlJsonException {
        return (XmlJsonArray) parse(x, true, null, keepStrings);
    }


    /**
     * Convert a well-formed (but not necessarily valid) XML string into a
     * XmlJsonArray using the JsonML transform. Each XML tag is represented as
     * a XmlJsonArray in which the first element is the tag name. If the tag has
     * attributes, then the second element will be JSONObject containing the
     * name/value pairs. If the tag contains children, then strings and
     * JSONArrays will represent the child content and tags.
     * Comments, prologs, DTDs, and <pre>{@code &lt;[ [ ]]>}</pre> are ignored.
     *
     * @param x An XmlTokenizer.
     * @return A XmlJsonArray containing the structured data from the XML string.
     * @throws XmlJsonException Thrown on error converting to a XmlJsonArray
     */
    public static XmlJsonArray toJsonArray(XmlTokenizer x) throws XmlJsonException {
        return (XmlJsonArray) parse(x, true, null, false);
    }


    /**
     * Convert a well-formed (but not necessarily valid) XML string into a
     * JSONObject using the JsonML transform. Each XML tag is represented as
     * a JSONObject with a "tagName" property. If the tag has attributes, then
     * the attributes will be in the JSONObject as properties. If the tag
     * contains children, the object will have a "childNodes" property which
     * will be an array of strings and JsonML JSONObjects.
     * <p>
     * Comments, prologs, DTDs, and <pre>{@code &lt;[ [ ]]>}</pre> are ignored.
     *
     * @param string The XML source text.
     * @return A JSONObject containing the structured data from the XML string.
     * @throws XmlJsonException Thrown on error converting to a JSONObject
     */
    public static XmlJsonException toJsonObject(String string) throws XmlJsonException {
        return (XmlJsonException) parse(new XmlTokenizer(string), false, null, false);
    }


    /**
     * Convert a well-formed (but not necessarily valid) XML string into a
     * JSONObject using the JsonML transform. Each XML tag is represented as
     * a JSONObject with a "tagName" property. If the tag has attributes, then
     * the attributes will be in the JSONObject as properties. If the tag
     * contains children, the object will have a "childNodes" property which
     * will be an array of strings and JsonML JSONObjects.
     * <p>
     * Comments, prologs, DTDs, and <pre>{@code &lt;[ [ ]]>}</pre> are ignored.
     *
     * @param string      The XML source text.
     * @param keepStrings If true, then values will not be coerced into boolean
     *                    or numeric values and will instead be left as strings
     * @return A JSONObject containing the structured data from the XML string.
     * @throws XmlJsonException Thrown on error converting to a JSONObject
     */
    public static XmlJsonException toJsonObject(String string, boolean keepStrings) throws XmlJsonException {
        return (XmlJsonException) parse(new XmlTokenizer(string), false, null, keepStrings);
    }


    /**
     * Convert a well-formed (but not necessarily valid) XML string into a
     * JSONObject using the JsonML transform. Each XML tag is represented as
     * a JSONObject with a "tagName" property. If the tag has attributes, then
     * the attributes will be in the JSONObject as properties. If the tag
     * contains children, the object will have a "childNodes" property which
     * will be an array of strings and JsonML JSONObjects.
     * <p>
     * Comments, prologs, DTDs, and <pre>{@code &lt;[ [ ]]>}</pre> are ignored.
     *
     * @param x An XmlTokenizer of the XML source text.
     * @return A JSONObject containing the structured data from the XML string.
     * @throws XmlJsonException Thrown on error converting to a JSONObject
     */
    public static XmlJsonException toJsonObject(XmlTokenizer x) throws XmlJsonException {
        return (XmlJsonException) parse(x, false, null, false);
    }


    /**
     * Convert a well-formed (but not necessarily valid) XML string into a
     * JSONObject using the JsonML transform. Each XML tag is represented as
     * a JSONObject with a "tagName" property. If the tag has attributes, then
     * the attributes will be in the JSONObject as properties. If the tag
     * contains children, the object will have a "childNodes" property which
     * will be an array of strings and JsonML JSONObjects.
     * <p>
     * Comments, prologs, DTDs, and <pre>{@code &lt;[ [ ]]>}</pre> are ignored.
     *
     * @param x           An XmlTokenizer of the XML source text.
     * @param keepStrings If true, then values will not be coerced into boolean
     *                    or numeric values and will instead be left as strings
     * @return A JSONObject containing the structured data from the XML string.
     * @throws XmlJsonException Thrown on error converting to a JSONObject
     */
    public static XmlJsonException toJsonObject(XmlTokenizer x, boolean keepStrings) throws XmlJsonException {
        return (XmlJsonException) parse(x, false, null, keepStrings);
    }


    /**
     * Reverse the JSONML transformation, making an XML text from a XmlJsonArray.
     *
     * @param ja A XmlJsonArray.
     * @return An XML string.
     * @throws XmlJsonException Thrown on error converting to a string
     */
    public static String toString(XmlJsonArray ja) throws XmlJsonException {
        int i;
        XmlToJsonObject jo;
        int length;
        Object object;
        StringBuilder sb = new StringBuilder();
        String tagName;

// Emit <tagName

        tagName = ja.getString(0);
        noSpace(tagName);
        tagName = escape(tagName);
        sb.append('<');
        sb.append(tagName);

        object = ja.opt(1);
        if (object instanceof XmlToJsonObject) {
            i = 2;
            jo = (XmlToJsonObject) object;

// Emit the attributes

            // Don't use the new entrySet API to maintain Android support
            for (final String key : jo.keySet()) {
                final Object value = jo.opt(key);
                noSpace(key);
                if (value != null) {
                    sb.append(' ');
                    sb.append(escape(key));
                    sb.append('=');
                    sb.append('"');
                    sb.append(escape(value.toString()));
                    sb.append('"');
                }
            }
        } else {
            i = 1;
        }

// Emit content in body

        length = ja.length();
        if (i >= length) {
            sb.append('/');
            sb.append('>');
        } else {
            sb.append('>');
            do {
                object = ja.get(i);
                i += 1;
                if (object != null) {
                    if (object instanceof String) {
                        sb.append(escape(object.toString()));
                    } else if (object instanceof XmlJsonException) {
                        sb.append(toString((XmlToJsonObject) object));
                    } else if (object instanceof XmlJsonArray) {
                        sb.append(toString((XmlJsonArray) object));
                    } else {
                        sb.append(object);
                    }
                }
            } while (i < length);
            sb.append('<');
            sb.append('/');
            sb.append(tagName);
            sb.append('>');
        }
        return sb.toString();
    }

    /**
     * Reverse the JSONML transformation, making an XML text from a JSONObject.
     * The JSONObject must contain a "tagName" property. If it has children,
     * then it must have a "childNodes" property containing an array of objects.
     * The other properties are attributes with string values.
     *
     * @param jo A JSONObject.
     * @return An XML string.
     * @throws XmlJsonException Thrown on error converting to a string
     */
    public static String toString(XmlToJsonObject jo) throws XmlJsonException {
        StringBuilder sb = new StringBuilder();
        int i;
        XmlJsonArray ja;
        int length;
        Object object;
        String tagName;
        Object value;

//Emit <tagName

        tagName = jo.optString("tagName");
        if (tagName == null) {
            return escape(jo.toString());
        }
        noSpace(tagName);
        tagName = escape(tagName);
        sb.append('<');
        sb.append(tagName);

//Emit the attributes

        // Don't use the new entrySet API to maintain Android support
        for (final String key : jo.keySet()) {
            if (!"tagName".equals(key) && !"childNodes".equals(key)) {
                noSpace(key);
                value = jo.opt(key);
                if (value != null) {
                    sb.append(' ');
                    sb.append(escape(key));
                    sb.append('=');
                    sb.append('"');
                    sb.append(escape(value.toString()));
                    sb.append('"');
                }
            }
        }

//Emit content in body

        ja = jo.optJSONArray("childNodes");
        if (ja == null) {
            sb.append('/');
            sb.append('>');
        } else {
            sb.append('>');
            length = ja.length();
            for (i = 0; i < length; i += 1) {
                object = ja.get(i);
                if (object != null) {
                    if (object instanceof String) {
                        sb.append(escape(object.toString()));
                    } else if (object instanceof XmlToJsonObject) {
                        sb.append(toString((XmlToJsonObject) object));
                    } else if (object instanceof XmlJsonArray) {
                        sb.append(toString((XmlJsonArray) object));
                    } else {
                        sb.append(object.toString());
                    }
                }
            }
            sb.append('<');
            sb.append('/');
            sb.append(tagName);
            sb.append('>');
        }
        return sb.toString();
    }
}
