package com.chua.common.support.reflection.reflections;

import com.chua.common.support.reflection.reflections.scanners.ResourceScanner;
import com.chua.common.support.reflection.reflections.util.ConfigurationBuilder;

import java.net.URL;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

/**
 * Configuration is used to create a configured instance of {@link Reflections}
 * <p>it is preferred to use {@link ConfigurationBuilder}
 *
 * @author Administrator
 */
public interface Configuration {
    /**
     * the scanner instances used for indexing metadata. defaults to {@code SubTypes} and {@code TypesAnnotated}.
     *
     * @return Set<Scanner>
     */
    Set<ResourceScanner> getScanners();

    /**
     * the urls to be scanned. required.
     *
     * @return Set
     */
    Set<URL> getUrls();

    /**
     * the fully qualified name filter used to filter types to be scanned. defaults to accept all inputs (if null).
     *
     * @return Predicate
     */
    Predicate<String> getInputsFilter();

    /**
     * scan urls in parallel. defaults to true.
     *
     * @return boolean
     */
    boolean isParallel();

    /**
     * optional class loaders used for resolving types.
     *
     * @return ClassLoader
     */
    ClassLoader[] getClassLoaders();

    /**
     * if true (default), expand super types after scanning, for super types that were not scanned.
     * <p>see {@link Reflections#expandSuperTypes(Map, Map)}
     *
     * @return boolean
     */
    boolean shouldExpandSuperTypes();
}
