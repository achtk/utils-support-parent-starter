package com.chua.common.support.lang.net;

import com.chua.common.support.utils.Hex;
import com.chua.common.support.utils.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.BitSet;

import static com.chua.common.support.constant.CommonConstant.*;

/**
 * UrlEncoder
 * @author CH
 */
public class UrlEncoder {

    // --------------------------------------------------------------------------------------------- Static method start
    /**
     * 默认URLEncoder<br>
     * 默认的编码器针对URI路径编码，定义如下：
     *
     * <pre>
     * default = pchar / "/"
     * pchar = unreserved（不处理） / pct-encoded / sub-delims（子分隔符） / ":" / "@"
     * unreserved = ALPHA / DIGIT / "-" / "." / "_" / "~"
     * sub-delims = "!" / "$" / "&amp;" / "'" / "(" / ")" / "*" / "+" / "," / ";" / "="
     * </pre>
     */
    public static final UrlEncoder DEFAULT = createDefault();

    /**
     * URL的Path的每一个Segment UrlEncoder<br>
     * 默认的编码器针对URI路径编码，定义如下：
     *
     * <pre>
     * pchar = unreserved / pct-encoded / sub-delims / ":"（非空segment不包含:） / "@"
     * unreserved = ALPHA / DIGIT / "-" / "." / "_" / "~"
     * sub-delims = "!" / "$" / "&amp;" / "'" / "(" / ")" / "*" / "+" / "," / ";" / "="
     * </pre>
     *
     * 定义见：https://www.rfc-editor.org/rfc/rfc3986.html#section-3.3
     */
    public static final UrlEncoder PATH_SEGMENT = createPathSegment();

    /**
     * URL的Fragment UrlEncoder<br>
     * 默认的编码器针对Fragment，定义如下：
     *
     * <pre>
     * fragment    = *( pchar / "/" / "?" )
     * pchar       = unreserved / pct-encoded / sub-delims / ":" / "@"
     * unreserved  = ALPHA / DIGIT / "-" / "." / "_" / "~"
     * sub-delims  = "!" / "$" / "&amp;" / "'" / "(" / ")" / "*" / "+" / "," / ";" / "="
     * </pre>
     *
     * 具体见：https://datatracker.ietf.org/doc/html/rfc3986#section-3.5
     * @since 5.7.13
     */
    public static final UrlEncoder FRAGMENT = createFragment();

    /**
     * 用于查询语句的URLEncoder<br>
     * 编码器针对URI路径编码，定义如下：
     *
     * <pre>
     * 0x20 ' ' =》 '+'
     * 0x2A, 0x2D, 0x2E, 0x30 to 0x39, 0x41 to 0x5A, 0x5F, 0x61 to 0x7A as-is
     * '*', '-', '.', '0' to '9', 'A' to 'Z', '_', 'a' to 'z' Also '=' and '&amp;' 不编码
     * 其它编码为 %nn 形式
     * </pre>
     * <p>
     * 详细见：https://www.w3.org/TR/html5/forms.html#application/x-www-form-urlencoded-encoding-algorithm
     */
    public static final UrlEncoder QUERY = createQuery();

    /**
     * 全编码的URLEncoder<br>
     * <pre>
     *  0x2A, 0x2D, 0x2E, 0x30 to 0x39, 0x41 to 0x5A, 0x5F, 0x61 to 0x7A as-is
     *  '*', '-', '.', '0' to '9', 'A' to 'Z', '_', 'a' to 'z' 不编码
     *  其它编码为 %nn 形式
     * </pre>
     */
    public static final UrlEncoder ALL = createAll();

    /**
     * 创建默认URLEncoder<br>
     * 默认的编码器针对URI路径编码，定义如下：
     *
     * <pre>
     * default = pchar / "/"
     * pchar = unreserved（不处理） / pct-encoded / sub-delims（子分隔符） / ":" / "@"
     * unreserved = ALPHA / DIGIT / "-" / "." / "_" / "~"
     * sub-delims = "!" / "$" / "&amp;" / "'" / "(" / ")" / "*" / "+" / "," / ";" / "="
     * </pre>
     *
     * @return UrlEncoder
     */
    public static UrlEncoder createDefault() {
        final UrlEncoder encoder = new UrlEncoder();
        encoder.addSafeCharacter('-');
        encoder.addSafeCharacter('.');
        encoder.addSafeCharacter('_');
        encoder.addSafeCharacter('~');

        // Add the sub-delims
        addSubDelims(encoder);

        // Add the remaining literals
        encoder.addSafeCharacter(':');
        encoder.addSafeCharacter('@');

        // Add '/' so it isn't encoded when we encode a path
        encoder.addSafeCharacter('/');

        return encoder;
    }

