package com.chua.common.support.utils;

import com.chua.common.support.constant.CommonConstant;
import com.chua.common.support.constant.RegexConstant;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * regex
 *
 * @author CH
 */
public class RegexUtils {

    /**
     * 正则表达式匹配中文汉字
     */
    public final static String RE_CHINESE = CommonConstant.CHINESE_V2;
    /**
     * 正则表达式匹配中文字符串
     */
    public final static String RE_CHINESES = CommonConstant.CHINESES;

    /**
     * 正则中需要被转义的关键字
     */
    public final static Set<Character> RE_KEYS = new HashSet<>(Arrays.asList('$', '(', ')', '*', '+', '.', '[', ']', '?', '\\', '^', '{', '}', '|'));

    /**
     * 获得匹配的字符串，获得正则中分组0的内容
     *
     * @param regex   匹配的正则
     * @param content 被匹配的内容
     * @return 匹配后得到的字符串，未匹配返回null
     * @since 3.1.2
     */
    public static String getGroup0(String regex, CharSequence content) {
        return get(regex, content, 0);
    }

    /**
     * 获得匹配的字符串，获得正则中分组1的内容
     *
     * @param regex   匹配的正则
     * @param content 被匹配的内容
     * @return 匹配后得到的字符串，未匹配返回null
     * @since 3.1.2
     */
    public static String getGroup1(String regex, CharSequence content) {
        return get(regex, content, 1);
    }

    /**
     * 获得匹配的字符串
     *
     * @param regex      匹配的正则
     * @param content    被匹配的内容
     * @param groupIndex 匹配正则的分组序号
     * @return 匹配后得到的字符串，未匹配返回null
     */
    public static String get(String regex, CharSequence content, int groupIndex) {
        if (null == content || null == regex) {
            return null;
        }

        final Pattern pattern = RegexConstant.get(regex, Pattern.DOTALL);
        return get(pattern, content, groupIndex);
    }

    /**
     * 获得匹配的字符串
     *
     * @param regex     匹配的正则
     * @param content   被匹配的内容
     * @param groupName 匹配正则的分组名称
     * @return 匹配后得到的字符串，未匹配返回null
     */
    public static String get(String regex, CharSequence content, String groupName) {
        if (null == content || null == regex) {
            return null;
        }

        final Pattern pattern = RegexConstant.get(regex, Pattern.DOTALL);
        return get(pattern, content, groupName);
    }

    /**
     * 获得匹配的字符串，，获得正则中分组0的内容
     *
     * @param pattern 编译后的正则模式
     * @param content 被匹配的内容
     * @return 匹配后得到的字符串，未匹配返回null
     * @since 3.1.2
     */
    public static String getGroup0(Pattern pattern, CharSequence content) {
        return get(pattern, content, 0);
    }

    /**
     * 获得匹配的字符串，获得正则中分组1的内容
     *
     * @param pattern 编译后的正则模式
     * @param content 被匹配的内容
     * @return 匹配后得到的字符串，未匹配返回null
     * @since 3.1.2
     */
    public static String getGroup1(Pattern pattern, CharSequence content) {
        return get(pattern, content, 1);
    }

    /**
     * 获得匹配的字符串，对应分组0表示整个匹配内容，1表示第一个括号分组内容，依次类推
     *
     * @param pattern    编译后的正则模式
     * @param content    被匹配的内容
     * @param groupIndex 匹配正则的分组序号，0表示整个匹配内容，1表示第一个括号分组内容，依次类推
     * @return 匹配后得到的字符串，未匹配返回null
     */
    public static String get(Pattern pattern, CharSequence content, int groupIndex) {
        if (null == content || null == pattern) {
            return null;
        }

        return get(pattern, content, matcher -> {
            return matcher.group(groupIndex);
        });
    }

    /**
     * 获得匹配的字符串
     *
     * @param pattern   匹配的正则
     * @param content   被匹配的内容
     * @param groupName 匹配正则的分组名称
     * @return 匹配后得到的字符串，未匹配返回null
     * @since 5.7.15
     */
    public static String get(Pattern pattern, CharSequence content, String groupName) {
        if (null == content || null == pattern || null == groupName) {
            return null;
        }

        return get(pattern, content, matcher -> {
            return matcher.group(groupName);
        });
    }

    /**
     * 在给定字符串中查找给定规则的字符，如果找到则使用{@link Consumer}处理之<br>
     * 如果内容中有多个匹配项，则只处理找到的第一个结果。
     *
     * @param pattern  匹配的正则
     * @param content  被匹配的内容
     * @param function 匹配到的内容处理器
     * @return
     * @since 5.7.15
     */
    public static String get(Pattern pattern, CharSequence content, Function<Matcher, String> function) {
        if (null == content || null == pattern || null == function) {
            return null;
        }

        final Matcher m = pattern.matcher(content);
        if (m.find()) {
            return function.apply(m);
        }
        return null;
    }

