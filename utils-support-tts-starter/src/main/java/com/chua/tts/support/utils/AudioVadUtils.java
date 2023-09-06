package com.chua.tts.support.utils;

import com.chua.common.support.constant.Projects;
import com.orctom.vad4j.VAD;
import org.jitsi.webrtcvadwrapper.WebRTCVad;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static com.chua.tts.support.utils.SoundLongAsrUtils.WAV_PCM_SIGNED;

/**
 * 对音频预处理的工具: 静音切除，音频分段
 *
 * @author Calvin <179209347@qq.com>
 */
public class AudioVadUtils {
    /**
     * Filters out non-voiced audio frames.
     */
    public static Queue<byte[]> cropAudioVad(
            Path path, int padding_duration_ms, int frame_duration_ms) throws Exception {
        float sampleRate = SoundLongAsrUtils.getSampleRate(path.toFile());
        byte[] bytes = SoundLongAsrUtils.convertAsByteArray(path.toFile(), WAV_PCM_SIGNED);
        List<byte[]> frames = SoundLongAsrUtils.frameGenerator(bytes, frame_duration_ms, sampleRate);
        Queue<byte[]> segments = vadCollector(sampleRate, frames, padding_duration_ms, frame_duration_ms);
        return segments;
    }

    /**
     * Filters out non-voiced audio frames.
     */
    public static List<byte[]> vadCollector(List<byte[]> frames) {
        List<byte[]> voicedFrames = new ArrayList<>();
        try (VAD vad = new VAD()) {
            for (byte[] frame : frames) {
                boolean isSpeech = vad.isSpeech(frame);
                if (isSpeech) {
                    voicedFrames.add(frame);
                }
            }
        }
        return voicedFrames;
    }

    /**
     * Filters out non-voiced audio frames.
     */
    public static Queue<byte[]> vadCollector(
            float sampleRate, List<byte[]> frames, int padding_duration_ms, int frame_duration_ms) {
        Queue<byte[]> segments = new LinkedList<>();
        Queue<byte[]> voicedFrames = new LinkedList<>();

        int num_padding_frames = (int) (padding_duration_ms / frame_duration_ms);
        // We use a fixed queue for our sliding window/ring buffer.
        FixedQueue<byte[]> fixedQueue = new FixedQueue<byte[]>(num_padding_frames);

        // We have two states: TRIGGERED and NOTTRIGGERED. We start in the NOTTRIGGERED state.
        boolean triggered = false;

        if (!Projects.isWindows()) {
            try (VAD vad = new VAD()) {
                int num_voiced = 0;
                int num_unvoiced = 0;
                for (byte[] frame : frames) {
                    boolean isSpeech = vad.isSpeech(frame);
                    if (!triggered) {
                        fixedQueue.offer(frame);
                        if (isSpeech) {
                            num_voiced = num_voiced + 1;
                        }
                        // If we're NOTTRIGGERED and more than 90% of the frames in
                        // the ring buffer are voiced frames, then enter the
                        // TRIGGERED state.
                        if (num_voiced > 0.9 * fixedQueue.getSize()) {
                            triggered = true;
                            for (byte[] bytes : fixedQueue.getQueue()) {
                                voicedFrames.add(bytes);
                            }
                            fixedQueue.clear();
                            num_voiced = 0;
                        }
                    } else {
                        // We're in the TRIGGERED state, so collect the audio data
                        // and add it to the ring buffer.
                        voicedFrames.add(frame);
                        fixedQueue.offer(frame);
                        if (!isSpeech) {
                            num_unvoiced = num_unvoiced + 1;
                        }
                        // If more than 90% of the frames in the ring buffer are
                        // unvoiced, then enter NOTTRIGGERED and yield whatever
                        // audio we've collected.
                        if (num_unvoiced > 0.9 * fixedQueue.getSize()) {
                            triggered = false;
                            int len = 0;
                            for (byte[] item : voicedFrames) {
                                len = len + item.length;
                            }
                            byte[] voicedFramesBytes = new byte[len];
                            int index = 0;
                            for (byte[] item : voicedFrames) {
                                for (byte value : item) {
                                    voicedFramesBytes[index++] = value;
                                }
                            }

                            segments.add(voicedFramesBytes);
                            fixedQueue.clear();
                            voicedFrames.clear();
                            num_unvoiced = 0;
                        }
                    }
                }
            }
        } else {
            WebRTCVad vad = new WebRTCVad(32000, 3);
            int num_voiced = 0;
            int num_unvoiced = 0;
            for (byte[] frame : frames) {
                int size = frame.length / 2;
                int[] pcm_wave = new int[size];
                for (int i = 0; i < size; i++) {
                    pcm_wave[i] = IntegerConversion.convertTwoBytesToInt1(frame[2 * i], frame[2 * i + 1]);
                }

                boolean isSpeech = vad.isSpeech(pcm_wave);
                if (!triggered) {
                    fixedQueue.offer(frame);
                    if (isSpeech) {
                        num_voiced = num_voiced + 1;
                    }
                    // If we're NOTTRIGGERED and more than 90% of the frames in
                    // the ring buffer are voiced frames, then enter the
                    // TRIGGERED state.
                    if (num_voiced > 0.9 * fixedQueue.getSize()) {
                        triggered = true;
                        for (byte[] bytes : fixedQueue.getQueue()) {
                            voicedFrames.add(bytes);
                        }
                        fixedQueue.clear();
                        num_voiced = 0;
                    }
                } else {
                    // We're in the TRIGGERED state, so collect the audio data
                    // and add it to the ring buffer.
                    voicedFrames.add(frame);
                    fixedQueue.offer(frame);
                    if (!isSpeech) {
                        num_unvoiced = num_unvoiced + 1;
                    }
                    // If more than 90% of the frames in the ring buffer are
                    // unvoiced, then enter NOTTRIGGERED and yield whatever
                    // audio we've collected.
                    if (num_unvoiced > 0.9 * fixedQueue.getSize()) {
                        triggered = false;
                        int len = 0;
                        for (byte[] item : voicedFrames) {
                            len = len + item.length;
                        }
                        byte[] voicedFramesBytes = new byte[len];
                        int index = 0;
                        for (byte[] item : voicedFrames) {
                            for (byte value : item) {
                                voicedFramesBytes[index++] = value;
                            }
                        }

                        segments.add(voicedFramesBytes);
                        fixedQueue.clear();
                        voicedFrames.clear();
                        num_unvoiced = 0;
                    }
                }
            }
        }
        // If we have any leftover voiced audio when we run out of input, yield it.
        if (voicedFrames.size() > 0) {
            int len = 0;
            for (byte[] item : voicedFrames) {
                len = len + item.length;
            }
            byte[] voicedFramesBytes = new byte[len];
            int index = 0;
            for (byte[] item : voicedFrames) {
                for (byte value : item) {
                    voicedFramesBytes[index++] = value;
                }
            }
            segments.add(voicedFramesBytes);
        }

        return segments;
    }
}
