package com.chua.anpr.support.base;

import com.chua.common.support.os.SharedLoader;
import org.opencv.core.Core;

public abstract class OpenCVLoader {

    //静态加载动态链接库
    static{
        SharedLoader.load(Core.NATIVE_LIBRARY_NAME);
        nu.pattern.OpenCV.loadShared();
    }

}