    /**
     * 在给定字符串中查找给定规则的字符，如果找到则使用{@link Consumer}处理之<br>
     * 如果内容中有多个匹配项，则只处理找到的第一个结果。
     *
     * @param pattern  匹配的正则
     * @param content  被匹配的内容
     * @param consumer 匹配到的内容处理器
     * @since 5.7.15
     */
    public static void get(Pattern pattern, CharSequence content, Consumer<Matcher> consumer) {
        if (null == content || null == pattern || null == consumer) {
            return;
        }

        final Matcher m = pattern.matcher(content);
        if (m.find()) {
            consumer.accept(m);
        }
    }

    /**
     * 获得匹配的字符串匹配到的所有分组
     *
     * @param pattern 编译后的正则模式
     * @param content 被匹配的内容
     * @return 匹配后得到的字符串数组，按照分组顺序依次列出，未匹配到返回空列表，任何一个参数为null返回null
     * @since 3.1.0
     */
    public static List<String> getAllGroups(Pattern pattern, CharSequence content) {
        return getAllGroups(pattern, content, true);
    }

    /**
     * 获得匹配的字符串匹配到的所有分组
     *
     * @param pattern    编译后的正则模式
     * @param content    被匹配的内容
     * @param withGroup0 是否包括分组0，此分组表示全匹配的信息
     * @return 匹配后得到的字符串数组，按照分组顺序依次列出，未匹配到返回空列表，任何一个参数为null返回null
     * @since 4.0.13
     */
    public static List<String> getAllGroups(Pattern pattern, CharSequence content, boolean withGroup0) {
        if (null == content || null == pattern) {
            return null;
        }

        ArrayList<String> result = new ArrayList<>();
        final Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            final int startGroup = withGroup0 ? 0 : 1;
            final int groupCount = matcher.groupCount();
            for (int i = startGroup; i <= groupCount; i++) {
                result.add(matcher.group(i));
            }
        }
        return result;
    }

    /**
     * 取得内容中匹配的所有结果
     *
     * @param pattern 编译后的正则模式
     * @param content 被查找的内容
     * @param group   正则的分组
     * @return 结果列表
     * @since 3.0.6
     */
    public static List<String> findAll(Pattern pattern, CharSequence content, int group) {
        return findAll(pattern, content, group, new ArrayList<>());
    }

    /**
     * 取得内容中匹配的所有结果
     *
     * @param <T>        集合类型
     * @param pattern    编译后的正则模式
     * @param content    被查找的内容
     * @param group      正则的分组
     * @param collection 返回的集合类型
     * @return 结果集
     */
    public static <T extends Collection<String>> T findAll(Pattern pattern, CharSequence content, int group, T collection) {
        if (null == pattern || null == content) {
            return null;
        }

        findAll(pattern, content, (matcher) -> collection.add(matcher.group(group)));
        return collection;
    }

    /**
     * 取得内容中匹配的所有结果，使用{@link Consumer}完成匹配结果处理
     *
     * @param pattern  编译后的正则模式
     * @param content  被查找的内容
     * @param consumer 匹配结果处理函数
     * @since 5.7.15
     */
    public static void findAll(Pattern pattern, CharSequence content, Consumer<Matcher> consumer) {
        if (null == pattern || null == content) {
            return;
        }

        final Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            consumer.accept(matcher);
        }
    }

    /**
     * 计算指定字符串中，匹配pattern的个数
     *
     * @param regex   正则表达式
     * @param content 被查找的内容
     * @return 匹配个数
     */
    public static int count(String regex, CharSequence content) {
        if (null == regex || null == content) {
            return 0;
        }

        final Pattern pattern = RegexConstant.get(regex, Pattern.DOTALL);
        return count(pattern, content);
    }

    /**
     * 计算指定字符串中，匹配pattern的个数
     *
     * @param pattern 编译后的正则模式
     * @param content 被查找的内容
     * @return 匹配个数
     */
    public static int count(Pattern pattern, CharSequence content) {
        if (null == pattern || null == content) {
            return 0;
        }

        int count = 0;
        final Matcher matcher = pattern.matcher(content);
        while (matcher.find()) {
            count++;
        }

        return count;
    }

    /**
     * 指定内容中是否有表达式匹配的内容
     *
     * @param regex   正则表达式
     * @param content 被查找的内容
     * @return 指定内容中是否有表达式匹配的内容
     * @since 3.3.1
     */
    public static boolean contains(String regex, CharSequence content) {
        if (null == regex || null == content) {
            return false;
        }

        final Pattern pattern = RegexConstant.get(regex, Pattern.DOTALL);
        return contains(pattern, content);
    }

    /**
     * 指定内容中是否有表达式匹配的内容
     *
     * @param pattern 编译后的正则模式
     * @param content 被查找的内容
     * @return 指定内容中是否有表达式匹配的内容
     * @since 3.3.1
     */
    public static boolean contains(Pattern pattern, CharSequence content) {
        if (null == pattern || null == content) {
            return false;
        }
        return pattern.matcher(content).find();
    }

    /**
     * 找到指定正则匹配到字符串的开始位置
     *
     * @param regex   正则
     * @param content 字符串
     * @return 位置，{@code null}表示未找到
     * @since 5.6.5
     */
    public static MatchResult indexOf(String regex, CharSequence content) {
        if (null == regex || null == content) {
            return null;
        }

        final Pattern pattern = RegexConstant.get(regex, Pattern.DOTALL);
        return indexOf(pattern, content);
    }

    /**
     * 找到指定模式匹配到字符串的开始位置
     *
     * @param pattern 模式
     * @param content 字符串
     * @return 位置，{@code null}表示未找到
     * @since 5.6.5
     */
    public static MatchResult indexOf(Pattern pattern, CharSequence content) {
        if (null != pattern && null != content) {
            final Matcher matcher = pattern.matcher(content);
            if (matcher.find()) {
                return matcher.toMatchResult();
            }
        }

        return null;
    }

    /**
     * 找到指定正则匹配到第一个字符串的位置
     *
     * @param regex   正则
     * @param content 字符串
     * @return 位置，{@code null}表示未找到
     * @since 5.6.5
     */
    public static MatchResult lastIndexOf(String regex, CharSequence content) {
        if (null == regex || null == content) {
            return null;
        }

        final Pattern pattern = RegexConstant.get(regex, Pattern.DOTALL);
        return lastIndexOf(pattern, content);
    }

    /**
     * 找到指定模式匹配到最后一个字符串的位置
     *
     * @param pattern 模式
     * @param content 字符串
     * @return 位置，{@code null}表示未找到
     * @since 5.6.5
     */
    public static MatchResult lastIndexOf(Pattern pattern, CharSequence content) {
        MatchResult result = null;
        if (null != pattern && null != content) {
            final Matcher matcher = pattern.matcher(content);
            while (matcher.find()) {
                result = matcher.toMatchResult();
            }
        }

        return result;
    }

    /**
     * 给定内容是否匹配正则
     *
     * @param regex   正则
     * @param content 内容
     * @return 正则为null或者""则不检查，返回true，内容为null返回false
     */
    public static boolean isMatch(String regex, CharSequence content) {
        if (content == null) {
            // 提供null的字符串为不匹配
            return false;
        }

        if (StringUtils.isEmpty(regex)) {
            // 正则不存在则为全匹配
            return true;
        }

        final Pattern pattern = RegexConstant.get(regex, Pattern.DOTALL);
        return isMatch(pattern, content);
    }

    /**
     * 给定内容是否匹配正则
     *
     * @param pattern 模式
     * @param content 内容
     * @return 正则为null或者""则不检查，返回true，内容为null返回false
     */
    public static boolean isMatch(Pattern pattern, CharSequence content) {
        if (content == null || pattern == null) {
            // 提供null的字符串为不匹配
            return false;
        }
        return pattern.matcher(content).matches();
    }

    /**
     * 正则替换指定值<br>
     * 通过正则查找到字符串，然后把匹配到的字符串加入到replacementTemplate中，$1表示分组1的字符串
     *
     * <p>
     * 例如：原字符串是：中文1234，我想把1234换成(1234)，则可以：
     *
     * <pre>
     * ReUtil.replaceAll("中文1234", "(\\d+)", "($1)"))
     *
     * 结果：中文(1234)
     * </pre>
     *
     * @param content             文本
     * @param regex               正则
     * @param replacementTemplate 替换的文本模板，可以使用$1类似的变量提取正则匹配出的内容
     * @return 处理后的文本
     */
    public static String replaceAll(CharSequence content, String regex, String replacementTemplate) {
        final Pattern pattern = Pattern.compile(regex, Pattern.DOTALL);
        return replaceAll(content, pattern, replacementTemplate);
    }

    /**
     * 正则替换指定值<br>
     * 通过正则查找到字符串，然后把匹配到的字符串加入到replacementTemplate中，$1表示分组1的字符串
     *
     * @param content             文本
     * @param pattern             {@link Pattern}
     * @param replacementTemplate 替换的文本模板，可以使用$1类似的变量提取正则匹配出的内容
     * @return 处理后的文本
     * @since 3.0.4
     */
    public static String replaceAll(CharSequence content, Pattern pattern, String replacementTemplate) {
        if (StringUtils.isEmpty(content)) {
            return StringUtils.str(content);
        }

        final Matcher matcher = pattern.matcher(content);
        boolean result = matcher.find();
        if (result) {
            final Set<String> varNums = findAll(RegexConstant.GROUP_VAR, replacementTemplate, 1, new HashSet<>());
            final StringBuffer sb = new StringBuffer();
            do {
                String replacement = replacementTemplate;
                for (String var : varNums) {
                    int group = Integer.parseInt(var);
                    replacement = replacement.replace("$" + var, matcher.group(group));
                }
                matcher.appendReplacement(sb, escape(replacement));
                result = matcher.find();
            } while (result);
            matcher.appendTail(sb);
            return sb.toString();
        }
        return StringUtils.str(content);
    }

    /**
     * 替换所有正则匹配的文本，并使用自定义函数决定如何替换<br>
     * replaceFun可以通过{@link Matcher}提取出匹配到的内容的不同部分，然后经过重新处理、组装变成新的内容放回原位。
     *
     * <pre class="code">
     *     replaceAll(this.content, "(\\d+)", parameters -&gt; "-" + parameters.group(1) + "-")
     *     // 结果为："ZZZaaabbbccc中文-1234-"
     * </pre>
     *
     * @param str        要替换的字符串
     * @param regex      用于匹配的正则式
     * @param replaceFun 决定如何替换的函数
     * @return 替换后的文本
     * @since 4.2.2
     */
    public static String replaceAll(CharSequence str, String regex, Function<Matcher, String> replaceFun) {
        return replaceAll(str, Pattern.compile(regex), replaceFun);
    }

    /**
     * 替换所有正则匹配的文本，并使用自定义函数决定如何替换<br>
     * replaceFun可以通过{@link Matcher}提取出匹配到的内容的不同部分，然后经过重新处理、组装变成新的内容放回原位。
     *
     * <pre class="code">
     *     replaceAll(this.content, "(\\d+)", parameters -&gt; "-" + parameters.group(1) + "-")
     *     // 结果为："ZZZaaabbbccc中文-1234-"
     * </pre>
     *
     * @param str        要替换的字符串
     * @param pattern    用于匹配的正则式
     * @param replaceFun 决定如何替换的函数,可能被多次调用（当有多个匹配时）
     * @return 替换后的字符串
     * @since 4.2.2
     */
    public static String replaceAll(CharSequence str, Pattern pattern, Function<Matcher, String> replaceFun) {
        if (StringUtils.isEmpty(str)) {
            return StringUtils.str(str);
        }

        final Matcher matcher = pattern.matcher(str);
        final StringBuffer buffer = new StringBuffer();
        while (matcher.find()) {
            try {
                matcher.appendReplacement(buffer, replaceFun.apply(matcher));
            } catch (Exception e) {
                throw new IllegalArgumentException(e);
            }
        }
        matcher.appendTail(buffer);
        return buffer.toString();
    }

    /**
     * 转义字符，将正则的关键字转义
     *
     * @param c 字符
     * @return 转义后的文本
     */
    public static String escape(char c) {
        final StringBuilder builder = new StringBuilder();
        if (RE_KEYS.contains(c)) {
            builder.append('\\');
        }
        builder.append(c);
        return builder.toString();
    }

    /**
     * 转义字符串，将正则的关键字转义
     *
     * @param content 文本
     * @return 转义后的文本
     */
    public static String escape(CharSequence content) {
        if (StringUtils.isEmpty(content)) {
            return StringUtils.str(content);
        }

        final StringBuilder builder = new StringBuilder();
        int len = content.length();
        char current;
        for (int i = 0; i < len; i++) {
            current = content.charAt(i);
            if (RE_KEYS.contains(current)) {
                builder.append('\\');
            }
            builder.append(current);
        }
        return builder.toString();
    }

    /**
     * 是否是base64
     *
     * @param str str
     * @return 是否是base64
     */
    public static boolean isBase64(String str) {
        if (str == null || str.trim().length() == 0) {
            return false;
        } else {
        }
        if (str.length() % 4 != 0) {
            return false;
        }

        char[] strChars = str.toCharArray();
        for (char c : strChars) {
            if ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9')
                    || c == '+' || c == '/' || c == '=') {
                continue;
            } else {
                return false;
            }
        }
        return true;
    }
}