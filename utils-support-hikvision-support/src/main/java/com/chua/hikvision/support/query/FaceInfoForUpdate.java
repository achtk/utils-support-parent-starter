package com.chua.hikvision.support.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 人脸信息
 * @author CH
 */
@NoArgsConstructor
@Data
public class FaceInfoForUpdate {

    /**
     * 人脸的唯一标识，可从按条件批量查询人脸接口返回报文中的indexCode字段
     */
    @JsonProperty("indexCode")
    private String indexCode;
    /**
     * 人脸信息对象
     */
    @JsonProperty("faceInfo")
    private FaceInfoDTO faceInfo;
    /**
     * 人脸图片对象
     */
    @JsonProperty("facePic")
    private FacePicDTO facePic;

    @NoArgsConstructor
    @Data
    public static class FaceInfoDTO {
        /**
         * 人脸的名称,1~32个字符；不能包含 ’ / \ : * ? " < >
         */
        @JsonProperty("name")
        private String name;
        /**
         * 人脸的性别信息，
         * 1-男性，2-女性，UNKNOWN-未知
         */
        @JsonProperty("sex")
        private String sex = "UNKNOWN";
        /**
         * 人脸的证件类别，111-身份证，OTHER-其它证件
         */
        @JsonProperty("certificateType")
        private String certificateType = "OTHER";
        /**
         * 人脸的证件号码信息。1~20个数字、字母。
         */
        @JsonProperty("certificateNum")
        private String certificateNum;
    }

    @NoArgsConstructor
    @Data
    public static class FacePicDTO {
        /**
         * 人脸图片的URL。和faceBinaryData不能同时为空，同时存在时优先取faceBinaryData；图片的大小范围在10KB到200KB之间，只支持JGP格式图片。
         */
        @JsonProperty("faceUrl")
        private String faceUrl;
        /**
         * 脸图片的二进制数据经过Base64编码后的字符串，和faceUrl不能同时为空，同时存在时优先取faceBinaryData。 图片的大小范围在10KB到200KB之间，只支持JGP格式图片。
         * 最大长度：200*1024
         */
        @JsonProperty("faceBinaryData")
        private String faceBinaryData;

    }
}
