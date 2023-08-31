package com.chua.common.support.jsoup.nodes;

import com.chua.common.support.jsoup.UncheckedException;

import java.io.IOException;

/**
 * A Character Data node, to support CDATA sections.
 * @author Administrator
 */
public class CharDataNode extends TextNode {
    public CharDataNode(String text) {
        super(text);
    }

    @Override
    public String nodeName() {
        return "#cdata";
    }

    /**
     * Get the unencoded, <b>non-normalized</b> text content of this CDataNode.
     * @return unencoded, non-normalized text
     */
    @Override
    public String text() {
        return getWholeText();
    }

    @Override
    void outerHtmlHead(Appendable accum, int depth, Document.OutputSettings out) throws IOException {
        accum
            .append("<![CDATA[")
            .append(getWholeText());
    }

    @Override
    void outerHtmlTail(Appendable accum, int depth, Document.OutputSettings out) {
        try {
            accum.append("]]>");
        } catch (IOException e) {
            throw new UncheckedException(e);
        }
    }

    @Override
    public CharDataNode clone() {
        return (CharDataNode) super.clone();
    }
}
