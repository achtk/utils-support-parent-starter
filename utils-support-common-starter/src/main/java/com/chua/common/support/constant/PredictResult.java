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

    private Object boundingBox;

    private String text;
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
