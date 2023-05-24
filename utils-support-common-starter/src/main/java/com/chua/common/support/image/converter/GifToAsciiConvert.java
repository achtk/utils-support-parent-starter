package com.chua.common.support.image.converter;


import com.chua.common.support.image.AsciiImage;
import com.chua.common.support.image.strategy.BestCharacterFitStrategy;

/**
 * Ascii 图像转换器
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/6/11
 */
public class GifToAsciiConvert extends AsciiToImageConverter {

    public GifToAsciiConvert(AsciiImage characterCacher,
                             BestCharacterFitStrategy characterFitStrategy) {
        super(characterCacher, characterFitStrategy);
    }

    /**
     * @param srcFilePath 来源
     * @param disFilePath 目标
     * @param delay－－the  delay time(ms) between each frame
     * @param repeat－－he  number of times the set of GIF frames should be played.0 means play indefinitely.
     * @return int
     */
    public int convertGitToAscii(String srcFilePath, String disFilePath, int delay, int repeat) {
        com.chua.common.support.protocol.image.gif.GifDecoder decoder = new com.chua.common.support.protocol.image.gif.GifDecoder();
        int status = decoder.read(srcFilePath);
        if (status != 0) {
            return -1;
        }
        com.chua.common.support.protocol.image.gif.GifEncoder e = new com.chua.common.support.protocol.image.gif.GifEncoder();
        boolean openStatus = e.start(disFilePath);
        if (openStatus) {
            e.setDelay(delay);
            e.setRepeat(repeat);
            // initialize converters
            int frameCount = decoder.getFrameCount();
            for (int i = 0; i < frameCount; i++) {
                //convert per frame
                e.addFrame(this.convertImage(decoder.getFrame(i)));
            }
            e.finish();
            return 1;
        }
        return 0;
    }

}
