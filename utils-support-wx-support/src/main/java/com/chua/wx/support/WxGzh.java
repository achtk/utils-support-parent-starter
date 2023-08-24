package com.chua.wx.support;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.chua.common.support.extra.api.MessageResponse;
import com.chua.common.support.http.HttpClient;
import com.chua.common.support.http.HttpClientInvoker;
import com.chua.common.support.http.HttpResponse;
import com.chua.common.support.lang.code.ResultCode;
import com.chua.common.support.task.cache.CacheConfiguration;
import com.chua.common.support.task.cache.Cacheable;
import com.chua.common.support.utils.MapUtils;
import com.chua.common.support.utils.StringUtils;
import com.chua.common.support.utils.ThreadUtils;
import com.chua.common.support.value.TimeValue;
import com.chua.common.support.value.Value;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.chua.common.support.lang.code.ReturnResultCode.OK;
import static com.chua.common.support.lang.code.ReturnResultCode.SYSTEM_SERVER_BUSINESS;

/**
 * 微信公众号接口
 *
 * @author CH
 */
@Slf4j
public class WxGzh implements WxApi {

    public static final String VX_MINI_APP_ID = "VX_MINI_APP_ID";
    public static final String VX_MINI_PAGE_PATH = "VX_MINI_PAGE_PATH";

    public static final String ACCESS_TOKEN = "access_token";

    private static final Cacheable CACHEABLE = Cacheable.auto(CacheConfiguration.builder().expireAfterWrite((int) TimeUnit.MINUTES.toSeconds(5)).build());

    /**
     * 微信公众号获取认证信息
     */
    private static final String ACCESS_TOKEN_URL = "https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=%s&secret=%s";
    /**
     * 下发消息
     */
    private static final String SEND_MESSAGE_URL = "https://api.weixin.qq.com/cgi-bin/message/template/send?access_token=%s";
    /**
     * 获取公众号关注人
     */
    private static final String ATTENTION_USERS = "https://api.weixin.qq.com/cgi-bin/user/get?access_token=%s";

    private static final String OPEN_TO_UNION = "https://api.weixin.qq.com/cgi-bin/user/info?access_token=%s&openid=%s&lang=zh_CN";
    /**
     * token失效
     */
    private static final String TOKEN_VALID = " access_token is invalid or not latest,";
    private final String appId;
    private final String appSecret;
    /**
     * 下发的数据的间隔(防止数据重复提交)
     */
    private final int interval;
    /**
     * 异常时是否继续
     */

    private final boolean continueWhenError;

    private final Cacheable formChecker;

    public WxGzh(String appId, String appSecret, int interval, boolean continueWhenError) {
        this.appId = appId;
        this.appSecret = appSecret;
        this.interval = interval;
        this.continueWhenError = continueWhenError;
        this.formChecker = Cacheable.auto(CacheConfiguration.builder().expireAfterWrite((int) TimeUnit.MINUTES.toSeconds(interval)).build());
    }

    public WxGzh(String appId, String appSecret) {
        this(appId, appSecret, 10, true);
    }

    public WxGzh(String appId, String appSecret, int interval) {
        this(appId, appSecret, interval, true);
    }

    public WxGzh(String appId, String appSecret, boolean continueWhenError) {
        this(appId, appSecret, 10, continueWhenError);
    }

