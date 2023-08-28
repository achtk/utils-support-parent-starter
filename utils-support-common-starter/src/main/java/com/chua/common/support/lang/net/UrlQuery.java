package com.chua.common.support.lang.net;

import com.chua.common.support.collection.ImmutableBuilder;
import com.chua.common.support.collection.TableMap;
import com.chua.common.support.constant.CommonConstant;
import com.chua.common.support.constant.NameConstant;
import com.chua.common.support.converter.Converter;
import com.chua.common.support.crypto.PercentCodec;
import com.chua.common.support.function.Joiner;
import com.chua.common.support.utils.MapUtils;
import com.chua.common.support.utils.StringUtils;
import com.chua.common.support.utils.UrlUtils;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;

import static com.chua.common.support.constant.CommonConstant.*;
import static com.chua.common.support.constant.NameConstant.HTTP;

/**
 * @author CH
 */

public class UrlQuery {

    private TableMap<CharSequence, CharSequence> query;
    /**
     * 是否为x-www-form-urlencoded模式，此模式下空格会编码为'+'
     */
    private final boolean isFormUrlEncoded;
    /**
     * 构造
     */
    public UrlQuery() {
        this(null);
    }

    /**
     * 构造
     *
     * @param isFormUrlEncoded 是否为x-www-form-urlencoded模式，此模式下空格会编码为'+'
     * @since 5.7.16
     */
    public UrlQuery(boolean isFormUrlEncoded) {
        this(null, isFormUrlEncoded);
    }

    /**
     * 构造
     *
     * @param queryMap 初始化的查询键值对
     */
    public UrlQuery(Map<? extends CharSequence, ?> queryMap) {
        this(queryMap, false);
    }
    /**
     * 构造
     *
     * @param queryMap         初始化的查询键值对
     * @param isFormUrlEncoded 是否为x-www-form-urlencoded模式，此模式下空格会编码为'+'
     * @since 5.7.16
     */
    public UrlQuery(Map<? extends CharSequence, ?> queryMap, boolean isFormUrlEncoded) {
        if (MapUtils.isNotEmpty(queryMap)) {
            query = new TableMap<>(queryMap.size());
            addAll(queryMap);
        } else {
            query = new TableMap<>(MapUtils.DEFAULT_INITIAL_CAPACITY);
        }
        this.isFormUrlEncoded = isFormUrlEncoded;
    }
    /**
     * 构建UrlQuery
     *
     * @param queryMap 初始化的查询键值对
     * @return UrlQuery
     */
    public static UrlQuery of(Map<? extends CharSequence, ?> queryMap) {
        return new UrlQuery(queryMap);
    }

    /**
     * 构建UrlQuery
     *
     * @param queryStr 初始化的查询字符串
     * @param charset  decode用的编码，null表示不做decode
     * @return UrlQuery
     */
    public static UrlQuery of(String queryStr, Charset charset) {
        return of(queryStr, charset, true);
    }

    /**
     * 构建UrlQuery
     *
     * @param queryMap         初始化的查询键值对
     * @param isFormUrlEncoded 是否为x-www-form-urlencoded模式，此模式下空格会编码为'+'
     * @return UrlQuery
     */
    public static UrlQuery of(Map<? extends CharSequence, ?> queryMap, boolean isFormUrlEncoded) {
        return new UrlQuery(queryMap, isFormUrlEncoded);
    }
    /**
     * 构建UrlQuery
     *
     * @param queryStr       初始化的查询字符串
     * @param charset        decode用的编码，null表示不做decode
     * @param autoRemovePath 是否自动去除path部分，{@code true}则自动去除第一个?前的内容
     * @return UrlQuery
     * @since 5.5.8
     */
    public static UrlQuery of(String queryStr, Charset charset, boolean autoRemovePath) {
        final UrlQuery urlQuery = new UrlQuery();
        urlQuery.parse(queryStr, charset, autoRemovePath);
        return urlQuery;
    }


    /**
     * 增加键值对
     *
     * @param key   键
     * @param value 值，集合和数组转换为逗号分隔形式
     * @return this
     */
    public UrlQuery add(CharSequence key, Object value) {
        this.query.put(key, toStr(value));
        return this;
    }

    /**
     * 批量增加键值对
     *
     * @param queryMap query中的键值对
     * @return this
     */
    public UrlQuery addAll(Map<? extends CharSequence, ?> queryMap) {
        if (MapUtils.isNotEmpty(queryMap)) {
            queryMap.forEach(this::add);
        }
        return this;
    }
    /**
     * 解析URL中的查询字符串
     *
     * @param queryStr 查询字符串，类似于key1=v1&amp;key2=&amp;key3=v3
     * @param charset  decode编码，null表示不做decode
     * @return this
     */
    public UrlQuery parse(String queryStr, Charset charset) {
        return parse(queryStr, charset, true);
    }

