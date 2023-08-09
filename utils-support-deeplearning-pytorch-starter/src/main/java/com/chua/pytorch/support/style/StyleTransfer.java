package com.chua.pytorch.support.style;

import ai.djl.modality.cv.Image;
import ai.djl.repository.zoo.Criteria;
import ai.djl.training.util.ProgressBar;
import com.chua.common.support.function.InitializingAware;
import com.chua.pytorch.support.common.LearningConfig;
import com.chua.pytorch.support.utils.LocationUtils;
import com.google.common.base.Joiner;
import lombok.Setter;

import java.util.LinkedList;
import java.util.List;

/**
 * 風格
 *
 * @author CH
 */
public class StyleTransfer implements AutoCloseable, InitializingAware {

    private Criteria<Image, Image> criteria;

    @Setter
    private LearningConfig learningConfiguration;
    private Artist artist;

    public StyleTransfer(Artist artist) {
        this.artist = artist;
    }

    @Override
    public void close() throws Exception {
    }

    public Criteria<Image, Image> getCriteria() {
        if (null == criteria) {
            synchronized (this) {
                if (null == criteria) {
                    afterPropertiesSet();
                }
            }
        }
        return criteria;
    }

    @Override
    public void afterPropertiesSet() {
        String modelName = "style_" + artist.toString().toLowerCase() + ".zip";
        List<String> url = new LinkedList<>();
        try {
            List<String> url1 = LocationUtils.getUrl(modelName, "https://aias-home.oss-cn-beijing.aliyuncs.com/models/gan_models/" + modelName);
            url.addAll(url1);
        } catch (Exception ignored) {
        }
        if (null != learningConfiguration) {
            url.addAll(LocationUtils.doAnalysis(learningConfiguration.getModulePath(), new String[]{modelName, "style_" + artist.toString().toLowerCase() + ".pt"}, false));
        }

        this.criteria =
                Criteria.builder()
                        .setTypes(Image.class, Image.class)
                        .optEngine("PyTorch") // Use PyTorch engine
                        .optModelUrls(Joiner.on(',').join(url))
                        .optProgress(new ProgressBar())
                        .optTranslatorFactory(new StyleTransferTranslatorFactory())
                        .build();

    }

    public enum Artist {
        /**
         * 塞尚(Paul Cezanne, 1838～1906)
         */
        CEZANNE,
        /**
         * 莫奈 (Claude monet, 1840～1926)
         */
        MONET,
        /**
         * 日本浮世绘
         */
        UKIYOE,
        /**
         * 梵高 (Vincent Willem van Gogh, 1853~1890)
         */
        VANGOGH
    }
}
