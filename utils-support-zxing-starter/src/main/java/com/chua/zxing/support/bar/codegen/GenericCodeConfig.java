package com.chua.zxing.support.bar.codegen;

import java.io.Serializable;

public abstract class GenericCodeConfig implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 95683283981376217L;

	private int width;

	private int height;

	private String masterColor = Codectx.DEFAULT_CODE_MASTER_COLOR;

	private String slaveColor = Codectx.DEFAULT_CODE_SLAVE_COLOR;

	public GenericCodeConfig(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public GenericCodeConfig(int width, int height, String masterColor, String slaveColor) {
		this(width, height);
		this.masterColor = masterColor;
		this.slaveColor = slaveColor;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public String getMasterColor() {
		return masterColor;
	}

	public String getSlaveColor() {
		return slaveColor;
	}

	public GenericCodeConfig setWidth(int width) {
		this.width = width;
		return this;
	}

	public GenericCodeConfig setHeight(int height) {
		this.height = height;
		return this;
	}

	public GenericCodeConfig setSlaveColor(String slaveColor) {
		this.slaveColor = slaveColor;
		return this;
	}

	public GenericCodeConfig setMasterColor(String masterColor) {
		this.masterColor = masterColor;
		return this;
	}

}
