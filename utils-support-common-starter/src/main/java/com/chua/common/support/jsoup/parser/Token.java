package com.chua.common.support.jsoup.parser;

import com.chua.common.support.jsoup.helper.Validate;
import com.chua.common.support.jsoup.nodes.Attributes;


/**
 * Parse tokens for the Tokeniser.
 */
abstract class Token {
    TokenType type;
    static final int UNSET = -1;
    /**
     * position in CharacterReader this token was read from
     */
    private int startPos, endPos = UNSET;

    private Token() {
    }
    
    String tokenType() {
        return this.getClass().getSimpleName();
    }

    /**
     * Reset the data represent by this token, for reuse. Prevents the need to create transfer objects for every
     * piece of data, which immediately get GCed.
     */
    Token reset() {
        startPos = UNSET;
        endPos = UNSET;
        return this;
    }

    int startPos() {
        return startPos;
    }

    void startPos(int pos) {
        startPos = pos;
    }

    int endPos() {
        return endPos;
    }

    void endPos(int pos) {
        endPos = pos;
    }

    static void reset(StringBuilder sb) {
        if (sb != null) {
            sb.delete(0, sb.length());
        }
    }

    static final class Doctype extends Token {
        final StringBuilder name = new StringBuilder();
        String pubSysKey = null;
        final StringBuilder publicIdentifier = new StringBuilder();
        final StringBuilder systemIdentifier = new StringBuilder();
        boolean forceQuirks = false;

        Doctype() {
            type = TokenType.DOCTYPE;
        }

        @Override
        Token reset() {
            super.reset();
            reset(name);
            pubSysKey = null;
            reset(publicIdentifier);
            reset(systemIdentifier);
            forceQuirks = false;
            return this;
        }

        String getName() {
            return name.toString();
        }

        String getPubSysKey() {
            return pubSysKey;
        }

        String getPublicIdentifier() {
            return publicIdentifier.toString();
        }

        public String getSystemIdentifier() {
            return systemIdentifier.toString();
        }

        public boolean isForceQuirks() {
            return forceQuirks;
        }

        @Override
        public String toString() {
            return "<!doctype " + getName() + ">";
        }
    }

    static abstract class Tag extends Token {
         protected String tagName;
         protected String normalName; // lc version of tag name, for case insensitive tree build

        private final StringBuilder attrName = new StringBuilder(); // try to get attr names and vals in one shot, vs Builder
         private String attrNameS;
        private boolean hasAttrName = false;

        private final StringBuilder attrValue = new StringBuilder();
         private String attrValueS;
        private boolean hasAttrValue = false;
        private boolean hasEmptyAttrValue = false; // distinguish boolean attribute from empty string value

        boolean selfClosing = false;
         Attributes attributes; // start tags get attributes on construction. End tags get attributes on first new attribute (but only for parser convenience, not used).

        @Override
        Tag reset() {
            super.reset();
            tagName = null;
            normalName = null;
            reset(attrName);
            attrNameS = null;
            hasAttrName = false;
            reset(attrValue);
            attrValueS = null;
            hasEmptyAttrValue = false;
            hasAttrValue = false;
            selfClosing = false;
            attributes = null;
            return this;
        }

        /* Limits runaway crafted HTML from spewing attributes and getting a little sluggish in ensureCapacity.
        Real-world HTML will P99 around 8 attributes, so plenty of headroom. Implemented here and not in the Attributes
        object so that API users can add more if ever required. */
        private static final int MAX_ATTRIBUTES = 512;

        final void newAttribute() {
            if (attributes == null) {
                attributes = new Attributes();
            }

            if (hasAttrName && attributes.size() < MAX_ATTRIBUTES) {
                // the tokeniser has skipped whitespace control chars, but trimming could collapse to empty for other control codes, so verify here
                String name = attrName.length() > 0 ? attrName.toString() : attrNameS;
                name = name.trim();
                if (name.length() > 0) {
                    String value;
                    if (hasAttrValue) {
                        value = attrValue.length() > 0 ? attrValue.toString() : attrValueS;
                    } else if (hasEmptyAttrValue) {
                        value = "";
                    } else {
                        value = null;
                    }
                    // note that we add, not put. So that the first is kept, and rest are deduped, once in a context where case sensitivity is known (the appropriate tree builder).
                    attributes.add(name, value);
                }
            }
            reset(attrName);
            attrNameS = null;
            hasAttrName = false;

            reset(attrValue);
            attrValueS = null;
            hasAttrValue = false;
            hasEmptyAttrValue = false;
        }

        final boolean hasAttributes() {
            return attributes != null;
        }

        final boolean hasAttribute(String key) {
            return attributes != null && attributes.hasKey(key);
        }

        final void finaliseTag() {
            // finalises for emit
            if (hasAttrName) {
                newAttribute();
            }
        }

        /** Preserves case */
        final String name() { // preserves case, for input into Tag.valueOf (which may drop case)
            Validate.isFalse(tagName == null || tagName.length() == 0);
            return tagName;
        }

        /** Lower case */
        final String normalName() { // lower case, used in tree building for working out where in tree it should go
            return normalName;
        }

        final String toStringName() {
            return tagName != null ? tagName : "[unset]";
        }

        final Tag name(String name) {
            tagName = name;
            normalName = ParseSettings.normalName(tagName);
            return this;
        }

        final boolean isSelfClosing() {
            return selfClosing;
        }

        // these appenders are rarely hit in not null state-- caused by null chars.
        final void appendTagName(String append) {
            // might have null chars - need to replace with null replacement character
            append = append.replace(TokeniserState.nullChar, Tokeniser.REPLACEMENT_CHAR);
            tagName = tagName == null ? append : tagName.concat(append);
            normalName = ParseSettings.normalName(tagName);
        }

