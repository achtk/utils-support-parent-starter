package com.chua.common.support.os;

import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Arch
 * @author CH
 */
@Slf4j
public enum Arch {
    /**
     * x86
     */
    X86_32("i386", "i686", "x86"),
    /**
     * 64
     */
    X86_64("amd64", "x86_64"),
    /**
     * arm7
     */
    ARMv7("arm"),
    /**
     * arm8
     */
    ARMv8("aarch64", "arm64");

    private final Set<String> patterns;

    private Arch(final String... patterns) {
        this.patterns = new HashSet<String>(Arrays.asList(patterns));
    }

    private boolean is(final String id) {
        return patterns.contains(id);
    }

    public static Arch getCurrent() {
        final String osArch = System.getProperty("os.arch");

        for (final Arch arch : Arch.values()) {
            if (arch.is(osArch)) {
                log.info("Current environment matches architecture descriptor \"{}\".", arch);
                return arch;
            }
        }

        throw new UnsupportedOperationException(String.format("Architecture \"%s\" is not supported.", osArch));
    }
}
