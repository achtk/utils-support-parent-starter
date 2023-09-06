package com.chua.common.support.soft;

import com.chua.common.support.file.Decompress;
import com.chua.common.support.io.ProgressInputStream;
import com.chua.common.support.os.OS;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.ArrayUtils;
import com.chua.common.support.utils.FileUtils;
import com.chua.common.support.utils.StringUtils;
import com.chua.common.support.utils.ThreadUtils;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * 软件
 *
 * @author CH
 * @since 2023/09/06
 */
@Builder
@Data
@Slf4j
public class Soft {

    /**
     * 软件路径
     */
    @Builder.Default
    private String softPath = ".";

    /**
     * 安装目录
     */
    @Builder.Default
    private String installPath = ".";
    /**
     * 线程池
     */
    @Builder.Default
    private ExecutorService executorService = ThreadUtils.newProcessorThreadExecutor("service");

    /**
     * 软件列表
     * @return 列表
     */
    public List<SoftInfo> list() {
        List<SoftInfo> rs = new LinkedList<>();
        OS os = OS.getCurrent();
        try {
            Files.walkFileTree(Paths.get(softPath, os.name().toLowerCase()), Collections.emptySet(), 2, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    String fileName = dir.getFileName().toString();
                    if(fileName.equalsIgnoreCase(os.name())) {
                        return FileVisitResult.CONTINUE;
                    }
                    int index = fileName.indexOf("-");
                    if(index > -1) {
                        rs.add(SoftInfo.builder().name(fileName.substring(0, index)).version(fileName.substring(index + 1)).build());
                    } else {
                        rs.add(SoftInfo.builder().name(fileName).build());
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return rs;
    }

    /**
     * 安装
     *
     * @param name    名称
     */
    public void install(String name) {
        install(name,  initialVersion(name));
    }
    /**
     * 安装
     *
     * @param name    名称
     * @param version 版本
     */
    public void install(String name, String version) {

        if(isInstalled(name, version)) {
            log.info("{}(ver.{})已安装", name, version);
            return;
        }
        OS os = OS.getCurrent();
        Path path = Paths.get(softPath, os.name().toLowerCase(), name + "-" + version);
        log.info("开始安装 {}(ver.{})", name, version);
        File[] files = path.toFile().listFiles();
        if(ArrayUtils.isEmpty(files)) {
            log.error("安装包不存在");
            return;
        }
        File file = files[0];
        Decompress decompress = ServiceProvider.of(Decompress.class).getNewExtension(FileUtils.getExtension(file.getName()));
        try(ProgressInputStream fis = new ProgressInputStream(Files.newInputStream(file.toPath()))) {
            decompress.unFile(fis, new File(installPath, os.name().toLowerCase() + "/" + name + "-" + version));
        } catch (IOException e) {
            log.error("安装失败");
            throw new RuntimeException(e);
        }
        log.info("安装成功");
    }

    /**
     * 初始版本
     *
     * @param name 名称
     * @return {@link String}
     */
    private String initialVersion(String name) {
        List<SoftInfo> list = list();
        for (SoftInfo softInfo : list) {
            if(name.equals(softInfo.getName())) {
                return softInfo.getVersion();
            }
        }

        return "";
    }
    /**
     * 卸载
     *
     * @param name    名称
     */
    public void uninstall(String name) {
        uninstall(name, initialVersion(name));
    }
    /**
     * 卸载
     *
     * @param name    名称
     * @param version 版本
     */
    public void uninstall(String name, String version) {
        if(!isInstalled(name, version)) {
            log.info("{}(ver.{})未安装", name, version);
            return;
        }
        OS os = OS.getCurrent();
        Path path = Paths.get(installPath, os.name().toLowerCase(), name + "-" + version);
        log.info("开始卸载 {}(ver.{})", name, version);
        try {
            FileUtils.forceDeleteDirectory(path.toFile());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("卸载成功");
    }

    /**
     * 已安装
     *
     * @param name    名称
     * @return boolean
     */
    private boolean isInstalled(String name) {
        return isInstalled(name, initialVersion(name));
    }
    /**
     * 已安装
     *
     * @param name    名称
     * @param version 版本
     * @return boolean
     */
    private boolean isInstalled(String name, String version) {
        OS os = OS.getCurrent();
        Path path = Paths.get(installPath, os.name().toLowerCase(), name + "-" + version);
        if(Files.exists(path)) {
            return true;
        }

        return false;
    }

    /**
     * 开始
     *
     * @param name    名称
     * @return 服务
     */
    public SoftService createService(String name) {
        return createService(name, initialVersion(name));
    }
    /**
     * 开始
     *
     * @param name    名称
     * @param version 版本
     * @return 服务
     */
    public SoftService createService(String name, String version) {
        version = initialVersion(name);
        if(!isInstalled(name, version)) {
            throw new RuntimeException(StringUtils.format("{}({})未安装", name, version));
        }
        return ServiceProvider.of(SoftService.class).getNewExtension(name, this, version);
    }
}
