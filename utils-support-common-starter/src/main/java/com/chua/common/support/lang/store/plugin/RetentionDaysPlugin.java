package com.chua.common.support.lang.store.plugin;

import com.chua.common.support.lang.date.DateUtils;
import com.chua.common.support.lang.store.StoreConfig;
import com.chua.common.support.utils.FileUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.time.LocalDate;

/**
 * 保留天数
 *
 * @author CH
 */
@Slf4j
public class RetentionDaysPlugin implements Plugin {
    @Override
    public void doWith(File file, StoreConfig storeConfig) {
        File[] files = file.listFiles();
        if (null == files) {
            return;
        }

        LocalDate now = LocalDate.now();
        for (File file1 : files) {
            String name = file1.getName();
            try {
                LocalDate localDate1 = DateUtils.toLocalDate(name, "yyyyMMdd");
                if (!isRemove(localDate1, now, storeConfig)) {
                    continue;
                }

                doClear(file1);
            } catch (Exception ignored) {
                //NOTHING
            }
        }
    }

    /**
     * 清除文件
     * @param file1 文件
     */
    private void doClear(File file1) {
        try {
            FileUtils.forceDelete(file1);
        } catch (Exception e) {
            log.error("清除失败. 等待下次任务执行:{}", e.getLocalizedMessage());
        }
    }

    /**
     * 是否删除
     *
     * @param fileDay     文件
     * @param now         当前时间
     * @param storeConfig 配置
     * @return 结果
     */
    private boolean isRemove(LocalDate fileDay, LocalDate now, StoreConfig storeConfig) {
        int retentionDays = storeConfig.getRetentionDays();
        return now.getDayOfYear() - fileDay.getDayOfYear() > retentionDays;
    }
}
