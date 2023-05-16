package com.chua.common.support.reflection.reflections.scanners;

import javassist.bytecode.ClassFile;

import java.util.List;
import java.util.Map;

/**
 * scan superclass and interfaces of a class, allowing a reverse lookup for subtypes.
 * <i>{@code Deprecated}, use {@link Scanners#SubTypes} instead</i>
 *
 * @author Administrator
 */
@Deprecated
public class SubTypesResourceScanner extends AbstractResourceScanner {

    /**
     * create new SubTypesScanner. will exclude direct Object subtypes
     * <i>{@code Deprecated}, use {@link Scanners#SubTypes} instead</i>
     */
    @Deprecated
    public SubTypesResourceScanner() {
        super(Scanners.SubTypes);
    }

    /**
     * create new SubTypesScanner. include direct {@link Object} subtypes in results.
     * <i>{@code Deprecated}, use {@link Scanners#SubTypes} instead</i>
     */
    @Deprecated
    public SubTypesResourceScanner(boolean excludeObjectClass) {
        super(excludeObjectClass ? Scanners.SubTypes : Scanners.SubTypes.filterResultsBy(s -> true));
    }

    @Override
    public List<Map.Entry<String, String>> scan(final ClassFile cls) {
        return resourceScanner.scan(cls);
    }
}
