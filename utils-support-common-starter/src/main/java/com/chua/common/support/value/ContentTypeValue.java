package com.chua.common.support.value;

import com.chua.common.support.utils.StringUtils;

import static com.chua.common.support.view.ViewPreview.OCTET_STREAM;

/**
 * 带有默认值对象
 * @author ACHTK
 * @param <T>
 */
@SuppressWarnings("ALL")
public class ContentTypeValue<T> implements Value<T> {


    public static final ContentTypeValue INSTANCE = new ContentTypeValue(null, null);
    public static final ContentTypeValue TEXT = new ContentTypeValue(null, "text/plain");
    public static final ContentTypeValue HTML = new ContentTypeValue(null, "text/html");
    public static final ContentTypeValue OCET = new ContentTypeValue(null, OCTET_STREAM);
    private final T value;

    private String contentType;

    public ContentTypeValue(T value, String contentType) {
        this.value = value;
        this.contentType = contentType;
    }

    /**
     * 默认值
     * @return this
     */
    public static <T>ContentTypeValue<T> text() {
        return TEXT;
    }

    /**
     * 默认值
     * @return this
     */
    public static <T>ContentTypeValue<T> empty() {
        return INSTANCE;
    }

    /**
     * 默认值
     * @return this
     */
    public static <T>ContentTypeValue<T> newHtml() {
        return new ContentTypeValue<>(null, "text/html");
    }

    public void setContentType(String contentType) {
        if(null == this.contentType) {
            this.contentType = contentType;
        }
    }
    /**
     * 获取类型
     * @return 类型
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * 获取类型
     * @param defaultValue 默认类型
     * @return 类型
     */
    public String getContentType(String defaultValue) {
        return StringUtils.defaultString(contentType, defaultValue);
    }

    @Override
    public T getValue() {
        return value;
    }


    @Override
    public Throwable getThrowable() {
        return null;
    }

    @Override
    public boolean isNull() {
        return null == value;
    }
}
