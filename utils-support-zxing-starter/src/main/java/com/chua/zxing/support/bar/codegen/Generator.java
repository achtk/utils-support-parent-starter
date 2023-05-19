package com.chua.zxing.support.bar.codegen;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Generator interface.
 *
 * @author Bosco.Liao
 * @since 1.0.0
 */
public interface Generator {

	Generator generate(String content);

	BufferedImage getImage();

	boolean toFile(String pathname) throws IOException;

	boolean toStream(OutputStream output) throws IOException;

	void clear();

}
