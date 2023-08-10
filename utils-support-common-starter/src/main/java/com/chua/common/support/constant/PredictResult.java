package com.chua.common.support.constant;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * result
 *
 * @author CH
 */
@NoArgsConstructor
@Data
@Accessors(chain = true)
public class PredictResult {

    private static final PredictResult EMPTY = new PredictResult();
    static {
        EMPTY.setScore(-1);
    }

    private transient Object boundingBox;

    private int index = 0;
    private String text;
    private Object sign1;
    private Object sign2;
    private float score = -1.0f;
    private float clsScore;
    private String clsLabel;

    private Object ndArray;

    public PredictResult(String text) {
        this.text = text;
        this.clsLabel = text;
    }

    public PredictResult(float clsScore) {
        this.clsScore = clsScore;
        this.score = clsScore;
    }

    public PredictResult(String text, float clsScore) {
        this.text = text;
        this.clsLabel = text;
        this.clsScore = clsScore;
        this.score = clsScore;
    }

    public static PredictResult empty() {
        return EMPTY;
    }

    public <T>T getValue(Class<T> type) {
        if(null != ndArray && type.isAssignableFrom(ndArray.getClass())) {
            return (T) ndArray;
        }

        if(null != boundingBox && type.isAssignableFrom(boundingBox.getClass())) {
            return (T) boundingBox;
        }

        return null;
    }
}
