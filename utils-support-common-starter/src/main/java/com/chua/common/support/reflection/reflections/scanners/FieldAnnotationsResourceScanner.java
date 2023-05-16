package com.chua.common.support.reflection.reflections.scanners;

/**
 * scan field annotations.
 * <i>{@code Deprecated}, use {@link Scanners#FieldsAnnotated} instead</i>
 *
 * @author Administrator
 */
@Deprecated
public class FieldAnnotationsResourceScanner extends AbstractResourceScanner {

    /**
     * <i>{@code Deprecated}, use {@link Scanners#FieldsAnnotated} instead</i>
     */
    @Deprecated
    public FieldAnnotationsResourceScanner() {
        super(Scanners.FieldsAnnotated);
    }
}
