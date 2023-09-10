package com.chua.oshi.support;

import com.chua.common.support.net.IpUtils;
import com.chua.common.support.net.NetUtils;
import com.chua.common.support.utils.NumberUtils;
import com.chua.common.support.utils.StringUtils;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

/**
 * oshi
 *
 * @author CH
 */
public class Oshi {

    private static final int OSHI_WAIT_SECOND = 1000;

    final static SystemInfo si = new SystemInfo();

    /**
     * 新 SysFile
     *
     * @return {@link Mem}
     */
    public static List<SysFile> newSysFile() {
        OperatingSystem os = si.getOperatingSystem();
        FileSystem fileSystem = os.getFileSystem();
        List<OSFileStore> fsArray = fileSystem.getFileStores();
        List<SysFile> sysFiles = new ArrayList<>(fsArray.size());
        for (OSFileStore fs : fsArray) {
            long free = fs.getUsableSpace();
            long total = fs.getTotalSpace();
            long used = total - free;
            SysFile sysFile = new SysFile();
            sysFile.setDirName(fs.getMount());
            sysFile.setSysTypeName(fs.getType());
            sysFile.setTypeName(fs.getName());
            sysFile.setTotal(StringUtils.getNetFileSizeDescription(total));
            sysFile.setFree(StringUtils.getNetFileSizeDescription(free));
            sysFile.setUsed(StringUtils.getNetFileSizeDescription(used));
            sysFile.setUsage(NumberUtils.round(NumberUtils.mul(used, total, 4), 100).doubleValue());
            sysFiles.add(sysFile);
        }
        return sysFiles;
    }

    /**
     * 新 SysInfo
     *
     * @return {@link Mem}
     */
    public static Sys newSys() {
        Sys sys = new Sys();
        Properties props = System.getProperties();
        sys.setComputerName(IpUtils.getHostName());
        sys.setComputerIp(NetUtils.getLocalIpv4());
        sys.setOsName(props.getProperty("os.name"));
        sys.setOsArch(props.getProperty("os.arch"));
        sys.setUserDir(props.getProperty("user.dir"));
        return sys;
    }
    /**
     * 新 Mem
     *
     * @return {@link Mem}
     */
    public static Mem newMem() {
        HardwareAbstractionLayer hal = si.getHardware();
        GlobalMemory memory = hal.getMemory();
        Mem mem = new Mem();
        mem.setTotal(memory.getTotal());
        mem.setUsed(memory.getTotal() - memory.getAvailable());
        mem.setFree(memory.getAvailable());
        return mem;
    }

    /**
     * 新 process
     *
     * @return {@link Jvm}
     */
    public static List<Process> newProcess() {
        List<Process> rs = new LinkedList<>();
        OperatingSystem operatingSystem = si.getOperatingSystem();
        List<OSProcess> processes = operatingSystem.getProcesses();
        for (OSProcess process : processes) {
            Process process1 = new Process();
            process1.setId(process1.getId());
            process1.setName(process.getName());
            process1.setCommand(process.getCommandLine());
        }
        return rs;
    }

    /**
     * 新Jvm
     *
     * @return {@link Jvm}
     */
    public static Jvm newJvm() {
        Jvm jvm = new Jvm();
        Properties props = System.getProperties();
        jvm.setTotal(Runtime.getRuntime().totalMemory());
        jvm.setMax(Runtime.getRuntime().maxMemory());
        jvm.setFree(Runtime.getRuntime().freeMemory());
        jvm.setVersion(props.getProperty("java.version"));
        jvm.setHome(props.getProperty("java.home"));
        return jvm;
    }

    /**
     * 新cpu
     *
     * @return {@link Cpu}
     */
    public static Cpu newCpu(int sleepTime) {
        HardwareAbstractionLayer hal = si.getHardware();
        CentralProcessor processor = hal.getProcessor();
        return new Cpu(processor, sleepTime);
    }


}
