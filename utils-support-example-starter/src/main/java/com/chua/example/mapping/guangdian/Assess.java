package com.chua.example.mapping.guangdian;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author CH
 */
@NoArgsConstructor
@Data
public class Assess {

    /**
     * 礼堂ID
     */
    private String id;
    /**
     * 礼堂名称
     */
    private String name;
    /**
     * 镇街道ID
     */
    private Integer townId;
    /**
     * 镇街道名称
     */
    private String townName;
    /**
     * 纬度
     */
    private Integer lng;
    /**
     * 经度
     */
    private Integer lat;
    /**
     * 分数
     */
    private Integer score;
    /**
     * 场次
     */
    private Integer count;
    /**
     * 排名
     */
    private Integer order;
    /**
     * 星级
     */
    private Integer level;
}
