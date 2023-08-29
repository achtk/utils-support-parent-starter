package com.chua.common.support.lang.emoji;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_HASH_CHAR;
import static com.chua.common.support.lang.emoji.EmojiManager.EMOJI_TRIE;


/**
 * emoji
 *
 * @author CH
 */
@AllArgsConstructor(staticName = "of")
public class EmojiFactory {

    private final String code;
    private static final int S2 = 2;
    private static final int S4 = 4;
    private static final char COLON = ':';
    private static final char SEMICOLON = ';';
    private static final char OR = '|';
    private static final char AND = '&';
    private static final char HASH = '#';

    /**
     * 解析emoji
     *
     * @return emoji
     */
    public String parseFromUnicode() {
        return parseFromUnicode("");
    }

    /**
     * 解析emoji
     *
     * @param type 类型
     * @return emoji
     */
    public String parseFromUnicode(String type) {
        return parseFromUnicode(create(type));
    }

    /**
     * 解析emoji
     *
     * @param transformer 转化器
     * @return emoji
     */
    private String parseFromUnicode(Function<UnicodeCandidate, String> transformer) {
        int prev = 0;
        StringBuilder sb = new StringBuilder(code.length());
        List<UnicodeCandidate> replacements = getUnicodeCandidates(code);
        for (UnicodeCandidate candidate : replacements) {
            sb.append(code, prev, candidate.getEmojiStartIndex());
            sb.append(transformer.apply(candidate));
            prev = candidate.getFitzpatrickEndIndex();
        }

        return sb.append(code.substring(prev)).toString();
    }

    /**
     * 解码
     *
     * @param input 输入
     * @return 解码器
     */
    protected static List<UnicodeCandidate> getUnicodeCandidates(String input) {
        char[] inputCharArray = input.toCharArray();
        List<UnicodeCandidate> candidates = new ArrayList<UnicodeCandidate>();
        UnicodeCandidate next;
        for (int i = 0; (next = getNextUnicodeCandidate(inputCharArray, i)) != null; i = next.getFitzpatrickEndIndex()) {
            candidates.add(next);
        }

        return candidates;
    }

    /**
     * 解码
     *
     * @param chars 输入
     * @param start 位置
     * @return 解码器
     */
    protected static UnicodeCandidate getNextUnicodeCandidate(char[] chars, int start) {
        for (int i = start; i < chars.length; i++) {
            int emojiEnd = getEmojiEndPos(chars, i);

            if (emojiEnd != -1) {
                Emoji emoji = getByUnicode(new String(chars, i, emojiEnd - i));
                String fitzpatrickString = (emojiEnd + 2 <= chars.length) ?
                        new String(chars, emojiEnd, 2) :
                        null;
                return new UnicodeCandidate(emoji, i);
            }
        }

        return null;
    }

    /**
     * 获取结束位置
     *
     * @param text     输入
     * @param startPos 开始位置
     * @return 结束位置
     */
    protected static int getEmojiEndPos(char[] text, int startPos) {
        int best = -1;
        for (int j = startPos + 1; j <= text.length; j++) {
            EmojiTrie.Matches status = EMOJI_TRIE.isEmoji(text, startPos, j);

            if (status.exactMatch()) {
                best = j;
            } else if (status.impossibleMatch()) {
                return best;
            }
        }

        return best;
    }

    /**
     * unicode
     *
     * @param unicode unicode
     * @return Emoji
     */
    public static Emoji getByUnicode(String unicode) {
        if (unicode == null) {
            return null;
        }
        return EMOJI_TRIE.getEmoji(unicode);
    }

    /**
     * 转化器
     *
     * @param type 类型
     * @return 转化器
     */
    private Function<UnicodeCandidate, String> create(String type) {
        return unicodeCandidate -> {
            switch (type) {
                default:
                case "PARSE":
                case "REMOVE":
                case "IGNORE":
                    return ":" +
                            unicodeCandidate.getEmoji().getAliases().get(0) +
                            ":";
            }
        };
    }

    /**
     * 获取emoji
     *
     * @return emoji
     */
    public String parseToHtmlHex() {
        return parseFromUnicode(unicodeCandidate -> unicodeCandidate.getEmoji().getHexHtml());
    }

