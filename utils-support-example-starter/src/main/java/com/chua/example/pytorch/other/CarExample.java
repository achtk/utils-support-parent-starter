package com.chua.example.pytorch.other;

import com.chua.anpr.support.domain.ImageMat;
import com.chua.anpr.support.domain.PlateInfo;
import com.chua.anpr.support.models.TorchPlateDetection;
import com.chua.anpr.support.models.TorchPlateRecognition;

import java.util.List;

/**
 * @author CH
 */
public class CarExample {

    public static void main(String[] args) {
        TorchPlateDetection torchPlateDetection = new TorchPlateDetection(
                "Z:\\workspace\\utils-support-parent-starter\\utils-support-anpr-starter\\src\\main\\resources\\models\\plate_detect.onnx", 1);
        List<PlateInfo> inference = torchPlateDetection.inference(ImageMat.fromImage("E:\\images\\image003.jpg"), 0f, 0f, null);
        System.out.println(inference);
        TorchPlateRecognition torchPlateRecognition = new TorchPlateRecognition(
                "Z:\\workspace\\utils-support-parent-starter\\utils-support-anpr-starter\\src\\main\\resources\\models\\plate_rec_color.onnx", 1);
        PlateInfo.ParseInfo inference1 = torchPlateRecognition.inference(ImageMat.fromImage("E:\\images\\image003.jpg"), false, null);
        System.out.println();


    }
}
