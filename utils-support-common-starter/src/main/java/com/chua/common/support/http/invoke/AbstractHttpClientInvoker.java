package com.chua.common.support.http.invoke;

import com.chua.common.support.http.*;
import com.chua.common.support.utils.ThreadUtils;

import java.util.concurrent.ExecutorService;

import static com.chua.common.support.http.HttpMethod.*;

/**
 * 执行器
 *
 * @author CH
 */
public abstract class AbstractHttpClientInvoker implements HttpClientInvoker {

    private static final String HTTPS = "https://";
    protected final boolean isHttps;
    protected String url;
    protected HttpRequest request;
    protected final HttpMethod httpMethod;

    public AbstractHttpClientInvoker(HttpRequest request, HttpMethod httpMethod) {
        this.request = request;
        this.httpMethod = httpMethod;
        this.url = request.getUrl();
        this.isHttps = url.startsWith(HTTPS);
    }

    @Override
    public HttpResponse execute() {
        HttpResponse responseEntity = null;
        if (GET.equals(httpMethod)) {
            responseEntity = executeGet();
        } else if (POST.equals(httpMethod)) {
            responseEntity = executePost();
        } else if (PUT.equals(httpMethod)) {
            responseEntity = executePut();
        } else if (DELETE.equals(httpMethod)) {
            responseEntity = executeDelete();
        } else if (HEAD.equals(httpMethod)) {
            responseEntity = executeHead();
        } else if (OPTION.equals(httpMethod)) {
            responseEntity = executeOption();
        } else if (PATCH.equals(httpMethod)) {
            responseEntity = executePatch();
        }

        return responseEntity;
    }


    /**
     * delete
     *
     * @return delete
     */
    protected abstract HttpResponse executeDelete();

    /**
     * put
     *
     * @return put
     */
    protected abstract HttpResponse executePut();

    /**
     * post
     *
     * @return post
     */
    protected abstract HttpResponse executePost();

    /**
     * get
     *
     * @return get
     */
    protected abstract HttpResponse executeGet();

    /**
     * patch
     *
     * @return patch
     */
    protected abstract HttpResponse executePatch();

    /**
     * option
     *
     * @return option
     */
    protected abstract HttpResponse executeOption();

    /**
     * header
     *
     * @return header
     */
    protected abstract HttpResponse executeHead();

    @Override
    public void execute(ResponseCallback<HttpResponse> responseCallback) {
        ExecutorService executorService = ThreadUtils.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                HttpResponse responseEntity = execute();
                responseCallback.onResponse(responseEntity);
            } catch (Exception e) {
                responseCallback.onResponse(HttpResponse.builder().code(500).message(e.getMessage()).build());
                responseCallback.onFailure(e);
            } finally {
                executorService.shutdownNow();
            }
        });
    }
}