    /**
     * 获取emoji
     *
     * @return emoji
     */
    public String parseToHtmlDecimal() {
        return parseFromUnicode(unicodeCandidate -> unicodeCandidate.getEmoji().getDecimalHtml());
    }


    /**
     * 获取emoji
     *
     * @return emoji
     */
    public String parseToUnicode() {
        StringBuilder sb = new StringBuilder(code.length());

        for (int last = 0; last < code.length(); last++) {
            AliasCandidate alias = getAliasAt(code, last);
            if (alias == null) {
                alias = getHtmlEncodedEmojiAt(code, last);
            }

            if (alias != null) {
                sb.append(alias.emoji.getUnicode());
                last = alias.endIndex;
            } else {
                sb.append(code.charAt(last));
            }
        }

        return sb.toString();
    }


    /**
     * Finds the HTML encoded emoji in the given string starting at the given point, null otherwise
     */
    protected static AliasCandidate getHtmlEncodedEmojiAt(String input, int start) {
        if (input.length() < start + S4 || input.charAt(start) != AND || input.charAt(start + 1) != SYMBOL_HASH_CHAR) {
            return null;
        }

        Emoji longestEmoji = null;
        int longestCodePointEnd = -1;
        char[] chars = new char[EMOJI_TRIE.maxDepth];
        int charsIndex = 0;
        int codePointStart = start;
        do {
            int codePointEnd = input.indexOf(SEMICOLON, codePointStart + 3);
            if (codePointEnd == -1) {
                break;
            }

            try {
                int radix = input.charAt(codePointStart + 2) == 'x' ? 16 : 10;
                int codePoint = Integer.parseInt(input.substring(codePointStart + 2 + radix / 16, codePointEnd), radix);
                charsIndex += Character.toChars(codePoint, chars, charsIndex);
            } catch (IllegalArgumentException e) {
                break;
            }
            Emoji foundEmoji = EMOJI_TRIE.getEmoji(chars, 0, charsIndex);
            if (foundEmoji != null) {
                longestEmoji = foundEmoji;
                longestCodePointEnd = codePointEnd;
            }
            codePointStart = codePointEnd + 1;
        } while (input.length() > codePointStart + 4 &&
                input.charAt(codePointStart) == AND &&
                input.charAt(codePointStart + 1) == HASH &&
                charsIndex < chars.length &&
                !EMOJI_TRIE.isEmoji(chars, 0, charsIndex).impossibleMatch());

        if (longestEmoji == null) {
            return null;
        }
        return new AliasCandidate(longestEmoji, start, longestCodePointEnd);
    }

    /**
     * Finds the alias in the given string starting at the given point, null otherwise
     */
    protected static AliasCandidate getAliasAt(String input, int start) {
        if (input.length() < start + S2 || input.charAt(start) != COLON) {
            return null;
        }
        int aliasEnd = input.indexOf(COLON, start + 2);
        if (aliasEnd == -1) {
            return null;
        }

        int fitzpatrickStart = input.indexOf(OR, start + 2);
        if (fitzpatrickStart != -1 && fitzpatrickStart < aliasEnd) {
            Emoji emoji = EmojiUtils.getForAlias(input.substring(start, fitzpatrickStart));
            if (emoji == null) {
                return null;
            }
            return new AliasCandidate(emoji, start, aliasEnd);
        }

        Emoji emoji = EmojiUtils.getForAlias(input.substring(start, aliasEnd));
        if (emoji == null) {
            return null;
        }
        return new AliasCandidate(emoji, start, aliasEnd);
    }

    @Data
    protected static class AliasCandidate {
        public final Emoji emoji;
        public final int startIndex;
        public final int endIndex;
    }

    @Data
    @AllArgsConstructor
    public static class UnicodeCandidate {
        private final Emoji emoji;
        private final int startIndex;

        public int getEmojiEndIndex() {
            return startIndex + emoji.getUnicode().length();
        }

        public int getFitzpatrickEndIndex() {
            return getEmojiEndIndex();
        }

        public int getEmojiStartIndex() {
            return startIndex;
        }
    }
}
