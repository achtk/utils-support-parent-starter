package com.chua.common.support.pojo;

import com.chua.common.support.database.annotation.Column;
import com.chua.common.support.database.annotation.Id;
import com.chua.common.support.database.annotation.Table;
import lombok.Data;
/**
 * 基础类
 * @author CH
 */
@Data
@Table(value = "oss_system")
public class OssSystem {
    @Column(comment = "ossID")
    @Id
    private Integer ossId;
    @Column(comment = "oss路径")
    private String ossPath;
    @Column(comment = "bucket")
    private String ossBucket;
    @Column(comment = "oss类型", defaultValue = "'local'")
    private String ossType;
    @Column(comment = "appKey")
    private String ossAppKey;
    @Column(comment = "appSecret")
    private String ossAppSecret;
    @Column(comment = "传输字节", defaultValue = "4096")
    private Integer ossBuffer;
    @Column(comment = "重名覆盖")
    private Boolean ossCovering;
    @Column(comment = "命名策略", defaultValue = "'original'")
    private String ossNameStrategy;
    @Column(comment = "拒绝策略", defaultValue = "'null'")
    private String ossRejectStrategy;
    @Column(comment = "额外参数")
    private String ossProperties;
    @Column(comment = "插件,分隔")
    private String ossPlugins;
}