package com.chua.common.support.mapping.invoke.hik;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.constant.NameConstant;
import com.chua.common.support.http.HttpMethod;
import com.chua.common.support.http.HttpResponse;
import com.chua.common.support.json.Json;
import com.chua.common.support.mapping.MappingConfig;
import com.chua.common.support.mapping.Request;
import com.chua.common.support.mapping.invoke.AbstractHttpInvoker;
import com.chua.common.support.mapping.invoke.hik.constant.Constants;
import com.chua.common.support.mapping.invoke.hik.util.HikRequest;
import com.chua.common.support.mapping.invoke.hik.util.HikResponse;
import com.chua.common.support.mapping.invoke.hik.util.MessageDigestUtil;
import com.chua.common.support.net.NetAddress;
import com.chua.common.support.utils.MapUtils;
import com.chua.common.support.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

import static com.chua.common.support.constant.NameConstant.GET;
import static com.chua.common.support.constant.NameConstant.POST;
import static com.chua.common.support.constant.NameConstant.PUT;
import static com.chua.common.support.constant.NameConstant.*;
import static com.chua.common.support.http.HttpConstant.*;
import static com.chua.common.support.http.HttpMethod.*;

/**
 * 海康执行器
 *
 * @author CH
 */
@Slf4j
@Spi("hik")
public class HikInvoker extends AbstractHttpInvoker {


    /**
     * 调用网关成功的标志,标志位
     */
    private static final String SUCCESS_PRE = "2";

    /**
     * 调用网关重定向的标志,标志位
     */
    private static final String REDIRECT_PRE = "3";

    public HikInvoker(MappingConfig mappingConfig) {
        super(mappingConfig);
    }

    @Override
    public Object execute(String url, Request request) {
        Map<String, String> path = analysisPath(url);
        String method = request.getMethod();
        if (GET.equalsIgnoreCase(method)) {
            return doGetArtemis(path, MapUtils.asStringMap(request.getBody()), null, APPLICATION_JSON, request);
        }

        if (POST.equalsIgnoreCase(method)) {
            return doPostStringArtemis(path, Json.toJson(request.getBody()), null, null, APPLICATION_JSON, request);
        }

        if (PUT.equalsIgnoreCase(method)) {
            return doPutStringArtemis(path, Json.toJson(request.getBody()), null, APPLICATION_JSON, request);
        }

        if (NameConstant.DELETE.equalsIgnoreCase(method)) {
            return doDeleteArtemis(path, MapUtils.asStringMap(request.getBody()), null, APPLICATION_JSON, request);
        }
        return null;
    }

    /**
     * 分析地址
     *
     * @param url 链接
     * @return path
     */
    private Map<String, String> analysisPath(String url) {
        Map<String, String> path = new HashMap<>(mappingConfig.getProtocol().length);
        for (String s : mappingConfig.getProtocol()) {
            path.put(s.concat("://"), url);
        }
        return path;
    }

    /**
     * get请求
     *
     * @param path        artemis配置的get请求的路径 是一个数组长度为1的Hashmap集合，只存一组数据，key为http的请求方式，value为host后面的path路径。
     * @param querys      map类型  get请求的url查询参数（url中的query参数,没有就是为空）形如 "?aa=1&&bb=2"形式参数变成map键值对 query.put("aa","1");query.put("bb","2")
     * @param accept      指定客户端能够接收的内容类型，该参数传空时的默认全部类型接受
     * @param contentType 请求的与实体对应的MIME信息，该参数传空时的取默认值
     * @param header      头球
     * @return {@link String}
     */

    public String doGetArtemis(Map<String, String> path, Map<String, String> querys, String accept, String contentType, Map<String, String> header) {
        String httpSchema = (String) path.keySet().toArray()[0];
        if (httpSchema == null || StringUtils.isEmpty(httpSchema)) {
            throw new RuntimeException(HTTP_SCHEMA_ERROR + "httpSchema: " + httpSchema);
        }

        String responseStr = null;
        try {
            Map<String, String> headers = new HashMap<String, String>(1 << 4);
            //（必填）根据期望的Response内容类型设置
            if (StringUtils.isNotBlank(accept)) {
                headers.put(HTTP_HEADER_ACCEPT, accept);
            } else {
                headers.put(HTTP_HEADER_ACCEPT, "*/*");
            }
            //请求的与实体对应的MIME信息
            if (StringUtils.isNotBlank(contentType)) {
                headers.put(HTTP_HEADER_CONTENT_TYPE, contentType);
            } else {
                headers.put(HTTP_HEADER_CONTENT_TYPE, CONTENT_TYPE_TEXT);
            }
            if (header != null) {
                headers.putAll(header);
            }
            String url = path.get(httpSchema);
            log.info(url);
            NetAddress netAddress = NetAddress.of(url);

            HikRequest request = new HikRequest(HttpMethod.GET, httpSchema + netAddress.getAddress(),
                    netAddress.getPath(), mappingConfig.getAppKey(), mappingConfig.getSecretAccessKey(), Constants.DEFAULT_TIMEOUT);
            request.setHeaders(headers);

            request.setQuerys(querys);
            //调用服务端
            HikResponse response = Client.execute(request);
            responseStr = response.getBody();
        } catch (Exception e) {
            log.error("the Artemis GET Request is failed[doGetArtemis]", e);
        }
        return responseStr;
    }


