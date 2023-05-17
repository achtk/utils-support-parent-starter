package com.chua.common.support.view;

import com.chua.common.support.image.filter.ImageFilter;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.MapUtils;
import com.chua.common.support.utils.NetAddress;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import lombok.SneakyThrows;

import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.chua.common.support.constant.CommonConstant.PREVIEW;
import static com.chua.common.support.constant.CommonConstant.SYMBOL_LEFT_SLASH;

/**
 * sso服务器
 *
 * @author CH
 */
public class HttpViewServer implements ViewServer {

    private HttpServer httpServer;
    protected Map<String, ImageFilter> imageFilterList = new ConcurrentHashMap<>();

    private final Map<String, ViewResolver> resolverMap = new ConcurrentHashMap<>();


    @Override
    public void run(String[] args) throws Exception {
        NetAddress netAddress = NetAddress.of(args);
        httpServer = HttpServer.create(netAddress.getInetSocketAddress(), -1);
        httpServer.createContext(SYMBOL_LEFT_SLASH, new HttpHandler() {
            @Override
            public void handle(HttpExchange httpExchange) throws IOException {
                String url = httpExchange.getRequestURI().toString();
                BucketAddress bucketAddress = new BucketAddress(url);
                String bucket = bucketAddress.getBucket();
                if (!resolverMap.containsKey(bucket)) {
                    writeStream(httpExchange, null);
                    return;
                }
                ViewResolver viewResolver = resolverMap.get(bucket);
                Headers responseHeaders = httpExchange.getResponseHeaders();
                responseHeaders.set("Cache-Control", "no-cache");
                responseHeaders.set("Access-Control-Allow-Origin", "*");
                try (OutputStream responseBody = httpExchange.getResponseBody()) {
                    responseHeaders.set("Keep-Alive", "timeout=30");
                    responseHeaders.set("Date", new Date().toString());
                    responseHeaders.set("Accept-Encoding", "gzip,deflate");
                    Map<String, String> split = bucketAddress.getParam();

                    String mode = MapUtils.getString(split, "mode", PREVIEW);
                    String plugin = MapUtils.getString(split, "plugin", "");
                    Set<String> plugins = new HashSet<>(Arrays.asList(plugin.split(",")));
                    ViewPreview preview = null;
                    try {
                        preview = viewResolver.preview(bucket, bucketAddress.getPath(), mode, null, Collections.emptySet());
                        responseHeaders.set("Content-Type", preview.getContentType());
                        httpExchange.sendResponseHeaders(200, 0);
                        viewResolver.preview(bucket, bucketAddress.getPath(), mode, responseBody, plugins);
                    } catch (Throwable e) {
                        writeStream(httpExchange, responseBody);
                    }

                }
            }


            /**
             * 写入数据
             *
             * @param httpExchange 请求
             * @param outputStream 解释器
             */
            @SneakyThrows
            private void writeStream(HttpExchange httpExchange, OutputStream outputStream) {
                httpExchange.sendResponseHeaders(404, 0);
                if (null == outputStream) {
                    return;
                }
                outputStream.write(new byte[0]);
            }
        });
        httpServer.start();
    }

    @Override
    public void stop() throws Exception {
        httpServer.stop(0);
    }

    @Override
    public ViewServer addContext(String bucket, ViewConfig viewConfig) {
        if (resolverMap.containsKey(bucket)) {
            throw new IllegalStateException("bucket已存在");
        }

        ViewResolver viewResolver = ServiceProvider.of(ViewResolver.class).getNewExtension(viewConfig.getType());
        if (null == viewResolver) {
            return this;
        }

        viewResolver.setPlugin(imageFilterList);
        viewResolver.setConfig(viewConfig);
        resolverMap.put(bucket, viewResolver);
        return this;
    }

    @Override
    public ViewServer addPlugin(String name, ImageFilter imageFilter) {
        imageFilterList.put(name, imageFilter);
        for (ViewResolver viewResolver : resolverMap.values()) {
            viewResolver.setPlugin(imageFilterList);
        }
        return this;
    }
}
