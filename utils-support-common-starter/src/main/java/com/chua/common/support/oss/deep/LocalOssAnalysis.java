package com.chua.common.support.oss.deep;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.annotations.SpiOption;
import com.chua.common.support.lang.page.Page;
import com.chua.common.support.oss.handler.OssHandler;
import com.chua.common.support.oss.node.OssNode;
import com.chua.common.support.pojo.OssSystem;
import com.chua.common.support.spi.ServiceProvider;

/**
 * 解析器
 * @author CH
 */
@Spi("local")
@SpiOption("本地")
public class LocalOssAnalysis implements OssAnalysis{
    @Override
    public Page<OssNode> analysis(OssSystem ossSystem, String ossBucket, String path, String name, Integer pageNum, Integer pageSize) {
        return ServiceProvider.of(OssHandler.class).getDeepNewExtension(path).analysis(ossSystem, ossBucket, path, name, pageNum, pageSize);
    }
}
