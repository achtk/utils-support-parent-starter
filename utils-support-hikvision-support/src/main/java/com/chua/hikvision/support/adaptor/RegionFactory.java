package com.chua.hikvision.support.adaptor;

import com.alibaba.fastjson2.JSONObject;
import com.chua.hikvision.support.HikvisionClient;
import com.chua.hikvision.support.pojo.HikRegion;

import static com.chua.hikvision.support.HikvisionEnum.*;

/**
 * 组织适配器
 * @author CH
 */
public class RegionFactory {

    private final HikvisionClient hikvisionClient;

    public RegionFactory(HikvisionClient hikvisionClient) {
        this.hikvisionClient = hikvisionClient;
    }

    /**
     * 获取组织机构
     *
     * @param page     页码
     * @param pageSize 每页数量
     * @return 组织机构
     */
    public HikRegion regions(int page, int pageSize) {
        JSONObject json = new JSONObject();
        json.put("pageNo", page);
        json.put("pageSize", pageSize);
        try {
            return hikvisionClient.executePost(REGIONS, json, HikRegion.class);
        } catch (Exception e) {
            e.printStackTrace();
            return HikRegion.empty();
        }
    }

    /**
     * 获取组织机构
     *
     * @return 组织机构
     */
    public HikRegion regions() {
        JSONObject json = new JSONObject();
        try {
            return hikvisionClient.executePost(REGION_ROOT, json, HikRegion.class);
        } catch (Exception e) {
            e.printStackTrace();
            return HikRegion.empty();
        }
    }

    /**
     * 获取组织机构子机构
     *
     * @param parentIndexCode 父机构ID
     * @param resourceType    资源类型
     * @param authCodes       权限码集合
     * @param page            页码
     * @param pageSize        每页数量
     * @return 组织机构
     */
    public HikRegion regionSub(String parentIndexCode, String resourceType, String[] authCodes, int page, int pageSize) {
        JSONObject json = new JSONObject();
        json.put("parentIndexCode", parentIndexCode);
        json.put("resourceType", resourceType);
        json.put("authCodes", authCodes);
        json.put("pageNo", page);
        json.put("pageSize", pageSize);
        try {
            return hikvisionClient.executePost(REGIONS_SUB, json, HikRegion.class);
        } catch (Exception e) {
            e.printStackTrace();
            return HikRegion.empty();
        }
    }

    /**
     * 根据区域获取组织机构
     *
     * @param regionId 机构ID
     * @return 组织机构
     */
    public HikRegion regionInfo(String[] regionId) {
        JSONObject json = new JSONObject();
        json.put("indexCodes", regionId);
        try {
            return hikvisionClient.executePost(REGIONS_INFO, json, HikRegion.class);
        } catch (Exception e) {
            e.printStackTrace();
            return HikRegion.empty();
        }
    }
}