    /**
     * get请求
     *
     * @param path        artemis配置的get请求的路径 是一个数组长度为1的Hashmap集合，只存一组数据，key为http的请求方式，value为host后面的path路径。
     * @param querys      map类型  get请求的url查询参数（url中的query参数,没有就是为空）形如 "?aa=1&&bb=2"形式参数变成map键值对 query.put("aa","1");query.put("bb","2")
     * @param accept      指定客户端能够接收的内容类型，该参数传空时的默认全部类型接受
     * @param contentType 请求的与实体对应的MIME信息，该参数传空时的取默认值
     * @param request1    要求
     * @return {@link String}
     */

    public String doGetArtemis(Map<String, String> path, Map<String, String> querys, String accept, String contentType, Request request1) {

        // 根据传入的path获取是请求是http还是https
        String httpSchema = (String) path.keySet().toArray()[0];

        if (httpSchema == null || StringUtils.isEmpty(httpSchema)) {
            throw new RuntimeException(HTTP_SCHEMA_ERROR + "httpSchema: " + httpSchema);
        }

        String responseStr = null;
        try {
            Map<String, String> headers = new HashMap<String, String>(1 << 4);
            //（必填）根据期望的Response内容类型设置
            if (StringUtils.isNotBlank(accept)) {
                headers.put(HTTP_HEADER_ACCEPT, accept);
            } else {
                headers.put(HTTP_HEADER_ACCEPT, "*/*");
            }
            //请求的与实体对应的MIME信息
            if (StringUtils.isNotBlank(contentType)) {
                headers.put(HTTP_HEADER_CONTENT_TYPE, contentType);
            } else {
                headers.put(HTTP_HEADER_CONTENT_TYPE, CONTENT_TYPE_TEXT);
            }

            String url = path.get(httpSchema);
            log.info(url);
            NetAddress netAddress = NetAddress.of(url);

            HikRequest request = new HikRequest(HttpMethod.GET, httpSchema + netAddress.getAddress(),
                    netAddress.getPath(), mappingConfig.getAppKey(), mappingConfig.getSecretAccessKey(), request1.getReadTimeout());
            request.setHeaders(headers);

            request.setQuerys(querys);
            //调用服务端
            HikResponse response = Client.execute(request);
            responseStr = response.getBody();
        } catch (Exception e) {
            log.error("the Artemis GET Request is failed[doGetArtemis]", e);
        }
        return responseStr;
    }

    /**
     * get
     *
     * @param path        artemis配置的get请求的路径 是一个数组长度为1的Hashmap集合，只存一组数据，key为http的请求方式，value为host后面的path路径。
     * @param querys      map类型  get请求的url查询参数（url中的query参数,没有就是为空）形如 "?aa=1&&bb=2"形式参数变成map键值对 query.put("aa","1");query.put("bb","2")
     * @param accept      指定客户端能够接收的内容类型，该参数传空时的默认全部类型接受
     * @param contentType 请求的与实体对应的MIME信息，该参数传空时的取默认值
     * @param header      请求参数有header以map的方式,没有则为null
     * @param request1    请求1
     * @return GET图片下载类型 HttpResponse类型
     */
    public HttpResponse doGetResponse(Map<String, String> path,
                                      Map<String, String> querys,
                                      String accept,
                                      String contentType,
                                      Map<String, String> header,
                                      Request request1
                                      ) {


        /**
         * 根据传入的path获取是请求是http还是https
         */
        String httpSchema = (String) path.keySet().toArray()[0];

        if (httpSchema == null || StringUtils.isEmpty(httpSchema)) {
            throw new RuntimeException(HTTP_SCHEMA_ERROR + "httpSchema: " + httpSchema);
        }

        HttpResponse httpResponse = null;
        try {
            Map<String, String> headers = new HashMap<String, String>(1 << 4);
            //（必填）根据期望的Response内容类型设置
            if (StringUtils.isNotBlank(accept)) {
                headers.put(HTTP_HEADER_ACCEPT, accept);
            } else {
                headers.put(HTTP_HEADER_ACCEPT, "*/*");
            }
            //请求的与实体对应的MIME信息
            if (StringUtils.isNotBlank(contentType)) {
                headers.put(HTTP_HEADER_CONTENT_TYPE, contentType);
            } else {
                headers.put(HTTP_HEADER_CONTENT_TYPE, CONTENT_TYPE_TEXT);
            }
            if (header != null) {
                headers.putAll(header);
            }

            String url = path.get(httpSchema);
            log.info(url);
            NetAddress netAddress = NetAddress.of(url);

            HikRequest request = new HikRequest(GET_RESPONSE, httpSchema + netAddress.getAddress(),
                    netAddress.getPath(), mappingConfig.getAppKey(), mappingConfig.getSecretAccessKey(), request1.getReadTimeout());
            request.setHeaders(headers);

            request.setQuerys(querys);
            //调用服务端
            HikResponse response = Client.execute(request);

            httpResponse = response.getResponse();

        } catch (Exception e) {
            log.error("the Artemis GET Request is failed[doGetArtemis]", e);
        }

        return httpResponse;

    }


