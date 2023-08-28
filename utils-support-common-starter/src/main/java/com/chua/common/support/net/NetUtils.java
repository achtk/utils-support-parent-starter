package com.chua.common.support.net;

import com.chua.common.support.converter.Converter;
import com.chua.common.support.utils.CollectionUtils;
import com.chua.common.support.utils.IoUtils;
import com.chua.common.support.utils.NumberUtils;
import com.chua.common.support.utils.StringUtils;
import lombok.SneakyThrows;

import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.Query;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;

import static com.chua.common.support.constant.CommonConstant.*;
import static java.util.Collections.emptyList;


/**
 * 网络工具
 *
 * @author CH
 * @version 1.0.0
 * @since 2021/1/7
 */
public class NetUtils {
    /**
     * returned port range is [30000, 39999]
     */
    private static final int RND_PORT_START = 30000;
    private static final int RND_PORT_RANGE = 10000;

    /**
     * valid port range is (0, 65535]
     */
    private static final int MIN_PORT = 1;
    private static final int MAX_PORT = 65535;

    private static final Pattern ADDRESS_PATTERN = Pattern.compile("^\\d{1,3}(\\.\\d{1,3}){3}\\:\\d{1,5}$");
    private static final Pattern LOCAL_IP_PATTERN = Pattern.compile("127(\\.\\d{1,3}){3}$");
    private static final Pattern IP_PATTERN = Pattern.compile("\\d{1,3}(\\.\\d{1,3}){3,5}$");

    private static final Map<String, String> HOST_NAME_CACHE = new ConcurrentHashMap<>(1000);
    private static volatile InetAddress LOCAL_ADDRESS = null;
    private static volatile Inet6Address LOCAL_ADDRESS_V6 = null;

    private static final String SPLIT_IPV4_CHARACTER = "\\.";
    private static final String SPLIT_IPV6_CHARACTER = ":";

    /**
     * store the used port.
     * the set used only on the synchronized method.
     */
    private static final BitSet USED_PORT = new BitSet(65536);

    public static final String LOCAL_HOST = "127.0.0.1";
    /**
     * 任意地址
     */
    public static final String ANY_HOST = "0.0.0.0";
    /**
     * IPv4地址
     */
    public static final Pattern IPV4_PATTERN = Pattern
            .compile(
                    "^(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)(\\.(25[0-5]|2[0-4]\\d|[0-1]?\\d?\\d)){3}$");
    private static final String HTTP = "http:
    private static final String HTTPS = "https:

    /**
     * 连接转字符串
     *
     * @param local  本地地址
     * @param remote 远程地址
     * @return 地址信息字符串
     */
    public static String connectToString(InetSocketAddress local, InetSocketAddress remote) {
        return toAddressString(local) + " <-> " + toAddressString(remote);
    }

    /**
     * 获取地址
     *
     * @param newUrl 连接
     * @return 地址
     */
    public static String getAddress(String newUrl) {
        return NetAddress.of(newUrl).getAddress();
    }

    /**
     * 检查当前指定端口是否可用，不可用则自动+1再试（随机端口从默认端口开始检查）
     *
     * @param host    当前ip地址
     * @param port    当前指定端口
     * @param maxPort 最大端口
     * @return 从指定端口开始后第一个可用的端口
     */
    public static int getAvailablePort(String host, int port, int maxPort) {
        if (isAnyHost(host) || isLocalHost(host) || isHostInNetworkCard(host)) {
            if (port < MIN_PORT) {
                port = MIN_PORT;
            }
            for (int i = port; i <= maxPort; i++) {
                ServerSocket ss = null;
                try {
                    ss = new ServerSocket();
                    ss.bind(new InetSocketAddress(host, i));
                    return i;
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    IoUtils.closeQuietly(ss);
                }
            }
        }
        return -1;
    }

    /**
     * 获取hash
     *
     * @param address 地址
     * @return
     */
    public static String getHash(String address) {
        return null;
    }

    /**
     * 获取IP
     * <p>
     * 127.0.0.1:8080 -> 127.0.0.1
     * </p>
     *
     * @param address 地址
     * @return 地址
     */
    public static String getHost(String address) {
        return NetAddress.of(address).getHost();
    }

