package com.chua.common.support.monitor;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.reflection.FieldStation;
import com.sun.nio.file.SensitivityWatchEventModifier;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 * 文件系统监听
 *
 * @author CH
 */
@Spi("filesystem")
@Slf4j
public class FilesystemMonitor extends AbstractMonitor {

    protected static final WatchEvent.Kind[] DEFAULT_KIND = new WatchEvent.Kind[]{
            ENTRY_MODIFY, ENTRY_DELETE, ENTRY_CREATE
    };
    private final WatchService watchService = newWatchService();
    protected WatchEvent.Kind[] kinds = DEFAULT_KIND;

    /**
     * 初始化监听
     *
     * @return 监听
     */
    private static WatchService newWatchService() {
        Optional<WatchService> watchService = null;
        FileSystem fileSystem = FileSystems.getDefault();
        try {
            watchService = Optional.of(fileSystem.newWatchService());
            return watchService.get();
        } catch (IOException e) {
            if (log.isErrorEnabled()) {
                log.error(e.getMessage(), e);
            }
            watchService = Optional.empty();
        }
        return null;
    }

    @Override
    public void preStart() {
        Path path = Paths.get(configuration.url());
        try {
            path.register(watchService, kinds, SensitivityWatchEventModifier.HIGH);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void afterStart() {
        while (isRunning()) {
            WatchKey watchKey = null;
            try {
                //每隔{interval}秒拉取监听key
                //等待，超时就返回
                watchKey = watchService.poll(interval, TimeUnit.SECONDS);
                //监听key为null,则跳过
                if (watchKey == null) {
                    continue;
                }
                FieldStation fieldStation = FieldStation.of(watchKey);
                Path dir = (Path) fieldStation.getFieldValue("dir");
                List<WatchEvent<?>> watchEvents = watchKey.pollEvents();
                for (WatchEvent<?> event : watchEvents) {
                    Object context = event.context();
                    NotifyType notifyType = null;
                    if (event.kind() == ENTRY_MODIFY) {
                        notifyType = NotifyType.MODIFY;
                    } else if (event.kind() == ENTRY_CREATE) {
                        notifyType = NotifyType.CREATE;
                    } else if (event.kind() == ENTRY_DELETE) {
                        notifyType = NotifyType.DELETE;
                    }
                    notifyMessage(new NotifyMessage(notifyType, configuration.url() + "/" + context.toString()));
                }

            } catch (Throwable e) {
                notifyMessage(e);
            } finally {
                if (null != watchKey) {
                    watchKey.reset();
                }
            }
        }
    }

    @Override
    public void preStop() {

    }

    @Override
    public void afterStop() {
        try {
            watchService.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
