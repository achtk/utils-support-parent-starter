package com.chua.common.support.jsoup.nodes;

import com.chua.common.support.jsoup.SerializationException;
import com.chua.common.support.jsoup.helper.Validate;

import java.io.IOException;

import static com.chua.common.support.utils.StringUtils.borrowBuilder;

/**
 * An XML Declaration.
 */
public class XmlDeclaration extends LeafNode {

    /**
     * Create a new XML declaration
     * @param name of declaration
     * @param isProcessingInstruction is processing instruction
     */
    public XmlDeclaration(String name, boolean isProcessingInstruction) {
        Validate.notNull(name);
        value = name;
        this.isProcessingInstruction = isProcessingInstruction;
    }

    public String nodeName() {
        return "#declaration";
    }

    /**
     * Get the name of this declaration.
     * @return name of this declaration.
     */
    public String name() {
        return coreValue();
    }

    /**
     * Get the unencoded XML declaration.
     * @return XML declaration
     */
    public String getWholeDeclaration() {
        StringBuilder sb = borrowBuilder();
        try {
            getWholeDeclaration(sb, new Document.OutputSettings());
        } catch (IOException e) {
            throw new SerializationException(e);
        }
        return sb.toString().trim();
    }

    private void getWholeDeclaration(Appendable accum, Document.OutputSettings out) throws IOException {
        for (Attribute attribute : attributes()) {
            String key = attribute.getKey();
            String val = attribute.getValue();
            if (!key.equals(nodeName())) {
                accum.append(' ');
                // basically like Attribute, but skip empty vals in XML
                accum.append(key);
                if (!val.isEmpty()) {
                    accum.append("=\"");
                    Entities.escape(accum, val, out, true, false, false, false);
                    accum.append('"');
                }
            }
        }
    }

    void outerHtmlHead(Appendable accum, int depth, Document.OutputSettings out) throws IOException {
        accum
            .append("<")
            .append(isProcessingInstruction ? "!" : "?")
            .append(coreValue());
        getWholeDeclaration(accum, out);
        accum
            .append(isProcessingInstruction ? "!" : "?")
            .append(">");
    }

    void outerHtmlTail(Appendable accum, int depth, Document.OutputSettings out) {
    }

    @Override
    public String toString() {
        return outerHtml();
    }

    @Override
    public XmlDeclaration clone() {
        return (XmlDeclaration) super.clone();
    }
}
