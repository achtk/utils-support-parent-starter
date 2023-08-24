package com.chua.hikvision.support.adaptor;

import com.alibaba.fastjson2.JSONObject;
import com.chua.hikvision.support.HikvisionClient;
import com.chua.hikvision.support.pojo.HikCarCarport;
import com.chua.hikvision.support.pojo.HikCarCarportBill;
import org.apache.commons.lang3.StringUtils;

import static com.chua.hikvision.support.HikvisionEnum.CAR_CARPORT;
import static com.chua.hikvision.support.HikvisionEnum.CAR_CARPORT_BILL;

/**
 * 车辆/车库适配器
 * @author CH
 */
public class CarFactory {

    private final HikvisionClient hikvisionClient;

    public CarFactory(HikvisionClient hikvisionClient) {
        this.hikvisionClient = hikvisionClient;
    }


    /**
     * 分页获取监控点资源
     * <p>
     * 获取监控点列表接口可用来全量同步监控点信息，返回结果分页展示。
     * </p>
     *
     * @param parkIndexCodes     停车场唯一标识集合多个值使用英文逗号分隔,不超过1000个（选填）
     */
    public HikCarCarport carCarport(String... parkIndexCodes) {
        JSONObject json = new JSONObject();
        json.put("parkIndexCodes", parkIndexCodes);
        try {
            return hikvisionClient.executePost(CAR_CARPORT, json, HikCarCarport.class);
        } catch (Exception e) {
            e.printStackTrace();
            return HikCarCarport.empty();
        }
    }

    /**
     * 查询停车账单(根据停车库和车牌号)
     * <p>
     * </p>
     *
     * @param parkSysCode     停车场唯一标识（选填）
     * @param plateNo     车牌号
     */
    public HikCarCarportBill carCarportBill(String parkSysCode, String plateNo) {
        if(StringUtils.isBlank(plateNo)) {
            throw new RuntimeException("车牌号不能为空");
        }
        JSONObject json = new JSONObject();
        json.put("parkSyscode", parkSysCode);
        json.put("plateNo", plateNo);
        try {
            return hikvisionClient.executePost(CAR_CARPORT_BILL, json, HikCarCarportBill.class);
        } catch (Exception e) {
            e.printStackTrace();
            return HikCarCarportBill.empty();
        }
    }

}
