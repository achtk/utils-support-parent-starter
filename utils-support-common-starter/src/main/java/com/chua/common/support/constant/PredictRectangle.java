package com.chua.common.support.constant;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * 区域
 *
 * @author CH
 * @since 2022-04-11
 */
@SuppressWarnings("ALL")
@NoArgsConstructor
@Data
public class PredictRectangle {

    private double width;
    private double height;
    private double x;
    private double y;
    private List box;

    private int imageWidth;
    private int imageHeight;

    public PredictRectangle(List<?> box) {
        this.box = box;
        if (box.isEmpty()) {
            return;
        }
        int[] integers = (int[]) box.get(0);
        x = integers[0];
        y = integers[1];

        int[] integers1 = (int[]) box.get(1);
        width = integers1[0] - x;
        int[] integers2 = (int[]) box.get(2);
        height = integers2[1] - y;
    }

    public void setRect(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public List<?> getBox() {
        return box == null ? createBox() : box;
    }

    private List<?> createBox() {
        box = new LinkedList<>();
        box.add(Arrays.asList(x, y));
        box.add(Arrays.asList(x + width, y));
        box.add(Arrays.asList(x + width, y + height));
        box.add(Arrays.asList(x, y + height));
        return box;
    }
}
