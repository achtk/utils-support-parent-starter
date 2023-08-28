


























package com.chua.common.support.extra.asm;

/**
 * A {@link MethodVisitor} that generates a corresponding 'method_info' structure, as defined in the
 * Java Virtual Machine Specification (JVMS).
 *
 * @see <a href="https:
 *     4.6</a>
 * @author Eric Bruneton
 * @author Eugene Kuleshov
 */
final class MethodWriter extends MethodVisitor {

  /** Indicates that nothing must be computed. */
  static final int COMPUTE_NOTHING = 0;

  /**
   * Indicates that the maximum stack size and the maximum number of local variables must be
   * computed, from scratch.
   */
  static final int COMPUTE_MAX_STACK_AND_LOCAL = 1;

  /**
   * Indicates that the maximum stack size and the maximum number of local variables must be
   * computed, from the existing stack map frames. This can be done more efficiently than with the
   * control flow graph algorithm used for {@link #COMPUTE_MAX_STACK_AND_LOCAL}, by using a linear
   * scan of the bytecode instructions.
   */
  static final int COMPUTE_MAX_STACK_AND_LOCAL_FROM_FRAMES = 2;

  /**
   * Indicates that the stack map frames of type F_INSERT must be computed. The other frames are not
   * computed. They should all be of type F_NEW and should be sufficient to compute the content of
   * the F_INSERT frames, together with the bytecode instructions between a F_NEW and a F_INSERT
   * frame - and without any knowledge of the type hierarchy (by definition of F_INSERT).
   */
  static final int COMPUTE_INSERTED_FRAMES = 3;

  /**
   * Indicates that all the stack map frames must be computed. In this case the maximum stack size
   * and the maximum number of local variables is also computed.
   */
  static final int COMPUTE_ALL_FRAMES = 4;

  /** Indicates that {@link #STACK_SIZE_DELTA} is not applicable (not constant or never used). */
  private static final int NA = 0;

  /**
   * The stack size variation corresponding to each JVM opcode. The stack size variation for opcode
   * 'o' is given by the array element at index 'o'.
   *
   * @see <a href="https:
   */
  private static final int[] STACK_SIZE_DELTA = {
    0, 
    1, 
    1, 
    1, 
    1, 
    1, 
    1, 
    1, 
    1, 
    2, 
    2, 
    1, 
    1, 
    1, 
    2, 
    2, 
    1, 
    1, 
    1, 
    NA, 
    NA, 
    1, 
    2, 
    1, 
    2, 
    1, 
    NA, 
    NA, 
    NA, 
    NA, 
    NA, 
    NA, 
    NA, 
    NA, 
    NA, 
    NA, 
    NA, 
    NA, 
    NA, 
    NA, 
    NA, 
    NA, 
    NA, 
    NA, 
    NA, 
    NA, 
    -1, 
    0, 
    -1, 
    0, 
    -1, 
    -1, 
    -1, 
    -1, 
    -1, 
    -2, 
    -1, 
    -2, 
    -1, 
    NA, 
    NA, 
    NA, 
    NA, 
    NA, 
    NA, 
    NA, 
    NA, 
    NA, 
    NA, 
    NA, 
    NA, 
    NA, 
    NA, 
    NA, 
    NA, 
    NA, 
    NA, 
    NA, 
    NA, 
    -3, 
    -4, 
    -3, 
    -4, 
    -3, 
    -3, 
    -3, 
    -3, 
    -1, 
    -2, 
    1, 
    1, 
    1, 
    2, 
    2, 
    2, 
    0, 
    -1, 
    -2, 
    -1, 
    -2, 
    -1, 
    -2, 
    -1, 
    -2, 
    -1, 
    -2, 
    -1, 
    -2, 
    -1, 
    -2, 
    -1, 
    -2, 
    -1, 
    -2, 
    -1, 
    -2, 
    0, 
    0, 
    0, 
    0, 
    -1, 
    -1, 
    -1, 
    -1, 
    -1, 
    -1, 
    -1, 
    -2, 
    -1, 
    -2, 
    -1, 
    -2, 
    0, 
    1, 
    0, 
    1, 
    -1, 
    -1, 
    0, 
    0, 
    1, 
    1, 
    -1, 
    0, 
    -1, 
    0, 
    0, 
    0, 
    -3, 
    -1, 
    -1, 
    -3, 
    -3, 
    -1, 
    -1, 
    -1, 
    -1, 
    -1, 
    -1, 
    -2, 
    -2, 
    -2, 
    -2, 
    -2, 
    -2, 
    -2, 
    -2, 
    0, 
    1, 
    0, 
    -1, 
    -1, 
    -1, 
    -2, 
    -1, 
    -2, 
    -1, 
    0, 
    NA, 
    NA, 
    NA, 
    NA, 
    NA, 
    NA, 
    NA, 
    NA, 
    NA, 
    1, 
    0, 
    0, 
    0, 
    NA, 
    0, 
    0, 
    -1, 
    -1, 
    NA, 
    NA, 
    -1, 
    -1, 
    NA, 
    NA 
  };

  /** Where the constants used in this MethodWriter must be stored. */
  private final SymbolTable symbolTable;

  
  

  /**
   * The access_flags field of the method_info JVMS structure. This field can contain ASM specific
   * access flags, such as {@link Opcodes#ACC_DEPRECATED}, which are removed when generating the
   * ClassFile structure.
   */
  private final int accessFlags;

  /** The name_index field of the method_info JVMS structure. */
  private final int nameIndex;

  /** The name of this method. */
  private final String name;

  /** The descriptor_index field of the method_info JVMS structure. */
  private final int descriptorIndex;

  /** The descriptor of this method. */
  private final String descriptor;

  

  /** The max_stack field of the Code attribute. */
  private int maxStack;

  /** The max_locals field of the Code attribute. */
  private int maxLocals;

  /** The 'code' field of the Code attribute. */
  private final ByteVector code = new ByteVector();

  /**
   * The first element in the exception handler list (used to generate the exception_table of the
   * Code attribute). The next ones can be accessed with the {@link Handler#nextHandler} field. May
   * be {@literal null}.
   */
  private Handler firstHandler;

  /**
   * The last element in the exception handler list (used to generate the exception_table of the
   * Code attribute). The next ones can be accessed with the {@link Handler#nextHandler} field. May
   * be {@literal null}.
   */
  private Handler lastHandler;

  /** The line_number_table_length field of the LineNumberTable code attribute. */
  private int lineNumberTableLength;

  /** The line_number_table array of the LineNumberTable code attribute, or {@literal null}. */
  private ByteVector lineNumberTable;

  /** The local_variable_table_length field of the LocalVariableTable code attribute. */
  private int localVariableTableLength;

  /**
   * The local_variable_table array of the LocalVariableTable code attribute, or {@literal null}.
   */
  private ByteVector localVariableTable;

  /** The local_variable_type_table_length field of the LocalVariableTypeTable code attribute. */
  private int localVariableTypeTableLength;

  /**
   * The local_variable_type_table array of the LocalVariableTypeTable code attribute, or {@literal
   * null}.
   */
  private ByteVector localVariableTypeTable;

  /** The number_of_entries field of the StackMapTable code attribute. */
  private int stackMapTableNumberOfEntries;

  /** The 'entries' array of the StackMapTable code attribute. */
  private ByteVector stackMapTableEntries;

  /**
   * The last runtime visible type annotation of the Code attribute. The previous ones can be
   * accessed with the  field. May be {@literal null}.
   */
  private AnnotationWriter lastCodeRuntimeVisibleTypeAnnotation;

  /**
   * The last runtime invisible type annotation of the Code attribute. The previous ones can be
   * accessed with the  field. May be {@literal null}.
   */
  private AnnotationWriter lastCodeRuntimeInvisibleTypeAnnotation;

  /**
   * The first non standard attribute of the Code attribute. The next ones can be accessed with the
   * {@link Attribute#nextAttribute} field. May be {@literal null}.
   *
   * <p><b>WARNING</b>: this list stores the attributes in the <i>reverse</i> order of their visit.
   * firstAttribute is actually the last attribute visited in {@link #visitAttribute}. The {@link
   * #putMethodInfo} method writes the attributes in the order defined by this list, i.e. in the
   * reverse order specified by the user.
   */
  private Attribute firstCodeAttribute;

  

  /** The number_of_exceptions field of the Exceptions attribute. */
  private final int numberOfExceptions;

  /** The exception_index_table array of the Exceptions attribute, or {@literal null}. */
  private final int[] exceptionIndexTable;

  /** The signature_index field of the Signature attribute. */
  private final int signatureIndex;

  /**
   * The last runtime visible annotation of this method. The previous ones can be accessed with the
   *  field. May be {@literal null}.
   */
  private AnnotationWriter lastRuntimeVisibleAnnotation;

  /**
   * The last runtime invisible annotation of this method. The previous ones can be accessed with
   * the  field. May be {@literal null}.
   */
  private AnnotationWriter lastRuntimeInvisibleAnnotation;

  /** The number of method parameters that can have runtime visible annotations, or 0. */
  private int visibleAnnotableParameterCount;

  /**
   * The runtime visible parameter annotations of this method. Each array element contains the last
   * annotation of a parameter (which can be {@literal null} - the previous ones can be accessed
   * with the  field). May be {@literal null}.
   */
  private AnnotationWriter[] lastRuntimeVisibleParameterAnnotations;

  /** The number of method parameters that can have runtime visible annotations, or 0. */
  private int invisibleAnnotableParameterCount;

  /**
   * The runtime invisible parameter annotations of this method. Each array element contains the
   * last annotation of a parameter (which can be {@literal null} - the previous ones can be
   * accessed with the  field). May be {@literal null}.
   */
  private AnnotationWriter[] lastRuntimeInvisibleParameterAnnotations;

  /**
   * The last runtime visible type annotation of this method. The previous ones can be accessed with
   * the  field. May be {@literal null}.
   */
  private AnnotationWriter lastRuntimeVisibleTypeAnnotation;

  /**
   * The last runtime invisible type annotation of this method. The previous ones can be accessed
   * with the  field. May be {@literal null}.
   */
  private AnnotationWriter lastRuntimeInvisibleTypeAnnotation;

  /** The default_value field of the AnnotationDefault attribute, or {@literal null}. */
  private ByteVector defaultValue;

  /** The parameters_count field of the MethodParameters attribute. */
  private int parametersCount;

