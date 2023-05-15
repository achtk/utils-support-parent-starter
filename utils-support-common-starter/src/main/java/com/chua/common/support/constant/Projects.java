package com.chua.common.support.constant;

import com.chua.common.support.file.tar.TarEntry;
import com.chua.common.support.file.tar.TarInputStream;
import com.chua.common.support.file.xz.XZInputStream;
import com.chua.common.support.function.Joiner;
import com.chua.common.support.lang.process.ProgressBar;
import com.chua.common.support.lang.process.ProgressStyle;
import com.chua.common.support.log.Log;
import com.chua.common.support.pojo.ComputerUniqueIdentification;
import com.chua.common.support.resource.ResourceProvider;
import com.chua.common.support.resource.repository.Metadata;
import com.chua.common.support.resource.repository.Repository;
import com.chua.common.support.resource.resource.Resource;
import com.chua.common.support.utils.*;
import lombok.Builder;
import lombok.Data;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.Query;
import java.io.*;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 项目信息
 * @author CH
 */
public final class Projects {

    private static final Log log = Log.getLogger(Projects.class);
    /**
     * Windows
     */
    public static final String WINDOWS = "windows";
    /**
     * Linux
     */
    public static final String LINUX = "linux";
    /**
     * Unix
     */
    public static final String UNIX = "unix";
    /**
     * 正则表达式
     */
    public static final String REGEX = "\\b\\w+:\\w+:\\w+:\\w+:\\w+:\\w+\\b";

    /**
     * pid
     *
     * @return pid
     */
    public static String getPid() {
        String name = ManagementFactory.getRuntimeMXBean().getName();
        return name.split("@")[0];
    }

