package com.chua.common.support.jsoup.parser;

import com.chua.common.support.jsoup.helper.Validate;
import com.chua.common.support.jsoup.internal.Normalizer;

import java.util.HashMap;
import java.util.Map;

/**
 * HTML Tag capabilities.
 *
 * @author Jonathan Hedley, jonathan@hedley.net
 */
public class Tag implements Cloneable {
    private static final Map<String, Tag> TAGS = new HashMap<>();
    private String tagName;
    /**
     * always the lower case version of this tag, regardless of case preservation mode
     */
    private final String normalName;
    /**
     * block
     */
    private boolean isBlock = true;
    /**
     * should be formatted as a block
     */
    private boolean formatAsBlock = true;
    /**
     * can hold nothing; e.g. img
     */
    private boolean empty = false;
    /**
     * can self close (<foo />). used for unknown tags that self close, without forcing them as empty.
     */
    private boolean selfClosing = false;
    /**
     * for pre, textarea, script etc
     */
    private boolean preserveWhitespace = false;
    /**
     * a control that appears in forms: input, textarea, output etc
     */
    private boolean formList = false;
    /**
     * a control that can be submitted in a form: input etc
     */
    private boolean formSubmit = false;

    private Tag(String tagName) {
        this.tagName = tagName;
        normalName = Normalizer.lowerCase(tagName);
    }

    /**
     * Get this tag's name.
     *
     * @return the tag's name
     */
    public String getName() {
        return tagName;
    }

    /**
     * Get this tag's normalized (lowercased) name.
     * @return the tag's normal name.
     */
    public String normalName() {
        return normalName;
    }

    /**
     * Get a Tag by name. If not previously defined (unknown), returns a new generic tag, that can do anything.
     * <p>
     * Pre-defined tags (P, DIV etc) will be ==, but unknown tags are not registered and will only .equals().
     * </p>
     * 
     * @param tagName Name of tag, e.g. "p". Case insensitive.
     * @param settings used to control tag name sensitivity
     * @return The tag, either defined or new generic.
     */
    public static Tag valueOf(String tagName, ParseSettings settings) {
        Validate.notNull(tagName);
        Tag tag = TAGS.get(tagName);

        if (tag == null) {
            tagName = settings.normalizeTag(tagName);
            Validate.notEmpty(tagName);
            String normalName = Normalizer.lowerCase(tagName);
            tag = TAGS.get(normalName);

            if (tag == null) {
                tag = new Tag(tagName);
                tag.isBlock = false;
            } else if (settings.preserveTagCase() && !tagName.equals(normalName))  {
                tag = tag.clone();
                tag.tagName = tagName;
            }
        }
        return tag;
    }

    /**
     * Get a Tag by name. If not previously defined (unknown), returns a new generic tag, that can do anything.
     * <p>
     * Pre-defined tags (P, DIV etc) will be ==, but unknown tags are not registered and will only .equals().
     * </p>
     *
     * @param tagName Name of tag, e.g. "p". <b>Case sensitive</b>.
     * @return The tag, either defined or new generic.
     */
    public static Tag valueOf(String tagName) {
        return valueOf(tagName, ParseSettings.PRESERVE_CASE);
    }

    /**
     * Gets if this is a block tag.
     *
     * @return if block tag
     */
    public boolean isBlock() {
        return isBlock;
    }

    /**
     * Gets if this tag should be formatted as a block (or as inline)
     *
     * @return if should be formatted as block or inline
     */
    public boolean formatAsBlock() {
        return formatAsBlock;
    }

    /**
     * Gets if this tag is an inline tag.
     *
     * @return if this tag is an inline tag.
     */
    public boolean isInline() {
        return !isBlock;
    }

    /**
     * Get if this is an empty tag
     *
     * @return if this is an empty tag
     */
    public boolean isEmpty() {
        return empty;
    }

    /**
     * Get if this tag is self closing.
     *
     * @return if this tag should be output as self closing.
     */
    public boolean isSelfClosing() {
        return empty || selfClosing;
    }

    /**
     * Get if this is a pre-defined tag, or was auto created on parsing.
     *
     * @return if a known tag
     */
    public boolean isKnownTag() {
        return TAGS.containsKey(tagName);
    }

    /**
     * Check if this tagname is a known tag.
     *
     * @param tagName name of tag
     * @return if known HTML tag
     */
    public static boolean isKnownTag(String tagName) {
        return TAGS.containsKey(tagName);
    }

    /**
     * Get if this tag should preserve whitespace within child text nodes.
     *
     * @return if preserve whitespace
     */
    public boolean preserveWhitespace() {
        return preserveWhitespace;
    }

    /**
     * Get if this tag represents a control associated with a form. E.g. input, textarea, output
     * @return if associated with a form
     */
    public boolean isFormListed() {
        return formList;
    }

    /**
     * Get if this tag represents an element that should be submitted with a form. E.g. input, option
     * @return if submittable with a form
     */
    public boolean isFormSubmittable() {
        return formSubmit;
    }

