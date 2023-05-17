package com.chua.common.support.lang.bar;

import com.chua.common.support.spi.ServiceProvider;

/**
 * 条形码/二维码
 *
 * @author CH
 * @version 1.0.0
 */
public class BarCodeBuilder {

    public String level = "M";
    public int version = 1;
    public int width = 300;
    public int height = 300;
    public String masterColor = "#000000";
    public String slaveColor = "#FFFFFF";
    public String logoBorderColor;
    public String codeEyesBorderColor = "#000000";
    public String codeEyesPointColor = "#000000";
    public QrCodeEyesFormat qrCodeEyesFormat = QrCodeEyesFormat.R_BORDER;
    public String bgImage;
    public float alpha;
    public String logoPath;
    public String generate;
    public int borderRadius = 0;
    public int borderSize = 0;
    public String borderColor = "#808080";
    public BarCodeBorderStyle borderStyle = BarCodeBorderStyle.SOLID;
    public int borderDashGranularity = 5;
    public int margin = 2;
    public int ratio = 5;
    public int padding = 0;
    public BarCodeLogoShape logoShape = BarCodeLogoShape.RECTANGLE;
    public float panelArcWidth = 15f;
    public float panelArcHeight = 15f;
    public String panelColor = "#FFFFFF";
    public float arcWidth = 10f;
    public float arcHeight = 10f;
    public String backgroundColor = "#FFFFFF";
    public String outType = "png";

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
         * Rectangle random
         */
        R_RANDOM,
        /**
         * Rectangle Border with Rectangle Point.
         */
        R_BORDER,
        /**
         * Gradient
         */
        GRADIENT,
        /**
         * Rectangle Border with Rectangle Point.
         */
        R_BORDER_GRADIENT_POINT,
//        /**
//         * Rectangle Border with Rectangle Point.
//         */
//        R_BORDER_R_POINT,
//
//        /**
//         * Rectangle Border with Circle Point.
//         */
//        R_BORDER_C_POINT,
//
//        /**
//         * Circle Border with Rectangle Point.
//         */
//        C_BORDER_R_POINT,
//
//        /**
//         * Circle Border with Circle Point.
//         */
//        C_BORDER_C_POINT,
//
//        /**
//         * RoundRectangle Border with Rectangle Point.
//         */
//        R2_BORDER_R_POINT,
//
//        /**
//         * RoundRectangle Border with Circle Point.
//         */
//        R2_BORDER_C_POINT,
//
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
