package com.chua.httpclient.support.okhttp;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.collection.ConcurrentReferenceHashMap;
import com.chua.common.support.converter.Converter;
import com.chua.common.support.http.*;
import com.chua.common.support.http.invoke.AbstractHttpClientInvoker;
import com.chua.common.support.json.Json;
import com.chua.common.support.net.NetAddress;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.IoUtils;
import com.chua.common.support.utils.ObjectUtils;
import com.ejlchina.data.Array;
import com.ejlchina.data.ListMap;
import com.ejlchina.data.Mapper;
import com.ejlchina.okhttps.Process;
import com.ejlchina.okhttps.*;
import com.ejlchina.stomp.Stomp;
import lombok.extern.slf4j.Slf4j;
import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.Proxy;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * apache httpclient
 *
 * @author CH
 */
@Slf4j
@Spi(value = {"httpclient", "okhttp"}, order = 1)
public class OkHttpClientInvoker extends AbstractHttpClientInvoker implements AutoCloseable {

    private HTTP http;
    private static final Map<HttpRequest, HTTP> CACHE_HTTP = new ConcurrentReferenceHashMap<>();
    private WHttpTask wHttpTask;
    private Stomp stomp;

    public OkHttpClientInvoker(HttpRequest request, HttpMethod httpMethod) {
        super(request, httpMethod);
        this.initialCacheable();
    }

    @Override
    public void execute(ResponseCallback<HttpResponse> responseCallback) {
        AHttpTask sync = http.async(url);
        sync.setOnException(responseCallback::onFailure);
        sync.setOnResponse(httpResult -> {
            IOException error = httpResult.getError();
            if (null != error) {
                responseCallback.onFailure(error);
            }

            responseCallback.onResponse(createResponseEntity(httpResult));
        });
        doConfig(sync);
        execute();
    }

    @Override
    protected HttpResponse executeDelete() {
        SHttpTask sync = http.sync(url);
        doConfig(sync);
        return createResponseEntity(sync.delete());
    }

    @Override
    protected HttpResponse executePut() {
        SHttpTask sync = http.sync(url);
        doConfig(sync);
        return createResponseEntity(sync.put());
    }

    @Override
    protected HttpResponse executePost() {
        SHttpTask sync = http.sync(url);
        doConfig(sync);
        return createResponseEntity(sync.post());
    }

    @Override
    protected HttpResponse executeGet() {
        SHttpTask sync = http.sync(url);
        doConfig(sync);
        return createResponseEntity(sync.get());
    }

    @Override
    protected HttpResponse executePatch() {
        SHttpTask sync = http.sync(url);
        doConfig(sync);
        return createResponseEntity(sync.patch());
    }

    @Override
    protected HttpResponse executeOption() {
        return HttpResponse.builder().code(500).message("不支持该操作").build();
    }

    @Override
    protected HttpResponse executeHead() {
        SHttpTask sync = http.sync(url);
        doConfig(sync);
        return createResponseEntity(sync.head());
    }


    /**
     * 构建消息
     *
     * @param httpResult 结果
     * @return 结果
     */
    private <T> HttpResponse createResponseEntity(HttpResult httpResult) {
        HttpResponse.HttpResponseBuilder builder = HttpResponse.builder();
        builder.code(httpResult.getStatus());
        builder.content(httpResult.getBody());

        HttpHeader header = new HttpHeader();
        ListMap<String> stringListMap = httpResult.allHeaders();
        for (Map.Entry<String, String> entry : stringListMap.entrySet()) {
            header.put(entry.getKey(), entry.getValue());
        }

        builder.httpHeader(header);
        return builder.build();
    }

    /**
     * websocket
     *
     * @param tClass 任务
     * @param <T>    类型
     */
    private <T> void createWebSocket(Class<T> tClass) {

        StompCallback stompCallback = new StompCallback(tClass);

        this.wHttpTask = http.webSocket(request.getUrl())
                .heatbeat((int) (request.getConnectTimeout() / 1000), (int) (request.getReadTimeout() / 1000))
                // 传入 false 让客户端以固定间隔发送心跳
                .flexiblePing(false)
                .setOnOpen((webSocket, httpResult) -> ObjectUtils.ifValid(stompCallback.onOpen(), webSocket::send))
                .setOnMessage((webSocket, msg) -> {
                    String bean = msg.toBean(String.class);
                    ObjectUtils.ifValid(stompCallback.onMessage(bean), webSocket::send);
                })
                .setOnClosed((webSocket, httpResult) -> ObjectUtils.ifValid(stompCallback.onClose(), webSocket::send))
                .setOnException((webSocket, throwable) -> ObjectUtils.ifValid(stompCallback.onError(throwable), webSocket::send));

        wHttpTask.listen();
    }

