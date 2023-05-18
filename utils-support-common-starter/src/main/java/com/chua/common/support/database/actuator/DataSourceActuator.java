package com.chua.common.support.database.actuator;

import com.chua.common.support.collection.ConcurrentReferenceHashMap;
import com.chua.common.support.database.action.DataSourceActionExecutor;
import com.chua.common.support.database.dialect.Dialect;
import com.chua.common.support.database.expression.Expression;
import com.chua.common.support.database.metadata.Metadata;
import com.chua.common.support.utils.ClassUtils;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * 数据库执行器
 * @author CH
 */
@Slf4j
@SuppressWarnings("ALL")
public class DataSourceActuator implements Actuator{

    private final Map<String, Method> nameMethod;

    private static final Map<DataSource, List<Metadata<?>>> CACHE = new ConcurrentReferenceHashMap<>(512);

    public DataSourceActuator() {
        this.nameMethod = ClassUtils.getMethodsByName(this.getClass(), method -> !method.getName().startsWith("do"));
    }

    @Override
    public Object doExecute(String name, Object... args) {
        if(args.length != 1) {
            return null;
        }

        Method method = nameMethod.get(name);
        if(null == method) {
            return null;
        }

        try {
            method.setAccessible(true);
            return method.invoke(this, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Metadata<?> getTable(Metadata<?> metadata, Object key) {
        List<Metadata<?>> metadata1 = CACHE.get(key);
        if(null == metadata1) {
            return null;
        }

        for (Metadata metadata2 : metadata1) {
            if(metadata2.isMatch(metadata)) {
                return metadata2;
            }
        }
        return null;
    }

    /**
     * update表
     */
    private void update(DataSourceActionExecutor.DataSourceActionMetadata actionMetadata) {
        Dialect dialect = getDialect(actionMetadata);
        if(null == dialect) {
            return;
        }

        Expression expression = actionMetadata.getExpression();
        Metadata metadata = expression.getValue(Metadata.class);
        doAnalysis(actionMetadata, dialect);
        if(!isContainsKey(actionMetadata)) {
            log.warn("{}不存在, 开始创建表", metadata.getTable());
            dialect.createTable(metadata, actionMetadata.getDataSource());
            return;
        }

        dialect.updateTable(this, metadata, actionMetadata.getDataSource());
    }
    /**
     * drop表
     */
    private void drop(DataSourceActionExecutor.DataSourceActionMetadata actionMetadata) {
        Dialect dialect = getDialect(actionMetadata);
        if(null == dialect) {
            return;
        }

        Expression expression = actionMetadata.getExpression();
        Metadata metadata = expression.getValue(Metadata.class);
        doAnalysis(actionMetadata, dialect);
        if(!isContainsKey(actionMetadata)) {
            log.warn("{}不已存在", metadata.getTable());
            return;
        }

        dialect.dropTable(metadata, actionMetadata.getDataSource());
    }
    /**
     * create表
     */
    private void create(DataSourceActionExecutor.DataSourceActionMetadata actionMetadata) {
        Dialect dialect = getDialect(actionMetadata);
        if(null == dialect) {
            return;
        }

        Expression expression = actionMetadata.getExpression();
        Metadata metadata = expression.getValue(Metadata.class);
        doAnalysis(actionMetadata, dialect);
        if(isContainsKey(actionMetadata)) {
            log.warn("{}已存在", metadata.getTable());
            return;
        }

        dialect.createTable(metadata, actionMetadata.getDataSource());
    }



    private boolean isContainsKey(DataSourceActionExecutor.DataSourceActionMetadata actionMetadata) {
        Expression expression = actionMetadata.getExpression();
        DataSource dataSource = actionMetadata.getDataSource();
        Metadata<?> metadata = expression.getValue(Metadata.class);
        if(!CACHE.containsKey(dataSource)) {
            return true;
        }

        List<Metadata<?>> metadata1 = CACHE.get(dataSource);
        for (Metadata metadata2 : metadata1) {
            if(metadata2.isMatch(metadata)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 分析表
     * @param dataSource 数据源
     * @param dialect 方言
     */
    private void doAnalysis(DataSourceActionExecutor.DataSourceActionMetadata dataSource, Dialect dialect) {
        CACHE.computeIfAbsent(dataSource.getDataSource(), dialect::toMetaData);
    }



    /**
     * 数据方言
     * @param metadata 元数据
     * @return 方言
     */
    private Dialect getDialect(DataSourceActionExecutor.DataSourceActionMetadata metadata) {
        Expression expression = metadata.getExpression();
        Dialect dialect = expression.getValue(Dialect.class);
        if(null != dialect) {
            return dialect;
        }

        DataSource dataSource = metadata.getDataSource();
        return Dialect.create(dataSource);
    }

}