  /** The 'parameters' array of the MethodParameters attribute, or {@literal null}. */
  private ByteVector parameters;

  /**
   * The first non standard attribute of this method. The next ones can be accessed with the {@link
   * Attribute#nextAttribute} field. May be {@literal null}.
   *
   * <p><b>WARNING</b>: this list stores the attributes in the <i>reverse</i> order of their visit.
   * firstAttribute is actually the last attribute visited in {@link #visitAttribute}. The {@link
   * #putMethodInfo} method writes the attributes in the order defined by this list, i.e. in the
   * reverse order specified by the user.
   */
  private Attribute firstAttribute;

  
  
  

  /**
   * Indicates what must be computed. Must be one of {@link #COMPUTE_ALL_FRAMES}, {@link
   * #COMPUTE_INSERTED_FRAMES},{@link
   * #COMPUTE_MAX_STACK_AND_LOCAL} or {@link #COMPUTE_NOTHING}.
   */
  private final int compute;

  /**
   * The first basic block of the method. The next ones (in bytecode offset order) can be accessed
   * with the {@link Label#nextBasicBlock} field.
   */
  private Label firstBasicBlock;

  /**
   * The last basic block of the method (in bytecode offset order). This field is updated each time
   * a basic block is encountered, and is used to append it at the end of the basic block list.
   */
  private Label lastBasicBlock;

  /**
   * The current basic block, i.e. the basic block of the last visited instruction. When {@link
   * #compute} is equal to {@link #COMPUTE_MAX_STACK_AND_LOCAL} or {@link #COMPUTE_ALL_FRAMES}, this
   * field is {@literal null} for unreachable code. When {@link #compute} is equal to {@link
   * #COMPUTE_MAX_STACK_AND_LOCAL_FROM_FRAMES} or {@link #COMPUTE_INSERTED_FRAMES}, this field stays
   * unchanged throughout the whole method (i.e. the whole code is seen as a single basic block;
   * indeed, the existing frames are sufficient by hypothesis to compute any intermediate frame -
   * and the maximum stack size as well - without using any control flow graph).
   */
  private Label currentBasicBlock;

  /**
   * The relative stack size after the last visited instruction. This size is relative to the
   * beginning of {@link #currentBasicBlock}, i.e. the true stack size after the last visited
   * instruction is equal to the {@link Label#inputStackSize} of the current basic block plus {@link
   * #relativeStackSize}. When {@link #compute} is equal to {@link
   * #COMPUTE_MAX_STACK_AND_LOCAL_FROM_FRAMES}, {@link #currentBasicBlock} is always the start of
   * the method, so this relative size is also equal to the absolute stack size after the last
   * visited instruction.
   */
  private int relativeStackSize;

  /**
   * The maximum relative stack size after the last visited instruction. This size is relative to
   * the beginning of {@link #currentBasicBlock}, i.e. the true maximum stack size after the last
   * visited instruction is equal to the {@link Label#inputStackSize} of the current basic block
   * plus {@link #maxRelativeStackSize}.When {@link #compute} is equal to {@link
   * #COMPUTE_MAX_STACK_AND_LOCAL_FROM_FRAMES}, {@link #currentBasicBlock} is always the start of
   * the method, so this relative size is also equal to the absolute maximum stack size after the
   * last visited instruction.
   */
  private int maxRelativeStackSize;

  /** The number of local variables in the last visited stack map frame. */
  private int currentLocals;

  /** The bytecode offset of the last frame that was written in {@link #stackMapTableEntries}. */
  private int previousFrameOffset;

  /**
   * The last frame that was written in {@link #stackMapTableEntries}. This field has the same
   * format as {@link #currentFrame}.
   */
  private int[] previousFrame;

  /**
   * The current stack map frame. The first element contains the bytecode offset of the instruction
   * to which the frame corresponds, the second element is the number of locals and the third one is
   * the number of stack elements. The local variables start at index 3 and are followed by the
   * operand stack elements. In summary frame[0] = offset, frame[1] = numLocal, frame[2] = numStack.
   * Local variables and operand stack entries contain abstract types, as defined in {@link Frame},
   * but restricted to abstract types. Long and double types use only one array entry.
   */
  private int[] currentFrame;

  /** Whether this method contains subroutines. */
  private boolean hasSubroutines;

  
  
  

  /** Whether the bytecode of this method contains ASM specific instructions. */
  private boolean hasAsmInstructions;

  /**
   * The start offset of the last visited instruction. Used to set the offset field of type
   * annotations of type 'offset_target' (see <a
   * href="https:
   * 4.7.20.1</a>).
   */
  private int lastBytecodeOffset;

  /**
   * The offset in bytes in {@link SymbolTable#getSource} from which the method_info for this method
   * (excluding its first 6 bytes) must be copied, or 0.
   */
  private int sourceOffset;

  /**
   * The length in bytes in {@link SymbolTable#getSource} which must be copied to get the
   * method_info for this method (excluding its first 6 bytes for access_flags, name_index and
   * descriptor_index).
   */
  private int sourceLength;

  
  
  

  /**
   * Constructs a new {@link MethodWriter}.
   *
   * @param symbolTable where the constants used in this AnnotationWriter must be stored.
   * @param access the method's access flags (see {@link Opcodes}).
   * @param name the method's name.
   * @param descriptor the method's descriptor (see {@link Type}).
   * @param signature the method's signature. May be {@literal null}.
   * @param exceptions the internal names of the method's exceptions. May be {@literal null}.
   * @param compute indicates what must be computed (see #compute).
   */
  MethodWriter(
      final SymbolTable symbolTable,
      final int access,
      final String name,
      final String descriptor,
      final String signature,
      final String[] exceptions,
      final int compute) {
    super(/* latest api = */ Opcodes.ASM9);
    this.symbolTable = symbolTable;
    this.accessFlags = "<init>".equals(name) ? access | Constants.ACC_CONSTRUCTOR : access;
    this.nameIndex = symbolTable.addConstantUtf8(name);
    this.name = name;
    this.descriptorIndex = symbolTable.addConstantUtf8(descriptor);
    this.descriptor = descriptor;
    this.signatureIndex = signature == null ? 0 : symbolTable.addConstantUtf8(signature);
    if (exceptions != null && exceptions.length > 0) {
      numberOfExceptions = exceptions.length;
      this.exceptionIndexTable = new int[numberOfExceptions];
      for (int i = 0; i < numberOfExceptions; ++i) {
        this.exceptionIndexTable[i] = symbolTable.addConstantClass(exceptions[i]).index;
      }
    } else {
      numberOfExceptions = 0;
      this.exceptionIndexTable = null;
    }
    this.compute = compute;
    if (compute != COMPUTE_NOTHING) {
      
      int argumentsSize = Type.getArgumentsAndReturnSizes(descriptor) >> 2;
      if ((access & Opcodes.ACC_STATIC) != 0) {
        --argumentsSize;
      }
      maxLocals = argumentsSize;
      currentLocals = argumentsSize;
      
      firstBasicBlock = new Label();
      visitLabel(firstBasicBlock);
    }
  }

  boolean hasFrames() {
    return stackMapTableNumberOfEntries > 0;
  }

  boolean hasAsmInstructions() {
    return hasAsmInstructions;
  }

  
  
  

  @Override
  public void visitParameter(final String name, final int access) {
    if (parameters == null) {
      parameters = new ByteVector();
    }
    ++parametersCount;
    parameters.putShort((name == null) ? 0 : symbolTable.addConstantUtf8(name)).putShort(access);
  }

  @Override
  public AbstractAnnotationVisitor visitAnnotationDefault() {
    defaultValue = new ByteVector();
    return new AnnotationWriter(symbolTable, /* useNamedValues = */ false, defaultValue, null);
  }

  @Override
  public AbstractAnnotationVisitor visitAnnotation(final String descriptor, final boolean visible) {
    if (visible) {
      return lastRuntimeVisibleAnnotation =
          AnnotationWriter.create(symbolTable, descriptor, lastRuntimeVisibleAnnotation);
    } else {
      return lastRuntimeInvisibleAnnotation =
          AnnotationWriter.create(symbolTable, descriptor, lastRuntimeInvisibleAnnotation);
    }
  }

  @Override
  public AbstractAnnotationVisitor visitTypeAnnotation(
      final int typeRef, final TypePath typePath, final String descriptor, final boolean visible) {
    if (visible) {
      return lastRuntimeVisibleTypeAnnotation =
          AnnotationWriter.create(
              symbolTable, typeRef, typePath, descriptor, lastRuntimeVisibleTypeAnnotation);
    } else {
      return lastRuntimeInvisibleTypeAnnotation =
          AnnotationWriter.create(
              symbolTable, typeRef, typePath, descriptor, lastRuntimeInvisibleTypeAnnotation);
    }
  }

  @Override
  public void visitAnnotableParameterCount(final int parameterCount, final boolean visible) {
    if (visible) {
      visibleAnnotableParameterCount = parameterCount;
    } else {
      invisibleAnnotableParameterCount = parameterCount;
    }
  }

  @Override
  public AbstractAnnotationVisitor visitParameterAnnotation(
      final int parameter, final String annotationDescriptor, final boolean visible) {
    if (visible) {
      if (lastRuntimeVisibleParameterAnnotations == null) {
        lastRuntimeVisibleParameterAnnotations =
            new AnnotationWriter[Type.getArgumentTypes(descriptor).length];
      }
      return lastRuntimeVisibleParameterAnnotations[parameter] =
          AnnotationWriter.create(
              symbolTable, annotationDescriptor, lastRuntimeVisibleParameterAnnotations[parameter]);
    } else {
      if (lastRuntimeInvisibleParameterAnnotations == null) {
        lastRuntimeInvisibleParameterAnnotations =
            new AnnotationWriter[Type.getArgumentTypes(descriptor).length];
      }
      return lastRuntimeInvisibleParameterAnnotations[parameter] =
          AnnotationWriter.create(
              symbolTable,
              annotationDescriptor,
              lastRuntimeInvisibleParameterAnnotations[parameter]);
    }
  }

  @Override
  public void visitAttribute(final Attribute attribute) {
    
    if (attribute.isCodeAttribute()) {
      attribute.nextAttribute = firstCodeAttribute;
      firstCodeAttribute = attribute;
    } else {
      attribute.nextAttribute = firstAttribute;
      firstAttribute = attribute;
    }
  }

