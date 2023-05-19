package com.chua.common.support.engine;

import com.chua.common.support.engine.config.EngineConfig;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.StringUtils;

import java.util.List;

/**
 * 引擎
 *
 * @author CH
 */
public interface Engine<T> extends AutoCloseable {
    /**
     * 查询引擎
     *
     * @param <T>    类型
     * @param target 目标类型
     * @return 查询引擎
     */
    static <T> QueryEngine<T> newQueryEngine(Class<T> target) {
        return newQueryEngine(null, target, new EngineConfig());
    }

    /**
     * 查询引擎
     *
     * @param <T>          类型
     * @param target       目标类型
     * @param engineConfig 配置
     * @return 查询引擎
     */
    static <T> QueryEngine<T> newQueryEngine(Class<T> target, EngineConfig engineConfig) {
        return newQueryEngine(null, target, engineConfig);
    }

    /**
     * 查询引擎
     *
     * @param target 目标类型
     * @param engine 名称
     * @param <T>    类型
     * @return 查询引擎
     */
    @SuppressWarnings("ALL")
    static <T> QueryEngine<T> newQueryEngine(String engine, Class<T> target) {
        return ServiceProvider.of(QueryEngine.class).getNewExtension(StringUtils.defaultString(engine, "cq"), target, new EngineConfig());
    }

    /**
     * 查询引擎
     *
     * @param target       目标类型
     * @param engine       名称
     * @param engineConfig 配置
     * @param <T>          类型
     * @return 查询引擎
     */
    @SuppressWarnings("ALL")
    static <T> QueryEngine<T> newQueryEngine(String engine, Class<T> target, EngineConfig engineConfig) {
        return ServiceProvider.of(QueryEngine.class).getNewExtension(
                StringUtils.defaultString(engine, "cq"), "sqlite", target, engineConfig);
    }

    /**
     * 查询引擎
     *
     * @param <T>    类型
     * @param target 目标类型
     * @return 查询引擎
     */
    static <T> FullTextEngine<T> newFulltextEngine(Class<T> target) {
        return newFulltextEngine(null, target, new EngineConfig());
    }

    /**
     * 查询引擎
     *
     * @param <T>          类型
     * @param target       目标类型
     * @param engineConfig 配置
     * @return 查询引擎
     */
    static <T> FullTextEngine<T> newFulltextEngine(Class<T> target, EngineConfig engineConfig) {
        return newFulltextEngine(null, target, engineConfig);
    }

    /**
     * 查询引擎
     *
     * @param target 目标类型
     * @param engine 名称
     * @param <T>    类型
     * @return 查询引擎
     */
    @SuppressWarnings("ALL")
    static <T> FullTextEngine<T> newFulltextEngine(String engine, Class<T> target) {
        return ServiceProvider.of(FullTextEngine.class).getNewExtension(
                StringUtils.defaultString(engine, "cq"), "sqlite", target, new EngineConfig());
    }

    /**
     * 查询引擎
     *
     * @param target       目标类型
     * @param engine       名称
     * @param engineConfig 配置
     * @param <T>          类型
     * @return 查询引擎
     */
    @SuppressWarnings("ALL")
    static <T> FullTextEngine<T> newFulltextEngine(String engine, Class<T> target, EngineConfig engineConfig) {
        return ServiceProvider.of(FullTextEngine.class).getNewExtension(StringUtils.defaultString(engine, "cq"), target, engineConfig);
    }

    /**
     * 基础配置
     *
     * @param engineConfig 配置
     * @return 基础配置
     */
    Engine<T> config(EngineConfig engineConfig);

    /**
     * 注册信息
     *
     * @param t 信息
     * @return 是否成功
     */
    boolean addAll(T... t);

    /**
     * 注册信息
     *
     * @param t 信息
     * @return 是否成功
     */
    boolean addAll(List<T> t);

    /**
     * 注册信息
     *
     * @param t 信息
     * @return 是否成功
     */
    boolean remove(T t);
}
