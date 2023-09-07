package com.chua.common.support.mapping.invoke.hik.util;

import com.chua.common.support.utils.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;

import static com.chua.common.support.http.HttpConstant.*;
import static com.chua.common.support.mapping.invoke.hik.constant.Constants.*;


/**
 * 签名工具
 *
 * @author HIK
 * @since 2023/09/06
 */
public class HikSignUtil {

    /**
     * 计算签名
     *
     * @param secret               APP密钥
     * @param method               HttpMethod
     * @param signHeaderPrefixList 自定义参与签名Header前缀
     * @param path                 路径
     * @param headers              headers
     * @param querys               查询
     * @param bodys                bodys
     * @return 签名后的字符串
     */
    public static String sign(String secret, String method, String path,
                              Map<String, String> headers,
                              Map<String, String> querys,
                              Map<String, String> bodys,
                              List<String> signHeaderPrefixList) {
        try {
            Mac hmacSha256 = Mac.getInstance(HMAC_SHA256);
            byte[] keyBytes = secret.getBytes(ENCODING);
            hmacSha256.init(new SecretKeySpec(keyBytes, 0, keyBytes.length, HMAC_SHA256));

            return new String(Base64.getEncoder().encode(
                    hmacSha256.doFinal(buildStringToSign(method, path, headers, querys, bodys, signHeaderPrefixList)
                            .getBytes(ENCODING))),
                    ENCODING);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 生成要签名字符串
     * 构建待签名字符串
     *
     * @param method               方法
     * @param path                 路径
     * @param querys               查询
     * @param signHeaderPrefixList 符号头前缀列表
     * @param headers              消息头
     * @param bodys                消息体
     * @return {@link String}
     */
    private static String buildStringToSign(String method, String path,
                                            Map<String, String> headers,
                                            Map<String, String> querys,
                                            Map<String, String> bodys,
                                            List<String> signHeaderPrefixList) {
        StringBuilder sb = new StringBuilder();

        sb.append(method.toUpperCase()).append(LF);
        if (null != headers) {
            if (null != headers.get(HTTP_HEADER_ACCEPT)) {
                sb.append(headers.get(HTTP_HEADER_ACCEPT));
                sb.append(LF);
            }

            if (null != headers.get(HTTP_HEADER_CONTENT_MD5)) {
                sb.append(headers.get(HTTP_HEADER_CONTENT_MD5));
                sb.append(LF);
            }

            if (null != headers.get(HTTP_HEADER_CONTENT_TYPE)) {
                sb.append(headers.get(HTTP_HEADER_CONTENT_TYPE));
                sb.append(LF);
            }

            if (null != headers.get(HTTP_HEADER_DATE)) {
                sb.append(headers.get(HTTP_HEADER_DATE));
                sb.append(LF);
            }
        }
        sb.append(buildHeaders(headers, signHeaderPrefixList));
        sb.append(buildResource(path, querys, bodys));
        return sb.toString();
    }

    /**
     * 构建资源
     * 构建待签名Path+Query+BODY
     *
     * @param path   路径
     * @param querys 查询
     * @param bodys  消息体
     * @return 待签名
     */
    private static String buildResource(String path, Map<String, String> querys, Map<String, String> bodys) {
        StringBuilder sb = new StringBuilder();

        if (!StringUtils.isBlank(path)) {
            sb.append(path);
        }
        Map<String, String> sortMap = new TreeMap<String, String>();
        if (null != querys) {
            for (Map.Entry<String, String> query : querys.entrySet()) {
                if (!StringUtils.isBlank(query.getKey())) {
                    sortMap.put(query.getKey(), query.getValue());
                }
            }
        }

        if (null != bodys) {
            for (Map.Entry<String, String> body : bodys.entrySet()) {
                if (!StringUtils.isBlank(body.getKey())) {
                    sortMap.put(body.getKey(), body.getValue());
                }
            }
        }

        StringBuilder sbParam = new StringBuilder();
        for (Map.Entry<String, String> item : sortMap.entrySet()) {
            if (!StringUtils.isBlank(item.getKey())) {
                if (0 < sbParam.length()) {
                    sbParam.append(SPE3);
                }
                sbParam.append(item.getKey());
                if (!StringUtils.isBlank(item.getValue())) {
                    sbParam.append(SPE4).append(item.getValue());
                }
            }
        }
        if (0 < sbParam.length()) {
            sb.append(SPE5);
            sb.append(sbParam);
        }

        return sb.toString();
    }

    /**
     * 构建待签名Http头
     *
     * @param headers              请求中所有的Http头
     * @param signHeaderPrefixList 自定义参与签名Header前缀
     * @return 待签名Http头
     */
    private static String buildHeaders(Map<String, String> headers, List<String> signHeaderPrefixList) {
        StringBuilder sb = new StringBuilder();

        if (null != signHeaderPrefixList) {
            signHeaderPrefixList.remove(X_CA_SIGNATURE);
            signHeaderPrefixList.remove(HTTP_HEADER_ACCEPT);
            signHeaderPrefixList.remove(HTTP_HEADER_CONTENT_MD5);
            signHeaderPrefixList.remove(HTTP_HEADER_CONTENT_TYPE);
            signHeaderPrefixList.remove(HTTP_HEADER_DATE);
            Collections.sort(signHeaderPrefixList);
        }
        if (null != headers) {
            Map<String, String> sortMap = new TreeMap<String, String>(headers);
            StringBuilder signHeadersStringBuilder = new StringBuilder();
            for (Map.Entry<String, String> header : sortMap.entrySet()) {
                if (isHeaderToSign(header.getKey(), signHeaderPrefixList)) {
                    sb.append(header.getKey());
                    sb.append(SPE2);
                    if (!StringUtils.isBlank(header.getValue())) {
                        sb.append(header.getValue());
                    }
                    sb.append(LF);
                    if (0 < signHeadersStringBuilder.length()) {
                        signHeadersStringBuilder.append(SPE1);
                    }
                    signHeadersStringBuilder.append(header.getKey());
                }
            }
            headers.put(X_CA_SIGNATURE_HEADERS, signHeadersStringBuilder.toString());
        }


        return sb.toString();
    }

    /**
     * Http头是否参与签名 return
     */
    private static boolean isHeaderToSign(String headerName, List<String> signHeaderPrefixList) {
        if (StringUtils.isBlank(headerName)) {
            return false;
        }

        if (headerName.startsWith(CA_HEADER_TO_SIGN_PREFIX_SYSTEM)) {
            return true;
        }

        if (null != signHeaderPrefixList) {
            for (String signHeaderPrefix : signHeaderPrefixList) {
                if (headerName.equalsIgnoreCase(signHeaderPrefix)) {
                    return true;
                }
            }
        }

        return false;
    }
}