    /**
     * 遍历本地网卡，返回第一个合理的IP，保存到缓存中
     *
     * @return 本地网卡IP
     */
    public static InetAddress getLocalAddress01() {
        InetAddress localAddress = null;
        try {
            localAddress = InetAddress.getLocalHost();
            if (isValidAddress(localAddress)) {
                return localAddress;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            if (interfaces != null) {
                while (interfaces.hasMoreElements()) {
                    try {
                        NetworkInterface network = interfaces.nextElement();
                        Enumeration<InetAddress> addresses = network.getInetAddresses();
                        while (addresses.hasMoreElements()) {
                            try {
                                InetAddress address = addresses.nextElement();
                                if (isValidAddress(address)) {
                                    return address;
                                }
                            } catch (Throwable e) {
                            }
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return localAddress;
    }

    /**
     * 得到本机IPv4地址
     *
     * @return ip地址
     */
    public static String getLocalIpv4() {
        InetAddress address = getLocalAddress();
        return address == null ? null : address.getHostAddress();
    }

    /**
     * 获取tomcat 端口号
     *
     * @return tomcat 端口号
     */
    public static Integer getPort() {
        MBeanServer beanServer = ManagementFactory.getPlatformMBeanServer();
        Set<ObjectName> objectNames = null;
        try {
            objectNames = beanServer.queryNames(new ObjectName("*:type=Connector,*"),
                    Query.match(Query.attr("protocol"), Query.value("HTTP/1.1")));
        } catch (MalformedObjectNameException ignored) {
            return null;
        }
        try {
            return Converter.convertIfNecessary(objectNames.iterator().next().getKeyProperty("port"), Integer.class);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * pid
     *
     * @return pid
     */
    public static String getPid() {
        String processId = ManagementFactory.getRuntimeMXBean().getName();
        return processId.substring(0, processId.indexOf("@"));
    }

    /**
     * 获取所有网卡
     *
     * @return 网卡
     */
    @SneakyThrows
    public static List<InetAddress> getNetworkInterfaces() {
        List<InetAddress> result = new ArrayList<>();

        int lowest = Integer.MAX_VALUE;
        for (Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces(); interfaces.hasMoreElements(); ) {
            NetworkInterface ifc = interfaces.nextElement();
            if (ifc.isUp()) {
                if (ifc.getIndex() < lowest || result == null) {
                    lowest = ifc.getIndex();
                }

                for (Enumeration<InetAddress> inetAddresses = ifc.getInetAddresses(); inetAddresses.hasMoreElements(); ) {
                    InetAddress address = inetAddresses.nextElement();
                    if (address instanceof Inet4Address && !address.isLoopbackAddress() && address.isSiteLocalAddress()) {
                        result.add(address);
                    }
                }
            }
        }
        return result;
    }

    /**
     * 获取端口
     * <p>
     * 127.0.0.1:8080 -> 8080
     * </p>
     *
     * @param address 地址
     * @return 端口
     */
    public static int getPort(String address) {
        return NetAddress.of(address).getPort();
    }

    /**
     * 获取protocol
     *
     * @param address 地址
     * @return 协议
     */
    public static String getProtocol(String address) {
        return NetAddress.of(address).getProtocol();
    }

    /**
     * 获取路径
     *
     * @param address 地址
     * @return 路径
     */
    public static String getQuery(String address) {
        return NetAddress.of(address).getQuery();
    }

    /**
     * 连通性检测
     *
     * @param host 地址
     * @return 连通返回true
     */
    public static boolean isConnectable(String host) {
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(getHost(host), getPort(host)));
        } catch (IOException e) {
            return false;
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
        return true;
    }

    /**
     * 连通性检测
     *
     * @param ip   地址
     * @param port 端口
     * @return 连通返回true
     */
    public static boolean isConnectable(String ip, int port) {
        Socket socket = new Socket();
        try {
            socket.connect(new InetSocketAddress(ip, port));
        } catch (IOException e) {
            return false;
        } finally {
            try {
                socket.close();
            } catch (IOException ignored) {
            }
        }
        return true;
    }

    /**
     * 是否网卡上的地址
     *
     * @param host 地址
     * @return 是否默认地址
     */
    public static boolean isHostInNetworkCard(String host) {
        try {
            InetAddress addr = InetAddress.getByName(host);
            return NetworkInterface.getByInetAddress(addr) != null;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断端口是否有效 0-65535
     *
     * @param port 端口
     * @return 是否有效
     */
    public static boolean isInvalidPort(int port) {
        return port > MAX_PORT || port < MIN_PORT;
    }

    /**
     * 是否IPv4地址 0.0.0.0
     *
     * @param host 地址
     * @return 是否默认地址
     */
    public static boolean isIpv4Host(String host) {
        return !StringUtils.isNullOrEmpty(host) && IPV4_PATTERN.matcher(host).matches();
    }

    /**
     * 去除重复 /
     *
     * @param uri uri
     * @return 去除重复 /
     */
    public static String removeDuplicateSymbols(String uri) {
        if (null == uri) {
            return SYMBOL_EMPTY;
        }
        int index = uri.indexOf(SYMBOL_COLON);
        if (index != -1) {
            uri = uri.substring(index + 1);
        }
        uri = uri.replace("\\", SYMBOL_LEFT_SLASH).replaceAll("
        if (uri.startsWith(SYMBOL_LEFT_SLASH)) {
            uri = uri.substring(1);
        }
        if (uri.startsWith(SYMBOL_LEFT_SLASH)) {
            uri = uri.substring(1);
        }

        if (uri.startsWith(HTTPS)) {
            return uri;
        }
        return uri.startsWith(HTTP) ? uri : HTTP + uri;
    }

    /**
     * Address转 host:port 字符串
     *
     * @param address InetSocketAddress转
     * @return host:port 字符串
     */
    public static String toAddressString(final InetSocketAddress address) {
        if (address == null) {
            return "";
        } else {
            return toIpString(address) + ":" + address.getPort();
        }
    }

    /**
     * 得到ip地址
     *
     * @param address Address
     * @return ip地址
     */
    public static String toIpString(final InetSocketAddress address) {
        if (address == null) {
            return null;
        } else {
            InetAddress inetAddress = address.getAddress();
            return inetAddress == null ? address.getHostName() : inetAddress.getHostAddress();
        }
    }


    /**
     * 是否合法地址（非本地，非默认的IPv4地址）
     *
     * @param address address
     * @return 是否合法
     */
    private static boolean isValidAddress(InetAddress address) {
        if (address == null || address.isLoopbackAddress()) {
            return false;
        }
        String name = address.getHostAddress();
        return (name != null
                && !isAnyHost(name)
                && !isLocalHost(name)
                && isIpv4Host(name));
    }

    /**
     * 通过连接远程地址得到本机内网地址
     *
     * @param remoteAddress 远程地址
     * @return 本机内网地址
     */
    private static InetAddress getLocalHostBySocket(InetSocketAddress remoteAddress) {
        InetAddress host = null;
        try {
            
            Socket socket = new Socket();
            try {
                socket.connect(remoteAddress, 1000);
                
                host = socket.getLocalAddress();
            } finally {
                IoUtils.closeQuietly(socket);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return host;
    }

    /**
     * 是否连通
     *
     * @param host ip
     * @param port 端口
     * @return 是否连通
     */
    public static boolean ping(String host, Integer port) {
        try (Socket socket = new Socket();) {
            socket.connect(new InetSocketAddress(host, port));
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static int getRandomPort() {
        return RND_PORT_START + ThreadLocalRandom.current().nextInt(RND_PORT_RANGE);
    }

    public static synchronized int getAvailablePort() {
        int randomPort = getRandomPort();
        return getAvailablePort(randomPort);
    }

    public static synchronized int getAvailablePort(int port) {
        if (port < MIN_PORT) {
            return MIN_PORT;
        }

        for (int i = port; i < MAX_PORT; i++) {
            if (USED_PORT.get(i)) {
                continue;
            }
            try (ServerSocket ignored = new ServerSocket(i)) {
                USED_PORT.set(i);
                port = i;
                break;
            } catch (IOException e) {
                
            }
        }
        return port;
    }


    /**
     * Check the port whether is in use in os
     *
     * @param port port to check
     * @return true if it's occupied
     */
    public static boolean isPortInUsed(int port) {
        try (ServerSocket ignored = new ServerSocket(port)) {
            return false;
        } catch (IOException e) {
            
        }
        return true;
    }


    /**
     * Tells whether the address to test is an invalid address.
     *
     * @param address address to test
     * @return true if invalid
     * @implNote Pattern matching only.
     */
    public static boolean isValidAddress(String address) {
        return ADDRESS_PATTERN.matcher(address).matches();
    }

    public static boolean isLocalHost(String host) {
        return host != null
                && (LOCAL_IP_PATTERN.matcher(host).matches()
                || host.equalsIgnoreCase(LOCAL_HOST));
    }

    public static boolean isAnyHost(String host) {
        return ANY_HOST.equals(host);
    }

    public static boolean isInvalidLocalHost(String host) {
        return host == null
                || host.length() == 0
                || host.equalsIgnoreCase(LOCAL_HOST)
                || host.equals(ANY_HOST)
                || host.startsWith("127.");
    }

    public static boolean isValidLocalHost(String host) {
        return !isInvalidLocalHost(host);
    }

    public static InetSocketAddress getLocalSocketAddress(String host, int port) {
        return isInvalidLocalHost(host) ?
                new InetSocketAddress(port) : new InetSocketAddress(host, port);
    }

    static boolean isValidV4Address(InetAddress address) {
        if (address == null || address.isLoopbackAddress()) {
            return false;
        }

        String name = address.getHostAddress();
        return (name != null
                && IP_PATTERN.matcher(name).matches()
                && !ANY_HOST.equals(name)
                && !LOCAL_HOST.equals(name));
    }

    /**
     * Check if an ipv6 address
     *
     * @return true if it is reachable
     */
    static boolean isPreferIpV6Address() {
        return Boolean.getBoolean("java.net.preferIPv6Addresses");
    }

    /**
     * normalize the ipv6 Address, convert scope name to scope id.
     * e.g.
     * convert
     * fe80:0:0:0:894:aeec:f37d:23e1%en0
     * to
     * fe80:0:0:0:894:aeec:f37d:23e1%5
     * <p>
     * The %5 after ipv6 address is called scope id.
     * see java doc of {@link Inet6Address} for more details.
     *
     * @param address the input address
     * @return the normalized address, with scope id converted to int
     */
    static InetAddress normalizeV6Address(Inet6Address address) {
        String addr = address.getHostAddress();
        int i = addr.lastIndexOf('%');
        if (i > 0) {
            try {
                return InetAddress.getByName(addr.substring(0, i) + '%' + address.getScopeId());
            } catch (UnknownHostException ignored) {
            }
        }
        return address;
    }

    private static volatile String HOST_ADDRESS;

    private static volatile String HOST_ADDRESS_V6;

    public static String getLocalHost() {
        if (HOST_ADDRESS != null) {
            return HOST_ADDRESS;
        }

        InetAddress address = getLocalAddress();
        if (address != null) {
            if (address instanceof Inet6Address) {
                String ipv6AddressString = address.getHostAddress();
                if (ipv6AddressString.contains("%")) {
                    ipv6AddressString = ipv6AddressString.substring(0, ipv6AddressString.indexOf("%"));
                }
                HOST_ADDRESS = ipv6AddressString;
                return HOST_ADDRESS;
            }

            HOST_ADDRESS = address.getHostAddress();
            return HOST_ADDRESS;
        }

        return LOCAL_HOST;
    }

    public static String getLocalHostV6() {
        if (!StringUtils.isNullOrEmpty(HOST_ADDRESS_V6)) {
            return HOST_ADDRESS_V6;
        }
        
        if ("".equals(HOST_ADDRESS_V6)) {
            return null;
        }

        Inet6Address address = getLocalAddressV6();
        if (address != null) {
            String ipv6AddressString = address.getHostAddress();
            if (ipv6AddressString.contains("%")) {
                ipv6AddressString = ipv6AddressString.substring(0, ipv6AddressString.indexOf("%"));
            }

            HOST_ADDRESS_V6 = ipv6AddressString;
            return HOST_ADDRESS_V6;
        }
        HOST_ADDRESS_V6 = "";
        return null;
    }


    /**
     * Find first valid IP from local network card
     *
     * @return first valid local IP
     */
    public static InetAddress getLocalAddress() {
        if (LOCAL_ADDRESS != null) {
            return LOCAL_ADDRESS;
        }
        InetAddress localAddress = getLocalAddress0();
        LOCAL_ADDRESS = localAddress;
        return localAddress;
    }

    public static Inet6Address getLocalAddressV6() {
        if (LOCAL_ADDRESS_V6 != null) {
            return LOCAL_ADDRESS_V6;
        }
        Inet6Address localAddress = getLocalAddress0V6();
        LOCAL_ADDRESS_V6 = localAddress;
        return localAddress;
    }

    private static Optional<InetAddress> toValidAddress(InetAddress address) {
        if (address instanceof Inet6Address) {
            Inet6Address v6Address = (Inet6Address) address;
            if (isPreferIpV6Address()) {
                return Optional.ofNullable(normalizeV6Address(v6Address));
            }
        }
        if (isValidV4Address(address)) {
            return Optional.of(address);
        }
        return Optional.empty();
    }

    private static InetAddress getLocalAddress0() {
        InetAddress localAddress = null;

        
        try {
            NetworkInterface networkInterface = findNetworkInterface();
            Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                Optional<InetAddress> addressOp = toValidAddress(addresses.nextElement());
                if (addressOp.isPresent()) {
                    try {
                        if (addressOp.get().isReachable(100)) {
                            return addressOp.get();
                        }
                    } catch (IOException e) {
                        
                    }
                }
            }
        } catch (Throwable ignored) {
        }

        try {
            localAddress = InetAddress.getLocalHost();
            Optional<InetAddress> addressOp = toValidAddress(localAddress);
            if (addressOp.isPresent()) {
                return addressOp.get();
            }
        } catch (Throwable ignored) {
        }

        localAddress = getLocalAddressV6();

        return localAddress;
    }

    private static Inet6Address getLocalAddress0V6() {
        
        try {
            NetworkInterface networkInterface = findNetworkInterface();
            Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
            while (addresses.hasMoreElements()) {
                InetAddress address = addresses.nextElement();
                if (address instanceof Inet6Address) {
                    if (!address.isLoopbackAddress()
                            && !address.isAnyLocalAddress()
                            && !address.isLinkLocalAddress()
                            && address.getHostAddress().contains(":")) {
                        return (Inet6Address) address;
                    }
                }
            }
        } catch (Throwable ignored) {
        }

        return null;
    }

    /**
     * Returns {@code true} if the specified {@link NetworkInterface} should be ignored with the given conditions.
     *
     * @param networkInterface the {@link NetworkInterface} to check
     * @return {@code true} if the specified {@link NetworkInterface} should be ignored, otherwise {@code false}
     * @throws SocketException SocketException if an I/O error occurs.
     */
    private static boolean ignoreNetworkInterface(NetworkInterface networkInterface) throws SocketException {
        return networkInterface == null
                || networkInterface.isLoopback()
                || networkInterface.isVirtual()
                || !networkInterface.isUp();
    }

    /**
     * Get the valid {@link NetworkInterface network interfaces}
     *
     * @return the valid {@link NetworkInterface}s
     * @throws SocketException SocketException if an I/O error occurs.
     * @since 2.7.6
     */
    private static List<NetworkInterface> getValidNetworkInterfaces() throws SocketException {
        List<NetworkInterface> validNetworkInterfaces = new LinkedList<>();
        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();
            if (ignoreNetworkInterface(networkInterface)) { 
                continue;
            }
            validNetworkInterfaces.add(networkInterface);
        }
        return validNetworkInterfaces;
    }


    /**
     * Get the suitable {@link NetworkInterface}
     *
     * @return If no {@link NetworkInterface} is available , return <code>null</code>
     * @since 2.7.6
     */
    public static NetworkInterface findNetworkInterface() {

        List<NetworkInterface> validNetworkInterfaces = emptyList();
        try {
            validNetworkInterfaces = getValidNetworkInterfaces();
        } catch (Throwable ignored) {
        }

        NetworkInterface result = null;

        if (result == null) { 
            for (NetworkInterface networkInterface : validNetworkInterfaces) {
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    Optional<InetAddress> addressOp = toValidAddress(addresses.nextElement());
                    if (addressOp.isPresent()) {
                        try {
                            if (addressOp.get().isReachable(100)) {
                                return networkInterface;
                            }
                        } catch (IOException e) {
                            
                        }
                    }
                }
            }
        }

        if (result == null) {
            result = CollectionUtils.findFirst(validNetworkInterfaces);
        }

        return result;
    }

    public static String getHostName(String address) {
        try {
            int i = address.indexOf(':');
            if (i > -1) {
                address = address.substring(0, i);
            }
            String hostname = HOST_NAME_CACHE.get(address);
            if (hostname != null && hostname.length() > 0) {
                return hostname;
            }
            InetAddress inetAddress = InetAddress.getByName(address);
            if (inetAddress != null) {
                hostname = inetAddress.getHostName();
                HOST_NAME_CACHE.put(address, hostname);
                return hostname;
            }
        } catch (Throwable e) {
            
        }
        return address;
    }

    public static String getLocalHostName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return getLocalAddress().getHostName();
        }
    }

    /**
     * @param hostName
     * @return ip address or hostName if UnknownHostException
     */
    public static String getIpByHost(String hostName) {
        try {
            return InetAddress.getByName(hostName).getHostAddress();
        } catch (UnknownHostException e) {
            return hostName;
        }
    }


    public static InetSocketAddress toAddress(String address) {
        int i = address.indexOf(':');
        String host;
        int port;
        if (i > -1) {
            host = address.substring(0, i);
            port = Integer.parseInt(address.substring(i + 1));
        } else {
            host = address;
            port = 0;
        }
        return new InetSocketAddress(host, port);
    }

    public static String toURL(String protocol, String host, int port, String path) {
        StringBuilder sb = new StringBuilder();
        sb.append(protocol).append(":
        sb.append(host).append(':').append(port);
        if (path.charAt(0) != '/') {
            sb.append('/');
        }
        sb.append(path);
        return sb.toString();
    }

    @SuppressWarnings("deprecation")
    public static void joinMulticastGroup(MulticastSocket multicastSocket, InetAddress multicastAddress) throws
            IOException {
        setInterface(multicastSocket, multicastAddress instanceof Inet6Address);

        
        multicastSocket.setLoopbackMode(false);
        multicastSocket.joinGroup(multicastAddress);
    }

    @SuppressWarnings("deprecation")
    public static void setInterface(MulticastSocket multicastSocket, boolean preferIpv6) throws IOException {
        boolean interfaceSet = false;
        for (NetworkInterface networkInterface : getValidNetworkInterfaces()) {
            Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();

            while (addresses.hasMoreElements()) {
                InetAddress address = addresses.nextElement();
                if (preferIpv6 && address instanceof Inet6Address) {
                    try {
                        if (address.isReachable(100)) {
                            multicastSocket.setInterface(address);
                            interfaceSet = true;
                            break;
                        }
                    } catch (IOException e) {
                        
                    }
                } else if (!preferIpv6 && address instanceof Inet4Address) {
                    try {
                        if (address.isReachable(100)) {
                            multicastSocket.setInterface(address);
                            interfaceSet = true;
                            break;
                        }
                    } catch (IOException e) {
                        
                    }
                }
            }
            if (interfaceSet) {
                break;
            }
        }
    }

    /**
     * @param pattern
     * @param host
     * @param port
     * @return
     * @throws UnknownHostException
     */
    public static boolean matchIpRange(String pattern, String host, int port) throws UnknownHostException {
        if (pattern == null || host == null) {
            throw new IllegalArgumentException("Illegal Argument pattern or hostName. Pattern:" + pattern + ", Host:" + host);
        }
        pattern = pattern.trim();
        if ("*.*.*.*".equals(pattern) || "*".equals(pattern)) {
            return true;
        }

        InetAddress inetAddress = InetAddress.getByName(host);
        boolean isIpv4 = isValidV4Address(inetAddress);
        String[] hostAndPort = getPatternHostAndPort(pattern, isIpv4);
        if (hostAndPort[1] != null && !hostAndPort[1].equals(String.valueOf(port))) {
            return false;
        }
        pattern = hostAndPort[0];

        String splitCharacter = SPLIT_IPV4_CHARACTER;
        if (!isIpv4) {
            splitCharacter = SPLIT_IPV6_CHARACTER;
        }
        String[] mask = pattern.split(splitCharacter);
        
        checkHostPattern(pattern, mask, isIpv4);

        host = inetAddress.getHostAddress();
        if (pattern.equals(host)) {
            return true;
        }

        
        if (!ipPatternContainExpression(pattern)) {
            InetAddress patternAddress = InetAddress.getByName(pattern);
            return patternAddress.getHostAddress().equals(host);
        }

        String[] ipAddress = host.split(splitCharacter);

        for (int i = 0; i < mask.length; i++) {
            if ("*".equals(mask[i]) || mask[i].equals(ipAddress[i])) {
                continue;
            } else if (mask[i].contains("-")) {
                String[] rangeNumStrs = StringUtils.split(mask[i], '-');
                if (rangeNumStrs.length != 2) {
                    throw new IllegalArgumentException("There is wrong format of ip Address: " + mask[i]);
                }
                Integer min = getNumOfIpSegment(rangeNumStrs[0], isIpv4);
                Integer max = getNumOfIpSegment(rangeNumStrs[1], isIpv4);
                Integer ip = getNumOfIpSegment(ipAddress[i], isIpv4);
                if (ip < min || ip > max) {
                    return false;
                }
            } else if ("0".equals(ipAddress[i]) && ("0".equals(mask[i]) || "00".equals(mask[i]) || "000".equals(mask[i]) || "0000".equals(mask[i]))) {
                continue;
            } else if (!mask[i].equals(ipAddress[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * is multicast address or not
     *
     * @param host ipv4 address
     * @return {@code true} if is multicast address
     */
    public static boolean isMulticastAddress(String host) {
        int i = host.indexOf('.');
        if (i > 0) {
            String prefix = host.substring(0, i);
            if (NumberUtils.isNumber(prefix)) {
                int p = Integer.parseInt(prefix);
                return p >= 224 && p <= 239;
            }
        }
        return false;
    }

    private static boolean ipPatternContainExpression(String pattern) {
        return pattern.contains("*") || pattern.contains("-");
    }

    private static void checkHostPattern(String pattern, String[] mask, boolean isIpv4) {
        if (!isIpv4) {
            if (mask.length != 8 && ipPatternContainExpression(pattern)) {
                throw new IllegalArgumentException("If you config ip expression that contains '*' or '-', please fill qualified ip pattern like 234e:0:4567:0:0:0:3d:*. ");
            }
            if (mask.length != 8 && !pattern.contains("::")) {
                throw new IllegalArgumentException("The host is ipv6, but the pattern is not ipv6 pattern : " + pattern);
            }
        } else {
            if (mask.length != 4) {
                throw new IllegalArgumentException("The host is ipv4, but the pattern is not ipv4 pattern : " + pattern);
            }
        }
    }

    private static String[] getPatternHostAndPort(String pattern, boolean isIpv4) {
        String[] result = new String[2];
        if (pattern.startsWith("[") && pattern.contains("]:")) {
            int end = pattern.indexOf("]:");
            result[0] = pattern.substring(1, end);
            result[1] = pattern.substring(end + 2);
            return result;
        } else if (pattern.startsWith("[") && pattern.endsWith("]")) {
            result[0] = pattern.substring(1, pattern.length() - 1);
            result[1] = null;
            return result;
        } else if (isIpv4 && pattern.contains(":")) {
            int end = pattern.indexOf(":");
            result[0] = pattern.substring(0, end);
            result[1] = pattern.substring(end + 1);
            return result;
        } else {
            result[0] = pattern;
            return result;
        }
    }

    private static Integer getNumOfIpSegment(String ipSegment, boolean isIpv4) {
        if (isIpv4) {
            return Integer.parseInt(ipSegment);
        }
        return Integer.parseInt(ipSegment, 16);
    }


    public static boolean isIpV6URLStdFormat(String ip) {
        if ((ip.charAt(0) == '[' && ip.indexOf(']') > 2)) {
            return true;
        } else if (ip.indexOf(":") != ip.lastIndexOf(":")) {
            return true;
        } else {
            return false;
        }
    }

    public static String getLegalIp(String ip) {
        
        int ind;
        if ((ip.charAt(0) == '[' && (ind = ip.indexOf(']')) > 2)) {
            String nhost = ip;
            ip = nhost.substring(0, ind + 1);
            ip = ip.substring(1, ind);
            return ip;
        } else {
            return ip;
        }
    }


}
