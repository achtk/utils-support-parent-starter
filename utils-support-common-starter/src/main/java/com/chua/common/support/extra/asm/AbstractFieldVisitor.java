package com.chua.common.support.extra.asm;

/**
 * A visitor to visit a Java field. The methods of this class must be called in the following order:
 * ( {@code visitAnnotation} | {@code visitTypeAnnotation} | {@code visitAttribute} )* {@code
 * visitEnd}.
 *
 * @author Eric Bruneton
 */
public abstract class AbstractFieldVisitor {

  /**
   * The ASM API version implemented by this visitor. The value of this field must be one of the
   * {@code ASM}<i>x</i> values in {@link Opcodes}.
   */
  protected final int api;

  /** The field visitor to which this visitor must delegate method calls. May be {@literal null}. */
  protected AbstractFieldVisitor fv;

  /**
   * Constructs a new {@link AbstractFieldVisitor}.
   *
   * @param api the ASM API version implemented by this visitor. Must be one of the {@code
   *     ASM}<i>x</i> values in {@link Opcodes}.
   */
  protected AbstractFieldVisitor(final int api) {
    this(api, null);
  }

  /**
   * Constructs a new {@link AbstractFieldVisitor}.
   *
   * @param api the ASM API version implemented by this visitor. Must be one of the {@code
   *     ASM}<i>x</i> values in {@link Opcodes}.
   * @param fieldVisitor the field visitor to which this visitor must delegate method calls. May be
   *     null.
   */
  protected AbstractFieldVisitor(final int api, final AbstractFieldVisitor fieldVisitor) {
    if (api != Opcodes.ASM9
        && api != Opcodes.ASM8
        && api != Opcodes.ASM7
        && api != Opcodes.ASM6
        && api != Opcodes.ASM5
        && api != Opcodes.ASM4
        && api != Opcodes.ASM10_EXPERIMENTAL) {
      throw new IllegalArgumentException("Unsupported api " + api);
    }
    // SPRING PATCH: no preview mode check for ASM experimental
    this.api = api;
    this.fv = fieldVisitor;
  }

  /**
   * Visits an annotation of the field.
   *
   * @param descriptor the class descriptor of the annotation class.
   * @param visible {@literal true} if the annotation is visible at runtime.
   * @return a visitor to visit the annotation values, or {@literal null} if this visitor is not
   *     interested in visiting this annotation.
   */
  public AbstractAnnotationVisitor visitAnnotation(final String descriptor, final boolean visible) {
    if (fv != null) {
      return fv.visitAnnotation(descriptor, visible);
    }
    return null;
  }

  /**
   * Visits an annotation on the type of the field.
   *
   * @param typeRef a reference to the annotated type. The sort of this type reference must be
   *     {@link TypeReference#FIELD}. See {@link TypeReference}.
   * @param typePath the path to the annotated type argument, wildcard bound, array element type, or
   *     static inner type within 'typeRef'. May be {@literal null} if the annotation targets
   *     'typeRef' as a whole.
   * @param descriptor the class descriptor of the annotation class.
   * @param visible {@literal true} if the annotation is visible at runtime.
   * @return a visitor to visit the annotation values, or {@literal null} if this visitor is not
   *     interested in visiting this annotation.
   */
  public AbstractAnnotationVisitor visitTypeAnnotation(
      final int typeRef, final TypePath typePath, final String descriptor, final boolean visible) {
    if (api < Opcodes.ASM5) {
      throw new UnsupportedOperationException("This feature requires ASM5");
    }
    if (fv != null) {
      return fv.visitTypeAnnotation(typeRef, typePath, descriptor, visible);
    }
    return null;
  }

  /**
   * Visits a non standard attribute of the field.
   *
   * @param attribute an attribute.
   */
  public void visitAttribute(final Attribute attribute) {
    if (fv != null) {
      fv.visitAttribute(attribute);
    }
  }

  /**
   * Visits the end of the field. This method, which is the last one to be called, is used to inform
   * the visitor that all the annotations and attributes of the field have been visited.
   */
  public void visitEnd() {
    if (fv != null) {
      fv.visitEnd();
    }
  }
}
