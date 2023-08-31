package com.chua.common.support.extra.asm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import static com.chua.common.support.constant.CommonConstant.TWE;
import static com.chua.common.support.constant.CommonConstant.*;
import static com.chua.common.support.constant.NumberConstant.*;

/**
 * A parser to make a {@link AbstractClassVisitor} visit a ClassFile structure, as defined in the Java
 * Virtual Machine Specification (JVMS). This class parses the ClassFile content and calls the
 * appropriate visit methods of a given {@link AbstractClassVisitor} for each field, method and bytecode
 * instruction encountered.
 *
 * @author Eugene Kuleshov
 * @see <a href="https: * @author Eric Bruneton
 */
public class ClassReader {

    /**
     * A flag to skip the Code attributes. If this flag is set the Code attributes are neither parsed
     * nor visited.
     */
    public static final int SKIP_CODE = 1;

    /**
     * A flag to skip the SourceFile, SourceDebugExtension, LocalVariableTable,
     * LocalVariableTypeTable, LineNumberTable and MethodParameters attributes. If this flag is set
     * these attributes are neither parsed nor visited (i.e. {@link AbstractClassVisitor#visitSource}, {@link
     * BaseMethodVisitor#visitLocalVariable}, {@link BaseMethodVisitor#visitLineNumber} and {@link
     * BaseMethodVisitor#visitParameter} are not called).
     */
    public static final int SKIP_DEBUG = 2;

    /**
     * A flag to skip the StackMap and StackMapTable attributes. If this flag is set these attributes
     * are neither parsed nor visited (i.e. {@link BaseMethodVisitor#visitFrame} is not called). This flag
     * is useful when the {@link AbstractClassWriter#COMPUTE_FRAMES} option is used: it avoids visiting frames
     * that will be ignored and recomputed from scratch.
     */
    public static final int SKIP_FRAMES = 4;

    /**
     * A flag to expand the stack map frames. By default stack map frames are visited in their
     * original format (i.e. "expanded" for classes whose version is less than V1_6, and "compressed"
     * for the other classes). If this flag is set, stack map frames are always visited in expanded
     * format (this option adds a decompression/compression step in ClassReader and ClassWriter which
     * degrades performance quite a lot).
     */
    public static final int EXPAND_FRAMES = 8;

    /**
     * A flag to expand the ASM specific instructions into an equivalent sequence of standard bytecode
     * instructions. When resolving a forward jump it may happen that the signed 2 bytes offset
     * reserved for it is not sufficient to store the bytecode offset. In this case the jump
     * instruction is replaced with a temporary ASM specific instruction using an unsigned 2 bytes
     * offset (see {@link Label#resolve}). This internal flag is used to re-read classes containing
     * such instructions, in order to replace them with standard instructions. In addition, when this
     * flag is used, goto_w and jsr_w are <i>not</i> converted into goto and jsr, to make sure that
     * infinite loops where a goto_w is replaced with a goto in ClassReader and converted back to a
     * goto_w in ClassWriter cannot occur.
     */
    static final int EXPAND_ASM_INSNS = 256;

    /**
     * The maximum size of array to allocate.
     */
    private static final int MAX_BUFFER_SIZE = 1024 * 1024;

    /**
     * The size of the temporary byte array used to read class input streams chunk by chunk.
     */
    private static final int INPUT_STREAM_DATA_CHUNK_SIZE = 4096;

    /**
     * A byte array containing the JVMS ClassFile structure to be parsed.
     *
     * @deprecated Use {@link #readByte(int)} and the other read methods instead. This field will
     * eventually be deleted.
     */
    @Deprecated
    public final byte[] b;

    /**
     * The offset in bytes of the ClassFile's access_flags field.
     */
    public final int header;

    /**
     * A byte array containing the JVMS ClassFile structure to be parsed. <i>The content of this array
     * must not be modified. This field is intended for {@link Attribute} sub classes, and is normally
     * not needed by class visitors.</i>
     *
     * <p>NOTE: the ClassFile structure can start at any offset within this array, i.e. it does not
     * necessarily start at offset 0. Use {@link #getItem} and {@link #header} to get correct
     * ClassFile element offsets within this byte array.
     */
    final byte[] classFileBuffer;

    /**
     * The offset in bytes, in {@link #classFileBuffer}, of each cp_info entry of the ClassFile's
     * constant_pool array, <i>plus one</i>. In other words, the offset of constant pool entry i is
     * given by cpInfoOffsets[i] - 1, i.e. its cp_info's tag field is given by b[cpInfoOffsets[i] -
     * 1].
     */
    private final int[] cpInfoOffsets;

    /**
     * The String objects corresponding to the CONSTANT_Utf8 constant pool items. This cache avoids
     * multiple parsing of a given CONSTANT_Utf8 constant pool item.
     */
    private final String[] constantUtf8Values;

    /**
     * The ConstantDynamic objects corresponding to the CONSTANT_Dynamic constant pool items. This
     * cache avoids multiple parsing of a given CONSTANT_Dynamic constant pool item.
     */
    private final ConstantDynamic[] constantDynamicValues;

    /**
     * The start offsets in {@link #classFileBuffer} of each element of the bootstrap_methods array
     * (in the BootstrapMethods attribute).
     *
     * @see <a href="https:   *     4.7.23</a>
     */
    private final int[] bootstrapMethodOffsets;

    /**
     * A conservative estimate of the maximum length of the strings contained in the constant pool of
     * the class.
     */
    private final int maxStringLength;


    /**
     * Constructs a new {@link ClassReader} object.
     *
     * @param classFile the JVMS ClassFile structure to be read.
     */
    public ClassReader(final byte[] classFile) {
        this(classFile, 0, classFile.length);
    }

    /**
     * Constructs a new {@link ClassReader} object.
     *
     * @param classFileBuffer a byte array containing the JVMS ClassFile structure to be read.
     * @param classFileOffset the offset in byteBuffer of the first byte of the ClassFile to be read.
     * @param classFileLength the length in bytes of the ClassFile to be read.
     */
    public ClassReader(
            final byte[] classFileBuffer,
            final int classFileOffset,
            final int classFileLength) {
        this(classFileBuffer, classFileOffset, true);
    }

    /**
     * Constructs a new {@link ClassReader} object. <i>This internal constructor must not be exposed
     * as a public API</i>.
     *
     * @param classFileBuffer   a byte array containing the JVMS ClassFile structure to be read.
     * @param classFileOffset   the offset in byteBuffer of the first byte of the ClassFile to be read.
     * @param checkClassVersion whether to check the class version or not.
     */
    ClassReader(
            final byte[] classFileBuffer, final int classFileOffset, final boolean checkClassVersion) {
        this.classFileBuffer = classFileBuffer;
        this.b = classFileBuffer;
        if (checkClassVersion && readShort(classFileOffset + SIX) > Opcodes.V19) {
            throw new IllegalArgumentException(
                    "Unsupported class file major version " + readShort(classFileOffset + 6));
        }
        int constantPoolCount = readUnsignedShort(classFileOffset + 8);
        cpInfoOffsets = new int[constantPoolCount];
        constantUtf8Values = new String[constantPoolCount];
        int currentCpInfoIndex = 1;
        int currentCpInfoOffset = classFileOffset + 10;
        int currentMaxStringLength = 0;
        boolean hasBootstrapMethods = false;
        boolean hasConstantDynamic = false;
        while (currentCpInfoIndex < constantPoolCount) {
            cpInfoOffsets[currentCpInfoIndex++] = currentCpInfoOffset + 1;
            int cpInfoSize;
            switch (classFileBuffer[currentCpInfoOffset]) {
                case BaseSymbol.CONSTANT_FIELDREF_TAG:
                case BaseSymbol.CONSTANT_METHODREF_TAG:
                case BaseSymbol.CONSTANT_INTERFACE_METHODREF_TAG:
                case BaseSymbol.CONSTANT_INTEGER_TAG:
                case BaseSymbol.CONSTANT_FLOAT_TAG:
                case BaseSymbol.CONSTANT_NAME_AND_TYPE_TAG:
                    cpInfoSize = 5;
                    break;
                case BaseSymbol.CONSTANT_DYNAMIC_TAG:
                    cpInfoSize = 5;
                    hasBootstrapMethods = true;
                    hasConstantDynamic = true;
                    break;
                case BaseSymbol.CONSTANT_INVOKE_DYNAMIC_TAG:
                    cpInfoSize = 5;
                    hasBootstrapMethods = true;
                    break;
                case BaseSymbol.CONSTANT_LONG_TAG:
                case BaseSymbol.CONSTANT_DOUBLE_TAG:
                    cpInfoSize = 9;
                    currentCpInfoIndex++;
                    break;
                case BaseSymbol.CONSTANT_UTF8_TAG:
                    cpInfoSize = 3 + readUnsignedShort(currentCpInfoOffset + 1);
                    if (cpInfoSize > currentMaxStringLength) {
                        currentMaxStringLength = cpInfoSize;
                    }
                    break;
                case BaseSymbol.CONSTANT_METHOD_HANDLE_TAG:
                    cpInfoSize = 4;
                    break;
                case BaseSymbol.CONSTANT_CLASS_TAG:
                case BaseSymbol.CONSTANT_STRING_TAG:
                case BaseSymbol.CONSTANT_METHOD_TYPE_TAG:
                case BaseSymbol.CONSTANT_PACKAGE_TAG:
                case BaseSymbol.CONSTANT_MODULE_TAG:
                    cpInfoSize = 3;
                    break;
                default:
                    throw new IllegalArgumentException();
            }
            currentCpInfoOffset += cpInfoSize;
        }
        maxStringLength = currentMaxStringLength;
        header = currentCpInfoOffset;

        constantDynamicValues = hasConstantDynamic ? new ConstantDynamic[constantPoolCount] : null;

        bootstrapMethodOffsets =
                hasBootstrapMethods ? readBootstrapMethodsAttribute(currentMaxStringLength) : null;
    }

    /**
     * Constructs a new {@link ClassReader} object.
     *
     * @param inputStream an input stream of the JVMS ClassFile structure to be read. This input
     *                    stream must contain nothing more than the ClassFile structure itself. It is read from its
     *                    current position to its end.
     * @throws IOException if a problem occurs during reading.
     */
    public ClassReader(final InputStream inputStream) throws IOException {
        this(readStream(inputStream, false));
    }

    /**
     * Constructs a new {@link ClassReader} object.
     *
     * @param className the fully qualified name of the class to be read. The ClassFile structure is
     *                  retrieved with the current class loader's {@link ClassLoader#getSystemResourceAsStream}.
     * @throws IOException if an exception occurs during reading.
     */
    public ClassReader(final String className) throws IOException {
        this(
                readStream(
                        ClassLoader.getSystemResourceAsStream(className.replace('.', '/') + ".class"), true));
    }