    /**
     * postForm请求，postForm请求包含query参数和form表单参数
     *
     * @param path        artemis配置的get请求的路径 是一个数组长度为1的Hashmap集合，只存一组数据，key为http的请求方式，value为host后面的path路径。
     * @param paramMap    Form表单请求的参数，键值对形式的map
     * @param querys      map类型  post请求的url查询参数（url中的query参数,没有就是为空）形如 "?aa=1&&bb=2"形式参数变成map键值对 query.put("aa","1");query.put("bb","2")
     * @param accept      指定客户端能够接收的内容类型，该参数传空时的默认全部类型接受
     * @param contentType 请求的与实体对应的MIME信息，该参数传空时的取默认值("application/x-www-form-urlencoded; charset=UTF-8")
     * @param header      请求参数有header以map的方式,没有则为null
     * @param request1 请求
     * @return 返回表单post请求, 返回字符串类型
     */

    public String doPostFormArtemis(Map<String, String> path,
                                    Map<String, String> paramMap,
                                    Map<String, String> querys,
                                    String accept,
                                    String contentType,
                                    Map<String, String> header,
                                    Request request1
                                    ) {
        //根据传入的path获取是请求是http还是https
        String httpSchema = (String) path.keySet().toArray()[0];

        if (httpSchema == null || StringUtils.isEmpty(httpSchema)) {
            throw new RuntimeException(HTTP_SCHEMA_ERROR + "httpSchema: " + httpSchema);
        }
        String responseStr = null;
        try {
            Map<String, String> headers = new HashMap<String, String>(1 << 4);
            //（必填）根据期望的Response内容类型设置
            if (StringUtils.isNotBlank(accept)) {
                headers.put(HTTP_HEADER_ACCEPT, accept);
            } else {
                headers.put(HTTP_HEADER_ACCEPT, "*/*");
            }
            //请求的与实体对应的MIME信息
            if (StringUtils.isNotBlank(contentType)) {
                headers.put(HTTP_HEADER_CONTENT_TYPE, contentType);
            } else {
                headers.put(HTTP_HEADER_CONTENT_TYPE, CONTENT_TYPE_FORM);
            }
            if (header != null) {
                headers.putAll(header);
            }
            String url = path.get(httpSchema);
            log.info(url);
            NetAddress netAddress = NetAddress.of(url);

            HikRequest request = new HikRequest(POST_FORM, httpSchema + netAddress.getAddress(),
                    netAddress.getPath(), mappingConfig.getAppKey(), mappingConfig.getSecretAccessKey(), request1.getReadTimeout());
            request.setHeaders(headers);
            //postForm请求的query参数
            request.setQuerys(querys);
            //postForm请求的表单参数
            request.setBodys(paramMap);

            //调用服务端
            HikResponse response = Client.execute(request);

            responseStr = getResponseResult(response);

        } catch (Exception e) {
            log.error("the Artemis PostForm Request is failed[doPostFormArtemis]", e);
        }
        return responseStr;
    }


    /**
     * postForm请求，postForm请求包含query参数和form表单参数
     *
     * @param path        artemis配置的get请求的路径 是一个数组长度为1的Hashmap集合，只存一组数据，key为http的请求方式，value为host后面的path路径。
     * @param paramMap    Form表单请求的参数，键值对形式的map
     * @param querys      map类型  post请求的url查询参数（url中的query参数,没有就是为空）形如 "?aa=1&&bb=2"形式参数变成map键值对 query.put("aa","1");query.put("bb","2")
     * @param accept      指定客户端能够接收的内容类型，该参数传空时的默认全部类型接受
     * @param contentType 请求的与实体对应的MIME信息，该参数传空时的取默认值("application/x-www-form-urlencoded; charset=UTF-8")
     * @param request1 请求
     * @return 返回表单post请求, 返回字符串类型
     */

    public String doPostFormArtemis(Map<String, String> path,
                                    Map<String, String> paramMap,
                                    Map<String, String> querys,
                                    String accept,
                                    String contentType,
                                    Request request1
                                    ) {
        String httpSchema = (String) path.keySet().toArray()[0];

        if (httpSchema == null || StringUtils.isEmpty(httpSchema)) {
            throw new RuntimeException(HTTP_SCHEMA_ERROR + "httpSchema: " + httpSchema);
        }
        String responseStr = null;
        try {
            Map<String, String> headers = new HashMap<String, String>(1 << 4);
            //（必填）根据期望的Response内容类型设置
            if (StringUtils.isNotBlank(accept)) {
                headers.put(HTTP_HEADER_ACCEPT, accept);
            } else {
                headers.put(HTTP_HEADER_ACCEPT, "*/*");
            }
            //请求的与实体对应的MIME信息
            if (StringUtils.isNotBlank(contentType)) {
                headers.put(HTTP_HEADER_CONTENT_TYPE, contentType);
            } else {
                headers.put(HTTP_HEADER_CONTENT_TYPE, CONTENT_TYPE_FORM);
            }

            String url = path.get(httpSchema);
            log.info(url);
            NetAddress netAddress = NetAddress.of(url);

            HikRequest request = new HikRequest(POST_FORM, httpSchema + netAddress.getAddress(),
                    netAddress.getPath(), mappingConfig.getAppKey(), mappingConfig.getSecretAccessKey(), request1.getReadTimeout());
            request.setHeaders(headers);
            //postForm请求的query参数
            request.setQuerys(querys);
            //postForm请求的表单参数
            request.setBodys(paramMap);

            //调用服务端
            HikResponse response = Client.execute(request);

            responseStr = getResponseResult(response);

        } catch (Exception e) {
            log.error("the Artemis PostForm Request is failed[doPostFormArtemis]", e);
        }
        return responseStr;
    }

