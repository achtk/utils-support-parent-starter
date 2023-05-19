package com.chua.opencv.support.filter;

import com.chua.common.support.constant.Projects;
import com.chua.common.support.image.filter.AbstractImageFilter;
import org.opencv.core.Core;

/**
 * opencv
 * @author CH
 */
public abstract class AbstractOpencvImageFilter extends AbstractImageFilter {
    static {
        Projects.installDependency("**/*/", "opencv_java", Projects.Dependency.builder().build());
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }
}