  @Override
  public void visitCode() {
    
  }

  @Override
  public void visitFrame(
      final int type,
      final int numLocal,
      final Object[] local,
      final int numStack,
      final Object[] stack) {
    if (compute == COMPUTE_ALL_FRAMES) {
      return;
    }

    if (compute == COMPUTE_INSERTED_FRAMES) {
      if (currentBasicBlock.frame == null) {
        
        
        
        currentBasicBlock.frame = new CurrentFrame(currentBasicBlock);
        currentBasicBlock.frame.setInputFrameFromDescriptor(
            symbolTable, accessFlags, descriptor, numLocal);
        currentBasicBlock.frame.accept(this);
      } else {
        if (type == Opcodes.F_NEW) {
          currentBasicBlock.frame.setInputFrameFromApiFormat(
              symbolTable, numLocal, local, numStack, stack);
        }
        
        
        
        currentBasicBlock.frame.accept(this);
      }
    } else if (type == Opcodes.F_NEW) {
      if (previousFrame == null) {
        int argumentsSize = Type.getArgumentsAndReturnSizes(descriptor) >> 2;
        Frame implicitFirstFrame = new Frame(new Label());
        implicitFirstFrame.setInputFrameFromDescriptor(
            symbolTable, accessFlags, descriptor, argumentsSize);
        implicitFirstFrame.accept(this);
      }
      currentLocals = numLocal;
      int frameIndex = visitFrameStart(code.length, numLocal, numStack);
      for (int i = 0; i < numLocal; ++i) {
        currentFrame[frameIndex++] = Frame.getAbstractTypeFromApiFormat(symbolTable, local[i]);
      }
      for (int i = 0; i < numStack; ++i) {
        currentFrame[frameIndex++] = Frame.getAbstractTypeFromApiFormat(symbolTable, stack[i]);
      }
      visitFrameEnd();
    } else {
      if (symbolTable.getMajorVersion() < Opcodes.V1_6) {
        throw new IllegalArgumentException("Class versions V1_5 or less must use F_NEW frames.");
      }
      int offsetDelta;
      if (stackMapTableEntries == null) {
        stackMapTableEntries = new ByteVector();
        offsetDelta = code.length;
      } else {
        offsetDelta = code.length - previousFrameOffset - 1;
        if (offsetDelta < 0) {
          if (type == Opcodes.F_SAME) {
            return;
          } else {
            throw new IllegalStateException();
          }
        }
      }

      switch (type) {
        case Opcodes.F_FULL:
          currentLocals = numLocal;
          stackMapTableEntries.putByte(Frame.FULL_FRAME).putShort(offsetDelta).putShort(numLocal);
          for (int i = 0; i < numLocal; ++i) {
            putFrameType(local[i]);
          }
          stackMapTableEntries.putShort(numStack);
          for (int i = 0; i < numStack; ++i) {
            putFrameType(stack[i]);
          }
          break;
        case Opcodes.F_APPEND:
          currentLocals += numLocal;
          stackMapTableEntries.putByte(Frame.SAME_FRAME_EXTENDED + numLocal).putShort(offsetDelta);
          for (int i = 0; i < numLocal; ++i) {
            putFrameType(local[i]);
          }
          break;
        case Opcodes.F_CHOP:
          currentLocals -= numLocal;
          stackMapTableEntries.putByte(Frame.SAME_FRAME_EXTENDED - numLocal).putShort(offsetDelta);
          break;
        case Opcodes.F_SAME:
          if (offsetDelta < 64) {
            stackMapTableEntries.putByte(offsetDelta);
          } else {
            stackMapTableEntries.putByte(Frame.SAME_FRAME_EXTENDED).putShort(offsetDelta);
          }
          break;
        case Opcodes.F_SAME1:
          if (offsetDelta < 64) {
            stackMapTableEntries.putByte(Frame.SAME_LOCALS_1_STACK_ITEM_FRAME + offsetDelta);
          } else {
            stackMapTableEntries
                .putByte(Frame.SAME_LOCALS_1_STACK_ITEM_FRAME_EXTENDED)
                .putShort(offsetDelta);
          }
          putFrameType(stack[0]);
          break;
        default:
          throw new IllegalArgumentException();
      }

      previousFrameOffset = code.length;
      ++stackMapTableNumberOfEntries;
    }

    if (compute == COMPUTE_MAX_STACK_AND_LOCAL_FROM_FRAMES) {
      relativeStackSize = numStack;
      for (int i = 0; i < numStack; ++i) {
        if (stack[i] == Opcodes.LONG || stack[i] == Opcodes.DOUBLE) {
          relativeStackSize++;
        }
      }
      if (relativeStackSize > maxRelativeStackSize) {
        maxRelativeStackSize = relativeStackSize;
      }
    }

    maxStack = Math.max(maxStack, numStack);
    maxLocals = Math.max(maxLocals, currentLocals);
  }

  @Override
  public void visitInsn(final int opcode) {
    lastBytecodeOffset = code.length;
    
    code.putByte(opcode);
    
    if (currentBasicBlock != null) {
      if (compute == COMPUTE_ALL_FRAMES || compute == COMPUTE_INSERTED_FRAMES) {
        currentBasicBlock.frame.execute(opcode, 0, null, null);
      } else {
        int size = relativeStackSize + STACK_SIZE_DELTA[opcode];
        if (size > maxRelativeStackSize) {
          maxRelativeStackSize = size;
        }
        relativeStackSize = size;
      }
      if ((opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN) || opcode == Opcodes.ATHROW) {
        endCurrentBasicBlockWithNoSuccessor();
      }
    }
  }

  @Override
  public void visitIntInsn(final int opcode, final int operand) {
    lastBytecodeOffset = code.length;
    
    if (opcode == Opcodes.SIPUSH) {
      code.put12(opcode, operand);
    } else { 
      code.put11(opcode, operand);
    }
    
    if (currentBasicBlock != null) {
      if (compute == COMPUTE_ALL_FRAMES || compute == COMPUTE_INSERTED_FRAMES) {
        currentBasicBlock.frame.execute(opcode, operand, null, null);
      } else if (opcode != Opcodes.NEWARRAY) {
        
        int size = relativeStackSize + 1;
        if (size > maxRelativeStackSize) {
          maxRelativeStackSize = size;
        }
        relativeStackSize = size;
      }
    }
  }

  @Override
  public void visitVarInsn(final int opcode, final int varIndex) {
    lastBytecodeOffset = code.length;
    
    if (varIndex < 4 && opcode != Opcodes.RET) {
      int optimizedOpcode;
      if (opcode < Opcodes.ISTORE) {
        optimizedOpcode = Constants.ILOAD_0 + ((opcode - Opcodes.ILOAD) << 2) + varIndex;
      } else {
        optimizedOpcode = Constants.ISTORE_0 + ((opcode - Opcodes.ISTORE) << 2) + varIndex;
      }
      code.putByte(optimizedOpcode);
    } else if (varIndex >= 256) {
      code.putByte(Constants.WIDE).put12(opcode, varIndex);
    } else {
      code.put11(opcode, varIndex);
    }
    
    if (currentBasicBlock != null) {
      if (compute == COMPUTE_ALL_FRAMES || compute == COMPUTE_INSERTED_FRAMES) {
        currentBasicBlock.frame.execute(opcode, varIndex, null, null);
      } else {
        if (opcode == Opcodes.RET) {
          
          currentBasicBlock.flags |= Label.FLAG_SUBROUTINE_END;
          currentBasicBlock.outputStackSize = (short) relativeStackSize;
          endCurrentBasicBlockWithNoSuccessor();
        } else { 
          int size = relativeStackSize + STACK_SIZE_DELTA[opcode];
          if (size > maxRelativeStackSize) {
            maxRelativeStackSize = size;
          }
          relativeStackSize = size;
        }
      }
    }
    if (compute != COMPUTE_NOTHING) {
      int currentMaxLocals;
      if (opcode == Opcodes.LLOAD
          || opcode == Opcodes.DLOAD
          || opcode == Opcodes.LSTORE
          || opcode == Opcodes.DSTORE) {
        currentMaxLocals = varIndex + 2;
      } else {
        currentMaxLocals = varIndex + 1;
      }
      if (currentMaxLocals > maxLocals) {
        maxLocals = currentMaxLocals;
      }
    }
    if (opcode >= Opcodes.ISTORE && compute == COMPUTE_ALL_FRAMES && firstHandler != null) {
      
      
      
      
      
      
      
      visitLabel(new Label());
    }
  }

  @Override
  public void visitTypeInsn(final int opcode, final String type) {
    lastBytecodeOffset = code.length;
    
    Symbol typeSymbol = symbolTable.addConstantClass(type);
    code.put12(opcode, typeSymbol.index);
    
    if (currentBasicBlock != null) {
      if (compute == COMPUTE_ALL_FRAMES || compute == COMPUTE_INSERTED_FRAMES) {
        currentBasicBlock.frame.execute(opcode, lastBytecodeOffset, typeSymbol, symbolTable);
      } else if (opcode == Opcodes.NEW) {
        
        int size = relativeStackSize + 1;
        if (size > maxRelativeStackSize) {
          maxRelativeStackSize = size;
        }
        relativeStackSize = size;
      }
    }
  }

  @Override
  public void visitFieldInsn(
      final int opcode, final String owner, final String name, final String descriptor) {
    lastBytecodeOffset = code.length;
    
    Symbol fieldrefSymbol = symbolTable.addConstantFieldref(owner, name, descriptor);
    code.put12(opcode, fieldrefSymbol.index);
    
    if (currentBasicBlock != null) {
      if (compute == COMPUTE_ALL_FRAMES || compute == COMPUTE_INSERTED_FRAMES) {
        currentBasicBlock.frame.execute(opcode, 0, fieldrefSymbol, symbolTable);
      } else {
        int size;
        char firstDescChar = descriptor.charAt(0);
        switch (opcode) {
          case Opcodes.GETSTATIC:
            size = relativeStackSize + (firstDescChar == 'D' || firstDescChar == 'J' ? 2 : 1);
            break;
          case Opcodes.PUTSTATIC:
            size = relativeStackSize + (firstDescChar == 'D' || firstDescChar == 'J' ? -2 : -1);
            break;
          case Opcodes.GETFIELD:
            size = relativeStackSize + (firstDescChar == 'D' || firstDescChar == 'J' ? 1 : 0);
            break;
          case Opcodes.PUTFIELD:
          default:
            size = relativeStackSize + (firstDescChar == 'D' || firstDescChar == 'J' ? -3 : -2);
            break;
        }
        if (size > maxRelativeStackSize) {
          maxRelativeStackSize = size;
        }
        relativeStackSize = size;
      }
    }
  }

