package com.chua.common.support.tree.support;

import com.chua.common.support.tree.TextSprite;

/**
 * 工具
 *
 * @author Rodion "rodde" Efremov
 */
public final class Utils {
    /**
     * 设置文本
     *
     * @param textSprite 文本
     */
    public static void setEmptyTextSpriteCellsToSpace(TextSprite textSprite) {
        for (int y = 0; y < textSprite.getHeight(); ++y) {
            for (int x = 0; x < textSprite.getWidth(); ++x) {
                char c = textSprite.getChar(x, y);

                if (c == '\u0000') {
                    textSprite.setChar(x, y, ' ');
                }
            }
        }
    }
}
