package com.chua.common.support.lang.emoji;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utils to deal with emojis
 *
 * @author Krishna Chaitanya Thota
 */
public class EmojiUtils extends AbstractEmoji {


    /**
     * Get emoji by unicode, short code, decimal html entity or hexadecimal html
     * entity
     *
     * @param code unicode, short code, decimal html entity or hexadecimal html
     * @return Emoji
     */
    public static Emoji getEmoji(String code) {
        Matcher m = SHORT_CODE_PATTERN.matcher(code);
        // test for shortcode with colons
        if (m.find()) {
            code = m.group(1);
        }

        String fCode = code;
        Optional<Emoji> first = EmojiManager.data()
                .stream()
                .filter(it -> {
                    boolean b = fCode.equals(it.getEmojiChar())
                            || fCode.equalsIgnoreCase(it.getHexHtml())
                            || fCode.equalsIgnoreCase(it.getDecimalHtml())
                            || fCode.equalsIgnoreCase(it.getDecimalSurrogateHtml())
                            || fCode.equalsIgnoreCase(it.getHexHtmlShort())
                            || fCode.equalsIgnoreCase(it.getDecimalHtmlShort())
                            || null != it.getAliases() && it.getAliases().contains(fCode)
                            || null != it.getEmoticons() && it.getEmoticons().contains(fCode);

                    return b;
                }).findFirst();

        return first.isPresent() ? first.get() : null;
    }

    /**
     * get emoji by alias
     *
     * @param alias code
     * @return Emoji
     */
    public static Emoji getForAlias(String alias) {
        if (alias == null || alias.isEmpty()) {
            return null;
        }
        return EmojiManager.EMOJIS_BY_ALIAS.get(trimAlias(alias));
    }

    /**
     * trim
     *
     * @param alias alias
     * @return alias
     */
    private static String trimAlias(String alias) {
        int len = alias.length();
        return alias.substring(
                alias.charAt(0) == ':' ? 1 : 0,
                alias.charAt(len - 1) == ':' ? len - 1 : len);
    }

    /**
     * Checks if an Emoji exists for the unicode, short code, decimal or
     * hexadecimal html entity
     *
     * @param code unicode, short code, decimal html entity or hexadecimal html
     * @return is emoji
     */
    public static boolean isEmoji(String code) {
        return getEmoji(code) == null ? false : true;
    }

    /**
     * Converts emoji short codes or html entities in string with emojis
     *
     * @param text String to emojify
     * @return emojified String
     */
    public static String emojify(String text) {
        return emojify(text, 0);

    }

    private static String emojify(String text, int startIndex) {
        text = processStringWithRegex(text, SHORT_CODE_OR_HTML_ENTITY_PATTERN, startIndex, true);

        // emotions should be processed in second go.
        // this will avoid conflicts with shortcodes. For Example: :p:p should
        // not
        // be processed as shortcode, but as emoticon
        text = processStringWithRegex(text, EmojiManager.getEmoticonRegexPattern(), startIndex, true);

        return text;
    }

