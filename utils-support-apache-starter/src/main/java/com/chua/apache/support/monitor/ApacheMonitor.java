package com.chua.apache.support.monitor;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.monitor.AbstractMonitor;
import com.chua.common.support.monitor.NotifyMessage;
import com.chua.common.support.monitor.NotifyType;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * apache
 *
 * @author CH
 */
@Spi("apache")
public class ApacheMonitor extends AbstractMonitor {
    private FileAlterationMonitor monitor;

    @Override
    public void preStart() {

    }

    @Override
    public void afterStart() {
        String url = configuration.url();
        File file = new File(url);
        FileAlterationObserver observer = null;
        if (file.isFile()) {
            observer = new FileAlterationObserver(file.getParent(), new NameFileFilter(file.getName()));
        } else {
            observer = new FileAlterationObserver(url);
        }

        observer.addListener(new FileAlterationListener() {
            @Override
            public void onStart(FileAlterationObserver observer) {
            }

            @Override
            public void onDirectoryCreate(File directory) {
            }

            @Override
            public void onDirectoryChange(File directory) {
            }

            @Override
            public void onDirectoryDelete(File directory) {
            }

            @Override
            public void onFileCreate(File file) {
                notifyMessage(new NotifyMessage(NotifyType.CREATE, file.getAbsolutePath()));
            }

            @Override
            public void onFileChange(File file) {
                notifyMessage(new NotifyMessage(NotifyType.MODIFY, file.getAbsolutePath()));
            }

            @Override
            public void onFileDelete(File file) {
                notifyMessage(new NotifyMessage(NotifyType.DELETE, file.getAbsolutePath()));
            }

            @Override
            public void onStop(FileAlterationObserver observer) {


            }
        });
        this.monitor = new FileAlterationMonitor(TimeUnit.SECONDS.toMillis(interval), observer);
        try {
            monitor.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void preStop() {

    }

    @Override
    public void afterStop() {
        try {
            monitor.stop();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
