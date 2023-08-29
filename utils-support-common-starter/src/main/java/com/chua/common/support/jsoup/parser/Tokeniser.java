package com.chua.common.support.jsoup.parser;

import com.chua.common.support.jsoup.helper.Validate;
import com.chua.common.support.jsoup.nodes.Entities;
import com.chua.common.support.utils.StringUtils;

import java.util.Arrays;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_HASH;
import static com.chua.common.support.constant.CommonConstant.SYMBOL_HASH_CHAR;


/**
 * Readers the input stream into tokens.
 */
final class Tokeniser {
    /**
     * replaces null character
     */
    static final char REPLACEMENT_CHAR = '\uFFFD';
    private static final char[] NOT_CHAR_REF_CHARS_SORTED = new char[]{'\t', '\n', '\r', '\f', ' ', '<', '&'};

    static final int WIN_1252_EXTENSIONS_START = 0x80;
    static final int[] WIN_1252_EXTENSIONS = new int[]{
            0x20AC, 0x0081, 0x201A, 0x0192, 0x201E, 0x2026, 0x2020, 0x2021,
            0x02C6, 0x2030, 0x0160, 0x2039, 0x0152, 0x008D, 0x017D, 0x008F,
            0x0090, 0x2018, 0x2019, 0x201C, 0x201D, 0x2022, 0x2013, 0x2014,
            0x02DC, 0x2122, 0x0161, 0x203A, 0x0153, 0x009D, 0x017E, 0x0178,
    };

    static {
        Arrays.sort(NOT_CHAR_REF_CHARS_SORTED);
    }

    private final CharacterReader reader;
    private final ParseErrorList errors;

    private TokeniserState state = TokeniserState.Data;
     private Token emitPending = null;
    private boolean isEmitPending = false;
     private String charsString = null;
    private final StringBuilder charsBuilder = new StringBuilder(1024);
    StringBuilder dataBuffer = new StringBuilder(1024);

    Token.StartTag startPending = new Token.StartTag();
    Token.EndTag endPending = new Token.EndTag();
    Token.Tag tagPending;
    Token.Character charPending = new Token.Character();
    Token.Doctype doctypePending = new Token.Doctype();
    Token.Comment commentPending = new Token.Comment();
     private String lastStartTag;
     private String lastStartCloseSeq;

    private static final int UNSET = -1;
    private int markupStartPos, charStartPos = UNSET;

    Tokeniser(CharacterReader reader, ParseErrorList errors) {
        this.reader = reader;
        this.errors = errors;
        tagPending = startPending;
    }

    Token read() {
        while (!isEmitPending) {
            state.read(this, reader);
        }

        final StringBuilder cb = this.charsBuilder;
        if (cb.length() != 0) {
            String str = cb.toString();
            cb.delete(0, cb.length());
            Token token = charPending.data(str);
            charsString = null;
            return token;
        } else if (charsString != null) {
            Token token = charPending.data(charsString);
            charsString = null;
            return token;
        } else {
            isEmitPending = false;
            assert emitPending != null;
            return emitPending;
        }
    }

    void emit(Token token) {
        Validate.isFalse(isEmitPending);

        emitPending = token;
        isEmitPending = true;
        token.startPos(markupStartPos);
        token.endPos(reader.pos());
        charStartPos = UNSET;

        if (token.type == Token.TokenType.START_TAG) {
            Token.StartTag startTag = (Token.StartTag) token;
            lastStartTag = startTag.tagName;
            lastStartCloseSeq = null;
        } else if (token.type == Token.TokenType.END_TAG) {
            Token.EndTag endTag = (Token.EndTag) token;
            if (endTag.hasAttributes()) {
                error("Attributes incorrectly present on end tag [/%s]", endTag.normalName());
            }
        }
    }

    void emit(final String str) {
        if (charsString == null) {
            charsString = str;
        } else {
            if (charsBuilder.length() == 0) {
                charsBuilder.append(charsString);
            }
            charsBuilder.append(str);
        }
        charPending.startPos(charStartPos);
        charPending.endPos(reader.pos());
    }

    void emit(final StringBuilder str) {
        if (charsString == null) {
            charsString = str.toString();
        } else {
            if (charsBuilder.length() == 0) {
                charsBuilder.append(charsString);
            }
            charsBuilder.append(str);
        }
        charPending.startPos(charStartPos);
        charPending.endPos(reader.pos());
    }

    void emit(char c) {
        if (charsString == null) {
            charsString = String.valueOf(c);
        } else {
            if (charsBuilder.length() == 0) {
                charsBuilder.append(charsString);
            }
            charsBuilder.append(c);
        }
        charPending.startPos(charStartPos);
        charPending.endPos(reader.pos());
    }

    void emit(char[] chars) {
        emit(String.valueOf(chars));
    }

    void emit(int[] codepoints) {
        emit(new String(codepoints, 0, codepoints.length));
    }

    TokeniserState getState() {
        return state;
    }

    void transition(TokeniserState newState) {
        switch (newState) {
            case TagOpen:
                markupStartPos = reader.pos();
                break;
            case Data:
                if (charStartPos == UNSET) {
                    charStartPos = reader.pos();
                }
                break;
            default:
        }

        this.state = newState;
    }

    void advanceTransition(TokeniserState newState) {
        transition(newState);
        reader.advance();
    }

