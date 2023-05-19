package com.chua.zxing.support.bar.codegen.qrcode;


import com.chua.zxing.support.bar.codegen.AbstractGenerator;
import com.chua.zxing.support.bar.codegen.Codectx;
import com.chua.zxing.support.bar.codegen.QrcodeGenerateException;
import com.chua.zxing.support.bar.codegen.qrcode.Qrcode.Logo;
import com.chua.zxing.support.bar.codegen.qrcode.QrcodeWriter.QRCodeBitMatrix;
import com.chua.zxing.support.bar.codegen.utils.FileUtils;
import com.chua.zxing.support.bar.codegen.utils.HttpUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.Version;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class SimpleQrcodeGenerator extends AbstractGenerator implements QrcodeGenerator {

	private static final ThreadQrcode QRCODE = new ThreadQrcode();

	private final QrcodeConfig qrcodeConfig;

	public SimpleQrcodeGenerator() {
		this(new QrcodeConfig());
	}

	public SimpleQrcodeGenerator(QrcodeConfig qrcodeConfig) {
		super();
		this.qrcodeConfig = qrcodeConfig;
	}

	@Override
	public QrcodeGenerator setLogo(String path, boolean remote) {
		QRCODE.setLogo(path, remote);
		return this;
	}

	@Override
	public QrcodeConfig getQrcodeConfig() {
		return this.qrcodeConfig;
	}

	@Override
	public QrcodeGenerator generate(String content) {
		QRCODE.setImage(generateQrcode(getQrcodeConfig(), QRCODE.getLogo(), content));
		return this;
	}

	@Override
	public QrcodeGenerator generate(String content, String logoPath) {
		return (QrcodeGenerator) setLogo(logoPath).generate(content);
	}

	@Override
	public BufferedImage getImage(boolean clear) {
		try {
			return QRCODE.getImage();
		} finally {
			if (clear) {
				clear();
			}
		}
	}

	@Override
	public void clear() {
		QRCODE.remove();
	}

	@Override
	public boolean toFile(String pathname) throws IOException {
		try {
			return ImageIO.write(QRCODE.getImage(), Codectx.IMAGE_TYPE, new File(pathname));
		} finally {
			clear();
		}
	}

	@Override
	public boolean toStream(OutputStream output) throws IOException {
		try {
			return ImageIO.write(QRCODE.getImage(), Codectx.IMAGE_TYPE, output);
		} finally {
			clear();
		}
	}

	/**
	 * Generate implement.
	 * @param config {@link QrcodeConfig}
	 * @param logo {@link Logo}
	 * @param content qrcode content
	 * @return BufferedImage instance
	 */
	private static BufferedImage generateQrcode(QrcodeConfig config, Logo logo, final String content) {
		try {

			QRCodeBitMatrix m = new QrcodeWriter().encodeX(content, BarcodeFormat.QR_CODE, config.getWidth(),
					config.getHeight(), config.getHints());

			BufferedImage image = toBufferedImage(m, config);

			/**
			 * render image margin
			 */
			image = setRadius(image, config.getBorderRadius(), config.getBorderSize(), config.getBorderColor(),
					config.getBorderStyle(), config.getBorderDashGranularity(), config.getMargin());

			/**
			 * insert logo in the middle of the image
			 */
			if (logo != null && logo.getPath() != null && logo.getPath().length() > 0) {
				byte[] bytes;
				if (logo.isRemote()) {
					bytes = HttpUtils.readStreamToByteArray(logo.getPath());
				} else {
					bytes = FileUtils.readFileToByteArray(new File(logo.getPath()));
				}
				if (bytes.length > 0) {
					addLogo(image, bytes, config.getLogoConfig());
				}
			}
			return image;

		} catch (Exception e) {
			throw new QrcodeGenerateException(e);
		}
	}

	public static void addLogo(final BufferedImage image, final byte[] logo, final LogoConfig config)
			throws IOException {
		ByteArrayInputStream input = new ByteArrayInputStream(logo);
		BufferedImage srcImage = image, logoImage = null;
		logoImage = ImageIO.read(input);
		if (logoImage == null) {
			return;
		}
		/**
		 * calculate logo width and height
		 */
		final int ratio = config.getRatio();
		final int logoWidth = logoImage.getWidth(), logoHeight = logoImage.getHeight();
		float ratioWidthOfCodeImage = srcImage.getWidth() / (float) ratio;
		float ratioHeightOfCodeImage = srcImage.getHeight() / (float) ratio;
		float width = logoWidth > ratioWidthOfCodeImage ? ratioWidthOfCodeImage : logoWidth;
		float height = logoHeight > ratioHeightOfCodeImage ? ratioHeightOfCodeImage : logoHeight;
		/**
		 * get logo panel position
		 */
		int padding = config.getPadding() * 2;
		int margin = config.getMargin() * 2;
		float w = width + padding + margin, h = height + padding + margin;
		float positionX = (srcImage.getWidth() - w) / 2;
		float positionY = (srcImage.getHeight() - h) / 2;

		Shape shape = null;

		if (config.isRectangle()) {
			shape = new RoundRectangle2D.Float(positionX, positionY, w, h, config.getPanelArcWidth(),
					config.getPanelArcHeight());
		} else if (config.isCircle()) {
			shape = new Ellipse2D.Float(positionX, positionY, w, h);
		}

		if (shape == null) {
			return;
		}

		Graphics2D graphics = srcImage.createGraphics();
		graphics.setColor(getColor(config.getPanelColor()));
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.fill(shape);
		/**
		 * get logo position
		 */
		positionX += config.getMargin();
		positionY += config.getMargin();
		/**
		 * render logo background
		 */
		if (config.isRectangle()) {
			shape = new RoundRectangle2D.Float(positionX, positionY, width + padding, height + padding,
					config.getArcWidth(), config.getArcHeight());
		} else if (config.isCircle()) {
			shape = new Ellipse2D.Float(positionX, positionY, width + padding, height + padding);
			// Cut into a circle
			logoImage = clip(logoImage, Math.max(logoWidth, logoHeight));
		}
		graphics.setColor(getColor(config.getBackgroundColor()));
		graphics.fill(shape);
		/**
		 * draw logo
		 */
		positionX += config.getPadding() + 1; // 1px offset is added to ensure center position..
		positionY += config.getPadding() + 1; // 1px offset is added to ensure center position..
		graphics.drawImage(logoImage.getScaledInstance((int) width, (int) height, Image.SCALE_SMOOTH),
				(int) positionX, (int) positionY, null);
		/**
		 * border
		 */
		graphics.setStroke(new BasicStroke(config.getBorderSize()));
		graphics.setColor(getColor(config.getBorderColor()));
		/**
		 * anti-aliasing
		 */
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics.draw(shape);

		/**
		 * flush
		 */
		graphics.dispose();
		logoImage.flush();
		srcImage.flush();
	}

	private static BufferedImage toBufferedImage(final QRCodeBitMatrix matrix, final QrcodeConfig config) {

		BitMatrix bitMatrix = matrix.getBitMatrix();

		Version version = matrix.getQrcode().getVersion();

		int width = bitMatrix.getWidth(), height = bitMatrix.getHeight();

		/**
		 * Formula for calculating number of modules on each side: 
		 * (Version - 1) * 4 + 21.
		 */
		int modules = (version.getVersionNumber() - 1) * 4 + 21;
		int[] topLeftOnBit = bitMatrix.getTopLeftOnBit();

		QreyesPosition position = new QreyesPosition(modules, topLeftOnBit);

		int moduleHeight = position.getModuleHeight(height);
		int moduleWidth = position.getModuleWidth(width);

		/**
		 * Calculating codeEyes position.
		 */
		int leftStartX = topLeftOnBit[0] + moduleWidth * QreyesRenderStrategy.POINT_BORDER.getStart();
		int leftEndX = topLeftOnBit[0] + moduleWidth * QreyesRenderStrategy.POINT_BORDER.getEnd();
		int topStartY = topLeftOnBit[1] + moduleHeight * QreyesRenderStrategy.POINT_BORDER.getStart();
		int topEndY = topLeftOnBit[1] + moduleHeight * QreyesRenderStrategy.POINT_BORDER.getEnd();
		int rightStartX = topLeftOnBit[0] + moduleWidth * (modules - QreyesRenderStrategy.POINT_BORDER.getEnd());
		int rightEndX = width - topLeftOnBit[0] - moduleWidth * QreyesRenderStrategy.POINT_BORDER.getStart();
		int bottomStartY = height - topLeftOnBit[1] - moduleHeight * QreyesRenderStrategy.POINT_BORDER.getEnd();
		int bottomEndY = height - topLeftOnBit[1] - moduleHeight * QreyesRenderStrategy.POINT_BORDER.getStart();

		/**
		 * Build image.
		 */
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		// custom color.
		int masterColor = getColor(config.getMasterColor()).getRGB();
		int slaveColor = getColor(config.getSlaveColor()).getRGB();

		for (int y = 0; y < height; y++) {

			for (int x = 0; x < width; x++) {
				// top left
				if (x >= leftStartX && x < leftEndX && y >= topStartY && y < topEndY) {
				}
				// top right
				else if (x >= rightStartX && x < rightEndX && y >= topStartY && y < topEndY) {
				}
				// bottom left
				else if (x >= leftStartX && x < leftEndX && y >= bottomStartY && y < bottomEndY) {
				}
				// non codeEyes region
				else {
					image.setRGB(x, y, bitMatrix.get(x, y) ? masterColor : slaveColor);
				}
			}
		}

		position.setPosition(leftStartX, leftEndX, topStartY, topEndY, rightStartX, rightEndX, bottomStartY,
				bottomEndY);
		Color border = getColor(config.getCodeEyesBorderColor());
		Color point = getColor(config.getCodeEyesPointColor());
		QreyesFormat format = config.getCodeEyesFormat();
		new MultiFormatQreyesRenderer().render(image, format, position, new Color(slaveColor), border, point);

		return image;
	}

}
