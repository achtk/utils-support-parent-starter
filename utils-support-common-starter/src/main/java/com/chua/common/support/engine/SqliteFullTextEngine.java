package com.chua.common.support.engine;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.annotations.SpiDefault;
import com.chua.common.support.engine.config.EngineConfig;
import com.chua.common.support.function.Joiner;
import com.chua.common.support.utils.ClassUtils;
import com.chua.common.support.utils.StringUtils;

import java.util.List;

import static com.chua.common.support.constant.CommonConstant.EXIST_SIGN;

/**
 * 引擎
 *
 * @author CH
 */
@Spi("sqlite_full")
@SpiDefault
public class SqliteFullTextEngine<T> extends SqliteQueryEngine<T> implements FullTextEngine<T> {

    public SqliteFullTextEngine(Class<T> target, EngineConfig engineConfig) {
        super(target, engineConfig);
    }

    /**
     * 分析索引
     */
    @Override
    protected void doAnalysisIndex() {
        ClassUtils.doWithFields(target, field -> {
            String name = field.getName();
            columns.add(name);
            columnTypes.add(name);
        });
        this.questMsg = StringUtils.repeat("?", ",", columns.size());
    }

    @Override
    protected String createName() {
        return super.createName() + "_full";
    }

    @Override
    protected void doAnalysisVirtual() {
        try {
            jdbcInquirer.executeUpdate("CREATE VIRTUAL TABLE " + tableName + " USING fts5(" + Joiner.on(',').join(columnTypes) + ")");
        } catch (Exception e) {
            String localizedMessage = e.getLocalizedMessage();
            if (!localizedMessage.contains(EXIST_SIGN)) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public List<T> search(String sl) {
        try {
            return jdbcInquirer.query("select * from " + tableName + " where " + tableName + " MATCH '" + sl + "'", target);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws Exception {
        super.close();
    }
}
