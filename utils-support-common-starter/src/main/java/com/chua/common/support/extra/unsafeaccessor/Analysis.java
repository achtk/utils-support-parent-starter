package com.chua.common.support.extra.unsafeaccessor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

interface Analysis {
    @Retention(RetentionPolicy.CLASS)
    @interface SkipAnalysis {
    }
}
