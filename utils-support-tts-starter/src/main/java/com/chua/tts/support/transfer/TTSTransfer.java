package com.chua.tts.support.transfer;

import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDArrays;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.Shape;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.common.support.feature.transfer.AbstractTransfer;
import com.chua.common.support.resource.repository.Repository;
import com.chua.common.support.utils.IoUtils;
import com.chua.tts.support.denoiser.DenoiserEncoder;
import com.chua.tts.support.speaker.SpeakerEncoder;
import com.chua.tts.support.tacotron2.Tacotron2Encoder;
import com.chua.tts.support.utils.AudioUtils;
import com.chua.tts.support.utils.FfmpegUtils;
import com.chua.tts.support.utils.SequenceUtils;
import com.chua.tts.support.utils.SoundUtils;
import com.chua.tts.support.waveglow.WaveGlowEncoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * 文字转语音
 *
 * @author CH
 */
@Slf4j
public class TTSTransfer extends AbstractTransfer<String> {
    private final SpeakerEncoder speakerEncoder;
    private final DenoiserEncoder denoiserEncoder;
    private final Tacotron2Encoder tacotron2Encoder;
    private final WaveGlowEncoder waveGlowEncoder;
    /**
     * 目标音色保存路径
     */
    private final File audioFile;
    private int partialsNFrames = 160;

    public TTSTransfer(
            SpeakerEncoder speakerEncoder,
            DenoiserEncoder denoiserEncoder,
            Tacotron2Encoder tacotron2Encoder,
            WaveGlowEncoder waveGlowEncoder,
            File audioFile,
            DetectionConfiguration configuration) {
        super(configuration);
        this.speakerEncoder = speakerEncoder;
        this.denoiserEncoder = denoiserEncoder;
        this.tacotron2Encoder = tacotron2Encoder;
        this.waveGlowEncoder = waveGlowEncoder;
        this.audioFile = audioFile;
    }

    public TTSTransfer(
            SpeakerEncoder speakerEncoder,
            DenoiserEncoder denoiserEncoder,
            Tacotron2Encoder tacotron2Encoder,
            WaveGlowEncoder waveGlowEncoder,
            DetectionConfiguration configuration) throws Exception {
        this(speakerEncoder, denoiserEncoder, tacotron2Encoder, waveGlowEncoder, Repository.of("classpath:biaobei-009502.mp3").first("").toFile(), configuration);
    }


    @Override
    public void close() throws Exception {
        IoUtils.closeQuietly(speakerEncoder);
        IoUtils.closeQuietly(denoiserEncoder);
        IoUtils.closeQuietly(tacotron2Encoder);
        IoUtils.closeQuietly(waveGlowEncoder);
    }

    @Override
    public void transfer(String source, OutputStream stream) throws Exception {
        NDManager manager = NDManager.newBaseManager();

        // 文本转为ID列表
        List<Integer> textDataOrg = SequenceUtils.text2sequence(source);
        int[] textDataa = textDataOrg.stream().mapToInt(Integer::intValue).toArray();
        NDArray textData = manager.create(textDataa);
        textData.setName("text");

        // 目标音色作为Speaker Encoder的输入: 使用ffmpeg 将目标音色mp3文件转为wav格式
        NDArray audioArray = FfmpegUtils.loadWavToTorch(audioFile.toString(), 22050);
        // 提取这段语音的说话人特征（音色）作为Speaker Embedding
        Pair<LinkedList<LinkedList<Integer>>, LinkedList<LinkedList<Integer>>> slices =
                AudioUtils.compute_partial_slices(audioArray.size(), partialsNFrames, 0.75f, 0.5f);
        LinkedList<LinkedList<Integer>> waveSlices = slices.getLeft();
        LinkedList<LinkedList<Integer>> melSlices = slices.getRight();
        int maxWaveLength = waveSlices.getLast().getLast();
        if (maxWaveLength >= audioArray.size()) {
            audioArray = AudioUtils.pad(audioArray, (maxWaveLength - audioArray.size()), manager);
        }
        float[][] fframes = AudioUtils.wav_to_mel_spectrogram(audioArray);
        NDArray frames = manager.create(fframes).transpose();
        NDList frameslist = new NDList();
        for (LinkedList<Integer> s : melSlices) {
            NDArray temp = speakerEncoder.predict(frames.get(s.getFirst() + ":" + s.getLast()));
            frameslist.add(temp);
        }
        NDArray partialEmbeds = NDArrays.stack(frameslist);
        NDArray rawEmbed = partialEmbeds.mean(new int[]{0});

        // Speaker Embedding
        NDArray speakerData = rawEmbed.div(((rawEmbed.pow(2)).sum()).sqrt());

        Shape shape = speakerData.getShape();
        log.info("目标音色特征向量 Shape: {}", Arrays.toString(shape.getShape()));
        log.info("目标音色特征向量: {}", Arrays.toString(speakerData.toFloatArray()));

        // 模型数据
        NDList input = new NDList();
        input.add(textData);
        input.add(speakerData);

        // 生成mel频谱数据
        NDArray melsPostnet = tacotron2Encoder.predict(input);
        shape = melsPostnet.getShape();
        log.info("梅尔频谱数据 Shape: {}", Arrays.toString(shape.getShape()));
        log.info("梅尔频谱数据: {}", Arrays.toString(melsPostnet.toFloatArray()));

        // 生成wav数据
        NDArray wavWithNoise = waveGlowEncoder.predict(melsPostnet);
        NDArray wav = denoiserEncoder.predict(wavWithNoise);
        SoundUtils.saveWavFile(wav.get(0), 1.0f, stream);
        log.info("生成wav音频文件: {}", stream.toString());
    }
}
