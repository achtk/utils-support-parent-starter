package com.chua.agent.support.http;

import com.chua.agent.support.utils.ClassUtils;
import com.chua.agent.support.utils.ResourceUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

/**
 * 资源
 *
 * @author CH
 */
public class ResourceRequestHandler implements RequestHandler<Object, Object> {
    @Override
    public void handle(Object request, Object response) throws IOException {
        String path = ClassUtils.invoke("getRequestURI", request).toString();
        String url = path.substring(path.indexOf("resource") + 8);
        url = url.substring(url.indexOf("/") + 1);

        ClassUtils.invoke("setHeader", response, "Cache-Control", "no-cache");

        try (OutputStream os = (OutputStream) ClassUtils.invoke("getOutputStream", response)) {
            if (url.endsWith(".html")) {
                ClassUtils.invoke("setHeader", response, "content-type", "text/html; charset=utf-8");
                ClassUtils.invoke("setHeader", response, "Accept-Encoding", "gzip,deflate");
            } else if (url.endsWith(".png")) {
                ClassUtils.invoke("setHeader", response, "content-type", "image/png");
                ClassUtils.invoke("setHeader", response, "Accept-Encoding", "gzip,deflate");
            } else if (url.endsWith(".css")) {
                ClassUtils.invoke("setHeader", response, "content-type", "text/css; charset=utf-8");
                ClassUtils.invoke("setHeader", response, "Accept-Encoding", "gzip,deflate");
            } else if (url.endsWith(".ttf")) {
                ClassUtils.invoke("setHeader", response, "content-type", "application/x-font-ttf; charset=utf-8");
            } else if (url.endsWith(".woff")) {
                ClassUtils.invoke("setHeader", response, "content-type", "application/x-font-woff; charset=utf-8");
            } else if (url.endsWith(".woff2")) {
                ClassUtils.invoke("setHeader", response, "content-type", "application/x-font-woff2; charset=utf-8");
            } else if (url.endsWith(".svg")) {
                ClassUtils.invoke("setHeader", response, "content-type", "image/svg+xml; charset=utf-8");
            } else if (url.endsWith(".eot")) {
                ClassUtils.invoke("setHeader", response, "content-type", "application/vnd.ms-fontobject; charset=utf-8");
                ClassUtils.invoke("setHeader", response, "Accept-Encoding", "gzip,deflate");

            } else {
                ClassUtils.invoke("setHeader", response, "content-type", "text/javascript; charset=utf-8");
                ClassUtils.invoke("setHeader", response, "Accept-Encoding", "gzip,deflate");

            }
            byte[] bytes = null;
            ClassUtils.invoke("setHeader", response, "date", new Date().toString());
            if (url.endsWith(".png")) {
                bytes = ResourceUtils.getImage(url);
            } else {
                bytes = ResourceUtils.getUrl(url);
            }

            ClassUtils.invoke("setHeader", response, "content-length", String.valueOf(bytes.length));
            ClassUtils.invoke("setStatus", response, 200);
            os.write(bytes);
        }
    }


}
