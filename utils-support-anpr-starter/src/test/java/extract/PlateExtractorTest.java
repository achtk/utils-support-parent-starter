package extract;

import base.BaseTest;
import com.chua.anpr.support.domain.*;
import com.chua.anpr.support.extract.PlateExtractor;
import com.chua.anpr.support.extract.PlateExtractorImpl;
import com.chua.anpr.support.models.TorchPlateDetection;
import com.chua.anpr.support.models.TorchPlateRecognition;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlateExtractorTest  extends BaseTest {

    private static String plateDetectionPath = "E://plate_detect.onnx";
    private static String plateRecognitionPath = "E://plate_rec_color.onnx";

    public static void main(String[] args) {
        TorchPlateDetection torchPlateDetection = new TorchPlateDetection(plateDetectionPath, 1);
        TorchPlateRecognition torchPlateRecognition = new TorchPlateRecognition(plateRecognitionPath, 1);
        PlateExtractor extractor = new PlateExtractorImpl(torchPlateDetection, torchPlateRecognition);

        String imagePath = "E://images";
        Map<String, String> map = getImagePathMap(imagePath);
        for(String fileName : map.keySet()) {
            String imageFilePath = map.get(fileName);
            System.out.println(imageFilePath);
            Mat image = Imgcodecs.imread(imageFilePath);
            long startTime = System.currentTimeMillis();
            ExtParam extParam = ExtParam.build()
                    .setTopK(20)
                    .setScoreTh(0.3f)
                    .setIouTh(0.5f);
            //推理
            PlateImage plateImage = extractor.extract(ImageMat.fromCVMat(image), extParam, new HashMap<>());
            System.out.println("cost:" + (System.currentTimeMillis()-startTime));
            List<PlateInfo> plateInfos = plateImage.PlateInfos();
            //可视化
            DrawImage drawImage = DrawImage.build(imageFilePath);
            for(PlateInfo plateInfo: plateInfos){
                //画框
                PlateInfo.Point [] points = plateInfo.box.toArray();
                for(int i =0; i< points.length; i++){
                    if(i+1 == points.length){
                        drawImage.drawLine(
                                new DrawImage.Point((int)points[i].x, (int)points[i].y),
                                new DrawImage.Point((int)points[0].x, (int)points[0].y),
                                2, Color.RED
                        );
                    }else{
                        drawImage.drawLine(
                                new DrawImage.Point((int)points[i].x, (int)points[i].y),
                                new DrawImage.Point((int)points[i+1].x, (int)points[i+1].y),
                                2, Color.RED
                        );
                    }
                }
                //添加文本
                PlateInfo.ParseInfo parseInfo = plateInfo.parseInfo;
                int fonSize = Float.valueOf(plateInfo.box.width() / parseInfo.plateNo.length() * 1.4f).intValue();
                drawImage.drawText(parseInfo.plateNo,
                        new DrawImage.Point((int)points[0].x, (int)points[0].y-(int)(fonSize*2.2)), fonSize, Color.RED);
                drawImage.drawText((plateInfo.single ? "单排" : "双排") + ":" + parseInfo.plateColor,
                        new DrawImage.Point((int)points[0].x, (int)points[0].y-(int)(fonSize*1.2)), fonSize, Color.RED);
            }
            //show
//            ImageMat.fromCVMat(drawImage.toMat()).imShow();
            image.release();
        }
    }
}
