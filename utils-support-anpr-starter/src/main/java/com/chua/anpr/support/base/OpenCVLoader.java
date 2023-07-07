package com.chua.anpr.support.base;

public abstract class OpenCVLoader {

    //静态加载动态链接库
    static{ nu.pattern.OpenCV.loadShared(); }

}
