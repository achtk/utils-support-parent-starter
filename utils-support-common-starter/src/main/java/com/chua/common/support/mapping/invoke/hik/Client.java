package com.chua.common.support.mapping.invoke.hik;

import com.chua.common.support.mapping.invoke.hik.util.HikRequest;
import com.chua.common.support.mapping.invoke.hik.util.HikResponse;

import static com.boren.biz.emergency.hik.util.HikHttpUtil.*;

/**
 * Client
 *
 * @author HIK
 * @since 2023/09/06
 */
public class Client {
    /**
     * 发送请求
     *
     * @param request request对象
     * @return Response
     * @throws Exception
     */
    public static HikResponse execute(HikRequest request) throws Exception {
        switch (request.getMethod()) {
            case GET:
                return httpGet(request.getHost(), request.getPath(),
                        request.getTimeout(),
                        request.getHeaders(),
                        request.getQuerys(),
                        request.getSignHeaderPrefixList(),
                        request.getAppKey(), request.getAppSecret());
            case GET_RESPONSE:
                return httpImgGet(request.getHost(), request.getPath(),
                        request.getTimeout(),
                        request.getHeaders(),
                        request.getQuerys(),
                        request.getSignHeaderPrefixList(),
                        request.getAppKey(), request.getAppSecret());
            case POST_FORM:
                return httpPost(request.getHost(), request.getPath(),
                        request.getTimeout(),
                        request.getHeaders(),
                        request.getQuerys(),
                        request.getBodys(),
                        request.getSignHeaderPrefixList(),
                        request.getAppKey(), request.getAppSecret());
            case POST_FORM_RESPONSE:
                return httpImgPost(request.getHost(), request.getPath(),
                        request.getTimeout(),
                        request.getHeaders(),
                        request.getQuerys(),
                        request.getBodys(),
                        request.getSignHeaderPrefixList(),
                        request.getAppKey(), request.getAppSecret());
            case POST_STRING:
                return httpPost(request.getHost(), request.getPath(),
                        request.getTimeout(),
                        request.getHeaders(),
                        request.getQuerys(),
                        request.getStringBody(),
                        request.getSignHeaderPrefixList(),
                        request.getAppKey(), request.getAppSecret());
            case POST_STRING_RESPONSE:
                return httpImgPost(request.getHost(), request.getPath(),
                        request.getTimeout(),
                        request.getHeaders(),
                        request.getQuerys(),
                        request.getStringBody(),
                        request.getSignHeaderPrefixList(),
                        request.getAppKey(), request.getAppSecret());
            case POST_BYTES:
                return httpPost(request.getHost(), request.getPath(),
                        request.getTimeout(),
                        request.getHeaders(),
                        request.getQuerys(),
                        request.getBytesBody(),
                        request.getSignHeaderPrefixList(),
                        request.getAppKey(), request.getAppSecret());
            case PUT_STRING:
                return HikHttpUtil.httpPut(request.getHost(), request.getPath(),
                        request.getTimeout(),
                        request.getHeaders(),
                        request.getQuerys(),
                        request.getStringBody(),
                        request.getSignHeaderPrefixList(),
                        request.getAppKey(), request.getAppSecret());
            case PUT_BYTES:
                return httpPut(request.getHost(), request.getPath(),
                        request.getTimeout(),
                        request.getHeaders(),
                        request.getQuerys(),
                        request.getBytesBody(),
                        request.getSignHeaderPrefixList(),
                        request.getAppKey(), request.getAppSecret());
            case DELETE:
                return HikHttpUtil.httpDelete(request.getHost(), request.getPath(),
                        request.getTimeout(),
                        request.getHeaders(),
                        request.getQuerys(),
                        request.getSignHeaderPrefixList(),
                        request.getAppKey(), request.getAppSecret());
            default:
                throw new IllegalArgumentException(String.format("unsupported method:%s", request.getMethod()));
        }
    }
}
