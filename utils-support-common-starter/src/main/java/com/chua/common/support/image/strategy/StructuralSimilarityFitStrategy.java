package com.chua.common.support.image.strategy;

import com.chua.common.support.image.matrix.GrayscaleMatrix;

/**
 * 结构相似性拟合策略
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/6/11
 */
public class StructuralSimilarityFitStrategy implements BestCharacterFitStrategy {

    private final float K1 = 0.01f;
    private final float K2 = 0.03f;
    private final float aFloat = 255f;

    @Override
    public float calculateError(GrayscaleMatrix character, GrayscaleMatrix tile) {

        float c1 = K1 * aFloat;
        c1 *= c1;
        float c2 = K2 * aFloat;
        c2 *= c2;

        final int imgLength = character.getData().length;

        float score = 0f;
        for (int i = 0; i < imgLength; i++) {
            float pixelImg1 = character.getData()[i];
            float pixelImg2 = tile.getData()[i];

            score += (2 * pixelImg1 * pixelImg2 + c1) * (2 + c2)
                    / (pixelImg1 * pixelImg1 + pixelImg2 * pixelImg2 + c1) / c2;
        }

        // average and convert score to error
        return 1 - (score / imgLength);

    }
}
