package com.chua.common.support.discovery;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.bean.BeanMap;
import com.chua.common.support.converter.Converter;
import com.chua.common.support.lang.net.UrlQuery;
import com.chua.common.support.lang.robin.Node;
import com.chua.common.support.lang.robin.RandomRoundRobin;
import com.chua.common.support.lang.robin.Robin;
import com.chua.common.support.net.NetAddress;
import com.chua.common.support.net.NetUtils;
import com.chua.common.support.utils.CollectionUtils;
import com.chua.common.support.utils.FileUtils;
import com.chua.common.support.utils.StringUtils;
import com.chua.common.support.utils.ThreadUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_COMMA;
import static com.chua.common.support.discovery.Constants.*;

/**
 * 组播
 *
 * @author CH
 */
@Slf4j
@Spi("multicast")
public class MulticastServiceDiscovery implements ServiceDiscovery, Runnable {
    static String NETWORK_IGNORED_INTERFACE = "network.interface.ignored";
    private final ConcurrentMap<String, Set<NetAddress>> received = new ConcurrentHashMap<>();
    private final Set<NetAddress> registered = new CopyOnWriteArraySet<>();
    private final ExecutorService executor = ThreadUtils.newSingleThreadExecutor("multicast-service-discovery");
    private Robin robin = new RandomRoundRobin();
    private DatagramPacket datagramPacketSend;
    private DatagramPacket datagramPacketReceive;

    private ScheduledFuture<?> cleanFuture;
    private int cleanPeriod;

    /**
     * 多点广播的地址
     */
    private MulticastSocket multicastSocket;
    private InetAddress multicastAddress;
    private Integer multicastPort;
    private Integer bufferSize;

