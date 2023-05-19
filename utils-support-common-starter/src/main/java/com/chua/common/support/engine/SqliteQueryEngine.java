package com.chua.common.support.engine;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.annotations.SpiDefault;
import com.chua.common.support.database.Database;
import com.chua.common.support.database.inquirer.JdbcInquirer;
import com.chua.common.support.engine.config.EngineConfig;
import com.chua.common.support.engine.config.EngineConfig;
import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.function.Joiner;
import com.chua.common.support.utils.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.chua.common.support.constant.CommonConstant.EXIST_SIGN;

/**
 * h2
 *
 * @author CH
 */
@Slf4j
@Spi("sqlite")
@SpiDefault
public class SqliteQueryEngine<T> extends AbstractEngine<T> implements QueryEngine<T>, InitializingAware {
    protected final List<String> columns = new LinkedList<>();
    protected final List<String> columnTypes = new LinkedList<>();
    protected final String tableName;
    protected JdbcInquirer jdbcInquirer;
    protected String questMsg;

    public SqliteQueryEngine(Class<T> target, EngineConfig engineConfig) {
        super(target, engineConfig);
        this.tableName = createName().toUpperCase();
        if (engineConfig.isCleanWhenInitial()) {
            FileUtils.delete(tableName + ".index.", "db", "mv.db", "trace.db");
        }

        afterPropertiesSet();

        doAnalysisIndex();
        doAnalysisDatabase();
        doAnalysisVirtual();
    }

    protected String createName() {
        return "VIR" + Md5Utils.getInstance().getMd5String(target.getTypeName());
    }

    /**
     * 创建虚表
     */
    protected void doAnalysisVirtual() {
        try {
            jdbcInquirer.executeUpdate("CREATE TABLE " + tableName + "(" + Joiner.on(',').join(columnTypes) + ", primary key (" + CollectionUtils.findFirst(columns) + "))");
        } catch (Exception e) {
            String localizedMessage = e.getLocalizedMessage();
            if (!localizedMessage.contains(EXIST_SIGN)) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 初始化数据库
     */
    protected void doAnalysisDatabase() {
        Database database = Database
                .newBuilder()
                .driver("org.sqlite.JDBC")
                .url("jdbc:sqlite:./" + tableName + ".index")
                .build();
        this.jdbcInquirer = database.createJdbcInquirer();
    }

    /**
     * 分析索引
     */
    protected void doAnalysisIndex() {
        ClassUtils.doWithFields(target, field -> {
            String name = field.getName();
            columns.add(name);
            columnTypes.add(name + " TEXT");
        });
        this.questMsg = StringUtils.repeat("?", ",", columns.size());
    }

    @Override
    public Engine<T> config(EngineConfig engineConfig) {
        return this;
    }

    @Override
    public boolean addAll(T... t) {
        return addAll(Arrays.asList(t));
    }

    @Override
    public boolean addAll(List<T> t) {
        int size = t.size();
        log.info("开始注册数据数量: {}", size);
        StringBuilder sb = new StringBuilder("INSERT INTO ");
        sb.append(tableName).append(" (");
        sb.append(Joiner.on(',').join(columns)).append(") VALUES(");
        sb.append(questMsg).append(")");
        try {
            this.jdbcInquirer.batch(sb.toString(), ArrayUtils.toArrays(columns, t));
        } catch (Exception ignored) {
            throw new IllegalStateException();
        }

        return true;
    }

    @Override
    public boolean remove(T t) {
        return false;
    }

    @Override
    public List<T> query(String sl, Object... args) {
        StringBuilder sb = new StringBuilder();
        sb.append(StringUtils.format(sl, args).replace(" " + target.getSimpleName().toLowerCase() + " ", " " + tableName + " "));

        try {
            return jdbcInquirer.query(sb.toString(), target);
        } catch (Exception e) {
            throw new IllegalStateException("表达式异常");
        }
    }

    @Override
    public void close() throws Exception {
        jdbcInquirer.close();
    }

    @Override
    public void afterPropertiesSet() {

    }
}
