package com.chua.common.support.extra.quickio.core;

import com.chua.common.support.extra.quickio.api.Collection;
import com.chua.common.support.extra.quickio.api.Db;

import java.nio.file.Paths;

import static com.chua.common.support.extra.quickio.core.Constants.DB_PATH;
/**
 * 配置
 * @author CH
 */
final class QuDb implements Db {

    private final EngineIo engine;
    private final Indexer indexer;


    QuDb(Config config) {
        if (config.path == null) {
            config.path = DB_PATH;
        } else {
            config.path = Paths.get(config.path, DB_PATH).toAbsolutePath().toString();
        }
        engine = new EngineLevel().open(config);
        indexer = new Indexer(new EngineLevel(), config.path, config.name);
        Runtime.getRuntime().addShutdownHook(new Thread(this::close));
    }


    QuDb(String name) {
        this(Config.of(c -> c.name(name)));
    }


    @Override
    public void close() {
        engine.close();
        indexer.close();
    }


    @Override
    public void destroy() {
        engine.destroy();
        indexer.destroy();
    }


    @Override
    public <T extends IoEntity> Collection<T> collection(Class<T> clazz) {
        return new QuCollection<>(clazz, engine, indexer);
    }

}