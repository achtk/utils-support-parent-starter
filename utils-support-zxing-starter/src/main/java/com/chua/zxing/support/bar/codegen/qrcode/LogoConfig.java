package com.chua.zxing.support.bar.codegen.qrcode;

import com.chua.zxing.support.bar.codegen.Codectx;
import com.chua.zxing.support.bar.codegen.Codectx.LogoShape;

import java.io.Serializable;


public class LogoConfig implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -2922625672639593125L;

	private int ratio = Codectx.DEFAULT_LOGO_RATIO;

	/**
	 * When the shape is LogoShape.RECTANGLE, the configuration is valid.
	 */
	private int arcWidth = Codectx.DEFAULT_LOGO_ARCWIDTH;

	/**
	 * When the shape is LogoShape.RECTANGLE, the configuration is valid.
	 */
	private int arcHeight = Codectx.DEFAULT_LOGO_ARCWIDTH;

	private int borderSize = Codectx.DEFAULT_LOGO_BORDER_SIZE;

	private int padding = Codectx.DEFAULT_LOGO_PADDING;

	private String borderColor = Codectx.DEFAULT_LOGO_BORDER_COLOR;

	private String backgroundColor = Codectx.DEFAULT_LOGO_BACKGROUND_COLOR;

	private int margin = Codectx.DEFAULT_LOGO_MARGIN;

	/**
	 * Include margin and padding space.
	 */
	private String panelColor = Codectx.DEFAULT_LOGO_PANEL_COLOR;

	/**
	 * When the shape is LogoShape.RECTANGLE, the configuration is valid.
	 */
	private int panelArcWidth = Codectx.DEFAULT_LOGO_PANEL_RADIUS;

	/**
	 * When the shape is LogoShape.RECTANGLE, the configuration is valid.
	 */
	private int panelArcHeight = Codectx.DEFAULT_LOGO_PANEL_RADIUS;

	private LogoShape shape = LogoShape.RECTANGLE;

	public int getRatio() {
		return ratio;
	}

	public void setRatio(int ratio) {
		this.ratio = ratio;
	}

	public int getArcWidth() {
		return arcWidth;
	}

	/**
	 * This setting does work, if the shape is LogoShape.RECTANGLE.
	 *
	 * @param arcWidth draws logo arc width.
	 */
	public void setArcWidth(int arcWidth) {
		this.arcWidth = arcWidth;
	}

	public int getArcHeight() {
		return arcHeight;
	}

	/**
	 * This setting does work, if the shape is LogoShape.RECTANGLE.
	 *
	 * @param arcHeight draws logo arc heigth.
	 */
	public void setArcHeight(int arcHeight) {
		this.arcHeight = arcHeight;
	}

	public int getBorderSize() {
		return borderSize;
	}

	public void setBorderSize(int borderSize) {
		this.borderSize = borderSize;
	}

	public int getPadding() {
		return padding;
	}

	public void setPadding(int padding) {
		this.padding = padding;
	}

	public String getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(String borderColor) {
		this.borderColor = borderColor;
	}

	public String getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(String backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public int getMargin() {
		return margin;
	}

	public void setMargin(int margin) {
		this.margin = margin;
	}

	public String getPanelColor() {
		return panelColor;
	}

	public void setPanelColor(String panelColor) {
		this.panelColor = panelColor;
	}

	public int getPanelArcWidth() {
		return panelArcWidth;
	}

	/**
	 * This setting does work, if the shape is LogoShape.RECTANGLE.
	 *
	 * @param panelArcWidth container panel arc width.
	 */
	public void setPanelArcWidth(int panelArcWidth) {
		this.panelArcWidth = panelArcWidth;
	}

	public int getPanelArcHeight() {
		return panelArcHeight;
	}

	/**
	 * This setting does work, if the shape is LogoShape.RECTANGLE.
	 *
	 * @param panelArcHeight logo container panel arc height.
	 */
	public void setPanelArcHeight(int panelArcHeight) {
		this.panelArcHeight = panelArcHeight;
	}

	public LogoShape getShape() {
		return shape;
	}

	public void setShape(LogoShape shape) {
		this.shape = shape;
	}

	public final boolean isRectangle() {
		return getShape() == LogoShape.RECTANGLE;
	}

	public final boolean isCircle() {
		return getShape() == LogoShape.CIRCLE;
	}

	@Override
	public String toString() {
		return "LogoConfig [ratio=" + ratio + ", arcWidth=" + arcWidth + ", arcHeight=" + arcHeight + ", borderSize="
				+ borderSize + ", padding=" + padding + ", borderColor=" + borderColor + ", backgroundColor="
				+ backgroundColor + ", margin=" + margin + ", panelColor=" + panelColor + ", panelArcWidth="
				+ panelArcWidth + ", panelArcHeight=" + panelArcHeight + ", shape=" + shape + "]";
	}

}
