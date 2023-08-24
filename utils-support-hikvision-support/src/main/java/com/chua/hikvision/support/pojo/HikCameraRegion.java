package com.chua.hikvision.support.pojo;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 海康组织设备信息
 */

@Data
@EqualsAndHashCode(callSuper = false)
public class HikCameraRegion implements Serializable {

    private Integer id;

    /**
     * 设备唯一编号
     */
    private String cameraIndexCode;
    /**
     * 纬度
     */
    private String latitude;
    /**
     * 经度
     */
    private String longitude;

    /**
     * 所属区域唯一标识
     */
    private String regionIndexCode;

    /**
     * 所属区域名称
     */
    private String regionName;
    /**
     * value = "监控点类型\r\n" +
     *             "0: 枪机\r\n" +
     *             "1: 半球\r\n" +
     *             "2: 快球\r\n" +
     *             "3: 云台枪机\r\n" +
     *             ""
     */
    private int cameraType;


    /**
     * 监控点类型说明
     */
    private String cameraTypeName;

    /**
     * 设备定义类型, safe: 安全 emergency: 应急 protection: 环保 energy: 能源 close: 封闭
     */
    private String cameraCustomType;
    /**
     * 设备名称, 暂不使用，用于后续可能前端页面修改设备类型是可以查看设备的信息
     */
    private String cameraName;
    /**
     * 设备状态, 暂不使用, 0: 离线, 1: 在线
     */
    private Integer online;
    /**
     * 设备状态, 暂不使用, 1: 有效, 0: 无效
     */
    private Integer status;
    /**
     * 优先级
     */
    private Integer cameraOrder;
}