    /**
     * URL的Path的每一个Segment UrlEncoder<br>
     * 默认的编码器针对URI路径的每一段编码，定义如下：
     *
     * <pre>
     * pchar = unreserved / pct-encoded / sub-delims / ":"（非空segment不包含:） / "@"
     * unreserved = ALPHA / DIGIT / "-" / "." / "_" / "~"
     * sub-delims = "!" / "$" / "&amp;" / "'" / "(" / ")" / "*" / "+" / "," / ";" / "="
     * </pre>
     *
     * 定义见：https://www.rfc-editor.org/rfc/rfc3986.html#section-3.3
     *
     * @return UrlEncoder
     */
    public static UrlEncoder createPathSegment() {
        final UrlEncoder encoder = new UrlEncoder();

        // unreserved
        encoder.addSafeCharacter('-');
        encoder.addSafeCharacter('.');
        encoder.addSafeCharacter('_');
        encoder.addSafeCharacter('~');

        // Add the sub-delims
        addSubDelims(encoder);

        // Add the remaining literals
        //non-zero-length segment without any colon ":"
        encoder.addSafeCharacter('@');

        return encoder;
    }

    /**
     * URL的Fragment UrlEncoder<br>
     * 默认的编码器针对Fragment，定义如下：
     *
     * <pre>
     * fragment    = *( pchar / "/" / "?" )
     * pchar       = unreserved / pct-encoded / sub-delims / ":" / "@"
     * unreserved  = ALPHA / DIGIT / "-" / "." / "_" / "~"
     * sub-delims  = "!" / "$" / "&amp;" / "'" / "(" / ")" / "*" / "+" / "," / ";" / "="
     * </pre>
     *
     * 具体见：https://datatracker.ietf.org/doc/html/rfc3986#section-3.5
     *
     * @return UrlEncoder
     * @since 5.7.13
     */
    public static UrlEncoder createFragment() {
        final UrlEncoder encoder = new UrlEncoder();
        encoder.addSafeCharacter('-');
        encoder.addSafeCharacter('.');
        encoder.addSafeCharacter('_');
        encoder.addSafeCharacter('~');

        // Add the sub-delims
        addSubDelims(encoder);

        // Add the remaining literals
        encoder.addSafeCharacter(':');
        encoder.addSafeCharacter('@');

        encoder.addSafeCharacter('/');
        encoder.addSafeCharacter('?');

        return encoder;
    }

    /**
     * 创建用于查询语句的URLEncoder<br>
     * 编码器针对URI路径编码，定义如下：
     *
     * <pre>
     * 0x20 ' ' =》 '+'
     * 0x2A, 0x2D, 0x2E, 0x30 to 0x39, 0x41 to 0x5A, 0x5F, 0x61 to 0x7A as-is
     * '*', '-', '.', '0' to '9', 'A' to 'Z', '_', 'a' to 'z' Also '=' and '&amp;' 不编码
     * 其它编码为 %nn 形式
     * </pre>
     * <p>
     * 详细见：https://www.w3.org/TR/html5/forms.html#application/x-www-form-urlencoded-encoding-algorithm
     *
     * @return UrlEncoder
     */
    public static UrlEncoder createQuery() {
        final UrlEncoder encoder = new UrlEncoder();
        // Special encoding for space
        encoder.setEncodeSpaceAsPlus(true);
        // Alpha and digit are safe by default
        // Add the other permitted characters
        encoder.addSafeCharacter('*');
        encoder.addSafeCharacter('-');
        encoder.addSafeCharacter('.');
        encoder.addSafeCharacter('_');

        encoder.addSafeCharacter('=');
        encoder.addSafeCharacter('&');

        return encoder;
    }

    /**
     * 创建URLEncoder<br>
     * 编码器针对URI路径编码，定义如下：
     *
     * <pre>
     * 0x2A, 0x2D, 0x2E, 0x30 to 0x39, 0x41 to 0x5A, 0x5F, 0x61 to 0x7A as-is
     * '*', '-', '.', '0' to '9', 'A' to 'Z', '_', 'a' to 'z' 不编码
     * 其它编码为 %nn 形式
     * </pre>
     * <p>
     * 详细见：https://www.w3.org/TR/html5/forms.html#application/x-www-form-urlencoded-encoding-algorithm
     *
     * @return UrlEncoder
     */
    public static UrlEncoder createAll() {
        final UrlEncoder encoder = new UrlEncoder();
        encoder.addSafeCharacter('*');
        encoder.addSafeCharacter('-');
        encoder.addSafeCharacter('.');
        encoder.addSafeCharacter('_');

        return encoder;
    }
    // --------------------------------------------------------------------------------------------- Static method end

