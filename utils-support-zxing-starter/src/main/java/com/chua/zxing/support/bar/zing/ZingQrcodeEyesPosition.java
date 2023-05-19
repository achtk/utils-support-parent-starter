package com.chua.zxing.support.bar.zing;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

import static com.chua.zxing.support.bar.zing.ZingQrcodeEyesRenderStrategy.POINT;

/**
 * zing position
 *
 * @author CH
 * @since 2022-05-16
 */
@Data
public class ZingQrcodeEyesPosition {
    private final PointPosition POS = new PointPosition();

    private int leftStartX;

    private int leftEndX;

    private int topStartY;

    private int topEndY;

    private int rightStartX;

    private int rightEndX;

    private int bottomStartY;

    private int bottomEndY;

    /**
     * basic parameters.
     */
    private int modules;

    private int[] topLeftOnBit;

    /**
     *
     */
    public ZingQrcodeEyesPosition() {
        super();
    }

    /**
     * @param modules
     * @param topLeftOnBit
     */
    public ZingQrcodeEyesPosition(int modules, int[] topLeftOnBit) {
        super();
        this.modules = modules;
        this.topLeftOnBit = topLeftOnBit;
    }

    /**
     * @param leftStartX
     * @param leftEndX
     * @param topStartY
     * @param topEndY
     * @param rightStartX
     * @param rightEndX
     * @param bottomStartY
     * @param bottomEndY
     * @param modules
     * @param topLeftOnBit
     */
    public ZingQrcodeEyesPosition(int leftStartX, int leftEndX, int topStartY, int topEndY, int rightStartX, int rightEndX,
                                  int bottomStartY, int bottomEndY, int modules, int[] topLeftOnBit) {
        this(modules, topLeftOnBit);
        setPosition(leftStartX, leftEndX, topStartY, topEndY, rightStartX, rightEndX, bottomStartY, bottomEndY);
    }

    public ZingQrcodeEyesPosition setPosition(int leftStartX, int leftEndX, int topStartY, int topEndY, int rightStartX,
                                              int rightEndX, int bottomStartY, int bottomEndY) {
        this.leftStartX = leftStartX;
        this.leftEndX = leftEndX;
        this.topStartY = topStartY;
        this.topEndY = topEndY;
        this.rightStartX = rightStartX;
        this.rightEndX = rightEndX;
        this.bottomStartY = bottomStartY;
        this.bottomEndY = bottomEndY;
        return this;
    }


    public final int getModuleWidth(int imgWidth) {
        return (imgWidth - 2 * (topLeftOnBit[0])) / modules;
    }

    public final int getModuleHeight(int imgHeight) {
        return (imgHeight - 2 * (topLeftOnBit[1])) / modules;
    }

    public final int getBorderSize(int imgWidth) {
        return getModuleWidth(imgWidth);
    }

    public final int[] topLeftRect() {
        return new int[]{leftStartX, topStartY, leftEndX - leftStartX, topEndY - topStartY};
    }

    public final int[] topRightRect() {
        return new int[]{rightStartX, topStartY, rightEndX - rightStartX, topEndY - topStartY};
    }

    public final int[] bottomLeftRect() {
        return new int[]{leftStartX, bottomStartY, leftEndX - leftStartX, bottomEndY - bottomStartY};
    }

    private static final String CLEAR_COMMAND = "topLeftPoint" + "topRightPoint" + "bottomLeftPoint";

    public final int[] topLeftPoint() {
        Map<String, Object> m = POS.traceGet("topLeftPoint", CLEAR_COMMAND);
        return new int[]{(int) m.get("leftStartX"), (int) m.get("topStartY"),
                (int) m.get("leftEndX") - (int) m.get("leftStartX"),
                (int) m.get("topEndY") - (int) m.get("topStartY")};
    }

    public final int[] topRightPoint() {
        Map<String, Object> m = POS.traceGet("topRightPoint", CLEAR_COMMAND);
        return new int[]{(int) m.get("rightStartX"), (int) m.get("topStartY"),
                (int) m.get("rightEndX") - (int) m.get("rightStartX"),
                (int) m.get("topEndY") - (int) m.get("topStartY")};
    }

    public final int[] bottomLeftPoint() {
        Map<String, Object> m = POS.traceGet("bottomLeftPoint", CLEAR_COMMAND);
        return new int[]{(int) m.get("leftStartX"), (int) m.get("bottomStartY"),
                (int) m.get("leftEndX") - (int) m.get("leftStartX"),
                (int) m.get("bottomEndY") - (int) m.get("bottomStartY")};
    }

    public ZingQrcodeEyesPosition focusPoint(int imgWidth, int imgHeight) {

        if (POS.executed()) {
            return this;
        }

        int w = getModuleWidth(imgWidth), h = getModuleHeight(imgHeight);
        int leftStartX = topLeftOnBit[0] + w * POINT.getStart();
        int leftEndX = topLeftOnBit[0] + w * POINT.getEnd();
        int topStartY = topLeftOnBit[1] + h * POINT.getStart();
        int topEndY = topLeftOnBit[1] + h * POINT.getEnd();
        int rightStartX = topLeftOnBit[0] + w * (modules - POINT.getEnd());
        int rightEndX = imgWidth - topLeftOnBit[0] - w * POINT.getStart();
        int bottomStartY = imgHeight - topLeftOnBit[1] - h * POINT.getEnd();
        int bottomEndY = imgHeight - topLeftOnBit[1] - h * POINT.getStart();

        Map<String, Integer> map = new HashMap<>(8);
        map.put("leftStartX", leftStartX);
        map.put("leftEndX", leftEndX);
        map.put("topStartY", topStartY);
        map.put("topEndY", topEndY);
        map.put("rightStartX", rightStartX);
        map.put("rightEndX", rightEndX);
        map.put("bottomStartY", bottomStartY);
        map.put("bottomEndY", bottomEndY);

        POS.putAll(map);
        POS.count();
        return this;
    }

    private static class PointPosition extends ThreadLocal<Map<String, Object>> {

        private static final String RCK = PointPosition.class.getName() + "_record_check_methods.";

        private static final String COUNTER_KEY = PointPosition.class.getName() + "_method_execute_count.";

        @Override
        protected Map<String, Object> initialValue() {
            Map<String, Object> map = new HashMap<>(1);
            map.put(RCK, "");
            return map;
        }

        protected void putAll(Map<String, Integer> map) {
            get().putAll(map);
        }

        protected void count() {
            Object o = get().get(COUNTER_KEY);
            int c = o == null ? 0 : Integer.parseInt(o.toString());
            get().put(COUNTER_KEY, ++c);
        }

        protected boolean executed() {
            return null != get().get(COUNTER_KEY);
        }

        protected Map<String, Object> traceGet(String trace, String clearCommand) {
            get().put(RCK, get().get(RCK) + trace);
            Map<String, Object> result = get();
            if (result.get(RCK).toString().equals(clearCommand)) {
                remove();
            }
            return result;
        }

    }
}
