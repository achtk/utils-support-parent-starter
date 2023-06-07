package com.chua.common.support.discovery;

/**
 * Constants
 *
 * @author CH
 */
public interface Constants {
    String REGISTER_IP_KEY = "register.ip";

    String REGISTER_KEY = "register";

    String SUBSCRIBE_KEY = "subscribe";

    String DEFAULT_REGISTRY = "dubbo";

    String REGISTER = "register";

    String UNREGISTER = "unregister";

    String SUBSCRIBE = "subscribe";

    String UNSUBSCRIBE = "unsubscribe";

    String CONFIGURATORS_SUFFIX = ".configurators";

    String ADMIN_PROTOCOL = "admin";

    String PROVIDER_PROTOCOL = "provider";

    String CONSUMER_PROTOCOL = "consumer";

    String SCRIPT_PROTOCOL = "script";

    String CONDITION_PROTOCOL = "condition";
    String TRACE_PROTOCOL = "trace";
    /**
     * simple the registry for provider.
     *
     * @since 2.7.0
     */
    String SIMPLIFIED_KEY = "simplified";

    /**
     * To decide whether register center saves file synchronously, the default value is asynchronously
     */
    String REGISTRY_FILESAVE_SYNC_KEY = "save.file";

    /**
     * Reconnection period in milliseconds for register center
     */
    String REGISTRY_RECONNECT_PERIOD_KEY = "reconnect.period";

    int DEFAULT_SESSION_TIMEOUT = 60 * 1000;

    /**
     * Default value for the times of retry: 3
     */
    int DEFAULT_REGISTRY_RETRY_TIMES = 3;

    int DEFAULT_REGISTRY_RECONNECT_PERIOD = 3 * 1000;

    /**
     * Default value for the period of retry interval in milliseconds: 5000
     */
    int DEFAULT_REGISTRY_RETRY_PERIOD = 5 * 1000;

    /**
     * Most retry times
     */
    String REGISTRY_RETRY_TIMES_KEY = "retry.times";

    /**
     * Period of registry center's retry interval
     */
    String REGISTRY_RETRY_PERIOD_KEY = "retry.period";

    String SESSION_TIMEOUT_KEY = "session";

    /**
     * To decide the frequency of checking Distributed Service Discovery Registry callback hook (in ms)
     */
    String ECHO_POLLING_CYCLE_KEY = "echoPollingCycle";

    /**
     * Default value for check frequency: 60000 (ms)
     */
    int DEFAULT_ECHO_POLLING_CYCLE = 60000;

    String MIGRATION_STEP_KEY = "migration.step";

    String MIGRATION_DELAY_KEY = "migration.delay";

    String MIGRATION_FORCE_KEY = "migration.force";

    String MIGRATION_PROMOTION_KEY = "migration.promotion";

    String MIGRATION_THRESHOLD_KEY = "migration.threshold";

    String ENABLE_CONFIGURATION_LISTEN = "enable-configuration-listen";

    /**
     * MIGRATION_RULE_XXX from remote configuration
     */
    String MIGRATION_RULE_KEY = "key";

    String MIGRATION_RULE_STEP_KEY = "step";

    String MIGRATION_RULE_THRESHOLD_KEY = "threshold";

    String MIGRATION_RULE_PROPORTION_KEY = "proportion";

    String MIGRATION_RULE_DELAY_KEY = "delay";

    String MIGRATION_RULE_FORCE_KEY = "force";

    String MIGRATION_RULE_INTERFACES_KEY = "interfaces";

    String MIGRATION_RULE_APPLICATIONS_KEY = "applications";

    String USER_HOME = "user.home";

    String CACHE = ".cache";
    String DYNAMIC_KEY = "dynamic";
    String EMPTY_PROTOCOL = "empty";

    String POM = "pom.xml";
    String CAPTCHA_SESSION_KEY = "captcha-code";
}
