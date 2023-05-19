package com.chua.pytorch.support.pytorch;

import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.output.DetectedObjects;
import com.alibaba.fastjson2.JSONObject;
import com.chua.common.support.constant.PredictResult;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.common.support.file.yaml.YamlReader;
import com.chua.common.support.resource.repository.Metadata;
import com.chua.common.support.resource.repository.Repository;
import com.chua.common.support.utils.IoUtils;
import com.chua.common.support.utils.MapUtils;
import com.chua.common.support.utils.StringUtils;
import com.chua.pytorch.support.AbstractPytorchDetector;
import com.chua.pytorch.support.utils.LocationUtils;
import com.chua.pytorch.support.yolo.YoloV5Translator;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * FireSmoke
 *
 * @author CH
 */
public class YoloDetector extends AbstractPytorchDetector<DetectedObjects> {

    static final Map<String, Object> DEFAULT_ARGUMENTS = new ConcurrentHashMap<>();

    static {
        DEFAULT_ARGUMENTS.put("width", 640);
        DEFAULT_ARGUMENTS.put("height", 640);
        DEFAULT_ARGUMENTS.put("resize", true);
        DEFAULT_ARGUMENTS.put("rescale", true);
        DEFAULT_ARGUMENTS.put("threshold", 0.2);
        DEFAULT_ARGUMENTS.put("nmsThreshold", 0.5);
        DEFAULT_ARGUMENTS.put("synset", "fire,smoke");
    }


    public YoloDetector(DetectionConfiguration configuration) {
        super(configuration,
                YoloV5Translator.builder(megeSynset(configuration, MapUtils.compute(configuration.ext(), DEFAULT_ARGUMENTS)))
                        .build(),
                "PyTorch",
                null,
                StringUtils.defaultString(configuration.modelPath(), "fire_smoke"),
                "https://aias-home.oss-cn-beijing.aliyuncs.com/models/fire_smoke.zip",
                true);
    }

    private static Map<String, Object> megeSynset(DetectionConfiguration configuration, Map<String, Object> compute) {
        Map<String, Object> rs = new ConcurrentHashMap<>(compute);
        String artifactName = configuration.synsetArtifactName();
        if (!Strings.isNullOrEmpty(artifactName)) {
            rs.put("synset", artifactName);
        }

        String synset = configuration.synset();
        if (!Strings.isNullOrEmpty(synset)) {
            Repository repository = Repository.of("classpath:,.");
            Metadata metadata = repository.first(synset);
            if (synset.endsWith(".txt")) {
                try {
                    rs.put("synset", Joiner.on(',').join(Splitter.on("\r\n").omitEmptyStrings().trimResults().splitToList(IoUtils.toString(metadata.openInputStream(), UTF_8))));
                } catch (IOException ignored) {
                }
                return rs;
            }

            if (synset.endsWith(".yml") || synset.endsWith(".yaml")) {
                JSONObject jsonObject;
                try (YamlReader yaml = new YamlReader(new InputStreamReader(metadata.openInputStream(), UTF_8))) {
                    jsonObject = yaml.read(JSONObject.class);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                JSONObject jsonObject1 = jsonObject.getJSONObject("names");
                rs.put("synset", Joiner.on(',').join(jsonObject1.values()));
                return rs;
            }
        }

        return rs;
    }

    @Override
    protected Class<DetectedObjects> type() {
        return DetectedObjects.class;
    }

    @Override
    protected List<PredictResult> toDetect(DetectedObjects detections, Image img) {
        List<PredictResult> results = new LinkedList<>();

        List<DetectedObjects.DetectedObject> items = detections.topK(100);
        for (DetectedObjects.DetectedObject item : items) {
            if (item.getProbability() < 0) {
                continue;
            }

            PredictResult predictResult = LocationUtils.convertPredictResult(item, img);
            results.add(predictResult);
        }
        return results;
    }
}
