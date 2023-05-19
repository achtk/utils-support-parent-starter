package com.chua.subtable.support;

import com.chua.common.support.database.structure.DataSourceDataStructure;
import com.chua.common.support.database.structure.DataStructure;
import com.chua.common.support.database.structure.StructureValue;
import com.chua.common.support.database.subtable.AbstractSubTableFactory;
import com.chua.common.support.database.subtable.LogicTable;
import com.chua.common.support.database.subtable.Strategy;
import com.chua.common.support.database.subtable.SubTableStrategy;
import com.chua.common.support.lang.date.DateTime;
import com.chua.common.support.lang.date.DateUtils;
import com.chua.common.support.utils.CollectionUtils;
import com.chua.common.support.utils.StringUtils;
import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import org.apache.shardingsphere.api.config.sharding.ShardingRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.TableRuleConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.ShardingStrategyConfiguration;
import org.apache.shardingsphere.api.config.sharding.strategy.StandardShardingStrategyConfiguration;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.standard.RangeShardingValue;
import org.apache.shardingsphere.core.rule.ShardingRule;
import org.apache.shardingsphere.shardingjdbc.jdbc.core.datasource.ShardingDataSource;

import javax.sql.DataSource;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 分表
 *
 * @author CH
 */
public class ShardingSubTableFactory extends AbstractSubTableFactory {

    private ShardingDataSource datasource;

    public ShardingSubTableFactory(Properties properties, Map<String, DataSource> dataSourceMap, List<LogicTable> logicTables) {
        super(properties, dataSourceMap, logicTables);
        afterPropertiesSet();
    }

