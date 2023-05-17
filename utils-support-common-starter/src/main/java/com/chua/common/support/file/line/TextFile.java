package com.chua.common.support.file.line;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.file.AbstractResourceFile;
import com.chua.common.support.file.LineFile;
import com.chua.common.support.file.ResourceFileConfiguration;
import com.chua.common.support.utils.IoUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;

/**
 * txt
 *
 * @author CH
 */
@Spi("txt")
public class TextFile extends AbstractResourceFile implements LineFile<String> {

    public TextFile(ResourceFileConfiguration resourceFileConfiguration) {
        super(resourceFileConfiguration);
    }

    @Override
    public void line(Function<String, Boolean> line, int skip) throws IOException {
        int count = 0;
        try (InputStream inputStream = openInputStream()) {
            IoUtils.LineIterator lineIterator = IoUtils.lineIterator(inputStream, resourceConfiguration.getCharset());
            while (lineIterator.hasNext()) {
                if (count++ < skip) {
                    continue;
                }
                String next = lineIterator.next();
                Boolean apply = line.apply(next);
                if (apply) {
                    break;
                }
            }
        }
    }
}
