package com.chua.common.support.lang.emoji;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;
import java.util.regex.Matcher;

/**
 * Emoji
 *
 * @author Krishna Chaitanya Thota
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class Emoji extends AbstractEmoji {

    private String emojiChar;

    private String description;

    private String emoji;

    private List<String> aliases;
    private List<String> tags;

    private String hexHtml;

    private String decimalHtml;

    private String decimalHtmlShort;

    private String hexHtmlShort;

    private String decimalSurrogateHtml;

    private List<String> emoticons;

    public void setEmojiChar(String emojiChar) {
        setDecimalHtml(EmojiUtils.htmlHelper(emojiChar, false, false));
        setHexHtml(EmojiUtils.htmlHelper(emojiChar, true, false));

        setDecimalSurrogateHtml(EmojiUtils.htmlHelper(emojiChar, false, true));
        this.emojiChar = emojiChar;
    }

    public void setHexHtml(String hexHtml) {
        this.hexHtml = hexHtml;
        Matcher matcher = HTML_SURROGATE_ENTITY_PATTERN.matcher(hexHtml);
        if (matcher.find()) {
            String group = matcher.group("H");
            this.setHexHtmlShort(group);
        } else {
            this.setHexHtmlShort(hexHtml);
        }
    }

    public void setDecimalHtml(String decimalHtml) {

        this.decimalHtml = decimalHtml;
        Matcher matcher = HTML_SURROGATE_ENTITY_PATTERN.matcher(decimalHtml);
        if (matcher.find()) {
            String group = matcher.group("H");
            this.setDecimalHtmlShort(group);
        } else {
            this.setDecimalHtmlShort(decimalHtml);
        }
    }


    public String getUnicode() {
        return this.emoji;
    }

}
