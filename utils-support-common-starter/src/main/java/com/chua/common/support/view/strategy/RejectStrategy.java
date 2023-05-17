package com.chua.common.support.view.strategy;

import java.io.OutputStream;

/**
 * 拒绝策略
 *
 * @author CH
 * @since 2022/8/3 15:07
 */
public interface RejectStrategy {
    /**
     * 拒绝
     *
     * @param path 预览的文件路径
     * @param mode 模式. download/preview
     * @param os   输出
     */
    void reject(String path, String mode, OutputStream os);
}
