package com.chua.hikvision.support.pojo;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 海康监控点设备信息
 * </p>
 *
 * @author ch
 * @since 2022-06-28
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class HikCameraDevice {
    /**
     * 监控点唯一标识
     */
    private String cameraIndexCode;
    /**
     * 监控点名称
     */
    private String cameraName;
    /**
     * 监控点类型
     * 0: 枪机
     * 1: 半球
     * 2: 快球
     * 3: 云台枪机
     * "
     */
    private int cameraType;
    /**
     * 监控点类型说明
     */
    private String cameraTypeName;
    /**
     * 能力集说明
     */
    private String capabilitySetName;
    /**
     * 通道编号
     */
    private String channelNo;
    /**
     * 通道类型
     */
    private String channelType;
    /**
     * 通道类型说明
     */
    private String channelTypeName;
    /**
     * 创建时间，采用ISO8601标准，如2018-07-26T21:30:08+08:00 表示北京时间2018年7月26日21时30分08秒
     */
    private String createTime;
    /**
     * 所属编码设备唯一标识
     */
    private String encodeDevIndexCode;
    /**
     * 监控点国标编号，即外码编号externalIndexCode
     */
    private String gbIndexCode;
    /**
     * 安装位置
     * <table>
     *     <thead>
     *         <tr>
     *              <th>类型</th>
     *              <th>类型说明</th>
     *         </tr>
     *     </thead>
     *     <tbody>
     *         <tr>
     *             <td>communityPerimeter</td>
     *             <td>小区周界</td>
     *         </tr>
     *
     *         <tr>
     *             <td>communityEntrance</td>
     *             <td>小区出入口</td>
     *         </tr>
     *
     *         <tr>
     *             <td>fireChannel</td>
     *             <td>消防通道</td>
     *         </tr>
     *
     *         <tr>
     *             <td>andscapePool</td>
     *             <td>景观池</td>
     *         </tr>
     *
     *         <tr>
     *             <td>outsideBuilding</td>
     *             <td>住宅楼外</td>
     *         </tr>
     *
     *         <tr>
     *             <td>parkEntrance</td>
     *             <td>停车场（库）出入口</td>
     *         </tr>
     *
     *         <tr>
     *             <td>parkArea</td>
     *             <td>停车场区</td>
     *         </tr>
     *
     *         <tr>
     *             <td>equipmentRoom</td>
     *             <td>设备房（机房、配电房、泵房）</td>
     *         </tr>
     *
     *         <tr>
     *             <td>monitorCenter</td>
     *             <td>监控中心</td>
     *         </tr>
     *
     *         <tr>
     *             <td>stopArea</td>
     *             <td>禁停区</td>
     *         </tr>
     *
     *         <tr>
     *             <td>vault</td>
     *             <td>金库</td>
     *         </tr>
     *     </tbody>
     * </table>
     */
    private String installLocation;
    /**
     * 纬度
     */
    private String latitude;
    /**
     * 经度
     */
    private String longitude;
    /**
     * 录像存储位置
     */
    private String recordLocation;
    /**
     * 录像存储位置说明
     */
    private String recordLocationName;
    /**
     * 所属区域唯一标识
     */
    private String regionIndexCode;
    /**
     * 状态说明
     */
    private String statusName;
    /**
     * 传输协议类型说明
     */
    private String transTypeName;
    /**
     * 接入协议类型说明
     */
    private String treatyTypeName;
    /**
     * 更新时间
     * 采用ISO8601标准，如2018-07-26T21:30:08+08:00
     * 表示北京时间2017年7月26日21时30分08秒
     */
    private String updateTime;
}
