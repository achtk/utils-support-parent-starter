package com.chua.common.support.lang.exception;

import com.chua.common.support.os.Arch;
import com.chua.common.support.os.OS;

/**
 * UnsupportedPlatformException
 * @author CH
 */
public class UnsupportedPlatformException extends RuntimeException{
    public UnsupportedPlatformException(OS os, Arch arch) {
        super(String.format("Operating system \"%s\" and architecture \"%s\" are not supported.", os, arch));
    }
}
