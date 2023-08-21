package com.chua.common.support.lang.spide.downloader;

import com.chua.common.support.lang.spide.page.Page;
import com.chua.common.support.lang.spide.request.Request;
import com.chua.common.support.lang.spide.task.Task;

/**
 * 下载器
 * @author CH
 */
public interface Downloader<Spider> {

    /**
     * 下载器
     *
     * @param request request
     * @param task task
     * @return page
     */
    Page download(Request request, Task<Spider> task);

    /**
     *  设置线程数
     * @param threadNum 线程数
     */
    void setThread(int threadNum);
}