    /**
     * postformImg请求
     */

    /**
     * @param path        artemis配置的get请求的路径 是一个数组长度为1的Hashmap集合，只存一组数据，key为http的请求方式，value为host后面的path路径。
     * @param paramMap    Form表单请求的参数，键值对形式的map
     * @param querys      map类型  post请求的url查询参数（url中的query参数,没有就是为空）形如 "?aa=1&&bb=2"形式参数变成map键值对 query.put("aa","1");query.put("bb","2")
     * @param accept      指定客户端能够接收的内容类型，该参数传空时的默认全部类型接受
     * @param contentType 请求的与实体对应的MIME信息，该参数传空时的取默认值("application/x-www-form-urlencoded; charset=UTF-8")
     * @param header      请求参数有header以map的方式,没有则为null
     * @param request1 请求
     * @return POST表单类型图片下载接口  HttpResponse类型
     */
    public HttpResponse doPostFormImgArtemis(Map<String, String> path,
                                             Map<String, String> paramMap,
                                             Map<String, String> querys,
                                             String accept,
                                             String contentType,
                                             Map<String, String> header,
                                             Request request1
                                             ) {
        /*
          根据传入的path获取是请求是http还是https
         */
        String httpSchema = (String) path.keySet().toArray()[0];

        if (httpSchema == null || StringUtils.isEmpty(httpSchema)) {
            throw new RuntimeException(HTTP_SCHEMA_ERROR + "httpSchema: " + httpSchema);
        }
        HttpResponse response = null;
        try {
            Map<String, String> headers = new HashMap<String, String>(1 << 4);
            //（必填）根据期望的Response内容类型设置
            if (StringUtils.isNotBlank(accept)) {
                headers.put(HTTP_HEADER_ACCEPT, accept);
            } else {
                headers.put(HTTP_HEADER_ACCEPT, "*/*");
            }
            //请求的与实体对应的MIME信息
            if (StringUtils.isNotBlank(contentType)) {
                headers.put(HTTP_HEADER_CONTENT_TYPE, contentType);
            } else {
                headers.put(HTTP_HEADER_CONTENT_TYPE, CONTENT_TYPE_FORM);
            }
            if (header != null) {
                headers.putAll(header);
            }
            String url = path.get(httpSchema);
            log.info(url);
            NetAddress netAddress = NetAddress.of(url);

            HikRequest request = new HikRequest(POST_FORM_RESPONSE, httpSchema + netAddress.getAddress(),
                    netAddress.getPath(), mappingConfig.getAppKey(), mappingConfig.getSecretAccessKey(), request1.getReadTimeout());
            request.setHeaders(headers);
            //postForm请求的query参数
            request.setQuerys(querys);
            //postForm请求的表单参数
            request.setBodys(paramMap);

            //调用服务端
            HikResponse response1 = Client.execute(request);

            response = response1.getResponse();

        } catch (Exception e) {
            log.error("the Artemis PostForm Request is failed[doPostFormImgArtemis]", e);
        }
        return response;
    }


    /**
     * postString请求
     *
     * @param path        artemis配置的get请求的路径 是一个数组长度为1的Hashmap集合，只存一组数据，key为http的请求方式，value为host后面的path路径。
     * @param body        postString String请求体
     * @param querys      map类型  post请求的url查询参数（url中的query参数,没有就是为空）形如 "?aa=1&&bb=2"形式参数变成map键值对 query.put("aa","1");query.put("bb","2")
     * @param accept      指定客户端能够接收的内容类型，该参数传空时的默认全部类型接受
     * @param contentType 请求的与实体对应的MIME信息，该参数传空时的取默认值("application/text; charset=UTF-8")
     * @param header      header参数,无过没有值为null
     * @param request1 请求
     * @return POST json类型接口  返回字符串类型
     */


