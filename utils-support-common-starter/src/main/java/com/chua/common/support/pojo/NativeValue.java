package com.chua.common.support.pojo;

import com.chua.common.support.constant.Projects;
import com.chua.common.support.utils.StringUtils;
import lombok.Builder;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.chua.common.support.constant.CommonConstant.FILE;

/**
 * 软连接
 *
 * @author CH
 */
@Builder
public class NativeValue {

    @Builder.Default
    private String win = "win";
    @Builder.Default
    private String linux = "linux";
    @Builder.Default
    private String unix = "unix";
    @Builder.Default
    private String root = "native";

    private String name;

    public void link() {
        StringBuffer stringBuffer = new StringBuffer(root).append("/");
        if (Projects.isWindows()) {
            stringBuffer.append(win);
        } else if (Projects.isLinux()) {
            stringBuffer.append(linux);
        } else {
            stringBuffer.append(unix);
        }

        String version = Projects.osArch();
        if (!StringUtils.isEmpty(version)) {
            stringBuffer.append("-").append(version.contains("64") ? "x64" : "x86");
        }

        stringBuffer.append("/").append(name);
        if (Projects.isWindows()) {
            stringBuffer.append(".dll");
        } else if (Projects.isLinux()) {
            stringBuffer.append(".so");
        } else {
            stringBuffer.append(".lib");
        }


        URL resource = ClassLoader.getSystemClassLoader().getResource(stringBuffer.toString());
        if (null == resource) {
            return;
        }

        String modelPath = null;
        String protocol = resource.getProtocol();
        if (FILE.equals(protocol)) {
            modelPath = new File(resource.getFile()).getParent();
        } else {
            try {
                Path tempDirectory = Files.createTempDirectory(name);
                Files.copy(resource.openStream(), tempDirectory);
                modelPath = tempDirectory.toFile().getAbsolutePath();
            } catch (IOException ignored) {
            }
        }
        String separator = System.getProperty("path.separator");
        String sysLib = System.getProperty("java.library.path");
        if (sysLib.endsWith(separator)) {
            System.setProperty("java.library.path", sysLib + modelPath);
            Projects.refreshSystem();
            return;
        }
        System.setProperty("java.library.path", sysLib + separator + modelPath);
        Projects.refreshSystem();
    }
}
