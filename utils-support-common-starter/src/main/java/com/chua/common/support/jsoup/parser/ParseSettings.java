package com.chua.common.support.jsoup.parser;

import com.chua.common.support.jsoup.nodes.Attributes;

import static com.chua.common.support.jsoup.internal.Normalizer.lowerCase;

/**
 * Controls parser case settings, to optionally preserve tag and/or attribute name case.
 *
 * @author Administrator
 */
public class ParseSettings {
    /**
     * HTML default settings: both tag and attribute names are lower-cased during parsing.
     */
    public static final ParseSettings HTML_DEFAULT;
    /**
     * Preserve both tag and attribute case.
     */
    public static final ParseSettings PRESERVE_CASE;

    static {
        HTML_DEFAULT = new ParseSettings(false, false);
        PRESERVE_CASE = new ParseSettings(true, true);
    }

    private final boolean preserveTagCase;
    private final boolean preserveAttributeCase;

    /**
     * Returns true if preserving tag name case.
     */
    public boolean preserveTagCase() {
        return preserveTagCase;
    }

    /**
     * Returns true if preserving attribute case.
     */
    public boolean preserveAttributeCase() {
        return preserveAttributeCase;
    }

    /**
     * Define parse settings.
     * @param tag preserve tag case?
     * @param attribute preserve attribute name case?
     */
    public ParseSettings(boolean tag, boolean attribute) {
        preserveTagCase = tag;
        preserveAttributeCase = attribute;
    }

    ParseSettings(ParseSettings copy) {
        this(copy.preserveTagCase, copy.preserveAttributeCase);
    }

    /**
     * Normalizes a tag name according to the case preservation setting.
     */
    public String normalizeTag(String name) {
        name = name.trim();
        if (!preserveTagCase) {
            name = lowerCase(name);
        }
        return name;
    }

    /**
     * Normalizes an attribute according to the case preservation setting.
     */
    public String normalizeAttribute(String name) {
        name = name.trim();
        if (!preserveAttributeCase) {
            name = lowerCase(name);
        }
        return name;
    }
    Attributes normalizeAttributes(Attributes attributes) {
        if (attributes != null && !preserveAttributeCase) {
            attributes.normalize();
        }
        return attributes;
    }

    /** Returns the normal name that a Tag will have (trimmed and lower-cased) */
    static String normalName(String name) {
        return lowerCase(name.trim());
    }
}