    /**
     * stomp
     *
     * @param tClass 任务
     * @param <T>    类型
     */
    private <T> void createStomp(Class<T> tClass) {

        StompCallback stompCallback = new StompCallback(tClass);

        this.stomp = Stomp.over(OkHttps.webSocket(url)
                        .heatbeat((int) (request.getConnectTimeout() / 1000), (int) (request.getReadTimeout() / 1000))
                        // 传入 false 让客户端以固定间隔发送心跳
                        .flexiblePing(false),
                false
        );
        stomp.setOnConnected((webSocket) -> {
            ObjectUtils.ifValid(stompCallback.onOpen(), it -> null);
        }).setOnDisconnected((webSocket) -> {
            ObjectUtils.ifValid(stompCallback.onClose(), it -> null);
        }).setOnException(throwable -> {
            ObjectUtils.ifValid(stompCallback.onError(throwable), it -> null);
        });
        stomp.connect();
    }

    @Override
    public void close() throws Exception {
        if (null != wHttpTask) {
            wHttpTask.cancel();
        }
        if (null != stomp) {
            stomp.disconnect();
        }
    }


    /**
     * 设置配置
     *
     * @param sync 配置
     */
    private void doConfig(HttpTask sync) {
        sync.addHeader(request.getHeader().asSimpleMap());
        sync.charset(StandardCharsets.UTF_8);

        sync.setOnProcess(new Consumer<Process>() {
            private List<Double> sum = new ArrayList<>();

            @Override
            public void accept(Process process) {
                double rate = process.getRate();
                sum.add(rate);
                long doneBytes = process.getDoneBytes();
                long totalBytes = process.getTotalBytes();

                if (doneBytes >= totalBytes) {
                    log.info("当前平均下载速度: {}/s", sum.stream().collect(Collectors.averagingDouble(Double::doubleValue)));
                }
            }
        });

        Map<String, Object> body = request.getBody();
        for (Map.Entry<String, Object> entry : body.entrySet()) {
            Object value = entry.getValue();
            if (HttpMethod.GET == httpMethod) {
                sync.addUrlPara(entry.getKey(), value);
            } else {
                if (value instanceof File) {
                    sync.addFilePara(entry.getKey(), (File) value);
                    continue;
                }

                if (value instanceof byte[]) {
                    sync.addFilePara(entry.getKey(), "", (byte[]) value);
                    continue;
                }

                sync.addBodyPara(entry.getKey(), value);
            }
        }
    }


