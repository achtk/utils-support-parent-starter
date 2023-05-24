package com.chua.common.support.image.strategy;

import com.chua.common.support.image.matrix.GrayscaleMatrix;

/**
 * 角色策略
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/6/11
 */
public interface BestCharacterFitStrategy {

    /**
     * Returns the error between the character and tile matrices. The character
     * with minimun error wins.
     *
     * @param character the character
     * @param tile      the tile
     * @return error. Less values mean better fit. Least value character will be
     * chosen as best fit.
     */
    float calculateError(final GrayscaleMatrix character, final GrayscaleMatrix tile);
}
