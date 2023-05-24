package com.chua.xjar.support.io.loadkit.loader;

import com.chua.common.support.constant.Projects;
import com.chua.common.support.file.compress.ZipCompressFile;
import com.chua.common.support.lang.cmd.ProcessFactory;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.ArrayUtils;
import com.chua.common.support.utils.FileUtils;
import com.chua.common.support.utils.StringUtils;
import com.chua.xjar.support.io.xjar.XCryptos;
import com.chua.xjar.support.io.xjar.XEntryFilter;
import com.chua.xjar.support.io.xjar.boot.XBoot;
import org.apache.commons.compress.archivers.jar.JarArchiveEntry;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * kit
 *
 * @author CH
 */
public class Kit {

    private static final String X_JAR = "xjar.go";
    private static final String X_JAR_PATENTABLE = "xjar_agentable.go";
    private final String golang;

    private String jar;
    private JarType jarType;
    private String password = UUID.randomUUID().toString();
    private XEntryFilter<JarArchiveEntry> filter;
    private XEntryFilter<JarArchiveEntry> dependsFilter;
    private String fileName;
    private String outFile;
    private String folder;
    private String[] exclude;
    private String[] depends;
    private String launch = "ejar";

    /**
     * 初始化
     *
     * @param golang golang根目录
     */
    public Kit(String golang) {
        this.golang = golang;
    }

    /**
     * 构造器
     *
     * @return 构造器
     */
    public static KitBuilder newBuilder() {
        return new KitBuilder(null);
    }

    /**
     * 构造器
     *
     * @param golang golang根目录
     * @return 构造器
     */
    public static KitBuilder newBuilder(String golang) {
        return new KitBuilder(golang);
    }

    /**
     * 压缩
     */
    public void compress() throws Exception {
        File temp = new File(outFile);
//        FileUtils.deleteDirectory(temp.getParentFile().toPath());
//
        compile(true, true);
        ZipCompressFile zipFile = new ZipCompressFile(new File(temp.getParent(), FileUtils.getBaseName(temp) + ".zip"));
        zipFile.pack(folder, false, "*");
    }

    public static class KitBuilder {

        private final Kit kit;

        public KitBuilder(String golang) {
            kit = new Kit(golang);
        }

        /**
         * 启动器
         *
         * @param launch 启动器
         * @return this
         */
        public KitBuilder launch(String launch) {
            kit.launch = launch;
            return this;
        }

        /**
         * 加密jar
         *
         * @param jar jar
         * @return this
         */
        public KitBuilder from(String jar) {
            kit.jar = jar;
            return this;
        }


        /**
         * 排除文件
         *
         * @param exclude 排除文件
         * @return this
         */
        public KitBuilder exclude(String... exclude) {
            kit.exclude = exclude;
            return this;
        }

        /**
         * 排除jar
         *
         * @param depends 依赖
         * @return this
         */
        public KitBuilder excludeDepends(String... depends) {
            kit.depends = depends;
            return this;
        }

        /**
         * jar类型
         *
         * @param filter jarType
         * @return this
         */
        public KitBuilder filter(XEntryFilter<JarArchiveEntry> filter) {
            kit.filter = filter;
            return this;
        }

        /**
         * jar类型
         *
         * @param jarType jarType
         * @return this
         */
        public KitBuilder jarType(JarType jarType) {
            kit.jarType = jarType;
            return this;
        }

        /**
         * kit
         *
         * @param outFile 输出
         * @return kit
         */
        public Kit build(String outFile) throws Exception {
            kit.to(outFile);
            return kit;
        }
    }

    /**
     * 输出
     *
     * @param outFile 输出
     */
    public void to(String outFile) throws Exception {
        File temp = new File(outFile);
        FileUtils.forceMkdirParent(temp);
        this.fileName = temp.getName();
        this.outFile = outFile;
        this.folder = FileUtils.getFullPath(outFile);
        if (StringUtils.isNullOrEmpty(jar)) {
            return;
        }

        if (!ArrayUtils.isEmpty(exclude)) {
            filter = entry -> {
                String name = entry.getName();
                return !ArrayUtils.isMatch(exclude, name);
            };
        }

        dependsFilter = entry -> {
            String name = entry.toString();
            return !ArrayUtils.isMatch(depends, name);
        };

    }

    /**
     * 编译文件
     *
     * @param clear 是否清理
     */
    public void compile(boolean clear, boolean step) throws Exception {
        FileUtils.forceMkdirParent(new File(outFile));

        if (JarType.BOOT == jarType) {
            XBoot.encrypt(jar, outFile, password, filter, dependsFilter);
            return;
        }
        XCryptos.encrypt(jar, outFile, password, filter, dependsFilter);
        if (!step) {
            return;
        }

        StringBuffer stringBuffer = new StringBuffer();
        String suffix = "bat";
        stringBuffer.append("cd ").append(folder).append("\r\n");
        stringBuffer.append("go").append(Projects.isLinux() ? "" : ".exe").append(" build ").append(X_JAR).append("\r\n");
        if (clear) {
            if (Projects.isLinux()) {
                stringBuffer.append("rm -f ").append(X_JAR).append("\r\n");
                stringBuffer.append("rm -f ").append(X_JAR_PATENTABLE).append("\r\n");
            } else {
                stringBuffer.append("del /a/f/q ").append(X_JAR).append("\r\n");
                stringBuffer.append("del /a/f/q ").append(X_JAR_PATENTABLE).append("\r\n");

            }
        }

        Path path = Paths.get(folder, "install." + suffix);
        try {
            Files.write(path, stringBuffer.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException ignored) {
        }


        stringBuffer.delete(0, stringBuffer.length());
        if (Projects.isLinux()) {
            suffix = "sh";
            stringBuffer.append("./").append(launch).append(" java -jar ").append(fileName);
        } else {
            stringBuffer.append(launch).append(".exe java -jar ").append(fileName);
        }
        try {
            Files.write(Paths.get(folder, "start." + suffix), stringBuffer.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException ignored) {
        }

        ProcessFactory processFactory = ServiceProvider.of(ProcessFactory.class).getSpiService();

        String binRoot = "";
        if (!StringUtils.isNullOrEmpty(golang)) {
            binRoot = StringUtils.endWithAppend(golang, File.separator + "bin");
        }

        ProcessFactory.ProcessStatus status = processFactory
                .substitution("PATH", System.getenv("PATH") + ";" + binRoot)
                .substitution("GOCACHE", Projects.userDir() + File.separator + ".cache")
                .directory(folder)
                .timeout(10000)
                .exec(folder + "install." + suffix);


        if (status.getCode() == 0) {
            try {
                Files.delete(path);
            } catch (IOException ignored) {
            }
        }

        String suffix1 = ".sh";
        if (Projects.isWindows()) {
            suffix1 = ".exe";
        }
        Path path1 = Paths.get(folder, "xjar" + suffix1);
        if (Files.exists(path1)) {
            path1.toFile().renameTo(new File(folder, launch + suffix1));
        }
    }


    public static enum JarType {
        /**
         * boot
         */
        BOOT,
        /**
         * jar
         */
        JAR;
    }
}
