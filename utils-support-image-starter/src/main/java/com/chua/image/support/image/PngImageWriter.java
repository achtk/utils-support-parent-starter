package com.chua.image.support.image;

import com.chua.common.support.image.image.AbstractImageReader;
import com.chua.common.support.image.image.AbstractImageWriter;
import com.chua.common.support.spi.Spi;

import java.net.URL;

/**
 * png 图像
 * @author CH
 */
@Spi({"png"})
public class PngImageWriter extends AbstractImageWriter {

    @Override
    public String getType() {
        return "png";
    }

}
