package com.chua.agent.support.http;

import com.chua.agent.support.utils.ResourceUtils;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

/**
 * 资源
 *
 * @author CH
 */
public class ResourceHttpExchangeHandler implements RequestHandler<HttpExchange, Object>, HttpHandler {
    @Override
    public void handle(HttpExchange exchange, Object response) throws IOException {
        String path = exchange.getRequestURI().getPath();
        String url = path.substring(path.indexOf("resource") + 8);
        url = url.substring(url.indexOf("/") + 1);

        byte[] bytes = null;

        if (url.endsWith(".png")) {
            bytes = ResourceUtils.getImage(url);
        } else {
            bytes = ResourceUtils.getUrl(url);
        }
        Headers responseHeaders = exchange.getResponseHeaders();
        if (!url.endsWith(".png")) {
            responseHeaders.set("Cache-Control", "no-cache");
        }

        try (OutputStream os = exchange.getResponseBody()) {
            if (url.endsWith(".html")) {
                responseHeaders.set("content-type", "text/html; charset=utf-8");
                responseHeaders.set("Accept-Encoding", "gzip,deflate");
            } else if (url.endsWith(".png")) {
                responseHeaders.set("content-type", "image/png");
                responseHeaders.set("Accept-Encoding", "gzip,deflate");
            } else if (url.endsWith(".css")) {
                responseHeaders.set("content-type", "text/css");
                responseHeaders.set("accept-ranges", "bytes");
                responseHeaders.set("access-control-allow-credentials", "true");
                responseHeaders.set("access-control-allow-headers", "Accept,Authorization,Cache-Control,Content-Type,DNT,If-Modified-Since,Keep-Alive,Origin,User-Agent,X-Requested-With,X-CustomHeader,Content-Range,Range");


//            } else if (url.endsWith(".ttf")) {
//                responseHeaders.set("content-type", "font/ttf");
//            } else if (url.endsWith(".woff")) {
//                responseHeaders.set("content-type", "font/woff");
//            } else if (url.endsWith(".woff2")) {
//                responseHeaders.set("content-type", "font/woff2");
//            } else if (url.endsWith(".svg")) {
//                responseHeaders.set("content-type", "text/xml; charset=utf-8");
//            } else if (url.endsWith(".eot")) {
//                responseHeaders.set("content-type", "application/vnd.ms-fontobject; charset=utf-8");
            } else if (url.endsWith(".js")) {
                responseHeaders.set("content-type", "text/javascript; charset=utf-8");
                responseHeaders.set("Accept-Encoding", "gzip,deflate");
            } else {
                responseHeaders.set("content-type", "application/octet-stream");
                responseHeaders.set("accept-ranges", "bytes");
                responseHeaders.set("access-control-allow-credentials", "true");
                responseHeaders.set("access-control-allow-headers", "Accept,Authorization,Cache-Control,Content-Type,DNT,If-Modified-Since,Keep-Alive,Origin,User-Agent,X-Requested-With,X-CustomHeader,Content-Range,Range");
                responseHeaders.set("content-length", String.valueOf(bytes.length));
                responseHeaders.set("Cache-Control", "max-age=864000");
            }
            responseHeaders.set("date", new Date().toString());
            exchange.sendResponseHeaders(200, 0);
            os.write(bytes);
        }
    }


    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        handle(httpExchange, null);
    }
}
