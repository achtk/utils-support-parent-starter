package com.chua.zxing.support.bar.codegen.qrcode;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * QR-Code eyes renderer.
 *
 * @author Bosco.Liao
 * @since 1.3.0
 */
public interface QreyesRenderer {

	/**
	 * Render code-eyes by format.
	 *
	 * @param image
	 * @param format
	 * @param position
	 * @param slave
	 * @param border
	 * @param point
	 */
	void render(BufferedImage image, QreyesFormat format, QreyesPosition position, Color slave, Color border,
				Color point);

	/**
	 * Set code-eyes render shape.
	 *
	 * @param x
	 * @param y
	 * @param w
	 * @param h
	 * @param arcw
	 * @param arch
	 * @return
	 */
	default Shape getPointShape(double x, double y, double w, double h, double arcw, double arch) {
		return new Rectangle2D.Double(x, y, w, h);
	}

	/**
	 * Check the format match target renderer.
	 *
	 * @param format
	 */
	default void checkFormat(QreyesFormat format) {
		return;
	}

}