    public String doPostStringArtemis(Map<String, String> path,
                                      String body,
                                      Map<String, String> querys,
                                      String accept,
                                      String contentType,
                                      Map<String, String> header,
                                      Request request1

    ) {
        String httpSchema = (String) path.keySet().toArray()[0];

        if (httpSchema == null || StringUtils.isEmpty(httpSchema)) {
            throw new RuntimeException(HTTP_SCHEMA_ERROR + "httpSchema: " + httpSchema);
        }
        String responseStr = null;
        try {

            Map<String, String> headers = new HashMap<String, String>(1 << 4);
            //（必填）根据期望的Response内容类型设置
            if (StringUtils.isNotBlank(accept)) {
                headers.put(HTTP_HEADER_ACCEPT, accept);
            } else {
                headers.put(HTTP_HEADER_ACCEPT, "*/*");
            }
            //（POST/PUT请求必选）请求Body内容格式请求的与实体对应的MIME信息
            if (StringUtils.isNotBlank(contentType)) {
                headers.put(HTTP_HEADER_CONTENT_TYPE, contentType);
            } else {
                headers.put(HTTP_HEADER_CONTENT_TYPE, CONTENT_TYPE_TEXT);
            }
            if (header != null) {
                headers.putAll(header);
            }

            String url = path.get(httpSchema);
            log.info(url);
            NetAddress netAddress = NetAddress.of(url);

            HikRequest request = new HikRequest(POST_STRING, httpSchema + netAddress.getAddress(),
                    netAddress.getPath(), mappingConfig.getAppKey(), mappingConfig.getSecretAccessKey(), request1.getReadTimeout());
            request.setHeaders(headers);
            //请求的query
            request.setQuerys(querys);
            //请求的bodyString
            request.setStringBody(body);
            //调用服务端
            HikResponse response = Client.execute(request);

            responseStr = getResponseResult(response);

        } catch (Exception e) {
            log.error("the Artemis PostString Request is failed[doPostStringArtemis]", e);
        }
        return responseStr;
    }


    /**
     * postString请求
     *
     * @param path        artemis配置的get请求的路径 是一个数组长度为1的Hashmap集合，只存一组数据，key为http的请求方式，value为host后面的path路径。
     * @param body        postString String请求体
     * @param querys      map类型  post请求的url查询参数（url中的query参数,没有就是为空）形如 "?aa=1&&bb=2"形式参数变成map键值对 query.put("aa","1");query.put("bb","2")
     * @param accept      指定客户端能够接收的内容类型，该参数传空时的默认全部类型接受
     * @param contentType 请求的与实体对应的MIME信息，该参数传空时的取默认值("application/text; charset=UTF-8")
     * @param request1    请求1
     * @return POST json类型接口  返回字符串类型
     */


    public String doPostStringArtemis(Map<String, String> path, String body,
                                      Map<String, String> querys, String accept, String contentType, Request request1) {
        // 根据传入的path获取是请求是http还是https
        String httpSchema = (String) path.keySet().toArray()[0];

        if (httpSchema == null || StringUtils.isEmpty(httpSchema)) {
            throw new RuntimeException(HTTP_SCHEMA_ERROR + "httpSchema: " + httpSchema);
        }
        String responseStr = null;
        try {

            Map<String, String> headers = new HashMap<String, String>(1 << 4);
            //（必填）根据期望的Response内容类型设置
            if (StringUtils.isNotBlank(accept)) {
                headers.put(HTTP_HEADER_ACCEPT, accept);
            } else {
                headers.put(HTTP_HEADER_ACCEPT, "*/*");
            }
            //（POST/PUT请求必选）请求Body内容格式请求的与实体对应的MIME信息
            if (StringUtils.isNotBlank(contentType)) {
                headers.put(HTTP_HEADER_CONTENT_TYPE, contentType);
            } else {
                headers.put(HTTP_HEADER_CONTENT_TYPE, CONTENT_TYPE_TEXT);
            }
            String url = path.get(httpSchema);
            log.info(url);
            NetAddress netAddress = NetAddress.of(url);

            HikRequest request = new HikRequest(POST_STRING, httpSchema + netAddress.getAddress(),
                    netAddress.getPath(), mappingConfig.getAppKey(), mappingConfig.getSecretAccessKey(), request1.getReadTimeout());
            request.setHeaders(headers);
            //请求的query
            request.setQuerys(querys);
            //请求的bodyString
            request.setStringBody(body);
            //调用服务端
            HikResponse response = Client.execute(request);

            responseStr = getResponseResult(response);

        } catch (Exception e) {
            log.error("the Artemis PostString Request is failed[doPostStringArtemis]", e);
        }
        return responseStr;
    }

