package com.chua.hikvision.support.adaptor;

import com.alibaba.fastjson2.JSONObject;
import com.chua.hikvision.support.HikvisionClient;
import com.chua.hikvision.support.pojo.HikCamera;

import static com.chua.hikvision.support.HikvisionEnum.CAMERAS_PAGE_SEARCH;
import static com.chua.hikvision.support.HikvisionEnum.CAMERAS_STATUS;

/**
 * 设备/监控点适配器
 * @author CH
 */
public class CameraFactory {

    private final HikvisionClient hikvisionClient;

    public CameraFactory(HikvisionClient hikvisionClient) {
        this.hikvisionClient = hikvisionClient;
    }


    /**
     * 分页获取监控点资源
     * <p>
     * 获取监控点列表接口可用来全量同步监控点信息，返回结果分页展示。
     * </p>
     *
     * @param page     页码(1)
     * @param pageSize 分页数量 (1000)
     */
    public HikCamera cameras(int page, int pageSize) {
        JSONObject json = new JSONObject();
        json.put("pageNo", page);
        json.put("pageSize", pageSize);
        try {
            return hikvisionClient.executePost(CAMERAS_PAGE_SEARCH, json, HikCamera.class);
        } catch (Exception e) {
            e.printStackTrace();
            return HikCamera.empty();
        }
    }

    /**
     * 分页获取监控点资源
     * <p>
     * 获取监控点列表接口可用来全量同步监控点信息，返回结果分页展示。
     * </p>
     *
     * @param cameraIndexCode 设备编号
     * @param page            页码(1)
     * @param pageSize        分页数量 (1000)
     */
    public HikCamera camerasStatus(int page, int pageSize, String... cameraIndexCode) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("indexCodes", cameraIndexCode);
        jsonObject.put("pageNo", page);
        jsonObject.put("pageSize", pageSize);
        try {
            return hikvisionClient.executePost(CAMERAS_STATUS, jsonObject, HikCamera.class);
        } catch (Exception e) {
            e.printStackTrace();
            return HikCamera.empty();
        }
    }
}
