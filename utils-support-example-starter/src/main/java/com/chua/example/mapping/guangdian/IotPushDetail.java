package com.chua.example.mapping.guangdian;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 物联设备信息
 *
 * @author CH
 */
@NoArgsConstructor
@Data
public class IotPushDetail {

    private String id;
    private String address;
    private String deviceName;
    private List<ReceiversDTO> receivers;
    private String notifyTitle;
    private String notifyContent;
    private String deviceSN;
    private String productName;
    private String images;
    private String msgId;
    private String curPlaceId;
    private Long timestamp;
    private List<ReplysDTO> replys;

    @NoArgsConstructor
    @Data
    public static class ReceiversDTO {
        private String userName;
        private String mobile;
    }

    @NoArgsConstructor
    @Data
    public static class ReplysDTO {
        private String id;
        private String userName;
        private String content;
        private Long timestamp;
        private String created;
    }
}
