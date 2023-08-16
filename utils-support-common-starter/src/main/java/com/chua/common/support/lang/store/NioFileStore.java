package com.chua.common.support.lang.store;

import com.chua.common.support.function.InitializingAware;
import com.chua.common.support.function.Joiner;
import com.chua.common.support.lang.date.constant.DateFormatConstant;
import com.chua.common.support.lang.store.plugin.RetentionDaysPlugin;
import com.chua.common.support.task.cache.CacheConfiguration;
import com.chua.common.support.task.cache.Cacheable;
import com.chua.common.support.utils.FileUtils;
import com.chua.common.support.utils.ThreadUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

/**
 * 文件存储
 * @author CH
 */
public class NioFileStore implements FileStore, Runnable, InitializingAware {

    private final ScheduledExecutorService executor = ThreadUtils.newScheduledThreadPoolExecutor(1, "nio-file-store-check");
    private final ExecutorService runExecutor = ThreadUtils.newProcessorThreadExecutor("nio-file-store-writer");
    private final StoreConfig storeConfig;
    private final File file;

    private final Map<String, Queue<String>> queue = new ConcurrentHashMap<>(10);

    private final AtomicBoolean status = new AtomicBoolean(false);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DateFormatConstant.YYYYMMDD);

    final Cacheable CACHEABLE = Cacheable.auto(CacheConfiguration.builder().expireAfterWrite(
            (int) TimeUnit.DAYS.toSeconds(1)).build());

    public NioFileStore(String path, StoreConfig storeConfig) {
        this.file = new File(path);
        this.storeConfig = storeConfig;
        afterPropertiesSet();
        executor.schedule(this, 0, TimeUnit.SECONDS);
    }

    @Override
    public void close() throws Exception {
        status.set(false);
        ThreadUtils.closeQuietly(executor);
        ThreadUtils.closeQuietly(runExecutor);
    }

    @Override
    public void run() {
        new RetentionDaysPlugin().doWith(file, storeConfig);
    }

    @Override
    public void afterPropertiesSet() {
        FileUtils.mkdir(file);
      status.set(true);
      runExecutor.execute(() -> {
          while (status.get()) {
              for (Map.Entry<String, Queue<String>> entry : queue.entrySet()) {
                  Queue<String> stringQueue = entry.getValue();
                  if(stringQueue.size() > 100) {
                      List<String> rs = new LinkedList<>();
                      String poll = null;
                      while ((poll = stringQueue.poll()) != null) {
                          rs.add(poll);
                      }
                      try {
                          FileUtils.write(new File(entry.getKey()), Joiner.on("\r\n").join(rs), StandardCharsets.UTF_8, true);
                      } catch (Exception ignored) {
                      }
                  }
              }
              ThreadUtils.sleepSecondsQuietly(0);
          }
      });
    }

    @Override
    public void write(String message, String... parent) {
        String key = Joiner.on("-").join(parent);
        File director = check(parent, key);
        File file = new File(director, key + ".log");

        queue.computeIfAbsent(file.getAbsolutePath(), it -> new LinkedBlockingQueue<>(1000)).add(message);
    }

    private File check(String[] parent, String key) {
        return (File) CACHEABLE.getOrPut(key, (Supplier<File>) () -> {
            File file1 = new File(file, FORMATTER.format(LocalDate.now()));
            FileUtils.mkdir(file1);
            for (String s : parent) {
                file1 = new File(file1, s);
                FileUtils.mkdir(file1);
            }
            return file1;
        }).getValue();

    }
}