    Tag setSelfClosing() {
        selfClosing = true;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Tag)) {
            return false;
        }

        Tag tag = (Tag) o;

        if (!tagName.equals(tag.tagName)) {
            return false;
        }
        if (empty != tag.empty) {
            return false;
        }
        if (formatAsBlock != tag.formatAsBlock) {
            return false;
        }
        if (isBlock != tag.isBlock) {
            return false;
        }
        if (preserveWhitespace != tag.preserveWhitespace) {
            return false;
        }
        if (selfClosing != tag.selfClosing) {
            return false;
        }
        if (formList != tag.formList) {
            return false;
        }
        return formSubmit == tag.formSubmit;
    }

    @Override
    public int hashCode() {
        int result = tagName.hashCode();
        result = 31 * result + (isBlock ? 1 : 0);
        result = 31 * result + (formatAsBlock ? 1 : 0);
        result = 31 * result + (empty ? 1 : 0);
        result = 31 * result + (selfClosing ? 1 : 0);
        result = 31 * result + (preserveWhitespace ? 1 : 0);
        result = 31 * result + (formList ? 1 : 0);
        result = 31 * result + (formSubmit ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return tagName;
    }

    @Override
    protected Tag clone() {
        try {
            return (Tag) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * prepped from http://www.w3.org/TR/REC-html40/sgml/dtd.html and other sources
     */
    private static final String[] BLOCK_TAGS = {
            "html", "head", "body", "frameset", "script", "noscript", "style", "meta", "link", "title", "frame",
            "noframes", "section", "nav", "aside", "hgroup", "header", "footer", "p", "h1", "h2", "h3", "h4", "h5", "h6",
            "ul", "ol", "pre", "div", "blockquote", "hr", "address", "figure", "figcaption", "form", "fieldset", "ins",
            "del", "dl", "dt", "dd", "li", "table", "caption", "thead", "tfoot", "tbody", "colgroup", "col", "tr", "th",
            "td", "video", "audio", "canvas", "details", "menu", "plaintext", "template", "article", "main",
            "svg", "math", "center", "template",
            "dir", "applet", "marquee", "listing"
    };
    private static final String[] INLINE_TAGS = {
            "object", "base", "font", "tt", "i", "b", "u", "big", "small", "em", "strong", "dfn", "code", "samp", "kbd",
            "var", "cite", "abbr", "time", "acronym", "mark", "ruby", "rt", "rp", "a", "img", "br", "wbr", "map", "q",
            "sub", "sup", "bdo", "iframe", "embed", "span", "input", "select", "textarea", "label", "button", "optgroup",
            "option", "legend", "datalist", "keygen", "output", "progress", "meter", "area", "param", "source", "track",
            "summary", "command", "device", "area", "basefont", "bgsound", "menuitem", "param", "source", "track",
            "data", "bdi", "s", "strike", "nobr"
    };
    private static final String[] EMPTY_TAGS = {
            "meta", "link", "base", "frame", "img", "br", "wbr", "embed", "hr", "input", "keygen", "col", "command",
            "device", "area", "basefont", "bgsound", "menuitem", "param", "source", "track"
    };
    private static final String[] FORMAT_AS_INLINE_TAGS = {
            "title", "a", "p", "h1", "h2", "h3", "h4", "h5", "h6", "pre", "address", "li", "th", "td", "script", "style",
            "ins", "del", "s"
    };
    private static final String[] PRESERVE_WHITESPACE_TAGS = {
            "pre", "plaintext", "title", "textarea"
    };
    private static final String[] FORM_LISTED_TAGS = {
            "button", "fieldset", "input", "keygen", "object", "output", "select", "textarea"
    };
    private static final String[] FORM_SUBMIT_TAGS = {
            "input", "keygen", "object", "select", "textarea"
    };

    static {
        for (String tagName : BLOCK_TAGS) {
            Tag tag = new Tag(tagName);
            register(tag);
        }
        for (String tagName : INLINE_TAGS) {
            Tag tag = new Tag(tagName);
            tag.isBlock = false;
            tag.formatAsBlock = false;
            register(tag);
        }

        for (String tagName : EMPTY_TAGS) {
            Tag tag = TAGS.get(tagName);
            Validate.notNull(tag);
            tag.empty = true;
        }

        for (String tagName : FORMAT_AS_INLINE_TAGS) {
            Tag tag = TAGS.get(tagName);
            Validate.notNull(tag);
            tag.formatAsBlock = false;
        }

        for (String tagName : PRESERVE_WHITESPACE_TAGS) {
            Tag tag = TAGS.get(tagName);
            Validate.notNull(tag);
            tag.preserveWhitespace = true;
        }

        for (String tagName : FORM_LISTED_TAGS) {
            Tag tag = TAGS.get(tagName);
            Validate.notNull(tag);
            tag.formList = true;
        }

        for (String tagName : FORM_SUBMIT_TAGS) {
            Tag tag = TAGS.get(tagName);
            Validate.notNull(tag);
            tag.formSubmit = true;
        }
    }

    private static void register(Tag tag) {
        TAGS.put(tag.tagName, tag);
    }
}
