package com.chua.common.support.reflection.reflections.scanners;

/**
 * scan method annotations.
 * <p></p><i>breaking change: does not include constructor annotations, use {@link Scanners#ConstructorsAnnotated} instead </i>
 * <p></p><i>{@code Deprecated}, use {@link Scanners#MethodsAnnotated} and {@link Scanners#ConstructorsAnnotated} instead</i>
 *
 * @author Administrator
 */
@Deprecated
public class MethodAnnotationsResourceScanner extends AbstractResourceScanner {

    /**
     * <i>{@code Deprecated}, use {@link Scanners#MethodsAnnotated} and {@link Scanners#ConstructorsAnnotated} instead</i>
     */
    @Deprecated
    public MethodAnnotationsResourceScanner() {
        super(Scanners.MethodsAnnotated);
    }
}