  @Override
  public void visitMethodInsn(
      final int opcode,
      final String owner,
      final String name,
      final String descriptor,
      final boolean isInterface) {
    lastBytecodeOffset = code.length;
    
    Symbol methodrefSymbol = symbolTable.addConstantMethodref(owner, name, descriptor, isInterface);
    if (opcode == Opcodes.INVOKEINTERFACE) {
      code.put12(Opcodes.INVOKEINTERFACE, methodrefSymbol.index)
          .put11(methodrefSymbol.getArgumentsAndReturnSizes() >> 2, 0);
    } else {
      code.put12(opcode, methodrefSymbol.index);
    }
    
    if (currentBasicBlock != null) {
      if (compute == COMPUTE_ALL_FRAMES || compute == COMPUTE_INSERTED_FRAMES) {
        currentBasicBlock.frame.execute(opcode, 0, methodrefSymbol, symbolTable);
      } else {
        int argumentsAndReturnSize = methodrefSymbol.getArgumentsAndReturnSizes();
        int stackSizeDelta = (argumentsAndReturnSize & 3) - (argumentsAndReturnSize >> 2);
        int size;
        if (opcode == Opcodes.INVOKESTATIC) {
          size = relativeStackSize + stackSizeDelta + 1;
        } else {
          size = relativeStackSize + stackSizeDelta;
        }
        if (size > maxRelativeStackSize) {
          maxRelativeStackSize = size;
        }
        relativeStackSize = size;
      }
    }
  }

  @Override
  public void visitInvokeDynamicInsn(
      final String name,
      final String descriptor,
      final Handle bootstrapMethodHandle,
      final Object... bootstrapMethodArguments) {
    lastBytecodeOffset = code.length;
    
    Symbol invokeDynamicSymbol =
        symbolTable.addConstantInvokeDynamic(
            name, descriptor, bootstrapMethodHandle, bootstrapMethodArguments);
    code.put12(Opcodes.INVOKEDYNAMIC, invokeDynamicSymbol.index);
    code.putShort(0);
    
    if (currentBasicBlock != null) {
      if (compute == COMPUTE_ALL_FRAMES || compute == COMPUTE_INSERTED_FRAMES) {
        currentBasicBlock.frame.execute(Opcodes.INVOKEDYNAMIC, 0, invokeDynamicSymbol, symbolTable);
      } else {
        int argumentsAndReturnSize = invokeDynamicSymbol.getArgumentsAndReturnSizes();
        int stackSizeDelta = (argumentsAndReturnSize & 3) - (argumentsAndReturnSize >> 2) + 1;
        int size = relativeStackSize + stackSizeDelta;
        if (size > maxRelativeStackSize) {
          maxRelativeStackSize = size;
        }
        relativeStackSize = size;
      }
    }
  }

  @Override
  public void visitJumpInsn(final int opcode, final Label label) {
    lastBytecodeOffset = code.length;
    
    
    int baseOpcode =
        opcode >= Constants.GOTO_W ? opcode - Constants.WIDE_JUMP_OPCODE_DELTA : opcode;
    boolean nextInsnIsJumpTarget = false;
    if ((label.flags & Label.FLAG_RESOLVED) != 0
        && label.bytecodeOffset - code.length < Short.MIN_VALUE) {
      
      
      
      
      if (baseOpcode == Opcodes.GOTO) {
        code.putByte(Constants.GOTO_W);
      } else if (baseOpcode == Opcodes.JSR) {
        code.putByte(Constants.JSR_W);
      } else {
        
        
        
        code.putByte(baseOpcode >= Opcodes.IFNULL ? baseOpcode ^ 1 : ((baseOpcode + 1) ^ 1) - 1);
        code.putShort(8);
        
        
        
        
        
        
        code.putByte(Constants.ASM_GOTO_W);
        hasAsmInstructions = true;
        
        nextInsnIsJumpTarget = true;
      }
      label.put(code, code.length - 1, true);
    } else if (baseOpcode != opcode) {
      
      
      code.putByte(opcode);
      label.put(code, code.length - 1, true);
    } else {
      
      
      
      code.putByte(baseOpcode);
      label.put(code, code.length - 1, false);
    }

    
    if (currentBasicBlock != null) {
      Label nextBasicBlock = null;
      if (compute == COMPUTE_ALL_FRAMES) {
        currentBasicBlock.frame.execute(baseOpcode, 0, null, null);
        
        label.getCanonicalInstance().flags |= Label.FLAG_JUMP_TARGET;
        
        addSuccessorToCurrentBasicBlock(Edge.JUMP, label);
        if (baseOpcode != Opcodes.GOTO) {
          
          
          
          nextBasicBlock = new Label();
        }
      } else if (compute == COMPUTE_INSERTED_FRAMES) {
        currentBasicBlock.frame.execute(baseOpcode, 0, null, null);
      } else if (compute == COMPUTE_MAX_STACK_AND_LOCAL_FROM_FRAMES) {
        
        relativeStackSize += STACK_SIZE_DELTA[baseOpcode];
      } else {
        if (baseOpcode == Opcodes.JSR) {
          
          if ((label.flags & Label.FLAG_SUBROUTINE_START) == 0) {
            label.flags |= Label.FLAG_SUBROUTINE_START;
            hasSubroutines = true;
          }
          currentBasicBlock.flags |= Label.FLAG_SUBROUTINE_CALLER;
          
          
          
          
          
          
          addSuccessorToCurrentBasicBlock(relativeStackSize + 1, label);
          
          nextBasicBlock = new Label();
        } else {
          
          relativeStackSize += STACK_SIZE_DELTA[baseOpcode];
          addSuccessorToCurrentBasicBlock(relativeStackSize, label);
        }
      }
      
      
      if (nextBasicBlock != null) {
        if (nextInsnIsJumpTarget) {
          nextBasicBlock.flags |= Label.FLAG_JUMP_TARGET;
        }
        visitLabel(nextBasicBlock);
      }
      if (baseOpcode == Opcodes.GOTO) {
        endCurrentBasicBlockWithNoSuccessor();
      }
    }
  }

  @Override
  public void visitLabel(final Label label) {
    
    hasAsmInstructions |= label.resolve(code.data, code.length);
    
    
    if ((label.flags & Label.FLAG_DEBUG_ONLY) != 0) {
      return;
    }
    if (compute == COMPUTE_ALL_FRAMES) {
      if (currentBasicBlock != null) {
        if (label.bytecodeOffset == currentBasicBlock.bytecodeOffset) {
          
          
          
          
          currentBasicBlock.flags |= (label.flags & Label.FLAG_JUMP_TARGET);
          
          
          
          label.frame = currentBasicBlock.frame;
          
          
          return;
        }
        
        addSuccessorToCurrentBasicBlock(Edge.JUMP, label);
      }
      
      if (lastBasicBlock != null) {
        if (label.bytecodeOffset == lastBasicBlock.bytecodeOffset) {
          
          lastBasicBlock.flags |= (label.flags & Label.FLAG_JUMP_TARGET);
          
          label.frame = lastBasicBlock.frame;
          currentBasicBlock = lastBasicBlock;
          return;
        }
        lastBasicBlock.nextBasicBlock = label;
      }
      lastBasicBlock = label;
      
      currentBasicBlock = label;
      
      label.frame = new Frame(label);
    } else if (compute == COMPUTE_INSERTED_FRAMES) {
      if (currentBasicBlock == null) {
        
        
        currentBasicBlock = label;
      } else {
        
        currentBasicBlock.frame.owner = label;
      }
    } else if (compute == COMPUTE_MAX_STACK_AND_LOCAL) {
      if (currentBasicBlock != null) {
        
        currentBasicBlock.outputStackMax = (short) maxRelativeStackSize;
        addSuccessorToCurrentBasicBlock(relativeStackSize, label);
      }
      
      currentBasicBlock = label;
      relativeStackSize = 0;
      maxRelativeStackSize = 0;
      
      if (lastBasicBlock != null) {
        lastBasicBlock.nextBasicBlock = label;
      }
      lastBasicBlock = label;
    } else if (compute == COMPUTE_MAX_STACK_AND_LOCAL_FROM_FRAMES && currentBasicBlock == null) {
      
      
      
      currentBasicBlock = label;
    }
  }

  @Override
  public void visitLdcInsn(final Object value) {
    lastBytecodeOffset = code.length;
    
    Symbol constantSymbol = symbolTable.addConstant(value);
    int constantIndex = constantSymbol.index;
    char firstDescriptorChar;
    boolean isLongOrDouble =
        constantSymbol.tag == Symbol.CONSTANT_LONG_TAG
            || constantSymbol.tag == Symbol.CONSTANT_DOUBLE_TAG
            || (constantSymbol.tag == Symbol.CONSTANT_DYNAMIC_TAG
                && ((firstDescriptorChar = constantSymbol.value.charAt(0)) == 'J'
                    || firstDescriptorChar == 'D'));
    if (isLongOrDouble) {
      code.put12(Constants.LDC2_W, constantIndex);
    } else if (constantIndex >= 256) {
      code.put12(Constants.LDC_W, constantIndex);
    } else {
      code.put11(Opcodes.LDC, constantIndex);
    }
    
    if (currentBasicBlock != null) {
      if (compute == COMPUTE_ALL_FRAMES || compute == COMPUTE_INSERTED_FRAMES) {
        currentBasicBlock.frame.execute(Opcodes.LDC, 0, constantSymbol, symbolTable);
      } else {
        int size = relativeStackSize + (isLongOrDouble ? 2 : 1);
        if (size > maxRelativeStackSize) {
          maxRelativeStackSize = size;
        }
        relativeStackSize = size;
      }
    }
  }

