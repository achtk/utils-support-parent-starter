package com.chua.common.support.reflection.reflections.scanners;

/**
 * Not supported since 0.10, will be removed.
 * <p></p><i>{@code Deprecated}, use instead:
 * <ul>
 *  <li>{@link Scanners#MethodsParameter}</li>
 *  <li>{@link Scanners#MethodsSignature}</li>
 *  <li>{@link Scanners#MethodsReturn}</li>
 *  <li>{@link Scanners#ConstructorsParameter}</li>
 *  <li>{@link Scanners#ConstructorsSignature}</li>
 * </ul>
 *
 * @author Administrator
 */
@Deprecated
public class MethodParameterResourceScanner extends AbstractResourceScanner {

    /**
     * Not supported since 0.10, will be removed.
     * <p></p><i>{@code Deprecated}, use instead:
     * <ul>
     *  <li>{@link Scanners#MethodsParameter}</li>
     *  <li>{@link Scanners#MethodsSignature}</li>
     *  <li>{@link Scanners#MethodsReturn}</li>
     *  <li>{@link Scanners#ConstructorsParameter}</li>
     *  <li>{@link Scanners#ConstructorsSignature}</li>
     * </ul>
     */
    @Deprecated
    public MethodParameterResourceScanner() {
        super(Scanners.MethodsParameter);
    }
}
