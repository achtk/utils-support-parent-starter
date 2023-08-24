package com.chua.hikvision.support;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import com.alibaba.fastjson2.JSONObject;
import com.chua.hikvision.support.adaptor.CameraFactory;
import com.chua.hikvision.support.adaptor.RegionFactory;
import com.hikvision.artemis.sdk.ArtemisHttpUtil;
import com.hikvision.artemis.sdk.config.ArtemisConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * 海康客户端
 *
 * @author CH
 */
public class HikvisionClient {
    private static final String CODE = "code";
    private static final String DATA = "data";
    private String[] protocol;
    /**
     * artemis路径
     */
    public static String ARTEMIS_PATH = "/artemis";

    public HikvisionClient(String[] protocol, String host, String appKey, String secretAccessKey) {
        this.protocol = protocol;
        ArtemisConfig.host = host;
        ArtemisConfig.appKey = appKey;
        ArtemisConfig.appSecret = secretAccessKey;
    }


    public HikvisionClient(String host, String appKey, String secretAccessKey) {
        this(new String[]{"https"}, host, appKey, secretAccessKey);
    }

    /**
     * 获取设备机构接口
     *
     * @return 获取设备机构接口
     */
    public CameraFactory getCameraFactory() {
        return new CameraFactory(this);
    }

    /**
     * 获取组织机构接口
     *
     * @return 获取组织机构接口
     */
    public RegionFactory getRegionFactory() {
        return new RegionFactory(this);
    }

    /**
     * 调用海康接口
     *
     * @param url  调用地址
     * @param json 参数
     * @param type 返回类型
     * @return 结果
     */
    public <T> T executePost(final String url, JSONObject json, Class<T> type) {
        String artemis = ArtemisHttpUtil.doPostStringArtemis(analysisPath(url), json.toJSONString(), null, null, "application/json");
        return executeResponse(artemis, type);
    }

    /**
     * 调用海康接口
     *
     * @param hikvisionEnum 调用类型
     * @param json          参数
     * @param type          返回类型
     * @return 结果
     */
    public <T> T executePost(final HikvisionEnum hikvisionEnum, JSONObject json, Class<T> type) {
        return executePost(ARTEMIS_PATH.concat(hikvisionEnum.getUrl()), json, type);
    }

    /**
     * 分析地址
     *
     * @param url 链接
     * @return path
     */
    private Map<String, String> analysisPath(String url) {
        Map<String, String> path = new HashMap<String, String>(protocol.length);
        for (String s : protocol) {
            path.put(s.concat("://"), url);
        }
        return path;
    }

    /**
     * 结果
     *
     * @param artemis 结果
     * @param type    类型
     * @param <T>     类型
     * @return T
     */
    private <T> T executeResponse(String artemis, Class<T> type) {
        if (String.class.isAssignableFrom(type)) {
            return (T) artemis;
        }

        JSONObject artemisObject = JSONObject.parseObject(artemis);
        if (isFailure(artemisObject)) {
            try {
                return type.newInstance();
            } catch (Exception ignored) {
            }
        }

        return JSON.parseObject(artemisObject.getString(DATA), type, Feature.IgnoreNotMatch);
    }

    /**
     * 是否查询失败
     *
     * @param artemisObject 请求条件
     * @return 结果
     */
    private boolean isFailure(JSONObject artemisObject) {
        return !"0".equals(artemisObject.getString(CODE));
    }

}
