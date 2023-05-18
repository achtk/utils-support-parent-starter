package com.chua.agent.support.plugin;

import com.alibaba.json.JSON;
import com.alibaba.json.JSONObject;
import com.chua.agent.support.Agent;
import com.chua.agent.support.annotation.Path;
import com.chua.agent.support.constant.NamedThreadFactory;
import com.chua.agent.support.span.span.Span;
import com.chua.agent.support.ws.SimpleWsServer;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.matcher.ElementMatcher;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;
import oshi.util.Util;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.chua.agent.support.Agent.VM_DELAY;
import static com.chua.agent.support.Agent.VM_TOP;

/**
 * html静态页
 *
 * @author CH
 */
public class VmAgentPlugin implements HtmlAgentPlugin {
    public static final ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1, new NamedThreadFactory("agent-vm"));
    public static int OSHI_WAIT_SECOND = 1000;
    static DecimalFormat format = new DecimalFormat("0.00");

    static Object status = null;
    static void refresh(){

        if(null != status) {
            return;
        }

        status = new Object();
        SystemInfo si = new SystemInfo();

        HardwareAbstractionLayer hardware = si.getHardware();
        CentralProcessor processor = hardware.getProcessor();
        List<NetworkIF> networkIFs = hardware.getNetworkIFs();
        GlobalMemory memory = hardware.getMemory();
        OperatingSystem operatingSystem = si.getOperatingSystem();
        FileSystem fileSystem = operatingSystem.getFileSystem();
        executorService.scheduleWithFixedDelay(() -> {
            try {
                sendNet(networkIFs);
                sendProcess(operatingSystem);
                sendFileSystem(fileSystem);
                sendMem(memory);
                sendCpu(processor);
                Thread.sleep(0);
            } catch (Exception ignored) {
            }
        }, 0, Agent.getIntegerValue(VM_DELAY, 5000), TimeUnit.MILLISECONDS);
    }

    private static void sendProcess(OperatingSystem operatingSystem) {
        List<OSProcess> processes1 = operatingSystem.getProcesses(Agent.getIntegerValue(VM_TOP, 10), OperatingSystem.ProcessSort.CPU);
        List<Span> spanList = new LinkedList<>();
        Span span1 = new Span();
        span1.setType("process-cpu");
        for (OSProcess osProcess : processes1) {
            Span span = new Span();
            span.setType(osProcess.getUser());
            span.setThreadName(osProcess.getName());
            span.setId(String.valueOf(osProcess.getProcessID()));
            span.setEnterTime(new Date());
            span.setEx(format.format(osProcess.getProcessCpuLoadCumulative()) + "%");

            spanList.add(span);
        }
        span1.setEx(JSON.toJSONString(spanList));

        SimpleWsServer.send(span1, "vm");
        List<OSProcess> processes2 = operatingSystem.getProcesses(Agent.getIntegerValue(VM_TOP, 10), OperatingSystem.ProcessSort.MEMORY);

        List<Span> spanList1 = new LinkedList<>();
        Span span11 = new Span();
        span11.setType("process-mem");
        for (OSProcess osProcess : processes2) {
            Span span = new Span();
            span.setType(osProcess.getUser());
            span.setThreadName(osProcess.getName());
            span.setId(String.valueOf(osProcess.getProcessID()));
            span.setEnterTime(new Date());
            span.setEx(BeanAgentPlugin.getNetFileSizeDescription(osProcess.getResidentSetSize(), format));

            spanList1.add(span);
        }
        span11.setEx(JSON.toJSONString(spanList1));

        SimpleWsServer.send(span11, "vm");
    }

    private static void sendMem(GlobalMemory memory) {
        List<Span> spanList = new LinkedList<>();
        Span span1 = new Span();
        span1.setType("mem");
        DecimalFormat format = new DecimalFormat("#.00000");

        Span span = new Span();
        span.setType("mem");
        span.setThreadName("未使用内存");
        span.setEnterTime(new Date());
        span.setEx(format.format(1.0d * memory.getAvailable() / memory.getTotal()));

        spanList.add(span);
        Span span2 = new Span();
        span2.setType("mem");
        span2.setThreadName("已使用内存");
        span2.setEnterTime(new Date());
        span2.setEx(format.format(1d - 1.0 * memory.getAvailable() / memory.getTotal()));

        spanList.add(span2);
        span1.setEx(JSON.toJSONString(spanList));

        SimpleWsServer.send(span1, "vm");
    }

    private static void sendFileSystem(FileSystem fileSystem) {
        List<Span> spanList = new LinkedList<>();
        Span span1 = new Span();
        span1.setType("file");
        DecimalFormat format = new DecimalFormat("#.00000");
        List<OSFileStore> fileStores = fileSystem.getFileStores();
        for (OSFileStore fileStore : fileStores) {
            Span span = new Span();
            span.setType("file");
            span.setThreadName(fileStore.getName());
            span.setDb(BeanAgentPlugin.getNetFileSizeDescription(fileStore.getFreeSpace(), format));
            span.setId(BeanAgentPlugin.getNetFileSizeDescription(fileStore.getTotalSpace(), format));
            span.setPid(fileStore.getDescription());
            span.setEnterTime(new Date());
            span.setEx("0" + format.format(1d - 1.0d * fileStore.getFreeSpace() / fileStore.getTotalSpace()));

            spanList.add(span);
        }

        span1.setEx(JSON.toJSONString(spanList));

        SimpleWsServer.send(span1, "vm");
    }

    private static void sendNet(List<NetworkIF> networkIFs) {
        List<Span> spanList = new LinkedList<>();

        for (NetworkIF networkIF : networkIFs) {
            long bytesRecv = networkIF.getBytesRecv();
            long bytesSent = networkIF.getBytesSent();
            String name = networkIF.getDisplayName();

            Span span = new Span();
            span.setType("net");
            span.setEnterTime(new Date());
            span.setThreadName(name + "发送");
            span.setEx(String.valueOf(bytesSent));

            spanList.add(span);

            Span span1 = new Span();
            span1.setType("net");
            span1.setEnterTime(new Date());
            span1.setThreadName(name + "接收");
            span1.setEx(String.valueOf(bytesRecv));

            spanList.add(span1);
        }

        for (Span span : spanList) {
            SimpleWsServer.send(span, "vm");
        }
    }

    private static void sendCpu(CentralProcessor processor) {
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        // 睡眠1s
        Util.sleep(OSHI_WAIT_SECOND);
        long[] ticks = processor.getSystemCpuLoadTicks();
        long nice = ticks[CentralProcessor.TickType.NICE.getIndex()]
                - prevTicks[CentralProcessor.TickType.NICE.getIndex()];
        long irq = ticks[CentralProcessor.TickType.IRQ.getIndex()]
                - prevTicks[CentralProcessor.TickType.IRQ.getIndex()];
        long softirq = ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()]
                - prevTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
        long steal = ticks[CentralProcessor.TickType.STEAL.getIndex()]
                - prevTicks[CentralProcessor.TickType.STEAL.getIndex()];
        long cSys = ticks[CentralProcessor.TickType.SYSTEM.getIndex()]
                - prevTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
        long user = ticks[CentralProcessor.TickType.USER.getIndex()]
                - prevTicks[CentralProcessor.TickType.USER.getIndex()];
        long iowait = ticks[CentralProcessor.TickType.IOWAIT.getIndex()]
                - prevTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
        long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()]
                - prevTicks[CentralProcessor.TickType.IDLE.getIndex()];
        long totalCpu = user + nice + cSys + idle + iowait + irq + softirq + steal;
        Span span = new Span();
        try {
            span.setThreadName(processor.toString().split("\n")[0]);
        } catch (Exception ignored) {
            span.setThreadName("CPU");
        }
        span.setEnterTime(new Date());
        span.setEx(format.format((cSys + user) * 1.0 / totalCpu));
        span.setType("cpu");
        SimpleWsServer.send(span, "vm");
    }

    @Path("vm")
    public String vm() {
        refresh();
        return "html_vm.html";
    }


    @Override
    public String name() {
        return "vm";
    }

    @Override
    public Class<?> pluginType() {
        return VmAgentPlugin.class;
    }

    @Override
    public DynamicType.Builder.MethodDefinition.ReceiverTypeDefinition<?> transform(DynamicType.Builder<?> builder) {
        return null;
    }

    @Override
    public ElementMatcher<? super TypeDescription> type() {
        return null;
    }

    @Override
    public void setAddress(String address) {

    }

    @Override
    public void setParameter(JSONObject parameter) {

    }
}