    /**
     * 解析URL中的查询字符串
     *
     * @param queryStr       查询字符串，类似于key1=v1&amp;key2=&amp;key3=v3
     * @param charset        decode编码，null表示不做decode
     * @param autoRemovePath 是否自动去除path部分，{@code true}则自动去除第一个?前的内容
     * @return this
     * @since 5.5.8
     */
    public UrlQuery parse(String queryStr, Charset charset, boolean autoRemovePath) {
        if (StringUtils.isBlank(queryStr)) {
            return this;
        }

        if (autoRemovePath) {
            // 去掉Path部分
            int pathEndPos = queryStr.indexOf('?');
            if (pathEndPos > -1) {
                queryStr = StringUtils.subSuf(queryStr, pathEndPos + 1);
                if (StringUtils.isBlank(queryStr)) {
                    return this;
                }
            }
        }

        final int len = queryStr.length();
        String name = null;
        int pos = 0; // 未处理字符开始位置
        int i; // 未处理字符结束位置
        char c; // 当前字符
        for (i = 0; i < len; i++) {
            c = queryStr.charAt(i);
            switch (c) {
                case SYMBOL_EQUALS_CHAR://键和值的分界符
                    if (null == name) {
                        // name可以是""
                        name = queryStr.substring(pos, i);
                        // 开始位置从分节符后开始
                        pos = i + 1;
                    }
                    // 当=不作为分界符时，按照普通字符对待
                    break;
                case SYMBOL_AND_CHAR://键值对之间的分界符
                    addParam(name, queryStr.substring(pos, i), charset);
                    name = null;
                    if (i + 4 < len && "amp;".equals(queryStr.substring(i + 1, i + 5))) {
                        // issue#850@Github，"&amp;"转义为"&"
                        i += 4;
                    }
                    // 开始位置从分节符后开始
                    pos = i + 1;
                    break;
                default:
                    break;
            }
        }

        if (i - pos == len) {
            // 没有任何参数符号
            if (queryStr.startsWith(HTTP) || queryStr.contains(SYMBOL_LEFT_SLASH)) {
                // 可能为url路径，忽略之
                return this;
            }
        }

        // 处理结尾
        addParam(name, queryStr.substring(pos, i), charset);

        return this;
    }

    /**
     * 获得查询的Map
     *
     * @return 查询的Map，只读
     */
    public Map<CharSequence, CharSequence> getQueryMap() {
        return ImmutableBuilder.<CharSequence, CharSequence>builderOfMap().put(this.query).asUnmodifiableMap();
    }

    /**
     * 获取查询值
     *
     * @param key 键
     * @return 值
     */
    public CharSequence get(CharSequence key) {
        if (MapUtils.isEmpty(this.query)) {
            return null;
        }
        return this.query.get(key);
    }

    /**
     * 构建URL查询字符串，即将key-value键值对转换为key1=v1&amp;key2=&amp;key3=v3形式
     *
     * @param charset encode编码，null表示不做encode编码
     * @return URL查询字符串
     */
    public String build(Charset charset) {
        return build(charset, true);
    }

    /**
     * 构建URL查询字符串，即将key-value键值对转换为key1=v1&amp;key2=&amp;key3=v3形式
     *
     * @param charset  encode编码，null表示不做encode编码
     * @param isEncode 是否转义键和值
     * @return URL查询字符串
     * @since 5.7.13
     */
    public String build(Charset charset, boolean isEncode) {
        if (MapUtils.isEmpty(this.query)) {
            return EMPTY;
        }

        final StringBuilder sb = new StringBuilder();
        boolean isFirst = true;
        CharSequence key;
        CharSequence value;
        for (Map.Entry<CharSequence, CharSequence> entry : this.query) {
            if (isFirst) {
                isFirst = false;
            } else {
                sb.append("&");
            }
            key = entry.getKey();
            if (null != key) {
                sb.append(toStr(key, charset, isEncode));
                value = entry.getValue();
                if (null != value) {
                    sb.append("=").append(toStr(value, charset, isEncode));
                }
            }
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return build(null);
    }

    /**
     * 对象转换为字符串，用于URL的Query中
     *
     * @param value 值
     * @return 字符串
     */
    private static String toStr(Object value) {
        String result;
        if (value instanceof Iterable) {
            result = Joiner.on(",").join((Iterable<?>) value);
        } else if (value instanceof Iterator) {
            result = Joiner.on(",").join((Iterator<?>) value);
        } else {
            result = Converter.convertIfNecessary(value, String.class);
        }
        return result;
    }

    /**
     * 将键值对加入到值为List类型的Map中,，情况如下：
     * <pre>
     *     1、key和value都不为null，类似于 "a=1"或者"=1"，直接put
     *     2、key不为null，value为null，类似于 "a="，值传""
     *     3、key为null，value不为null，类似于 "1"
     *     4、key和value都为null，忽略之，比如&&
     * </pre>
     *
     * @param key     key，为null则value作为key
     * @param value   value，为null且key不为null时传入""
     * @param charset 编码
     */
    private void addParam(String key, String value, Charset charset) {
        if (null != key) {
            final String actualKey = UrlUtils.decode(key, charset);
            this.query.put(actualKey, StringUtils.nullToEmpty(UrlUtils.decode(value, charset)));
        } else if (null != value) {
            // name为空，value作为name，value赋值null
            this.query.put(UrlUtils.decode(value, charset), null);
        }
    }

    /**
     * 键值对的{@link CharSequence}转换为String，可选是否转义
     *
     * @param str      原字符串
     * @param charset  编码，只用于encode中
     * @param isEncode 是否转义
     * @return 转换后的String
     * @since 5.7.13
     */
    private static String toStr(CharSequence str, Charset charset, boolean isEncode) {
        String result = StringUtils.str(str);
        if (isEncode) {
            try {
                result = UrlUtils.encodeAll(result, charset);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }
}
