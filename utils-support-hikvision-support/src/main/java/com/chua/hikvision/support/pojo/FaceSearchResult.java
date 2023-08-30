package com.chua.hikvision.support.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author CH
 */
@NoArgsConstructor
@Data
public class FaceSearchResult {


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
        /**
         * 人脸的唯一标识
         */
        @JsonProperty("indexCode")
        private String indexCode;
        /**
         * 人脸所属的人脸分组的唯一标识
         */
        @JsonProperty("faceGroupIndexCode")
        private String faceGroupIndexCode;
        @JsonProperty("faceInfo")
        private FaceInfoDTO faceInfo;
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
             *人脸的性别信息
             */
            @JsonProperty("sex")
            private String sex;
            /**
             * 人脸的证件类别信息
             */
            @JsonProperty("certificateType")
            private String certificateType;
            /**
             * 人脸的证件号码信息。1~20个数字、字母
             */
            @JsonProperty("certificateNum")
            private String certificateNum;
        }

        @NoArgsConstructor
        @Data
        public static class FacePicDTO {
            /**
             * 人脸图片的URL，查询返回时，为绝对路径
             */
            @JsonProperty("faceUrl")
            private String faceUrl;
        }
    }
}
