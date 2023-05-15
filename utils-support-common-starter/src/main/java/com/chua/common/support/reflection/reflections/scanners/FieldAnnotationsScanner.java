package com.chua.common.support.reflection.reflections.scanners;

/**
 * scan field annotations.
 * <i>{@code Deprecated}, use {@link Scanners#FieldsAnnotated} instead</i>
 *
 * @author Administrator
 */
@Deprecated
public class FieldAnnotationsScanner extends AbstractScanner {

    /**
     * <i>{@code Deprecated}, use {@link Scanners#FieldsAnnotated} instead</i>
     */
    @Deprecated
    public FieldAnnotationsScanner() {
        super(Scanners.FieldsAnnotated);
    }
}
