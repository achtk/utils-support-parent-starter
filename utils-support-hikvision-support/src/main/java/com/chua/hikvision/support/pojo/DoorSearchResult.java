package com.chua.hikvision.support.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 门禁列表
 *
 * @author CH
 */
@NoArgsConstructor
@Data
public class DoorSearchResult {
    @JsonProperty("total")
    private Integer total;
    @JsonProperty("pageNo")
    private Integer pageNo;
    @JsonProperty("pageSize")
    private Integer pageSize;
    @JsonProperty("list")
    private List<ListDTO> list;

    @NoArgsConstructor
    @Data
    public static class ListDTO {
        @JsonProperty("indexCode")
        private String indexCode;
        @JsonProperty("resourceType")
        private String resourceType;
        @JsonProperty("name")
        private String name;
        @JsonProperty("parentIndexCode")
        private String parentIndexCode;
        @JsonProperty("devTypeCode")
        private String devTypeCode;
        @JsonProperty("devTypeDesc")
        private String devTypeDesc;
        @JsonProperty("deviceCode")
        private String deviceCode;
        @JsonProperty("manufacturer")
        private String manufacturer;
        @JsonProperty("regionIndexCode")
        private String regionIndexCode;
        @JsonProperty("regionPath")
        private String regionPath;
        @JsonProperty("treatyType")
        private String treatyType;
        @JsonProperty("cardCapacity")
        private Integer cardCapacity;
        @JsonProperty("fingerCapacity")
        private Integer fingerCapacity;
        @JsonProperty("veinCapacity")
        private Integer veinCapacity;
        @JsonProperty("faceCapacity")
        private Integer faceCapacity;
        @JsonProperty("doorCapacity")
        private Integer doorCapacity;
        @JsonProperty("deployId")
        private String deployId;
        @JsonProperty("netZoneId")
        private String netZoneId;
        @JsonProperty("createTime")
        private String createTime;
        @JsonProperty("updateTime")
        private String updateTime;
        @JsonProperty("description")
        private String description;
        @JsonProperty("acsReaderVerifyModeAbility")
        private String acsReaderVerifyModeAbility;
        @JsonProperty("regionName")
        private String regionName;
        @JsonProperty("regionPathName")
        private String regionPathName;
        @JsonProperty("ip")
        private String ip;
        @JsonProperty("port")
        private String port;
        @JsonProperty("capability")
        private String capability;
        @JsonProperty("devSerialNum")
        private String devSerialNum;
        @JsonProperty("dataVersion")
        private String dataVersion;
    }
}
