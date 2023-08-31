package com.chua.common.support.view;

import lombok.Data;
import lombok.experimental.Accessors;

import static com.chua.common.support.constant.CommonConstant.DOWNLOAD;

/**
 * 视图
 *
 * @author CH
 * @since 2022/8/3 15:18
 */
@Data
@Accessors(chain = true)
public class ViewPreview {

    public static final String OCTET_STREAM = "application/octet-stream";

    /**
     * 初始化
     *
     * @param mode 模式
     * @return this
     */
    public static ViewPreview of(String mode) {
        ViewPreview viewPreview = new ViewPreview();
        if (DOWNLOAD.equalsIgnoreCase(mode)) {
            viewPreview.setContentType(OCTET_STREAM);
        }
        return viewPreview;
    }

    /**
     * contentType
     */
    private String contentType;

    private static final ViewPreview VIEW_PREVIEW = new ViewPreview();
    private static final ViewPreview VIEW_PREVIEW2 = new ViewPreview();

    static {
        VIEW_PREVIEW.setContentType(OCTET_STREAM);
        VIEW_PREVIEW2.setContentType("text/plain");
    }

    public static ViewPreview emptyDownloader() {
        return VIEW_PREVIEW;
    }

    public static ViewPreview empty() {
        return VIEW_PREVIEW2;
    }

    /**
     * 赋值
     *
     * @param contentType 内容
     * @return this
     */
    public ViewPreview setContentType(String contentType) {
        if (!OCTET_STREAM.equalsIgnoreCase(this.contentType)) {
            this.contentType = contentType + ";charset=UTF-8";
        }
        return this;
    }

    /**
     * 是否是下载
     *
     * @return 是否是下载
     */
    public boolean isDownload() {
        return OCTET_STREAM.equalsIgnoreCase(contentType);
    }

    /**
     * 是否是图片
     *
     * @return 是否是图片
     */
    public boolean isImage() {
        return null != contentType && contentType.startsWith("image/");
    }
}
