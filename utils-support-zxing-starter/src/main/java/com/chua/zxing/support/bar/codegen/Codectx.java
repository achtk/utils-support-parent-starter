package com.chua.zxing.support.bar.codegen;

public final class Codectx {

    /******************************************************************/
    // About Qrcode.
    /******************************************************************/

    public static final int DEFAULT_CODE_WIDTH = 250;

    public static final int DEFAULT_CODE_HEIGHT = 250;

    public static final int DEFAULT_CODE_MARGIN = 10;

    public static final int DEFAULT_CODE_PADDING = 0;

    public static final int DEFAULT_CODE_BORDER_SIZE = 0;

    public static final int DEFAULT_CODE_BORDER_RADIUS = 0;

    public static final int DEFAULT_CODE_BORDER_DASH_GRANULARITY = 5;

    public static final String DEFAULT_CODE_BORDER_COLOR = "#808080";

    public static final String DEFAULT_CODE_MASTER_COLOR = "#000000";

    public static final String DEFAULT_CODE_SLAVE_COLOR = "#FFFFFF";

    /******************************************************************/
    // About Logo.
    /******************************************************************/

    public static final int DEFAULT_LOGO_RATIO = 5;

    public static final int DEFAULT_LOGO_BORDER_SIZE = 2;

    public static final int DEFAULT_LOGO_ARCWIDTH = 10;

    public static final int DEFAULT_LOGO_PADDING = 5;

    public static final int DEFAULT_LOGO_MARGIN = 5;

    public static final int DEFAULT_LOGO_PANEL_RADIUS = 15;

    public static final String DEFAULT_LOGO_BORDER_COLOR = "#808080";

    public static final String DEFAULT_LOGO_BACKGROUND_COLOR = "#FFFFFF";

    public static final String DEFAULT_LOGO_PANEL_COLOR = "#FFFFFF";

    /******************************************************************/
    // About Write.
    /******************************************************************/

    public static final String IMAGE_TYPE = "PNG";

    public static enum BorderStyle {

        SOLID, DASHED;
    }

    public static enum LogoShape {
        RECTANGLE, CIRCLE;
    }

}
