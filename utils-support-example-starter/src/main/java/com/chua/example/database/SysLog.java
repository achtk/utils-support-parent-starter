//package com.chua.example.database;
//
//import com.chua.common.support.database.annotation.Column;
//import com.chua.common.support.database.entity.JdbcType;
//import lombok.Data;
//import lombok.EqualsAndHashCode;
//
//import java.io.Serializable;
//import java.time.LocalDateTime;
//
//@Data
//@OpenMapping(value = "sys_log", virtual = "sys_log", dataSource = "atguigudb")
//@EqualsAndHashCode(callSuper = false)
//public class SysLog implements Serializable {
//
//    private static final long serialVersionUID = 1L;
//
//    @TableId(value = "id", type = IdType.AUTO)
//    private Integer id;
//
//    private Integer platform;
//
//    private String platformName;
//
//    private Integer institutionId;
//
//    private Integer institutionType;
//
//    private String moduleName;
//
//    private String ip;
//
//    private String mac;
//
//    private String className;
//
//    private String requestUrl;
//
//    private String requestMethod;
//
//    @Column(jdbcType = JdbcType.TEXT)
//    private String requestParam;
//
//    private Integer status;
//
//    private String errorText;
//
//    private String takeUpTime;
//
//    @TableField("CREATED_BY")
//    private Integer createdBy;
//
//    @TableField("CREATED_BY_NAME")
//    private String createdByName;
//
//    private String createdPhone;
//
//    @TableField("CREATED_TIME")
//    private LocalDateTime createdTime;
//}