    /**
     * postString请求
     *
     * @param path        artemis配置的get请求的路径 是一个数组长度为1的Hashmap集合，只存一组数据，key为http的请求方式，value为host后面的path路径。
     * @param body        postString String请求体
     * @param querys      map类型  post请求的url查询参数（url中的query参数,没有就是为空）形如 "?aa=1&&bb=2"形式参数变成map键值对 query.put("aa","1");query.put("bb","2")
     * @param accept      指定客户端能够接收的内容类型，该参数传空时的默认全部类型接受
     * @param contentType 请求的与实体对应的MIME信息，该参数传空时的取默认值("application/x-www-form-urlencoded; charset=UTF-8")
     * @param header      请求参数有header以map的方式,没有则为null
     * @param request1    请求
     * @return POST json请求类型图片下载接口  HttpResponse类型
     */
    public HttpResponse doPostStringImgArtemis(Map<String, String> path, String body,
                                               Map<String, String> querys, String accept, String contentType, Map<String, String> header, Request request1) {
        /**
         * 根据传入的path获取是请求是http还是https
         */
        String httpSchema = (String) path.keySet().toArray()[0];

        if (httpSchema == null || StringUtils.isEmpty(httpSchema)) {
            throw new RuntimeException(HTTP_SCHEMA_ERROR + "httpSchema: " + httpSchema);
        }
        HttpResponse responseStr = null;
        try {

            Map<String, String> headers = new HashMap<String, String>(1 << 4);
            //（必填）根据期望的Response内容类型设置
            if (StringUtils.isNotBlank(accept)) {
                headers.put(HTTP_HEADER_ACCEPT, accept);
            } else {
                headers.put(HTTP_HEADER_ACCEPT, "*/*");
            }
            //（POST/PUT请求必选）请求Body内容格式请求的与实体对应的MIME信息
            if (StringUtils.isNotBlank(contentType)) {
                headers.put(HTTP_HEADER_CONTENT_TYPE, contentType);
            } else {
                headers.put(HTTP_HEADER_CONTENT_TYPE, CONTENT_TYPE_TEXT);
            }
            if (header != null) {
                headers.putAll(header);
            }
            String url = path.get(httpSchema);
            log.info(url);
            NetAddress netAddress = NetAddress.of(url);

            HikRequest request = new HikRequest(POST_STRING_RESPONSE, httpSchema + netAddress.getAddress(),
                    netAddress.getPath(), mappingConfig.getAppKey(), mappingConfig.getSecretAccessKey(), request1.getReadTimeout());
            request.setHeaders(headers);
            //请求的query
            request.setQuerys(querys);
            //请求的bodyString
            request.setStringBody(body);
            //调用服务端
            HikResponse response = Client.execute(request);

            responseStr = response.getResponse();

        } catch (Exception e) {
            log.error("the Artemis PostString Request is failed[doPostStringArtemis]", e);
        }
        return responseStr;
    }

    /**
     * postBytes请求
     *
     * @param path        artemis配置的get请求的路径 是一个数组长度为1的Hashmap集合，只存一组数据，key为http的请求方式，value为host后面的path路径。
     * @param bytesBody   请求体，byte字节
     * @param querys      map类型  post请求的url查询参数（url中的query参数,没有就是为空）形如 "?aa=1&&bb=2"形式参数变成map键值对 query.put("aa","1");query.put("bb","2")
     * @param accept      指定客户端能够接收的内容类型，该参数传空时的默认全部类型接受
     * @param contentType 请求的与实体对应的MIME信息，该参数传空时的取默认值("application/text; charset=UTF-8")
     * @param request1 请求
     * @return string
     */
    public String doPostBytesArtemis(Map<String, String> path,
                                     byte[] bytesBody,
                                     Map<String, String> querys,
                                     String accept,
                                     String contentType,
                                     Map<String, String> header,
                                     Request request1
                                     ) {
        String httpSchema = (String) path.keySet().toArray()[0];

        if (httpSchema == null || StringUtils.isEmpty(httpSchema)) {
            throw new RuntimeException(HTTP_SCHEMA_ERROR + "httpSchema: " + httpSchema);
        }

        String responseStr = null;
        try {
            Map<String, String> headers = new HashMap<String, String>(1 << 4);
            //（必填）根据期望的Response内容类型设置
            if (StringUtils.isNotBlank(accept)) {
                headers.put(HTTP_HEADER_ACCEPT, accept);
            } else {
                headers.put(HTTP_HEADER_ACCEPT, "*/*");
            }
            //（可选）Body MD5,服务端会校验Body内容是否被篡改,建议Body非Form表单时添加此Header
            if (bytesBody != null) {
                headers.put(HTTP_HEADER_CONTENT_MD5, MessageDigestUtil.base64AndMd5(bytesBody));
            }
            //（POST/PUT请求必选）请求Body内容格式
            if (StringUtils.isNotBlank(contentType)) {
                headers.put(HTTP_HEADER_CONTENT_TYPE, contentType);
            } else {
                headers.put(HTTP_HEADER_CONTENT_TYPE, CONTENT_TYPE_TEXT);
            }
            if (header != null) {
                headers.putAll(header);
            }

            String url = path.get(httpSchema);
            log.info(url);
            NetAddress netAddress = NetAddress.of(url);

            HikRequest request = new HikRequest(POST_BYTES, httpSchema + netAddress.getAddress(),
                    netAddress.getPath(), mappingConfig.getAppKey(), mappingConfig.getSecretAccessKey(), request1.getReadTimeout());

            request.setHeaders(headers);
            //请求的query
            request.setQuerys(querys);
            request.setBytesBody(bytesBody);
            //调用服务端
            HikResponse response = Client.execute(request);

            responseStr = getResponseResult(response);
        } catch (Exception e) {
            log.error("the Artemis PostBytes Request is failed[doPostBytesArtemis]", e);
        }
        return responseStr;
    }