  @Override
  public void visitIincInsn(final int varIndex, final int increment) {
    lastBytecodeOffset = code.length;
    
    if ((varIndex > 255) || (increment > 127) || (increment < -128)) {
      code.putByte(Constants.WIDE).put12(Opcodes.IINC, varIndex).putShort(increment);
    } else {
      code.putByte(Opcodes.IINC).put11(varIndex, increment);
    }
    
    if (currentBasicBlock != null
        && (compute == COMPUTE_ALL_FRAMES || compute == COMPUTE_INSERTED_FRAMES)) {
      currentBasicBlock.frame.execute(Opcodes.IINC, varIndex, null, null);
    }
    if (compute != COMPUTE_NOTHING) {
      int currentMaxLocals = varIndex + 1;
      if (currentMaxLocals > maxLocals) {
        maxLocals = currentMaxLocals;
      }
    }
  }

  @Override
  public void visitTableSwitchInsn(
      final int min, final int max, final Label dflt, final Label... labels) {
    lastBytecodeOffset = code.length;
    
    code.putByte(Opcodes.TABLESWITCH).putByteArray(null, 0, (4 - code.length % 4) % 4);
    dflt.put(code, lastBytecodeOffset, true);
    code.putInt(min).putInt(max);
    for (Label label : labels) {
      label.put(code, lastBytecodeOffset, true);
    }
    
    visitSwitchInsn(dflt, labels);
  }

  @Override
  public void visitLookupSwitchInsn(final Label dflt, final int[] keys, final Label[] labels) {
    lastBytecodeOffset = code.length;
    
    code.putByte(Opcodes.LOOKUPSWITCH).putByteArray(null, 0, (4 - code.length % 4) % 4);
    dflt.put(code, lastBytecodeOffset, true);
    code.putInt(labels.length);
    for (int i = 0; i < labels.length; ++i) {
      code.putInt(keys[i]);
      labels[i].put(code, lastBytecodeOffset, true);
    }
    
    visitSwitchInsn(dflt, labels);
  }

  private void visitSwitchInsn(final Label dflt, final Label[] labels) {
    if (currentBasicBlock != null) {
      if (compute == COMPUTE_ALL_FRAMES) {
        currentBasicBlock.frame.execute(Opcodes.LOOKUPSWITCH, 0, null, null);
        
        addSuccessorToCurrentBasicBlock(Edge.JUMP, dflt);
        dflt.getCanonicalInstance().flags |= Label.FLAG_JUMP_TARGET;
        for (Label label : labels) {
          addSuccessorToCurrentBasicBlock(Edge.JUMP, label);
          label.getCanonicalInstance().flags |= Label.FLAG_JUMP_TARGET;
        }
      } else if (compute == COMPUTE_MAX_STACK_AND_LOCAL) {
        
        --relativeStackSize;
        
        addSuccessorToCurrentBasicBlock(relativeStackSize, dflt);
        for (Label label : labels) {
          addSuccessorToCurrentBasicBlock(relativeStackSize, label);
        }
      }
      
      endCurrentBasicBlockWithNoSuccessor();
    }
  }

  @Override
  public void visitMultiANewArrayInsn(final String descriptor, final int numDimensions) {
    lastBytecodeOffset = code.length;
    
    Symbol descSymbol = symbolTable.addConstantClass(descriptor);
    code.put12(Opcodes.MULTIANEWARRAY, descSymbol.index).putByte(numDimensions);
    
    if (currentBasicBlock != null) {
      if (compute == COMPUTE_ALL_FRAMES || compute == COMPUTE_INSERTED_FRAMES) {
        currentBasicBlock.frame.execute(
            Opcodes.MULTIANEWARRAY, numDimensions, descSymbol, symbolTable);
      } else {
        
        relativeStackSize += 1 - numDimensions;
      }
    }
  }

  @Override
  public AbstractAnnotationVisitor visitInsnAnnotation(
      final int typeRef, final TypePath typePath, final String descriptor, final boolean visible) {
    if (visible) {
      return lastCodeRuntimeVisibleTypeAnnotation =
          AnnotationWriter.create(
              symbolTable,
              (typeRef & 0xFF0000FF) | (lastBytecodeOffset << 8),
              typePath,
              descriptor,
              lastCodeRuntimeVisibleTypeAnnotation);
    } else {
      return lastCodeRuntimeInvisibleTypeAnnotation =
          AnnotationWriter.create(
              symbolTable,
              (typeRef & 0xFF0000FF) | (lastBytecodeOffset << 8),
              typePath,
              descriptor,
              lastCodeRuntimeInvisibleTypeAnnotation);
    }
  }

  @Override
  public void visitTryCatchBlock(
      final Label start, final Label end, final Label handler, final String type) {
    Handler newHandler =
        new Handler(
            start, end, handler, type != null ? symbolTable.addConstantClass(type).index : 0, type);
    if (firstHandler == null) {
      firstHandler = newHandler;
    } else {
      lastHandler.nextHandler = newHandler;
    }
    lastHandler = newHandler;
  }

  @Override
  public AbstractAnnotationVisitor visitTryCatchAnnotation(
      final int typeRef, final TypePath typePath, final String descriptor, final boolean visible) {
    if (visible) {
      return lastCodeRuntimeVisibleTypeAnnotation =
          AnnotationWriter.create(
              symbolTable, typeRef, typePath, descriptor, lastCodeRuntimeVisibleTypeAnnotation);
    } else {
      return lastCodeRuntimeInvisibleTypeAnnotation =
          AnnotationWriter.create(
              symbolTable, typeRef, typePath, descriptor, lastCodeRuntimeInvisibleTypeAnnotation);
    }
  }

  @Override
  public void visitLocalVariable(
      final String name,
      final String descriptor,
      final String signature,
      final Label start,
      final Label end,
      final int index) {
    if (signature != null) {
      if (localVariableTypeTable == null) {
        localVariableTypeTable = new ByteVector();
      }
      ++localVariableTypeTableLength;
      localVariableTypeTable
          .putShort(start.bytecodeOffset)
          .putShort(end.bytecodeOffset - start.bytecodeOffset)
          .putShort(symbolTable.addConstantUtf8(name))
          .putShort(symbolTable.addConstantUtf8(signature))
          .putShort(index);
    }
    if (localVariableTable == null) {
      localVariableTable = new ByteVector();
    }
    ++localVariableTableLength;
    localVariableTable
        .putShort(start.bytecodeOffset)
        .putShort(end.bytecodeOffset - start.bytecodeOffset)
        .putShort(symbolTable.addConstantUtf8(name))
        .putShort(symbolTable.addConstantUtf8(descriptor))
        .putShort(index);
    if (compute != COMPUTE_NOTHING) {
      char firstDescChar = descriptor.charAt(0);
      int currentMaxLocals = index + (firstDescChar == 'J' || firstDescChar == 'D' ? 2 : 1);
      if (currentMaxLocals > maxLocals) {
        maxLocals = currentMaxLocals;
      }
    }
  }

  @Override
  public AbstractAnnotationVisitor visitLocalVariableAnnotation(
      final int typeRef,
      final TypePath typePath,
      final Label[] start,
      final Label[] end,
      final int[] index,
      final String descriptor,
      final boolean visible) {
    
    
    ByteVector typeAnnotation = new ByteVector();
    
    typeAnnotation.putByte(typeRef >>> 24).putShort(start.length);
    for (int i = 0; i < start.length; ++i) {
      typeAnnotation
          .putShort(start[i].bytecodeOffset)
          .putShort(end[i].bytecodeOffset - start[i].bytecodeOffset)
          .putShort(index[i]);
    }
    TypePath.put(typePath, typeAnnotation);
    
    typeAnnotation.putShort(symbolTable.addConstantUtf8(descriptor)).putShort(0);
    if (visible) {
      return lastCodeRuntimeVisibleTypeAnnotation =
          new AnnotationWriter(
              symbolTable,
              /* useNamedValues = */ true,
              typeAnnotation,
              lastCodeRuntimeVisibleTypeAnnotation);
    } else {
      return lastCodeRuntimeInvisibleTypeAnnotation =
          new AnnotationWriter(
              symbolTable,
              /* useNamedValues = */ true,
              typeAnnotation,
              lastCodeRuntimeInvisibleTypeAnnotation);
    }
  }

  @Override
  public void visitLineNumber(final int line, final Label start) {
    if (lineNumberTable == null) {
      lineNumberTable = new ByteVector();
    }
    ++lineNumberTableLength;
    lineNumberTable.putShort(start.bytecodeOffset);
    lineNumberTable.putShort(line);
  }

  @Override
  public void visitMaxs(final int maxStack, final int maxLocals) {
    if (compute == COMPUTE_ALL_FRAMES) {
      computeAllFrames();
    } else if (compute == COMPUTE_MAX_STACK_AND_LOCAL) {
      computeMaxStackAndLocal();
    } else if (compute == COMPUTE_MAX_STACK_AND_LOCAL_FROM_FRAMES) {
      this.maxStack = maxRelativeStackSize;
    } else {
      this.maxStack = maxStack;
      this.maxLocals = maxLocals;
    }
  }

