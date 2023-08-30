package com.chua.hikvision.support.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 门禁事件
 *
 * @author CH
 */
@NoArgsConstructor
@Data
public class DoorEventResult {

    @JsonProperty("pageSize")
    private Integer pageSize;
    @JsonProperty("list")
    private List<ListDTO> list;
    @JsonProperty("total")
    private Integer total;
    @JsonProperty("totalPage")
    private Integer totalPage;
    @JsonProperty("pageNo")
    private Integer pageNo;

    @NoArgsConstructor
    @Data
    public static class ListDTO {
        /**
         * 事件 ID，唯一标识这个事
         * 件
         */
        @JsonProperty("eventId")
        private String eventId;
        /**
         * 事件名称
         */
        @JsonProperty("eventName")
        private String eventName;
        /**
         * 事件产生时间(事件产生
         * 的时间，采用 ISO8601 时
         * 间格式)
         */
        @JsonProperty("eventTime")
        private String eventTime;
        /**
         * 人员唯一编码
         */
        @JsonProperty("personId")
        private String personId;
        /**
         * 卡号
         */
        @JsonProperty("cardNo")
        private Object cardNo;
        /**
         * 人员姓名
         */
        @JsonProperty("personName")
        private Object personName;
        /**
         * 人员所属组织编码
         */
        @JsonProperty("orgIndexCode")
        private Object orgIndexCode;
        /**
         * 人员所属组织名称
         */
        @JsonProperty("orgName")
        private Object orgName;
        /**
         * 门禁点名称
         */
        @JsonProperty("doorName")
        private String doorName;
        /**
         * 门禁点编码
         */
        @JsonProperty("doorIndexCode")
        private String doorIndexCode;
        /**
         * 门禁点所在区域编码
         */
        @JsonProperty("doorRegionIndexCode")
        private String doorRegionIndexCode;
        /**
         * 抓拍图片地址(抓拍图片
         * uri，它是一个相对地址，
         * 可以通过“获取门禁事件
         * 抓拍的图片”的接口，获
         * 取到图片的数据)
         */
        @JsonProperty("picUri")
        private String picUri;
        /**
         * 图片存储服务的唯一标
         * 识(与picUri配对输出的
         * 字段信息，用于“获取门禁事件抓拍的图片”接口
         * 的输入参数)
         */
        @JsonProperty("svrIndexCode")
        private String svrIndexCode;
        /**
         * 事件类型
         */
        @JsonProperty("eventType")
        private Integer eventType;
        /**
         * 进出类型(1：进 0：出-1:
         * 未知要求：进门读卡器拨
         * 码设置为 1，出门读卡器
         * 拨码设置为 2)
         */
        @JsonProperty("inAndOutType")
        private Integer inAndOutType;
        /**
         * 读卡器唯一标识
         */
        @JsonProperty("readerDevIndexCode")
        private String readerDevIndexCode;
        /**
         * 读卡器名称
         */
        @JsonProperty("readerDevName")
        private String readerDevName;
        /**
         * 控制器设备唯一标识
         */
        @JsonProperty("devIndexCode")
        private String devIndexCode;
        /**
         * 控制器设备名称
         */
        @JsonProperty("devName")
        private String devName;
        /**
         * 身份证图片地址(身份证
         * 图片 uri，可以通过“获
         * 取门禁事件抓拍的图片”
         * 的接口，获取到图片的数
         * 据)
         */
        @JsonProperty("identityCardUri")
        private Object identityCardUri;
        /**
         * 事 件 入 库 时 间 ， 采 用
         * ISO8601 时间格式
         */
        @JsonProperty("receiveTime")
        private String receiveTime;
        /**
         * 工号
         */
        @JsonProperty("jobNo")
        private Object jobNo;
        /**
         * 学号
         */
        @JsonProperty("studentId")
        private Object studentId;
        /**
         * 证件号码
         */
        @JsonProperty("certNo")
        private Object certNo;
    }
}