    public static void joinMulticastGroup(MulticastSocket multicastSocket, InetAddress multicastAddress) throws
            IOException {
        setInterface(multicastSocket, multicastAddress instanceof Inet6Address);

        // For the deprecation notice: the equivalent only appears in JDK 9+.
        multicastSocket.setLoopbackMode(false);
        multicastSocket.joinGroup(multicastAddress);
    }

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
                        // ignore
                    }
                } else if (!preferIpv6 && address instanceof Inet4Address) {
                    try {
                        if (address.isReachable(100)) {
                            multicastSocket.setInterface(address);
                            interfaceSet = true;
                            break;
                        }
                    } catch (IOException e) {
                        // ignore
                    }
                }
            }
            if (interfaceSet) {
                break;
            }
        }
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
     * Returns {@code true} if the specified {@link NetworkInterface} should be ignored with the given conditions.
     *
     * @param networkInterface the {@link NetworkInterface} to check
     * @return {@code true} if the specified {@link NetworkInterface} should be ignored, otherwise {@code false}
     * @throws SocketException SocketException if an I/O error occurs.
     */
    private static boolean ignoreNetworkInterface(NetworkInterface networkInterface) throws SocketException {
        if (networkInterface == null
                || networkInterface.isLoopback()
                || networkInterface.isVirtual()
                || !networkInterface.isUp()) {
            return true;
        }
        String ignoredInterfaces = System.getProperty(NETWORK_IGNORED_INTERFACE);
        String networkInterfaceDisplayName;
        if (!StringUtils.isEmpty(ignoredInterfaces)
                && !StringUtils.isEmpty(networkInterfaceDisplayName = networkInterface.getDisplayName())) {
            for (String ignoredInterface : ignoredInterfaces.split(SYMBOL_COMMA)) {
                String trimIgnoredInterface = ignoredInterface.trim();
                boolean matched = false;
                try {
                    matched = networkInterfaceDisplayName.matches(trimIgnoredInterface);
                } catch (PatternSyntaxException e) {
                    // if trimIgnoredInterface is an invalid regular expression, a PatternSyntaxException will be thrown out
                    log.warn("exception occurred: " + networkInterfaceDisplayName + " matches " + trimIgnoredInterface, e);
                }
            }
        }
        return false;
    }

    @Override
    public ServiceDiscovery robin(Robin robin) {
        this.robin = robin;
        return this;
    }

    @Override
    public Discovery discovery(String discovery, DiscoveryBoundType strategy) throws Exception {
        discovery = StringUtils.startWithAppend(discovery, "/");
        Set<NetAddress> netAddresses = received.get(discovery);
        List<Discovery> proxyUri = new LinkedList<>();
        if (null != netAddresses) {
            for (NetAddress netAddress : netAddresses) {
                UrlQuery source = netAddress.getUrlQuery();
                proxyUri.add(Discovery.builder()
                        .address(source.get("address").toString())
                        .discovery(source.get("discovery").toString())
                        .weight(Converter.convertIfNecessary(source.get("weight").toString(), Float.class))
                        .build());
            }
        }

        String proxyPath = discovery;
        if (proxyUri.isEmpty()) {
            String fullPath = discovery;
            while (!StringUtils.isNullOrEmpty(fullPath = (FileUtils.getFullPath(fullPath)))) {
                if (fullPath.endsWith("/")) {
                    fullPath = fullPath.substring(0, fullPath.length() - 1);
                }

                Collection<NetAddress> strings1 = received.get(fullPath);

                proxyPath = fullPath;
                if (null != strings1 && !strings1.isEmpty()) {
                    for (NetAddress netAddress : strings1) {
                        Object source = netAddress.getParameter("source", null);
                        if (null == source) {
                            continue;
                        }
                        proxyUri.add((Discovery) source);
                    }
                    break;
                }
            }
        }

        Robin robin1 = robin.create();
        robin1.addNodes(createNode(proxyUri, discovery.substring(proxyPath.length())));
        Node discoveryNode = robin1.selectNode();
        return null == discoveryNode ? null : discoveryNode.getValue(Discovery.class);
    }

    /**
     * 创建节点
     *
     * @param proxyUri url
     * @param uri      uri
     * @return node
     */
    private List<Node> createNode(List<Discovery> proxyUri, String uri) {
        return proxyUri.stream().map(it -> {
            Node node = new Node();
            node.setContent(it);
            node.setWeight((int) it.getWeight());
            return node;
        }).collect(Collectors.toList());
    }

    @Override
    public ServiceDiscovery register(Discovery discovery) {
        NetAddress netAddress = NetAddress.of(discovery.getAddress());
        netAddress.addParameter(BeanMap.create(discovery));
        multicast(REGISTER + " " + netAddress.toFullString());
        return this;
    }

    @Override
    public ServiceDiscovery start(DiscoveryOption discoveryOption) throws Exception {
        NetAddress netAddress = NetAddress.of(discoveryOption.getAddress());
        String host = netAddress.getHost();
        this.multicastAddress = InetAddress.getByName(host);
        this.multicastPort = netAddress.getPort();

        this.multicastSocket = new MulticastSocket(multicastPort);

        this.bufferSize = (Integer) discoveryOption.getOrDefault("buffer.size", 2048);

        this.datagramPacketReceive = new DatagramPacket(new byte[bufferSize], bufferSize);
        this.datagramPacketSend = new DatagramPacket(new byte[bufferSize], bufferSize, InetAddress.getByName(host), netAddress.getPort());
        joinMulticastGroup(multicastSocket, multicastAddress);
        executor.execute(this);

        this.cleanPeriod = (int) discoveryOption.getOrDefault(SESSION_TIMEOUT_KEY, DEFAULT_SESSION_TIMEOUT);
        this.cleanFuture = ThreadUtils.newScheduleWithFixedDelay(() -> {
            try {
                clean(); // Remove the expired
            } catch (Throwable t) { // Defensive fault tolerance
                log.error("Unexpected exception occur at clean expired provider, cause: " + t.getMessage(), t);
            }
        }, cleanPeriod, cleanPeriod, TimeUnit.MILLISECONDS);
        return this;
    }

    /**
     * Remove the expired providers, only when "clean" parameter is true.
     */
    private void clean() {
        for (Set<NetAddress> providers : new HashSet<>(received.values())) {
            for (NetAddress url : new HashSet<>(providers)) {
                if (isExpired(url)) {
                    if (log.isWarnEnabled()) {
                        log.warn("Clean expired provider " + url);
                    }
                    doUnregister(url);
                }
            }
        }
    }

    public void doUnregister(NetAddress netAddress) {
        multicast(UNREGISTER + " " + netAddress.toFullString());
    }

    private boolean isExpired(NetAddress netAddress) {
        if (!netAddress.getParameter(DYNAMIC_KEY, true) || netAddress.getPort() <= 0 || CONSUMER_PROTOCOL.equals(netAddress.getProtocol())) {
            return false;
        }
        try (Socket socket = new Socket(netAddress.getHost(), netAddress.getPort())) {
        } catch (Throwable e) {
            try {
                Thread.sleep(100);
            } catch (Throwable ignored) {
            }
            try (Socket socket2 = new Socket(netAddress.getHost(), netAddress.getPort())) {
            } catch (Throwable e2) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ServiceDiscovery stop() throws Exception {
        executor.shutdownNow();
        return this;
    }

    @Override
    public void run() {
        byte[] buf = new byte[bufferSize];
        while (!multicastSocket.isClosed()) {
            try {
                multicastSocket.receive(datagramPacketReceive);
                String msg = new String(datagramPacketReceive.getData()).trim();
                int i = msg.indexOf('\n');
                if (i > 0) {
                    msg = msg.substring(0, i).trim();
                }
                this.receive(msg, (InetSocketAddress) datagramPacketReceive.getSocketAddress());
                Arrays.fill(buf, (byte) 0);
            } catch (Throwable e) {
                if (!multicastSocket.isClosed()) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    private void receive(String msg, InetSocketAddress remoteAddress) {
        if (log.isInfoEnabled()) {
            log.info("Receive multicast message: " + msg + " from " + remoteAddress);
        }

        if (msg.startsWith(REGISTER)) {
            NetAddress netAddress = NetAddress.of(msg.substring(REGISTER.length()).trim());
            registered(netAddress);
        } else if (msg.startsWith(UNREGISTER)) {
            NetAddress netAddress = NetAddress.of(msg.substring(UNREGISTER.length()).trim());
            unregistered(netAddress);
        } else if (msg.startsWith(SUBSCRIBE)) {
            NetAddress url = NetAddress.valueOf(msg.substring(SUBSCRIBE.length()).trim());
            Set<NetAddress> urls = getRegistered();
            if (CollectionUtils.isNotEmpty(urls)) {
                for (NetAddress u : urls) {
                    String host = remoteAddress != null && remoteAddress.getAddress() != null ? remoteAddress.getAddress().getHostAddress() : url.getIp();
                    if (url.getParameter("unicast", true)
                            && !NetUtils.getLocalHost().equals(host)) {
                        unicast(REGISTER + " " + u.toFullString(), host);
                    } else {
                        multicast(REGISTER + " " + u.toFullString());
                    }
                }
            }
        }
    }

    public Set<NetAddress> getRegistered() {
        Set<NetAddress> rs = new LinkedHashSet<>();
        Collection<Set<NetAddress>> values = received.values();
        for (Set<NetAddress> value : values) {
            rs.addAll(value);
        }
        return Collections.unmodifiableSet(rs);
    }


    protected void registered(NetAddress netAddress) {
        Set<NetAddress> urls = received.computeIfAbsent(
                netAddress.getParameter("discovery", "/"), k -> new CopyOnWriteArraySet<>());
        urls.add(netAddress);
    }

    protected void unregistered(NetAddress url) {
        URL key = url.toUrl();
        Set<NetAddress> urls = received.get(key);
        if (urls != null) {
            urls.remove(url);
        }
        if (urls == null || urls.isEmpty()) {
            if (urls == null) {
                urls = new CopyOnWriteArraySet<>();
            }
            NetAddress empty = url.setProtocol(EMPTY_PROTOCOL);
            urls.add(empty);
        }
    }


    private void unicast(String msg, String host) {
        if (log.isInfoEnabled()) {
            log.info("Send unicast message: " + msg + " to " + host + ":" + multicastPort);
        }
        try {
            byte[] data = (msg + "\n").getBytes();
            DatagramPacket hi = new DatagramPacket(data, data.length, InetAddress.getByName(host), multicastPort);
            multicastSocket.send(hi);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    private void multicast(String msg) {
        if (log.isInfoEnabled()) {
            log.info("Send multicast message: " + msg + " to " + multicastAddress + ":" + multicastPort);
        }
        try {
            byte[] data = (msg + "\n").getBytes();
            DatagramPacket hi = new DatagramPacket(data, data.length, multicastAddress, multicastPort);
            multicastSocket.send(hi);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

}
