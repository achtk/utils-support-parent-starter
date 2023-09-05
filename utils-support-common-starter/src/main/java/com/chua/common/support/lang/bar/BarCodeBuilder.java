package com.chua.common.support.lang.bar;

import com.chua.common.support.spi.ServiceProvider;

/**
 * 条形码/二维码
 *
 * @author CH
 * @version 1.0.0
 */
public class BarCodeBuilder {

    private String level = "M";
    private int version = 1;
    private int width = 300;
    private int height = 300;
    private String masterColor = "#000000";
    private String slaveColor = "#FFFFFF";
    private String logoBorderColor;
    private String codeEyesBorderColor = "#000000";
    private String codeEyesPointColor = "#000000";
    private QrCodeEyesFormat qrCodeEyesFormat = QrCodeEyesFormat.R_BORDER_C_POINT;
    private String bgImage;
    private float alpha;
    private String logoPath;
    private String generate;
    private int borderRadius = 0;
    private int borderSize = 0;
    private String borderColor = "#808080";
    private BarCodeBorderStyle borderStyle = BarCodeBorderStyle.SOLID;
    private int borderDashGranularity = 5;
    private int margin = 2;
    private int ratio = 5;
    private int padding = 0;
    private BarCodeLogoShape logoShape = BarCodeLogoShape.RECTANGLE;
    private float panelArcWidth = 15f;
    private float panelArcHeight = 15f;
    private String panelColor = "#FFFFFF";
    private float arcWidth = 10f;
    private float arcHeight = 10f;
    private String backgroundColor = "#FFFFFF";
    private String outType = "png";

    /**
     * 初始化
     *
     * @return BarCodeBuilder
     */
    public static BarCodeBuilder newBuilder() {
        return new BarCodeBuilder();
    }

    /**
     * logo类型
     *
     * @param logoShape logo类型
     * @return this
     */
    public BarCodeBuilder logoShape(BarCodeLogoShape logoShape) {
        this.logoShape = logoShape;
        return this;
    }

    /**
     * 输出类型
     *
     * @param outType 输出类型
     * @return this
     */
    public BarCodeBuilder outType(String outType) {
        this.outType = outType;
        return this;
    }

    /**
     * 内容
     *
     * @param generate 内容
     * @return this
     */
    public BarCodeBuilder generate(String generate) {
        this.generate = generate;
        return this;
    }

    /**
     * level
     *
     * @param level level
     * @return this
     */
    public BarCodeBuilder level(String level) {
        this.level = level;
        return this;
    }

    /**
     * 宽度
     *
     * @param width 宽度
     * @return this
     */
    public BarCodeBuilder width(int width) {
        this.width = width;
        return this;
    }

    /**
     * 高度
     *
     * @param height 高度
     * @return this
     */
    public BarCodeBuilder height(int height) {
        this.height = height;
        return this;
    }

    /**
     * 主体颜色
     *
     * @param masterColor 主体颜色
     * @return this
     */
    public BarCodeBuilder masterColor(String masterColor) {
        this.masterColor = masterColor;
        return this;
    }

    /**
     * logo主体颜色
     *
     * @param logoBorderColor logo主体颜色
     * @return this
     */
    public BarCodeBuilder logoBorderColor(String logoBorderColor) {
        this.logoBorderColor = logoBorderColor;
        return this;
    }

    /**
     * 码眼
     *
     * @param codeEyesPointColor 码眼
     * @return this
     */
    public BarCodeBuilder codeEyesPointColor(String codeEyesPointColor) {
        this.codeEyesPointColor = codeEyesPointColor;
        return this;
    }

    /**
     * 码眼
     *
     * @param qrCodeEyesFormat 码眼
     * @return this
     */
    public BarCodeBuilder codeEyesFormat(QrCodeEyesFormat qrCodeEyesFormat) {
        this.qrCodeEyesFormat = qrCodeEyesFormat;
        return this;
    }

    /**
     * logo
     *
     * @param logoPath logo
     * @return this
     */
    public BarCodeBuilder logo(String logoPath) {
        this.logoPath = logoPath;
        return this;
    }

    /**
     * bgImage
     *
     * @param bgImage 背景图片
     * @param alpha   透明度
     * @return this
     */
    public BarCodeBuilder bgImage(String bgImage, float alpha) {
        this.bgImage = bgImage;
        this.alpha = alpha;
        return this;
    }

    /**
     * 类型
     *
     * @param type 类型
     * @return BarCodeWriter
     */
    public BarCodeWriter transfer(String type) {
        ServiceProvider<BarCodeWriter> provider = ServiceProvider.of(BarCodeWriter.class);
        BarCodeWriter writer = provider.getNewExtension(type);
        return writer.config(this);
    }

    /**
     * 码眼
     */
    public enum QrCodeEyesFormat {

        /**
         * Rectangle Border with Rectangle Point.
         */
        R_BORDER_R_POINT,

        /**
         * Rectangle Border with Circle Point.
         */
        R_BORDER_C_POINT,

        /**
         * Circle Border with Rectangle Point.
         */
        C_BORDER_R_POINT,

        /**
         * Circle Border with Circle Point.
         */
        C_BORDER_C_POINT,

        /**
         * RoundRectangle Border with Rectangle Point.
         */
        R2_BORDER_R_POINT,

        /**
         * RoundRectangle Border with Circle Point.
         */
        R2_BORDER_C_POINT,

        /**
         * Diagonal RoundRectangle Border with Rectangle Point.
         */
        DR2_BORDER_R_POINT,

        /**
         * Diagonal RoundRectangle Border with Circle Point.
         */
        DR2_BORDER_C_POINT;

    }

}