        final void appendTagName(char append) {
            appendTagName(String.valueOf(append));
        }

        final void appendAttributeName(String append) {
            // might have null chars because we eat in one pass - need to replace with null replacement character
            append = append.replace(TokeniserState.nullChar, Tokeniser.REPLACEMENT_CHAR);

            ensureAttrName();
            if (attrName.length() == 0) {
                attrNameS = append;
            } else {
                attrName.append(append);
            }
        }

        final void appendAttributeName(char append) {
            ensureAttrName();
            attrName.append(append);
        }

        final void appendAttributeValue(String append) {
            ensureAttrValue();
            if (attrValue.length() == 0) {
                attrValueS = append;
            } else {
                attrValue.append(append);
            }
        }

        final void appendAttributeValue(char append) {
            ensureAttrValue();
            attrValue.append(append);
        }

        final void appendAttributeValue(char[] append) {
            ensureAttrValue();
            attrValue.append(append);
        }

        final void appendAttributeValue(int[] appendCodepoints) {
            ensureAttrValue();
            for (int codepoint : appendCodepoints) {
                attrValue.appendCodePoint(codepoint);
            }
        }
        
        final void setEmptyAttributeValue() {
            hasEmptyAttrValue = true;
        }

        private void ensureAttrName() {
            hasAttrName = true;
            // if on second hit, we'll need to move to the builder
            if (attrNameS != null) {
                attrName.append(attrNameS);
                attrNameS = null;
            }
        }

        private void ensureAttrValue() {
            hasAttrValue = true;
            // if on second hit, we'll need to move to the builder
            if (attrValueS != null) {
                attrValue.append(attrValueS);
                attrValueS = null;
            }
        }

        /**
         * toString
         *
         * @return str
         */
        @Override
        abstract public String toString();
    }

    final static class StartTag extends Tag {
        StartTag() {
            super();
            type = TokenType.START_TAG;
        }

        @Override
        Tag reset() {
            super.reset();
            attributes = null;
            return this;
        }

        StartTag nameAttr(String name, Attributes attributes) {
            this.tagName = name;
            this.attributes = attributes;
            normalName = ParseSettings.normalName(tagName);
            return this;
        }

        @Override
        public String toString() {
            if (hasAttributes() && attributes.size() > 0) {
                return "<" + toStringName() + " " + attributes.toString() + ">";
            } else {
                return "<" + toStringName() + ">";
            }
        }
    }

    final static class EndTag extends Tag{
        EndTag() {
            super();
            type = TokenType.END_TAG;
        }

        @Override
        public String toString() {
            return "</" + toStringName() + ">";
        }
    }

    final static class Comment extends Token {
        private final StringBuilder data = new StringBuilder();
        private String dataS; // try to get in one shot
        boolean bogus = false;

        @Override
        Token reset() {
            super.reset();
            reset(data);
            dataS = null;
            bogus = false;
            return this;
        }

        Comment() {
            type = TokenType.COMMENT;
        }

        String getData() {
            return dataS != null ? dataS : data.toString();
        }

        final Comment append(String append) {
            ensureData();
            if (data.length() == 0) {
                dataS = append;
            } else {
                data.append(append);
            }
            return this;
        }

        final Comment append(char append) {
            ensureData();
            data.append(append);
            return this;
        }

        private void ensureData() {
            // if on second hit, we'll need to move to the builder
            if (dataS != null) {
                data.append(dataS);
                dataS = null;
            }
        }

        @Override
        public String toString() {
            return "<!--" + getData() + "-->";
        }
    }

    static class Character extends Token {
        private String data;

        Character() {
            super();
            type = TokenType.CHARACTER;
        }

        @Override
        Token reset() {
            super.reset();
            data = null;
            return this;
        }

        Character data(String data) {
            this.data = data;
            return this;
        }

        String getData() {
            return data;
        }

        @Override
        public String toString() {
            return getData();
        }
    }

    final static class CharData extends Character {
        CharData(String data) {
            super();
            this.data(data);
        }

        @Override
        public String toString() {
            return "<![CDATA[" + getData() + "]]>";
        }

    }

    final static class Eof extends Token {
        Eof() {
            type = TokenType.EOF;
        }

        @Override
        Token reset() {
            super.reset();
            return this;
        }

        @Override
        public String toString() {
            return "";
        }
    }

    final boolean isDoctype() {
        return type == TokenType.DOCTYPE;
    }

    final Doctype asDoctype() {
        return (Doctype) this;
    }

    final boolean isStartTag() {
        return type == TokenType.START_TAG;
    }

    final StartTag asStartTag() {
        return (StartTag) this;
    }

    final boolean isEndTag() {
        return type == TokenType.END_TAG;
    }

    final EndTag asEndTag() {
        return (EndTag) this;
    }

    final boolean isComment() {
        return type == TokenType.COMMENT;
    }

    final Comment asComment() {
        return (Comment) this;
    }

    final boolean isCharacter() {
        return type == TokenType.CHARACTER;
    }

    final boolean isCharData() {
        return this instanceof CharData;
    }

    final Character asCharacter() {
        return (Character) this;
    }

    final boolean isEof() {
        return type == TokenType.EOF;
    }

    public enum TokenType {
        /**
         * dt
         */
        DOCTYPE,
        /**
         * st
         */
        START_TAG,
        /**
         * et
         */
        END_TAG,
        /**
         * c
         */
        COMMENT,
        /**
         * note no CData - treated in builder as an extension of Character
         */
        CHARACTER,
        /**
         * eof
         */
        EOF
    }
}
