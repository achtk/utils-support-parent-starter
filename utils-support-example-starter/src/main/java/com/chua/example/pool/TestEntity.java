package com.chua.example.pool;

import com.chua.common.support.database.annotation.Column;
import com.chua.common.support.database.annotation.Id;
import lombok.Data;

/**
 * @author CH
 * @version 1.0.0
 * @since 2021/6/28
 */
@Data
public class TestEntity  {

    @Id
    private Integer id;

    private String success;
    @Column(length = 11)
    private Integer device;

}