    /**
     * 獲取本地地址
     *
     * @return 本地地址
     */
    public static List<InetAddress> getAddressList() {
        List<InetAddress> ipList = new ArrayList<>();
        Enumeration<NetworkInterface> networkInterfaces =
                null;
        try {
            networkInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            return ipList;
        }
        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaces.nextElement();
            Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
            while (inetAddresses.hasMoreElements()) {
                InetAddress inetAddress = inetAddresses.nextElement();
                if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) {
                    //非链接和回路真实ip
                    ipList.add(inetAddress);
                }
            }
        }

        return ipList;
    }

    /**
     * 獲取本地mac
     *
     * @return 本地mac
     */
    public static List<String> getMacList() {
        List<String> macList = new ArrayList<>();
        Enumeration<NetworkInterface> networkInterfaces =
                null;
        try {
            networkInterfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            return macList;
        }
        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaces.nextElement();
            Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
            while (inetAddresses.hasMoreElements()) {
                InetAddress inetAddress = inetAddresses.nextElement();
                if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) {
                    byte[] hardwareAddress;
                    try {
                        hardwareAddress = NetworkInterface.getByInetAddress(InetAddress.getLocalHost()).getHardwareAddress();
                    } catch (Exception e) {
                        continue;
                    }

                    if (null == hardwareAddress || hardwareAddress.length == 0) {
                        continue;
                    }

                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < hardwareAddress.length; i++) {
                        if (i != 0) {
                            stringBuilder.append("-");
                        }
                        //字节转换为整数
                        int temp = hardwareAddress[i] & 0xff;
                        String toHexString = Integer.toHexString(temp);
                        if (toHexString.length() == 1) {
                            stringBuilder.append("0").append(toHexString);
                        } else {
                            stringBuilder.append(toHexString);
                        }
                    }

                    macList.add(stringBuilder.toString());
                }
            }
        }

        return macList;
    }

    /**
     * 服务器端口
     *
     * @return 服务器端口
     */
    public static Integer getTomcatServerPort() {
        MBeanServer beanServer = ManagementFactory.getPlatformMBeanServer();
        Set<ObjectName> objectNames;
        try {
            objectNames = beanServer.queryNames(new ObjectName("*:type=Connector,*"), Query.match(Query.attr("protocol"), Query.value("HTTP/1.1")));
        } catch (MalformedObjectNameException e) {
            return null;
        }
        String port;
        try {
            port = objectNames.iterator().next().getKeyProperty("port");
        } catch (NoSuchElementException e) {
            return null;
        }
        Integer portInt = null;
        if (NumberUtils.isNumber(port)) {
            portInt = Integer.valueOf(port);
        }
        return portInt;
    }

    /**
     * 是否是window
     *
     * @return window
     */
    public static boolean isWindow() {
        return osName().toUpperCase().contains("WINDOWS");
    }

    /**
     * 是否是window
     *
     * @return window
     */
    public static boolean isLinux() {
        return osName().toUpperCase().contains("LINUX");
    }

    /**
     * 默认的临时文件路径
     *
     * @return 默认的临时文件路径
     */
    public static String tmpdir() {
        return System.getProperty("java.io.tmpdir");
    }

    /**
     * Java的运行环境版本
     *
     * @return Java的运行环境版本
     */
    public static String javaVersion() {
        return System.getProperty("java.version");
    }

    /**
     * Java的安装路径
     *
     * @return Java的安装路径
     */
    public static String javaHome() {
        return System.getProperty("java.home");
    }

    /**
     * Java的类路径
     *
     * @return Java的类路径
     */
    public static String javaClassPath() {
        return System.getProperty("java.class.path");
    }

    /**
     * 加载库时搜索的路径列表
     *
     * @return 加载库时搜索的路径列表
     */
    public static String javaLibraryPath() {
        return System.getProperty("java.library.path");
    }

    /**
     * 一个或多个扩展目录的路径
     *
     * @return 一个或多个扩展目录的路径
     */
    public static String javaExtDirs() {
        return System.getProperty("java.ext.dirs");
    }

    /**
     * 操作系统的名称
     *
     * @return 操作系统的名称
     */
    public static String osName() {
        return System.getProperty("os.name");
    }

    /**
     * 操作系统的版本
     *
     * @return 操作系统的版本
     */
    public static String osVersion() {
        return System.getProperty("os.version");
    }

    /**
     * 操作系统的构架
     *
     * @return 操作系统的构架
     */
    public static String osArch() {
        return System.getProperty("os.arch");
    }

    /**
     * 文件分隔符
     *
     * @return 文件分隔符
     */
    public static String fileSeparator() {
        return System.getProperty("file.separator");
    }

    /**
     * 路径分隔符
     *
     * @return 路径分隔符
     */
    public static String pathSeparator() {
        return System.getProperty("path.separator");
    }

    /**
     * 行分隔符
     *
     * @return 行分隔符
     */
    public static String lineSeparator() {
        return System.getProperty("line.separator");
    }

    /**
     * 用户的账户名称
     *
     * @return 用户的账户名称
     */
    public static String userName() {
        return System.getProperty("user.name");
    }

    /**
     * 用户的主目录
     *
     * @return 用户的主目录
     */
    public static String userHome() {
        return System.getProperty("user.home");
    }

    /**
     * 用户的当前工作目录
     *
     * @return 用户的当前工作目录
     */
    public static String userDir() {
        return System.getProperty("user.dir");
    }

    /**
     * 用户的当前工作目录
     *
     * @return 用户的当前工作目录
     */
    public static Map<String, List<InetAddress>> ips() {
        Map<String, List<InetAddress>> result = new LinkedHashMap<>();
        try {
            Enumeration<NetworkInterface> interfaces = null;
            interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface ni = interfaces.nextElement();
                String name = ni.getName();
                Enumeration<InetAddress> addresss = ni.getInetAddresses();
                while (addresss.hasMoreElements()) {
                    InetAddress nextElement = addresss.nextElement();
                    result.computeIfAbsent(name, it -> new ArrayList<>()).add(nextElement);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 用户的当前工作目录
     *
     * @return 用户的当前工作目录
     */
    public static String mac() {
        NetworkInterface byInetAddress;
        try {
            byInetAddress = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
            byte[] bytes = byInetAddress.getHardwareAddress();
            StringBuilder mac = new StringBuilder();
            byte currentByte;
            boolean first = false;
            for (byte b : bytes) {
                if (first) {
                    mac.append("-");
                }
                currentByte = (byte) ((b & 240) >> 4);
                mac.append(Integer.toHexString(currentByte));
                currentByte = (byte) (b & 15);
                mac.append(Integer.toHexString(currentByte));
                first = true;
            }
            return mac.toString().toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 用户的当前工作目录
     *
     * @return 用户的当前工作目录
     */
    public static String ip() {
        InetAddress localHost = null;
        try {
            localHost = InetAddress.getLocalHost();
            return localHost.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 用户的当前工作目录
     *
     * @return 用户的当前工作目录
     */
    public static String hostName() {
        InetAddress localHost = null;
        try {
            localHost = InetAddress.getLocalHost();
            return localHost.getHostName();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 刷新System
     */
    public static void refreshSystem() {
        try {
            Field sysPathsField = ClassLoader.class.getDeclaredField("sys_paths");
            sysPathsField.setAccessible(true);
            sysPathsField.set(null, null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * 换行符
     *
     * @return 换行符
     */
    public static String getLineSeparator() {
        return System.lineSeparator();
    }


    /**
     * 获取 Windows 主板序列号
     *
     * @return String - 计算机主板序列号
     */
    private static String getWindowsMainBoardSerialNumber() {
        StringBuilder result = new StringBuilder();
        try {
            File file = File.createTempFile("realhowto", ".vbs");
            file.deleteOnExit();
            try (FileWriter fw = new FileWriter(file)) {

                String vbs = "Set objWMIService = GetObject(\"winmgmts:\\\\.\\root\\cimv2\")\n"
                        + "Set colItems = objWMIService.ExecQuery _ \n" + "   (\"Select * from Win32_BaseBoard\") \n"
                        + "For Each objItem in colItems \n" + "    Wscript.Echo objItem.SerialNumber \n"
                        + "    exit for  ' do the first cpu only! \n" + "Next \n";

                fw.write(vbs);
            }
            Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
            try (BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line;
                while ((line = input.readLine()) != null) {
                    result.append(line);
                }
            }
        } catch (Exception e) {
            log.error("获取 Windows 主板信息错误", e);
        }
        return result.toString().trim();
    }

    /**
     * 获取 Linux 主板序列号
     *
     * @return String - 计算机主板序列号
     */
    private static String getLinuxMainBoardSerialNumber() {
        String result = "";
        String maniBord_cmd = "dmidecode | grep 'Serial Number' | awk '{print $3}' | tail -1";
        Process p;
        try {
            // 管道
            p = Runtime.getRuntime().exec(new String[]{"sh", "-c", maniBord_cmd});
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                result += line;
                break;
            }
            br.close();
        } catch (IOException e) {
            log.error("获取 Linux 主板信息错误", e);
        }
        return result;
    }

    /**
     * 从字节获取 MAC
     *
     * @param bytes - 字节
     * @return String - MAC
     */
    private static String getMacFromBytes(byte[] bytes) {
        StringBuffer mac = new StringBuffer();
        byte currentByte;
        boolean first = false;
        for (byte b : bytes) {
            if (first) {
                mac.append("-");
            }
            currentByte = (byte) ((b & 240) >> 4);
            mac.append(Integer.toHexString(currentByte));
            currentByte = (byte) (b & 15);
            mac.append(Integer.toHexString(currentByte));
            first = true;
        }
        return mac.toString().toUpperCase();
    }

    /**
     * 获取 Windows 网卡的 MAC 地址
     *
     * @return String - MAC 地址
     */
    private static String getWindowsMacAddress() {
        InetAddress ip = null;
        NetworkInterface ni = null;
        List<String> macList = new ArrayList<String>();
        try {
            Enumeration<NetworkInterface> netInterfaces = (Enumeration<NetworkInterface>) NetworkInterface
                    .getNetworkInterfaces();
            while (netInterfaces.hasMoreElements()) {
                ni = (NetworkInterface) netInterfaces.nextElement();
                //  遍历所有 IP 特定情况，可以考虑用 ni.getName() 判断
                Enumeration<InetAddress> ips = ni.getInetAddresses();
                while (ips.hasMoreElements()) {
                    ip = (InetAddress) ips.nextElement();
                    // 非127.0.0.1
                    if (!ip.isLoopbackAddress() && ip.getHostAddress().matches("(\\d{1,3}\\.){3}\\d{1,3}")) {
                        macList.add(getMacFromBytes(ni.getHardwareAddress()));
                    }
                }
            }
        } catch (Exception e) {
            log.error("获取 Windows MAC 错误", e);
        }
        if (macList.size() > 0) {
            return macList.get(0);
        } else {
            return "";
        }
    }

    /**
     * 获取 Linux 网卡的 MAC 地址 （如果 Linux 下有 eth0 这个网卡）
     *
     * @return String - MAC 地址
     */
    private static String getLinuxMacAddressForEth0() {
        String mac = null;
        BufferedReader bufferedReader = null;
        Process process = null;
        try {
            // Linux下的命令，一般取eth0作为本地主网卡
            process = Runtime.getRuntime().exec("ifconfig eth0");
            // 显示信息中包含有 MAC 地址信息
            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            int index = -1;
            while ((line = bufferedReader.readLine()) != null) {
                // 寻找标示字符串[hwaddr]
                index = line.toLowerCase().indexOf("hwaddr");
                if (index >= 0) {
                    // // 找到并取出 MAC 地址并去除2边空格
                    mac = line.substring(index + "hwaddr".length() + 1).trim();
                    break;
                }
            }
        } catch (IOException e) {
            log.error("获取 Linux MAC 信息错误", e);
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e1) {
                log.error("获取 Linux MAC 信息错误", e1);
            }
            bufferedReader = null;
            process = null;
        }
        return mac;
    }

    /**
     * 获取 Linux 网卡的 MAC 地址
     *
     * @return String - MAC 地址
     */
    private static String getLinuxMacAddress() {
        String mac = null;
        BufferedReader bufferedReader = null;
        Process process = null;
        try {
            // Linux下的命令 显示或设置网络设备
            process = Runtime.getRuntime().exec("ifconfig");
            // 显示信息中包含有 MAC 地址信息
            bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = null;
            int index = -1;
            while ((line = bufferedReader.readLine()) != null) {
                Pattern pat = Pattern.compile(REGEX);
                Matcher mat = pat.matcher(line);
                if (mat.find()) {
                    mac = mat.group(0);
                }
            }
        } catch (IOException e) {
            log.error("获取 Linux MAC 信息错误", e);
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e1) {
                log.error("获取 Linux MAC 信息错误", e1);
            }
            bufferedReader = null;
            process = null;
        }
        return mac;
    }

    /**
     * 获取 Windows 的 CPU 序列号
     *
     * @return String - CPU 序列号
     */
    private static String getWindowsProcessorSerialNumber() {
        StringBuilder result = new StringBuilder();
        try {
            File file = File.createTempFile("tmp", ".vbs");
            file.deleteOnExit();
            FileWriter fw = new java.io.FileWriter(file);
            String vbs = "Set objWMIService = GetObject(\"winmgmts:\\\\.\\root\\cimv2\")\n"
                    + "Set colItems = objWMIService.ExecQuery _ \n" + "   (\"Select * from Win32_Processor\") \n"
                    + "For Each objItem in colItems \n" + "    Wscript.Echo objItem.ProcessorId \n"
                    + "    exit for  ' do the first cpu only! \n" + "Next \n";

            fw.write(vbs);
            fw.close();
            Process p = Runtime.getRuntime().exec("cscript //NoLogo " + file.getPath());
            try (BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line;
                while ((line = input.readLine()) != null) {
                    result.append(line);
                }
            }
            file.delete();
        } catch (Exception e) {
            log.error("获取 Windows CPU 信息错误", e);
        }
        return result.toString().trim();
    }

    /**
     * 获取 Linux 的 CPU 序列号
     *
     * @return String - CPU 序列号
     */
    private static String getLinuxProcessorSerialNumber() {
        String result = "";
        String cmd = "dmidecode";
        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec(new String[]{"sh", "-c", cmd}).getInputStream()))) {
            String line = null;
            int index = -1;
            while ((line = bufferedReader.readLine()) != null) {
                index = line.toLowerCase().indexOf("uuid");
                if (index >= 0) {
                    result = line.substring(index + "uuid".length() + 1).trim();
                    break;
                }
            }
        } catch (IOException e) {
            log.error("获取 Linux CPU 信息错误", e);
        }
        return result.trim();
    }

    /**
     * 获取当前计算机操作系统名称 例如:windows,Linux,Unix等.
     *
     * @return String - 计算机操作系统名称
     * @author XinLau
     */
    public static String getOsName() {
        return System.getProperty("os.name").toLowerCase();
    }

    /**
     * 获取当前计算机操作系统名称前缀 例如:windows,Linux,Unix等.
     *
     * @return String - 计算机操作系统名称
     */
    public static String getOsNamePrefix() {
        String name = getOsName();
        if (name.startsWith(WINDOWS)) {
            return WINDOWS;
        } else if (name.startsWith(LINUX)) {
            return LINUX;
        } else if (name.startsWith(UNIX)) {
            return UNIX;
        } else {
            return CommonConstant.EMPTY;
        }
    }

    /**
     * 获取当前计算机主板序列号
     *
     * @return String - 计算机主板序列号
     */
    public static String getMainBoardSerialNumber() {
        switch (getOsNamePrefix()) {
            case WINDOWS:
                return getWindowsMainBoardSerialNumber();
            case LINUX:
                return getLinuxMainBoardSerialNumber();
            default:
                return CommonConstant.EMPTY;
        }
    }

    /**
     * 获取当前计算机网卡的 MAC 地址
     *
     * @return String - 网卡的 MAC 地址
     */
    public static String getMacAddress() {
        switch (getOsNamePrefix()) {
            case WINDOWS:
                return getWindowsMacAddress();
            case LINUX:
                String macAddressForEth0 = getLinuxMacAddressForEth0();
                if (StringUtils.isEmpty(macAddressForEth0)) {
                    macAddressForEth0 = getLinuxMacAddress();
                }
                return macAddressForEth0;
            default:
                return CommonConstant.EMPTY;
        }
    }

    /**
     * 获取当前计算机的 CPU 序列号
     *
     * @return String - CPU 序列号
     */
    public static String getCpuSerialNumber() {
        switch (getOsNamePrefix()) {
            case WINDOWS:
                return getWindowsProcessorSerialNumber();
            case LINUX:
                return getLinuxProcessorSerialNumber();
            default:
                return CommonConstant.EMPTY;
        }
    }

    /**
     * 获取当前计算机的 硬盘 序列号
     *
     * @return String - 硬盘 序列号
     */
    public static String getDiskSerialNumber() {
        switch (getOsNamePrefix()) {
            case WINDOWS:
                return getWindowsDiskSerialNumber();
            case LINUX:
                return getLinuxDiskSerialNumber();
            default:
                return CommonConstant.EMPTY;
        }
    }

    /**
     * 获取当前计算机的 硬盘 序列号
     *
     * @return String - 硬盘 序列号
     */
    private static String getLinuxDiskSerialNumber() {
        String cmd = "fdisk -l";
        String record = "Disk identifier";
        String symbol = ":";
        String execResult = executeLinuxCmd(cmd);
        String[] infos = execResult.split("\n");

        for (String info : infos) {
            info = info.trim();
            if (info.contains(record)) {
                info = info.replace(" ", "");
                String[] sn = info.split(symbol);
                return sn[1];
            }
        }
        return null;
    }

    /**
     * cmd
     *
     * @param cmd cmd
     * @return result
     */
    public static String executeLinuxCmd(String cmd) {
        try {
            Runtime run = Runtime.getRuntime();
            Process process;
            process = run.exec(cmd);
            StringBuffer out;
            try (InputStream in = process.getInputStream()) {
                BufferedReader bs = new BufferedReader(new InputStreamReader(in));
                out = new StringBuffer();
                byte[] b = new byte[8192];
                for (int n; (n = in.read(b)) != -1; ) {
                    out.append(new String(b, 0, n));
                }

            }
            process.destroy();
            return out.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取当前计算机的 硬盘 序列号
     *
     * @return String - 硬盘 序列号
     */
    private static String getWindowsDiskSerialNumber() {
        File[] fs = File.listRoots();
        Set<String> rs = new HashSet<>();
        for (File f : fs) {
            String number = getWindowsDiskSerialNumber(f.getName());
            rs.add(number);
        }

        return Joiner.on("-").join(rs);
    }

    /**
     * 获取当前计算机的 硬盘 序列号
     *
     * @param driver 磁盘
     * @return String - 硬盘 序列号
     */
    private static String getWindowsDiskSerialNumber(String driver) {

        String result = "";
        try {
            File file = File.createTempFile("damn", ".vbs");
            file.deleteOnExit();
            FileWriter fw = new java.io.FileWriter(file);
            String vbs = "Set objFSO = CreateObject(\"Scripting.FileSystemObject\")\n"
                    + "Set colDrives = objFSO.Drives\n"
                    + "Set objDrive = colDrives.item(\""
                    + driver
                    + "\")\n"
                    + "Wscript.Echo objDrive.SerialNumber"; // see note
            fw.write(vbs);
            fw.close();
            Process p = Runtime.getRuntime().exec(
                    "cscript //NoLogo " + file.getPath());
            BufferedReader input = new BufferedReader(new InputStreamReader(
                    p.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                result += line;

            }
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.trim();
    }

    /**
     * 获取计算机唯一标识
     *
     * @return ComputerUniqueIdentification - 计算机唯一标识
     */
    public static ComputerUniqueIdentification getComputerUniqueIdentification() {
        return new ComputerUniqueIdentification(getOsNamePrefix(), getMainBoardSerialNumber(), getMacAddress(), getCpuSerialNumber());
    }

    /**
     * 获取计算机唯一标识
     *
     * @return String - 计算机唯一标识
     */
    public static String getComputerUniqueIdentificationString() {
        return getComputerUniqueIdentification().toString();
    }

    /**
     * 默认编码
     *
     * @return 默认编码
     */
    public static Charset defaultCharset() {
        return isWindow() ? Charset.forName("GBK") : StandardCharsets.UTF_8;
    }

    /**
     * 加载动态库
     *
     * @param pattern 动态库文件
     */
    public static void loaded(String pattern) {
        loaded(pattern, false);
    }

    /**
     * 加载动态库
     *
     * @param pattern 动态库文件
     * @param force   是否强制更新
     */
    public static void loaded(String pattern, boolean force) {
        Set<Resource> resources = ResourceProvider.of(pattern).getResources();

        for (Resource resolverResource : resources) {
            String filename = FileUtils.getName(resolverResource.getUrl().toExternalForm());
            String[] split = System.getProperty("java.library.path").split(";");
            root:
            for (String s : split) {
                try (InputStream inputStream = resolverResource.openStream()) {
                    File file = new File(s, filename);
                    if (file.exists()) {
                        if (force) {
                            try {
                                FileUtils.delete(file);
                                file = new File(s, filename);
                            } catch (IOException ignored) {
                            }
                        }
                        if ((file.length() == 0 || file.length() != inputStream.available())) {
                            try {
                                FileUtils.delete(file);
                                file = new File(s, filename);
                            } catch (IOException ignored) {
                            }
                        }

                        break root;
                    }

                    try (FileOutputStream fos = new FileOutputStream(file)) {
                        IoUtils.copy(inputStream, fos);
                        break root;
                    } catch (IOException ignored) {
                        //
                    }
                } catch (IOException ignored) {
                }
            }
        }
    }

    /**
     * 安装paddle环境
     */
    public static void installPaddlePaddle() {
        String[] split = System.getProperty("java.library.path").split(";");
        if (isWindow()) {
            installWinEnv(split);
            return;
        }

        installLinuxEnv(split);

    }

    private static void installLinuxEnv(String[] split) {
        String linuxPath = "linux-x86-64/paddle.tar.xz";

    }

    /**
     * window环境
     *
     * @param path 路径
     */
    private static void installWinEnv(String[] path) {
        String djlCacheDir = System.getProperty("DJL_CACHE_DIR");
        if (StringUtils.isEmpty(djlCacheDir)) {
            installLocalWindowEnv(path);
            return;
        }

        installEnvWindowEnv(djlCacheDir, path);
    }

    /**
     * 安装下载文件到本地环境
     *
     * @param path 安装目录
     */
    private static void installEnvWindowEnv(String djlCacheDir, String[] path) {
        File paddle = new File(djlCacheDir, "paddle");
        if (!paddle.exists()) {
            log.warn("{}不存在, 先运行代码后在执行", paddle.getAbsolutePath());
            return;
        }

//        Class.forName("ai.djl.mxnet.jna.LibUtils")

    }

    /**
     * 安装本地环境
     *
     * @param path 安装目录
     */
    private static void installLocalWindowEnv(String[] path) {
        String winPath = "win-x86-64/paddle.tar.xz";
        Metadata metadata = Repository.of("classpath:").first(winPath);

        try (TarInputStream inputStream = new TarInputStream(new XZInputStream(metadata.openInputStream()))) {
            TarEntry tarEntry;
            while ((tarEntry = inputStream.getNextEntry()) != null) {
                installLink(inputStream, tarEntry, path);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void installLink(TarInputStream tis, TarEntry tarEntry, String[] path) {
        if (tarEntry.isDirectory()) {
            return;
        }

        String name = tarEntry.getName();
        if (existLink(name, path, tarEntry)) {
            return;
        }
        try (ProgressBar consoleProgressBar = new ProgressBar(tarEntry.getName() + "安装进度: ", tarEntry.getSize(), ProgressStyle.SIZE)) {
            String[] strings = ArrayUtils.copyRange(path, 3);
            for (String s : strings) {
                File temp = new File(s, name);
                int count;
                byte[] data = new byte[4096];
                try (FileOutputStream fos = new FileOutputStream(temp)) {
                    while ((count = tis.read(data)) != -1) {
                        fos.write(data, 0, count);
                        consoleProgressBar.stepBy(count);
                    }
                    fos.flush();
                    break;
                } catch (Exception ignored) {
                }
            }
        }
    }

    private static boolean existLink(String name, String[] path, TarEntry tarEntry) {
        for (String s : path) {
            File temp = new File(s, name);
            if (temp.exists() && tarEntry.getSize() == temp.length()) {
                return true;
            }
        }
        return false;
    }
    /**
     * 安装依赖
     *
     * @param name       动态库名称
     * @param dependency 依赖
     */
    public static void installDependency(String name, Dependency dependency) {
        installDependency("", name, dependency);
    }
    /**
     * 安装依赖
     *
     * @param path       路径
     * @param name       动态库名称
     * @param dependency 依赖
     */
    public static void installDependency(String path, String name, Dependency dependency) {
        if (checkDependency(name, dependency)) {
            return;
        }
        Repository repository = Repository.current().add(Repository.classpath(true));
        String arch = getArch();
        String name1 = (StringUtils.isNullOrEmpty(path) ? "" : StringUtils.endWithAppend(path, "/")) +
                (StringUtils.isNullOrEmpty(arch) ? dependency.getSystem() : dependency.getSystem() + dependency.getSub() + arch) +
                "/" +
                name;
        Repository resolve = repository.resolve(name1 + dependency.getSuffix());

        if (!resolve.isEmpty()) {
            resolve.transferTo(System.getProperty("java.library.path"), false);
            registerSign(name, dependency);
            return;
        }

        Repository resolveZip = repository.resolve(name1 + ".*");
        if (!resolveZip.isEmpty()) {
            resolveZip.transferTo(System.getProperty("java.library.path"), false);
            registerSign(name, dependency);
            return;
        }


    }

    private static void registerSign(String name, Dependency dependency) {
        name = name + dependency.getSuffix() + ".link";
        for (String s : System.getProperty("java.library.path").split(";")) {
            if (!new File(s, name).exists()) {
                try {
                    FileUtils.write("", new File(s, name));
                } catch (IOException e) {
                    continue;
                }
            }
            break;
        }
    }

    private static boolean checkDependency(String name, Dependency dependency) {
        name = name + dependency.getSuffix();
        for (String s : System.getProperty("java.library.path").split(";")) {
            if (new File(s, name).exists()) {
                return true;
            }
        }
        return false;
    }

    private static String getArch() {
        String arch = System.getProperty("os.arch");
        if (isWindow()) {
            return arch.endsWith("64") ? "x86_64" : "x86";
        }

        if (isLinux()) {
            if (arch.contains("ppc64le")) {
                return "ppc64le";

            }
            if (arch.contains("armhf")) {
                return "armhf";
            }
            return arch.endsWith("64") ? "x86_64" : "x86";
        }

        return "";
    }


    @Data
    @Builder
    public static class Dependency {
        /**
         * window环境目录
         */
        @Builder.Default
        private String window = "windows";
        /**
         * linux 环境目录
         */
        @Builder.Default
        private String linux = "linux";
        /**
         * 下级目录
         */
        @Builder.Default
        private String sub = "/";


        public String getSystem() {
            if (isLinux()) {
                return linux;
            }

            return window;
        }

        public String getSuffix() {
            if (isLinux()) {
                return ".so";
            }

            return ".dll";
        }
    }
}
