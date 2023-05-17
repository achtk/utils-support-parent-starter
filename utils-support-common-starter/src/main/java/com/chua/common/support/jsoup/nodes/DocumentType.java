package com.chua.common.support.jsoup.nodes;

import com.chua.common.support.jsoup.helper.Validate;
import com.chua.common.support.jsoup.nodes.Document.OutputSettings.Syntax;
import com.chua.common.support.utils.StringUtils;

import java.io.IOException;

/**
 * A {@code <!DOCTYPE>} node.
 *
 * @author Administrator
 */
public class DocumentType extends LeafNode {
        public static final String PUBLIC_KEY = "PUBLIC";
    public static final String SYSTEM_KEY = "SYSTEM";
    private static final String NAME = "name";
    private static final String PUB_SYS_KEY = "pubSysKey";
    private static final String PUBLIC_ID = "publicId";
    private static final String SYSTEM_ID = "systemId";

    /**
     * Create a new doctype element.
     *
     * @param name     the doctype's name
     * @param publicId the doctype's public ID
     * @param systemId the doctype's system ID
     */
    public DocumentType(String name, String publicId, String systemId) {
        Validate.notNull(name);
        Validate.notNull(publicId);
        Validate.notNull(systemId);
        attr(NAME, name);
        attr(PUBLIC_ID, publicId);
        attr(SYSTEM_ID, systemId);
        updatePubSyskey();
    }

    public void setPubSysKey(String value) {
        if (value != null) {
            attr(PUB_SYS_KEY, value);
        }
    }

    private void updatePubSyskey() {
        if (has(PUBLIC_ID)) {
            attr(PUB_SYS_KEY, PUBLIC_KEY);
        } else if (has(SYSTEM_ID)) {
            attr(PUB_SYS_KEY, SYSTEM_KEY);
        }
    }

    /**
     * Get this doctype's name (when set, or empty string)
     * @return doctype name
     */
    public String name() {
        return attr(NAME);
    }

    /**
     * Get this doctype's Public ID (when set, or empty string)
     * @return doctype Public ID
     */
    public String publicId() {
        return attr(PUBLIC_ID);
    }

    /**
     * Get this doctype's System ID (when set, or empty string)
     * @return doctype System ID
     */
    public String systemId() {
        return attr(SYSTEM_ID);
    }

    @Override
    public String nodeName() {
        return "#doctype";
    }

    @Override
    void outerHtmlHead(Appendable accum, int depth, Document.OutputSettings out) throws IOException {
        if (siblingIndex > 0 && out.prettyPrint()) {
            accum.append('\n');
        }

        if (out.syntax() == Syntax.html && !has(PUBLIC_ID) && !has(SYSTEM_ID)) {
            accum.append("<!doctype");
        } else {
            accum.append("<!DOCTYPE");
        }
        if (has(NAME)) {
            accum.append(" ").append(attr(NAME));
        }
        if (has(PUB_SYS_KEY)) {
            accum.append(" ").append(attr(PUB_SYS_KEY));
        }
        if (has(PUBLIC_ID)) {
            accum.append(" \"").append(attr(PUBLIC_ID)).append('"');
        }
        if (has(SYSTEM_ID)) {
            accum.append(" \"").append(attr(SYSTEM_ID)).append('"');
        }
        accum.append('>');
    }

    @Override
    void outerHtmlTail(Appendable accum, int depth, Document.OutputSettings out) {
    }

    private boolean has(final String attribute) {
        return !StringUtils.isBlank(attr(attribute));
    }
}
