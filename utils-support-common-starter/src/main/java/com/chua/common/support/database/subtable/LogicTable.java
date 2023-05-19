package com.chua.common.support.database.subtable;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.List;

/**
 * 逻辑表
 *
 * @author CH
 */
@Data
@Builder
public class LogicTable {

    /**
     * 策略
     */
    @Singular("strategy")
    private List<SubTableStrategy> strategy;

    /**
     * 逻辑表名
     */
    private String logicTable;
    /**
     * 分隔符
     */
    @Builder.Default
    private String separator = "";
    /**
     * 真实表, 多个,分隔
     */
    private String actualTable;
    /**
     * 后缀
     */
    private String databaseStrategyMode;
    /**
     * 后缀
     */
    @Builder.Default
    private String tableStrategyMode = "_$->{0..1}";
}
