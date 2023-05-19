package com.chua.tts.support;

import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.tts.support.denoiser.DenoiserEncoder;
import com.chua.tts.support.denoiser.PytorchDenoiserEncoder;
import com.chua.tts.support.speaker.PytorchSpeakerEncoder;
import com.chua.tts.support.speaker.SpeakerEncoder;
import com.chua.tts.support.tacotron2.PytorchTacotron2Encoder;
import com.chua.tts.support.tacotron2.Tacotron2Encoder;
import com.chua.tts.support.transfer.TTSTransfer;
import com.chua.tts.support.waveglow.PytorchWaveGlowEncoder;
import com.chua.tts.support.waveglow.WaveGlowEncoder;

import java.io.FileOutputStream;

/**
 * @author CH
 */
public class TTSExample {

    public static void main(String[] args) throws Exception {
        SpeakerEncoder speakerEncoder = new PytorchSpeakerEncoder(DetectionConfiguration.DEFAULT);
        DenoiserEncoder denoiserEncoder = new PytorchDenoiserEncoder(DetectionConfiguration.DEFAULT);
        Tacotron2Encoder tacotron2Encoder = new PytorchTacotron2Encoder(DetectionConfiguration.DEFAULT);
        WaveGlowEncoder waveGlowEncoder = new PytorchWaveGlowEncoder(DetectionConfiguration.DEFAULT);
        TTSTransfer transfer = new TTSTransfer(speakerEncoder, denoiserEncoder, tacotron2Encoder, waveGlowEncoder, DetectionConfiguration.DEFAULT);

        transfer.transfer("啥也不会还菜，还没钱", new FileOutputStream("D:/tts.wav"));
    }
}
