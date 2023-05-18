package com.chua.example.emoji;


import com.chua.common.support.lang.emoji.EmojiFactory;

/**
 * emoji例子
 *
 * @author CH
 * @since 2021-10-18
 */
public class EmojiExample {

    private static final String UNICODE = "\uD83D\uDC2D\uD83D\uDC2D 安定阿大撒的撒打算打算An \uD83D\uDE00awesome \uD83D\uDE03string \uD83D\uDE04with a few \uD83D\uDE09emojis!\n";
    private static final String UNICODE1 = "A :cat:, :dog: and a :mouse: became friends. For :dog:'s birthday party, they all had :hamburger:s, :fries:s, :cookie:s and :cake:.";
    private static final String UNICODE2 = "a fix for \uD83D\uDE14";

    public static void main(String[] args) {
        testEmoji();
    }

    private static void testEmoji() {

        System.out.println("=================================");
        String emoji = EmojiFactory.of(UNICODE).parseToUnicode();
        System.out.println(emoji);
        String emoji1 = EmojiFactory.of(emoji).parseFromUnicode();
        System.out.println(emoji1);
        String emoji2 = EmojiFactory.of(emoji).parseToHtmlHex();
        System.out.println(emoji2);
        String emoji3 = EmojiFactory.of(emoji).parseToHtmlDecimal();
        System.out.println(emoji3);
    }

}
