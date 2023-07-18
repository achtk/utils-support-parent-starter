package com.chua.example.easy;

import com.alibaba.fastjson2.JSON;
import com.chua.easy.support.config.Config;
import com.chua.easy.support.distinguish.Distinguish;
import com.chua.easy.support.entity.FoodPicture;
import com.chua.easy.support.entity.PicturePosition;
import com.chua.easy.support.tools.Picture;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author CH
 */
public class Example {

    public static void main(String[] args) throws Exception {
        train();
    }

    private static void train() throws Exception {
        Picture picture = new Picture();//图片解析类
        Config config = new Config();//配置文件
        config.setTypeNub(2);//设置训练种类数
        config.setBoxSize(125);//设置物体大致大小 单位像素 即 125*125 的矩形
        File file = new File("D:\\1\\indoor scenens\\indoor scenens\\archive\\indoorCVPR_09\\Images");
        config.setPictureNumber(5);//设置每个种类训练图片数量 某个类别有几张照片，注意所有种类照片数量要保持一致
        config.setPth(0.7);//设置可信概率，只有超过可信概率阈值，得出的结果才是可信的 数值为0-1之间
        config.setShowLog(true);//输出学习时打印数据
        Distinguish distinguish = new Distinguish(config);//创建识别类
        distinguish.setBackGround(picture.getThreeMatrix("E:\\yolov5\\datasets\\right-wrong\\images\\train\\000000000026.jpg"));//设置识别的背景图片(该api为固定背景)
        List<FoodPicture> foodPictures = new ArrayList<>();//创建训练模板集合
        for (int i = 1; i < 3; i++) {
            FoodPicture foodPicture = new FoodPicture();//创建每一类图片的训练模板类
            foodPictures.add(foodPicture);//将该类模板加入集合
            List<PicturePosition> picturePositionList = new ArrayList<>();//创建该类模板的训练集合类
            foodPicture.setId(i + 1);//设置该图片类别id
            foodPicture.setPicturePositionList(picturePositionList);
            for (int j = 1; j < 6; j++) {//训练图片数量为 每种五张 注意跟config 中的 pictureNumber 要一致
                String name;
                if (i == 1) {//加载图片url地址名称
                    name = "a";
                } else {
                    name = "b";
                }
                PicturePosition picturePosition = new PicturePosition();
                picturePosition.setUrl("E:\\ls\\fp15\\" + name + i + ".jpg");//加载该类别图片地址
                picturePosition.setNeedCut(false);//是否需要剪切，若训练素材为充满全图图片，则充满全图不需要剪切 写false
                picturePositionList.add(picturePosition);//加载
            }
        }
        distinguish.studyImage(foodPictures);//进行学习
        System.out.println(JSON.toJSONString(distinguish.getModel()));//输出模型保存,将模型实体类序列化为json保存
    }
}
