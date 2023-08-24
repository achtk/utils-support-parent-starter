package com.chua.example.crawler;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


@Data
@TableName("t_area_info")
public class TAreaInfo {

    @TableId(type= IdType.AUTO)
    private Integer id;

    /**
     * 区域名称
     */
    private String name;

    /**
     * 编码
     */
    private String code;

    /**
     * 父级编码
     */
    private String pcode;

    /**
     * 备注
     */
    private String mark;


}
