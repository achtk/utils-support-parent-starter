package com.chua.common.support.lang.qr;

import lombok.Builder;
import lombok.Data;

/**
 * qr码配置
 *
 * @author CH
 * @since 2023/09/05
 */
@Builder
@Data
public class QrCodeConfigure {

    /**
     * 内容
     */
    private String content;

    /**
     * logo
     */
    private String logo;
    /**
     * 设置二维码的纠错级别
     * L(7%) M(15%) Q(25%) H(30%)
     */
    private String qrcodeErrorCorrect;

    /**
     * 宽度
     */
    @Builder.Default
    private int width = 300;

    /**
     * 高度
     */
    @Builder.Default
    private int height = 300;
    /**
     * 码眼央视
     */
    private EysStyle eyes;


    /**
     * eys样式
     *
     * @author CH
     * @since 2023/09/05
     */
    public enum EysStyle {

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