    /**
     * postBytes请求
     *
     * @param path        artemis配置的get请求的路径 是一个数组长度为1的Hashmap集合，只存一组数据，key为http的请求方式，value为host后面的path路径。
     * @param bytesBody   请求体，byte字节
     * @param querys      map类型  post请求的url查询参数（url中的query参数,没有就是为空）形如 "?aa=1&&bb=2"形式参数变成map键值对 query.put("aa","1");query.put("bb","2")
     * @param accept      指定客户端能够接收的内容类型，该参数传空时的默认全部类型接受
     * @param contentType 请求的与实体对应的MIME信息，该参数传空时的取默认值("application/text; charset=UTF-8")
     * @param request1 请求
     * @return str
     */
    public String doPostBytesArtemis(Map<String, String> path,
                                     byte[] bytesBody,
                                     Map<String, String> querys,
                                     String accept,
                                     String contentType,
                                     Request request1
                                     ) {
        String httpSchema = (String) path.keySet().toArray()[0];

        if (httpSchema == null || StringUtils.isEmpty(httpSchema)) {
            throw new RuntimeException(HTTP_SCHEMA_ERROR + "httpSchema: " + httpSchema);
        }

        String responseStr = null;
        try {
            Map<String, String> headers = new HashMap<String, String>(1 << 4);
            //（必填）根据期望的Response内容类型设置
            if (StringUtils.isNotBlank(accept)) {
                headers.put(HTTP_HEADER_ACCEPT, accept);
            } else {
                headers.put(HTTP_HEADER_ACCEPT, "*/*");
            }
            //（可选）Body MD5,服务端会校验Body内容是否被篡改,建议Body非Form表单时添加此Header
            if (bytesBody != null) {
                headers.put(HTTP_HEADER_CONTENT_MD5, MessageDigestUtil.base64AndMd5(bytesBody));
            }
            //（POST/PUT请求必选）请求Body内容格式
            if (StringUtils.isNotBlank(contentType)) {
                headers.put(HTTP_HEADER_CONTENT_TYPE, contentType);
            } else {
                headers.put(HTTP_HEADER_CONTENT_TYPE, CONTENT_TYPE_TEXT);
            }

            String url = path.get(httpSchema);
            log.info(url);
            NetAddress netAddress = NetAddress.of(url);

            HikRequest request = new HikRequest(POST_BYTES, httpSchema + netAddress.getAddress(),
                    netAddress.getPath(), mappingConfig.getAppKey(), mappingConfig.getSecretAccessKey(), request1.getReadTimeout());

            request.setHeaders(headers);
            //请求的query
            request.setQuerys(querys);
            request.setBytesBody(bytesBody);
            //调用服务端
            HikResponse response = Client.execute(request);

            responseStr = getResponseResult(response);
        } catch (Exception e) {
            log.error("the Artemis PostBytes Request is failed[doPostBytesArtemis]", e);
        }
        return responseStr;
    }


    /**
     * putString请求
     *
     * @param path        artemis配置的get请求的路径 是一个数组长度为1的Hashmap集合，只存一组数据，key为http的请求方式，value为host后面的path路径。
     * @param body        putString String请求体
     * @param accept      指定客户端能够接收的内容类型，该参数传空时的默认全部类型接受
     * @param contentType 请求的与实体对应的MIME信息，该参数传空时的取默认值("application/text; charset=UTF-8")
     * @param request1 请求
     * @return str
     */
    public String doPutStringArtemis(Map<String, String> path, String body, String accept, String contentType, Request request1) {
        String httpSchema = (String) path.keySet().toArray()[0];

        if (httpSchema == null || StringUtils.isEmpty(httpSchema)) {
            throw new RuntimeException(HTTP_SCHEMA_ERROR + "httpSchema: " + httpSchema);
        }

        String responseStr = null;
        try {
            Map<String, String> headers = new HashMap<String, String>(1 << 4);
            //（必填）根据期望的Response内容类型设置
            if (StringUtils.isNotBlank(accept)) {
                headers.put(HTTP_HEADER_ACCEPT, accept);
            } else {
                headers.put(HTTP_HEADER_ACCEPT, "*/*");
            }
            //（可选）Body MD5,服务端会校验Body内容是否被篡改,建议Body非Form表单时添加此Header
            if (StringUtils.isNotBlank(body)) {
                headers.put(HTTP_HEADER_CONTENT_MD5, MessageDigestUtil.base64AndMd5(body));
            }
            //（POST/PUT请求必选）请求Body内容格式
            if (StringUtils.isNotBlank(contentType)) {
                headers.put(HTTP_HEADER_CONTENT_TYPE, contentType);
            } else {
                headers.put(HTTP_HEADER_CONTENT_TYPE, CONTENT_TYPE_TEXT);
            }

            String url = path.get(httpSchema);
            log.info(url);
            NetAddress netAddress = NetAddress.of(url);

            HikRequest request = new HikRequest(PUT_STRING, httpSchema + netAddress.getAddress(),
                    netAddress.getPath(), mappingConfig.getAppKey(), mappingConfig.getSecretAccessKey(), request1.getReadTimeout());

            request.setHeaders(headers);
            request.setStringBody(body);

            //调用服务端
            HikResponse response = Client.execute(request);

            responseStr = getResponseResult(response);

        } catch (Exception e) {
            log.error("the Artemis PutString Request is failed[doPutStringArtemis]", e);
        }
        return responseStr;
    }

