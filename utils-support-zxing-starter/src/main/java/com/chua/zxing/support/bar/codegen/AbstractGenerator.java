package com.chua.zxing.support.bar.codegen;

import com.chua.zxing.support.bar.codegen.Codectx.BorderStyle;
import com.google.zxing.common.BitMatrix;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

/**
 * AbstractGenerator
 *
 * @author Bosco.Liao
 * @since 1.0.0
 */
public abstract class AbstractGenerator {

	/**
	 * Translate the color from hex color code.
	 * <p>Usage: getColor("#FFFFFF") or getColor("FFFFFF").</p>
	 *
	 * @param hexColor hex color code
	 * @return An instance of color.
	 */
	public static final Color getColor(final String hexColor) {
		String hexc = hexColor;
		Color result = Color.WHITE;
		if (hexc != null && !"".equals(hexc)) {
			if (hexc.startsWith("#")) {
				hexc = hexc.replaceFirst("#", "");
			}
			result = new Color(Integer.parseInt(hexc, 16));
		}
		return result;
	}

	/**
	 * In order to completely eliminate the white edges, it must be redraw
	 * BitMatrix, determined by the parameter 'margin'.
	 *
	 * @param bitMatrix An instance of BitMatrix.
	 * @param padding setting padding
	 * @return A BitMatrix instance after redrawing.
	 */
	public static BitMatrix redrawBitMatrix(BitMatrix bitMatrix, final int padding) {
		int tempPadding = padding * 2;
		/**
		 * [left,top,width,height]
		 */
		final int[] attributes = bitMatrix.getEnclosingRectangle();
		int rawWidth = attributes[2] + tempPadding;
		int rawHeight = attributes[3] + tempPadding;
		BitMatrix redrawBM = new BitMatrix(rawWidth, rawHeight);
		redrawBM.clear();
		/**
		 * redraw start
		 */
		int left = attributes[0], top = attributes[1];
		for (int x = padding; x < rawWidth - padding; x++) {
			for (int y = padding; y < rawHeight - padding; y++) {
				if (bitMatrix.get(x + left - padding, y + top - padding)) {
					redrawBM.set(x, y);
				}
			}
		}
		return redrawBM;
	}

	/**
	 * Sets the border-radius attribute to BufferedImage.
	 *
	 * @param image An instance of BufferedImage.
	 * @param radius Attribute value of border-radius.
	 * @param borderSize setting border size
	 * @param borderColor setting border color
	 * @param style BorderStyle.SOLID || BorderStyle.DASHED
	 * @param borderDashGranularity setting border dashing 
	 * @param margin setting margin
	 * @return BufferedImage instance with cliping.
	 */
	public static BufferedImage setRadius(BufferedImage image, int radius, int borderSize, String borderColor,
										  BorderStyle style, int borderDashGranularity, int margin) {

		int width = image.getWidth(), height = image.getHeight();
		int canvasWidth = width + margin * 2, canvasHeight = height + margin * 2;
		BufferedImage canvasImage = new BufferedImage(canvasWidth, canvasHeight, BufferedImage.TYPE_INT_ARGB);

		Graphics2D graphics = canvasImage.createGraphics();
		graphics.setComposite(AlphaComposite.Src);
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.setColor(Color.WHITE);
		graphics.fill(new RoundRectangle2D.Float(0, 0, canvasWidth, canvasHeight, radius, radius));
		graphics.setComposite(AlphaComposite.SrcAtop);
		graphics.drawImage(clip(image, radius), margin, margin, null);

		if (borderSize > 0) {
			Stroke stroke;
			if (BorderStyle.SOLID == style) {
				stroke = new BasicStroke(borderSize);
			} else {
				stroke = new BasicStroke(borderSize, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 10.0f,
						new float[]{borderDashGranularity, borderDashGranularity}, 0.0f);
			}
			graphics.setColor(getColor(borderColor));
			graphics.setStroke(stroke);
			graphics.drawRoundRect(margin, margin, width - 1, height - 1, radius, radius);
		}

		graphics.dispose();
		image.flush();

		return canvasImage;
	}

	public static BufferedImage clip(final BufferedImage image, final int radius) {

		int width = image.getWidth(), height = image.getHeight();

		int max = Math.max(width, height);

		Shape shape = null;

		if (max == radius) { // draw circular
			shape = new Ellipse2D.Float(0, 0, width, height);
		} else {
			shape = new RoundRectangle2D.Float(0, 0, width, height, radius, radius);
		}

		BufferedImage canvasImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = canvasImage.createGraphics();
		graphics.setClip(shape);
		// 抗锯齿
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		graphics.drawImage(image, 0, 0, null);

		graphics.dispose();
		image.flush();

		return canvasImage;
	}

}