    final private int[] codepointHolder = new int[1];
    final private int[] multipointHolder = new int[2];
     int[] consumeCharacterReference( Character additionalAllowedCharacter, boolean inAttribute) {
        if (reader.isEmpty()) {
            return null;
        }
        if (additionalAllowedCharacter != null && additionalAllowedCharacter == reader.current()) {
            return null;
        }
        if (reader.matchesAnySorted(NOT_CHAR_REF_CHARS_SORTED)) {
            return null;
        }

        final int[] codeRef = codepointHolder;
        reader.mark();
        if (reader.matchConsume(SYMBOL_HASH)) {
            boolean isHexMode = reader.matchConsumeIgnoreCase("X");
            String numRef = isHexMode ? reader.consumeHexSequence() : reader.consumeDigitSequence();
            if (numRef.length() == 0) {
                characterReferenceError("numeric reference with no numerals");
                reader.rewindToMark();
                return null;
            }

            reader.unmark();
            if (!reader.matchConsume(";")) {
                characterReferenceError("missing semicolon on [&#%s]", numRef);
            }
            int charval = -1;
            try {
                int base = isHexMode ? 16 : 10;
                charval = Integer.valueOf(numRef, base);
            } catch (NumberFormatException ignored) {
            }

            boolean b = charval == -1 || (charval >= 0xD800 && charval <= 0xDFFF) || charval > 0x10FFFF;
            if (b) {
                characterReferenceError("character [%s] outside of valid range", charval);
                codeRef[0] = REPLACEMENT_CHAR;
            } else {
                if (charval >= WIN_1252_EXTENSIONS_START && charval < WIN_1252_EXTENSIONS_START + WIN_1252_EXTENSIONS.length) {
                    characterReferenceError("character [%s] is not a valid unicode code point", charval);
                    charval = WIN_1252_EXTENSIONS[charval - WIN_1252_EXTENSIONS_START];
                }

                codeRef[0] = charval;
            }
            return codeRef;
        } else {
            String nameRef = reader.consumeLetterThenDigitSequence();
            boolean looksLegit = reader.matches(';');
            boolean found = (Entities.isBaseNamedEntity(nameRef) || (Entities.isNamedEntity(nameRef) && looksLegit));

            if (!found) {
                reader.rewindToMark();
                if (looksLegit) {
                    characterReferenceError("invalid named reference [%s]", nameRef);
                }
                return null;
            }

            boolean b = inAttribute && (reader.matchesLetter() || reader.matchesDigit() || reader.matchesAny('=', '-', '_'));
            if (b) {
                reader.rewindToMark();
                return null;
            }

            reader.unmark();
            if (!reader.matchConsume(";")) {
                characterReferenceError("missing semicolon on [&%s]", nameRef);
            }
            int numChars = Entities.codepointsForName(nameRef, multipointHolder);
            if (numChars == 1) {
                codeRef[0] = multipointHolder[0];
                return codeRef;
            } else if (numChars ==2) {
                return multipointHolder;
            } else {
                Validate.fail("Unexpected characters returned for " + nameRef);
                return multipointHolder;
            }
        }
    }

    Token.Tag createTagPending(boolean start) {
        tagPending = start ? startPending.reset() : endPending.reset();
        return tagPending;
    }

    void emitTagPending() {
        tagPending.finaliseTag();
        emit(tagPending);
    }

    void createCommentPending() {
        commentPending.reset();
    }

    void emitCommentPending() {
        emit(commentPending);
    }

    void createBogusCommentPending() {
        commentPending.reset();
        commentPending.bogus = true;
    }

    void createDoctypePending() {
        doctypePending.reset();
    }

    void emitDoctypePending() {
        emit(doctypePending);
    }

    void createTempBuffer() {
        Token.reset(dataBuffer);
    }

    boolean isAppropriateEndTagToken() {
        return lastStartTag != null && tagPending.name().equalsIgnoreCase(lastStartTag);
    }

     String appropriateEndTagName() {
        return lastStartTag;
    }

    /** Returns the closer sequence {@code </lastStart} */
    String appropriateEndTagSeq() {
        if (lastStartCloseSeq == null) {
            lastStartCloseSeq = "</" + lastStartTag;
        }
        return lastStartCloseSeq;
    }

    void error(TokeniserState state) {
        if (errors.canAddError()) {
            errors.add(new ParseError(reader, "Unexpected character '%s' in input state [%s]", reader.current(), state));
        }
    }

    void eofError(TokeniserState state) {
        if (errors.canAddError()) {
            errors.add(new ParseError(reader, "Unexpectedly reached end of file (EOF) in input state [%s]", state));
        }
    }

    private void characterReferenceError(String message, Object... args) {
        if (errors.canAddError()) {
            errors.add(new ParseError(reader, String.format("Invalid character reference: " + message, args)));
        }
    }

    void error(String errorMsg) {
        if (errors.canAddError()) {
            errors.add(new ParseError(reader, errorMsg));
        }
    }

    void error(String errorMsg, Object... args) {
        if (errors.canAddError()) {
            errors.add(new ParseError(reader, errorMsg, args));
        }
    }

    boolean currentNodeInHtmlNS() {
        return true;
        // return currentNode != null && currentNode.namespace().equals("HTML");
    }

    /**
     * Utility method to consume reader and unescape entities found within.
     * @param inAttribute if the text to be unescaped is in an attribute
     * @return unescaped string from reader
     */
    String unescapeEntities(boolean inAttribute) {
        StringBuilder builder = StringUtils.borrowBuilder();
        while (!reader.isEmpty()) {
            builder.append(reader.consumeTo('&'));
            if (reader.matches('&')) {
                reader.consume();
                int[] c = consumeCharacterReference(null, inAttribute);
                if (c == null || c.length==0) {
                    builder.append('&');
                } else {
                    builder.appendCodePoint(c[0]);
                    if (c.length == 2) {
                        builder.appendCodePoint(c[1]);
                    }
                }

            }
        }
        return builder.toString();
    }
}
