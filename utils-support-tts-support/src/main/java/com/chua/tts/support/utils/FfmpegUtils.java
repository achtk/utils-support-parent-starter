package com.chua.tts.support.utils;

import ai.djl.Device;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDArrays;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.DataType;
import com.chua.common.support.collection.ImmutableCollection;
import com.chua.common.support.function.Joiner;
import org.aspectj.util.FileUtil;
import org.bytedeco.javacpp.Loader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * 使用ffmpeg 将文件mp3转为wav文件
 * <p>
 * 仿python的audioread
 */
public class FfmpegUtils {

    public static int samplerate = 0;
    public static int channels = 0;
    public static float duration = 0.0f;
    static String likeStr = "subtitle";
    static float scale = (float) 1.0 / Float.valueOf(1 << ((8 * 2) - 1));
    static int n_bytes = 2;
    static Pattern re = Pattern.compile("(\\d+) hz");
    static Pattern rec = Pattern.compile("hz, ([^,]+),");
    static Pattern rec1 = Pattern.compile("(\\d+)\\.?(\\d)?");
    static Pattern rec2 = Pattern.compile("duration: (\\d+):(\\d+):(\\d+)\\.(\\d)");

    public static void main(String[] args) throws Exception {
        NDArray and = audioReadLoad("src/test/resources/voice/biaobei-009502.mp3",
                0, DataType.FLOAT32);
//		System.out.println(and.toDebugString(1000000000, 1000, 1000, 1000));
    }

    public static NDArray loadWavToTorch(String path, int sr_force) throws Exception {
        return read(path, sr_force);
    }

    public static NDArray read(String path, int srForce) throws Exception {
        NDArray wav = audioReadLoad(path,
                0, DataType.FLOAT32);
        wav = wav.mul(0.9).div(NDArrays.maximum(wav.abs().max(), 0.01));
        return wav;
    }

    public static Queue<byte[]> audio_open(String path, int offset, DataType dtype) throws InterruptedException, IOException {

        String ffmpeg = Loader.load(org.bytedeco.ffmpeg.ffmpeg.class);
        ProcessBuilder pb1 = new ProcessBuilder(ffmpeg, "-i",
                path);

        BufferedReader br = new BufferedReader(new InputStreamReader(pb1.start().getErrorStream(), StandardCharsets.UTF_8));
        List<String> out_parts = ImmutableCollection.<String>builder().newArrayList();
        String ch = "";
        while ((ch = br.readLine()) != null) {
            out_parts.add(ch.toLowerCase());
            parseInfo(Joiner.on("").join(out_parts));
        }

        ProcessBuilder pb = new ProcessBuilder(ffmpeg, "-i", path, "-f", "s16le", "-");
        InputStream input = pb.start().getInputStream();

        byte[] all = FileUtil.readAsByteArray(input);
        int len = all.length;
        int base = 4096;
        int loop = len / base;
        int copylen = base;
        Queue<byte[]> queue = new LinkedList<byte[]>();
        int yu = len % base;
        if (yu > 0) {
            for (int i = 0; i < loop; i++) {
                if (copylen < len) {
                    queue.add(Arrays.copyOfRange(all, i * base, copylen));
                }
                copylen += base;
            }
        }
        queue.add(Arrays.copyOfRange(all, len - yu, len));
        input.close();
        return queue;
    }

    public static NDArray audioReadLoad(String path, int offset, DataType dtype) throws Exception {
        NDList y = new NDList();
        Queue<byte[]> queue = audio_open(path, offset, dtype);

        int srNative = samplerate;
        int nChannels = channels;
        int sStart = Math.round(srNative * offset) * nChannels;
        int sEnd = 0;
        if (duration == 0.0) {
            sEnd = Integer.MAX_VALUE;
        } else {
            sEnd = sStart + (Math.round(srNative * duration) * nChannels);
        }
        int n = 0;

        NDManager manager = NDManager.newBaseManager(Device.cpu());
        for (byte[] que : queue) {
            NDArray frame = bufToFloat(que, manager);
            int nPrev = n;
            n = n + (int) frame.size();
            if (n < sStart) {
                //# offset is after the current frame
                // # keep reading
                continue;
            }
            if (sEnd < nPrev) {
                // we're off the end.  stop reading
                break;
            }
            if (sEnd < n) {
                // the end is in this frame.  crop.
                frame = frame.get(":" + (sEnd - nPrev));
            }
            if (nPrev <= sStart && sStart <= n) {
                // beginning is in this frame
                frame = frame.get((sStart - nPrev) + ":");
            }
            //System.out.println(frame.toDebugString(1000000000, 1000, 1000, 1000));
            y.add(frame);
        }
        NDArray yy = null;
        if (y.size() > 0) {
            yy = NDArrays.concat(y);
        }

        yy.setName(srNative + "");
        return yy;
    }

    private static NDArray bufToFloat(byte[] frame, NDManager manager) {

        //System.out.println(Arrays.toString(frame));
        //System.out.println(HexUtil.encodeHex(frame));
        int size = frame.length / n_bytes;
        int[] framei = new int[size];
        for (int i = 0; i < size; i++) {
            framei[i] = IntegerConversion.convertTwoBytesToInt1(frame[2 * i], frame[2 * i + 1]);
        }
        NDArray ans = manager.create(framei).toType(DataType.FLOAT32, false).mul(scale);

        return ans;
    }

    public static void parseInfo(String s) {
        /* Given relevant data from the ffmpeg output, set audio
        parameter fields on this object.*/
        // Sample rate.
        Matcher match = re.matcher(s);
        if (match.find()) {
            samplerate = Integer.valueOf(match.group(1));
        } else {
            samplerate = 0;
        }
        // Channel count.

        match = rec.matcher(s);
        if (match.find()) {
            String mode = match.group(1);
            if (Objects.equals(mode, "stereo")) {
                channels = 2;
            } else {
                Matcher cmatch = rec1.matcher(mode);
                if (cmatch.find()) {
                    channels = Stream.of(cmatch.group().split("\\.")).mapToInt(t -> Integer.valueOf(t)).sum();
                } else {
                    channels = 1;
                }
            }
        } else {
            channels = 0;
        }
        // Duration.
        match = rec2.matcher(s);
        if (match.find()) {
            String index = match.group();
            index = index.substring(10, index.length());
            String[] dur = index.split(":");
            String[] dur1 = dur[2].split("\\.");
            duration = Float.valueOf(dur[0]) * 60 * 60 + Float.valueOf(dur[1]) * 60 + Float.valueOf(dur1[0]) + Float.valueOf(dur1[1]) / 10;

        }
    }
}
