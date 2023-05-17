package com.chua.image.support.image;

import com.chua.common.support.image.image.AbstractImageReader;
import com.chua.common.support.image.image.AbstractImageWriter;
import com.chua.common.support.spi.Spi;
import com.idrsolutions.image.png.PngCompressor;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

/**
 * png 图像
 * @author CH
 */
@Spi({"png"})
public class PngImageReader extends AbstractImageReader {

    public PngImageReader(URL imageUrl) {
        super(imageUrl);
    }

}
