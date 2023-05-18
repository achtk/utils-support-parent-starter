package com.chua.common.support.lang.emoji;


import com.chua.common.support.json.Json;
import com.chua.common.support.json.TypeReference;
import com.chua.common.support.utils.IoUtils;

import java.io.InputStream;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Loads emojis from resource bundle
 *
 * @author Krishna Chaitanya Thota
 */
public class EmojiManager {
    private static Pattern emoticonRegexPattern;

    private static final List<Emoji> EMOJI_DATA;
    public static final EmojiTrie EMOJI_TRIE;
    public static final Map<String, Emoji> EMOJIS_BY_ALIAS =
            new HashMap<>();
    private static final Map<String, Set<Emoji>> EMOJIS_BY_TAG =
            new HashMap<>();

    static {
        try {
            try (InputStream stream = IoUtils.newClassPathStream("/emoji.json")) {
                EMOJI_DATA = Json.fromJson(stream, new TypeReference<List<Emoji>>() {
                });
            }

            for (Emoji emoji : EMOJI_DATA) {
                for (String tag : emoji.getTags()) {
                    EMOJIS_BY_TAG.computeIfAbsent(tag, k -> new HashSet<>());
                    EMOJIS_BY_TAG.get(tag).add(emoji);
                }
                for (String alias : emoji.getAliases()) {
                    EMOJIS_BY_ALIAS.put(alias, emoji);
                }
            }

            EMOJI_TRIE = new EmojiTrie(EMOJI_DATA);
            processEmoticonsToRegex();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Returns the complete emoji data
     *
     * @return List of emoji objects
     */
    public static List<Emoji> data() {
        return EMOJI_DATA;
    }

    /**
     * Returns the Regex which can match all emoticons in a string
     *
     * @return regex pattern for emoticons
     */
    public static Pattern getEmoticonRegexPattern() {
        return emoticonRegexPattern;
    }


    /**
     * Processes the Emoji data to emoticon regex
     */
    private static void processEmoticonsToRegex() {

        List<String> emoticons = new ArrayList<>();

        for (Emoji e : EMOJI_DATA) {
            if (e.getEmoticons() != null) {
                emoticons.addAll(e.getEmoticons());
            }
        }

        //List of emotions should be pre-processed to handle instances of subtrings like :-) :-
        //Without this pre-processing, emoticons in a string won't be processed properly
        for (int i = 0; i < emoticons.size(); i++) {
            for (int j = i + 1; j < emoticons.size(); j++) {
                String o1 = emoticons.get(i);
                String o2 = emoticons.get(j);

                if (o2.contains(o1)) {
                    emoticons.remove(j);
                    emoticons.add(i, o2);
                }
            }
        }

        emoticonRegexPattern = getAsciiEmojiRegex(emoticons);
    }

    /**
     * add stop words
     *
     * @param stopWords words
     */
    public static void addStopWords(String... stopWords) {
        if (stopWords == null || stopWords.length == 0) {
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (String stopWord : stopWords) {
            sb.append(stopWord);
            sb.append("|");
        }
        String emojiRegex = emoticonRegexPattern.toString();
        sb.append(emojiRegex);
        emoticonRegexPattern = Pattern.compile(sb.toString());
    }

    public static void clearStopWords() {
        //rebuild the emoji list
        processEmoticonsToRegex();
    }

    /**
     * fet ascii emoji regex
     *
     * @param emojiList emojiList
     * @return Pattern
     */
    private static Pattern getAsciiEmojiRegex(List<String> emojiList) {
        StringBuilder sb = new StringBuilder();
        for (String emoticon : emojiList) {
            if (sb.length() != 0) {
                sb.append("|");
            }
            sb.append(Pattern.quote(emoticon));
        }

        return Pattern.compile(sb.toString());
    }
}
