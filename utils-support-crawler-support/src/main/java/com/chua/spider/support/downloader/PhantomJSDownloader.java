package com.chua.spider.support.downloader;

import com.chua.common.support.lang.spide.downloader.Downloader;
import com.chua.common.support.lang.spide.page.Page;
import com.chua.common.support.lang.spide.request.Request;
import com.chua.common.support.lang.spide.task.Task;
import com.chua.spider.support.page.MagicPage;
import com.chua.spider.support.request.MagicRequest;
import us.codecraft.webmagic.Spider;

/**
 * @author CH
 */
public class PhantomJSDownloader extends us.codecraft.webmagic.downloader.PhantomJSDownloader implements Downloader<Spider> {
    @Override
    public Page download(Request request, Task<Spider> task) {
        Spider spider = task.getSpider();
        return new MagicPage(super.download(new MagicRequest(request), spider));
    }
}
