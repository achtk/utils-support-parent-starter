package com.chua.common.support.lang.spider.model;

import com.chua.common.support.lang.spider.selector.Selector;

/**
 * The object contains 'ExtractBy' information.
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.2.0
 */
class Extractor {

    protected Selector selector;

    protected final Source source;

    protected final boolean notNull;

    protected final boolean multi;

    static enum Source {
        /**
         * Html
         */
        HTML,
        /**
         * Url
         */
        URL,
        /**
         * RawHtml
         */
        RAW_HTML,
        /**
         * RawText
         */
        RAW_TEXT
    }

    public Extractor(Selector selector, Source source, boolean notNull, boolean multi) {
        this.selector = selector;
        this.source = source;
        this.notNull = notNull;
        this.multi = multi;
    }

    Selector getSelector() {
        return selector;
    }

    Source getSource() {
        return source;
    }

    boolean isNotNull() {
        return notNull;
    }

    boolean isMulti() {
        return multi;
    }

    void setSelector(Selector selector) {
        this.selector = selector;
    }
}
