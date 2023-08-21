package com.chua.spider.support.page;

import com.chua.common.support.bean.BeanUtils;
import com.chua.common.support.http.HttpStatus;
import com.chua.common.support.lang.proxy.BridgingMethodIntercept;
import com.chua.common.support.lang.proxy.BridgingProxyMethodIntercept;
import com.chua.common.support.lang.proxy.ProxyUtils;
import com.chua.common.support.lang.proxy.plugin.ProxyPlugin;
import com.chua.common.support.lang.spide.page.Page;
import com.chua.common.support.lang.spide.pipeline.ResultItems;
import com.chua.common.support.lang.spide.request.Request;
import com.chua.common.support.lang.spide.selector.Selectable;
import us.codecraft.webmagic.selector.Html;

import java.lang.reflect.Method;
import java.util.List;

/**
 * page
 * @author CH
 */
public class MagicPage implements Page {
    private final us.codecraft.webmagic.Page page;

    public MagicPage(us.codecraft.webmagic.Page page) {
        this.page = page;
    }

    @Override
    public Request getSpiderRequest() {
        return null;
    }

    @Override
    public boolean isDownloadSuccess() {
        return page.isDownloadSuccess();
    }

    @Override
    public void putField(String key, Object field) {
        page.putField(key, field);
    }

    @Override
    public HttpStatus getStatusCode() {
        return HttpStatus.ofCode(page.getStatusCode());
    }

    @Override
    public ResultItems getResultItems() {
        return BeanUtils.copyProperties(page.getResultItems(), ResultItems.class);
    }

    @Override
    public List<Request> getTargetRequests() {
        return null;
    }

    @Override
    public void setSkip(boolean b) {
        page.setSkip(true);
    }

    @Override
    public Selectable getHtml() {
        return ProxyUtils.proxy(Selectable.class,
                ClassLoader.getSystemClassLoader(),
                new BridgingProxyMethodIntercept<Selectable>(Selectable.class, page.getHtml()));
    }

    @Override
    public void addTargetRequests(List<String> all) {
        page.addTargetRequests(all);
    }
}
