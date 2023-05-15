package com.chua.common.support.reflection.reflections.scanners;

/**
 * scan class annotations, where @Retention(RetentionPolicy.RUNTIME).
 * <i>{@code Deprecated}, use {@link Scanners#TypesAnnotated} instead</i>
 *
 * @author Administrator
 */
@Deprecated
public class TypeAnnotationsScanner extends AbstractScanner {

    /**
     * <i>{@code Deprecated}, use {@link Scanners#TypesAnnotated} instead</i>
     */
    @Deprecated
    public TypeAnnotationsScanner() {
        super(Scanners.TypesAnnotated);
    }
}