    /**
     * Common method used for processing the string to replace with emojis
     *
     * @param text           text
     * @param pattern        pattern
     * @param startIndex     startIndex
     * @param recurseEmojify recurseEmojify
     * @return String
     */
    private static String processStringWithRegex(String text, Pattern pattern, int startIndex, boolean recurseEmojify) {
        Matcher matcher = pattern.matcher(text);
        StringBuffer sb = new StringBuffer();
        int resetIndex = 0;

        if (startIndex > 0) {
            matcher.region(startIndex, text.length());
        }

        while (matcher.find()) {

            String emojiCode = matcher.group();

            Emoji emoji = getEmoji(emojiCode);
            // replace matched tokens with emojis

            if (emoji != null) {
                matcher.appendReplacement(sb, emoji.getEmojiChar());
            } else {
                if (HTML_SURROGATE_ENTITY_PATTERN_2.matcher(emojiCode).matches()) {
                    String highSurrogate1 = matcher.group("H1");
                    String highSurrogate2 = matcher.group("H2");
                    String lowSurrogate1 = matcher.group("L1");
                    String lowSurrogate2 = matcher.group("L2");
                    matcher.appendReplacement(sb, processStringWithRegex(highSurrogate1 + highSurrogate2, SHORT_CODE_OR_HTML_ENTITY_PATTERN, 0, false));

                    //basically this handles &#junk1;&#10084;&#65039;&#junk2; scenario
                    //verifies if &#junk1;&#10084; or &#junk1; are valid emojis via recursion
                    //if not move past &#junk1; and reset the cursor to &#10084;
                    if (sb.toString().endsWith(highSurrogate2)) {
                        resetIndex = sb.length() - highSurrogate2.length();
                    } else {
                        resetIndex = sb.length();
                    }
                    sb.append(lowSurrogate1);
                    sb.append(lowSurrogate2);
                    break;
                } else if (HTML_SURROGATE_ENTITY_PATTERN.matcher(emojiCode).matches()) {
                    //could be individual html entities assumed as surrogate pair
                    String highSurrogate = matcher.group("H");
                    String lowSurrogate = matcher.group("L");
                    matcher.appendReplacement(sb, processStringWithRegex(highSurrogate, HTML_ENTITY_PATTERN, 0, true));
                    resetIndex = sb.length();
                    sb.append(lowSurrogate);
                    break;
                } else {
                    matcher.appendReplacement(sb, emojiCode);
                }
            }

        }
        matcher.appendTail(sb);

        //do not recurse emojify when coming here through htmlSurrogateEntityPattern2..so we get a chance to check if the tail
        //is part of a surrogate entity
        if (recurseEmojify && resetIndex > 0) {
            return emojify(sb.toString(), resetIndex);
        }
        return sb.toString();
    }

    /**
     * Counts valid emojis passed string
     *
     * @param text String to count emoji characters in.
     * @return returns count of emojis
     */
    public static int countEmojis(String text) {

        String htmlifiedText = htmlify(text);
        // regex to identify html entitities in htmlified text
        Matcher matcher = HTML_ENTITY_PATTERN.matcher(htmlifiedText);

        int counter = 0;
        while (matcher.find()) {
            String emojiCode = matcher.group();
            if (isEmoji(emojiCode)) {
                counter++;
            }
        }
        return counter;
    }

    /**
     * Converts unicode characters in text to corresponding decimal html
     * entities
     *
     * @param text String to htmlify
     * @return htmlified String
     */
    public static String htmlify(String text) {
        String emojifiedStr = emojify(text);
        return htmlHelper(emojifiedStr, false, false);
    }

    public static String htmlify(String text, boolean asSurrogate) {
        String emojifiedStr = emojify(text);
        return htmlHelper(emojifiedStr, false, asSurrogate);
    }

    /**
     * Converts unicode characters in text to corresponding hexadecimal html
     * entities
     *
     * @param text String to hexHtmlify
     * @return hexadecimal htmlified string
     */
    public static String hexHtmlify(String text) {
        String emojifiedStr = emojify(text);
        return htmlHelper(emojifiedStr, true, false);
    }


    /**
     * Converts emojis, hex, decimal htmls, emoticons in a string to short codes
     *
     * @param text String to shortcodify
     * @return shortcodified string
     */
    public static String shortCodify(String text) {
        String emojifiedText = emojify(text);

        // TODO - this approach is ugly, need to find an optimal way to replace
        // the emojis
        // could not find an ideal way to identify emojis in the passed string
        // characters like <3 has multiple characters, but doesn't have
        // surrogate pairs
        // so at this point, we iterate through all the emojis and replace with
        // short codes
        for (Emoji emoji : EmojiManager.data()) {
            StringBuilder shortCodeBuilder = new StringBuilder();
            shortCodeBuilder.append(":").append(emoji.getAliases().get(0)).append(":");

            emojifiedText = emojifiedText.replace(emoji.getEmojiChar(), shortCodeBuilder.toString());
        }
        return emojifiedText;
    }

    /**
     * Removes all emoji characters from the passed string. This method does not remove html characters, shortcodes.
     * To remove all shortcodes, html characters, emojify and then pass the emojified string to this method.
     *
     * @param emojiText String to remove emoji's from.
     * @return emoji stripped string
     */
    public static String removeAllEmojis(String emojiText) {

        for (Emoji emoji : EmojiManager.data()) {
            emojiText = emojiText.replace(emoji.getEmojiChar(), "");
        }
        return emojiText;
    }

}