    /**
     * openId -> unionId
     * @param openIds openId
     * @return unionId
     */
    public List<String> transOpenIdToUnionId(String... openIds) {
        if(log.isDebugEnabled()) {
            log.debug("===================开始获取关注公众号用户的openId转换成unionId======================");
        }
        List<String> rs = new LinkedList<>();
        CountDownLatch countDownLatch = new CountDownLatch(openIds.length);
        ExecutorService executorService1 = ThreadUtils.newProcessorThreadExecutor();
        for (String openId : openIds) {
            executorService1.execute(() ->{
                try {
                    rs.add(transOpenIdToUnionId(openId));
                } catch (Exception ignored) {
                    rs.add(null);
                } finally {
                    countDownLatch.countDown();
                }
            });
        }
        try {
            countDownLatch.await(3, TimeUnit.MINUTES);
        } catch (InterruptedException ignored) {
        }

        return rs;
    }
    /**
     * openId -> unionId
     * @param openId openId
     * @return unionId
     */
    public String transOpenIdToUnionId(String openId) {
        if(log.isDebugEnabled()) {
            log.debug("===================开始获取关注公众号用户的openId转换成unionId======================");
        }
        HttpResponse httpResponse = HttpClient.get().url(String.format(OPEN_TO_UNION, getAccessToken().getString(ACCESS_TOKEN), openId)).newInvoker().execute();
        JSONObject result = httpResponse.content(JSONObject.class);
        return result.getString("unionid");
    }
    /**
     * 获取公众号关注人
     * @return 关注人
     */
    public List<WxUser> getAllUser() {
        if(log.isDebugEnabled()) {
            log.debug("===================开始获取关注公众号用户======================");
        }

        HttpResponse httpResponse = HttpClient.post().url(String.format(ATTENTION_USERS, getAccessToken().getString(ACCESS_TOKEN))).newInvoker().execute();
        JSONObject content = httpResponse.content(JSONObject.class);
        JSONArray openIdJsonArray = null;
        try {
            openIdJsonArray = content.getJSONObject("data").getJSONArray("openid");
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }

       return Arrays.stream(openIdJsonArray.toArray())
                .filter(Objects::nonNull)
                .map(String::valueOf)
                .map(it -> {
                    WxUser wxUser = new WxUser();
                    wxUser.setOpenId(it);
                    return wxUser;
                })
                .collect(Collectors.toList());

    }
    /**
     * 获取token
     *
     * @return token
     */
    @Override
    public MessageResponse getAccessToken() {
        return CACHEABLE.getOrPut(appId + appSecret, (Supplier<Value<MessageResponse>>) () -> {
            String tokenUrl = String.format(ACCESS_TOKEN_URL, appId, appSecret);
            HttpResponse httpResponse = HttpClient.get().url(tokenUrl).newInvoker().execute();
            JSONObject result = httpResponse.content(JSONObject.class);
            if (log.isDebugEnabled()) {
                log.debug("获取到的凭证结果: {}", result);
            }

            return TimeValue.of(MessageResponse.builder().code(ResultCode.transferForHttpCode(httpResponse.code()))
                    .message(httpResponse.message())
                    .data(result)
                    .build(), Duration.ofSeconds(result.getLongValue("expires_in")));
        }).getValue(MessageResponse.class);
    }

    @Override
    public void refreshAccessToken() {
        CACHEABLE.remove(appId + appSecret);
    }


    @Override
    public MessageResponse sendMessage(String templateId, Map<String, ?> data, String... toUser) {
        if (log.isDebugEnabled()) {
            log.debug("===================开始模板推送======================");
        }

        JSONObject rs = new JSONObject();
        ResultCode code = OK;
        for (String s : toUser) {
            rs.put("user", s);
            MessageResponse messageResponse = sendMessage(templateId, data, s);
            if (messageResponse.code().hasError()) {
                code = messageResponse.code();
            }
            rs.put("result", messageResponse);
        }
        return MessageResponse.builder().code(code)
                .message(code.getMsg())
                .data(rs)
                .build();
    }

    public MessageResponse sendMessage(String templateId, Map<String, ?> data, String toUser) {
        String string = MapUtils.getString(data, VX_MINI_APP_ID);
        JSONObject miniprogram = new JSONObject();
        if (StringUtils.isNotEmpty(string)) {
            miniprogram.put("appid", string);
            miniprogram.put("pagepath", MapUtils.getString(data, VX_MINI_PAGE_PATH));
        }

        JSONObject param = new JSONObject();
        if (miniprogram.isEmpty()) {
            param.put("miniprogram", miniprogram);
        }
        param.put("touser", toUser);
        param.put("data", data);
        param.put("template_id", templateId);
        data.remove(VX_MINI_APP_ID);
        data.remove(VX_MINI_PAGE_PATH);

        HttpClientInvoker invoker = HttpClient.post()
                .url(String.format(SEND_MESSAGE_URL, getAccessToken().getString(ACCESS_TOKEN)))
                .isJson()
                .body(param)
                .newInvoker();
        HttpResponse execute = invoker.execute();
        JSONObject content = execute.content(JSONObject.class);
        if (content.getString("errmsg").contains(TOKEN_VALID)) {
            refreshAccessToken();
            return MessageResponse.builder()
                    .code(SYSTEM_SERVER_BUSINESS)
                    .build();
        }

        return MessageResponse.builder()
                .code(ResultCode.transferForHttpCode(execute.code()))
                .data(content)
                .build();
    }
}
