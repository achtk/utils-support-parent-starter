package com.chua.hikvision.support.adaptor;

import com.alibaba.fastjson2.JSONObject;
import com.boren.school.attendance.support.HikvisionClient;
import com.boren.school.attendance.support.query.DoorQuery;
import com.boren.school.attendance.support.result.DoorEventResult;
import com.boren.school.attendance.support.result.DoorSearchResult;
import com.chua.common.support.bean.BeanMap;

import static com.boren.school.attendance.support.HikvisionEnum.*;

/**
 * 门禁
 *
 * @author CH
 */
public class EntranceFactory {

    private final HikvisionClient hikvisionClient;

    public EntranceFactory(HikvisionClient hikvisionClient) {
        this.hikvisionClient = hikvisionClient;
    }

    /**
     * 根据条件查询目录下有权限的门禁设备列表。
     *
     * @param page     页码(1)
     * @param pageSize 分页数量 (1000)
     */
    public DoorSearchResult doorSearch(int page, int pageSize) {
        JSONObject json = new JSONObject();
        json.put("pageNo", page);
        json.put("pageSize", pageSize);
        try {
            return hikvisionClient.executePost(DOOR_SEARCH, json, DoorSearchResult.class);
        } catch (Exception e) {
            e.printStackTrace();
            return new DoorSearchResult();
        }
    }

    /**
     * 获取门禁事件的图片，重定向后返回图片流。
     *
     * @param svrIndexCode 提供picUri处会提供此字段(doorSearch返回字段中包含)
     * @param picUri       图片相对地址
     */
    public DoorSearchResult doorPicture(String svrIndexCode, String picUri) {
        JSONObject json = new JSONObject();
        json.put("svrIndexCode", svrIndexCode);
        json.put("picUri", picUri);
        try {
            return hikvisionClient.executePost(DOOR_PICTURE, json, DoorSearchResult.class);
        } catch (Exception e) {
            e.printStackTrace();
            return new DoorSearchResult();
        }
    }

    /**
     * 接口说明
     * 功能描述：该接口可以查询发生在门禁点上的人员出入事件，支持多个维度来查询，支持按
     * 时间、人员、门禁点、事件类型四个维度来查询；其中按事件类型来查询的方式，如果查询
     * 不到事件，存在两种情况，一种是该类型的事件没有发生过，所以查询不到，还有一种情况，
     * 该类型的事件发生过，但是由于门禁管理组件对该事件类型订阅配置处于关闭状态，所以不
     * 会存储该类型的事件，导致查询不到，对于这种情况，需要到门禁管理组件中，将该事件类
     * 型的订阅配置打开
     *
     * @param query query
     */
    public DoorEventResult doorEvent(DoorQuery query) {
        JSONObject json = new JSONObject();
        json.putAll(BeanMap.create(query));
        try {
            return hikvisionClient.executePost(DOOR_EVENT, json, DoorEventResult.class);
        } catch (Exception e) {
            e.printStackTrace();
            return new DoorEventResult();
        }
    }
}
