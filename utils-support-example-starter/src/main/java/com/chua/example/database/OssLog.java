package com.chua.example.database;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.chua.common.support.file.export.ExportIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: zzl
 * @CreateTime: 2022-12-19  13:19
 * @Description:
 * @Version: 1.0
 */
@NoArgsConstructor
//@OpenMapping( virtual = "oss_log", dataSource = "atguigudb")
@Data
public class OssLog{

    @TableId(type = IdType.AUTO)
    private Integer dataId;

    @ExportIgnore
    private Integer id;
    private String fileName;
    private String url;
    private String qiniukey;
    private String suffix;

    public OssLog(String fileName, String url, String key, String suffix) {
        this.fileName = fileName;
        this.url = url;
        this.qiniukey = key;
        this.suffix = suffix;
    }


}
