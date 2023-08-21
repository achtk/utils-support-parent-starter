package com.chua.common.support.lang.spider.pipeline;

import com.chua.common.support.lang.function.ToStringBuilder;
import com.chua.common.support.lang.spider.Task;
import com.chua.common.support.lang.spider.model.HasKey;
import com.chua.common.support.lang.spider.utils.FilePersistentBase;
import com.chua.common.support.utils.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Store results objects (page models) to files in plain format.<br>
 * Use model.getKey() as file name if the model implements HasKey.<br>
 * Otherwise use SHA1 as file name.
 *
 * @author code4crafter@gmail.com <br>
 * @since 0.3.0
 */
public class FilePageModelPipeline extends FilePersistentBase implements PageModelPipeline {

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * new JsonFilePageModelPipeline with default path "/data/webmagic/"
     */
    public FilePageModelPipeline() {
        setPath("/data/webmagic/");
    }

    public FilePageModelPipeline(String path) {
        setPath(path);
    }

    @Override
    public void process(Object o, Task task) {
        String path = this.path + PATH_SEPERATOR + task.getUUID() + PATH_SEPERATOR;
        try {
            String filename;
            if (o instanceof HasKey) {
                filename = path + ((HasKey) o).key() + ".html";
            } else {
                filename = path + DigestUtils.md5Hex(ToStringBuilder.reflectionToString(o)) + ".html";
            }
            PrintWriter printWriter = new PrintWriter(new FileWriter(getFile(filename)));
            printWriter.write(ToStringBuilder.reflectionToString(o));
            printWriter.close();
        } catch (IOException e) {
            logger.warn("write file error", e);
        }
    }
}
