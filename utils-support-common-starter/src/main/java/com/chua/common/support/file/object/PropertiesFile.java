package com.chua.common.support.file.object;

import com.chua.common.support.bean.BeanBinder;
import com.chua.common.support.bean.ProfileMapHandler;
import com.chua.common.support.file.resource.AbstractResourceFile;
import com.chua.common.support.file.resource.LineFile;
import com.chua.common.support.file.resource.ObjectFile;
import com.chua.common.support.file.resource.ResourceConfiguration;
import com.chua.common.support.reflect.Reflect;
import com.chua.common.support.spi.Spi;
import com.chua.common.support.utils.IoUtils;
import com.google.common.base.Strings;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.function.Function;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_EQUALS;

/**
 * @author CH
 */
@Spi("properties")
public class PropertiesFile extends AbstractResourceFile implements ObjectFile, LineFile<Properties> {

    public PropertiesFile(ResourceConfiguration resourceConfiguration) {
        super(resourceConfiguration);
    }

    @Override
    public <E> E parse(Class<E> target) {
        try (InputStreamReader isr = new InputStreamReader(openInputStream(), resourceConfiguration.getCharset())) {
            Properties properties = new Properties();
            properties.load(isr);
            return BeanBinder.of(new ProfileMapHandler(properties)).bind(target).getValue();
        } catch (IOException ignored) {
        }
        return Reflect.create(target).getObjectValue().getObject();
    }

    @Override
    public <E> List<E> parseArray(Class<E> target) {
        return Collections.singletonList(parse(target));
    }

    @Override
    public void line(Function<Properties, Boolean> line, int skip) throws IOException {
        int count = 0;
        try (InputStream inputStream = openInputStream()) {
            IoUtils.LineIterator lineIterator = IoUtils.lineIterator(inputStream, resourceConfiguration.getCharset());
            while (lineIterator.hasNext()) {
                if (count++ < skip) {
                    continue;
                }

                String next = lineIterator.next();
                String[] split = next.split(SYMBOL_EQUALS, 2);
                if (split.length == 2 && !Strings.isNullOrEmpty(split[1])) {
                    Properties properties = new Properties();
                    properties.put(split[0], split[1]);
                    Boolean aBoolean = line.apply(properties);
                    if (aBoolean) {
                        break;
                    }
                }
            }
        }
    }
}
