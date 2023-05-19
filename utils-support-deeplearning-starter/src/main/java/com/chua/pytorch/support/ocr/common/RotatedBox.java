package com.chua.pytorch.support.ocr.common;

import ai.djl.ndarray.NDArray;
import lombok.Data;

/**
 * 結果
 *
 * @author CH
 */
@Data
public class RotatedBox {

    private NDArray box;
    private String text;
}
