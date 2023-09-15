package com.chua.common.support.discovery;

import com.chua.common.support.annotations.Spi;
import com.chua.common.support.json.Json;
import com.chua.common.support.lang.robin.Node;
import com.chua.common.support.lang.robin.Robin;
import com.chua.common.support.net.NetAddress;
import com.chua.common.support.net.NetUtils;
import com.chua.common.support.spi.ServiceProvider;
import com.chua.common.support.utils.CollectionUtils;
import com.chua.common.support.utils.StringUtils;
import com.chua.common.support.utils.ThreadUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.PatternSyntaxException;

import static com.chua.common.support.constant.CommonConstant.SYMBOL_COMMA;
import static com.chua.common.support.discovery.Constants.*;

/**
 * @author CH
 */
@Slf4j
@Spi("multicast")
public class MulticastServiceDiscovery extends AbstractServiceDiscovery implements Runnable {

    static String NETWORK_IGNORED_INTERFACE = "network.interface.ignored";
    private final ConcurrentMap<String, List<Discovery>> received = new ConcurrentHashMap<>();
    private final Set<Discovery> registered = new CopyOnWriteArraySet<>();
    private final ExecutorService executor = ThreadUtils.newSingleThreadExecutor("multicast-service-discovery");
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


    public MulticastServiceDiscovery(DiscoveryOption discoveryOption) {
        super(discoveryOption);
    }


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
    public ServiceDiscovery registerService(String path, Discovery discovery) {
        multicast(REGISTER + " " + discovery.toFullString());
        received.computeIfAbsent(path, it -> new LinkedList<>()).add(discovery);
        return this;
    }

    @Override
    public Discovery getService(String path, String balance) {
        List<Discovery> netAddresses = received.get(path);
        Robin robin = ServiceProvider.of(Robin.class).getNewExtension(balance);
        Robin robin1 = robin.create();
        robin1.addNode(netAddresses);
        Node selectNode = robin1.selectNode();
        return selectNode.getValue(Discovery.class);
    }

    @Override
    public void afterPropertiesSet() {

    }

    @Override
    public void start() throws IOException {
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
        multicast(SUBSCRIBE + "L" + multicastPort);
    }

    /**
     * Remove the expired providers, only when "clean" parameter is true.
     */
    private void clean() {
        for (List<Discovery> providers : new HashSet<>(received.values())) {
            for (Discovery url : new HashSet<>(providers)) {
                if (isExpired(url)) {
                    if (log.isWarnEnabled()) {
                        log.warn("Clean expired provider " + url);
                    }
                    doUnregister(url);
                }
            }
        }
    }

    public void doUnregister(Discovery discovery) {
        multicast(UNREGISTER + " " + discovery.toFullString());
    }

    private boolean isExpired(Discovery discovery) {
        NetAddress netAddress = NetAddress.of(discovery.getAddress());
        if (!netAddress.getParameter(DYNAMIC_KEY, true) || netAddress.getPort() <= 0 || CONSUMER_PROTOCOL.equals(netAddress.getProtocol())) {
            return false;
        }
        try (Socket socket = new Socket(netAddress.getAddress(), netAddress.getPort())) {
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
    public void close() {
        executor.shutdownNow();
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
            Discovery discovery = Json.fromJson(msg.substring(REGISTER.length()).trim(), Discovery.class);
            registered(discovery);
        } else if (msg.startsWith(UNREGISTER)) {
            Discovery discovery = Json.fromJson(msg.substring(REGISTER.length()).trim(), Discovery.class);
            unregistered(discovery);
        } else if (msg.startsWith(SUBSCRIBE)) {
            String urlAndHost = msg.substring(SUBSCRIBE.length()).trim();
            String[] ls = urlAndHost.split("L");
            String url = ls[0];
            String host = ls[1];
            List<Discovery> urls = received.get(url);
            if (CollectionUtils.isNotEmpty(urls)) {
                for (Discovery u : urls) {
                    if (!NetUtils.getLocalHost().equals(host)) {
                        unicast(REGISTER + " " + u.toFullString(), host);
                    } else {
                        multicast(REGISTER + " " + u.toFullString());
                    }
                }
            }
        }
    }


    /**
     * 注册
     *
     * @param discovery 发现
     */
    protected void registered(Discovery discovery) {
        List<Discovery> urls = received.computeIfAbsent(discovery.getUriSpec(), k -> new LinkedList<>());
        urls.add(discovery);
    }

    /**
     * 注销
     *
     * @param discovery 发现
     */
    protected void unregistered(Discovery discovery) {
        List<Discovery> discoveryList = received.get(discovery.getUriSpec());
        if (discoveryList == null) {
            return;
        }

        List<Discovery> remove = new LinkedList<>();
        for (Discovery discovery1 : discoveryList) {
            if(discovery1.getPort() == discovery.getPort() && discovery1.getAddress().equals(discovery.getAddress())) {
                remove.add(discovery1);
            }
        }

        discoveryList.removeAll(remove);
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
