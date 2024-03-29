package com.chua.common.support.reflection.reflections.util;

import com.chua.common.support.reflection.reflections.Configuration;
import com.chua.common.support.reflection.reflections.Reflections;
import com.chua.common.support.reflection.reflections.ReflectionsException;
import com.chua.common.support.reflection.reflections.scanners.ResourceScanner;
import com.chua.common.support.reflection.reflections.scanners.Scanners;

import java.net.URL;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * {@link Configuration} builder for instantiating Reflections
 * <pre>{@code
 * // add urls for package prefix, use default scanners
 * new Reflections(
 *   new ConfigurationBuilder()
 *     .forPackage("com.chua.common.support.reflections"))
 *
 * new Reflections(
 *   new ConfigurationBuilder()
 *     .addUrls(ClasspathHelper.forPackage("com.chua.common.support.reflections"))   // add urls for package prefix
 *     .addScanners(Scanners.values())                           // use all standard scanners
 *     .filterInputsBy(new FilterBuilder().includePackage(...))) // optionally filter inputs
 * }</pre>
 * <p>defaults scanners: {@link Scanners#SubTypes} and {@link Scanners#TypesAnnotated}
 * <p><i>(breaking changes) Inputs filter will NOT be set automatically, consider adding in case too many classes are scanned.</i>
 * @author Administrator
 */
public class ConfigurationBuilder implements Configuration {
    public static final Set<ResourceScanner> DEFAULT_SCANNERS = new HashSet<>(Arrays.asList(Scanners.TypesAnnotated, Scanners.SubTypes));
    public static final Predicate<String> DEFAULT_INPUTS_FILTER = t -> true;

    private Set<ResourceScanner> scanners;
    private Set<URL> urls;
    private Predicate<String> inputsFilter;
    private boolean isParallel = true;
    private ClassLoader[] classLoaders;
    private boolean expandSuperTypes = true;

    public ConfigurationBuilder() {
        urls = new HashSet<>();
    }

    /**
     * constructs a {@link ConfigurationBuilder}.
     * <p>each parameter in {@code params} is referred by its type:
     * <ul>
     *     <li>{@link String} - add urls using {@link AbstractClasspathHelper#forPackage(String, ClassLoader...)} and an input filter
     *     <li>{@link Class} - add urls using {@link AbstractClasspathHelper#forClass(Class, ClassLoader...)} and an input filter
     *     <li>{@link Scanner} - use scanner, overriding default scanners
     *     <li>{@link URL} - add url for scanning
     *     <li>{@link Predicate} - set/override inputs filter
     *     <li>{@link ClassLoader} - use these classloaders in order to find urls using ClasspathHelper and for resolving types
     *     <li>{@code Object[]} - flatten and use each element as above
     * </ul>
     * input filter will be set according to given packages
     * <p></p><i>prefer using the explicit accessor methods instead:</i>
     * <pre>{@code new ConfigurationBuilder().forPackage(...).setScanners(...)}</pre>
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static ConfigurationBuilder build(Object... params) {
        final ConfigurationBuilder builder = new ConfigurationBuilder();

        // flatten
        List<Object> parameters = new ArrayList<>();
        for (Object param : params) {
            if (param.getClass().isArray()) {
                for (Object p : (Object[]) param) {
                    parameters.add(p);
                }
            } else if (param instanceof Iterable) {
                for (Object p : (Iterable) param) {
                    parameters.add(p);
                }
            } else {
                parameters.add(param);
            }
        }

        ClassLoader[] loaders = Stream.of(params).filter(p -> p instanceof ClassLoader).distinct().toArray(ClassLoader[]::new);
        if (loaders.length != 0) {
            builder.addClassLoaders(loaders);
        }

        FilterBuilder inputsFilter = new FilterBuilder();
        builder.filterInputsBy(inputsFilter);

        for (Object param : parameters) {
            if (param instanceof String && !((String) param).isEmpty()) {
                builder.forPackage((String) param, loaders);
                inputsFilter.includePackage((String) param);
            } else if (param instanceof Class && !Scanner.class.isAssignableFrom((Class) param)) {
                builder.addUrls(AbstractClasspathHelper.forClass((Class) param, loaders));
                inputsFilter.includePackage(((Class) param).getPackage().getName());
            } else if (param instanceof URL) {
                builder.addUrls((URL) param);
            } else if (param instanceof Scanner) {
                builder.addScanners((ResourceScanner) param);
            } else if (param instanceof Class && ResourceScanner.class.isAssignableFrom((Class) param)) {
                try {
                    builder.addScanners(((Class<ResourceScanner>) param).getDeclaredConstructor().newInstance());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else if (param instanceof Predicate) {
                builder.filterInputsBy((Predicate<String>) param);
            } else {
                throw new ReflectionsException("could not use param '" + param + "'");
            }
        }

        if (builder.getUrls().isEmpty()) {
            // scan all classpath if no urls provided todo avoid
            builder.addUrls(AbstractClasspathHelper.forClassLoader(loaders));
        }

        return builder;
    }

    /**
     * {@link #addUrls(URL...)} by applying {@link AbstractClasspathHelper#forPackage(String, ClassLoader...)} for the given {@code pkg}
     */
    public ConfigurationBuilder forPackage(String pkg, ClassLoader... classLoaders) {
        return addUrls(AbstractClasspathHelper.forPackage(pkg, classLoaders));
    }

    /**
     * {@link #addUrls(URL...)} by applying {@link AbstractClasspathHelper#forPackage(String, ClassLoader...)} for the given {@code packages}
     */
    public ConfigurationBuilder forPackages(String... packages) {
        for (String pkg : packages) {
            forPackage(pkg);
        }
        return this;
    }

    @Override
    public Set<ResourceScanner> getScanners() {
        return scanners != null ? scanners : DEFAULT_SCANNERS;
    }

    /**
     * set the scanners instances for scanning different metadata
     */
    public ConfigurationBuilder setScanners(ResourceScanner... scanners) {
        this.scanners = new HashSet<>(Arrays.asList(scanners));
        return this;
    }

    /**
     * set the scanners instances for scanning different metadata
     */
    public ConfigurationBuilder addScanners(ResourceScanner... scanners) {
        if (this.scanners == null) {
            setScanners(scanners);
        } else {
            this.scanners.addAll(Arrays.asList(scanners));
        }
        return this;
    }

    @Override
    public Set<URL> getUrls() {
        return urls;
    }

    /**
     * set the urls to be scanned
     * <p>use {@link AbstractClasspathHelper} convenient methods to get the relevant urls
     * <p>see also {@link #forPackages(String...)}
     */
    public ConfigurationBuilder setUrls(Collection<URL> urls) {
        this.urls = new HashSet<>(urls);
        return this;
    }

    /**
     * set the urls to be scanned
     * <p>use {@link AbstractClasspathHelper} convenient methods to get the relevant urls
     * <p>see also {@link #forPackages(String...)}
     */
    public ConfigurationBuilder setUrls(URL... urls) {
        return setUrls(Arrays.asList(urls));
    }

    /**
     * add urls to be scanned
     * <p>use {@link AbstractClasspathHelper} convenient methods to get the relevant urls
     * <p>see also {@link #forPackages(String...)}
     */
    public ConfigurationBuilder addUrls(Collection<URL> urls) {
        this.urls.addAll(urls);
        return this;
    }

    /**
     * add urls to be scanned
     * <p>use {@link AbstractClasspathHelper} convenient methods to get the relevant urls
     * <p>see also {@link #forPackages(String...)}
     */
    public ConfigurationBuilder addUrls(URL... urls) {
        return addUrls(Arrays.asList(urls));
    }

    @Override
    public Predicate<String> getInputsFilter() {
        return inputsFilter != null ? inputsFilter : DEFAULT_INPUTS_FILTER;
    }

    /**
     * sets the input filter for all resources to be scanned.
     * <p>prefer using {@link FilterBuilder}
     */
    public ConfigurationBuilder setInputsFilter(Predicate<String> inputsFilter) {
        this.inputsFilter = inputsFilter;
        return this;
    }

    /**
     * sets the input filter for all resources to be scanned.
     * <p>prefer using {@link FilterBuilder}
     */
    public ConfigurationBuilder filterInputsBy(Predicate<String> inputsFilter) {
        return setInputsFilter(inputsFilter);
    }

    @Override
    public boolean isParallel() {
        return isParallel;
    }

    /**
     * if true, scan urls in parallel.
     */
    public ConfigurationBuilder setParallel(boolean parallel) {
        isParallel = parallel;
        return this;
    }

    @Override
    public ClassLoader[] getClassLoaders() {
        return classLoaders;
    }


    /**
     * set optional class loaders used for resolving types.
     */
    public ConfigurationBuilder setClassLoaders(ClassLoader[] classLoaders) {
        this.classLoaders = classLoaders;
        return this;
    }

    /**
     * add optional class loaders used for resolving types.
     */
    public ConfigurationBuilder addClassLoaders(ClassLoader... classLoaders) {
        this.classLoaders = this.classLoaders == null ? classLoaders :
                Stream.concat(Arrays.stream(this.classLoaders), Arrays.stream(classLoaders)).distinct().toArray(ClassLoader[]::new);
        return this;
    }

    @Override
    public boolean shouldExpandSuperTypes() {
        return expandSuperTypes;
    }

    /**
     * if set to true, Reflections will expand super types after scanning.
     * <p>see {@link Reflections#expandSuperTypes(Map, Map)}
     */
    public ConfigurationBuilder setExpandSuperTypes(boolean expandSuperTypes) {
        this.expandSuperTypes = expandSuperTypes;
        return this;
    }
}
