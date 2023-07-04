package com.chua.common.support.oss.handler;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.annotations.SpiOption;
import com.chua.common.support.constant.FileType;
import com.chua.common.support.file.tar.Tar;
import com.chua.common.support.file.zip.Zip;
import com.chua.common.support.lang.page.Page;
import com.chua.common.support.lang.page.PageMemData;
import com.chua.common.support.media.MediaType;
import com.chua.common.support.oss.node.OssNode;
import com.chua.common.support.pojo.OssSystem;
import com.chua.common.support.utils.PageUtils;
import com.chua.common.support.utils.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

/**
 * 解析器
 *
 * @author CH
 */
@Spi("tar")
@SpiOption("tar解析器")
public class TarOssHandler implements OssHandler {
    @Override
    public Page<OssNode> analysis(OssSystem ossSystem, String ossBucket, String path, String name, Integer pageNum, Integer pageSize) {
        File file = new File(ossSystem.getOssPath(), path);
        List<OssNode> rs = new LinkedList<>();
        int[] ints = PageUtils.transToStartEnd(pageNum - 1, pageSize);
        int start = ints[0];
        int end = ints[1];
        int index = 0;
        try {
            InputStream inputStream = null;
            if (file.exists()) {
                inputStream = new FileInputStream(file);
            } else {
                inputStream = new URL(ossBucket + "/" + path).openStream();
            }
            Tar tar = new Tar();
            String newName = StringUtils.removeStart(name, "/");
            try (InputStream is = inputStream) {
                tar.unFile(is, it -> {
                    String fileName = it.getName();
                    if (fileName.startsWith(newName) ) {
                        int count = 0;
                            count = StringUtils.count(StringUtils.removeStart(StringUtils.removeEnd(fileName.substring(newName.length()), "/"), "/"), "/");
                        if (count == 0) {
                            if (start <= index && index < end) {
                                MediaType mediaType = it.getMediaType();
                                rs.add(new OssNode(it.getName(),
                                        mediaType.type(),
                                        mediaType.subtype(),
                                        name + it.getName(),
                                        StringUtils.removeSuffix(it.getName().replace(newName, ""), "/"),
                                        LocalDateTime.now(),
                                        it.getFileType() == FileType.FILE,
                                        false));
                            }
                        } else {
                            return true;
                        }
                    }
                    if (end < index) {
                        return true;
                    }
                    return false;
                }, false);
            }
            return PageMemData.of(rs).find(1, rs.size());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
