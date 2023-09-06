package com.chua.tts.support.longasr;

import ai.djl.Device;
import ai.djl.inference.Predictor;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDManager;
import com.chua.common.support.constant.PredictResult;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.common.support.feature.recognizer.AbstractRecognizer;
import com.chua.tts.support.asr.SpeedRecognizer;
import com.chua.tts.support.utils.AudioLongAsrProcess;
import com.chua.tts.support.utils.AudioLongAsrUtils;
import com.chua.tts.support.utils.AudioVadUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * 短语音识别
 *
 * @author CH
 */
@Slf4j
public class LongSpeedRecognizer extends AbstractRecognizer {

    final SpeedRecognizer speedRecognizer;

    public LongSpeedRecognizer(DetectionConfiguration configuration) throws Exception {
        super(configuration);
        speedRecognizer = new SpeedRecognizer(configuration);
    }

    @Override
    public float[] predict(Object img) {
        return new float[0];
    }

    @SneakyThrows
    @Override
    public List<PredictResult> recognize(Object image) {
        NDManager manager = NDManager.newBaseManager(Device.cpu());
        Queue<byte[]> segments = AudioVadUtils.cropAudioVad(Paths.get(image.toString()), 300, 30);

        List<PredictResult> results = new LinkedList<>();
        try (Predictor<NDArray, Pair> predictor = speedRecognizer.getModel().newPredictor()) {
            for (byte[] que : segments) {
                NDArray array = AudioLongAsrUtils.bytesToFloatArray(manager, que);
                NDArray audioFeature = AudioLongAsrProcess.processUtterance(manager, array);
                Pair result = predictor.predict(audioFeature);
                results.add(new PredictResult((String) result.getRight(), (Float) result.getLeft()));
            }
        }

        return results;
    }

    @Override
    public void close() throws Exception {
        speedRecognizer.close();
    }
}
