package com.chua.server.support.server.file;

import com.chua.common.support.function.Splitter;
import com.chua.common.support.image.filter.ImageFilter;
import com.chua.common.support.protocol.server.AbstractServer;
import com.chua.common.support.protocol.server.ServerOption;
import com.chua.common.support.utils.MapUtils;
import com.chua.common.support.utils.StringUtils;
import com.chua.common.support.view.*;
import com.chua.server.support.server.parameter.VertxParameterResolver;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.net.impl.SocketAddressImpl;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.chua.common.support.constant.CommonConstant.DOWNLOAD;
import static com.chua.common.support.constant.CommonConstant.PREVIEW;


/**
 * 文件服务器
 *
 * @author CH
 * @since 2021-09-07
 */
@Slf4j
public class FileVfsServer extends AbstractServer {

    final Vertx vertx = Vertx.vertx();
    private final Map<String, ViewResolver> viewResolver = new ConcurrentHashMap<>();
    protected Map<String, ImageFilter> imageFilterList = new ConcurrentHashMap<>();

    protected FileVfsServer(ServerOption serverOption, String... args) {
        super(serverOption);
    }


    @Override
    public void run() {
//        viewResolver.putAll(beanFactory.getBeanMap(ViewResolver.class));
//        imageFilterList.putAll(beanFactory.getBeanMap(ImageFilter.class));
        addClassPathViewResolver();
        addViewResolver();
        refreshResolver();

        HttpServer server = vertx.createHttpServer();
        server.requestHandler(request -> {
            String uri = request.uri();
            if (uri.contains("?")) {
                uri = uri.substring(0, uri.indexOf("?"));
            }
            HttpServerResponse httpServerResponse = request.response();
            BucketAddress bucketAddress = new BucketAddress(uri);
            String bucket = bucketAddress.getBucket();
            if (!viewResolver.containsKey(bucket)) {
                writeStream(httpServerResponse, 404, Buffer.buffer());
                return;
            }
            ViewResolver viewResolver = this.viewResolver.get(bucket);
            httpServerResponse.putHeader("Cache-Control", "no-cache");
            httpServerResponse.putHeader("Access-Control-Allow-Origin", "*");
            httpServerResponse.putHeader("Keep-Alive", "timeout=30");
            httpServerResponse.putHeader("Date", new Date().toString());

            Map<String, String> split = bucketAddress.getParam();
            String mode = MapUtils.getString(split, "mode", request.getParam("mode", PREVIEW));
            if(!DOWNLOAD.equals(mode)) {
                httpServerResponse.putHeader("Accept-Encoding", "gzip,deflate");
            }

            String plugin = MapUtils.getString(split, "plugin", request.getParam("plugin", ""));
            Set<String> plugins = new HashSet<>(Splitter.on(',').trimResults().omitEmptyStrings().splitToList(plugin));

            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                ViewPreview viewPreview = viewResolver.preview(bucket, bucketAddress.getPath(), mode, outputStream, plugins);
                httpServerResponse.putHeader("Content-Type", viewPreview.getContentType());
                writeStream(httpServerResponse, 200, Buffer.buffer(outputStream.toByteArray()));
            } catch (Throwable e) {
                writeStream(httpServerResponse, 404, Buffer.buffer());
            }


        });

        int port = getPort();
        String host = getHost();
        InetSocketAddress inetSocketAddress;
        if (StringUtils.isNullOrEmpty(host)) {
            inetSocketAddress = new InetSocketAddress(port);
        } else {
            inetSocketAddress = new InetSocketAddress(host, port);
        }
        server.listen(new SocketAddressImpl(inetSocketAddress));
    }




    /**
     * 注入过滤器
     */
    private void refreshResolver() {
        for (ViewResolver resolver : viewResolver.values()) {
            resolver.setPlugin(imageFilterList);
        }
    }
    private void addClassPathViewResolver() {
        ViewResolver viewResolver1 = new ClasspathViewResolver();
        ViewConfig viewConfig = new ViewConfig();
        viewConfig.setPath("/");
        viewResolver1.setConfig(viewConfig);

        viewResolver.put("webjars", viewResolver1);
    }
    /**
     * 添加解析器
     */
    private void addViewResolver() {
        String store = request.getString("store");
        String storeName = request.getString("store-name");
        if (StringUtils.isNullOrEmpty(store) && StringUtils.isNullOrEmpty(storeName)) {
            return;
        } else if (StringUtils.isNullOrEmpty(store) || StringUtils.isNullOrEmpty(storeName)) {
            log.warn("store => {} 或者storeName => {} 都不能为空", store, storeName);
            return;
        }

        ViewResolver viewResolver1 = new LocalViewResolver();
        ViewConfig viewConfig = new ViewConfig();
        viewConfig.setPath(store);
        viewResolver1.setConfig(viewConfig);

        viewResolver.put(storeName, viewResolver1);
    }


    /**
     * 写入数据
     *
     * @param httpServerResponse 响应
     * @param code               编码
     * @param buffer             结果
     */
    @SneakyThrows
    private void writeStream(HttpServerResponse httpServerResponse, int code, Buffer buffer) {
        httpServerResponse.setStatusCode(code);
        httpServerResponse.end(buffer);
    }

    @Override
    public void shutdown() {
        vertx.close();
    }

    @Override
    public void afterPropertiesSet() {
        super.register(new VertxParameterResolver());
    }
}