  /** Computes all the stack map frames of the method, from scratch. */
  private void computeAllFrames() {
    
    Handler handler = firstHandler;
    while (handler != null) {
      String catchTypeDescriptor =
          handler.catchTypeDescriptor == null ? "java/lang/Throwable" : handler.catchTypeDescriptor;
      int catchType = Frame.getAbstractTypeFromInternalName(symbolTable, catchTypeDescriptor);
      
      Label handlerBlock = handler.handlerPc.getCanonicalInstance();
      handlerBlock.flags |= Label.FLAG_JUMP_TARGET;
      
      Label handlerRangeBlock = handler.startPc.getCanonicalInstance();
      Label handlerRangeEnd = handler.endPc.getCanonicalInstance();
      while (handlerRangeBlock != handlerRangeEnd) {
        handlerRangeBlock.outgoingEdges =
            new Edge(catchType, handlerBlock, handlerRangeBlock.outgoingEdges);
        handlerRangeBlock = handlerRangeBlock.nextBasicBlock;
      }
      handler = handler.nextHandler;
    }

    
    Frame firstFrame = firstBasicBlock.frame;
    firstFrame.setInputFrameFromDescriptor(symbolTable, accessFlags, descriptor, this.maxLocals);
    firstFrame.accept(this);

    
    
    
    
    
    
    Label listOfBlocksToProcess = firstBasicBlock;
    listOfBlocksToProcess.nextListElement = Label.EMPTY_LIST;
    int maxStackSize = 0;
    while (listOfBlocksToProcess != Label.EMPTY_LIST) {
      
      Label basicBlock = listOfBlocksToProcess;
      listOfBlocksToProcess = listOfBlocksToProcess.nextListElement;
      basicBlock.nextListElement = null;
      
      basicBlock.flags |= Label.FLAG_REACHABLE;
      
      int maxBlockStackSize = basicBlock.frame.getInputStackSize() + basicBlock.outputStackMax;
      if (maxBlockStackSize > maxStackSize) {
        maxStackSize = maxBlockStackSize;
      }
      
      Edge outgoingEdge = basicBlock.outgoingEdges;
      while (outgoingEdge != null) {
        Label successorBlock = outgoingEdge.successor.getCanonicalInstance();
        boolean successorBlockChanged =
            basicBlock.frame.merge(symbolTable, successorBlock.frame, outgoingEdge.info);
        if (successorBlockChanged && successorBlock.nextListElement == null) {
          
          
          successorBlock.nextListElement = listOfBlocksToProcess;
          listOfBlocksToProcess = successorBlock;
        }
        outgoingEdge = outgoingEdge.nextEdge;
      }
    }

    
    
    
    Label basicBlock = firstBasicBlock;
    while (basicBlock != null) {
      if ((basicBlock.flags & (Label.FLAG_JUMP_TARGET | Label.FLAG_REACHABLE))
          == (Label.FLAG_JUMP_TARGET | Label.FLAG_REACHABLE)) {
        basicBlock.frame.accept(this);
      }
      if ((basicBlock.flags & Label.FLAG_REACHABLE) == 0) {
        
        Label nextBasicBlock = basicBlock.nextBasicBlock;
        int startOffset = basicBlock.bytecodeOffset;
        int endOffset = (nextBasicBlock == null ? code.length : nextBasicBlock.bytecodeOffset) - 1;
        if (endOffset >= startOffset) {
          
          for (int i = startOffset; i < endOffset; ++i) {
            code.data[i] = Opcodes.NOP;
          }
          code.data[endOffset] = (byte) Opcodes.ATHROW;
          
          
          int frameIndex = visitFrameStart(startOffset, /* numLocal = */ 0, /* numStack = */ 1);
          currentFrame[frameIndex] =
              Frame.getAbstractTypeFromInternalName(symbolTable, "java/lang/Throwable");
          visitFrameEnd();
          
          firstHandler = Handler.removeRange(firstHandler, basicBlock, nextBasicBlock);
          
          maxStackSize = Math.max(maxStackSize, 1);
        }
      }
      basicBlock = basicBlock.nextBasicBlock;
    }

    this.maxStack = maxStackSize;
  }

  /** Computes the maximum stack size of the method. */
  private void computeMaxStackAndLocal() {
    
    Handler handler = firstHandler;
    while (handler != null) {
      Label handlerBlock = handler.handlerPc;
      Label handlerRangeBlock = handler.startPc;
      Label handlerRangeEnd = handler.endPc;
      
      while (handlerRangeBlock != handlerRangeEnd) {
        if ((handlerRangeBlock.flags & Label.FLAG_SUBROUTINE_CALLER) == 0) {
          handlerRangeBlock.outgoingEdges =
              new Edge(Edge.EXCEPTION, handlerBlock, handlerRangeBlock.outgoingEdges);
        } else {
          
          
          
          handlerRangeBlock.outgoingEdges.nextEdge.nextEdge =
              new Edge(
                  Edge.EXCEPTION, handlerBlock, handlerRangeBlock.outgoingEdges.nextEdge.nextEdge);
        }
        handlerRangeBlock = handlerRangeBlock.nextBasicBlock;
      }
      handler = handler.nextHandler;
    }

    
    if (hasSubroutines) {
      
      
      short numSubroutines = 1;
      firstBasicBlock.markSubroutine(numSubroutines);
      
      
      for (short currentSubroutine = 1; currentSubroutine <= numSubroutines; ++currentSubroutine) {
        Label basicBlock = firstBasicBlock;
        while (basicBlock != null) {
          if ((basicBlock.flags & Label.FLAG_SUBROUTINE_CALLER) != 0
              && basicBlock.subroutineId == currentSubroutine) {
            Label jsrTarget = basicBlock.outgoingEdges.nextEdge.successor;
            if (jsrTarget.subroutineId == 0) {
              
              jsrTarget.markSubroutine(++numSubroutines);
            }
          }
          basicBlock = basicBlock.nextBasicBlock;
        }
      }
      
      
      
      Label basicBlock = firstBasicBlock;
      while (basicBlock != null) {
        if ((basicBlock.flags & Label.FLAG_SUBROUTINE_CALLER) != 0) {
          
          
          Label subroutine = basicBlock.outgoingEdges.nextEdge.successor;
          subroutine.addSubroutineRetSuccessors(basicBlock);
        }
        basicBlock = basicBlock.nextBasicBlock;
      }
    }

    
    
    
    
    Label listOfBlocksToProcess = firstBasicBlock;
    listOfBlocksToProcess.nextListElement = Label.EMPTY_LIST;
    int maxStackSize = maxStack;
    while (listOfBlocksToProcess != Label.EMPTY_LIST) {
      
      
      
      Label basicBlock = listOfBlocksToProcess;
      listOfBlocksToProcess = listOfBlocksToProcess.nextListElement;
      
      int inputStackTop = basicBlock.inputStackSize;
      int maxBlockStackSize = inputStackTop + basicBlock.outputStackMax;
      
      if (maxBlockStackSize > maxStackSize) {
        maxStackSize = maxBlockStackSize;
      }
      
      
      Edge outgoingEdge = basicBlock.outgoingEdges;
      if ((basicBlock.flags & Label.FLAG_SUBROUTINE_CALLER) != 0) {
        
        
        
        
        outgoingEdge = outgoingEdge.nextEdge;
      }
      while (outgoingEdge != null) {
        Label successorBlock = outgoingEdge.successor;
        if (successorBlock.nextListElement == null) {
          successorBlock.inputStackSize =
              (short) (outgoingEdge.info == Edge.EXCEPTION ? 1 : inputStackTop + outgoingEdge.info);
          successorBlock.nextListElement = listOfBlocksToProcess;
          listOfBlocksToProcess = successorBlock;
        }
        outgoingEdge = outgoingEdge.nextEdge;
      }
    }
    this.maxStack = maxStackSize;
  }

  @Override
  public void visitEnd() {
    
  }

  
  
  

  /**
   * Adds a successor to {@link #currentBasicBlock} in the control flow graph.
   *
   * @param info information about the control flow edge to be added.
   * @param successor the successor block to be added to the current basic block.
   */
  private void addSuccessorToCurrentBasicBlock(final int info, final Label successor) {
    currentBasicBlock.outgoingEdges = new Edge(info, successor, currentBasicBlock.outgoingEdges);
  }

  /**
   * Ends the current basic block. This method must be used in the case where the current basic
   * block does not have any successor.
   *
   * <p>WARNING: this method must be called after the currently visited instruction has been put in
   * {@link #code} (if frames are computed, this method inserts a new Label to start a new basic
   * block after the current instruction).
   */
  private void endCurrentBasicBlockWithNoSuccessor() {
    if (compute == COMPUTE_ALL_FRAMES) {
      Label nextBasicBlock = new Label();
      nextBasicBlock.frame = new Frame(nextBasicBlock);
      nextBasicBlock.resolve(code.data, code.length);
      lastBasicBlock.nextBasicBlock = nextBasicBlock;
      lastBasicBlock = nextBasicBlock;
      currentBasicBlock = null;
    } else if (compute == COMPUTE_MAX_STACK_AND_LOCAL) {
      currentBasicBlock.outputStackMax = (short) maxRelativeStackSize;
      currentBasicBlock = null;
    }
  }

  
  
  

  /**
   * Starts the visit of a new stack map frame, stored in {@link #currentFrame}.
   *
   * @param offset the bytecode offset of the instruction to which the frame corresponds.
   * @param numLocal the number of local variables in the frame.
   * @param numStack the number of stack elements in the frame.
   * @return the index of the next element to be written in this frame.
   */
  int visitFrameStart(final int offset, final int numLocal, final int numStack) {
    int frameLength = 3 + numLocal + numStack;
    if (currentFrame == null || currentFrame.length < frameLength) {
      currentFrame = new int[frameLength];
    }
    currentFrame[0] = offset;
    currentFrame[1] = numLocal;
    currentFrame[2] = numStack;
    return 3;
  }

  /**
   * Sets an abstract type in {@link #currentFrame}.
   *
   * @param frameIndex the index of the element to be set in {@link #currentFrame}.
   * @param abstractType an abstract type.
   */
  void visitAbstractType(final int frameIndex, final int abstractType) {
    currentFrame[frameIndex] = abstractType;
  }

  /**
   * Ends the visit of {@link #currentFrame} by writing it in the StackMapTable entries and by
   * updating the StackMapTable number_of_entries (except if the current frame is the first one,
   * which is implicit in StackMapTable). Then resets {@link #currentFrame} to {@literal null}.
   */
  void visitFrameEnd() {
    if (previousFrame != null) {
      if (stackMapTableEntries == null) {
        stackMapTableEntries = new ByteVector();
      }
      putFrame();
      ++stackMapTableNumberOfEntries;
    }
    previousFrame = currentFrame;
    currentFrame = null;
  }