    /**
     * Reads the given input stream and returns its content as a byte array.
     *
     * @param inputStream an input stream.
     * @param close       true to close the input stream after reading.
     * @return the content of the given input stream.
     * @throws IOException if a problem occurs during reading.
     */
    private static byte[] readStream(final InputStream inputStream, final boolean close)
            throws IOException {
        if (inputStream == null) {
            throw new IOException("Class not found");
        }
        int bufferSize = computeBufferSize(inputStream);
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            byte[] data = new byte[bufferSize];
            int bytesRead;
            int readCount = 0;
            while ((bytesRead = inputStream.read(data, 0, bufferSize)) != -1) {
                outputStream.write(data, 0, bytesRead);
                readCount++;
            }
            outputStream.flush();
            if (readCount == 1) {
            }
            return outputStream.toByteArray();
        } finally {
            if (close) {
                inputStream.close();
            }
        }
    }

    private static int computeBufferSize(final InputStream inputStream) throws IOException {
        int expectedLength = inputStream.available();
        /*
         * Some implementations can return 0 while holding available data (e.g. new
         * FileInputStream("/proc/a_file")). Also in some pathological cases a very small number might
         * be returned, and in this case we use a default size.
         */
        if (expectedLength < NUM_256) {
            return INPUT_STREAM_DATA_CHUNK_SIZE;
        }
        return Math.min(expectedLength, MAX_BUFFER_SIZE);
    }


    /**
     * Returns the class's access flags (see {@link Opcodes}). This value may not reflect Deprecated
     * and Synthetic flags when bytecode is before 1.5 and those flags are represented by attributes.
     *
     * @return the class access flags.
     * @see AbstractClassVisitor#visit(int, int, String, String, String, String[])
     */
    public int getAccess() {
        return readUnsignedShort(header);
    }

    /**
     * Returns the internal name of the class (see {@link Type#getInternalName()}).
     *
     * @return the internal class name.
     * @see AbstractClassVisitor#visit(int, int, String, String, String, String[])
     */
    public String getClassName() {
        return readClass(header + 2, new char[maxStringLength]);
    }

    /**
     * Returns the internal of name of the super class (see {@link Type#getInternalName()}). For
     * interfaces, the super class is {@link Object}.
     *
     * @return the internal name of the super class, or {@literal null} for {@link Object} class.
     * @see AbstractClassVisitor#visit(int, int, String, String, String, String[])
     */
    public String getSuperName() {
        return readClass(header + 4, new char[maxStringLength]);
    }

    /**
     * Returns the internal names of the implemented interfaces (see {@link Type#getInternalName()}).
     *
     * @return the internal names of the directly implemented interfaces. Inherited implemented
     * interfaces are not returned.
     * @see AbstractClassVisitor#visit(int, int, String, String, String, String[])
     */
    public String[] getInterfaces() {
        int currentOffset = header + 6;
        int interfacesCount = readUnsignedShort(currentOffset);
        String[] interfaces = new String[interfacesCount];
        if (interfacesCount > 0) {
            char[] charBuffer = new char[maxStringLength];
            for (int i = 0; i < interfacesCount; ++i) {
                currentOffset += 2;
                interfaces[i] = readClass(currentOffset, charBuffer);
            }
        }
        return interfaces;
    }


    /**
     * Makes the given visitor visit the JVMS ClassFile structure passed to the constructor of this
     * {@link ClassReader}.
     *
     * @param abstractClassVisitor the visitor that must visit this class.
     * @param parsingOptions       the options to use to parse this class. One or more of {@link
     *                             #SKIP_CODE}, {@link #SKIP_DEBUG}, {@link #SKIP_FRAMES} or {@link #EXPAND_FRAMES}.
     */
    public void accept(final AbstractClassVisitor abstractClassVisitor, final int parsingOptions) {
        accept(abstractClassVisitor, new Attribute[0], parsingOptions);
    }

    /**
     * Makes the given visitor visit the JVMS ClassFile structure passed to the constructor of this
     * {@link ClassReader}.
     *
     * @param abstractClassVisitor the visitor that must visit this class.
     * @param attributePrototypes  prototypes of the attributes that must be parsed during the visit of
     *                             the class. Any attribute whose type is not equal to the type of one the prototypes will not
     *                             be parsed: its byte array value will be passed unchanged to the ClassWriter. <i>This may
     *                             corrupt it if this value contains references to the constant pool, or has syntactic or
     *                             semantic links with a class element that has been transformed by a class adapter between
     *                             the reader and the writer</i>.
     * @param parsingOptions       the options to use to parse this class. One or more of {@link
     *                             #SKIP_CODE}, {@link #SKIP_DEBUG}, {@link #SKIP_FRAMES} or {@link #EXPAND_FRAMES}.
     */
    public void accept(
            final AbstractClassVisitor abstractClassVisitor,
            final Attribute[] attributePrototypes,
            final int parsingOptions) {
        Context context = new Context();
        context.attributePrototypes = attributePrototypes;
        context.parsingOptions = parsingOptions;
        context.charBuffer = new char[maxStringLength];

        char[] charBuffer = context.charBuffer;
        int currentOffset = header;
        int accessFlags = readUnsignedShort(currentOffset);
        String thisClass = readClass(currentOffset + 2, charBuffer);
        String superClass = readClass(currentOffset + 4, charBuffer);
        String[] interfaces = new String[readUnsignedShort(currentOffset + 6)];
        currentOffset += 8;
        for (int i = 0; i < interfaces.length; ++i) {
            interfaces[i] = readClass(currentOffset, charBuffer);
            currentOffset += 2;
        }

        int innerClassesOffset = 0;
        int enclosingMethodOffset = 0;
        String signature = null;
        String sourceFile = null;
        String sourceDebugExtension = null;
        int runtimeVisibleAnnotationsOffset = 0;
        int runtimeInvisibleAnnotationsOffset = 0;
        int runtimeVisibleTypeAnnotationsOffset = 0;
        int runtimeInvisibleTypeAnnotationsOffset = 0;
        int moduleOffset = 0;
        int modulePackagesOffset = 0;
        String moduleMainClass = null;
        String nestHostClass = null;
        int nestMembersOffset = 0;
        int permittedSubclassesOffset = 0;
        int recordOffset = 0;
        Attribute attributes = null;

        int currentAttributeOffset = getFirstAttributeOffset();
        for (int i = readUnsignedShort(currentAttributeOffset - TWE); i > 0; --i) {
            String attributeName = readUtf8(currentAttributeOffset, charBuffer);
            int attributeLength = readInt(currentAttributeOffset + TWE);
            currentAttributeOffset += 6;
            if (Constants.SOURCE_FILE.equals(attributeName)) {
                sourceFile = readUtf8(currentAttributeOffset, charBuffer);
            } else if (Constants.INNER_CLASSES.equals(attributeName)) {
                innerClassesOffset = currentAttributeOffset;
            } else if (Constants.ENCLOSING_METHOD.equals(attributeName)) {
                enclosingMethodOffset = currentAttributeOffset;
            } else if (Constants.NEST_HOST.equals(attributeName)) {
                nestHostClass = readClass(currentAttributeOffset, charBuffer);
            } else if (Constants.NEST_MEMBERS.equals(attributeName)) {
                nestMembersOffset = currentAttributeOffset;
            } else if (Constants.PERMITTED_SUBCLASSES.equals(attributeName)) {
                permittedSubclassesOffset = currentAttributeOffset;
            } else if (Constants.SIGNATURE.equals(attributeName)) {
                signature = readUtf8(currentAttributeOffset, charBuffer);
            } else if (Constants.RUNTIME_VISIBLE_ANNOTATIONS.equals(attributeName)) {
                runtimeVisibleAnnotationsOffset = currentAttributeOffset;
            } else if (Constants.RUNTIME_VISIBLE_TYPE_ANNOTATIONS.equals(attributeName)) {
                runtimeVisibleTypeAnnotationsOffset = currentAttributeOffset;
            } else if (Constants.DEPRECATED.equals(attributeName)) {
                accessFlags |= Opcodes.ACC_DEPRECATED;
            } else if (Constants.SYNTHETIC.equals(attributeName)) {
                accessFlags |= Opcodes.ACC_SYNTHETIC;
            } else if (Constants.SOURCE_DEBUG_EXTENSION.equals(attributeName)) {
                if (attributeLength > classFileBuffer.length - currentAttributeOffset) {
                    throw new IllegalArgumentException();
                }
                sourceDebugExtension =
                        readUtf(currentAttributeOffset, attributeLength, new char[attributeLength]);
            } else if (Constants.RUNTIME_INVISIBLE_ANNOTATIONS.equals(attributeName)) {
                runtimeInvisibleAnnotationsOffset = currentAttributeOffset;
            } else if (Constants.RUNTIME_INVISIBLE_TYPE_ANNOTATIONS.equals(attributeName)) {
                runtimeInvisibleTypeAnnotationsOffset = currentAttributeOffset;
            } else if (Constants.RECORD.equals(attributeName)) {
                recordOffset = currentAttributeOffset;
                accessFlags |= Opcodes.ACC_RECORD;
            } else if (Constants.MODULE.equals(attributeName)) {
                moduleOffset = currentAttributeOffset;
            } else if (Constants.MODULE_MAIN_CLASS.equals(attributeName)) {
                moduleMainClass = readClass(currentAttributeOffset, charBuffer);
            } else if (Constants.MODULE_PACKAGES.equals(attributeName)) {
                modulePackagesOffset = currentAttributeOffset;
            } else if (!Constants.BOOTSTRAP_METHODS.equals(attributeName)) {
                Attribute attribute =
                        readAttribute(
                                attributePrototypes,
                                attributeName,
                                currentAttributeOffset,
                                attributeLength,
                                charBuffer,
                                -1,
                                null);
                attribute.nextAttribute = attributes;
                attributes = attribute;
            }
            currentAttributeOffset += attributeLength;
        }

        abstractClassVisitor.visit(
                readInt(cpInfoOffsets[1] - 7), accessFlags, thisClass, signature, superClass, interfaces);

        boolean b = (parsingOptions & SKIP_DEBUG) == 0
                && (sourceFile != null || sourceDebugExtension != null);
        if (b) {
            abstractClassVisitor.visitSource(sourceFile, sourceDebugExtension);
        }

        if (moduleOffset != 0) {
            readModuleAttributes(
                    abstractClassVisitor, context, moduleOffset, modulePackagesOffset, moduleMainClass);
        }

        if (nestHostClass != null) {
            abstractClassVisitor.visitNestHost(nestHostClass);
        }

        if (enclosingMethodOffset != 0) {
            String className = readClass(enclosingMethodOffset, charBuffer);
            int methodIndex = readUnsignedShort(enclosingMethodOffset + 2);
            String name = methodIndex == 0 ? null : readUtf8(cpInfoOffsets[methodIndex], charBuffer);
            String type = methodIndex == 0 ? null : readUtf8(cpInfoOffsets[methodIndex] + 2, charBuffer);
            abstractClassVisitor.visitOuterClass(className, name, type);
        }

        if (runtimeVisibleAnnotationsOffset != 0) {
            int numAnnotations = readUnsignedShort(runtimeVisibleAnnotationsOffset);
            int currentAnnotationOffset = runtimeVisibleAnnotationsOffset + 2;
            while (numAnnotations-- > 0) {
                String annotationDescriptor = readUtf8(currentAnnotationOffset, charBuffer);
                currentAnnotationOffset += 2;
                currentAnnotationOffset =
                        readElementValues(
                                abstractClassVisitor.visitAnnotation(annotationDescriptor, true),
                                currentAnnotationOffset,
                                true,
                                charBuffer);
            }
        }

        if (runtimeInvisibleAnnotationsOffset != 0) {
            int numAnnotations = readUnsignedShort(runtimeInvisibleAnnotationsOffset);
            int currentAnnotationOffset = runtimeInvisibleAnnotationsOffset + 2;
            while (numAnnotations-- > 0) {
                String annotationDescriptor = readUtf8(currentAnnotationOffset, charBuffer);
                currentAnnotationOffset += 2;
                currentAnnotationOffset =
                        readElementValues(
                                abstractClassVisitor.visitAnnotation(annotationDescriptor, false),
                                currentAnnotationOffset,
                                true,
                                charBuffer);
            }
        }

        if (runtimeVisibleTypeAnnotationsOffset != 0) {
            int numAnnotations = readUnsignedShort(runtimeVisibleTypeAnnotationsOffset);
            int currentAnnotationOffset = runtimeVisibleTypeAnnotationsOffset + 2;
            while (numAnnotations-- > 0) {
                currentAnnotationOffset = readTypeAnnotationTarget(context, currentAnnotationOffset);
                String annotationDescriptor = readUtf8(currentAnnotationOffset, charBuffer);
                currentAnnotationOffset += 2;
                currentAnnotationOffset =
                        readElementValues(
                                abstractClassVisitor.visitTypeAnnotation(
                                        context.currentTypeAnnotationTarget,
                                        context.currentTypeAnnotationTargetPath,
                                        annotationDescriptor,
                                        true),
                                currentAnnotationOffset,
                                true,
                                charBuffer);
            }
        }

        if (runtimeInvisibleTypeAnnotationsOffset != 0) {
            int numAnnotations = readUnsignedShort(runtimeInvisibleTypeAnnotationsOffset);
            int currentAnnotationOffset = runtimeInvisibleTypeAnnotationsOffset + 2;
            while (numAnnotations-- > 0) {
                currentAnnotationOffset = readTypeAnnotationTarget(context, currentAnnotationOffset);
                String annotationDescriptor = readUtf8(currentAnnotationOffset, charBuffer);
                currentAnnotationOffset += 2;
                currentAnnotationOffset =
                        readElementValues(
                                abstractClassVisitor.visitTypeAnnotation(
                                        context.currentTypeAnnotationTarget,
                                        context.currentTypeAnnotationTargetPath,
                                        annotationDescriptor,
                                        false),
                                currentAnnotationOffset,
                                true,
                                charBuffer);
            }
        }

        while (attributes != null) {
            Attribute nextAttribute = attributes.nextAttribute;
            attributes.nextAttribute = null;
            abstractClassVisitor.visitAttribute(attributes);
            attributes = nextAttribute;
        }

        if (nestMembersOffset != 0) {
            int numberOfNestMembers = readUnsignedShort(nestMembersOffset);
            int currentNestMemberOffset = nestMembersOffset + 2;
            while (numberOfNestMembers-- > 0) {
                abstractClassVisitor.visitNestMember(readClass(currentNestMemberOffset, charBuffer));
                currentNestMemberOffset += 2;
            }
        }

        if (permittedSubclassesOffset != 0) {
            int numberOfPermittedSubclasses = readUnsignedShort(permittedSubclassesOffset);
            int currentPermittedSubclassesOffset = permittedSubclassesOffset + 2;
            while (numberOfPermittedSubclasses-- > 0) {
                abstractClassVisitor.visitPermittedSubclass(
                        readClass(currentPermittedSubclassesOffset, charBuffer));
                currentPermittedSubclassesOffset += 2;
            }
        }

        if (innerClassesOffset != 0) {
            int numberOfClasses = readUnsignedShort(innerClassesOffset);
            int currentClassesOffset = innerClassesOffset + 2;
            while (numberOfClasses-- > 0) {
                abstractClassVisitor.visitInnerClass(
                        readClass(currentClassesOffset, charBuffer),
                        readClass(currentClassesOffset + 2, charBuffer),
                        readUtf8(currentClassesOffset + 4, charBuffer),
                        readUnsignedShort(currentClassesOffset + 6));
                currentClassesOffset += 8;
            }
        }

        if (recordOffset != 0) {
            int recordComponentsCount = readUnsignedShort(recordOffset);
            recordOffset += 2;
            while (recordComponentsCount-- > 0) {
                recordOffset = readRecordComponent(abstractClassVisitor, context, recordOffset);
            }
        }

        int fieldsCount = readUnsignedShort(currentOffset);
        currentOffset += 2;
        while (fieldsCount-- > 0) {
            currentOffset = readField(abstractClassVisitor, context, currentOffset);
        }
        int methodsCount = readUnsignedShort(currentOffset);
        currentOffset += 2;
        while (methodsCount-- > 0) {
            currentOffset = readMethod(abstractClassVisitor, context, currentOffset);
        }

        abstractClassVisitor.visitEnd();
    }


    /**
     * Reads the Module, ModulePackages and ModuleMainClass attributes and visit them.
     *
     * @param abstractClassVisitor         the current class visitor
     * @param context              information about the class being parsed.
     * @param moduleOffset         the offset of the Module attribute (excluding the attribute_info's
     *                             attribute_name_index and attribute_length fields).
     * @param modulePackagesOffset the offset of the ModulePackages attribute (excluding the
     *                             attribute_info's attribute_name_index and attribute_length fields), or 0.
     * @param moduleMainClass      the string corresponding to the ModuleMainClass attribute, or {@literal
     *                             null}.
     */
    private void readModuleAttributes(
            final AbstractClassVisitor abstractClassVisitor,
            final Context context,
            final int moduleOffset,
            final int modulePackagesOffset,
            final String moduleMainClass) {
        char[] buffer = context.charBuffer;

        int currentOffset = moduleOffset;
        String moduleName = readModule(currentOffset, buffer);
        int moduleFlags = readUnsignedShort(currentOffset + 2);
        String moduleVersion = readUtf8(currentOffset + 4, buffer);
        currentOffset += 6;
        BaseModuleVisitor moduleVisitor = abstractClassVisitor.visitModule(moduleName, moduleFlags, moduleVersion);
        if (moduleVisitor == null) {
            return;
        }

        if (moduleMainClass != null) {
            moduleVisitor.visitMainClass(moduleMainClass);
        }

        if (modulePackagesOffset != 0) {
            int packageCount = readUnsignedShort(modulePackagesOffset);
            int currentPackageOffset = modulePackagesOffset + 2;
            while (packageCount-- > 0) {
                moduleVisitor.visitPackage(readPackage(currentPackageOffset, buffer));
                currentPackageOffset += 2;
            }
        }

        int requiresCount = readUnsignedShort(currentOffset);
        currentOffset += 2;
        while (requiresCount-- > 0) {
            String requires = readModule(currentOffset, buffer);
            int requiresFlags = readUnsignedShort(currentOffset + 2);
            String requiresVersion = readUtf8(currentOffset + 4, buffer);
            currentOffset += 6;
            moduleVisitor.visitRequire(requires, requiresFlags, requiresVersion);
        }

        int exportsCount = readUnsignedShort(currentOffset);
        currentOffset += 2;
        while (exportsCount-- > 0) {
            String exports = readPackage(currentOffset, buffer);
            int exportsFlags = readUnsignedShort(currentOffset + 2);
            int exportsToCount = readUnsignedShort(currentOffset + 4);
            currentOffset += 6;
            String[] exportsTo = null;
            if (exportsToCount != 0) {
                exportsTo = new String[exportsToCount];
                for (int i = 0; i < exportsToCount; ++i) {
                    exportsTo[i] = readModule(currentOffset, buffer);
                    currentOffset += 2;
                }
            }
            moduleVisitor.visitExport(exports, exportsFlags, exportsTo);
        }

        int opensCount = readUnsignedShort(currentOffset);
        currentOffset += 2;
        while (opensCount-- > 0) {
            String opens = readPackage(currentOffset, buffer);
            int opensFlags = readUnsignedShort(currentOffset + 2);
            int opensToCount = readUnsignedShort(currentOffset + 4);
            currentOffset += 6;
            String[] opensTo = null;
            if (opensToCount != 0) {
                opensTo = new String[opensToCount];
                for (int i = 0; i < opensToCount; ++i) {
                    opensTo[i] = readModule(currentOffset, buffer);
                    currentOffset += 2;
                }
            }
            moduleVisitor.visitOpen(opens, opensFlags, opensTo);
        }

        int usesCount = readUnsignedShort(currentOffset);
        currentOffset += 2;
        while (usesCount-- > 0) {
            moduleVisitor.visitUse(readClass(currentOffset, buffer));
            currentOffset += 2;
        }

        int providesCount = readUnsignedShort(currentOffset);
        currentOffset += 2;
        while (providesCount-- > 0) {
            String provides = readClass(currentOffset, buffer);
            int providesWithCount = readUnsignedShort(currentOffset + 2);
            currentOffset += 4;
            String[] providesWith = new String[providesWithCount];
            for (int i = 0; i < providesWithCount; ++i) {
                providesWith[i] = readClass(currentOffset, buffer);
                currentOffset += 2;
            }
            moduleVisitor.visitProvide(provides, providesWith);
        }

        moduleVisitor.visitEnd();
    }

    /**
     * Reads a record component and visit it.
     *
     * @param abstractClassVisitor  the current class visitor
     * @param context               information about the class being parsed.
     * @param recordComponentOffset the offset of the current record component.
     * @return the offset of the first byte following the record component.
     */
    private int readRecordComponent(
            final AbstractClassVisitor abstractClassVisitor, final Context context, final int recordComponentOffset) {
        char[] charBuffer = context.charBuffer;

        int currentOffset = recordComponentOffset;
        String name = readUtf8(currentOffset, charBuffer);
        String descriptor = readUtf8(currentOffset + 2, charBuffer);
        currentOffset += 4;


        String signature = null;
        int runtimeVisibleAnnotationsOffset = 0;
        int runtimeInvisibleAnnotationsOffset = 0;
        int runtimeVisibleTypeAnnotationsOffset = 0;
        int runtimeInvisibleTypeAnnotationsOffset = 0;
        Attribute attributes = null;

        int attributesCount = readUnsignedShort(currentOffset);
        currentOffset += 2;
        while (attributesCount-- > 0) {
            String attributeName = readUtf8(currentOffset, charBuffer);
            int attributeLength = readInt(currentOffset + 2);
            currentOffset += 6;
            if (Constants.SIGNATURE.equals(attributeName)) {
                signature = readUtf8(currentOffset, charBuffer);
            } else if (Constants.RUNTIME_VISIBLE_ANNOTATIONS.equals(attributeName)) {
                runtimeVisibleAnnotationsOffset = currentOffset;
            } else if (Constants.RUNTIME_VISIBLE_TYPE_ANNOTATIONS.equals(attributeName)) {
                runtimeVisibleTypeAnnotationsOffset = currentOffset;
            } else if (Constants.RUNTIME_INVISIBLE_ANNOTATIONS.equals(attributeName)) {
                runtimeInvisibleAnnotationsOffset = currentOffset;
            } else if (Constants.RUNTIME_INVISIBLE_TYPE_ANNOTATIONS.equals(attributeName)) {
                runtimeInvisibleTypeAnnotationsOffset = currentOffset;
            } else {
                Attribute attribute =
                        readAttribute(
                                context.attributePrototypes,
                                attributeName,
                                currentOffset,
                                attributeLength,
                                charBuffer,
                                -1,
                                null);
                attribute.nextAttribute = attributes;
                attributes = attribute;
            }
            currentOffset += attributeLength;
        }

        BaseRecordComponentVisitor recordComponentVisitor =
                abstractClassVisitor.visitRecordComponent(name, descriptor, signature);
        if (recordComponentVisitor == null) {
            return currentOffset;
        }

        if (runtimeVisibleAnnotationsOffset != 0) {
            int numAnnotations = readUnsignedShort(runtimeVisibleAnnotationsOffset);
            int currentAnnotationOffset = runtimeVisibleAnnotationsOffset + 2;
            while (numAnnotations-- > 0) {
                String annotationDescriptor = readUtf8(currentAnnotationOffset, charBuffer);
                currentAnnotationOffset += 2;
                currentAnnotationOffset =
                        readElementValues(
                                recordComponentVisitor.visitAnnotation(annotationDescriptor, true),
                                currentAnnotationOffset,
                                true,
                                charBuffer);
            }
        }

        if (runtimeInvisibleAnnotationsOffset != 0) {
            int numAnnotations = readUnsignedShort(runtimeInvisibleAnnotationsOffset);
            int currentAnnotationOffset = runtimeInvisibleAnnotationsOffset + 2;
            while (numAnnotations-- > 0) {
                String annotationDescriptor = readUtf8(currentAnnotationOffset, charBuffer);
                currentAnnotationOffset += 2;
                currentAnnotationOffset =
                        readElementValues(
                                recordComponentVisitor.visitAnnotation(annotationDescriptor, false),
                                currentAnnotationOffset,
                                true,
                                charBuffer);
            }
        }

        if (runtimeVisibleTypeAnnotationsOffset != 0) {
            int numAnnotations = readUnsignedShort(runtimeVisibleTypeAnnotationsOffset);
            int currentAnnotationOffset = runtimeVisibleTypeAnnotationsOffset + 2;
            while (numAnnotations-- > 0) {
                currentAnnotationOffset = readTypeAnnotationTarget(context, currentAnnotationOffset);
                String annotationDescriptor = readUtf8(currentAnnotationOffset, charBuffer);
                currentAnnotationOffset += 2;
                currentAnnotationOffset =
                        readElementValues(
                                recordComponentVisitor.visitTypeAnnotation(
                                        context.currentTypeAnnotationTarget,
                                        context.currentTypeAnnotationTargetPath,
                                        annotationDescriptor,
                                        true),
                                currentAnnotationOffset,
                                true,
                                charBuffer);
            }
        }

        if (runtimeInvisibleTypeAnnotationsOffset != 0) {
            int numAnnotations = readUnsignedShort(runtimeInvisibleTypeAnnotationsOffset);
            int currentAnnotationOffset = runtimeInvisibleTypeAnnotationsOffset + 2;
            while (numAnnotations-- > 0) {
                currentAnnotationOffset = readTypeAnnotationTarget(context, currentAnnotationOffset);
                String annotationDescriptor = readUtf8(currentAnnotationOffset, charBuffer);
                currentAnnotationOffset += 2;
                currentAnnotationOffset =
                        readElementValues(
                                recordComponentVisitor.visitTypeAnnotation(
                                        context.currentTypeAnnotationTarget,
                                        context.currentTypeAnnotationTargetPath,
                                        annotationDescriptor,
                                        false),
                                currentAnnotationOffset,
                                true,
                                charBuffer);
            }
        }

        while (attributes != null) {
            Attribute nextAttribute = attributes.nextAttribute;
            attributes.nextAttribute = null;
            recordComponentVisitor.visitAttribute(attributes);
            attributes = nextAttribute;
        }

        recordComponentVisitor.visitEnd();
        return currentOffset;
    }

    /**
     * Reads a JVMS field_info structure and makes the given visitor visit it.
     *
     * @param abstractClassVisitor the visitor that must visit the field.
     * @param context              information about the class being parsed.
     * @param fieldInfoOffset      the start offset of the field_info structure.
     * @return the offset of the first byte following the field_info structure.
     */
    private int readField(
            final AbstractClassVisitor abstractClassVisitor, final Context context, final int fieldInfoOffset) {
        char[] charBuffer = context.charBuffer;

        int currentOffset = fieldInfoOffset;
        int accessFlags = readUnsignedShort(currentOffset);
        String name = readUtf8(currentOffset + 2, charBuffer);
        String descriptor = readUtf8(currentOffset + 4, charBuffer);
        currentOffset += 6;

        Object constantValue = null;
        String signature = null;
        int runtimeVisibleAnnotationsOffset = 0;
        int runtimeInvisibleAnnotationsOffset = 0;
        int runtimeVisibleTypeAnnotationsOffset = 0;
        int runtimeInvisibleTypeAnnotationsOffset = 0;
        Attribute attributes = null;

        int attributesCount = readUnsignedShort(currentOffset);
        currentOffset += 2;
        while (attributesCount-- > 0) {
            String attributeName = readUtf8(currentOffset, charBuffer);
            int attributeLength = readInt(currentOffset + 2);
            currentOffset += 6;
            if (Constants.CONSTANT_VALUE.equals(attributeName)) {
                int constantvalueIndex = readUnsignedShort(currentOffset);
                constantValue = constantvalueIndex == 0 ? null : readConst(constantvalueIndex, charBuffer);
            } else if (Constants.SIGNATURE.equals(attributeName)) {
                signature = readUtf8(currentOffset, charBuffer);
            } else if (Constants.DEPRECATED.equals(attributeName)) {
                accessFlags |= Opcodes.ACC_DEPRECATED;
            } else if (Constants.SYNTHETIC.equals(attributeName)) {
                accessFlags |= Opcodes.ACC_SYNTHETIC;
            } else if (Constants.RUNTIME_VISIBLE_ANNOTATIONS.equals(attributeName)) {
                runtimeVisibleAnnotationsOffset = currentOffset;
            } else if (Constants.RUNTIME_VISIBLE_TYPE_ANNOTATIONS.equals(attributeName)) {
                runtimeVisibleTypeAnnotationsOffset = currentOffset;
            } else if (Constants.RUNTIME_INVISIBLE_ANNOTATIONS.equals(attributeName)) {
                runtimeInvisibleAnnotationsOffset = currentOffset;
            } else if (Constants.RUNTIME_INVISIBLE_TYPE_ANNOTATIONS.equals(attributeName)) {
                runtimeInvisibleTypeAnnotationsOffset = currentOffset;
            } else {
                Attribute attribute =
                        readAttribute(
                                context.attributePrototypes,
                                attributeName,
                                currentOffset,
                                attributeLength,
                                charBuffer,
                                -1,
                                null);
                attribute.nextAttribute = attributes;
                attributes = attribute;
            }
            currentOffset += attributeLength;
        }

        AbstractFieldVisitor fieldVisitor =
                abstractClassVisitor.visitField(accessFlags, name, descriptor, signature, constantValue);
        if (fieldVisitor == null) {
            return currentOffset;
        }

        if (runtimeVisibleAnnotationsOffset != 0) {
            int numAnnotations = readUnsignedShort(runtimeVisibleAnnotationsOffset);
            int currentAnnotationOffset = runtimeVisibleAnnotationsOffset + 2;
            while (numAnnotations-- > 0) {
                String annotationDescriptor = readUtf8(currentAnnotationOffset, charBuffer);
                currentAnnotationOffset += 2;
                currentAnnotationOffset =
                        readElementValues(
                                fieldVisitor.visitAnnotation(annotationDescriptor, true),
                                currentAnnotationOffset,
                                true,
                                charBuffer);
            }
        }

        if (runtimeInvisibleAnnotationsOffset != 0) {
            int numAnnotations = readUnsignedShort(runtimeInvisibleAnnotationsOffset);
            int currentAnnotationOffset = runtimeInvisibleAnnotationsOffset + 2;
            while (numAnnotations-- > 0) {
                String annotationDescriptor = readUtf8(currentAnnotationOffset, charBuffer);
                currentAnnotationOffset += 2;
                currentAnnotationOffset =
                        readElementValues(
                                fieldVisitor.visitAnnotation(annotationDescriptor, false),
                                currentAnnotationOffset,
                                true,
                                charBuffer);
            }
        }

        if (runtimeVisibleTypeAnnotationsOffset != 0) {
            int numAnnotations = readUnsignedShort(runtimeVisibleTypeAnnotationsOffset);
            int currentAnnotationOffset = runtimeVisibleTypeAnnotationsOffset + 2;
            while (numAnnotations-- > 0) {
                currentAnnotationOffset = readTypeAnnotationTarget(context, currentAnnotationOffset);
                String annotationDescriptor = readUtf8(currentAnnotationOffset, charBuffer);
                currentAnnotationOffset += 2;
                currentAnnotationOffset =
                        readElementValues(
                                fieldVisitor.visitTypeAnnotation(
                                        context.currentTypeAnnotationTarget,
                                        context.currentTypeAnnotationTargetPath,
                                        annotationDescriptor,
                                        true),
                                currentAnnotationOffset,
                                true,
                                charBuffer);
            }
        }

        if (runtimeInvisibleTypeAnnotationsOffset != 0) {
            int numAnnotations = readUnsignedShort(runtimeInvisibleTypeAnnotationsOffset);
            int currentAnnotationOffset = runtimeInvisibleTypeAnnotationsOffset + 2;
            while (numAnnotations-- > 0) {
                currentAnnotationOffset = readTypeAnnotationTarget(context, currentAnnotationOffset);
                String annotationDescriptor = readUtf8(currentAnnotationOffset, charBuffer);
                currentAnnotationOffset += 2;
                currentAnnotationOffset =
                        readElementValues(
                                fieldVisitor.visitTypeAnnotation(
                                        context.currentTypeAnnotationTarget,
                                        context.currentTypeAnnotationTargetPath,
                                        annotationDescriptor,
                                        false),
                                currentAnnotationOffset,
                                true,
                                charBuffer);
            }
        }

        while (attributes != null) {
            Attribute nextAttribute = attributes.nextAttribute;
            attributes.nextAttribute = null;
            fieldVisitor.visitAttribute(attributes);
            attributes = nextAttribute;
        }

        fieldVisitor.visitEnd();
        return currentOffset;
    }

    /**
     * Reads a JVMS method_info structure and makes the given visitor visit it.
     *
     * @param abstractClassVisitor the visitor that must visit the method.
     * @param context              information about the class being parsed.
     * @param methodInfoOffset     the start offset of the method_info structure.
     * @return the offset of the first byte following the method_info structure.
     */
    private int readMethod(
            final AbstractClassVisitor abstractClassVisitor, final Context context, final int methodInfoOffset) {
        char[] charBuffer = context.charBuffer;

        int currentOffset = methodInfoOffset;
        context.currentMethodAccessFlags = readUnsignedShort(currentOffset);
        context.currentMethodName = readUtf8(currentOffset + 2, charBuffer);
        context.currentMethodDescriptor = readUtf8(currentOffset + 4, charBuffer);
        currentOffset += 6;

        int codeOffset = 0;
        int exceptionsOffset = 0;
        String[] exceptions = null;
        boolean synthetic = false;
        int signatureIndex = 0;
        int runtimeVisibleAnnotationsOffset = 0;
        int runtimeInvisibleAnnotationsOffset = 0;
        int runtimeVisibleParameterAnnotationsOffset = 0;
        int runtimeInvisibleParameterAnnotationsOffset = 0;
        int runtimeVisibleTypeAnnotationsOffset = 0;
        int runtimeInvisibleTypeAnnotationsOffset = 0;
        int annotationDefaultOffset = 0;
        int methodParametersOffset = 0;
        Attribute attributes = null;

        int attributesCount = readUnsignedShort(currentOffset);
        currentOffset += 2;
        while (attributesCount-- > 0) {
            String attributeName = readUtf8(currentOffset, charBuffer);
            int attributeLength = readInt(currentOffset + 2);
            currentOffset += 6;
            if (Constants.CODE.equals(attributeName)) {
                if ((context.parsingOptions & SKIP_CODE) == 0) {
                    codeOffset = currentOffset;
                }
            } else if (Constants.EXCEPTIONS.equals(attributeName)) {
                exceptionsOffset = currentOffset;
                exceptions = new String[readUnsignedShort(exceptionsOffset)];
                int currentExceptionOffset = exceptionsOffset + 2;
                for (int i = 0; i < exceptions.length; ++i) {
                    exceptions[i] = readClass(currentExceptionOffset, charBuffer);
                    currentExceptionOffset += 2;
                }
            } else if (Constants.SIGNATURE.equals(attributeName)) {
                signatureIndex = readUnsignedShort(currentOffset);
            } else if (Constants.DEPRECATED.equals(attributeName)) {
                context.currentMethodAccessFlags |= Opcodes.ACC_DEPRECATED;
            } else if (Constants.RUNTIME_VISIBLE_ANNOTATIONS.equals(attributeName)) {
                runtimeVisibleAnnotationsOffset = currentOffset;
            } else if (Constants.RUNTIME_VISIBLE_TYPE_ANNOTATIONS.equals(attributeName)) {
                runtimeVisibleTypeAnnotationsOffset = currentOffset;
            } else if (Constants.ANNOTATION_DEFAULT.equals(attributeName)) {
                annotationDefaultOffset = currentOffset;
            } else if (Constants.SYNTHETIC.equals(attributeName)) {
                synthetic = true;
                context.currentMethodAccessFlags |= Opcodes.ACC_SYNTHETIC;
            } else if (Constants.RUNTIME_INVISIBLE_ANNOTATIONS.equals(attributeName)) {
                runtimeInvisibleAnnotationsOffset = currentOffset;
            } else if (Constants.RUNTIME_INVISIBLE_TYPE_ANNOTATIONS.equals(attributeName)) {
                runtimeInvisibleTypeAnnotationsOffset = currentOffset;
            } else if (Constants.RUNTIME_VISIBLE_PARAMETER_ANNOTATIONS.equals(attributeName)) {
                runtimeVisibleParameterAnnotationsOffset = currentOffset;
            } else if (Constants.RUNTIME_INVISIBLE_PARAMETER_ANNOTATIONS.equals(attributeName)) {
                runtimeInvisibleParameterAnnotationsOffset = currentOffset;
            } else if (Constants.METHOD_PARAMETERS.equals(attributeName)) {
                methodParametersOffset = currentOffset;
            } else {
                Attribute attribute =
                        readAttribute(
                                context.attributePrototypes,
                                attributeName,
                                currentOffset,
                                attributeLength,
                                charBuffer,
                                -1,
                                null);
                attribute.nextAttribute = attributes;
                attributes = attribute;
            }
            currentOffset += attributeLength;
        }

        BaseMethodVisitor methodVisitor =
                abstractClassVisitor.visitMethod(
                        context.currentMethodAccessFlags,
                        context.currentMethodName,
                        context.currentMethodDescriptor,
                        signatureIndex == 0 ? null : readUtf(signatureIndex, charBuffer),
                        exceptions);
        if (methodVisitor == null) {
            return currentOffset;
        }

        if (methodVisitor instanceof MethodWriter) {
            MethodWriter methodWriter = (MethodWriter) methodVisitor;
            if (methodWriter.canCopyMethodAttributes(
                    this,
                    synthetic,
                    (context.currentMethodAccessFlags & Opcodes.ACC_DEPRECATED) != 0,
                    readUnsignedShort(methodInfoOffset + 4),
                    signatureIndex,
                    exceptionsOffset)) {
                methodWriter.setMethodAttributesSource(methodInfoOffset, currentOffset - methodInfoOffset);
                return currentOffset;
            }
        }

        if (methodParametersOffset != 0 && (context.parsingOptions & SKIP_DEBUG) == 0) {
            int parametersCount = readByte(methodParametersOffset);
            int currentParameterOffset = methodParametersOffset + 1;
            while (parametersCount-- > 0) {
                methodVisitor.visitParameter(
                        readUtf8(currentParameterOffset, charBuffer),
                        readUnsignedShort(currentParameterOffset + 2));
                currentParameterOffset += 4;
            }
        }

        if (annotationDefaultOffset != 0) {
            AbstractAnnotationVisitor annotationVisitor = methodVisitor.visitAnnotationDefault();
            readElementValue(annotationVisitor, annotationDefaultOffset, null, charBuffer);
            if (annotationVisitor != null) {
                annotationVisitor.visitEnd();
            }
        }

        if (runtimeVisibleAnnotationsOffset != 0) {
            int numAnnotations = readUnsignedShort(runtimeVisibleAnnotationsOffset);
            int currentAnnotationOffset = runtimeVisibleAnnotationsOffset + 2;
            while (numAnnotations-- > 0) {
                String annotationDescriptor = readUtf8(currentAnnotationOffset, charBuffer);
                currentAnnotationOffset += 2;
                currentAnnotationOffset =
                        readElementValues(
                                methodVisitor.visitAnnotation(annotationDescriptor, true),
                                currentAnnotationOffset,
                                true,
                                charBuffer);
            }
        }

        if (runtimeInvisibleAnnotationsOffset != 0) {
            int numAnnotations = readUnsignedShort(runtimeInvisibleAnnotationsOffset);
            int currentAnnotationOffset = runtimeInvisibleAnnotationsOffset + 2;
            while (numAnnotations-- > 0) {
                String annotationDescriptor = readUtf8(currentAnnotationOffset, charBuffer);
                currentAnnotationOffset += 2;
                currentAnnotationOffset =
                        readElementValues(
                                methodVisitor.visitAnnotation(annotationDescriptor, false),
                                currentAnnotationOffset,
                                true,
                                charBuffer);
            }
        }

        if (runtimeVisibleTypeAnnotationsOffset != 0) {
            int numAnnotations = readUnsignedShort(runtimeVisibleTypeAnnotationsOffset);
            int currentAnnotationOffset = runtimeVisibleTypeAnnotationsOffset + 2;
            while (numAnnotations-- > 0) {
                currentAnnotationOffset = readTypeAnnotationTarget(context, currentAnnotationOffset);
                String annotationDescriptor = readUtf8(currentAnnotationOffset, charBuffer);
                currentAnnotationOffset += 2;
                currentAnnotationOffset =
                        readElementValues(
                                methodVisitor.visitTypeAnnotation(
                                        context.currentTypeAnnotationTarget,
                                        context.currentTypeAnnotationTargetPath,
                                        annotationDescriptor,
                                        true),
                                currentAnnotationOffset,
                                true,
                                charBuffer);
            }
        }

        if (runtimeInvisibleTypeAnnotationsOffset != 0) {
            int numAnnotations = readUnsignedShort(runtimeInvisibleTypeAnnotationsOffset);
            int currentAnnotationOffset = runtimeInvisibleTypeAnnotationsOffset + 2;
            while (numAnnotations-- > 0) {
                currentAnnotationOffset = readTypeAnnotationTarget(context, currentAnnotationOffset);
                String annotationDescriptor = readUtf8(currentAnnotationOffset, charBuffer);
                currentAnnotationOffset += 2;
                currentAnnotationOffset =
                        readElementValues(
                                methodVisitor.visitTypeAnnotation(
                                        context.currentTypeAnnotationTarget,
                                        context.currentTypeAnnotationTargetPath,
                                        annotationDescriptor,
                                        false),
                                currentAnnotationOffset,
                                true,
                                charBuffer);
            }
        }

        if (runtimeVisibleParameterAnnotationsOffset != 0) {
            readParameterAnnotations(
                    methodVisitor, context, runtimeVisibleParameterAnnotationsOffset, true);
        }

        if (runtimeInvisibleParameterAnnotationsOffset != 0) {
            readParameterAnnotations(
                    methodVisitor,
                    context,
                    runtimeInvisibleParameterAnnotationsOffset,
                    false);
        }

        while (attributes != null) {
            Attribute nextAttribute = attributes.nextAttribute;
            attributes.nextAttribute = null;
            methodVisitor.visitAttribute(attributes);
            attributes = nextAttribute;
        }

        if (codeOffset != 0) {
            methodVisitor.visitCode();
            readCode(methodVisitor, context, codeOffset);
        }

        methodVisitor.visitEnd();
        return currentOffset;
    }


    /**
     * Reads a JVMS 'Code' attribute and makes the given visitor visit it.
     *
     * @param methodVisitor the visitor that must visit the Code attribute.
     * @param context       information about the class being parsed.
     * @param codeOffset    the start offset in {@link #classFileBuffer} of the Code attribute, excluding
     *                      its attribute_name_index and attribute_length fields.
     */
    private void readCode(
            final BaseMethodVisitor methodVisitor, final Context context, final int codeOffset) {
        int currentOffset = codeOffset;

        final byte[] classBuffer = classFileBuffer;
        final char[] charBuffer = context.charBuffer;
        final int maxStack = readUnsignedShort(currentOffset);
        final int maxLocals = readUnsignedShort(currentOffset + 2);
        final int codeLength = readInt(currentOffset + 4);
        currentOffset += 8;
        if (codeLength > classFileBuffer.length - currentOffset) {
            throw new IllegalArgumentException();
        }

        final int bytecodeStartOffset = currentOffset;
        final int bytecodeEndOffset = currentOffset + codeLength;
        final Label[] labels = context.currentMethodLabels = new Label[codeLength + 1];
        while (currentOffset < bytecodeEndOffset) {
            final int bytecodeOffset = currentOffset - bytecodeStartOffset;
            final int opcode = classBuffer[currentOffset] & 0xFF;
            switch (opcode) {
                case Opcodes.NOP:
                case Opcodes.ACONST_NULL:
                case Opcodes.ICONST_M1:
                case Opcodes.ICONST_0:
                case Opcodes.ICONST_1:
                case Opcodes.ICONST_2:
                case Opcodes.ICONST_3:
                case Opcodes.ICONST_4:
                case Opcodes.ICONST_5:
                case Opcodes.LCONST_0:
                case Opcodes.LCONST_1:
                case Opcodes.FCONST_0:
                case Opcodes.FCONST_1:
                case Opcodes.FCONST_2:
                case Opcodes.DCONST_0:
                case Opcodes.DCONST_1:
                case Opcodes.IALOAD:
                case Opcodes.LALOAD:
                case Opcodes.FALOAD:
                case Opcodes.DALOAD:
                case Opcodes.AALOAD:
                case Opcodes.BALOAD:
                case Opcodes.CALOAD:
                case Opcodes.SALOAD:
                case Opcodes.IASTORE:
                case Opcodes.LASTORE:
                case Opcodes.FASTORE:
                case Opcodes.DASTORE:
                case Opcodes.AASTORE:
                case Opcodes.BASTORE:
                case Opcodes.CASTORE:
                case Opcodes.SASTORE:
                case Opcodes.POP:
                case Opcodes.POP2:
                case Opcodes.DUP:
                case Opcodes.DUP_X1:
                case Opcodes.DUP_X2:
                case Opcodes.DUP2:
                case Opcodes.DUP2_X1:
                case Opcodes.DUP2_X2:
                case Opcodes.SWAP:
                case Opcodes.IADD:
                case Opcodes.LADD:
                case Opcodes.FADD:
                case Opcodes.DADD:
                case Opcodes.ISUB:
                case Opcodes.LSUB:
                case Opcodes.FSUB:
                case Opcodes.DSUB:
                case Opcodes.IMUL:
                case Opcodes.LMUL:
                case Opcodes.FMUL:
                case Opcodes.DMUL:
                case Opcodes.IDIV:
                case Opcodes.LDIV:
                case Opcodes.FDIV:
                case Opcodes.DDIV:
                case Opcodes.IREM:
                case Opcodes.LREM:
                case Opcodes.FREM:
                case Opcodes.DREM:
                case Opcodes.INEG:
                case Opcodes.LNEG:
                case Opcodes.FNEG:
                case Opcodes.DNEG:
                case Opcodes.ISHL:
                case Opcodes.LSHL:
                case Opcodes.ISHR:
                case Opcodes.LSHR:
                case Opcodes.IUSHR:
                case Opcodes.LUSHR:
                case Opcodes.IAND:
                case Opcodes.LAND:
                case Opcodes.IOR:
                case Opcodes.LOR:
                case Opcodes.IXOR:
                case Opcodes.LXOR:
                case Opcodes.I2L:
                case Opcodes.I2F:
                case Opcodes.I2D:
                case Opcodes.L2I:
                case Opcodes.L2F:
                case Opcodes.L2D:
                case Opcodes.F2I:
                case Opcodes.F2L:
                case Opcodes.F2D:
                case Opcodes.D2I:
                case Opcodes.D2L:
                case Opcodes.D2F:
                case Opcodes.I2B:
                case Opcodes.I2C:
                case Opcodes.I2S:
                case Opcodes.LCMP:
                case Opcodes.FCMPL:
                case Opcodes.FCMPG:
                case Opcodes.DCMPL:
                case Opcodes.DCMPG:
                case Opcodes.IRETURN:
                case Opcodes.LRETURN:
                case Opcodes.FRETURN:
                case Opcodes.DRETURN:
                case Opcodes.ARETURN:
                case Opcodes.RETURN:
                case Opcodes.ARRAYLENGTH:
                case Opcodes.ATHROW:
                case Opcodes.MONITORENTER:
                case Opcodes.MONITOREXIT:
                case Constants.ILOAD_0:
                case Constants.ILOAD_1:
                case Constants.ILOAD_2:
                case Constants.ILOAD_3:
                case Constants.LLOAD_0:
                case Constants.LLOAD_1:
                case Constants.LLOAD_2:
                case Constants.LLOAD_3:
                case Constants.FLOAD_0:
                case Constants.FLOAD_1:
                case Constants.FLOAD_2:
                case Constants.FLOAD_3:
                case Constants.DLOAD_0:
                case Constants.DLOAD_1:
                case Constants.DLOAD_2:
                case Constants.DLOAD_3:
                case Constants.ALOAD_0:
                case Constants.ALOAD_1:
                case Constants.ALOAD_2:
                case Constants.ALOAD_3:
                case Constants.ISTORE_0:
                case Constants.ISTORE_1:
                case Constants.ISTORE_2:
                case Constants.ISTORE_3:
                case Constants.LSTORE_0:
                case Constants.LSTORE_1:
                case Constants.LSTORE_2:
                case Constants.LSTORE_3:
                case Constants.FSTORE_0:
                case Constants.FSTORE_1:
                case Constants.FSTORE_2:
                case Constants.FSTORE_3:
                case Constants.DSTORE_0:
                case Constants.DSTORE_1:
                case Constants.DSTORE_2:
                case Constants.DSTORE_3:
                case Constants.ASTORE_0:
                case Constants.ASTORE_1:
                case Constants.ASTORE_2:
                case Constants.ASTORE_3:
                    currentOffset += 1;
                    break;
                case Opcodes.IFEQ:
                case Opcodes.IFNE:
                case Opcodes.IFLT:
                case Opcodes.IFGE:
                case Opcodes.IFGT:
                case Opcodes.IFLE:
                case Opcodes.IF_ICMPEQ:
                case Opcodes.IF_ICMPNE:
                case Opcodes.IF_ICMPLT:
                case Opcodes.IF_ICMPGE:
                case Opcodes.IF_ICMPGT:
                case Opcodes.IF_ICMPLE:
                case Opcodes.IF_ACMPEQ:
                case Opcodes.IF_ACMPNE:
                case Opcodes.GOTO:
                case Opcodes.JSR:
                case Opcodes.IFNULL:
                case Opcodes.IFNONNULL:
                    createLabel(bytecodeOffset + readShort(currentOffset + 1), labels);
                    currentOffset += 3;
                    break;
                case Constants.ASM_IFEQ:
                case Constants.ASM_IFNE:
                case Constants.ASM_IFLT:
                case Constants.ASM_IFGE:
                case Constants.ASM_IFGT:
                case Constants.ASM_IFLE:
                case Constants.ASM_IF_ICMPEQ:
                case Constants.ASM_IF_ICMPNE:
                case Constants.ASM_IF_ICMPLT:
                case Constants.ASM_IF_ICMPGE:
                case Constants.ASM_IF_ICMPGT:
                case Constants.ASM_IF_ICMPLE:
                case Constants.ASM_IF_ACMPEQ:
                case Constants.ASM_IF_ACMPNE:
                case Constants.ASM_GOTO:
                case Constants.ASM_JSR:
                case Constants.ASM_IFNULL:
                case Constants.ASM_IFNONNULL:
                    createLabel(bytecodeOffset + readUnsignedShort(currentOffset + 1), labels);
                    currentOffset += 3;
                    break;
                case Constants.GOTO_W:
                case Constants.JSR_W:
                case Constants.ASM_GOTO_W:
                    createLabel(bytecodeOffset + readInt(currentOffset + 1), labels);
                    currentOffset += 5;
                    break;
                case Constants.WIDE:
                    switch (classBuffer[currentOffset + 1] & 0xFF) {
                        case Opcodes.ILOAD:
                        case Opcodes.FLOAD:
                        case Opcodes.ALOAD:
                        case Opcodes.LLOAD:
                        case Opcodes.DLOAD:
                        case Opcodes.ISTORE:
                        case Opcodes.FSTORE:
                        case Opcodes.ASTORE:
                        case Opcodes.LSTORE:
                        case Opcodes.DSTORE:
                        case Opcodes.RET:
                            currentOffset += 4;
                            break;
                        case Opcodes.IINC:
                            currentOffset += 6;
                            break;
                        default:
                            throw new IllegalArgumentException();
                    }
                    break;
                case Opcodes.TABLESWITCH:
                    currentOffset += 4 - (bytecodeOffset & 3);
                    createLabel(bytecodeOffset + readInt(currentOffset), labels);
                    int numTableEntries = readInt(currentOffset + 8) - readInt(currentOffset + 4) + 1;
                    currentOffset += 12;
                    while (numTableEntries-- > 0) {
                        createLabel(bytecodeOffset + readInt(currentOffset), labels);
                        currentOffset += 4;
                    }
                    break;
                case Opcodes.LOOKUPSWITCH:
                    currentOffset += 4 - (bytecodeOffset & 3);
                    createLabel(bytecodeOffset + readInt(currentOffset), labels);
                    int numSwitchCases = readInt(currentOffset + 4);
                    currentOffset += 8;
                    while (numSwitchCases-- > 0) {
                        createLabel(bytecodeOffset + readInt(currentOffset + 4), labels);
                        currentOffset += 8;
                    }
                    break;
                case Opcodes.ILOAD:
                case Opcodes.LLOAD:
                case Opcodes.FLOAD:
                case Opcodes.DLOAD:
                case Opcodes.ALOAD:
                case Opcodes.ISTORE:
                case Opcodes.LSTORE:
                case Opcodes.FSTORE:
                case Opcodes.DSTORE:
                case Opcodes.ASTORE:
                case Opcodes.RET:
                case Opcodes.BIPUSH:
                case Opcodes.NEWARRAY:
                case Opcodes.LDC:
                    currentOffset += 2;
                    break;
                case Opcodes.SIPUSH:
                case Constants.LDC_W:
                case Constants.LDC2_W:
                case Opcodes.GETSTATIC:
                case Opcodes.PUTSTATIC:
                case Opcodes.GETFIELD:
                case Opcodes.PUTFIELD:
                case Opcodes.INVOKEVIRTUAL:
                case Opcodes.INVOKESPECIAL:
                case Opcodes.INVOKESTATIC:
                case Opcodes.NEW:
                case Opcodes.ANEWARRAY:
                case Opcodes.CHECKCAST:
                case Opcodes.INSTANCEOF:
                case Opcodes.IINC:
                    currentOffset += 3;
                    break;
                case Opcodes.INVOKEINTERFACE:
                case Opcodes.INVOKEDYNAMIC:
                    currentOffset += 5;
                    break;
                case Opcodes.MULTIANEWARRAY:
                    currentOffset += 4;
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }

        int exceptionTableLength = readUnsignedShort(currentOffset);
        currentOffset += 2;
        while (exceptionTableLength-- > 0) {
            Label start = createLabel(readUnsignedShort(currentOffset), labels);
            Label end = createLabel(readUnsignedShort(currentOffset + 2), labels);
            Label handler = createLabel(readUnsignedShort(currentOffset + 4), labels);
            String catchType = readUtf8(cpInfoOffsets[readUnsignedShort(currentOffset + 6)], charBuffer);
            currentOffset += 8;
            methodVisitor.visitTryCatchBlock(start, end, handler, catchType);
        }

        int stackMapFrameOffset = 0;
        int stackMapTableEndOffset = 0;
        boolean compressedFrames = true;
        int localVariableTableOffset = 0;
        int localVariableTypeTableOffset = 0;
        int[] visibleTypeAnnotationOffsets = null;
        int[] invisibleTypeAnnotationOffsets = null;
        Attribute attributes = null;

        int attributesCount = readUnsignedShort(currentOffset);
        currentOffset += 2;
        while (attributesCount-- > 0) {
            String attributeName = readUtf8(currentOffset, charBuffer);
            int attributeLength = readInt(currentOffset + 2);
            currentOffset += 6;
            if (Constants.LOCAL_VARIABLE_TABLE.equals(attributeName)) {
                if ((context.parsingOptions & SKIP_DEBUG) == 0) {
                    localVariableTableOffset = currentOffset;
                    int currentLocalVariableTableOffset = currentOffset;
                    int localVariableTableLength = readUnsignedShort(currentLocalVariableTableOffset);
                    currentLocalVariableTableOffset += 2;
                    while (localVariableTableLength-- > 0) {
                        int startPc = readUnsignedShort(currentLocalVariableTableOffset);
                        createDebugLabel(startPc, labels);
                        int length = readUnsignedShort(currentLocalVariableTableOffset + 2);
                        createDebugLabel(startPc + length, labels);
                        currentLocalVariableTableOffset += 10;
                    }
                }
            } else if (Constants.LOCAL_VARIABLE_TYPE_TABLE.equals(attributeName)) {
                localVariableTypeTableOffset = currentOffset;
            } else if (Constants.LINE_NUMBER_TABLE.equals(attributeName)) {
                if ((context.parsingOptions & SKIP_DEBUG) == 0) {
                    int currentLineNumberTableOffset = currentOffset;
                    int lineNumberTableLength = readUnsignedShort(currentLineNumberTableOffset);
                    currentLineNumberTableOffset += 2;
                    while (lineNumberTableLength-- > 0) {
                        int startPc = readUnsignedShort(currentLineNumberTableOffset);
                        int lineNumber = readUnsignedShort(currentLineNumberTableOffset + 2);
                        currentLineNumberTableOffset += 4;
                        createDebugLabel(startPc, labels);
                        labels[startPc].addLineNumber(lineNumber);
                    }
                }
            } else if (Constants.RUNTIME_VISIBLE_TYPE_ANNOTATIONS.equals(attributeName)) {
                visibleTypeAnnotationOffsets =
                        readTypeAnnotations(methodVisitor, context, currentOffset, true);
            } else if (Constants.RUNTIME_INVISIBLE_TYPE_ANNOTATIONS.equals(attributeName)) {
                invisibleTypeAnnotationOffsets =
                        readTypeAnnotations(methodVisitor, context, currentOffset, false);
            } else if (Constants.STACK_MAP_TABLE.equals(attributeName)) {
                if ((context.parsingOptions & SKIP_FRAMES) == 0) {
                    stackMapFrameOffset = currentOffset + 2;
                    stackMapTableEndOffset = currentOffset + attributeLength;
                }
            } else if ("StackMap".equals(attributeName)) {
                if ((context.parsingOptions & SKIP_FRAMES) == 0) {
                    stackMapFrameOffset = currentOffset + 2;
                    stackMapTableEndOffset = currentOffset + attributeLength;
                    compressedFrames = false;
                }
            } else {
                Attribute attribute =
                        readAttribute(
                                context.attributePrototypes,
                                attributeName,
                                currentOffset,
                                attributeLength,
                                charBuffer,
                                codeOffset,
                                labels);
                attribute.nextAttribute = attributes;
                attributes = attribute;
            }
            currentOffset += attributeLength;
        }

        final boolean expandFrames = (context.parsingOptions & EXPAND_FRAMES) != 0;
        if (stackMapFrameOffset != 0) {
            context.currentFrameOffset = -1;
            context.currentFrameType = 0;
            context.currentFrameLocalCount = 0;
            context.currentFrameLocalCountDelta = 0;
            context.currentFrameLocalTypes = new Object[maxLocals];
            context.currentFrameStackCount = 0;
            context.currentFrameStackTypes = new Object[maxStack];
            if (expandFrames) {
                computeImplicitFrame(context);
            }
            for (int offset = stackMapFrameOffset; offset < stackMapTableEndOffset - TWE; ++offset) {
                if (classBuffer[offset] == Frame.ITEM_UNINITIALIZED) {
                    int potentialBytecodeOffset = readUnsignedShort(offset + 1);
                    if (potentialBytecodeOffset >= 0
                            && potentialBytecodeOffset < codeLength
                            && (classBuffer[bytecodeStartOffset + potentialBytecodeOffset] & 0xFF)
                            == Opcodes.NEW) {
                        createLabel(potentialBytecodeOffset, labels);
                    }
                }
            }
        }
        if (expandFrames && (context.parsingOptions & EXPAND_ASM_INSNS) != 0) {
            methodVisitor.visitFrame(Opcodes.F_NEW, maxLocals, null, 0, null);
        }


        int currentVisibleTypeAnnotationIndex = 0;
        int currentVisibleTypeAnnotationBytecodeOffset =
                getTypeAnnotationBytecodeOffset(visibleTypeAnnotationOffsets, 0);
        int currentInvisibleTypeAnnotationIndex = 0;
        int currentInvisibleTypeAnnotationBytecodeOffset =
                getTypeAnnotationBytecodeOffset(invisibleTypeAnnotationOffsets, 0);

        boolean insertFrame = false;

        final int wideJumpOpcodeDelta =
                (context.parsingOptions & EXPAND_ASM_INSNS) == 0 ? Constants.WIDE_JUMP_OPCODE_DELTA : 0;

        currentOffset = bytecodeStartOffset;
        while (currentOffset < bytecodeEndOffset) {
            final int currentBytecodeOffset = currentOffset - bytecodeStartOffset;

            Label currentLabel = labels[currentBytecodeOffset];
            if (currentLabel != null) {
                currentLabel.accept(methodVisitor, (context.parsingOptions & SKIP_DEBUG) == 0);
            }

            while (stackMapFrameOffset != 0
                    && (context.currentFrameOffset == currentBytecodeOffset
                    || context.currentFrameOffset == -1)) {
                if (context.currentFrameOffset != -1) {
                    if (!compressedFrames || expandFrames) {
                        methodVisitor.visitFrame(
                                Opcodes.F_NEW,
                                context.currentFrameLocalCount,
                                context.currentFrameLocalTypes,
                                context.currentFrameStackCount,
                                context.currentFrameStackTypes);
                    } else {
                        methodVisitor.visitFrame(
                                context.currentFrameType,
                                context.currentFrameLocalCountDelta,
                                context.currentFrameLocalTypes,
                                context.currentFrameStackCount,
                                context.currentFrameStackTypes);
                    }
                    insertFrame = false;
                }
                if (stackMapFrameOffset < stackMapTableEndOffset) {
                    stackMapFrameOffset =
                            readStackMapFrame(stackMapFrameOffset, compressedFrames, expandFrames, context);
                } else {
                    stackMapFrameOffset = 0;
                }
            }

            if (insertFrame) {
                if ((context.parsingOptions & EXPAND_FRAMES) != 0) {
                    methodVisitor.visitFrame(Constants.F_INSERT, 0, null, 0, null);
                }
                insertFrame = false;
            }

            int opcode = classBuffer[currentOffset] & 0xFF;
            switch (opcode) {
                case Opcodes.NOP:
                case Opcodes.ACONST_NULL:
                case Opcodes.ICONST_M1:
                case Opcodes.ICONST_0:
                case Opcodes.ICONST_1:
                case Opcodes.ICONST_2:
                case Opcodes.ICONST_3:
                case Opcodes.ICONST_4:
                case Opcodes.ICONST_5:
                case Opcodes.LCONST_0:
                case Opcodes.LCONST_1:
                case Opcodes.FCONST_0:
                case Opcodes.FCONST_1:
                case Opcodes.FCONST_2:
                case Opcodes.DCONST_0:
                case Opcodes.DCONST_1:
                case Opcodes.IALOAD:
                case Opcodes.LALOAD:
                case Opcodes.FALOAD:
                case Opcodes.DALOAD:
                case Opcodes.AALOAD:
                case Opcodes.BALOAD:
                case Opcodes.CALOAD:
                case Opcodes.SALOAD:
                case Opcodes.IASTORE:
                case Opcodes.LASTORE:
                case Opcodes.FASTORE:
                case Opcodes.DASTORE:
                case Opcodes.AASTORE:
                case Opcodes.BASTORE:
                case Opcodes.CASTORE:
                case Opcodes.SASTORE:
                case Opcodes.POP:
                case Opcodes.POP2:
                case Opcodes.DUP:
                case Opcodes.DUP_X1:
                case Opcodes.DUP_X2:
                case Opcodes.DUP2:
                case Opcodes.DUP2_X1:
                case Opcodes.DUP2_X2:
                case Opcodes.SWAP:
                case Opcodes.IADD:
                case Opcodes.LADD:
                case Opcodes.FADD:
                case Opcodes.DADD:
                case Opcodes.ISUB:
                case Opcodes.LSUB:
                case Opcodes.FSUB:
                case Opcodes.DSUB:
                case Opcodes.IMUL:
                case Opcodes.LMUL:
                case Opcodes.FMUL:
                case Opcodes.DMUL:
                case Opcodes.IDIV:
                case Opcodes.LDIV:
                case Opcodes.FDIV:
                case Opcodes.DDIV:
                case Opcodes.IREM:
                case Opcodes.LREM:
                case Opcodes.FREM:
                case Opcodes.DREM:
                case Opcodes.INEG:
                case Opcodes.LNEG:
                case Opcodes.FNEG:
                case Opcodes.DNEG:
                case Opcodes.ISHL:
                case Opcodes.LSHL:
                case Opcodes.ISHR:
                case Opcodes.LSHR:
                case Opcodes.IUSHR:
                case Opcodes.LUSHR:
                case Opcodes.IAND:
                case Opcodes.LAND:
                case Opcodes.IOR:
                case Opcodes.LOR:
                case Opcodes.IXOR:
                case Opcodes.LXOR:
                case Opcodes.I2L:
                case Opcodes.I2F:
                case Opcodes.I2D:
                case Opcodes.L2I:
                case Opcodes.L2F:
                case Opcodes.L2D:
                case Opcodes.F2I:
                case Opcodes.F2L:
                case Opcodes.F2D:
                case Opcodes.D2I:
                case Opcodes.D2L:
                case Opcodes.D2F:
                case Opcodes.I2B:
                case Opcodes.I2C:
                case Opcodes.I2S:
                case Opcodes.LCMP:
                case Opcodes.FCMPL:
                case Opcodes.FCMPG:
                case Opcodes.DCMPL:
                case Opcodes.DCMPG:
                case Opcodes.IRETURN:
                case Opcodes.LRETURN:
                case Opcodes.FRETURN:
                case Opcodes.DRETURN:
                case Opcodes.ARETURN:
                case Opcodes.RETURN:
                case Opcodes.ARRAYLENGTH:
                case Opcodes.ATHROW:
                case Opcodes.MONITORENTER:
                case Opcodes.MONITOREXIT:
                    methodVisitor.visitInsn(opcode);
                    currentOffset += 1;
                    break;
                case Constants.ILOAD_0:
                case Constants.ILOAD_1:
                case Constants.ILOAD_2:
                case Constants.ILOAD_3:
                case Constants.LLOAD_0:
                case Constants.LLOAD_1:
                case Constants.LLOAD_2:
                case Constants.LLOAD_3:
                case Constants.FLOAD_0:
                case Constants.FLOAD_1:
                case Constants.FLOAD_2:
                case Constants.FLOAD_3:
                case Constants.DLOAD_0:
                case Constants.DLOAD_1:
                case Constants.DLOAD_2:
                case Constants.DLOAD_3:
                case Constants.ALOAD_0:
                case Constants.ALOAD_1:
                case Constants.ALOAD_2:
                case Constants.ALOAD_3:
                    opcode -= Constants.ILOAD_0;
                    methodVisitor.visitVarInsn(Opcodes.ILOAD + (opcode >> 2), opcode & 0x3);
                    currentOffset += 1;
                    break;
                case Constants.ISTORE_0:
                case Constants.ISTORE_1:
                case Constants.ISTORE_2:
                case Constants.ISTORE_3:
                case Constants.LSTORE_0:
                case Constants.LSTORE_1:
                case Constants.LSTORE_2:
                case Constants.LSTORE_3:
                case Constants.FSTORE_0:
                case Constants.FSTORE_1:
                case Constants.FSTORE_2:
                case Constants.FSTORE_3:
                case Constants.DSTORE_0:
                case Constants.DSTORE_1:
                case Constants.DSTORE_2:
                case Constants.DSTORE_3:
                case Constants.ASTORE_0:
                case Constants.ASTORE_1:
                case Constants.ASTORE_2:
                case Constants.ASTORE_3:
                    opcode -= Constants.ISTORE_0;
                    methodVisitor.visitVarInsn(Opcodes.ISTORE + (opcode >> 2), opcode & 0x3);
                    currentOffset += 1;
                    break;
                case Opcodes.IFEQ:
                case Opcodes.IFNE:
                case Opcodes.IFLT:
                case Opcodes.IFGE:
                case Opcodes.IFGT:
                case Opcodes.IFLE:
                case Opcodes.IF_ICMPEQ:
                case Opcodes.IF_ICMPNE:
                case Opcodes.IF_ICMPLT:
                case Opcodes.IF_ICMPGE:
                case Opcodes.IF_ICMPGT:
                case Opcodes.IF_ICMPLE:
                case Opcodes.IF_ACMPEQ:
                case Opcodes.IF_ACMPNE:
                case Opcodes.GOTO:
                case Opcodes.JSR:
                case Opcodes.IFNULL:
                case Opcodes.IFNONNULL:
                    methodVisitor.visitJumpInsn(
                            opcode, labels[currentBytecodeOffset + readShort(currentOffset + 1)]);
                    currentOffset += 3;
                    break;
                case Constants.GOTO_W:
                case Constants.JSR_W:
                    methodVisitor.visitJumpInsn(
                            opcode - wideJumpOpcodeDelta,
                            labels[currentBytecodeOffset + readInt(currentOffset + 1)]);
                    currentOffset += 5;
                    break;
                case Constants.ASM_IFEQ:
                case Constants.ASM_IFNE:
                case Constants.ASM_IFLT:
                case Constants.ASM_IFGE:
                case Constants.ASM_IFGT:
                case Constants.ASM_IFLE:
                case Constants.ASM_IF_ICMPEQ:
                case Constants.ASM_IF_ICMPNE:
                case Constants.ASM_IF_ICMPLT:
                case Constants.ASM_IF_ICMPGE:
                case Constants.ASM_IF_ICMPGT:
                case Constants.ASM_IF_ICMPLE:
                case Constants.ASM_IF_ACMPEQ:
                case Constants.ASM_IF_ACMPNE:
                case Constants.ASM_GOTO:
                case Constants.ASM_JSR:
                case Constants.ASM_IFNULL:
                case Constants.ASM_IFNONNULL: {
                    opcode =
                            opcode < Constants.ASM_IFNULL
                                    ? opcode - Constants.ASM_OPCODE_DELTA
                                    : opcode - Constants.ASM_IFNULL_OPCODE_DELTA;
                    Label target = labels[currentBytecodeOffset + readUnsignedShort(currentOffset + 1)];
                    if (opcode == Opcodes.GOTO || opcode == Opcodes.JSR) {
                        methodVisitor.visitJumpInsn(opcode + Constants.WIDE_JUMP_OPCODE_DELTA, target);
                    } else {
                        opcode = opcode < Opcodes.GOTO ? ((opcode + 1) ^ 1) - 1 : opcode ^ 1;
                        Label endif = createLabel(currentBytecodeOffset + 3, labels);
                        methodVisitor.visitJumpInsn(opcode, endif);
                        methodVisitor.visitJumpInsn(Constants.GOTO_W, target);
                        insertFrame = true;
                    }
                    currentOffset += 3;
                    break;
                }
                case Constants.ASM_GOTO_W:
                    methodVisitor.visitJumpInsn(
                            Constants.GOTO_W, labels[currentBytecodeOffset + readInt(currentOffset + 1)]);
                    insertFrame = true;
                    currentOffset += 5;
                    break;
                case Constants.WIDE:
                    opcode = classBuffer[currentOffset + 1] & 0xFF;
                    if (opcode == Opcodes.IINC) {
                        methodVisitor.visitIincInsn(
                                readUnsignedShort(currentOffset + 2), readShort(currentOffset + 4));
                        currentOffset += 6;
                    } else {
                        methodVisitor.visitVarInsn(opcode, readUnsignedShort(currentOffset + 2));
                        currentOffset += 4;
                    }
                    break;
                case Opcodes.TABLESWITCH: {
                    currentOffset += 4 - (currentBytecodeOffset & 3);
                    Label defaultLabel = labels[currentBytecodeOffset + readInt(currentOffset)];
                    int low = readInt(currentOffset + 4);
                    int high = readInt(currentOffset + 8);
                    currentOffset += 12;
                    Label[] table = new Label[high - low + 1];
                    for (int i = 0; i < table.length; ++i) {
                        table[i] = labels[currentBytecodeOffset + readInt(currentOffset)];
                        currentOffset += 4;
                    }
                    methodVisitor.visitTableSwitchInsn(low, high, defaultLabel, table);
                    break;
                }
                case Opcodes.LOOKUPSWITCH: {
                    currentOffset += 4 - (currentBytecodeOffset & 3);
                    Label defaultLabel = labels[currentBytecodeOffset + readInt(currentOffset)];
                    int numPairs = readInt(currentOffset + 4);
                    currentOffset += 8;
                    int[] keys = new int[numPairs];
                    Label[] values = new Label[numPairs];
                    for (int i = 0; i < numPairs; ++i) {
                        keys[i] = readInt(currentOffset);
                        values[i] = labels[currentBytecodeOffset + readInt(currentOffset + 4)];
                        currentOffset += 8;
                    }
                    methodVisitor.visitLookupSwitchInsn(defaultLabel, keys, values);
                    break;
                }
                case Opcodes.ILOAD:
                case Opcodes.LLOAD:
                case Opcodes.FLOAD:
                case Opcodes.DLOAD:
                case Opcodes.ALOAD:
                case Opcodes.ISTORE:
                case Opcodes.LSTORE:
                case Opcodes.FSTORE:
                case Opcodes.DSTORE:
                case Opcodes.ASTORE:
                case Opcodes.RET:
                    methodVisitor.visitVarInsn(opcode, classBuffer[currentOffset + 1] & 0xFF);
                    currentOffset += 2;
                    break;
                case Opcodes.BIPUSH:
                case Opcodes.NEWARRAY:
                    methodVisitor.visitIntInsn(opcode, classBuffer[currentOffset + 1]);
                    currentOffset += 2;
                    break;
                case Opcodes.SIPUSH:
                    methodVisitor.visitIntInsn(opcode, readShort(currentOffset + 1));
                    currentOffset += 3;
                    break;
                case Opcodes.LDC:
                    methodVisitor.visitLdcInsn(readConst(classBuffer[currentOffset + 1] & 0xFF, charBuffer));
                    currentOffset += 2;
                    break;
                case Constants.LDC_W:
                case Constants.LDC2_W:
                    methodVisitor.visitLdcInsn(readConst(readUnsignedShort(currentOffset + 1), charBuffer));
                    currentOffset += 3;
                    break;
                case Opcodes.GETSTATIC:
                case Opcodes.PUTSTATIC:
                case Opcodes.GETFIELD:
                case Opcodes.PUTFIELD:
                case Opcodes.INVOKEVIRTUAL:
                case Opcodes.INVOKESPECIAL:
                case Opcodes.INVOKESTATIC:
                case Opcodes.INVOKEINTERFACE: {
                    int cpInfoOffset = cpInfoOffsets[readUnsignedShort(currentOffset + 1)];
                    int nameAndTypeCpInfoOffset = cpInfoOffsets[readUnsignedShort(cpInfoOffset + 2)];
                    String owner = readClass(cpInfoOffset, charBuffer);
                    String name = readUtf8(nameAndTypeCpInfoOffset, charBuffer);
                    String descriptor = readUtf8(nameAndTypeCpInfoOffset + 2, charBuffer);
                    if (opcode < Opcodes.INVOKEVIRTUAL) {
                        methodVisitor.visitFieldInsn(opcode, owner, name, descriptor);
                    } else {
                        boolean isInterface =
                                classBuffer[cpInfoOffset - 1] == BaseSymbol.CONSTANT_INTERFACE_METHODREF_TAG;
                        methodVisitor.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
                    }
                    if (opcode == Opcodes.INVOKEINTERFACE) {
                        currentOffset += 5;
                    } else {
                        currentOffset += 3;
                    }
                    break;
                }
                case Opcodes.INVOKEDYNAMIC: {
                    int cpInfoOffset = cpInfoOffsets[readUnsignedShort(currentOffset + 1)];
                    int nameAndTypeCpInfoOffset = cpInfoOffsets[readUnsignedShort(cpInfoOffset + 2)];
                    String name = readUtf8(nameAndTypeCpInfoOffset, charBuffer);
                    String descriptor = readUtf8(nameAndTypeCpInfoOffset + 2, charBuffer);
                    int bootstrapMethodOffset = bootstrapMethodOffsets[readUnsignedShort(cpInfoOffset)];
                    Handle handle =
                            (Handle) readConst(readUnsignedShort(bootstrapMethodOffset), charBuffer);
                    Object[] bootstrapMethodArguments =
                            new Object[readUnsignedShort(bootstrapMethodOffset + 2)];
                    bootstrapMethodOffset += 4;
                    for (int i = 0; i < bootstrapMethodArguments.length; i++) {
                        bootstrapMethodArguments[i] =
                                readConst(readUnsignedShort(bootstrapMethodOffset), charBuffer);
                        bootstrapMethodOffset += 2;
                    }
                    methodVisitor.visitInvokeDynamicInsn(
                            name, descriptor, handle, bootstrapMethodArguments);
                    currentOffset += 5;
                    break;
                }
                case Opcodes.NEW:
                case Opcodes.ANEWARRAY:
                case Opcodes.CHECKCAST:
                case Opcodes.INSTANCEOF:
                    methodVisitor.visitTypeInsn(opcode, readClass(currentOffset + 1, charBuffer));
                    currentOffset += 3;
                    break;
                case Opcodes.IINC:
                    methodVisitor.visitIincInsn(
                            classBuffer[currentOffset + 1] & 0xFF, classBuffer[currentOffset + 2]);
                    currentOffset += 3;
                    break;
                case Opcodes.MULTIANEWARRAY:
                    methodVisitor.visitMultiNewArrayInsn(
                            readClass(currentOffset + 1, charBuffer), classBuffer[currentOffset + 3] & 0xFF);
                    currentOffset += 4;
                    break;
                default:
                    throw new AssertionError();
            }

            while (visibleTypeAnnotationOffsets != null
                    && currentVisibleTypeAnnotationIndex < visibleTypeAnnotationOffsets.length
                    && currentVisibleTypeAnnotationBytecodeOffset <= currentBytecodeOffset) {
                if (currentVisibleTypeAnnotationBytecodeOffset == currentBytecodeOffset) {
                    int currentAnnotationOffset =
                            readTypeAnnotationTarget(
                                    context, visibleTypeAnnotationOffsets[currentVisibleTypeAnnotationIndex]);
                    String annotationDescriptor = readUtf8(currentAnnotationOffset, charBuffer);
                    currentAnnotationOffset += 2;
                    readElementValues(
                            methodVisitor.visitInsnAnnotation(
                                    context.currentTypeAnnotationTarget,
                                    context.currentTypeAnnotationTargetPath,
                                    annotationDescriptor,
                                    true),
                            currentAnnotationOffset,
                            true,
                            charBuffer);
                }
                currentVisibleTypeAnnotationBytecodeOffset =
                        getTypeAnnotationBytecodeOffset(
                                visibleTypeAnnotationOffsets, ++currentVisibleTypeAnnotationIndex);
            }

            while (invisibleTypeAnnotationOffsets != null
                    && currentInvisibleTypeAnnotationIndex < invisibleTypeAnnotationOffsets.length
                    && currentInvisibleTypeAnnotationBytecodeOffset <= currentBytecodeOffset) {
                if (currentInvisibleTypeAnnotationBytecodeOffset == currentBytecodeOffset) {
                    int currentAnnotationOffset =
                            readTypeAnnotationTarget(
                                    context, invisibleTypeAnnotationOffsets[currentInvisibleTypeAnnotationIndex]);
                    String annotationDescriptor = readUtf8(currentAnnotationOffset, charBuffer);
                    currentAnnotationOffset += 2;
                    readElementValues(
                            methodVisitor.visitInsnAnnotation(
                                    context.currentTypeAnnotationTarget,
                                    context.currentTypeAnnotationTargetPath,
                                    annotationDescriptor,
                                    false),
                            currentAnnotationOffset,
                            true,
                            charBuffer);
                }
                currentInvisibleTypeAnnotationBytecodeOffset =
                        getTypeAnnotationBytecodeOffset(
                                invisibleTypeAnnotationOffsets, ++currentInvisibleTypeAnnotationIndex);
            }
        }
        if (labels[codeLength] != null) {
            methodVisitor.visitLabel(labels[codeLength]);
        }

        if (localVariableTableOffset != 0 && (context.parsingOptions & SKIP_DEBUG) == 0) {
            int[] typeTable = null;
            if (localVariableTypeTableOffset != 0) {
                typeTable = new int[readUnsignedShort(localVariableTypeTableOffset) * 3];
                currentOffset = localVariableTypeTableOffset + 2;
                int typeTableIndex = typeTable.length;
                while (typeTableIndex > 0) {
                    typeTable[--typeTableIndex] = currentOffset + 6;
                    typeTable[--typeTableIndex] = readUnsignedShort(currentOffset + 8);
                    typeTable[--typeTableIndex] = readUnsignedShort(currentOffset);
                    currentOffset += 10;
                }
            }
            int localVariableTableLength = readUnsignedShort(localVariableTableOffset);
            currentOffset = localVariableTableOffset + 2;
            while (localVariableTableLength-- > 0) {
                int startPc = readUnsignedShort(currentOffset);
                int length = readUnsignedShort(currentOffset + 2);
                String name = readUtf8(currentOffset + 4, charBuffer);
                String descriptor = readUtf8(currentOffset + 6, charBuffer);
                int index = readUnsignedShort(currentOffset + 8);
                currentOffset += 10;
                String signature = null;
                if (typeTable != null) {
                    for (int i = 0; i < typeTable.length; i += THREE) {
                        if (typeTable[i] == startPc && typeTable[i + 1] == index) {
                            signature = readUtf8(typeTable[i + 2], charBuffer);
                            break;
                        }
                    }
                }
                methodVisitor.visitLocalVariable(
                        name, descriptor, signature, labels[startPc], labels[startPc + length], index);
            }
        }

        if (visibleTypeAnnotationOffsets != null) {
            for (int typeAnnotationOffset : visibleTypeAnnotationOffsets) {
                int targetType = readByte(typeAnnotationOffset);
                if (targetType == TypeReference.LOCAL_VARIABLE
                        || targetType == TypeReference.RESOURCE_VARIABLE) {
                    currentOffset = readTypeAnnotationTarget(context, typeAnnotationOffset);
                    String annotationDescriptor = readUtf8(currentOffset, charBuffer);
                    currentOffset += 2;
                    readElementValues(
                            methodVisitor.visitLocalVariableAnnotation(
                                    context.currentTypeAnnotationTarget,
                                    context.currentTypeAnnotationTargetPath,
                                    context.currentLocalVariableAnnotationRangeStarts,
                                    context.currentLocalVariableAnnotationRangeEnds,
                                    context.currentLocalVariableAnnotationRangeIndices,
                                    annotationDescriptor,
                                    true),
                            currentOffset,
                            true,
                            charBuffer);
                }
            }
        }

        if (invisibleTypeAnnotationOffsets != null) {
            for (int typeAnnotationOffset : invisibleTypeAnnotationOffsets) {
                int targetType = readByte(typeAnnotationOffset);
                if (targetType == TypeReference.LOCAL_VARIABLE
                        || targetType == TypeReference.RESOURCE_VARIABLE) {
                    currentOffset = readTypeAnnotationTarget(context, typeAnnotationOffset);
                    String annotationDescriptor = readUtf8(currentOffset, charBuffer);
                    currentOffset += 2;
                    readElementValues(
                            methodVisitor.visitLocalVariableAnnotation(
                                    context.currentTypeAnnotationTarget,
                                    context.currentTypeAnnotationTargetPath,
                                    context.currentLocalVariableAnnotationRangeStarts,
                                    context.currentLocalVariableAnnotationRangeEnds,
                                    context.currentLocalVariableAnnotationRangeIndices,
                                    annotationDescriptor,
                                    false),
                            currentOffset,
                            true,
                            charBuffer);
                }
            }
        }

        while (attributes != null) {
            Attribute nextAttribute = attributes.nextAttribute;
            attributes.nextAttribute = null;
            methodVisitor.visitAttribute(attributes);
            attributes = nextAttribute;
        }

        methodVisitor.visitMaxs(maxStack, maxLocals);
    }

    /**
     * Returns the label corresponding to the given bytecode offset. The default implementation of
     * this method creates a label for the given offset if it has not been already created.
     *
     * @param bytecodeOffset a bytecode offset in a method.
     * @param labels         the already created labels, indexed by their offset. If a label already exists
     *                       for bytecodeOffset this method must not create a new one. Otherwise it must store the new
     *                       label in this array.
     * @return a non null Label, which must be equal to labels[bytecodeOffset].
     */
    protected Label readLabel(final int bytecodeOffset, final Label[] labels) {
        if (bytecodeOffset >= labels.length) {
            return new Label();
        }
        if (labels[bytecodeOffset] == null) {
            labels[bytecodeOffset] = new Label();
        }
        return labels[bytecodeOffset];
    }

    /**
     * Creates a label without the {@link Label#FLAG_DEBUG_ONLY} flag set, for the given bytecode
     * offset. The label is created with a call to {@link #readLabel} and its {@link
     * Label#FLAG_DEBUG_ONLY} flag is cleared.
     *
     * @param bytecodeOffset a bytecode offset in a method.
     * @param labels         the already created labels, indexed by their offset.
     * @return a Label without the {@link Label#FLAG_DEBUG_ONLY} flag set.
     */
    private Label createLabel(final int bytecodeOffset, final Label[] labels) {
        Label label = readLabel(bytecodeOffset, labels);
        label.flags &= ~Label.FLAG_DEBUG_ONLY;
        return label;
    }

    /**
     * Creates a label with the {@link Label#FLAG_DEBUG_ONLY} flag set, if there is no already
     * existing label for the given bytecode offset (otherwise does nothing). The label is created
     * with a call to {@link #readLabel}.
     *
     * @param bytecodeOffset a bytecode offset in a method.
     * @param labels         the already created labels, indexed by their offset.
     */
    private void createDebugLabel(final int bytecodeOffset, final Label[] labels) {
        if (labels[bytecodeOffset] == null) {
            readLabel(bytecodeOffset, labels).flags |= Label.FLAG_DEBUG_ONLY;
        }
    }


    /**
     * Parses a Runtime[In]VisibleTypeAnnotations attribute to find the offset of each type_annotation
     * entry it contains, to find the corresponding labels, and to visit the try catch block
     * annotations.
     *
     * @param methodVisitor                the method visitor to be used to visit the try catch block annotations.
     * @param context                      information about the class being parsed.
     * @param runtimeTypeAnnotationsOffset the start offset of a Runtime[In]VisibleTypeAnnotations
     *                                     attribute, excluding the attribute_info's attribute_name_index and attribute_length fields.
     * @param visible                      true if the attribute to parse is a RuntimeVisibleTypeAnnotations attribute,
     *                                     false it is a RuntimeInvisibleTypeAnnotations attribute.
     * @return the start offset of each entry of the Runtime[In]VisibleTypeAnnotations_attribute's
     * 'annotations' array field.
     */
    private int[] readTypeAnnotations(
            final BaseMethodVisitor methodVisitor,
            final Context context,
            final int runtimeTypeAnnotationsOffset,
            final boolean visible) {
        char[] charBuffer = context.charBuffer;
        int currentOffset = runtimeTypeAnnotationsOffset;
        int[] typeAnnotationsOffsets = new int[readUnsignedShort(currentOffset)];
        currentOffset += 2;
        for (int i = 0; i < typeAnnotationsOffsets.length; ++i) {
            typeAnnotationsOffsets[i] = currentOffset;
            int targetType = readInt(currentOffset);
            switch (targetType >>> 24) {
                case TypeReference.LOCAL_VARIABLE:
                case TypeReference.RESOURCE_VARIABLE:
                    int tableLength = readUnsignedShort(currentOffset + 1);
                    currentOffset += 3;
                    while (tableLength-- > 0) {
                        int startPc = readUnsignedShort(currentOffset);
                        int length = readUnsignedShort(currentOffset + 2);
                        currentOffset += 6;
                        createLabel(startPc, context.currentMethodLabels);
                        createLabel(startPc + length, context.currentMethodLabels);
                    }
                    break;
                case TypeReference.CAST:
                case TypeReference.CONSTRUCTOR_INVOCATION_TYPE_ARGUMENT:
                case TypeReference.METHOD_INVOCATION_TYPE_ARGUMENT:
                case TypeReference.CONSTRUCTOR_REFERENCE_TYPE_ARGUMENT:
                case TypeReference.METHOD_REFERENCE_TYPE_ARGUMENT:
                    currentOffset += 4;
                    break;
                case TypeReference.CLASS_EXTENDS:
                case TypeReference.CLASS_TYPE_PARAMETER_BOUND:
                case TypeReference.METHOD_TYPE_PARAMETER_BOUND:
                case TypeReference.THROWS:
                case TypeReference.EXCEPTION_PARAMETER:
                case TypeReference.INSTANCEOF:
                case TypeReference.NEW:
                case TypeReference.CONSTRUCTOR_REFERENCE:
                case TypeReference.METHOD_REFERENCE:
                    currentOffset += 3;
                    break;
                case TypeReference.CLASS_TYPE_PARAMETER:
                case TypeReference.METHOD_TYPE_PARAMETER:
                case TypeReference.METHOD_FORMAL_PARAMETER:
                case TypeReference.FIELD:
                case TypeReference.METHOD_RETURN:
                case TypeReference.METHOD_RECEIVER:
                default:
                    throw new IllegalArgumentException();
            }
            int pathLength = readByte(currentOffset);
            if ((targetType >>> 24) == TypeReference.EXCEPTION_PARAMETER) {
                TypePath path = pathLength == 0 ? null : new TypePath(classFileBuffer, currentOffset);
                currentOffset += 1 + 2 * pathLength;
                String annotationDescriptor = readUtf8(currentOffset, charBuffer);
                currentOffset += 2;
                currentOffset =
                        readElementValues(
                                methodVisitor.visitTryCatchAnnotation(
                                        targetType & 0xFFFFFF00, path, annotationDescriptor, visible),
                                currentOffset,
                                true,
                                charBuffer);
            } else {
                currentOffset += 3 + 2 * pathLength;
                currentOffset =
                        readElementValues(
                                /* annotationVisitor = */ null, currentOffset, true, charBuffer);
            }
        }
        return typeAnnotationsOffsets;
    }

    /**
     * Returns the bytecode offset corresponding to the specified JVMS 'type_annotation' structure, or
     * -1 if there is no such type_annotation of if it does not have a bytecode offset.
     *
     * @param typeAnnotationOffsets the offset of each 'type_annotation' entry in a
     *                              Runtime[In]VisibleTypeAnnotations attribute, or {@literal null}.
     * @param typeAnnotationIndex   the index a 'type_annotation' entry in typeAnnotationOffsets.
     * @return bytecode offset corresponding to the specified JVMS 'type_annotation' structure, or -1
     * if there is no such type_annotation of if it does not have a bytecode offset.
     */
    private int getTypeAnnotationBytecodeOffset(
            final int[] typeAnnotationOffsets, final int typeAnnotationIndex) {
        if (typeAnnotationOffsets == null
                || typeAnnotationIndex >= typeAnnotationOffsets.length
                || readByte(typeAnnotationOffsets[typeAnnotationIndex]) < TypeReference.INSTANCEOF) {
            return -1;
        }
        return readUnsignedShort(typeAnnotationOffsets[typeAnnotationIndex] + 1);
    }

    /**
     * Parses the header of a JVMS type_annotation structure to extract its target_type, target_info
     * and target_path (the result is stored in the given context), and returns the start offset of
     * the rest of the type_annotation structure.
     *
     * @param context              information about the class being parsed. This is where the extracted
     *                             target_type and target_path must be stored.
     * @param typeAnnotationOffset the start offset of a type_annotation structure.
     * @return the start offset of the rest of the type_annotation structure.
     */
    private int readTypeAnnotationTarget(final Context context, final int typeAnnotationOffset) {
        int currentOffset = typeAnnotationOffset;
        int targetType = readInt(typeAnnotationOffset);
        switch (targetType >>> 24) {
            case TypeReference.CLASS_TYPE_PARAMETER:
            case TypeReference.METHOD_TYPE_PARAMETER:
            case TypeReference.METHOD_FORMAL_PARAMETER:
                targetType &= 0xFFFF0000;
                currentOffset += 2;
                break;
            case TypeReference.FIELD:
            case TypeReference.METHOD_RETURN:
            case TypeReference.METHOD_RECEIVER:
                targetType &= 0xFF000000;
                currentOffset += 1;
                break;
            case TypeReference.LOCAL_VARIABLE:
            case TypeReference.RESOURCE_VARIABLE:
                targetType &= 0xFF000000;
                int tableLength = readUnsignedShort(currentOffset + 1);
                currentOffset += 3;
                context.currentLocalVariableAnnotationRangeStarts = new Label[tableLength];
                context.currentLocalVariableAnnotationRangeEnds = new Label[tableLength];
                context.currentLocalVariableAnnotationRangeIndices = new int[tableLength];
                for (int i = 0; i < tableLength; ++i) {
                    int startPc = readUnsignedShort(currentOffset);
                    int length = readUnsignedShort(currentOffset + 2);
                    int index = readUnsignedShort(currentOffset + 4);
                    currentOffset += 6;
                    context.currentLocalVariableAnnotationRangeStarts[i] =
                            createLabel(startPc, context.currentMethodLabels);
                    context.currentLocalVariableAnnotationRangeEnds[i] =
                            createLabel(startPc + length, context.currentMethodLabels);
                    context.currentLocalVariableAnnotationRangeIndices[i] = index;
                }
                break;
            case TypeReference.CAST:
            case TypeReference.CONSTRUCTOR_INVOCATION_TYPE_ARGUMENT:
            case TypeReference.METHOD_INVOCATION_TYPE_ARGUMENT:
            case TypeReference.CONSTRUCTOR_REFERENCE_TYPE_ARGUMENT:
            case TypeReference.METHOD_REFERENCE_TYPE_ARGUMENT:
                targetType &= 0xFF0000FF;
                currentOffset += 4;
                break;
            case TypeReference.CLASS_EXTENDS:
            case TypeReference.CLASS_TYPE_PARAMETER_BOUND:
            case TypeReference.METHOD_TYPE_PARAMETER_BOUND:
            case TypeReference.THROWS:
            case TypeReference.EXCEPTION_PARAMETER:
                targetType &= 0xFFFFFF00;
                currentOffset += 3;
                break;
            case TypeReference.INSTANCEOF:
            case TypeReference.NEW:
            case TypeReference.CONSTRUCTOR_REFERENCE:
            case TypeReference.METHOD_REFERENCE:
                targetType &= 0xFF000000;
                currentOffset += 3;
                break;
            default:
                throw new IllegalArgumentException();
        }
        context.currentTypeAnnotationTarget = targetType;
        int pathLength = readByte(currentOffset);
        context.currentTypeAnnotationTargetPath =
                pathLength == 0 ? null : new TypePath(classFileBuffer, currentOffset);
        return currentOffset + 1 + 2 * pathLength;
    }

    /**
     * Reads a Runtime[In]VisibleParameterAnnotations attribute and makes the given visitor visit it.
     *
     * @param methodVisitor                     the visitor that must visit the parameter annotations.
     * @param context                           information about the class being parsed.
     * @param runtimeParameterAnnotationsOffset the start offset of a
     *                                          Runtime[In]VisibleParameterAnnotations attribute, excluding the attribute_info's
     *                                          attribute_name_index and attribute_length fields.
     * @param visible                           true if the attribute to parse is a RuntimeVisibleParameterAnnotations
     *                                          attribute, false it is a RuntimeInvisibleParameterAnnotations attribute.
     */
    private void readParameterAnnotations(
            final BaseMethodVisitor methodVisitor,
            final Context context,
            final int runtimeParameterAnnotationsOffset,
            final boolean visible) {
        int currentOffset = runtimeParameterAnnotationsOffset;
        int numParameters = classFileBuffer[currentOffset++] & 0xFF;
        methodVisitor.visitAnnotableParameterCount(numParameters, visible);
        char[] charBuffer = context.charBuffer;
        for (int i = 0; i < numParameters; ++i) {
            int numAnnotations = readUnsignedShort(currentOffset);
            currentOffset += 2;
            while (numAnnotations-- > 0) {
                String annotationDescriptor = readUtf8(currentOffset, charBuffer);
                currentOffset += 2;
                currentOffset =
                        readElementValues(
                                methodVisitor.visitParameterAnnotation(i, annotationDescriptor, visible),
                                currentOffset,
                                true,
                                charBuffer);
            }
        }
    }

    /**
     * Reads the element values of a JVMS 'annotation' structure and makes the given visitor visit
     * them. This method can also be used to read the values of the JVMS 'array_value' field of an
     * annotation's 'element_value'.
     *
     * @param annotationVisitor the visitor that must visit the values.
     * @param annotationOffset  the start offset of an 'annotation' structure (excluding its type_index
     *                          field) or of an 'array_value' structure.
     * @param named             if the annotation values are named or not. This should be true to parse the values
     *                          of a JVMS 'annotation' structure, and false to parse the JVMS 'array_value' of an
     *                          annotation's element_value.
     * @param charBuffer        the buffer used to read strings in the constant pool.
     * @return the end offset of the JVMS 'annotation' or 'array_value' structure.
     */
    private int readElementValues(
            final AbstractAnnotationVisitor annotationVisitor,
            final int annotationOffset,
            final boolean named,
            final char[] charBuffer) {
        int currentOffset = annotationOffset;
        int numElementValuePairs = readUnsignedShort(currentOffset);
        currentOffset += 2;
        if (named) {
            while (numElementValuePairs-- > 0) {
                String elementName = readUtf8(currentOffset, charBuffer);
                currentOffset =
                        readElementValue(annotationVisitor, currentOffset + 2, elementName, charBuffer);
            }
        } else {
            while (numElementValuePairs-- > 0) {
                currentOffset =
                        readElementValue(annotationVisitor, currentOffset, null, charBuffer);
            }
        }
        if (annotationVisitor != null) {
            annotationVisitor.visitEnd();
        }
        return currentOffset;
    }

    /**
     * Reads a JVMS 'element_value' structure and makes the given visitor visit it.
     *
     * @param annotationVisitor  the visitor that must visit the element_value structure.
     * @param elementValueOffset the start offset in {@link #classFileBuffer} of the element_value
     *                           structure to be read.
     * @param elementName        the name of the element_value structure to be read, or {@literal null}.
     * @param charBuffer         the buffer used to read strings in the constant pool.
     * @return the end offset of the JVMS 'element_value' structure.
     */
    private int readElementValue(
            final AbstractAnnotationVisitor annotationVisitor,
            final int elementValueOffset,
            final String elementName,
            final char[] charBuffer) {
        int currentOffset = elementValueOffset;
        if (annotationVisitor == null) {
            switch (classFileBuffer[currentOffset] & 0xFF) {
                case 'e':
                    return currentOffset + 5;
                case '@':
                    return readElementValues(null, currentOffset + 3, true, charBuffer);
                case '[':
                    return readElementValues(null, currentOffset + 1, false, charBuffer);
                default:
                    return currentOffset + 3;
            }
        }
        switch (classFileBuffer[currentOffset++] & 0xFF) {
            case 'B':
                annotationVisitor.visit(
                        elementName, (byte) readInt(cpInfoOffsets[readUnsignedShort(currentOffset)]));
                currentOffset += 2;
                break;
            case 'C':
                annotationVisitor.visit(
                        elementName, (char) readInt(cpInfoOffsets[readUnsignedShort(currentOffset)]));
                currentOffset += 2;
                break;
            case 'D':
            case 'F':
            case 'I':
            case 'J':
                annotationVisitor.visit(
                        elementName, readConst(readUnsignedShort(currentOffset), charBuffer));
                currentOffset += 2;
                break;
            case 'S':
                annotationVisitor.visit(
                        elementName, (short) readInt(cpInfoOffsets[readUnsignedShort(currentOffset)]));
                currentOffset += 2;
                break;

            case 'Z':
                annotationVisitor.visit(
                        elementName,
                        readInt(cpInfoOffsets[readUnsignedShort(currentOffset)]) == 0
                                ? Boolean.FALSE
                                : Boolean.TRUE);
                currentOffset += 2;
                break;
            case 's':
                annotationVisitor.visit(elementName, readUtf8(currentOffset, charBuffer));
                currentOffset += 2;
                break;
            case 'e':
                annotationVisitor.visitEnum(
                        elementName,
                        readUtf8(currentOffset, charBuffer),
                        readUtf8(currentOffset + 2, charBuffer));
                currentOffset += 4;
                break;
            case 'c':
                annotationVisitor.visit(elementName, Type.getType(readUtf8(currentOffset, charBuffer)));
                currentOffset += 2;
                break;
            case '@':
                currentOffset =
                        readElementValues(
                                annotationVisitor.visitAnnotation(elementName, readUtf8(currentOffset, charBuffer)),
                                currentOffset + 2,
                                true,
                                charBuffer);
                break;
            case '[':
                int numValues = readUnsignedShort(currentOffset);
                currentOffset += 2;
                if (numValues == 0) {
                    return readElementValues(
                            annotationVisitor.visitArray(elementName),
                            currentOffset - 2,
                            false,
                            charBuffer);
                }
                switch (classFileBuffer[currentOffset] & 0xFF) {
                    case 'B':
                        byte[] byteValues = new byte[numValues];
                        for (int i = 0; i < numValues; i++) {
                            byteValues[i] = (byte) readInt(cpInfoOffsets[readUnsignedShort(currentOffset + 1)]);
                            currentOffset += 3;
                        }
                        annotationVisitor.visit(elementName, byteValues);
                        break;
                    case 'Z':
                        boolean[] booleanValues = new boolean[numValues];
                        for (int i = 0; i < numValues; i++) {
                            booleanValues[i] = readInt(cpInfoOffsets[readUnsignedShort(currentOffset + 1)]) != 0;
                            currentOffset += 3;
                        }
                        annotationVisitor.visit(elementName, booleanValues);
                        break;
                    case 'S':
                        short[] shortValues = new short[numValues];
                        for (int i = 0; i < numValues; i++) {
                            shortValues[i] = (short) readInt(cpInfoOffsets[readUnsignedShort(currentOffset + 1)]);
                            currentOffset += 3;
                        }
                        annotationVisitor.visit(elementName, shortValues);
                        break;
                    case 'C':
                        char[] charValues = new char[numValues];
                        for (int i = 0; i < numValues; i++) {
                            charValues[i] = (char) readInt(cpInfoOffsets[readUnsignedShort(currentOffset + 1)]);
                            currentOffset += 3;
                        }
                        annotationVisitor.visit(elementName, charValues);
                        break;
                    case 'I':
                        int[] intValues = new int[numValues];
                        for (int i = 0; i < numValues; i++) {
                            intValues[i] = readInt(cpInfoOffsets[readUnsignedShort(currentOffset + 1)]);
                            currentOffset += 3;
                        }
                        annotationVisitor.visit(elementName, intValues);
                        break;
                    case 'J':
                        long[] longValues = new long[numValues];
                        for (int i = 0; i < numValues; i++) {
                            longValues[i] = readLong(cpInfoOffsets[readUnsignedShort(currentOffset + 1)]);
                            currentOffset += 3;
                        }
                        annotationVisitor.visit(elementName, longValues);
                        break;
                    case 'F':
                        float[] floatValues = new float[numValues];
                        for (int i = 0; i < numValues; i++) {
                            floatValues[i] =
                                    Float.intBitsToFloat(
                                            readInt(cpInfoOffsets[readUnsignedShort(currentOffset + 1)]));
                            currentOffset += 3;
                        }
                        annotationVisitor.visit(elementName, floatValues);
                        break;
                    case 'D':
                        double[] doubleValues = new double[numValues];
                        for (int i = 0; i < numValues; i++) {
                            doubleValues[i] =
                                    Double.longBitsToDouble(
                                            readLong(cpInfoOffsets[readUnsignedShort(currentOffset + 1)]));
                            currentOffset += 3;
                        }
                        annotationVisitor.visit(elementName, doubleValues);
                        break;
                    default:
                        currentOffset =
                                readElementValues(
                                        annotationVisitor.visitArray(elementName),
                                        currentOffset - 2,
                                        false,
                                        charBuffer);
                        break;
                }
                break;
            default:
                throw new IllegalArgumentException();
        }
        return currentOffset;
    }


    /**
     * Computes the implicit frame of the method currently being parsed (as defined in the given
     * {@link Context}) and stores it in the given context.
     *
     * @param context information about the class being parsed.
     */
    private void computeImplicitFrame(final Context context) {
        String methodDescriptor = context.currentMethodDescriptor;
        Object[] locals = context.currentFrameLocalTypes;
        int numLocal = 0;
        if ((context.currentMethodAccessFlags & Opcodes.ACC_STATIC) == 0) {
            String init = "<init>";
            if (init.equals(context.currentMethodName)) {
                locals[numLocal++] = Opcodes.UNINITIALIZED_THIS;
            } else {
                locals[numLocal++] = readClass(header + 2, context.charBuffer);
            }
        }
        int currentMethodDescritorOffset = 1;
        while (true) {
            int currentArgumentDescriptorStartOffset = currentMethodDescritorOffset;
            switch (methodDescriptor.charAt(currentMethodDescritorOffset++)) {
                case 'Z':
                case 'C':
                case 'B':
                case 'S':
                case 'I':
                    locals[numLocal++] = Opcodes.INTEGER;
                    break;
                case 'F':
                    locals[numLocal++] = Opcodes.FLOAT;
                    break;
                case 'J':
                    locals[numLocal++] = Opcodes.LONG;
                    break;
                case 'D':
                    locals[numLocal++] = Opcodes.DOUBLE;
                    break;
                case SYMBOL_LEFT_SQUARE_BRACKET_CHAR:
                    while (methodDescriptor.charAt(currentMethodDescritorOffset) == SYMBOL_LEFT_SQUARE_BRACKET_CHAR) {
                        ++currentMethodDescritorOffset;
                    }
                    if (methodDescriptor.charAt(currentMethodDescritorOffset) == 'L') {
                        ++currentMethodDescritorOffset;
                        while (methodDescriptor.charAt(currentMethodDescritorOffset) != SYMBOL_SEMICOLON_CHAR) {
                            ++currentMethodDescritorOffset;
                        }
                    }
                    locals[numLocal++] =
                            methodDescriptor.substring(
                                    currentArgumentDescriptorStartOffset, ++currentMethodDescritorOffset);
                    break;
                case 'L':
                    while (methodDescriptor.charAt(currentMethodDescritorOffset) != SYMBOL_SEMICOLON_CHAR) {
                        ++currentMethodDescritorOffset;
                    }
                    locals[numLocal++] =
                            methodDescriptor.substring(
                                    currentArgumentDescriptorStartOffset + 1, currentMethodDescritorOffset++);
                    break;
                default:
                    context.currentFrameLocalCount = numLocal;
                    return;
            }
        }
    }

    /**
     * Reads a JVMS 'stack_map_frame' structure and stores the result in the given {@link Context}
     * object. This method can also be used to read a full_frame structure, excluding its frame_type
     * field (this is used to parse the legacy StackMap attributes).
     *
     * @param stackMapFrameOffset the start offset in {@link #classFileBuffer} of the
     *                            stack_map_frame_value structure to be read, or the start offset of a full_frame structure
     *                            (excluding its frame_type field).
     * @param compressed          true to read a 'stack_map_frame' structure, false to read a 'full_frame'
     *                            structure without its frame_type field.
     * @param expand              if the stack map frame must be expanded. See {@link #EXPAND_FRAMES}.
     * @param context             where the parsed stack map frame must be stored.
     * @return the end offset of the JVMS 'stack_map_frame' or 'full_frame' structure.
     */
    private int readStackMapFrame(
            final int stackMapFrameOffset,
            final boolean compressed,
            final boolean expand,
            final Context context) {
        int currentOffset = stackMapFrameOffset;
        final char[] charBuffer = context.charBuffer;
        final Label[] labels = context.currentMethodLabels;
        int frameType;
        if (compressed) {
            frameType = classFileBuffer[currentOffset++] & 0xFF;
        } else {
            frameType = Frame.FULL_FRAME;
            context.currentFrameOffset = -1;
        }
        int offsetDelta;
        context.currentFrameLocalCountDelta = 0;
        if (frameType < Frame.SAME_LOCALS_1_STACK_ITEM_FRAME) {
            offsetDelta = frameType;
            context.currentFrameType = Opcodes.F_SAME;
            context.currentFrameStackCount = 0;
        } else if (frameType < Frame.RESERVED) {
            offsetDelta = frameType - Frame.SAME_LOCALS_1_STACK_ITEM_FRAME;
            currentOffset =
                    readVerificationTypeInfo(
                            currentOffset, context.currentFrameStackTypes, 0, charBuffer, labels);
            context.currentFrameType = Opcodes.F_SAME1;
            context.currentFrameStackCount = 1;
        } else if (frameType >= Frame.SAME_LOCALS_1_STACK_ITEM_FRAME_EXTENDED) {
            offsetDelta = readUnsignedShort(currentOffset);
            currentOffset += 2;
            if (frameType == Frame.SAME_LOCALS_1_STACK_ITEM_FRAME_EXTENDED) {
                currentOffset =
                        readVerificationTypeInfo(
                                currentOffset, context.currentFrameStackTypes, 0, charBuffer, labels);
                context.currentFrameType = Opcodes.F_SAME1;
                context.currentFrameStackCount = 1;
            } else if (frameType >= Frame.CHOP_FRAME && frameType < Frame.SAME_FRAME_EXTENDED) {
                context.currentFrameType = Opcodes.F_CHOP;
                context.currentFrameLocalCountDelta = Frame.SAME_FRAME_EXTENDED - frameType;
                context.currentFrameLocalCount -= context.currentFrameLocalCountDelta;
                context.currentFrameStackCount = 0;
            } else if (frameType == Frame.SAME_FRAME_EXTENDED) {
                context.currentFrameType = Opcodes.F_SAME;
                context.currentFrameStackCount = 0;
            } else if (frameType < Frame.FULL_FRAME) {
                int local = expand ? context.currentFrameLocalCount : 0;
                for (int k = frameType - Frame.SAME_FRAME_EXTENDED; k > 0; k--) {
                    currentOffset =
                            readVerificationTypeInfo(
                                    currentOffset, context.currentFrameLocalTypes, local++, charBuffer, labels);
                }
                context.currentFrameType = Opcodes.F_APPEND;
                context.currentFrameLocalCountDelta = frameType - Frame.SAME_FRAME_EXTENDED;
                context.currentFrameLocalCount += context.currentFrameLocalCountDelta;
                context.currentFrameStackCount = 0;
            } else {
                final int numberOfLocals = readUnsignedShort(currentOffset);
                currentOffset += 2;
                context.currentFrameType = Opcodes.F_FULL;
                context.currentFrameLocalCountDelta = numberOfLocals;
                context.currentFrameLocalCount = numberOfLocals;
                for (int local = 0; local < numberOfLocals; ++local) {
                    currentOffset =
                            readVerificationTypeInfo(
                                    currentOffset, context.currentFrameLocalTypes, local, charBuffer, labels);
                }
                final int numberOfStackItems = readUnsignedShort(currentOffset);
                currentOffset += 2;
                context.currentFrameStackCount = numberOfStackItems;
                for (int stack = 0; stack < numberOfStackItems; ++stack) {
                    currentOffset =
                            readVerificationTypeInfo(
                                    currentOffset, context.currentFrameStackTypes, stack, charBuffer, labels);
                }
            }
        } else {
            throw new IllegalArgumentException();
        }
        context.currentFrameOffset += offsetDelta + 1;
        createLabel(context.currentFrameOffset, labels);
        return currentOffset;
    }

    /**
     * Reads a JVMS 'verification_type_info' structure and stores it at the given index in the given
     * array.
     *
     * @param verificationTypeInfoOffset the start offset of the 'verification_type_info' structure to
     *                                   read.
     * @param frame                      the array where the parsed type must be stored.
     * @param index                      the index in 'frame' where the parsed type must be stored.
     * @param charBuffer                 the buffer used to read strings in the constant pool.
     * @param labels                     the labels of the method currently being parsed, indexed by their offset. If the
     *                                   parsed type is an ITEM_Uninitialized, a new label for the corresponding NEW instruction is
     *                                   stored in this array if it does not already exist.
     * @return the end offset of the JVMS 'verification_type_info' structure.
     */
    private int readVerificationTypeInfo(
            final int verificationTypeInfoOffset,
            final Object[] frame,
            final int index,
            final char[] charBuffer,
            final Label[] labels) {
        int currentOffset = verificationTypeInfoOffset;
        int tag = classFileBuffer[currentOffset++] & 0xFF;
        switch (tag) {
            case Frame.ITEM_TOP:
                frame[index] = Opcodes.TOP;
                break;
            case Frame.ITEM_INTEGER:
                frame[index] = Opcodes.INTEGER;
                break;
            case Frame.ITEM_FLOAT:
                frame[index] = Opcodes.FLOAT;
                break;
            case Frame.ITEM_DOUBLE:
                frame[index] = Opcodes.DOUBLE;
                break;
            case Frame.ITEM_LONG:
                frame[index] = Opcodes.LONG;
                break;
            case Frame.ITEM_NULL:
                frame[index] = Opcodes.NULL;
                break;
            case Frame.ITEM_UNINITIALIZED_THIS:
                frame[index] = Opcodes.UNINITIALIZED_THIS;
                break;
            case Frame.ITEM_OBJECT:
                frame[index] = readClass(currentOffset, charBuffer);
                currentOffset += 2;
                break;
            case Frame.ITEM_UNINITIALIZED:
                frame[index] = createLabel(readUnsignedShort(currentOffset), labels);
                currentOffset += 2;
                break;
            default:
                throw new IllegalArgumentException();
        }
        return currentOffset;
    }


    /**
     * Returns the offset in {@link #classFileBuffer} of the first ClassFile's 'attributes' array
     * field entry.
     *
     * @return the offset in {@link #classFileBuffer} of the first ClassFile's 'attributes' array
     * field entry.
     */
    final int getFirstAttributeOffset() {
        int currentOffset = header + 8 + readUnsignedShort(header + 6) * 2;

        int fieldsCount = readUnsignedShort(currentOffset);
        currentOffset += 2;
        while (fieldsCount-- > 0) {
            int attributesCount = readUnsignedShort(currentOffset + 6);
            currentOffset += 8;
            while (attributesCount-- > 0) {
                currentOffset += 6 + readInt(currentOffset + 2);
            }
        }

        int methodsCount = readUnsignedShort(currentOffset);
        currentOffset += 2;
        while (methodsCount-- > 0) {
            int attributesCount = readUnsignedShort(currentOffset + 6);
            currentOffset += 8;
            while (attributesCount-- > 0) {
                currentOffset += 6 + readInt(currentOffset + 2);
            }
        }

        return currentOffset + 2;
    }

    /**
     * Reads the BootstrapMethods attribute to compute the offset of each bootstrap method.
     *
     * @param maxStringLength a conservative estimate of the maximum length of the strings contained
     *                        in the constant pool of the class.
     * @return the offsets of the bootstrap methods.
     */
    private int[] readBootstrapMethodsAttribute(final int maxStringLength) {
        char[] charBuffer = new char[maxStringLength];
        int currentAttributeOffset = getFirstAttributeOffset();
        for (int i = readUnsignedShort(currentAttributeOffset - TWE); i > 0; --i) {
            String attributeName = readUtf8(currentAttributeOffset, charBuffer);
            int attributeLength = readInt(currentAttributeOffset + 2);
            currentAttributeOffset += 6;
            if (Constants.BOOTSTRAP_METHODS.equals(attributeName)) {
                int[] result = new int[readUnsignedShort(currentAttributeOffset)];
                int currentBootstrapMethodOffset = currentAttributeOffset + 2;
                for (int j = 0; j < result.length; ++j) {
                    result[j] = currentBootstrapMethodOffset;
                    currentBootstrapMethodOffset +=
                            4 + readUnsignedShort(currentBootstrapMethodOffset + 2) * 2;
                }
                return result;
            }
            currentAttributeOffset += attributeLength;
        }
        throw new IllegalArgumentException();
    }

    /**
     * Reads a non standard JVMS 'attribute' structure in {@link #classFileBuffer}.
     *
     * @param attributePrototypes prototypes of the attributes that must be parsed during the visit of
     *                            the class. Any attribute whose type is not equal to the type of one the prototypes will not
     *                            be parsed: its byte array value will be passed unchanged to the ClassWriter.
     * @param type                the type of the attribute.
     * @param offset              the start offset of the JVMS 'attribute' structure in {@link #classFileBuffer}.
     *                            The 6 attribute header bytes (attribute_name_index and attribute_length) are not taken into
     *                            account here.
     * @param length              the length of the attribute's content (excluding the 6 attribute header bytes).
     * @param charBuffer          the buffer to be used to read strings in the constant pool.
     * @param codeAttributeOffset the start offset of the enclosing Code attribute in {@link
     *                            #classFileBuffer}, or -1 if the attribute to be read is not a code attribute. The 6
     *                            attribute header bytes (attribute_name_index and attribute_length) are not taken into
     *                            account here.
     * @param labels              the labels of the method's code, or {@literal null} if the attribute to be read
     *                            is not a code attribute.
     * @return the attribute that has been read.
     */
    private Attribute readAttribute(
            final Attribute[] attributePrototypes,
            final String type,
            final int offset,
            final int length,
            final char[] charBuffer,
            final int codeAttributeOffset,
            final Label[] labels) {
        for (Attribute attributePrototype : attributePrototypes) {
            if (attributePrototype.type.equals(type)) {
                return attributePrototype.read(
                        this, offset, length, charBuffer, codeAttributeOffset, labels);
            }
        }
        return new Attribute(type).read(this, offset, length, null, -1, null);
    }


    /**
     * Returns the number of entries in the class's constant pool table.
     *
     * @return the number of entries in the class's constant pool table.
     */
    public int getItemCount() {
        return cpInfoOffsets.length;
    }

    /**
     * Returns the start offset in this {@link ClassReader} of a JVMS 'cp_info' structure (i.e. a
     * constant pool entry), plus one. <i>This method is intended for {@link Attribute} sub classes,
     * and is normally not needed by class generators or adapters.</i>
     *
     * @param constantPoolEntryIndex the index a constant pool entry in the class's constant pool
     *                               table.
     * @return the start offset in this {@link ClassReader} of the corresponding JVMS 'cp_info'
     * structure, plus one.
     */
    public int getItem(final int constantPoolEntryIndex) {
        return cpInfoOffsets[constantPoolEntryIndex];
    }

    /**
     * Returns a conservative estimate of the maximum length of the strings contained in the class's
     * constant pool table.
     *
     * @return a conservative estimate of the maximum length of the strings contained in the class's
     * constant pool table.
     */
    public int getMaxStringLength() {
        return maxStringLength;
    }

    /**
     * Reads a byte value in this {@link ClassReader}. <i>This method is intended for {@link
     * Attribute} sub classes, and is normally not needed by class generators or adapters.</i>
     *
     * @param offset the start offset of the value to be read in this {@link ClassReader}.
     * @return the read value.
     */
    public int readByte(final int offset) {
        return classFileBuffer[offset] & 0xFF;
    }

    /**
     * Reads an unsigned short value in this {@link ClassReader}. <i>This method is intended for
     * {@link Attribute} sub classes, and is normally not needed by class generators or adapters.</i>
     *
     * @param offset the start index of the value to be read in this {@link ClassReader}.
     * @return the read value.
     */
    public int readUnsignedShort(final int offset) {
        byte[] classBuffer = classFileBuffer;
        return ((classBuffer[offset] & 0xFF) << 8) | (classBuffer[offset + 1] & 0xFF);
    }

    /**
     * Reads a signed short value in this {@link ClassReader}. <i>This method is intended for {@link
     * Attribute} sub classes, and is normally not needed by class generators or adapters.</i>
     *
     * @param offset the start offset of the value to be read in this {@link ClassReader}.
     * @return the read value.
     */
    public short readShort(final int offset) {
        byte[] classBuffer = classFileBuffer;
        return (short) (((classBuffer[offset] & 0xFF) << 8) | (classBuffer[offset + 1] & 0xFF));
    }

    /**
     * Reads a signed int value in this {@link ClassReader}. <i>This method is intended for {@link
     * Attribute} sub classes, and is normally not needed by class generators or adapters.</i>
     *
     * @param offset the start offset of the value to be read in this {@link ClassReader}.
     * @return the read value.
     */
    public int readInt(final int offset) {
        byte[] classBuffer = classFileBuffer;
        return ((classBuffer[offset] & 0xFF) << 24)
                | ((classBuffer[offset + 1] & 0xFF) << 16)
                | ((classBuffer[offset + 2] & 0xFF) << 8)
                | (classBuffer[offset + 3] & 0xFF);
    }

    /**
     * Reads a signed long value in this {@link ClassReader}. <i>This method is intended for {@link
     * Attribute} sub classes, and is normally not needed by class generators or adapters.</i>
     *
     * @param offset the start offset of the value to be read in this {@link ClassReader}.
     * @return the read value.
     */
    public long readLong(final int offset) {
        long l1 = readInt(offset);
        long l0 = readInt(offset + 4) & 0xFFFFFFFFL;
        return (l1 << 32) | l0;
    }

    /**
     * Reads a CONSTANT_Utf8 constant pool entry in this {@link ClassReader}. <i>This method is
     * intended for {@link Attribute} sub classes, and is normally not needed by class generators or
     * adapters.</i>
     *
     * @param offset     the start offset of an unsigned short value in this {@link ClassReader}, whose
     *                   value is the index of a CONSTANT_Utf8 entry in the class's constant pool table.
     * @param charBuffer the buffer to be used to read the string. This buffer must be sufficiently
     *                   large. It is not automatically resized.
     * @return the String corresponding to the specified CONSTANT_Utf8 entry.
     */
    public String readUtf8(final int offset, final char[] charBuffer) {
        int constantPoolEntryIndex = readUnsignedShort(offset);
        if (offset == 0 || constantPoolEntryIndex == 0) {
            return null;
        }
        return readUtf(constantPoolEntryIndex, charBuffer);
    }

    /**
     * Reads a CONSTANT_Utf8 constant pool entry in {@link #classFileBuffer}.
     *
     * @param constantPoolEntryIndex the index of a CONSTANT_Utf8 entry in the class's constant pool
     *                               table.
     * @param charBuffer             the buffer to be used to read the string. This buffer must be sufficiently
     *                               large. It is not automatically resized.
     * @return the String corresponding to the specified CONSTANT_Utf8 entry.
     */
    final String readUtf(final int constantPoolEntryIndex, final char[] charBuffer) {
        String value = constantUtf8Values[constantPoolEntryIndex];
        if (value != null) {
            return value;
        }
        int cpInfoOffset = cpInfoOffsets[constantPoolEntryIndex];
        return constantUtf8Values[constantPoolEntryIndex] =
                readUtf(cpInfoOffset + 2, readUnsignedShort(cpInfoOffset), charBuffer);
    }

    /**
     * Reads an UTF8 string in {@link #classFileBuffer}.
     *
     * @param utfOffset  the start offset of the UTF8 string to be read.
     * @param utfLength  the length of the UTF8 string to be read.
     * @param charBuffer the buffer to be used to read the string. This buffer must be sufficiently
     *                   large. It is not automatically resized.
     * @return the String corresponding to the specified UTF8 string.
     */
    private String readUtf(final int utfOffset, final int utfLength, final char[] charBuffer) {
        int currentOffset = utfOffset;
        int endOffset = currentOffset + utfLength;
        int strLength = 0;
        byte[] classBuffer = classFileBuffer;
        while (currentOffset < endOffset) {
            int currentByte = classBuffer[currentOffset++];
            if ((currentByte & 0x80) == 0) {
                charBuffer[strLength++] = (char) (currentByte & 0x7F);
            } else if ((currentByte & 0xE0) == 0xC0) {
                charBuffer[strLength++] =
                        (char) (((currentByte & 0x1F) << 6) + (classBuffer[currentOffset++] & 0x3F));
            } else {
                charBuffer[strLength++] =
                        (char)
                                (((currentByte & 0xF) << 12)
                                        + ((classBuffer[currentOffset++] & 0x3F) << 6)
                                        + (classBuffer[currentOffset++] & 0x3F));
            }
        }
        return new String(charBuffer, 0, strLength);
    }

    /**
     * Reads a CONSTANT_Class, CONSTANT_String, CONSTANT_MethodType, CONSTANT_Module or
     * CONSTANT_Package constant pool entry in {@link #classFileBuffer}. <i>This method is intended
     * for {@link Attribute} sub classes, and is normally not needed by class generators or
     * adapters.</i>
     *
     * @param offset     the start offset of an unsigned short value in {@link #classFileBuffer}, whose
     *                   value is the index of a CONSTANT_Class, CONSTANT_String, CONSTANT_MethodType,
     *                   CONSTANT_Module or CONSTANT_Package entry in class's constant pool table.
     * @param charBuffer the buffer to be used to read the item. This buffer must be sufficiently
     *                   large. It is not automatically resized.
     * @return the String corresponding to the specified constant pool entry.
     */
    private String readStringish(final int offset, final char[] charBuffer) {
        return readUtf8(cpInfoOffsets[readUnsignedShort(offset)], charBuffer);
    }

    /**
     * Reads a CONSTANT_Class constant pool entry in this {@link ClassReader}. <i>This method is
     * intended for {@link Attribute} sub classes, and is normally not needed by class generators or
     * adapters.</i>
     *
     * @param offset     the start offset of an unsigned short value in this {@link ClassReader}, whose
     *                   value is the index of a CONSTANT_Class entry in class's constant pool table.
     * @param charBuffer the buffer to be used to read the item. This buffer must be sufficiently
     *                   large. It is not automatically resized.
     * @return the String corresponding to the specified CONSTANT_Class entry.
     */
    public String readClass(final int offset, final char[] charBuffer) {
        return readStringish(offset, charBuffer);
    }

    /**
     * Reads a CONSTANT_Module constant pool entry in this {@link ClassReader}. <i>This method is
     * intended for {@link Attribute} sub classes, and is normally not needed by class generators or
     * adapters.</i>
     *
     * @param offset     the start offset of an unsigned short value in this {@link ClassReader}, whose
     *                   value is the index of a CONSTANT_Module entry in class's constant pool table.
     * @param charBuffer the buffer to be used to read the item. This buffer must be sufficiently
     *                   large. It is not automatically resized.
     * @return the String corresponding to the specified CONSTANT_Module entry.
     */
    public String readModule(final int offset, final char[] charBuffer) {
        return readStringish(offset, charBuffer);
    }

    /**
     * Reads a CONSTANT_Package constant pool entry in this {@link ClassReader}. <i>This method is
     * intended for {@link Attribute} sub classes, and is normally not needed by class generators or
     * adapters.</i>
     *
     * @param offset     the start offset of an unsigned short value in this {@link ClassReader}, whose
     *                   value is the index of a CONSTANT_Package entry in class's constant pool table.
     * @param charBuffer the buffer to be used to read the item. This buffer must be sufficiently
     *                   large. It is not automatically resized.
     * @return the String corresponding to the specified CONSTANT_Package entry.
     */
    public String readPackage(final int offset, final char[] charBuffer) {
        return readStringish(offset, charBuffer);
    }

    /**
     * Reads a CONSTANT_Dynamic constant pool entry in {@link #classFileBuffer}.
     *
     * @param constantPoolEntryIndex the index of a CONSTANT_Dynamic entry in the class's constant
     *                               pool table.
     * @param charBuffer             the buffer to be used to read the string. This buffer must be sufficiently
     *                               large. It is not automatically resized.
     * @return the ConstantDynamic corresponding to the specified CONSTANT_Dynamic entry.
     */
    private ConstantDynamic readConstantDynamic(
            final int constantPoolEntryIndex, final char[] charBuffer) {
        ConstantDynamic constantDynamic = constantDynamicValues[constantPoolEntryIndex];
        if (constantDynamic != null) {
            return constantDynamic;
        }
        int cpInfoOffset = cpInfoOffsets[constantPoolEntryIndex];
        int nameAndTypeCpInfoOffset = cpInfoOffsets[readUnsignedShort(cpInfoOffset + 2)];
        String name = readUtf8(nameAndTypeCpInfoOffset, charBuffer);
        String descriptor = readUtf8(nameAndTypeCpInfoOffset + 2, charBuffer);
        int bootstrapMethodOffset = bootstrapMethodOffsets[readUnsignedShort(cpInfoOffset)];
        Handle handle = (Handle) readConst(readUnsignedShort(bootstrapMethodOffset), charBuffer);
        Object[] bootstrapMethodArguments = new Object[readUnsignedShort(bootstrapMethodOffset + 2)];
        bootstrapMethodOffset += 4;
        for (int i = 0; i < bootstrapMethodArguments.length; i++) {
            bootstrapMethodArguments[i] = readConst(readUnsignedShort(bootstrapMethodOffset), charBuffer);
            bootstrapMethodOffset += 2;
        }
        return constantDynamicValues[constantPoolEntryIndex] =
                new ConstantDynamic(name, descriptor, handle, bootstrapMethodArguments);
    }

    /**
     * Reads a numeric or string constant pool entry in this {@link ClassReader}. <i>This method is
     * intended for {@link Attribute} sub classes, and is normally not needed by class generators or
     * adapters.</i>
     *
     * @param constantPoolEntryIndex the index of a CONSTANT_Integer, CONSTANT_Float, CONSTANT_Long,
     *                               CONSTANT_Double, CONSTANT_Class, CONSTANT_String, CONSTANT_MethodType,
     *                               CONSTANT_MethodHandle or CONSTANT_Dynamic entry in the class's constant pool.
     * @param charBuffer             the buffer to be used to read strings. This buffer must be sufficiently
     *                               large. It is not automatically resized.
     * @return the {@link Integer}, {@link Float}, {@link Long}, {@link Double}, {@link String},
     * {@link Type}, {@link Handle} or {@link ConstantDynamic} corresponding to the specified
     * constant pool entry.
     */
    public Object readConst(final int constantPoolEntryIndex, final char[] charBuffer) {
        int cpInfoOffset = cpInfoOffsets[constantPoolEntryIndex];
        switch (classFileBuffer[cpInfoOffset - 1]) {
            case BaseSymbol.CONSTANT_INTEGER_TAG:
                return readInt(cpInfoOffset);
            case BaseSymbol.CONSTANT_FLOAT_TAG:
                return Float.intBitsToFloat(readInt(cpInfoOffset));
            case BaseSymbol.CONSTANT_LONG_TAG:
                return readLong(cpInfoOffset);
            case BaseSymbol.CONSTANT_DOUBLE_TAG:
                return Double.longBitsToDouble(readLong(cpInfoOffset));
            case BaseSymbol.CONSTANT_CLASS_TAG:
                return Type.getObjectType(readUtf8(cpInfoOffset, charBuffer));
            case BaseSymbol.CONSTANT_STRING_TAG:
                return readUtf8(cpInfoOffset, charBuffer);
            case BaseSymbol.CONSTANT_METHOD_TYPE_TAG:
                return Type.getMethodType(readUtf8(cpInfoOffset, charBuffer));
            case BaseSymbol.CONSTANT_METHOD_HANDLE_TAG:
                int referenceKind = readByte(cpInfoOffset);
                int referenceCpInfoOffset = cpInfoOffsets[readUnsignedShort(cpInfoOffset + 1)];
                int nameAndTypeCpInfoOffset = cpInfoOffsets[readUnsignedShort(referenceCpInfoOffset + 2)];
                String owner = readClass(referenceCpInfoOffset, charBuffer);
                String name = readUtf8(nameAndTypeCpInfoOffset, charBuffer);
                String descriptor = readUtf8(nameAndTypeCpInfoOffset + 2, charBuffer);
                boolean isInterface =
                        classFileBuffer[referenceCpInfoOffset - 1] == BaseSymbol.CONSTANT_INTERFACE_METHODREF_TAG;
                return new Handle(referenceKind, owner, name, descriptor, isInterface);
            case BaseSymbol.CONSTANT_DYNAMIC_TAG:
                return readConstantDynamic(constantPoolEntryIndex, charBuffer);
            default:
                throw new IllegalArgumentException();
        }
    }
}