    private void initialCacheable() {
        this.http = CACHE_HTTP.computeIfAbsent(request, it -> HTTP.builder()
                .addMsgConvertor(new MsgConvertor() {
                    @Override
                    public String mediaType() {
                        return null;
                    }

                    @Override
                    public Mapper toMapper(InputStream inputStream, Charset charset) {
                        return null;
                    }

                    @Override
                    public Array toArray(InputStream inputStream, Charset charset) {
                        return null;
                    }

                    @Override
                    public byte[] serialize(Object o, Charset charset) {
                        return new byte[0];
                    }

                    @Override
                    public <T> T toBean(Type type, InputStream inputStream, Charset charset) {
                        String string = null;
                        try {
                            string = IoUtils.toString(inputStream, charset);
                            if (type == String.class) {
                                return (T) string;
                            }
                        } catch (IOException e) {
                            try {
                                throw new IOException(e);
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }
                        }
                        return Json.fromJson(string, (Class<T>) type);
                    }

                    @Override
                    public <T> List<T> toList(Class<T> aClass, InputStream inputStream, Charset charset) {
                        return null;
                    }
                })
                .config(new HTTP.OkConfig() {
                    @Override
                    public void config(OkHttpClient.Builder builder) {
                        //转化器
                        // 连接超时时间（默认10秒）
                        builder.connectTimeout(request.getConnectTimeout(), TimeUnit.MILLISECONDS);
                        // 读取超时时间（默认10秒）
                        builder.readTimeout(request.getReadTimeout(), TimeUnit.MILLISECONDS);
                        builder.callTimeout(request.getConnectTimeout(), TimeUnit.MILLISECONDS);
                        // 配置连接池 最小10个连接（不配置默认为 5）
                        builder.connectionPool(new ConnectionPool(request.getMaxConnTotal(), 5, TimeUnit.MINUTES));
                        //代理
                        ObjectUtils.ifNone(request.getProxy(), it -> {
                            NetAddress netAddress = NetAddress.of(it.toString());
                            builder.proxy(new Proxy(Proxy.Type.HTTP, netAddress.toInetSocketAddress()));
                        });
                        //重试
                        builder.addInterceptor(new Interceptor() {
                            @Override
                            public Response intercept(Chain chain) throws IOException {
                                int retryTimes = 0;
                                while (true) {
                                    Response response = null;
                                    Exception exception = null;
                                    try {
                                        response = chain.proceed(chain.request());
                                    } catch (Exception e) {
                                        exception = e;
                                    }
                                    boolean b = (exception != null || response.code() == 500) && retryTimes < request.getRetry();
                                    if (b) {
                                        System.out.println("失败重试第" + retryTimes + "次！");
                                        if (response != null) {
                                            // 注意，这里一定要 close 掉失败的 Response
                                            response.close();
                                        }
                                        retryTimes++;
                                        continue;
                                    } else if (exception != null) {
                                        try {
                                            throw exception;
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    return response;
                                }
                            }
                        });
                    }
                })
                        .

                build());
    }


    final class StompCallback {

        private static final String OPEN = "javax.websocket.OnOpen";
        private static final String MESSAGE = "javax.websocket.OnMessage";
        private static final String CLOSE = "javax.websocket.OnClose";
        private static final String ERROR = "javax.websocket.OnError";

        private final Map<String, Method> stomps = new ConcurrentHashMap<>();
        private final Object object;
        private final Class<?> type;

        public StompCallback(Object object) {
            this.object = ClassUtils.asObject(object);
            this.type = ClassUtils.toType(object);
            doAnalysisStomp();
        }

        /**
         * 解析类
         */
        private void doAnalysisStomp() {
            Method[] declaredMethods = type.getDeclaredMethods();
            for (Method declaredMethod : declaredMethods) {
                ClassUtils.ifPresent(OPEN, it -> {
                    Annotation annotation = declaredMethod.getDeclaredAnnotation((Class<? extends Annotation>) it);
                    if (null != annotation) {
                        declaredMethod.setAccessible(true);
                        stomps.put("open", declaredMethod);
                    }
                });
                ClassUtils.ifPresent(CLOSE, it -> {
                    Annotation annotation = declaredMethod.getDeclaredAnnotation((Class<? extends Annotation>) it);
                    if (null != annotation) {
                        declaredMethod.setAccessible(true);
                        stomps.put("close", declaredMethod);
                    }
                });
                ClassUtils.ifPresent(ERROR, it -> {
                    Annotation annotation = declaredMethod.getDeclaredAnnotation((Class<? extends Annotation>) it);
                    if (null != annotation) {
                        declaredMethod.setAccessible(true);
                        stomps.put("error", declaredMethod);
                    }
                });
                ClassUtils.ifPresent(MESSAGE, it -> {
                    Annotation annotation = declaredMethod.getDeclaredAnnotation((Class<? extends Annotation>) it);
                    if (null != annotation) {
                        declaredMethod.setAccessible(true);
                        stomps.put("message", declaredMethod);
                    }
                });
            }
        }

        /**
         * 关闭
         *
         * @param value 参数
         * @return 消息
         */
        public String onMessage(String value) {
            Method method = stomps.get("message");
            int parameterCount = method.getParameterCount();
            Object[] params = null;
            if (0 == parameterCount) {
                params = new Object[0];
            } else {
                params = new Object[parameterCount];
                params[0] = Converter.convertIfNecessary(value, method.getParameterTypes()[0]);
            }
            return invokes(method, params);
        }

        /**
         * 关闭
         *
         * @return 消息
         */
        public String onClose() {
            Method method = stomps.get("close");
            return invokes(method, new Object[method.getParameterCount()]);
        }

        /**
         * 异常
         *
         * @param throwable 异常
         * @return 消息
         */
        public String onError(Throwable throwable) {
            Method method = stomps.get("error");
            return invokes(method, new Object[]{throwable});
        }

        /**
         * 执行
         *
         * @param method  方法
         * @param objects 参数
         * @return 结果
         */
        private String invokes(Method method, Object[] objects) {
            if (null == method) {
                return null;
            }
            Object invoke = null;
            try {
                invoke = method.invoke(object, new Object[method.getParameterCount()]);
            } catch (Exception ignored) {
            }
            if (null == invoke) {
                return null;
            }
            if (invoke instanceof String) {
                return invoke.toString();
            }

            return Converter.convertIfNecessary(invoke, String.class);
        }

        /**
         * 连接成功
         *
         * @return 消息
         */
        public String onOpen() {
            Method method = stomps.get("open");
            return invokes(method, new Object[method.getParameterCount()]);
        }
    }
}
