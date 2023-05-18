package com.chua.htmlunit.support.crawler.loader;

import com.chua.common.support.crawler.browser.BeanBrowser;
import com.chua.common.support.crawler.browser.Browser;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;

import java.io.IOException;

/**
 * 窗口加载器
 * @author CH
 */
public class WindowLoader implements AutoCloseable{

    private final String url;
    final WebClient webClient = new WebClient(BrowserVersion.BEST_SUPPORTED);

    {
        webClient.getOptions().setUseInsecureSSL(true);
        webClient.getOptions().setCssEnabled(false);//关闭css
        webClient.getOptions().setJavaScriptEnabled(true);//开启js
        webClient.getOptions().setRedirectEnabled(true);//重定向
        webClient.getOptions().setThrowExceptionOnScriptError(false);//关闭js报错
        webClient.getOptions().setTimeout(50000);//超时时间
        webClient.getCookieManager().setCookiesEnabled(true);//允许cookie
        webClient.waitForBackgroundJavaScript(15000);
        webClient.waitForBackgroundJavaScriptStartingBefore(5000);
        webClient.setAjaxController(new NicelyResynchronizingAjaxController());//设置支持AJAX
    }

    public WindowLoader(String url) {
        this.url = url;
    }

    /**
     * 创建浏览器
     * @return 浏览器
     */
    public Browser createWindow() {
        try {
            return new BeanBrowser(new Object[]{webClient.getPage(url), webClient});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws Exception {
        webClient.close();
    }
}