    /**
     * putBytes请求
     *
     * @param path        artemis配置的putBytes请求的路径
     * @param bytesBody   请求体，byte字节
     * @param accept      指定客户端能够接收的内容类型，该参数传空时的默认全部类型接受
     * @param contentType 请求的与实体对应的MIME信息，该参数传空时的取默认值("application/text; charset=UTF-8")
     * @param request1 请求
     * @return str
     */
    public String doPutBytesArtemis(Map<String, String> path, byte[] bytesBody, String accept, String contentType, Request request1) {

        String httpSchema = (String) path.keySet().toArray()[0];

        if (httpSchema == null || StringUtils.isEmpty(httpSchema)) {
            throw new RuntimeException(HTTP_SCHEMA_ERROR + "httpSchema: " + httpSchema);
        }

        String responseStr = null;
        try {
            Map<String, String> headers = new HashMap<String, String>(1 << 4);
            //（必填）根据期望的Response内容类型设置
            if (StringUtils.isNotBlank(accept)) {
                headers.put(HTTP_HEADER_ACCEPT, accept);
            } else {
                headers.put(HTTP_HEADER_ACCEPT, "*/*");
            }
            //（可选）Body MD5,服务端会校验Body内容是否被篡改,建议Body非Form表单时添加此Header
            if (bytesBody != null) {
                headers.put(HTTP_HEADER_CONTENT_MD5, MessageDigestUtil.base64AndMd5(bytesBody));
            }
            //（POST/PUT请求必选）请求Body内容格式
            if (StringUtils.isNotBlank(contentType)) {
                headers.put(HTTP_HEADER_CONTENT_TYPE, contentType);
            } else {
                headers.put(HTTP_HEADER_CONTENT_TYPE, CONTENT_TYPE_TEXT);
            }
            String url = path.get(httpSchema);
            log.info(url);
            NetAddress netAddress = NetAddress.of(url);

            HikRequest request = new HikRequest(PUT_BYTES, httpSchema + netAddress.getAddress(),
                    netAddress.getPath(), mappingConfig.getAppKey(), mappingConfig.getSecretAccessKey(), request1.getReadTimeout());

            request.setHeaders(headers);
            request.setBytesBody(bytesBody);
            //调用服务端
            HikResponse response = Client.execute(request);

            responseStr = getResponseResult(response);
        } catch (Exception e) {
            log.error("the Artemis PutBytes Request is failed[doPutBytesArtemis]", e);
        }
        return responseStr;
    }

    /**
     * delete请求
     *
     * @param path        artemis配置的get请求的路径 是一个数组长度为1的Hashmap集合，只存一组数据，key为http的请求方式，value为host后面的path路径。
     * @param querys      map类型  delete请求的url查询参数（url中的query参数,没有就是为空）形如 "?aa=1&&bb=2"形式参数变成map键值对 query.put("aa","1");query.put("bb","2")
     * @param accept      指定客户端能够接收的内容类型，该参数传空时的默认全部类型接受
     * @param contentType 请求的与实体对应的MIME信息，该参数传空时的取默认值
     * @param request1 请求
     * @return str
     */
    public String doDeleteArtemis(Map<String, String> path, Map<String, String> querys, String accept, String contentType, Request request1) {
        String httpSchema = (String) path.keySet().toArray()[0];

        if (httpSchema == null || StringUtils.isEmpty(httpSchema)) {
            throw new RuntimeException(HTTP_SCHEMA_ERROR + "httpSchema: " + httpSchema);
        }

        String responseStr = null;
        try {
            Map<String, String> headers = new HashMap<String, String>(1 << 4);
            //（必填）根据期望的Response内容类型设置
            if (StringUtils.isNotBlank(accept)) {
                headers.put(HTTP_HEADER_ACCEPT, accept);
            } else {
                headers.put(HTTP_HEADER_ACCEPT, "*/*");
            }
            //请求的与实体对应的MIME信息
            if (StringUtils.isNotBlank(contentType)) {
                headers.put(HTTP_HEADER_CONTENT_TYPE, contentType);
            }
            String url = path.get(httpSchema);
            log.info(url);
            NetAddress netAddress = NetAddress.of(url);

            HikRequest request = new HikRequest(HttpMethod.DELETE, httpSchema + netAddress.getAddress(),
                    netAddress.getPath(), mappingConfig.getAppKey(), mappingConfig.getSecretAccessKey(), request1.getReadTimeout());

            request.setHeaders(headers);
            request.setQuerys(querys);
            //调用服务端
            HikResponse response = Client.execute(request);

            responseStr = getResponseResult(response);
        } catch (Exception e) {
            log.error("the Artemis DELETE Request is failed[doDeleteArtemis]", e);
        }
        return responseStr;
    }

    /**
     * 获取响应结果
     *
     * @param response 回答
     * @return {@link String}
     */
    private String getResponseResult(HikResponse response) {
        String responseStr = null;

        int statusCode = response.getStatusCode();

        log.info("返回状态码: {}", statusCode);
        if (String.valueOf(statusCode).startsWith(SUCCESS_PRE) || String.valueOf(statusCode).startsWith(REDIRECT_PRE)) {
            responseStr = response.getBody();
            if(log.isDebugEnabled()) {
                log.debug("the Artemis Request is Success,statusCode:" + statusCode + " SuccessMsg:" + response.getBody());
            }

        } else {
            String msg = response.getErrorMessage();
            responseStr = response.getBody();
            log.error("the Artemis Request is Failed,statusCode:" + statusCode + " errorMsg:" + msg);
        }
        return responseStr;
    }
}
