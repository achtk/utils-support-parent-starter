package com.chua.pytorch.support.diffusion;

import ai.djl.Device;
import ai.djl.huggingface.tokenizers.HuggingFaceTokenizer;
import ai.djl.modality.cv.BufferedImageFactory;
import ai.djl.modality.cv.Image;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.DataType;
import ai.djl.ndarray.types.Shape;
import com.chua.common.support.constant.PredictResult;
import com.chua.common.support.feature.DetectionConfiguration;
import com.chua.common.support.utils.ArrayUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 文本扩散图片
 *
 * @author CH
 */
public class Diffusion {

    private final int MAX_LENGTH = 77;
    private NDManager manager =null;

    private final HuggingFaceTokenizer tokenizer;

    {
        try {
            tokenizer =
                    HuggingFaceTokenizer.builder()
                            .optManager(manager)
                            .optPadding(true)
                            .optPadToMaxLength()
                            .optMaxLength(MAX_LENGTH)
                            .optTruncation(true)
                            .optTokenizerName("openai/clip-vit-large-patch14")
                            .build();
            // sentence-transformers/msmarco-distilbert-dot-v5
            // openai/clip-vit-large-patch14
            // https://huggingface.co/sentence-transformers/msmarco-distilbert-dot-v5
            // https://huggingface.co/runwayml/stable-diffusion-v1-5/blob/main/tokenizer/tokenizer_config.json
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    private final TextDecoder textDecoder;
    private final TextEncoder textEncoder;
    private final TextNetEncoder textNetEncoder;


    public Diffusion(DetectionConfiguration detectionConfiguration) {
        if("GPU".equalsIgnoreCase(detectionConfiguration.device())) {
            this.manager = NDManager.newBaseManager(Device.of(detectionConfiguration.device(), detectionConfiguration.deviceId()), "PyTorch");
        } else {
            this.manager = NDManager.newBaseManager(Device.cpu(), "PyTorch");
        }
        this.textEncoder = new TextEncoder(detectionConfiguration);
        this.textNetEncoder = new TextNetEncoder(detectionConfiguration);
        this.textDecoder = new TextDecoder(detectionConfiguration);
    }


    public List<PredictResult> detect(Object face) throws Exception {
        if(!face.getClass().isArray()) {
            face = new String[]{face.toString()};
        }

        String[] args = (String[]) face;
        NDList textEncoding = textEncoder(SDTextTokenizer(args[0]));
        NDList uncondEncoding = textEncoder(SDTextTokenizer(ArrayUtils.getIndex(args, 1, "")));

        NDArray textEncodingArray = textEncoding.get(1);
        NDArray uncondEncodingArray = uncondEncoding.get(1);

        NDArray embeddings = uncondEncodingArray.concat(textEncodingArray);

        int width = 512;
        int height = 512;
        Shape shape = new Shape(1, 4, height / 8, width / 8);
        NDArray latent = manager.randomNormal(shape);

        StableDiffusionPNDMScheduler scheduler = new StableDiffusionPNDMScheduler(manager);
        int offset = 1;
        int steps = 25;
        scheduler.setTimesteps(steps, offset);

//        for (int i = (int) scheduler.timesteps.size() - 1; i >= 0; i--) {
        for (int i = 0; i < (int) scheduler.timesteps.size(); i++) {
            NDArray t = manager.create(scheduler.timesteps.toArray()[i]);
            // expand the latents if we are doing classifier free guidance
            NDArray latentModelInput = latent.concat(latent);
            // embeddings 2,77,768
            // t tensor 981
            // latentModelOutput 2,4,64,64

            NDArray noisePred = textNetEncoder.detect(buildUnetInput(embeddings, t, latentModelInput)).get(0).getValue(NDList.class).get(0);

            NDList splitNoisePred = noisePred.split(2);
            NDArray noisePredUncond = splitNoisePred.get(0);
            NDArray noisePredText = splitNoisePred.get(1);

            NDArray scaledNoisePredUncond = noisePredText.add(noisePredUncond.neg());
            float guidanceScale = (float) 7.5;
            scaledNoisePredUncond = scaledNoisePredUncond.mul(guidanceScale);
            noisePred = noisePredUncond.add(scaledNoisePredUncond);

            latent = scheduler.step(noisePred, t, latent);
        }
        NDArray input = latent;
        input = input.div(0.18215);

        NDList encoded = new NDList();
        encoded.add(input);
        NDList decoded = sdDecoderPredictor(encoded);
        NDArray scaled = decoded.get(0).div(2).add(0.5).clip(0, 1);

        scaled = scaled.transpose(0, 2, 3, 1);
        scaled = scaled.mul(255).round().toType(DataType.INT8, true).get(0);
        Image image = BufferedImageFactory.getInstance().fromNDArray(scaled);
        Object wrappedImage = image.getWrappedImage();

        return Collections.singletonList(new PredictResult().setNdArray(wrappedImage));
    }


    private NDList buildUnetInput(NDArray input, NDArray timestep, NDArray latents) {
        input.setName("encoder_hidden_states");
        NDList list = new NDList();
        list.add(latents);
        list.add(timestep);
        list.add(input);
        return list;
    }

    private NDList textEncoder(NDList input) {
        List<PredictResult> detect = textEncoder.detect(input);
        return detect.get(0).getValue(NDList.class);
    }

    private NDList sdDecoderPredictor(NDList input) {
        List<PredictResult> detect = textDecoder.detect(input);
        return detect.get(0).getValue(NDList.class);
    }


    private NDList SDTextTokenizer(String prompt) {
        List<String> tokens = tokenizer.tokenize(prompt);
        int[][] tokenValues = new int[1][MAX_LENGTH];
        ObjectMapper mapper = new ObjectMapper();
        String sdArtifacts = "./pytorch_cpu";
        File fileObj = new File(sdArtifacts + "/vocab_dictionary.json"); // full_vocab.json vocab_dictionary.json
        try {
            Map<String, Integer> mapObj = mapper.readValue(fileObj, new TypeReference<Map<String, Integer>>() {
            });
            int counter = 0;
            for (String token : tokens) {
                if (mapObj.get(token) != null) {
                    tokenValues[0][counter] = mapObj.get(token);
                } else {
                    int UNKNOWN_TOKEN = 49407;
                    tokenValues[0][counter] = UNKNOWN_TOKEN;
                }
                counter++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        NDArray ndArray = manager.create(tokenValues);
        return new NDList(ndArray);
    }
}
