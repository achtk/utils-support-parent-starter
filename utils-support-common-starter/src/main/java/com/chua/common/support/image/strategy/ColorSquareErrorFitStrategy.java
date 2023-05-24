package com.chua.common.support.image.strategy;

import com.chua.common.support.image.matrix.GrayscaleMatrix;

/**
 * 颜色平方误差拟合策略
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/6/11
 */
public class ColorSquareErrorFitStrategy implements BestCharacterFitStrategy {

    @Override
    public float calculateError(GrayscaleMatrix character, GrayscaleMatrix tile) {
        float error = 0;
        for (int i = 0; i < character.getData().length; i++) {
            error += (character.getData()[i] - tile.getData()[i])
                    * (character.getData()[i] - tile.getData()[i]);
        }
        return error / character.getData().length;

    }
}
