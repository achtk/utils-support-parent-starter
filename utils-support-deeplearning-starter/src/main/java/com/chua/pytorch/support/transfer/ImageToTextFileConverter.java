package com.chua.pytorch.support.transfer;

import com.chua.common.support.constant.PredictResult;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.common.support.file.transfer.AbstractFileConverter;
import com.chua.pytorch.support.ocr.detector.OcrDetector;
import com.chua.pytorch.support.ocr.recognizer.OcrRecognizer;
import com.chua.pytorch.support.ocr.rotation.OcrDirectionDetector;
import com.chua.pytorch.support.utils.LocationUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * image -> txt
 * @author CH
 */
public class ImageToTextFileConverter extends AbstractFileConverter {
    @Override
    public String target() {
        return "txt";
    }

    @Override
    public String source() {
        return DEFAULT_SIMPLE_PIC;
    }

    @Override
    public void convert(String type, InputStream inputStream, String suffix, OutputStream outputStream) throws Exception {
        OcrDetector ocrDetector = new OcrDetector(DetectionConfiguration.builder().build());
        OcrDirectionDetector directionDetector = new OcrDirectionDetector(DetectionConfiguration.builder().build());

        StringBuffer stringBuffer = new StringBuffer();
        OcrRecognizer recognizer = new OcrRecognizer(directionDetector, ocrDetector, DetectionConfiguration.builder().build());
        List<PredictResult> recognize = recognizer.recognize(inputStream);
        for (PredictResult predictResult : recognize) {
            stringBuffer.append(predictResult.getText()).append("\r\n");
        }

        try (OutputStream output = outputStream) {
            output.write(stringBuffer.toString().getBytes(StandardCharsets.UTF_8));
            output.flush();
        }
    }
}
