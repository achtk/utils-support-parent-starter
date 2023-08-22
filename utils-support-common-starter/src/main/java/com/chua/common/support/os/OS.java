package com.chua.common.support.os;

import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * os
 * @author CH
 */
@Slf4j
public enum OS {
    OSX("^[Mm]ac OS X$"),
    LINUX("^[Ll]inux$"),
    WINDOWS("^[Ww]indows.*"),
    ANY("any");

    private final Set<Pattern> patterns;

    private OS(final String... patterns) {
        this.patterns = new HashSet<Pattern>();

        for (final String pattern : patterns) {
            this.patterns.add(Pattern.compile(pattern));
        }
    }

    private boolean is(final String id) {
        for (final Pattern pattern : patterns) {
            if (pattern.matcher(id).matches()) {
                return true;
            }
        }
        return false;
    }

    public static OS getCurrent() {
        final String osName = System.getProperty("os.name");

        for (final OS os : OS.values()) {
            if (os.is(osName)) {
                log.info("Current environment matches operating system descriptor '{}'.", os);
                return os;
            }
        }

        throw new UnsupportedOperationException(String.format("Operating system \"%s\" is not supported.", osName));
    }
}