    @Override
    public void afterPropertiesSet() {
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        Collection<TableRuleConfiguration> tableRuleConfigs = shardingRuleConfig.getTableRuleConfigs();

        for (LogicTable logicTable : logicTables) {
            tableRuleConfigs.add(doAnalysisLogicTable(logicTable));
        }

        try {
            this.datasource = new ShardingDataSource(dataSourceMap, new ShardingRule(shardingRuleConfig, dataSourceMap.keySet()), properties);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 逻辑表
     *
     * @param logicTable 逻辑表
     * @return 配置
     */
    private TableRuleConfiguration doAnalysisLogicTable(LogicTable logicTable) {
        TableRuleConfiguration ruleConfiguration = new TableRuleConfiguration(
                logicTable.getLogicTable(),
                StringUtils.defaultString(logicTable.getActualTable(),
                        StringUtils.defaultString(logicTable.getDatabaseStrategyMode(), CollectionUtils.findFirst(dataSourceMap.keySet()))
                                + "." + logicTable.getLogicTable() + logicTable.getTableStrategyMode()));

        for (SubTableStrategy strategy : logicTable.getStrategy()) {
            doAnalysisStrategy(logicTable, strategy, ruleConfiguration);
        }
        return ruleConfiguration;
    }

    /**
     * 分析策略
     *
     * @param logicTable        logicTable
     * @param strategy          策略
     * @param ruleConfiguration 配置器
     */
    private void doAnalysisStrategy(LogicTable logicTable, SubTableStrategy strategy, TableRuleConfiguration ruleConfiguration) {
        ShardingStrategyConfiguration configuration = new StandardShardingStrategyConfiguration(strategy.getColumn(), new ShardingAlgorithm(logicTable, strategy), new ShardingAlgorithm(logicTable, strategy));

        if (strategy.getStrategy() == Strategy.DB) {
            ruleConfiguration.setDatabaseShardingStrategyConfig(configuration);
            return;
        }

        if (strategy.getStrategy() == Strategy.TABLE_DB) {
            ruleConfiguration.setDatabaseShardingStrategyConfig(configuration);
        }

        ruleConfiguration.setTableShardingStrategyConfig(configuration);
    }

    @Override
    public DataSource dataSource() {
        return datasource;
    }

    @Override
    public void createSubTable(String sourceTable, String targetTable) {
        String[] split = targetTable.split("\\.");
        if (split.length == 2) {
            createSubTable(sourceTable, split[0], split[1]);
            return;
        }
        createSubTable(sourceTable, CollectionUtils.findFirst(dataSourceMap.keySet()), targetTable);
    }

    /**
     * 创建子表
     *
     * @param sourceTable 原表
     * @param schema      数据库
     * @param table       表
     */
    private void createSubTable(String sourceTable, String schema, String table) {
        DataSource dataSource = dataSourceMap.get(schema);
        if (null == dataSource) {
            throw new IllegalArgumentException(schema + "数据库不存在");
        }

        createTable(dataSource, sourceTable, schema, table);
    }

    /**
     * 创建表
     *
     * @param dataSource  数据源
     * @param sourceTable 原表
     * @param schema      数据库
     * @param table       表
     */
    private void createTable(DataSource dataSource, String sourceTable, String schema, String table) {
        DataStructure dataStructure = new DataSourceDataStructure(dataSource);
        dataStructure.createStructure(StructureValue.builder()
                .schema(schema)
                .source(sourceTable)
                .target(table).build());
    }


    public static class ShardingAlgorithm implements PreciseShardingAlgorithm<String>, RangeShardingAlgorithm<String> {

        private final LogicTable logicTable;
        private SubTableStrategy strategy;

        public ShardingAlgorithm(LogicTable logicTable, SubTableStrategy strategy) {
            this.logicTable = logicTable;
            this.strategy = strategy;
        }

        @Override
        public String doSharding(Collection<String> collection, PreciseShardingValue<String> preciseShardingValue) {
            String tbName = preciseShardingValue.getLogicTableName() + logicTable.getSeparator();
            try {
                Date date = DateUtils.parseDate(preciseShardingValue.getValue());
                String year = String.format("%tY", date);
                String mon = String.format("%tm", date);
                String dat = String.format("%td", date);
                tbName = tbName + year + mon + dat;
            } catch (ParseException e) {
                e.printStackTrace();
            }

            for (String each : collection) {
                if (tbName.startsWith(each)) {
                    return tbName;
                }
            }

            throw new IllegalArgumentException();
        }

        @Override
        public Collection<String> doSharding(Collection<String> availableTargetNames, RangeShardingValue<String> shardingValue) {
            Range<String> valueRange = shardingValue.getValueRange();
            DateRangeTime dateRangeTime = ofStr(valueRange);
            return null;
        }
    }


    /**
     * 初始化
     *
     * @param valueRange 时间区间
     * @return this
     */
    public static DateRangeTime ofStr(Range<String> valueRange) {
        if (valueRange.hasLowerBound() && valueRange.hasUpperBound()) {
            return DateRangeTime.of(Range.range(DateTime.of(valueRange.lowerEndpoint()).toLocalDateTime(),
                    valueRange.lowerBoundType(),
                    DateTime.of(valueRange.upperEndpoint()).toLocalDateTime(),
                    valueRange.upperBoundType()));
        }

        if (valueRange.hasLowerBound()) {
            return DateRangeTime.of(valueRange.lowerBoundType() == BoundType.CLOSED ?
                    Range.atLeast(DateTime.of(valueRange.lowerEndpoint()).toLocalDateTime()) : Range.greaterThan(DateTime.of(valueRange.lowerEndpoint()).toLocalDateTime()));
        }

        if (valueRange.hasUpperBound()) {
            return DateRangeTime.of(valueRange.upperBoundType() == BoundType.CLOSED ? Range.atMost(DateTime.of(valueRange.upperEndpoint()).toLocalDateTime()) : Range.lessThan(DateTime.of(valueRange.upperEndpoint()).toLocalDateTime()));
        }

        return DateRangeTime.of(Range.all());

    }

    /**
     * 初始化
     *
     * @param valueRange 时间区间
     * @return this
     */
    public static DateRangeTime of(Range<LocalDateTime> valueRange) {
        return new DateRangeTime(valueRange);
    }

}
