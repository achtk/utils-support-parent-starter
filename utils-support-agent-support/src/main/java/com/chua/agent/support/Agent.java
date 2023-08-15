package com.chua.agent.support;

import java.lang.instrument.Instrumentation;

import static com.chua.agent.support.store.AgentStore.*;
import static com.chua.agent.support.store.InstrumentationStore.installInstrumentation;
import static com.chua.agent.support.store.InstrumentationStore.preInstrumentation;
import static com.chua.agent.support.store.PluginStore.installPlugins;
import static com.chua.agent.support.store.PluginStore.prePlugins;
import static com.chua.agent.support.store.TransPointStore.installTransPoint;

/**
 * 代理
 *
 * @author CH
 */
public class Agent {

    public static void agentmain(String agentArguments, Instrumentation instrumentation) throws Exception {
        premain(agentArguments, instrumentation);
    }

    public static void premain(String agentArguments, Instrumentation instrumentation) throws Exception {
        install(instrumentation);
        installConfig(agentArguments);
        installEnvironment();
        installPlugins();
        prePlugins();
        installTransPoint();
        installInstrumentation();
        preInstrumentation();
    }


}