    /**
     * 存放安全编码
     */
    private final BitSet safeCharacters;
    /**
     * 是否编码空格为+
     */
    private boolean encodeSpaceAsPlus = false;

    /**
     * 构造<br>
     * [a-zA-Z0-9]默认不被编码
     */
    public UrlEncoder() {
        this(new BitSet(256));

        // unreserved
        addAlpha();
        addDigit();
    }

    /**
     * 构造
     *
     * @param safeCharacters 安全字符，安全字符不被编码
     */
    private UrlEncoder(BitSet safeCharacters) {
        this.safeCharacters = safeCharacters;
    }

    /**
     * 增加安全字符<br>
     * 安全字符不被编码
     *
     * @param c 字符
     */
    public void addSafeCharacter(char c) {
        safeCharacters.set(c);
    }

    /**
     * 移除安全字符<br>
     * 安全字符不被编码
     *
     * @param c 字符
     */
    public void removeSafeCharacter(char c) {
        safeCharacters.clear(c);
    }

    /**
     * 是否将空格编码为+
     *
     * @param encodeSpaceAsPlus 是否将空格编码为+
     */
    public void setEncodeSpaceAsPlus(boolean encodeSpaceAsPlus) {
        this.encodeSpaceAsPlus = encodeSpaceAsPlus;
    }

    /**
     * 将URL中的字符串编码为%形式
     *
     * @param path    需要编码的字符串
     * @param charset 编码, {@code null}返回原字符串，表示不编码
     * @return 编码后的字符串
     */
    public String encode(String path, Charset charset) {
        if (null == charset || StringUtils.isEmpty(path)) {
            return path;
        }

        final StringBuilder rewrittenPath = new StringBuilder(path.length());
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(buf, charset);

        int c;
        for (int i = 0; i < path.length(); i++) {
            c = path.charAt(i);
            if (safeCharacters.get(c)) {
                rewrittenPath.append((char) c);
            } else if (encodeSpaceAsPlus && c == SYMBOL_SPACE_CHAR) {
                // 对于空格单独处理
                rewrittenPath.append('+');
            } else {
                // convert to external encoding before hex conversion
                try {
                    writer.write((char) c);
                    writer.flush();
                } catch (IOException e) {
                    buf.reset();
                    continue;
                }

                byte[] ba = buf.toByteArray();
                for (byte toEncode : ba) {
                    // Converting each byte in the buffer
                    rewrittenPath.append('%');
                    Hex.appendHex(rewrittenPath, toEncode, false);
                }
                buf.reset();
            }
        }
        return rewrittenPath.toString();
    }

    /**
     * 增加安全字符[a-z][A-Z]
     */
    private void addAlpha() {
        for (char i = LETTER_LOWERCASE_A; i <= LETTER_LOWERCASE_Z; i++) {
            addSafeCharacter(i);
        }
        for (char i = LETTER_UPPERCASE_A; i <= LETTER_UPPERCASE_Z; i++) {
            addSafeCharacter(i);
        }
    }

    /**
     * 增加数字1-9
     */
    private void addDigit() {
        for (char i = CHARACTER_0; i <= CHARACTER_9; i++) {
            addSafeCharacter(i);
        }
    }


    /**
     * 增加sub-delims<br>
     * sub-delims  = "!" / "$" / "&" / "'" / "(" / ") / "*" / "+" / "," / ";" / "="
     * 定义见：https://datatracker.ietf.org/doc/html/rfc3986#section-2.2
     */
    private static void addSubDelims(UrlEncoder encoder){
        // Add the sub-delims
        encoder.addSafeCharacter('!');
        encoder.addSafeCharacter('$');
        encoder.addSafeCharacter('&');
        encoder.addSafeCharacter('\'');
        encoder.addSafeCharacter('(');
        encoder.addSafeCharacter(')');
        encoder.addSafeCharacter('*');
        encoder.addSafeCharacter('+');
        encoder.addSafeCharacter(',');
        encoder.addSafeCharacter(';');
        encoder.addSafeCharacter('=');
    }
}
