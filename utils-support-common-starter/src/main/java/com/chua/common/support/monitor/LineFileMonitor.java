package com.chua.common.support.monitor;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.file.tailer.Tailer;
import com.chua.common.support.file.tailer.TailerListenerAdapter;

import java.io.File;

/**
 * 行文件
 *
 * @author CH
 */
@Spi("tailer")
public class LineFileMonitor extends AbstractMonitor {
    private Tailer tailer;

    @Override
    public void preStart() {
        this.tailer = Tailer.create(new File(configuration.url()), new TailerListenerAdapter() {
            @Override
            public void handle(String line) {
                notifyMessage(NotifyMessage.builder().type(NotifyType.CREATE).message(line).build());
            }

            @Override
            public void handle(Exception ex) {
                notifyMessage(ex);
            }
        }, interval);
    }

    @Override
    public void start() {
        status.set(true);
        preStart();
        executorService.execute(tailer::run);
    }

    @Override
    public void afterStart() {

    }

    @Override
    public void preStop() {
        tailer.stop();
    }

    @Override
    public void afterStop() {

    }
}
