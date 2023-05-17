package com.chua.common.support.jsoup.nodes;

import com.chua.common.support.jsoup.parser.Tag;

/**
 * Represents a {@link TextNode} as an {@link Element}, to enable text nodes to be selected with
 * the {@link com.chua.common.support.jsoup.select.Selector} {@code :matchText} syntax.
 *
 * @author Administrator
 */
public class PseudoTextElement extends Element {

    public PseudoTextElement(Tag tag, String baseUri, Attributes attributes) {
        super(tag, baseUri, attributes);
    }

    @Override
    void outerHtmlHead(Appendable accum, int depth, Document.OutputSettings out) {
    }

    @Override
    void outerHtmlTail(Appendable accum, int depth, Document.OutputSettings out) {
    }
}