  /** Compresses and writes {@link #currentFrame} in a new StackMapTable entry. */
  private void putFrame() {
    final int numLocal = currentFrame[1];
    final int numStack = currentFrame[2];
    if (symbolTable.getMajorVersion() < Opcodes.V1_6) {
      
      stackMapTableEntries.putShort(currentFrame[0]).putShort(numLocal);
      putAbstractTypes(3, 3 + numLocal);
      stackMapTableEntries.putShort(numStack);
      putAbstractTypes(3 + numLocal, 3 + numLocal + numStack);
      return;
    }
    final int offsetDelta =
        stackMapTableNumberOfEntries == 0
            ? currentFrame[0]
            : currentFrame[0] - previousFrame[0] - 1;
    final int previousNumlocal = previousFrame[1];
    final int numLocalDelta = numLocal - previousNumlocal;
    int type = Frame.FULL_FRAME;
    if (numStack == 0) {
      switch (numLocalDelta) {
        case -3:
        case -2:
        case -1:
          type = Frame.CHOP_FRAME;
          break;
        case 0:
          type = offsetDelta < 64 ? Frame.SAME_FRAME : Frame.SAME_FRAME_EXTENDED;
          break;
        case 1:
        case 2:
        case 3:
          type = Frame.APPEND_FRAME;
          break;
        default:
          
          break;
      }
    } else if (numLocalDelta == 0 && numStack == 1) {
      type =
          offsetDelta < 63
              ? Frame.SAME_LOCALS_1_STACK_ITEM_FRAME
              : Frame.SAME_LOCALS_1_STACK_ITEM_FRAME_EXTENDED;
    }
    if (type != Frame.FULL_FRAME) {
      
      int frameIndex = 3;
      for (int i = 0; i < previousNumlocal && i < numLocal; i++) {
        if (currentFrame[frameIndex] != previousFrame[frameIndex]) {
          type = Frame.FULL_FRAME;
          break;
        }
        frameIndex++;
      }
    }
    switch (type) {
      case Frame.SAME_FRAME:
        stackMapTableEntries.putByte(offsetDelta);
        break;
      case Frame.SAME_LOCALS_1_STACK_ITEM_FRAME:
        stackMapTableEntries.putByte(Frame.SAME_LOCALS_1_STACK_ITEM_FRAME + offsetDelta);
        putAbstractTypes(3 + numLocal, 4 + numLocal);
        break;
      case Frame.SAME_LOCALS_1_STACK_ITEM_FRAME_EXTENDED:
        stackMapTableEntries
            .putByte(Frame.SAME_LOCALS_1_STACK_ITEM_FRAME_EXTENDED)
            .putShort(offsetDelta);
        putAbstractTypes(3 + numLocal, 4 + numLocal);
        break;
      case Frame.SAME_FRAME_EXTENDED:
        stackMapTableEntries.putByte(Frame.SAME_FRAME_EXTENDED).putShort(offsetDelta);
        break;
      case Frame.CHOP_FRAME:
        stackMapTableEntries
            .putByte(Frame.SAME_FRAME_EXTENDED + numLocalDelta)
            .putShort(offsetDelta);
        break;
      case Frame.APPEND_FRAME:
        stackMapTableEntries
            .putByte(Frame.SAME_FRAME_EXTENDED + numLocalDelta)
            .putShort(offsetDelta);
        putAbstractTypes(3 + previousNumlocal, 3 + numLocal);
        break;
      case Frame.FULL_FRAME:
      default:
        stackMapTableEntries.putByte(Frame.FULL_FRAME).putShort(offsetDelta).putShort(numLocal);
        putAbstractTypes(3, 3 + numLocal);
        stackMapTableEntries.putShort(numStack);
        putAbstractTypes(3 + numLocal, 3 + numLocal + numStack);
        break;
    }
  }

  /**
   * Puts some abstract types of {@link #currentFrame} in {@link #stackMapTableEntries} , using the
   * JVMS verification_type_info format used in StackMapTable attributes.
   *
   * @param start index of the first type in {@link #currentFrame} to write.
   * @param end index of last type in {@link #currentFrame} to write (exclusive).
   */
  private void putAbstractTypes(final int start, final int end) {
    for (int i = start; i < end; ++i) {
      Frame.putAbstractType(symbolTable, currentFrame[i], stackMapTableEntries);
    }
  }

  /**
   * Puts the given public API frame element type in {@link #stackMapTableEntries} , using the JVMS
   * verification_type_info format used in StackMapTable attributes.
   *
   * @param type a frame element type described using the same format as in {@link
   *     MethodVisitor#visitFrame}, i.e. either {@link Opcodes#TOP}, {@link Opcodes#INTEGER}, {@link
   *     Opcodes#FLOAT}, {@link Opcodes#LONG}, {@link Opcodes#DOUBLE}, {@link Opcodes#NULL}, or
   *     {@link Opcodes#UNINITIALIZED_THIS}, or the internal name of a class, or a Label designating
   *     a NEW instruction (for uninitialized types).
   */
  private void putFrameType(final Object type) {
    if (type instanceof Integer) {
      stackMapTableEntries.putByte(((Integer) type).intValue());
    } else if (type instanceof String) {
      stackMapTableEntries
          .putByte(Frame.ITEM_OBJECT)
          .putShort(symbolTable.addConstantClass((String) type).index);
    } else {
      stackMapTableEntries
          .putByte(Frame.ITEM_UNINITIALIZED)
          .putShort(((Label) type).bytecodeOffset);
    }
  }

  
  
  

  /**
   * Returns whether the attributes of this method can be copied from the attributes of the given
   * method (assuming there is no method visitor between the given ClassReader and this
   * MethodWriter). This method should only be called just after this MethodWriter has been created,
   * and before any content is visited. It returns true if the attributes corresponding to the
   * constructor arguments (at most a Signature, an Exception, a Deprecated and a Synthetic
   * attribute) are the same as the corresponding attributes in the given method.
   *
   * @param source the source ClassReader from which the attributes of this method might be copied.
   * @param hasSyntheticAttribute whether the method_info JVMS structure from which the attributes
   *     of this method might be copied contains a Synthetic attribute.
   * @param hasDeprecatedAttribute whether the method_info JVMS structure from which the attributes
   *     of this method might be copied contains a Deprecated attribute.
   * @param descriptorIndex the descriptor_index field of the method_info JVMS structure from which
   *     the attributes of this method might be copied.
   * @param signatureIndex the constant pool index contained in the Signature attribute of the
   *     method_info JVMS structure from which the attributes of this method might be copied, or 0.
   * @param exceptionsOffset the offset in 'source.b' of the Exceptions attribute of the method_info
   *     JVMS structure from which the attributes of this method might be copied, or 0.
   * @return whether the attributes of this method can be copied from the attributes of the
   *     method_info JVMS structure in 'source.b', between 'methodInfoOffset' and 'methodInfoOffset'
   *     + 'methodInfoLength'.
   */
  boolean canCopyMethodAttributes(
      final ClassReader source,
      final boolean hasSyntheticAttribute,
      final boolean hasDeprecatedAttribute,
      final int descriptorIndex,
      final int signatureIndex,
      final int exceptionsOffset) {
    
    
    
    
    
    if (source != symbolTable.getSource()
        || descriptorIndex != this.descriptorIndex
        || signatureIndex != this.signatureIndex
        || hasDeprecatedAttribute != ((accessFlags & Opcodes.ACC_DEPRECATED) != 0)) {
      return false;
    }
    boolean needSyntheticAttribute =
        symbolTable.getMajorVersion() < Opcodes.V1_5 && (accessFlags & Opcodes.ACC_SYNTHETIC) != 0;
    if (hasSyntheticAttribute != needSyntheticAttribute) {
      return false;
    }
    if (exceptionsOffset == 0) {
      if (numberOfExceptions != 0) {
        return false;
      }
    } else if (source.readUnsignedShort(exceptionsOffset) == numberOfExceptions) {
      int currentExceptionOffset = exceptionsOffset + 2;
      for (int i = 0; i < numberOfExceptions; ++i) {
        if (source.readUnsignedShort(currentExceptionOffset) != exceptionIndexTable[i]) {
          return false;
        }
        currentExceptionOffset += 2;
      }
    }
    return true;
  }

  /**
   * Sets the source from which the attributes of this method will be copied.
   *
   * @param methodInfoOffset the offset in 'symbolTable.getSource()' of the method_info JVMS
   *     structure from which the attributes of this method will be copied.
   * @param methodInfoLength the length in 'symbolTable.getSource()' of the method_info JVMS
   *     structure from which the attributes of this method will be copied.
   */
  void setMethodAttributesSource(final int methodInfoOffset, final int methodInfoLength) {
    
    
    
    this.sourceOffset = methodInfoOffset + 6;
    this.sourceLength = methodInfoLength - 6;
  }

  /**
   * Returns the size of the method_info JVMS structure generated by this MethodWriter. Also add the
   * names of the attributes of this method in the constant pool.
   *
   * @return the size in bytes of the method_info JVMS structure.
   */
  int computeMethodInfoSize() {
    
    if (sourceOffset != 0) {
      
      return 6 + sourceLength;
    }
    
    int size = 8;
    
    if (code.length > 0) {
      if (code.length > 65535) {
        throw new MethodTooLargeException(
            symbolTable.getClassName(), name, descriptor, code.length);
      }
      symbolTable.addConstantUtf8(Constants.CODE);
      
      
      size += 16 + code.length + Handler.getExceptionTableSize(firstHandler);
      if (stackMapTableEntries != null) {
        boolean useStackMapTable = symbolTable.getMajorVersion() >= Opcodes.V1_6;
        symbolTable.addConstantUtf8(useStackMapTable ? Constants.STACK_MAP_TABLE : "StackMap");
        
        size += 8 + stackMapTableEntries.length;
      }
      if (lineNumberTable != null) {
        symbolTable.addConstantUtf8(Constants.LINE_NUMBER_TABLE);
        
        size += 8 + lineNumberTable.length;
      }
      if (localVariableTable != null) {
        symbolTable.addConstantUtf8(Constants.LOCAL_VARIABLE_TABLE);
        
        size += 8 + localVariableTable.length;
      }
      if (localVariableTypeTable != null) {
        symbolTable.addConstantUtf8(Constants.LOCAL_VARIABLE_TYPE_TABLE);
        
        size += 8 + localVariableTypeTable.length;
      }
      if (lastCodeRuntimeVisibleTypeAnnotation != null) {
        size +=
            lastCodeRuntimeVisibleTypeAnnotation.computeAnnotationsSize(
                Constants.RUNTIME_VISIBLE_TYPE_ANNOTATIONS);
      }
      if (lastCodeRuntimeInvisibleTypeAnnotation != null) {
        size +=
            lastCodeRuntimeInvisibleTypeAnnotation.computeAnnotationsSize(
                Constants.RUNTIME_INVISIBLE_TYPE_ANNOTATIONS);
      }
      if (firstCodeAttribute != null) {
        size +=
            firstCodeAttribute.computeAttributesSize(
                symbolTable, code.data, code.length, maxStack, maxLocals);
      }
    }
    if (numberOfExceptions > 0) {
      symbolTable.addConstantUtf8(Constants.EXCEPTIONS);
      size += 8 + 2 * numberOfExceptions;
    }
    size += Attribute.computeAttributesSize(symbolTable, accessFlags, signatureIndex);
    size +=
        AnnotationWriter.computeAnnotationsSize(
            lastRuntimeVisibleAnnotation,
            lastRuntimeInvisibleAnnotation,
            lastRuntimeVisibleTypeAnnotation,
            lastRuntimeInvisibleTypeAnnotation);
    if (lastRuntimeVisibleParameterAnnotations != null) {
      size +=
          AnnotationWriter.computeParameterAnnotationsSize(
              Constants.RUNTIME_VISIBLE_PARAMETER_ANNOTATIONS,
              lastRuntimeVisibleParameterAnnotations,
              visibleAnnotableParameterCount == 0
                  ? lastRuntimeVisibleParameterAnnotations.length
                  : visibleAnnotableParameterCount);
    }
    if (lastRuntimeInvisibleParameterAnnotations != null) {
      size +=
          AnnotationWriter.computeParameterAnnotationsSize(
              Constants.RUNTIME_INVISIBLE_PARAMETER_ANNOTATIONS,
              lastRuntimeInvisibleParameterAnnotations,
              invisibleAnnotableParameterCount == 0
                  ? lastRuntimeInvisibleParameterAnnotations.length
                  : invisibleAnnotableParameterCount);
    }
    if (defaultValue != null) {
      symbolTable.addConstantUtf8(Constants.ANNOTATION_DEFAULT);
      size += 6 + defaultValue.length;
    }
    if (parameters != null) {
      symbolTable.addConstantUtf8(Constants.METHOD_PARAMETERS);
      
      size += 7 + parameters.length;
    }
    if (firstAttribute != null) {
      size += firstAttribute.computeAttributesSize(symbolTable);
    }
    return size;
  }

