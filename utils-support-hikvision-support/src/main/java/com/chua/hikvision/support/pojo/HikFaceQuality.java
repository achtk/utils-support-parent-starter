package com.chua.hikvision.support.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 人脸质量
 *
 * @author CH
 */
@NoArgsConstructor
@Data
public class HikFaceQuality {
    /**
     * 人 脸 图 片 检 测 结 果 ，
     * true-评分合格
     * false-评 分不合格
     */
    @JsonProperty("checkResult")
    private Boolean checkResult;
    /**
     * 评分失败详细错误码，
     * 0x1f902300-人脸检测错 误（图片格式不符合要 求、检测不到人脸等情况 时）
     * 0x1f902301-人脸检 测超时
     * 0x1f902303-图片 两 眼 间 距 过 小
     * 0x1f902304-图片彩色置 信度过低
     * 0x1f902305-图 片 人 脸 角 度 过 大
     * 0x1f902306-图片清晰度 过低
     * 0x1f902307-图片过 曝或过暗（灰阶值不符合 要求）
     * 0x1f902308-图片 遮挡严重
     * 0x1f902309-图 片分数过低
     */
    @JsonProperty("statusCode")
    private String statusCode;
    /**
     * 评分失败详情描述
     */
    @JsonProperty("statusMessage")
    private String statusMessage;
    /**
     * 人脸评分，范围： 1-100
     */
    @JsonProperty("faceScore")
    private Integer faceScore;

    /**
     * 评分成功后的人脸信息，用于人脸裁剪
     */
    @JsonProperty("facePicAnalysisResult")
    private FacePicAnalysisResultDTO facePicAnalysisResult;

    @NoArgsConstructor
    @Data
    public static class FacePicAnalysisResultDTO {
        /**
         * 序号
         */
        @JsonProperty("id")
        private Integer id;
        /**
         * 年龄
         */
        @JsonProperty("age")
        private Integer age;
        /**
         * 年龄偏差值
         */
        @JsonProperty("ageRange")
        private Integer ageRange;
        /**
         * 年龄段，
         * UNKNOWN-未知，
         * INFANT-婴幼儿，
         * KID-儿童 ，
         * CHILD- 少 年 ，
         * TEENAGER- 青 少 年 ，
         * YOUNG- 青 年 ，
         * PRIME- 壮 年 ，
         * MIDDLE- 中 年 ，
         * MIDDLEAGED- 中 老 年 ，
         * OLD-老年
         */
        @JsonProperty("ageGroup")
        private String ageGroup;
        /**
         * 性 别 ，
         * male- 男 性 ，
         * female-女性，
         * UNKNOWN- 未知
         */
        @JsonProperty("gender")
        private String gender;
        /**
         * 是否戴眼镜，
         * YES-是，
         * NO-否，
         * UNKNOWN-未知
         */
        @JsonProperty("glasses")
        private String glasses;
        /**
         * 是否微笑，
         * YES-是，
         * NO- 否，
         * UNKNOWN-未知
         */
        @JsonProperty("smile")
        private String smile;
        /**
         * 人脸角度信息
         */
        @JsonProperty("facePose")
        private FacePoseDTO facePose;
        @JsonProperty("targetModelData")
        private String targetModelData;
        @JsonProperty("faceRect")
        private FaceRectDTO faceRect;
        @JsonProperty("recommendFaceRect")
        private RecommendFaceRectDTO recommendFaceRect;
        @JsonProperty("faceMark")
        private FaceMarkDTO faceMark;
        @JsonProperty("beard")
        private String beard;
        @JsonProperty("mask")
        private String mask;
        @JsonProperty("faceScore")
        private Integer faceScore;

        @NoArgsConstructor
        @Data
        public static class FacePoseDTO {
            /**
             * 平面外上下俯仰角，
             * 范围： [-90,90]
             */
            @JsonProperty("pitch")
            private Integer pitch;
            /**
             * 平面外左右偏转角，
             * 范围： [-90,90]
             */
            @JsonProperty("yaw")
            private Integer yaw;
            /**
             * 平面内旋转角，范围： [- 90,90]
             */
            @JsonProperty("roll")
            private Integer roll;
            /**
             * 清晰度评分,范围： [0,1]
             */
            @JsonProperty("clearityScore")
            private Double clearityScore;
            /**
             * 彩色置信度,范围： [0,1]
             */
            @JsonProperty("colorConfidence")
            private Double colorConfidence;
            /**
             * 两眼间距,真实像素值
             */
            @JsonProperty("eyeDistance")
            private Integer eyeDistance;
            @JsonProperty("grayMean")
            private Integer grayMean;
            @JsonProperty("visibleScore")
            private Double visibleScore;
        }

        @NoArgsConstructor
        @Data
        public static class FaceRectDTO {
            @JsonProperty("height")
            private Double height;
            @JsonProperty("width")
            private Integer width;
            @JsonProperty("x")
            private Integer x;
            @JsonProperty("y")
            private Integer y;
        }

        @NoArgsConstructor
        @Data
        public static class RecommendFaceRectDTO {
            @JsonProperty("height")
            private Integer height;
            @JsonProperty("width")
            private Integer width;
            @JsonProperty("x")
            private Integer x;
            @JsonProperty("y")
            private Integer y;
        }

        @NoArgsConstructor
        @Data
        public static class FaceMarkDTO {
            @JsonProperty("leftEye")
            private FacePicAnalysisResultDTO.FaceMarkDTO.LeftEyeDTO leftEye;
            @JsonProperty("rightEye")
            private FacePicAnalysisResultDTO.FaceMarkDTO.RightEyeDTO rightEye;
            @JsonProperty("noseTip")
            private FacePicAnalysisResultDTO.FaceMarkDTO.NoseTipDTO noseTip;
            @JsonProperty("leftMouth")
            private FacePicAnalysisResultDTO.FaceMarkDTO.LeftMouthDTO leftMouth;
            @JsonProperty("rightMouth")
            private FacePicAnalysisResultDTO.FaceMarkDTO.RightMouthDTO rightMouth;

            @NoArgsConstructor
            @Data
            public static class LeftEyeDTO {
                @JsonProperty("x")
                private Integer x;
                @JsonProperty("y")
                private Integer y;
            }

            @NoArgsConstructor
            @Data
            public static class RightEyeDTO {
                @JsonProperty("x")
                private Integer x;
                @JsonProperty("y")
                private Integer y;
            }

            @NoArgsConstructor
            @Data
            public static class NoseTipDTO {
                @JsonProperty("x")
                private Integer x;
                @JsonProperty("y")
                private Integer y;
            }

            @NoArgsConstructor
            @Data
            public static class LeftMouthDTO {
                @JsonProperty("x")
                private Integer x;
                @JsonProperty("y")
                private Integer y;
            }

            @NoArgsConstructor
            @Data
            public static class RightMouthDTO {
                @JsonProperty("x")
                private Integer x;
                @JsonProperty("y")
                private Integer y;
            }
        }
    }
}
