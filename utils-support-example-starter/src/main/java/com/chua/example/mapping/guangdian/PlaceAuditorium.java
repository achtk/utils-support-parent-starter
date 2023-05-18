package com.chua.example.mapping.guangdian;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 三方礼堂
 *
 * @author CH
 */
@NoArgsConstructor
@Data
public class PlaceAuditorium {

    /**
     * 数据ID
     */
    private String id;
    /**
     * 名称
     */
    private String name;
    /**
     * 礼堂logo
     */
    private String logo;
    /**
     * 简介
     */
    private String notes;
}
