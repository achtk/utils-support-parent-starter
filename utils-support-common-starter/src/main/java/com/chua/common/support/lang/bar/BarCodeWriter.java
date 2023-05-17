package com.chua.common.support.lang.bar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * 条形码/二维码输出
 *
 * @author CH
 * @version 1.0.0
 */
public interface BarCodeWriter {
    /**
     * 配置
     *
     * @param barCodeBuilder 配置
     * @return this
     */
    BarCodeWriter config(BarCodeBuilder barCodeBuilder);

    /**
     * 文件
     *
     * @param file 文件
     * @return 文件
     * @throws Exception ex
     */
    default File toFile(String file) throws Exception {
        File tmp = new File(file);
        try (FileOutputStream fos = new FileOutputStream(tmp)) {
            toStream(fos);
        }
        return tmp;
    }

    /**
     * 流
     *
     * @param outputStream 流
     * @throws Exception ex
     */
    void toStream(OutputStream outputStream) throws Exception;
}