  /**
   * Puts the content of the method_info JVMS structure generated by this MethodWriter into the
   * given ByteVector.
   *
   * @param output where the method_info structure must be put.
   */
  void putMethodInfo(final ByteVector output) {
    boolean useSyntheticAttribute = symbolTable.getMajorVersion() < Opcodes.V1_5;
    int mask = useSyntheticAttribute ? Opcodes.ACC_SYNTHETIC : 0;
    output.putShort(accessFlags & ~mask).putShort(nameIndex).putShort(descriptorIndex);
    
    if (sourceOffset != 0) {
      output.putByteArray(symbolTable.getSource().classFileBuffer, sourceOffset, sourceLength);
      return;
    }
    
    int attributeCount = 0;
    if (code.length > 0) {
      ++attributeCount;
    }
    if (numberOfExceptions > 0) {
      ++attributeCount;
    }
    if ((accessFlags & Opcodes.ACC_SYNTHETIC) != 0 && useSyntheticAttribute) {
      ++attributeCount;
    }
    if (signatureIndex != 0) {
      ++attributeCount;
    }
    if ((accessFlags & Opcodes.ACC_DEPRECATED) != 0) {
      ++attributeCount;
    }
    if (lastRuntimeVisibleAnnotation != null) {
      ++attributeCount;
    }
    if (lastRuntimeInvisibleAnnotation != null) {
      ++attributeCount;
    }
    if (lastRuntimeVisibleParameterAnnotations != null) {
      ++attributeCount;
    }
    if (lastRuntimeInvisibleParameterAnnotations != null) {
      ++attributeCount;
    }
    if (lastRuntimeVisibleTypeAnnotation != null) {
      ++attributeCount;
    }
    if (lastRuntimeInvisibleTypeAnnotation != null) {
      ++attributeCount;
    }
    if (defaultValue != null) {
      ++attributeCount;
    }
    if (parameters != null) {
      ++attributeCount;
    }
    if (firstAttribute != null) {
      attributeCount += firstAttribute.getAttributeCount();
    }
    
    output.putShort(attributeCount);
    if (code.length > 0) {
      
      
      int size = 10 + code.length + Handler.getExceptionTableSize(firstHandler);
      int codeAttributeCount = 0;
      if (stackMapTableEntries != null) {
        
        size += 8 + stackMapTableEntries.length;
        ++codeAttributeCount;
      }
      if (lineNumberTable != null) {
        
        size += 8 + lineNumberTable.length;
        ++codeAttributeCount;
      }
      if (localVariableTable != null) {
        
        size += 8 + localVariableTable.length;
        ++codeAttributeCount;
      }
      if (localVariableTypeTable != null) {
        
        size += 8 + localVariableTypeTable.length;
        ++codeAttributeCount;
      }
      if (lastCodeRuntimeVisibleTypeAnnotation != null) {
        size +=
            lastCodeRuntimeVisibleTypeAnnotation.computeAnnotationsSize(
                Constants.RUNTIME_VISIBLE_TYPE_ANNOTATIONS);
        ++codeAttributeCount;
      }
      if (lastCodeRuntimeInvisibleTypeAnnotation != null) {
        size +=
            lastCodeRuntimeInvisibleTypeAnnotation.computeAnnotationsSize(
                Constants.RUNTIME_INVISIBLE_TYPE_ANNOTATIONS);
        ++codeAttributeCount;
      }
      if (firstCodeAttribute != null) {
        size +=
            firstCodeAttribute.computeAttributesSize(
                symbolTable, code.data, code.length, maxStack, maxLocals);
        codeAttributeCount += firstCodeAttribute.getAttributeCount();
      }
      output
          .putShort(symbolTable.addConstantUtf8(Constants.CODE))
          .putInt(size)
          .putShort(maxStack)
          .putShort(maxLocals)
          .putInt(code.length)
          .putByteArray(code.data, 0, code.length);
      Handler.putExceptionTable(firstHandler, output);
      output.putShort(codeAttributeCount);
      if (stackMapTableEntries != null) {
        boolean useStackMapTable = symbolTable.getMajorVersion() >= Opcodes.V1_6;
        output
            .putShort(
                symbolTable.addConstantUtf8(
                    useStackMapTable ? Constants.STACK_MAP_TABLE : "StackMap"))
            .putInt(2 + stackMapTableEntries.length)
            .putShort(stackMapTableNumberOfEntries)
            .putByteArray(stackMapTableEntries.data, 0, stackMapTableEntries.length);
      }
      if (lineNumberTable != null) {
        output
            .putShort(symbolTable.addConstantUtf8(Constants.LINE_NUMBER_TABLE))
            .putInt(2 + lineNumberTable.length)
            .putShort(lineNumberTableLength)
            .putByteArray(lineNumberTable.data, 0, lineNumberTable.length);
      }
      if (localVariableTable != null) {
        output
            .putShort(symbolTable.addConstantUtf8(Constants.LOCAL_VARIABLE_TABLE))
            .putInt(2 + localVariableTable.length)
            .putShort(localVariableTableLength)
            .putByteArray(localVariableTable.data, 0, localVariableTable.length);
      }
      if (localVariableTypeTable != null) {
        output
            .putShort(symbolTable.addConstantUtf8(Constants.LOCAL_VARIABLE_TYPE_TABLE))
            .putInt(2 + localVariableTypeTable.length)
            .putShort(localVariableTypeTableLength)
            .putByteArray(localVariableTypeTable.data, 0, localVariableTypeTable.length);
      }
      if (lastCodeRuntimeVisibleTypeAnnotation != null) {
        lastCodeRuntimeVisibleTypeAnnotation.putAnnotations(
            symbolTable.addConstantUtf8(Constants.RUNTIME_VISIBLE_TYPE_ANNOTATIONS), output);
      }
      if (lastCodeRuntimeInvisibleTypeAnnotation != null) {
        lastCodeRuntimeInvisibleTypeAnnotation.putAnnotations(
            symbolTable.addConstantUtf8(Constants.RUNTIME_INVISIBLE_TYPE_ANNOTATIONS), output);
      }
      if (firstCodeAttribute != null) {
        firstCodeAttribute.putAttributes(
            symbolTable, code.data, code.length, maxStack, maxLocals, output);
      }
    }
    if (numberOfExceptions > 0) {
      output
          .putShort(symbolTable.addConstantUtf8(Constants.EXCEPTIONS))
          .putInt(2 + 2 * numberOfExceptions)
          .putShort(numberOfExceptions);
      for (int exceptionIndex : exceptionIndexTable) {
        output.putShort(exceptionIndex);
      }
    }
    Attribute.putAttributes(symbolTable, accessFlags, signatureIndex, output);
    AnnotationWriter.putAnnotations(
        symbolTable,
        lastRuntimeVisibleAnnotation,
        lastRuntimeInvisibleAnnotation,
        lastRuntimeVisibleTypeAnnotation,
        lastRuntimeInvisibleTypeAnnotation,
        output);
    if (lastRuntimeVisibleParameterAnnotations != null) {
      AnnotationWriter.putParameterAnnotations(
          symbolTable.addConstantUtf8(Constants.RUNTIME_VISIBLE_PARAMETER_ANNOTATIONS),
          lastRuntimeVisibleParameterAnnotations,
          visibleAnnotableParameterCount == 0
              ? lastRuntimeVisibleParameterAnnotations.length
              : visibleAnnotableParameterCount,
          output);
    }
    if (lastRuntimeInvisibleParameterAnnotations != null) {
      AnnotationWriter.putParameterAnnotations(
          symbolTable.addConstantUtf8(Constants.RUNTIME_INVISIBLE_PARAMETER_ANNOTATIONS),
          lastRuntimeInvisibleParameterAnnotations,
          invisibleAnnotableParameterCount == 0
              ? lastRuntimeInvisibleParameterAnnotations.length
              : invisibleAnnotableParameterCount,
          output);
    }
    if (defaultValue != null) {
      output
          .putShort(symbolTable.addConstantUtf8(Constants.ANNOTATION_DEFAULT))
          .putInt(defaultValue.length)
          .putByteArray(defaultValue.data, 0, defaultValue.length);
    }
    if (parameters != null) {
      output
          .putShort(symbolTable.addConstantUtf8(Constants.METHOD_PARAMETERS))
          .putInt(1 + parameters.length)
          .putByte(parametersCount)
          .putByteArray(parameters.data, 0, parameters.length);
    }
    if (firstAttribute != null) {
      firstAttribute.putAttributes(symbolTable, output);
    }
  }

  /**
   * Collects the attributes of this method into the given set of attribute prototypes.
   *
   * @param attributePrototypes a set of attribute prototypes.
   */
  final void collectAttributePrototypes(final Attribute.Set attributePrototypes) {
    attributePrototypes.addAttributes(firstAttribute);
    attributePrototypes.addAttributes(firstCodeAttribute);
  }
}
