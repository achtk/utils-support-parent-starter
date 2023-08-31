package com.chua.common.support.lang.net;


import com.chua.common.support.utils.CollectionUtils;
import com.chua.common.support.utils.Preconditions;
import com.chua.common.support.utils.StringUtils;

import java.nio.charset.Charset;
import java.util.LinkedList;
import java.util.List;

import static com.chua.common.support.constant.CommonConstant.*;

/**
 * URL中Path部分的封装
 *
 * @author looly
 * @since 5.3.1
 */
public class UrlPath {

    private List<String> segments;
    private boolean withEngTag;

    /**
     * 构建UrlPath
     *
     * @param pathStr 初始化的路径字符串
     * @param charset decode用的编码，null表示不做decode
     * @return UrlPath
     */
    public static UrlPath of(CharSequence pathStr, Charset charset) {
        final UrlPath urlPath = new UrlPath();
        urlPath.parse(pathStr, charset);
        return urlPath;
    }

    /**
     * 是否path的末尾加 /
     *
     * @param withEngTag 是否path的末尾加 /
     * @return this
     */
    public UrlPath setWithEndTag(boolean withEngTag) {
        this.withEngTag = withEngTag;
        return this;
    }

    /**
     * 获取path的节点列表
     *
     * @return 节点列表
     */
    public List<String> getSegments() {
        return this.segments;
    }

    /**
     * 获得指定节点
     *
     * @param index 节点位置
     * @return 节点，无节点或者越界返回null
     */
    public String getSegment(int index) {
        if (null == this.segments || index >= this.segments.size()) {
            return null;
        }
        return this.segments.get(index);
    }

    /**
     * 添加到path最后面
     *
     * @param segment Path节点
     * @return this
     */
    public UrlPath add(CharSequence segment) {
        addInternal(fixPath(segment), false);
        return this;
    }

    /**
     * 添加到path最前面
     *
     * @param segment Path节点
     * @return this
     */
    public UrlPath addBefore(CharSequence segment) {
        addInternal(fixPath(segment), true);
        return this;
    }

    /**
     * 解析path
     *
     * @param path    路径，类似于aaa/bb/ccc或/aaa/bbb/ccc
     * @param charset decode编码，null表示不解码
     * @return this
     */
    public UrlPath parse(CharSequence path, Charset charset) {
        if (StringUtils.isNotEmpty(path)) {
            // 原URL中以/结尾，则这个规则需保留，issue#I1G44J@Gitee
            if(StringUtils.endWith(path, SYMBOL_LEFT_SLASH)){
                this.withEngTag = true;
            }

            path = fixPath(path);
            final List<String> split = StringUtils.splitList(path, SYMBOL_LEFT_SLASH_CHAR);
            for (String seg : split) {
                addInternal(UrlDecoder.decodeForPath(seg, charset), false);
            }
        }

        return this;
    }

    /**
     * 构建path，前面带'/'
     *
     * @param charset encode编码，null表示不做encode
     * @return 如果没有任何内容，则返回空字符串""
     */
    public String build(Charset charset) {
        if (CollectionUtils.isEmpty(this.segments)) {
            return EMPTY;
        }

        final StringBuilder builder = new StringBuilder();
        for (String segment : segments) {
            // 根据https://www.ietf.org/rfc/rfc3986.html#section-3.3定义
            // path的第一部分允许有":"，其余部分不允许
            // 在此处的Path部分特指host之后的部分，即不包含第一部分
            builder.append(SYMBOL_LEFT_SLASH).append(Rfc3986.SEGMENT_NZ_NC.encode(segment, charset));
        }
        if (withEngTag || StringUtils.isEmpty(builder)) {
            builder.append(SYMBOL_LEFT_SLASH);
        }
        return builder.toString();
    }

    @Override
    public String toString() {
        return build(null);
    }

    /**
     * 增加节点
     *
     * @param segment 节点
     * @param before  是否在前面添加
     */
    private void addInternal(CharSequence segment, boolean before) {
        if (this.segments == null) {
            this.segments = new LinkedList<>();
        }

        final String seg = StringUtils.str(segment);
        if (before) {
            this.segments.add(0, seg);
        } else {
            this.segments.add(seg);
        }
    }

    /**
     * 修正路径，包括去掉前后的/，去掉空白符
     *
     * @param path 节点或路径path
     * @return 修正后的路径
     */
    private static String fixPath(CharSequence path) {
        Preconditions.checkNotNull(path, "Path segment must be not null!");
        if (SYMBOL_LEFT_SLASH.contentEquals(path)) {
            return EMPTY;
        }

        String segmentStr = StringUtils.trim(path);
        segmentStr = StringUtils.removePrefix(segmentStr, SYMBOL_LEFT_SLASH);
        segmentStr = StringUtils.removeSuffix(segmentStr, SYMBOL_LEFT_SLASH);
        segmentStr = StringUtils.trim(segmentStr);
        return segmentStr;
    }
}
