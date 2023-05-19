package com.chua.common.support.lang.download;

import com.chua.common.support.utils.FileUtils;
import com.chua.common.support.utils.StringUtils;
import com.chua.common.support.utils.UrlUtils;

import java.net.URLConnection;

/**
 * 下载处理
 *
 * @author CH
 * @since 2022/8/10 0:37
 */
public interface DownloadHandler {
    /**
     * 开启
     */
    void execute();

    /**
     * 文件名
     * @return 文件名
     */
    String getFileName();
    /**
     * 文件名
     *
     * @param connection 连接
     * @return 文件名
     */
    default String createFileName(URLConnection connection) {
       return UrlUtils.getFileName(connection);
    }
}
