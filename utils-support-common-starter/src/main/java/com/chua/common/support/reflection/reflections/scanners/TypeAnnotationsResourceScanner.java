package com.chua.common.support.reflection.reflections.scanners;

/**
 * scan class annotations, where @Retention(RetentionPolicy.RUNTIME).
 * <i>{@code Deprecated}, use {@link Scanners#TypesAnnotated} instead</i>
 *
 * @author Administrator
 */
@Deprecated
public class TypeAnnotationsResourceScanner extends AbstractResourceScanner {

    /**
     * <i>{@code Deprecated}, use {@link Scanners#TypesAnnotated} instead</i>
     */
    @Deprecated
    public TypeAnnotationsResourceScanner() {
        super(Scanners.TypesAnnotated);
    }
}
