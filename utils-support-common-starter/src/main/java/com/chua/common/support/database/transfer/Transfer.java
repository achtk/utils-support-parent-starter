package com.chua.common.support.database.transfer;

import com.chua.common.support.database.transfer.collection.SinkTable;
import com.chua.common.support.function.DisposableAware;
import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.utils.ThreadUtils;
import com.chua.common.support.value.DataMapping;
import lombok.Builder;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * 传输器
 *
 * @author Administrator
 */
@Builder
@Data
public class Transfer {

    @Builder.Default
    private SinkConfig sinkConfig = SinkConfig.builder().build();

    @Builder.Default
    private DataMapping dataMapping = DataMapping.builder().build();

    /**
     * 传输数据
     */
    public void transferTo(WriterChannel writerChannel, ReaderChannel... readerChannels) {
        writerChannel.register(sinkConfig).register(dataMapping);
        for (ReaderChannel readerChannel : readerChannels) {
            readerChannel.register(sinkConfig).register(dataMapping);
            if (readerChannel instanceof InitializingAware) {
                ((InitializingAware) readerChannel).afterPropertiesSet();
            }
        }

        if(writerChannel instanceof InitializingAware) {
            ((InitializingAware) writerChannel).afterPropertiesSet();
        }

        List<Future<Boolean>> queue = new LinkedList<>();
        ExecutorService executorService = ThreadUtils.newMinThreadExecutor(Runtime.getRuntime().availableProcessors() * 2 - 1, readerChannels.length);
        while (!writerChannel.isFinish()) {
            SinkTable sinkTable = writerChannel.createSinkTable();
            if (null == sinkTable) {
                continue;
            }

            for (ReaderChannel readerChannel : readerChannels) {
                queue.add(executorService.submit(() -> {
                    readerChannel.read(sinkTable);
                    return true;
                }));
            }
        }

        executorService.shutdown();
        for (Future<Boolean> future : queue) {
            try {
                future.get();
            } catch (Exception ignored) {
            }
        }
        for (ReaderChannel readerChannel : readerChannels) {
            if (readerChannel instanceof DisposableAware) {
                ((DisposableAware) readerChannel).destroy();
            }
        }

        if(writerChannel instanceof DisposableAware) {
            ((DisposableAware) writerChannel).destroy();
        }

        finish(writerChannel, readerChannels);
    }

    /**
     * 完成
     *
     * @param writerChannel  输出
     * @param readerChannels 输入
     */
    private void finish(WriterChannel writerChannel, ReaderChannel[] readerChannels) {
        try {
            writerChannel.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        for (ReaderChannel readerChannel : readerChannels) {
            try {
                readerChannel.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

}
