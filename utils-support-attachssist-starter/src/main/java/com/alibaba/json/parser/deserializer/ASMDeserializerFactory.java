package com.alibaba.json.parser.deserializer;

import com.alibaba.json.parser.*;
import com.alibaba.json.parser.DefaultJSONParser.ResolveTask;
import com.alibaba.json.util.TypeUtils;
import com.alibaba.json.asm.*;

import java.lang.reflect.Type;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class ASMDeserializerFactory implements Opcodes {

    final static String DefaultJSONParser = com.alibaba.json.util.ASMUtils.type(DefaultJSONParser.class);
    final static String JSONLexerBase = com.alibaba.json.util.ASMUtils.type(JSONLexerBase.class);
    public final com.alibaba.json.util.ASMClassLoader classLoader;
    protected final AtomicLong seed = new AtomicLong();

    public ASMDeserializerFactory(ClassLoader parentClassLoader) {
        classLoader = parentClassLoader instanceof com.alibaba.json.util.ASMClassLoader //
                ? (com.alibaba.json.util.ASMClassLoader) parentClassLoader //
                : new com.alibaba.json.util.ASMClassLoader(parentClassLoader);
    }

    public ObjectDeserializer createJavaBeanDeserializer(ParserConfig config, com.alibaba.json.util.JavaBeanInfo beanInfo) throws Exception {
        Class<?> clazz = beanInfo.clazz;
        if (clazz.isPrimitive()) {
            throw new IllegalArgumentException("not support type :" + clazz.getName());
        }

        String className = "FastjsonASMDeserializer_" + seed.incrementAndGet() + "_" + clazz.getSimpleName();
        String classNameType;
        String classNameFull;

        Package pkg = ASMDeserializerFactory.class.getPackage();
        if (pkg != null) {
            String packageName = pkg.getName();
            classNameType = packageName.replace('.', '/') + "/" + className;
            classNameFull = packageName + "." + className;
        } else {
            classNameType = className;
            classNameFull = className;
        }

        com.alibaba.json.asm.ClassWriter cw = new com.alibaba.json.asm.ClassWriter();
        cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER, classNameType, com.alibaba.json.util.ASMUtils.type(JavaBeanDeserializer.class), null);

        _init(cw, new Context(classNameType, config, beanInfo, 3));
        _createInstance(cw, new Context(classNameType, config, beanInfo, 3));
        _deserialze(cw, new Context(classNameType, config, beanInfo, 5));

        _deserialzeArrayMapping(cw, new Context(classNameType, config, beanInfo, 4));
        byte[] code = cw.toByteArray();

        Class<?> deserClass = classLoader.defineClassPublic(classNameFull, code, 0, code.length);
        Constructor<?> constructor = deserClass.getConstructor(ParserConfig.class, com.alibaba.json.util.JavaBeanInfo.class);
        Object instance = constructor.newInstance(config, beanInfo);

        return (ObjectDeserializer) instance;
    }

    private void _setFlag(com.alibaba.json.asm.MethodVisitor mw, Context context, int i) {
        String varName = "_asm_flag_" + (i / 32);

        mw.visitVarInsn(ILOAD, context.var(varName));
        mw.visitLdcInsn(1 << i);
        mw.visitInsn(IOR);
        mw.visitVarInsn(ISTORE, context.var(varName));
    }

    private void _isFlag(com.alibaba.json.asm.MethodVisitor mw, Context context, int i, com.alibaba.json.asm.Label label) {
        mw.visitVarInsn(ILOAD, context.var("_asm_flag_" + (i / 32)));
        mw.visitLdcInsn(1 << i);
        mw.visitInsn(IAND);

        mw.visitJumpInsn(IFEQ, label);
    }

    private void _deserialzeArrayMapping(com.alibaba.json.asm.ClassWriter cw, Context context) {
        com.alibaba.json.asm.MethodVisitor mw = new com.alibaba.json.asm.MethodWriter(cw, ACC_PUBLIC, "deserialzeArrayMapping",
                "(L" + DefaultJSONParser + ";Ljava/lang/reflect/Type;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;",
                null, null);

        defineVarLexer(context, mw);

        mw.visitVarInsn(ALOAD, context.var("lexer"));
        mw.visitVarInsn(ALOAD, 1);
        mw.visitMethodInsn(INVOKEVIRTUAL, DefaultJSONParser, "getSymbolTable", "()" + com.alibaba.json.util.ASMUtils.desc(SymbolTable.class));
        mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "scanTypeName", "(" + com.alibaba.json.util.ASMUtils.desc(SymbolTable.class) + ")Ljava/lang/String;");
        mw.visitVarInsn(ASTORE, context.var("typeName"));

        com.alibaba.json.asm.Label typeNameNotNull_ = new com.alibaba.json.asm.Label();
        mw.visitVarInsn(ALOAD, context.var("typeName"));
        mw.visitJumpInsn(IFNULL, typeNameNotNull_);

        mw.visitVarInsn(ALOAD, 1);
        mw.visitMethodInsn(INVOKEVIRTUAL, DefaultJSONParser, "getConfig", "()" + com.alibaba.json.util.ASMUtils.desc(ParserConfig.class));
        mw.visitVarInsn(ALOAD, 0);
        mw.visitFieldInsn(GETFIELD, com.alibaba.json.util.ASMUtils.type(JavaBeanDeserializer.class), "beanInfo", com.alibaba.json.util.ASMUtils.desc(com.alibaba.json.util.JavaBeanInfo.class));
        mw.visitVarInsn(ALOAD, context.var("typeName"));
        mw.visitMethodInsn(INVOKESTATIC, com.alibaba.json.util.ASMUtils.type(JavaBeanDeserializer.class), "getSeeAlso"
                , "(" + com.alibaba.json.util.ASMUtils.desc(ParserConfig.class) + com.alibaba.json.util.ASMUtils.desc(com.alibaba.json.util.JavaBeanInfo.class) + "Ljava/lang/String;)" + com.alibaba.json.util.ASMUtils.desc(JavaBeanDeserializer.class));
        mw.visitVarInsn(ASTORE, context.var("userTypeDeser"));
        mw.visitVarInsn(ALOAD, context.var("userTypeDeser"));
        mw.visitTypeInsn(INSTANCEOF, com.alibaba.json.util.ASMUtils.type(JavaBeanDeserializer.class));
        mw.visitJumpInsn(IFEQ, typeNameNotNull_);

        mw.visitVarInsn(ALOAD, context.var("userTypeDeser"));
        mw.visitVarInsn(ALOAD, Context.parser);
        mw.visitVarInsn(ALOAD, 2);
        mw.visitVarInsn(ALOAD, 3);
        mw.visitVarInsn(ALOAD, 4);
        mw.visitMethodInsn(INVOKEVIRTUAL, //
                com.alibaba.json.util.ASMUtils.type(JavaBeanDeserializer.class), //
                "deserialzeArrayMapping", //
                "(L" + DefaultJSONParser + ";Ljava/lang/reflect/Type;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
        mw.visitInsn(ARETURN);

        mw.visitLabel(typeNameNotNull_);

        _createInstance(context, mw);

        com.alibaba.json.util.FieldInfo[] sortedFieldInfoList = context.beanInfo.sortedFields;
        int fieldListSize = sortedFieldInfoList.length;
        for (int i = 0; i < fieldListSize; ++i) {
            final boolean last = (i == fieldListSize - 1);
            final char seperator = last ? ']' : ',';

            com.alibaba.json.util.FieldInfo fieldInfo = sortedFieldInfoList[i];
            Class<?> fieldClass = fieldInfo.fieldClass;
            Type fieldType = fieldInfo.fieldType;
            if (fieldClass == byte.class //
                || fieldClass == short.class //
                || fieldClass == int.class) {
                mw.visitVarInsn(ALOAD, context.var("lexer"));
                mw.visitVarInsn(BIPUSH, seperator);
                mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "scanInt", "(C)I");
                mw.visitVarInsn(ISTORE, context.var_asm(fieldInfo));
            } else if (fieldClass == Byte.class) {
                mw.visitVarInsn(ALOAD, context.var("lexer"));
                mw.visitVarInsn(BIPUSH, seperator);
                mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "scanInt", "(C)I");
                mw.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;");

                mw.visitVarInsn(ASTORE, context.var_asm(fieldInfo));
                com.alibaba.json.asm.Label valueNullEnd_ = new com.alibaba.json.asm.Label();
                mw.visitVarInsn(ALOAD, context.var("lexer"));
                mw.visitFieldInsn(GETFIELD, JSONLexerBase, "matchStat", "I");
                mw.visitLdcInsn(com.alibaba.json.parser.JSONLexerBase.VALUE_NULL);
                mw.visitJumpInsn(IF_ICMPNE, valueNullEnd_);
                mw.visitInsn(ACONST_NULL);
                mw.visitVarInsn(ASTORE, context.var_asm(fieldInfo));
                mw.visitLabel(valueNullEnd_);
            } else if (fieldClass == Short.class) {
                mw.visitVarInsn(ALOAD, context.var("lexer"));
                mw.visitVarInsn(BIPUSH, seperator);
                mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "scanInt", "(C)I");
                mw.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");

                mw.visitVarInsn(ASTORE, context.var_asm(fieldInfo));
                com.alibaba.json.asm.Label valueNullEnd_ = new com.alibaba.json.asm.Label();
                mw.visitVarInsn(ALOAD, context.var("lexer"));
                mw.visitFieldInsn(GETFIELD, JSONLexerBase, "matchStat", "I");
                mw.visitLdcInsn(com.alibaba.json.parser.JSONLexerBase.VALUE_NULL);
                mw.visitJumpInsn(IF_ICMPNE, valueNullEnd_);
                mw.visitInsn(ACONST_NULL);
                mw.visitVarInsn(ASTORE, context.var_asm(fieldInfo));
                mw.visitLabel(valueNullEnd_);
            } else if (fieldClass == Integer.class) {
                mw.visitVarInsn(ALOAD, context.var("lexer"));
                mw.visitVarInsn(BIPUSH, seperator);
                mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "scanInt", "(C)I");
                mw.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");

                mw.visitVarInsn(ASTORE, context.var_asm(fieldInfo));
                com.alibaba.json.asm.Label valueNullEnd_ = new com.alibaba.json.asm.Label();
                mw.visitVarInsn(ALOAD, context.var("lexer"));
                mw.visitFieldInsn(GETFIELD, JSONLexerBase, "matchStat", "I");
                mw.visitLdcInsn(com.alibaba.json.parser.JSONLexerBase.VALUE_NULL);
                mw.visitJumpInsn(IF_ICMPNE, valueNullEnd_);
                mw.visitInsn(ACONST_NULL);
                mw.visitVarInsn(ASTORE, context.var_asm(fieldInfo));
                mw.visitLabel(valueNullEnd_);
            } else if (fieldClass == long.class) {
                mw.visitVarInsn(ALOAD, context.var("lexer"));
                mw.visitVarInsn(BIPUSH, seperator);
                mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "scanLong", "(C)J");
                mw.visitVarInsn(LSTORE, context.var_asm(fieldInfo, 2));

            } else if (fieldClass == Long.class) {
                mw.visitVarInsn(ALOAD, context.var("lexer"));
                mw.visitVarInsn(BIPUSH, seperator);
                mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "scanLong", "(C)J");
                mw.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");

                mw.visitVarInsn(ASTORE, context.var_asm(fieldInfo));
                com.alibaba.json.asm.Label valueNullEnd_ = new com.alibaba.json.asm.Label();
                mw.visitVarInsn(ALOAD, context.var("lexer"));
                mw.visitFieldInsn(GETFIELD, JSONLexerBase, "matchStat", "I");
                mw.visitLdcInsn(com.alibaba.json.parser.JSONLexerBase.VALUE_NULL);
                mw.visitJumpInsn(IF_ICMPNE, valueNullEnd_);
                mw.visitInsn(ACONST_NULL);
                mw.visitVarInsn(ASTORE, context.var_asm(fieldInfo));
                mw.visitLabel(valueNullEnd_);
            } else if (fieldClass == boolean.class) {
                mw.visitVarInsn(ALOAD, context.var("lexer"));
                mw.visitVarInsn(BIPUSH, seperator);
                mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "scanBoolean", "(C)Z");
                mw.visitVarInsn(ISTORE, context.var_asm(fieldInfo));
            } else if (fieldClass == float.class) {
                mw.visitVarInsn(ALOAD, context.var("lexer"));
                mw.visitVarInsn(BIPUSH, seperator);
                mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "scanFloat", "(C)F");
                mw.visitVarInsn(FSTORE, context.var_asm(fieldInfo));

            } else if (fieldClass == Float.class) {
                mw.visitVarInsn(ALOAD, context.var("lexer"));
                mw.visitVarInsn(BIPUSH, seperator);
                mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "scanFloat", "(C)F");
                mw.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");

                mw.visitVarInsn(ASTORE, context.var_asm(fieldInfo));
                com.alibaba.json.asm.Label valueNullEnd_ = new com.alibaba.json.asm.Label();
                mw.visitVarInsn(ALOAD, context.var("lexer"));
                mw.visitFieldInsn(GETFIELD, JSONLexerBase, "matchStat", "I");
                mw.visitLdcInsn(com.alibaba.json.parser.JSONLexerBase.VALUE_NULL);
                mw.visitJumpInsn(IF_ICMPNE, valueNullEnd_);
                mw.visitInsn(ACONST_NULL);
                mw.visitVarInsn(ASTORE, context.var_asm(fieldInfo));
                mw.visitLabel(valueNullEnd_);

            } else if (fieldClass == double.class) {
                mw.visitVarInsn(ALOAD, context.var("lexer"));
                mw.visitVarInsn(BIPUSH, seperator);
                mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "scanDouble", "(C)D");
                mw.visitVarInsn(DSTORE, context.var_asm(fieldInfo, 2));

            } else if (fieldClass == Double.class) {
                mw.visitVarInsn(ALOAD, context.var("lexer"));
                mw.visitVarInsn(BIPUSH, seperator);
                mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "scanDouble", "(C)D");
                mw.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");

                mw.visitVarInsn(ASTORE, context.var_asm(fieldInfo));
                com.alibaba.json.asm.Label valueNullEnd_ = new com.alibaba.json.asm.Label();
                mw.visitVarInsn(ALOAD, context.var("lexer"));
                mw.visitFieldInsn(GETFIELD, JSONLexerBase, "matchStat", "I");
                mw.visitLdcInsn(com.alibaba.json.parser.JSONLexerBase.VALUE_NULL);
                mw.visitJumpInsn(IF_ICMPNE, valueNullEnd_);
                mw.visitInsn(ACONST_NULL);
                mw.visitVarInsn(ASTORE, context.var_asm(fieldInfo));
                mw.visitLabel(valueNullEnd_);

            } else if (fieldClass == char.class) {
                mw.visitVarInsn(ALOAD, context.var("lexer"));
                mw.visitVarInsn(BIPUSH, seperator);
                mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "scanString", "(C)Ljava/lang/String;");
                mw.visitInsn(ICONST_0);
                mw.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "charAt", "(I)C");
                mw.visitVarInsn(ISTORE, context.var_asm(fieldInfo));
            } else if (fieldClass == String.class) {
                mw.visitVarInsn(ALOAD, context.var("lexer"));
                mw.visitVarInsn(BIPUSH, seperator);
                mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "scanString", "(C)Ljava/lang/String;");
                mw.visitVarInsn(ASTORE, context.var_asm(fieldInfo));

            } else if (fieldClass == BigDecimal.class) {
                mw.visitVarInsn(ALOAD, context.var("lexer"));
                mw.visitVarInsn(BIPUSH, seperator);
                mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "scanDecimal", "(C)Ljava/math/BigDecimal;");
                mw.visitVarInsn(ASTORE, context.var_asm(fieldInfo));

            } else if (fieldClass == java.util.Date.class) {
                mw.visitVarInsn(ALOAD, context.var("lexer"));
                mw.visitVarInsn(BIPUSH, seperator);
                mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "scanDate", "(C)Ljava/util/Date;");
                mw.visitVarInsn(ASTORE, context.var_asm(fieldInfo));

            } else if (fieldClass == java.util.UUID.class) {
                mw.visitVarInsn(ALOAD, context.var("lexer"));
                mw.visitVarInsn(BIPUSH, seperator);
                mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "scanUUID", "(C)Ljava/util/UUID;");
                mw.visitVarInsn(ASTORE, context.var_asm(fieldInfo));

            } else if (fieldClass.isEnum()) {
                com.alibaba.json.asm.Label enumNumIf_ = new com.alibaba.json.asm.Label();
                com.alibaba.json.asm.Label enumNumErr_ = new com.alibaba.json.asm.Label();
                com.alibaba.json.asm.Label enumStore_ = new com.alibaba.json.asm.Label();
                com.alibaba.json.asm.Label enumQuote_ = new com.alibaba.json.asm.Label();

                mw.visitVarInsn(ALOAD, context.var("lexer"));
                mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "getCurrent", "()C");
                mw.visitInsn(DUP);
                mw.visitVarInsn(ISTORE, context.var("ch"));
                mw.visitLdcInsn((int) 'n');
                mw.visitJumpInsn(IF_ICMPEQ, enumQuote_);

                mw.visitVarInsn(ILOAD, context.var("ch"));
                mw.visitLdcInsn((int) '\"');
                mw.visitJumpInsn(IF_ICMPNE, enumNumIf_);

                mw.visitLabel(enumQuote_);
                mw.visitVarInsn(ALOAD, context.var("lexer"));
                mw.visitLdcInsn(com.alibaba.json.asm.Type.getType(com.alibaba.json.util.ASMUtils.desc(fieldClass)));
                mw.visitVarInsn(ALOAD, 1);
                mw.visitMethodInsn(INVOKEVIRTUAL, DefaultJSONParser, "getSymbolTable", "()" + com.alibaba.json.util.ASMUtils.desc(SymbolTable.class));
                mw.visitVarInsn(BIPUSH, seperator);
                mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "scanEnum",
                        "(Ljava/lang/Class;" + com.alibaba.json.util.ASMUtils.desc(SymbolTable.class) + "C)Ljava/lang/Enum;");
                mw.visitJumpInsn(GOTO, enumStore_);

                // (ch >= '0' && ch <= '9') {
                mw.visitLabel(enumNumIf_);
                mw.visitVarInsn(ILOAD, context.var("ch"));
                mw.visitLdcInsn((int) '0');
                mw.visitJumpInsn(IF_ICMPLT, enumNumErr_);

                mw.visitVarInsn(ILOAD, context.var("ch"));
                mw.visitLdcInsn((int) '9');
                mw.visitJumpInsn(IF_ICMPGT, enumNumErr_);

                _getFieldDeser(context, mw, fieldInfo);
                mw.visitTypeInsn(CHECKCAST, com.alibaba.json.util.ASMUtils.type(EnumDeserializer.class)); // cast
                mw.visitVarInsn(ALOAD, context.var("lexer"));
                mw.visitVarInsn(BIPUSH, seperator);
                mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "scanInt", "(C)I");
                mw.visitMethodInsn(INVOKEVIRTUAL, com.alibaba.json.util.ASMUtils.type(EnumDeserializer.class), "valueOf", "(I)Ljava/lang/Enum;");
                mw.visitJumpInsn(GOTO, enumStore_);

                mw.visitLabel(enumNumErr_);
                mw.visitVarInsn(ALOAD, 0);
                mw.visitVarInsn(ALOAD, context.var("lexer"));
                mw.visitVarInsn(BIPUSH, seperator);
                mw.visitMethodInsn(INVOKEVIRTUAL, com.alibaba.json.util.ASMUtils.type(JavaBeanDeserializer.class), "scanEnum",
                        "(L" + JSONLexerBase + ";C)Ljava/lang/Enum;");

                mw.visitLabel(enumStore_);
                mw.visitTypeInsn(CHECKCAST, com.alibaba.json.util.ASMUtils.type(fieldClass)); // cast
                mw.visitVarInsn(ASTORE, context.var_asm(fieldInfo));
            } else if (Collection.class.isAssignableFrom(fieldClass)) {
                
                Class<?> itemClass = TypeUtils.getCollectionItemClass(fieldType);
                if (itemClass == String.class) {
                    if (fieldClass == List.class
                            || fieldClass == Collections.class
                            || fieldClass == ArrayList.class
                    ) {
                        mw.visitTypeInsn(NEW, com.alibaba.json.util.ASMUtils.type(ArrayList.class));
                        mw.visitInsn(DUP);
                        mw.visitMethodInsn(INVOKESPECIAL, com.alibaba.json.util.ASMUtils.type(ArrayList.class), "<init>", "()V");
                    } else {
                        mw.visitLdcInsn(com.alibaba.json.asm.Type.getType(com.alibaba.json.util.ASMUtils.desc(fieldClass)));
                        mw.visitMethodInsn(INVOKESTATIC, com.alibaba.json.util.ASMUtils.type(TypeUtils.class), "createCollection",
                                "(Ljava/lang/Class;)Ljava/util/Collection;");
                    }
                    mw.visitVarInsn(ASTORE, context.var_asm(fieldInfo));
                    
                    mw.visitVarInsn(ALOAD, context.var("lexer"));
                    mw.visitVarInsn(ALOAD, context.var_asm(fieldInfo));
                    mw.visitVarInsn(BIPUSH, seperator);
                    mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "scanStringArray", "(Ljava/util/Collection;C)V");

                    com.alibaba.json.asm.Label valueNullEnd_ = new com.alibaba.json.asm.Label();
                    mw.visitVarInsn(ALOAD, context.var("lexer"));
                    mw.visitFieldInsn(GETFIELD, JSONLexerBase, "matchStat", "I");
                    mw.visitLdcInsn(com.alibaba.json.parser.JSONLexerBase.VALUE_NULL);
                    mw.visitJumpInsn(IF_ICMPNE, valueNullEnd_);
                    mw.visitInsn(ACONST_NULL);
                    mw.visitVarInsn(ASTORE, context.var_asm(fieldInfo));
                    
                    mw.visitLabel(valueNullEnd_);
                    
                } else {
                    com.alibaba.json.asm.Label notError_ = new com.alibaba.json.asm.Label();
                    mw.visitVarInsn(ALOAD, context.var("lexer"));
                    mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "token", "()I");
                    mw.visitVarInsn(ISTORE, context.var("token"));

                    mw.visitVarInsn(ILOAD, context.var("token"));
                    int token = i == 0 ? JSONToken.LBRACKET : JSONToken.COMMA;
                    mw.visitLdcInsn(token);
                    mw.visitJumpInsn(IF_ICMPEQ, notError_);

                    mw.visitVarInsn(ALOAD, 1); // DefaultJSONParser
                    mw.visitLdcInsn(token);
                    mw.visitMethodInsn(INVOKEVIRTUAL, DefaultJSONParser, "throwException", "(I)V");

                    mw.visitLabel(notError_);

                    com.alibaba.json.asm.Label quickElse_ = new com.alibaba.json.asm.Label(), quickEnd_ = new com.alibaba.json.asm.Label();
                    mw.visitVarInsn(ALOAD, context.var("lexer"));
                    mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "getCurrent", "()C");
                    mw.visitVarInsn(BIPUSH, '[');
                    mw.visitJumpInsn(IF_ICMPNE, quickElse_);

                    mw.visitVarInsn(ALOAD, context.var("lexer"));
                    mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "next", "()C");
                    mw.visitInsn(POP);
                    mw.visitVarInsn(ALOAD, context.var("lexer"));
                    mw.visitLdcInsn(JSONToken.LBRACKET);
                    mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "setToken", "(I)V");
                    mw.visitJumpInsn(GOTO, quickEnd_);

                    mw.visitLabel(quickElse_);
                    mw.visitVarInsn(ALOAD, context.var("lexer"));
                    mw.visitLdcInsn(JSONToken.LBRACKET);
                    mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "nextToken", "(I)V");
                    mw.visitLabel(quickEnd_);

                    _newCollection(mw, fieldClass, i, false);
                    mw.visitInsn(DUP);
                    mw.visitVarInsn(ASTORE, context.var_asm(fieldInfo));
                    _getCollectionFieldItemDeser(context, mw, fieldInfo, itemClass);
                    mw.visitVarInsn(ALOAD, 1);
                    mw.visitLdcInsn(com.alibaba.json.asm.Type.getType(com.alibaba.json.util.ASMUtils.desc(itemClass)));
                    mw.visitVarInsn(ALOAD, 3);
                    mw.visitMethodInsn(INVOKESTATIC, com.alibaba.json.util.ASMUtils.type(JavaBeanDeserializer.class),
                            "parseArray",
                            "(Ljava/util/Collection;" //
                                    + com.alibaba.json.util.ASMUtils.desc(ObjectDeserializer.class) //
                                    + "L" + DefaultJSONParser + ";" //
                                    + "Ljava/lang/reflect/Type;Ljava/lang/Object;)V");
                }
            } else if (fieldClass.isArray()) {
                mw.visitVarInsn(ALOAD, context.var("lexer"));
                mw.visitLdcInsn(com.alibaba.json.parser.JSONToken.LBRACKET);
                mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "nextToken", "(I)V");

                mw.visitVarInsn(ALOAD, Context.parser);
                mw.visitVarInsn(ALOAD, 0);
                mw.visitLdcInsn(i);
                mw.visitMethodInsn(INVOKEVIRTUAL, com.alibaba.json.util.ASMUtils.type(JavaBeanDeserializer.class), "getFieldType",
                        "(I)Ljava/lang/reflect/Type;");
                mw.visitMethodInsn(INVOKEVIRTUAL, DefaultJSONParser, "parseObject",
                                   "(Ljava/lang/reflect/Type;)Ljava/lang/Object;");

                mw.visitTypeInsn(CHECKCAST, com.alibaba.json.util.ASMUtils.type(fieldClass)); // cast
                mw.visitVarInsn(ASTORE, context.var_asm(fieldInfo));
            } else {
                com.alibaba.json.asm.Label objElseIf_ = new com.alibaba.json.asm.Label();
                com.alibaba.json.asm.Label objEndIf_ = new com.alibaba.json.asm.Label();

                if (fieldClass == java.util.Date.class) {
                    mw.visitVarInsn(ALOAD, context.var("lexer"));
                    mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "getCurrent", "()C");
                    mw.visitLdcInsn((int) '1');
                    mw.visitJumpInsn(IF_ICMPNE, objElseIf_);

                    mw.visitTypeInsn(NEW, com.alibaba.json.util.ASMUtils.type(java.util.Date.class));
                    mw.visitInsn(DUP);

                    mw.visitVarInsn(ALOAD, context.var("lexer"));
                    mw.visitVarInsn(BIPUSH, seperator);
                    mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "scanLong", "(C)J");

                    mw.visitMethodInsn(INVOKESPECIAL, com.alibaba.json.util.ASMUtils.type(java.util.Date.class), "<init>", "(J)V");
                    mw.visitVarInsn(ASTORE, context.var_asm(fieldInfo));

                    mw.visitJumpInsn(GOTO, objEndIf_);
                }

                mw.visitLabel(objElseIf_);

                _quickNextToken(context, mw, JSONToken.LBRACKET);

                _deserObject(context, mw, fieldInfo, fieldClass, i);

                mw.visitVarInsn(ALOAD, context.var("lexer"));
                mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "token", "()I");
                mw.visitLdcInsn(JSONToken.RBRACKET);
                mw.visitJumpInsn(IF_ICMPEQ, objEndIf_);
//                mw.visitInsn(POP);
//                mw.visitInsn(POP);

                mw.visitVarInsn(ALOAD, 0);
                mw.visitVarInsn(ALOAD, context.var("lexer"));
                if (!last) {
                    mw.visitLdcInsn(JSONToken.COMMA);
                } else {
                    mw.visitLdcInsn(JSONToken.RBRACKET);
                }
                mw.visitMethodInsn(INVOKESPECIAL, //
                        com.alibaba.json.util.ASMUtils.type(JavaBeanDeserializer.class), //
                        "check", "(" + com.alibaba.json.util.ASMUtils.desc(JSONLexer.class) + "I)V");

                mw.visitLabel(objEndIf_);
                continue;
            }
        }

        _batchSet(context, mw, false);

        com.alibaba.json.asm.Label quickElse_ = new com.alibaba.json.asm.Label(), quickElseIf_ = new com.alibaba.json.asm.Label(), quickElseIfEOI_ = new com.alibaba.json.asm.Label(),
                quickEnd_ = new com.alibaba.json.asm.Label();
        mw.visitVarInsn(ALOAD, context.var("lexer"));
        mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "getCurrent", "()C");
        mw.visitInsn(DUP);
        mw.visitVarInsn(ISTORE, context.var("ch"));
        mw.visitVarInsn(BIPUSH, ',');
        mw.visitJumpInsn(IF_ICMPNE, quickElseIf_);

        mw.visitVarInsn(ALOAD, context.var("lexer"));
        mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "next", "()C");
        mw.visitInsn(POP);
        mw.visitVarInsn(ALOAD, context.var("lexer"));
        mw.visitLdcInsn(JSONToken.COMMA);
        mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "setToken", "(I)V");
        mw.visitJumpInsn(GOTO, quickEnd_);

        mw.visitLabel(quickElseIf_);
        mw.visitVarInsn(ILOAD, context.var("ch"));
        mw.visitVarInsn(BIPUSH, ']');
        mw.visitJumpInsn(IF_ICMPNE, quickElseIfEOI_);

        mw.visitVarInsn(ALOAD, context.var("lexer"));
        mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "next", "()C");
        mw.visitInsn(POP);
        mw.visitVarInsn(ALOAD, context.var("lexer"));
        mw.visitLdcInsn(JSONToken.RBRACKET);
        mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "setToken", "(I)V");
        mw.visitJumpInsn(GOTO, quickEnd_);

        mw.visitLabel(quickElseIfEOI_);
        mw.visitVarInsn(ILOAD, context.var("ch"));
        mw.visitVarInsn(BIPUSH, (char) JSONLexer.EOI);
        mw.visitJumpInsn(IF_ICMPNE, quickElse_);

        mw.visitVarInsn(ALOAD, context.var("lexer"));
        mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "next", "()C");
        mw.visitInsn(POP);
        mw.visitVarInsn(ALOAD, context.var("lexer"));
        mw.visitLdcInsn(JSONToken.EOF);
        mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "setToken", "(I)V");
        mw.visitJumpInsn(GOTO, quickEnd_);

        mw.visitLabel(quickElse_);
        mw.visitVarInsn(ALOAD, context.var("lexer"));
        mw.visitLdcInsn(JSONToken.COMMA);
        mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "nextToken", "(I)V");

        mw.visitLabel(quickEnd_);

        mw.visitVarInsn(ALOAD, context.var("instance"));
        mw.visitInsn(ARETURN);
        mw.visitMaxs(5, context.variantIndex);
        mw.visitEnd();
    }

    private void _deserialze(com.alibaba.json.asm.ClassWriter cw, Context context) {
        if (context.fieldInfoList.length == 0) {
            return;
        }

        for (com.alibaba.json.util.FieldInfo fieldInfo : context.fieldInfoList) {
            Class<?> fieldClass = fieldInfo.fieldClass;
            Type fieldType = fieldInfo.fieldType;

            if (fieldClass == char.class) {
                return;
            }

            if (Collection.class.isAssignableFrom(fieldClass)) {
                if (fieldType instanceof ParameterizedType) {
                    Type itemType = ((ParameterizedType) fieldType).getActualTypeArguments()[0];
                    if (itemType instanceof Class) {
                        continue;
                    } else {
                        return;
                    }
                } else {
                    return;
                }
            }
        }

        com.alibaba.json.util.JavaBeanInfo beanInfo = context.beanInfo;
        context.fieldInfoList = beanInfo.sortedFields;

        com.alibaba.json.asm.MethodVisitor mw = new com.alibaba.json.asm.MethodWriter(cw, ACC_PUBLIC, "deserialze",
                "(L" + DefaultJSONParser + ";Ljava/lang/reflect/Type;Ljava/lang/Object;I)Ljava/lang/Object;",
                null, null);

        com.alibaba.json.asm.Label reset_ = new com.alibaba.json.asm.Label();
        com.alibaba.json.asm.Label super_ = new com.alibaba.json.asm.Label();
        com.alibaba.json.asm.Label return_ = new com.alibaba.json.asm.Label();
        com.alibaba.json.asm.Label end_ = new com.alibaba.json.asm.Label();

        defineVarLexer(context, mw);

        {
            com.alibaba.json.asm.Label next_ = new com.alibaba.json.asm.Label();

            // isSupportArrayToBean

            mw.visitVarInsn(ALOAD, context.var("lexer"));
            mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "token", "()I");
            mw.visitLdcInsn(JSONToken.LBRACKET);
            mw.visitJumpInsn(IF_ICMPNE, next_);

            if ((beanInfo.parserFeatures & Feature.SupportArrayToBean.mask) == 0) {
                mw.visitVarInsn(ALOAD, context.var("lexer"));
                mw.visitVarInsn(ILOAD, 4);
                mw.visitLdcInsn(Feature.SupportArrayToBean.mask);
                mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "isEnabled", "(II)Z");
                mw.visitJumpInsn(IFEQ, next_);
            }

            mw.visitVarInsn(ALOAD, 0);
            mw.visitVarInsn(ALOAD, Context.parser);
            mw.visitVarInsn(ALOAD, 2);
            mw.visitVarInsn(ALOAD, 3);
            mw.visitInsn(ACONST_NULL); //mw.visitVarInsn(ALOAD, 5);
            mw.visitMethodInsn(INVOKESPECIAL, //
                               context.className, //
                               "deserialzeArrayMapping", //
                               "(L" + DefaultJSONParser + ";Ljava/lang/reflect/Type;Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
            mw.visitInsn(ARETURN);

            mw.visitLabel(next_);
            // deserialzeArrayMapping
        }

        mw.visitVarInsn(ALOAD, context.var("lexer"));
        mw.visitLdcInsn(Feature.SortFeidFastMatch.mask);
        mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "isEnabled", "(I)Z");

        com.alibaba.json.asm.Label continue_ = new com.alibaba.json.asm.Label();
        mw.visitJumpInsn(IFNE, continue_);
        mw.visitJumpInsn(GOTO_W, super_);
        mw.visitLabel(continue_);

        mw.visitVarInsn(ALOAD, context.var("lexer"));
        mw.visitLdcInsn(context.clazz.getName());
        mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "scanType", "(Ljava/lang/String;)I");

        mw.visitLdcInsn(com.alibaba.json.parser.JSONLexerBase.NOT_MATCH);

        com.alibaba.json.asm.Label continue_2 = new com.alibaba.json.asm.Label();
        mw.visitJumpInsn(IF_ICMPNE, continue_2);
        mw.visitJumpInsn(GOTO_W, super_);
        mw.visitLabel(continue_2);

        mw.visitVarInsn(ALOAD, 1); // parser
        mw.visitMethodInsn(INVOKEVIRTUAL, DefaultJSONParser, "getContext", "()" + com.alibaba.json.util.ASMUtils.desc(ParseContext.class));
        mw.visitVarInsn(ASTORE, context.var("mark_context"));

        // ParseContext context = parser.getContext();
        mw.visitInsn(ICONST_0);
        mw.visitVarInsn(ISTORE, context.var("matchedCount"));

        _createInstance(context, mw);

        {
            mw.visitVarInsn(ALOAD, 1); // parser
            mw.visitMethodInsn(INVOKEVIRTUAL, DefaultJSONParser, "getContext", "()" + com.alibaba.json.util.ASMUtils.desc(ParseContext.class));
            mw.visitVarInsn(ASTORE, context.var("context"));

            mw.visitVarInsn(ALOAD, 1); // parser
            mw.visitVarInsn(ALOAD, context.var("context"));
            mw.visitVarInsn(ALOAD, context.var("instance"));
            mw.visitVarInsn(ALOAD, 3); // fieldName
            mw.visitMethodInsn(INVOKEVIRTUAL, DefaultJSONParser, "setContext", //
                    "(" + com.alibaba.json.util.ASMUtils.desc(ParseContext.class) + "Ljava/lang/Object;Ljava/lang/Object;)"
                            + com.alibaba.json.util.ASMUtils.desc(ParseContext.class));
            mw.visitVarInsn(ASTORE, context.var("childContext"));
        }

        mw.visitVarInsn(ALOAD, context.var("lexer"));
        mw.visitFieldInsn(GETFIELD, JSONLexerBase, "matchStat", "I");
        mw.visitLdcInsn(com.alibaba.json.parser.JSONLexerBase.END);
        //mw.visitJumpInsn(IF_ICMPEQ, return_);

        com.alibaba.json.asm.Label continue_3 = new com.alibaba.json.asm.Label();
        mw.visitJumpInsn(IF_ICMPNE, continue_3);
        mw.visitJumpInsn(GOTO_W, return_);
        mw.visitLabel(continue_3);

        mw.visitInsn(ICONST_0); // UNKOWN
        mw.visitIntInsn(ISTORE, context.var("matchStat"));

        int fieldListSize = context.fieldInfoList.length;
        for (int i = 0; i < fieldListSize; i += 32) {
            mw.visitInsn(ICONST_0);
            mw.visitVarInsn(ISTORE, context.var("_asm_flag_" + (i / 32)));
        }

        mw.visitVarInsn(ALOAD, context.var("lexer"));
        mw.visitLdcInsn(Feature.InitStringFieldAsEmpty.mask);
        mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "isEnabled", "(I)Z");
        mw.visitIntInsn(ISTORE, context.var("initStringFieldAsEmpty"));

        // declare and init
        for (int i = 0; i < fieldListSize; ++i) {
            com.alibaba.json.util.FieldInfo fieldInfo = context.fieldInfoList[i];
            Class<?> fieldClass = fieldInfo.fieldClass;

            if (fieldClass == boolean.class //
                || fieldClass == byte.class //
                || fieldClass == short.class //
                || fieldClass == int.class) {
                mw.visitInsn(ICONST_0);
                mw.visitVarInsn(ISTORE, context.var_asm(fieldInfo));
            } else if (fieldClass == long.class) {
                mw.visitInsn(LCONST_0);
                mw.visitVarInsn(LSTORE, context.var_asm(fieldInfo, 2));
            } else if (fieldClass == float.class) {
                mw.visitInsn(FCONST_0);
                mw.visitVarInsn(FSTORE, context.var_asm(fieldInfo));
            } else if (fieldClass == double.class) {
                mw.visitInsn(DCONST_0);
                mw.visitVarInsn(DSTORE, context.var_asm(fieldInfo, 2));
            } else {
                if (fieldClass == String.class) {
                    com.alibaba.json.asm.Label flagEnd_ = new com.alibaba.json.asm.Label();
                    com.alibaba.json.asm.Label flagElse_ = new com.alibaba.json.asm.Label();
                    mw.visitVarInsn(ILOAD, context.var("initStringFieldAsEmpty"));
                    mw.visitJumpInsn(IFEQ, flagElse_);
                    _setFlag(mw, context, i);
                    mw.visitVarInsn(ALOAD, context.var("lexer"));
                    mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "stringDefaultValue", "()Ljava/lang/String;");
                    mw.visitJumpInsn(GOTO, flagEnd_);

                    mw.visitLabel(flagElse_);
                    mw.visitInsn(ACONST_NULL);

                    mw.visitLabel(flagEnd_);
                } else {
                    mw.visitInsn(ACONST_NULL);
                }

                mw.visitTypeInsn(CHECKCAST, com.alibaba.json.util.ASMUtils.type(fieldClass)); // cast
                mw.visitVarInsn(ASTORE, context.var_asm(fieldInfo));
            }
        }

        for (int i = 0; i < fieldListSize; ++i) {
            com.alibaba.json.util.FieldInfo fieldInfo = context.fieldInfoList[i];
            Class<?> fieldClass = fieldInfo.fieldClass;
            Type fieldType = fieldInfo.fieldType;

            com.alibaba.json.asm.Label notMatch_ = new com.alibaba.json.asm.Label();

            if (fieldClass == boolean.class) {
                mw.visitVarInsn(ALOAD, context.var("lexer"));
                mw.visitVarInsn(ALOAD, 0);
                mw.visitFieldInsn(GETFIELD, context.className, context.fieldName(fieldInfo), "[C");
                mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "scanFieldBoolean", "([C)Z");
                mw.visitVarInsn(ISTORE, context.var_asm(fieldInfo));
            } else if (fieldClass == byte.class) {
                mw.visitVarInsn(ALOAD, context.var("lexer"));
                mw.visitVarInsn(ALOAD, 0);
                mw.visitFieldInsn(GETFIELD, context.className, context.fieldName(fieldInfo), "[C");
                mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "scanFieldInt", "([C)I");
                mw.visitVarInsn(ISTORE, context.var_asm(fieldInfo));

            } else if (fieldClass == Byte.class) {
                mw.visitVarInsn(ALOAD, context.var("lexer"));
                mw.visitVarInsn(ALOAD, 0);
                mw.visitFieldInsn(GETFIELD, context.className, context.fieldName(fieldInfo), "[C");
                mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "scanFieldInt", "([C)I");
                mw.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;");

                mw.visitVarInsn(ASTORE, context.var_asm(fieldInfo));
                com.alibaba.json.asm.Label valueNullEnd_ = new com.alibaba.json.asm.Label();
                mw.visitVarInsn(ALOAD, context.var("lexer"));
                mw.visitFieldInsn(GETFIELD, JSONLexerBase, "matchStat", "I");
                mw.visitLdcInsn(com.alibaba.json.parser.JSONLexerBase.VALUE_NULL);
                mw.visitJumpInsn(IF_ICMPNE, valueNullEnd_);
                mw.visitInsn(ACONST_NULL);
                mw.visitVarInsn(ASTORE, context.var_asm(fieldInfo));
                mw.visitLabel(valueNullEnd_);

            } else if (fieldClass == short.class) {
                mw.visitVarInsn(ALOAD, context.var("lexer"));
                mw.visitVarInsn(ALOAD, 0);
                mw.visitFieldInsn(GETFIELD, context.className, context.fieldName(fieldInfo), "[C");
                mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "scanFieldInt", "([C)I");
                mw.visitVarInsn(ISTORE, context.var_asm(fieldInfo));

            } else if (fieldClass == Short.class) {
                mw.visitVarInsn(ALOAD, context.var("lexer"));
                mw.visitVarInsn(ALOAD, 0);
                mw.visitFieldInsn(GETFIELD, context.className, context.fieldName(fieldInfo), "[C");
                mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "scanFieldInt", "([C)I");
                mw.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");

                mw.visitVarInsn(ASTORE, context.var_asm(fieldInfo));
                com.alibaba.json.asm.Label valueNullEnd_ = new com.alibaba.json.asm.Label();
                mw.visitVarInsn(ALOAD, context.var("lexer"));
                mw.visitFieldInsn(GETFIELD, JSONLexerBase, "matchStat", "I");
                mw.visitLdcInsn(com.alibaba.json.parser.JSONLexerBase.VALUE_NULL);
                mw.visitJumpInsn(IF_ICMPNE, valueNullEnd_);
                mw.visitInsn(ACONST_NULL);
                mw.visitVarInsn(ASTORE, context.var_asm(fieldInfo));
                mw.visitLabel(valueNullEnd_);

            } else if (fieldClass == int.class) {
                mw.visitVarInsn(ALOAD, context.var("lexer"));
                mw.visitVarInsn(ALOAD, 0);
                mw.visitFieldInsn(GETFIELD, context.className, context.fieldName(fieldInfo), "[C");
                mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "scanFieldInt", "([C)I");
                mw.visitVarInsn(ISTORE, context.var_asm(fieldInfo));

            } else if (fieldClass == Integer.class) {
                mw.visitVarInsn(ALOAD, context.var("lexer"));
                mw.visitVarInsn(ALOAD, 0);
                mw.visitFieldInsn(GETFIELD, context.className, context.fieldName(fieldInfo), "[C");
                mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "scanFieldInt", "([C)I");
                mw.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");

                mw.visitVarInsn(ASTORE, context.var_asm(fieldInfo));
                com.alibaba.json.asm.Label valueNullEnd_ = new com.alibaba.json.asm.Label();
                mw.visitVarInsn(ALOAD, context.var("lexer"));
                mw.visitFieldInsn(GETFIELD, JSONLexerBase, "matchStat", "I");
                mw.visitLdcInsn(com.alibaba.json.parser.JSONLexerBase.VALUE_NULL);
                mw.visitJumpInsn(IF_ICMPNE, valueNullEnd_);
                mw.visitInsn(ACONST_NULL);
                mw.visitVarInsn(ASTORE, context.var_asm(fieldInfo));
                mw.visitLabel(valueNullEnd_);

            } else if (fieldClass == long.class) {
                mw.visitVarInsn(ALOAD, context.var("lexer"));
                mw.visitVarInsn(ALOAD, 0);
                mw.visitFieldInsn(GETFIELD, context.className, context.fieldName(fieldInfo), "[C");
                mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "scanFieldLong", "([C)J");
                mw.visitVarInsn(LSTORE, context.var_asm(fieldInfo, 2));

            } else if (fieldClass == Long.class) {
                mw.visitVarInsn(ALOAD, context.var("lexer"));
                mw.visitVarInsn(ALOAD, 0);
                mw.visitFieldInsn(GETFIELD, context.className, context.fieldName(fieldInfo), "[C");
                mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "scanFieldLong", "([C)J");
                mw.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");

                mw.visitVarInsn(ASTORE, context.var_asm(fieldInfo));
                com.alibaba.json.asm.Label valueNullEnd_ = new com.alibaba.json.asm.Label();
                mw.visitVarInsn(ALOAD, context.var("lexer"));
                mw.visitFieldInsn(GETFIELD, JSONLexerBase, "matchStat", "I");
                mw.visitLdcInsn(com.alibaba.json.parser.JSONLexerBase.VALUE_NULL);
                mw.visitJumpInsn(IF_ICMPNE, valueNullEnd_);
                mw.visitInsn(ACONST_NULL);
                mw.visitVarInsn(ASTORE, context.var_asm(fieldInfo));
                mw.visitLabel(valueNullEnd_);

            } else if (fieldClass == float.class) {
                mw.visitVarInsn(ALOAD, context.var("lexer"));
                mw.visitVarInsn(ALOAD, 0);
                mw.visitFieldInsn(GETFIELD, context.className, context.fieldName(fieldInfo), "[C");
                mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "scanFieldFloat", "([C)F");
                mw.visitVarInsn(FSTORE, context.var_asm(fieldInfo));

            } else if (fieldClass == Float.class) {
                mw.visitVarInsn(ALOAD, context.var("lexer"));
                mw.visitVarInsn(ALOAD, 0);
                mw.visitFieldInsn(GETFIELD, context.className, context.fieldName(fieldInfo), "[C");
                mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "scanFieldFloat", "([C)F");
                mw.visitMethodInsn(INVOKESTATIC, "java/lang/Float", "valueOf", "(F)Ljava/lang/Float;");

                mw.visitVarInsn(ASTORE, context.var_asm(fieldInfo));
                com.alibaba.json.asm.Label valueNullEnd_ = new com.alibaba.json.asm.Label();
                mw.visitVarInsn(ALOAD, context.var("lexer"));
                mw.visitFieldInsn(GETFIELD, JSONLexerBase, "matchStat", "I");
                mw.visitLdcInsn(com.alibaba.json.parser.JSONLexerBase.VALUE_NULL);
                mw.visitJumpInsn(IF_ICMPNE, valueNullEnd_);
                mw.visitInsn(ACONST_NULL);
                mw.visitVarInsn(ASTORE, context.var_asm(fieldInfo));
                mw.visitLabel(valueNullEnd_);
            } else if (fieldClass == double.class) {
                mw.visitVarInsn(ALOAD, context.var("lexer"));
                mw.visitVarInsn(ALOAD, 0);
                mw.visitFieldInsn(GETFIELD, context.className, context.fieldName(fieldInfo), "[C");
                mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "scanFieldDouble", "([C)D");
                mw.visitVarInsn(DSTORE, context.var_asm(fieldInfo, 2));

            } else if (fieldClass == Double.class) {
                mw.visitVarInsn(ALOAD, context.var("lexer"));
                mw.visitVarInsn(ALOAD, 0);
                mw.visitFieldInsn(GETFIELD, context.className, context.fieldName(fieldInfo), "[C");
                mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "scanFieldDouble", "([C)D");
                mw.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");

                mw.visitVarInsn(ASTORE, context.var_asm(fieldInfo));
                com.alibaba.json.asm.Label valueNullEnd_ = new com.alibaba.json.asm.Label();
                mw.visitVarInsn(ALOAD, context.var("lexer"));
                mw.visitFieldInsn(GETFIELD, JSONLexerBase, "matchStat", "I");
                mw.visitLdcInsn(com.alibaba.json.parser.JSONLexerBase.VALUE_NULL);
                mw.visitJumpInsn(IF_ICMPNE, valueNullEnd_);
                mw.visitInsn(ACONST_NULL);
                mw.visitVarInsn(ASTORE, context.var_asm(fieldInfo));

                mw.visitLabel(valueNullEnd_);
            } else if (fieldClass == String.class) {
                mw.visitVarInsn(ALOAD, context.var("lexer"));
                mw.visitVarInsn(ALOAD, 0);
                mw.visitFieldInsn(GETFIELD, context.className, context.fieldName(fieldInfo), "[C");
                mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "scanFieldString", "([C)Ljava/lang/String;");
                mw.visitVarInsn(ASTORE, context.var_asm(fieldInfo));

            } else if (fieldClass == java.util.Date.class) {
                mw.visitVarInsn(ALOAD, context.var("lexer"));
                mw.visitVarInsn(ALOAD, 0);
                mw.visitFieldInsn(GETFIELD, context.className, context.fieldName(fieldInfo), "[C");
                mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "scanFieldDate", "([C)Ljava/util/Date;");
                mw.visitVarInsn(ASTORE, context.var_asm(fieldInfo));

            } else if (fieldClass == java.util.UUID.class) {
                mw.visitVarInsn(ALOAD, context.var("lexer"));
                mw.visitVarInsn(ALOAD, 0);
                mw.visitFieldInsn(GETFIELD, context.className, context.fieldName(fieldInfo), "[C");
                mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "scanFieldUUID", "([C)Ljava/util/UUID;");
                mw.visitVarInsn(ASTORE, context.var_asm(fieldInfo));

            } else if (fieldClass == BigDecimal.class) {
                mw.visitVarInsn(ALOAD, context.var("lexer"));
                mw.visitVarInsn(ALOAD, 0);
                mw.visitFieldInsn(GETFIELD, context.className, context.fieldName(fieldInfo), "[C");
                mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "scanFieldDecimal", "([C)Ljava/math/BigDecimal;");
                mw.visitVarInsn(ASTORE, context.var_asm(fieldInfo));
            } else if (fieldClass == BigInteger.class) {
                mw.visitVarInsn(ALOAD, context.var("lexer"));
                mw.visitVarInsn(ALOAD, 0);
                mw.visitFieldInsn(GETFIELD, context.className, context.fieldName(fieldInfo), "[C");
                mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "scanFieldBigInteger", "([C)Ljava/math/BigInteger;");
                mw.visitVarInsn(ASTORE, context.var_asm(fieldInfo));
            } else if (fieldClass == int[].class) {
                mw.visitVarInsn(ALOAD, context.var("lexer"));
                mw.visitVarInsn(ALOAD, 0);
                mw.visitFieldInsn(GETFIELD, context.className, context.fieldName(fieldInfo), "[C");
                mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "scanFieldIntArray", "([C)[I");
                mw.visitVarInsn(ASTORE, context.var_asm(fieldInfo));
            } else if (fieldClass == float[].class) {
                mw.visitVarInsn(ALOAD, context.var("lexer"));
                mw.visitVarInsn(ALOAD, 0);
                mw.visitFieldInsn(GETFIELD, context.className, context.fieldName(fieldInfo), "[C");
                mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "scanFieldFloatArray", "([C)[F");
                mw.visitVarInsn(ASTORE, context.var_asm(fieldInfo));
            } else if (fieldClass == float[][].class) {
                mw.visitVarInsn(ALOAD, context.var("lexer"));
                mw.visitVarInsn(ALOAD, 0);
                mw.visitFieldInsn(GETFIELD, context.className, context.fieldName(fieldInfo), "[C");
                mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "scanFieldFloatArray2", "([C)[[F");
                mw.visitVarInsn(ASTORE, context.var_asm(fieldInfo));
            } else if (fieldClass.isEnum()) {
                mw.visitVarInsn(ALOAD, 0);
                mw.visitVarInsn(ALOAD, context.var("lexer"));
                mw.visitVarInsn(ALOAD, 0);
                mw.visitFieldInsn(GETFIELD, context.className, context.fieldName(fieldInfo), "[C");
                _getFieldDeser(context, mw, fieldInfo);
                mw.visitMethodInsn(INVOKEVIRTUAL, com.alibaba.json.util.ASMUtils.type(JavaBeanDeserializer.class), "scanEnum"
                        , "(L" + JSONLexerBase + ";[C" + com.alibaba.json.util.ASMUtils.desc(ObjectDeserializer.class) + ")Ljava/lang/Enum;");
                mw.visitTypeInsn(CHECKCAST, com.alibaba.json.util.ASMUtils.type(fieldClass)); // cast
                mw.visitVarInsn(ASTORE, context.var_asm(fieldInfo));

//            } else if (fieldClass.isEnum()) {
//                mw.visitVarInsn(ALOAD, context.var("lexer"));
//                mw.visitVarInsn(ALOAD, 0);
//                mw.visitFieldInsn(GETFIELD, context.className, context.fieldName(fieldInfo), "[C");
//                Label enumNull_ = new Label();
//                mw.visitInsn(ACONST_NULL);
//                mw.visitTypeInsn(CHECKCAST, type(fieldClass)); // cast
//                mw.visitVarInsn(ASTORE, context.var_asm(fieldInfo));
//
//                mw.visitVarInsn(ALOAD, 1);
//
//                mw.visitMethodInsn(INVOKEVIRTUAL, DefaultJSONParser, "getSymbolTable", "()" + desc(SymbolTable.class));
//
//                mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "scanFieldSymbol",
//                        "([C" + desc(SymbolTable.class) + ")Ljava/lang/String;");
//                mw.visitInsn(DUP);
//                mw.visitVarInsn(ASTORE, context.var(fieldInfo.name + "_asm_enumName"));
//
//                mw.visitJumpInsn(IFNULL, enumNull_);
//
//                mw.visitVarInsn(ALOAD, context.var(fieldInfo.name + "_asm_enumName"));
//                mw.visitMethodInsn(INVOKEVIRTUAL, type(String.class), "length", "()I");
//                mw.visitJumpInsn(IFEQ, enumNull_);
//
//                mw.visitVarInsn(ALOAD, context.var(fieldInfo.name + "_asm_enumName"));
//                mw.visitMethodInsn(INVOKESTATIC, type(fieldClass), "valueOf",
//                        "(Ljava/lang/String;)" + desc(fieldClass));
//                mw.visitVarInsn(ASTORE, context.var_asm(fieldInfo));
//                mw.visitLabel(enumNull_);
            } else if (Collection.class.isAssignableFrom(fieldClass)) {
                mw.visitVarInsn(ALOAD, context.var("lexer"));
                mw.visitVarInsn(ALOAD, 0);
                mw.visitFieldInsn(GETFIELD, context.className, context.fieldName(fieldInfo), "[C");

                Class<?> itemClass = TypeUtils.getCollectionItemClass(fieldType);

                if (itemClass == String.class) {
                    mw.visitLdcInsn(com.alibaba.json.asm.Type.getType(com.alibaba.json.util.ASMUtils.desc(fieldClass))); // cast
                    mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "scanFieldStringArray",
                            "([CLjava/lang/Class;)" + com.alibaba.json.util.ASMUtils.desc(Collection.class));
                    mw.visitVarInsn(ASTORE, context.var_asm(fieldInfo));
                } else {
                    _deserialze_list_obj(context, mw, reset_, fieldInfo, fieldClass, itemClass, i);

                    if (i == fieldListSize - 1) {
                        _deserialize_endCheck(context, mw, reset_);
                    }
                    continue;
                }
            } else {
                _deserialze_obj(context, mw, reset_, fieldInfo, fieldClass, i);

                if (i == fieldListSize - 1) {
                    _deserialize_endCheck(context, mw, reset_);
                }

                continue;
            }

            mw.visitVarInsn(ALOAD, context.var("lexer"));
            mw.visitFieldInsn(GETFIELD, JSONLexerBase, "matchStat", "I");
            com.alibaba.json.asm.Label flag_ = new com.alibaba.json.asm.Label();
            // mw.visitInsn(DUP);
            mw.visitJumpInsn(IFLE, flag_);
            _setFlag(mw, context, i);
            mw.visitLabel(flag_);

            mw.visitVarInsn(ALOAD, context.var("lexer"));
            mw.visitFieldInsn(GETFIELD, JSONLexerBase, "matchStat", "I");
            mw.visitInsn(DUP);
            mw.visitVarInsn(ISTORE, context.var("matchStat"));

            mw.visitLdcInsn(com.alibaba.json.parser.JSONLexerBase.NOT_MATCH);
            mw.visitJumpInsn(IF_ICMPEQ, reset_);

            mw.visitVarInsn(ALOAD, context.var("lexer"));
            mw.visitFieldInsn(GETFIELD, JSONLexerBase, "matchStat", "I");
            mw.visitJumpInsn(IFLE, notMatch_);

            // increment matchedCount
            mw.visitVarInsn(ILOAD, context.var("matchedCount"));
            mw.visitInsn(ICONST_1);
            mw.visitInsn(IADD);
            mw.visitVarInsn(ISTORE, context.var("matchedCount"));

            mw.visitVarInsn(ALOAD, context.var("lexer"));
            mw.visitFieldInsn(GETFIELD, JSONLexerBase, "matchStat", "I");
            mw.visitLdcInsn(com.alibaba.json.parser.JSONLexerBase.END);
            mw.visitJumpInsn(IF_ICMPEQ, end_);

            mw.visitLabel(notMatch_);

            if (i == fieldListSize - 1) {
                mw.visitVarInsn(ALOAD, context.var("lexer"));
                mw.visitFieldInsn(GETFIELD, JSONLexerBase, "matchStat", "I");
                mw.visitLdcInsn(com.alibaba.json.parser.JSONLexerBase.END);
                mw.visitJumpInsn(IF_ICMPNE, reset_);
            }
        } // endFor

        mw.visitLabel(end_);

        if (!context.clazz.isInterface() && !Modifier.isAbstract(context.clazz.getModifiers())) {
            _batchSet(context, mw);
        }

        mw.visitLabel(return_);

        _setContext(context, mw);
        mw.visitVarInsn(ALOAD, context.var("instance"));

        Method buildMethod = context.beanInfo.buildMethod;
        if (buildMethod != null) {
            mw.visitMethodInsn(INVOKEVIRTUAL, com.alibaba.json.util.ASMUtils.type(context.getInstClass()), buildMethod.getName(),
                    "()" + com.alibaba.json.util.ASMUtils.desc(buildMethod.getReturnType()));
        }

        mw.visitInsn(ARETURN);

        mw.visitLabel(reset_);

        _batchSet(context, mw);
        mw.visitVarInsn(ALOAD, 0);
        mw.visitVarInsn(ALOAD, 1);
        mw.visitVarInsn(ALOAD, 2);
        mw.visitVarInsn(ALOAD, 3);
        mw.visitVarInsn(ALOAD, context.var("instance"));
        mw.visitVarInsn(ILOAD, 4);


        int flagSize = (fieldListSize / 32);

        if (fieldListSize != 0 && (fieldListSize % 32) != 0) {
            flagSize += 1;
        }

        if (flagSize == 1) {
            mw.visitInsn(ICONST_1);
        } else {
            mw.visitIntInsn(BIPUSH, flagSize);
        }
        mw.visitIntInsn(NEWARRAY, T_INT);
        for (int i = 0; i < flagSize; ++i) {
            mw.visitInsn(DUP);
            if (i == 0) {
                mw.visitInsn(ICONST_0);
            } else if (i == 1) {
                mw.visitInsn(ICONST_1);
            } else {
                mw.visitIntInsn(BIPUSH, i);
            }
            mw.visitVarInsn(ILOAD, context.var("_asm_flag_" + i));
            mw.visitInsn(IASTORE);
        }

        mw.visitMethodInsn(INVOKEVIRTUAL, com.alibaba.json.util.ASMUtils.type(JavaBeanDeserializer.class),
                "parseRest", "(L" + DefaultJSONParser
                        + ";Ljava/lang/reflect/Type;Ljava/lang/Object;Ljava/lang/Object;I[I)Ljava/lang/Object;");
        mw.visitTypeInsn(CHECKCAST, com.alibaba.json.util.ASMUtils.type(context.clazz)); // cast
        mw.visitInsn(ARETURN);

        mw.visitLabel(super_);
        mw.visitVarInsn(ALOAD, 0);
        mw.visitVarInsn(ALOAD, 1);
        mw.visitVarInsn(ALOAD, 2);
        mw.visitVarInsn(ALOAD, 3);
        mw.visitVarInsn(ILOAD, 4);
        mw.visitMethodInsn(INVOKESPECIAL, com.alibaba.json.util.ASMUtils.type(JavaBeanDeserializer.class), //
                "deserialze", //
                "(L" + DefaultJSONParser + ";Ljava/lang/reflect/Type;Ljava/lang/Object;I)Ljava/lang/Object;");
        mw.visitInsn(ARETURN);

        mw.visitMaxs(10, context.variantIndex);
        mw.visitEnd();

    }

    private void defineVarLexer(Context context, com.alibaba.json.asm.MethodVisitor mw) {
        mw.visitVarInsn(ALOAD, 1);
        mw.visitFieldInsn(GETFIELD, DefaultJSONParser, "lexer", com.alibaba.json.util.ASMUtils.desc(JSONLexer.class));
        mw.visitTypeInsn(CHECKCAST, JSONLexerBase); // cast
        mw.visitVarInsn(ASTORE, context.var("lexer"));
    }

    private void _createInstance(Context context, com.alibaba.json.asm.MethodVisitor mw) {
        com.alibaba.json.util.JavaBeanInfo beanInfo = context.beanInfo;
        Constructor<?> defaultConstructor = beanInfo.defaultConstructor;
        if (Modifier.isPublic(defaultConstructor.getModifiers())) {
            mw.visitTypeInsn(NEW, com.alibaba.json.util.ASMUtils.type(context.getInstClass()));
            mw.visitInsn(DUP);

            mw.visitMethodInsn(INVOKESPECIAL, com.alibaba.json.util.ASMUtils.type(defaultConstructor.getDeclaringClass()), "<init>", "()V");
        } else {
            mw.visitVarInsn(ALOAD, 0);
            mw.visitVarInsn(ALOAD, 1);
            mw.visitVarInsn(ALOAD, 0);
            mw.visitFieldInsn(GETFIELD, com.alibaba.json.util.ASMUtils.type(JavaBeanDeserializer.class), "clazz", "Ljava/lang/Class;");
            mw.visitMethodInsn(INVOKESPECIAL, com.alibaba.json.util.ASMUtils.type(JavaBeanDeserializer.class), "createInstance",
                    "(L" + DefaultJSONParser + ";Ljava/lang/reflect/Type;)Ljava/lang/Object;");
            mw.visitTypeInsn(CHECKCAST, com.alibaba.json.util.ASMUtils.type(context.getInstClass())); // cast
        }

        mw.visitVarInsn(ASTORE, context.var("instance"));
    }

    private void _batchSet(Context context, com.alibaba.json.asm.MethodVisitor mw) {
        _batchSet(context, mw, true);
    }

    private void _batchSet(Context context, com.alibaba.json.asm.MethodVisitor mw, boolean flag) {
        for (int i = 0, size = context.fieldInfoList.length; i < size; ++i) {
            com.alibaba.json.asm.Label notSet_ = new com.alibaba.json.asm.Label();

            if (flag) {
                _isFlag(mw, context, i, notSet_);
            }

            com.alibaba.json.util.FieldInfo fieldInfo = context.fieldInfoList[i];
            _loadAndSet(context, mw, fieldInfo);

            if (flag) {
                mw.visitLabel(notSet_);
            }
        }
    }

    private void _loadAndSet(Context context, com.alibaba.json.asm.MethodVisitor mw, com.alibaba.json.util.FieldInfo fieldInfo) {
        Class<?> fieldClass = fieldInfo.fieldClass;
        Type fieldType = fieldInfo.fieldType;

        if (fieldClass == boolean.class) {
            mw.visitVarInsn(ALOAD, context.var("instance"));
            mw.visitVarInsn(ILOAD, context.var_asm(fieldInfo));
            _set(context, mw, fieldInfo);
        } else if (fieldClass == byte.class //
                || fieldClass == short.class //
                || fieldClass == int.class //
                   || fieldClass == char.class) {
            mw.visitVarInsn(ALOAD, context.var("instance"));
            mw.visitVarInsn(ILOAD, context.var_asm(fieldInfo));
            _set(context, mw, fieldInfo);
        } else if (fieldClass == long.class) {
            mw.visitVarInsn(ALOAD, context.var("instance"));
            mw.visitVarInsn(LLOAD, context.var_asm(fieldInfo, 2));
            if (fieldInfo.method != null) {
                mw.visitMethodInsn(INVOKEVIRTUAL, com.alibaba.json.util.ASMUtils.type(context.getInstClass()), fieldInfo.method.getName(),
                        com.alibaba.json.util.ASMUtils.desc(fieldInfo.method));
                if (!fieldInfo.method.getReturnType().equals(Void.TYPE)) {
                    mw.visitInsn(POP);
                }
            } else {
                mw.visitFieldInsn(PUTFIELD, com.alibaba.json.util.ASMUtils.type(fieldInfo.declaringClass), fieldInfo.field.getName(),
                        com.alibaba.json.util.ASMUtils.desc(fieldInfo.fieldClass));
            }
        } else if (fieldClass == float.class) {
            mw.visitVarInsn(ALOAD, context.var("instance"));
            mw.visitVarInsn(FLOAD, context.var_asm(fieldInfo));
            _set(context, mw, fieldInfo);
        } else if (fieldClass == double.class) {
            mw.visitVarInsn(ALOAD, context.var("instance"));
            mw.visitVarInsn(DLOAD, context.var_asm(fieldInfo, 2));
            _set(context, mw, fieldInfo);
        } else if (fieldClass == String.class) {
            mw.visitVarInsn(ALOAD, context.var("instance"));
            mw.visitVarInsn(ALOAD, context.var_asm(fieldInfo));
            _set(context, mw, fieldInfo);
        } else if (fieldClass.isEnum()) {
            mw.visitVarInsn(ALOAD, context.var("instance"));
            mw.visitVarInsn(ALOAD, context.var_asm(fieldInfo));
            _set(context, mw, fieldInfo);
        } else if (Collection.class.isAssignableFrom(fieldClass)) {
            mw.visitVarInsn(ALOAD, context.var("instance"));
            Type itemType = TypeUtils.getCollectionItemClass(fieldType);
            if (itemType == String.class) {
                mw.visitVarInsn(ALOAD, context.var_asm(fieldInfo));
                mw.visitTypeInsn(CHECKCAST, com.alibaba.json.util.ASMUtils.type(fieldClass)); // cast
            } else {
                mw.visitVarInsn(ALOAD, context.var_asm(fieldInfo));
            }
            _set(context, mw, fieldInfo);

        } else {
            mw.visitVarInsn(ALOAD, context.var("instance"));
            mw.visitVarInsn(ALOAD, context.var_asm(fieldInfo));
            _set(context, mw, fieldInfo);
        }
    }

    private void _set(Context context, com.alibaba.json.asm.MethodVisitor mw, com.alibaba.json.util.FieldInfo fieldInfo) {
        Method method = fieldInfo.method;
        if (method != null) {
            Class<?> declaringClass = method.getDeclaringClass();
            mw.visitMethodInsn(declaringClass.isInterface() ? INVOKEINTERFACE : INVOKEVIRTUAL, com.alibaba.json.util.ASMUtils.type(fieldInfo.declaringClass), method.getName(), com.alibaba.json.util.ASMUtils.desc(method));

            if (!fieldInfo.method.getReturnType().equals(Void.TYPE)) {
                mw.visitInsn(POP);
            }
        } else {
            mw.visitFieldInsn(PUTFIELD, com.alibaba.json.util.ASMUtils.type(fieldInfo.declaringClass), fieldInfo.field.getName(),
                    com.alibaba.json.util.ASMUtils.desc(fieldInfo.fieldClass));
        }
    }

    private void _setContext(Context context, com.alibaba.json.asm.MethodVisitor mw) {
        mw.visitVarInsn(ALOAD, 1); // parser
        mw.visitVarInsn(ALOAD, context.var("context"));
        mw.visitMethodInsn(INVOKEVIRTUAL, DefaultJSONParser, "setContext", "(" + com.alibaba.json.util.ASMUtils.desc(ParseContext.class) + ")V");

        com.alibaba.json.asm.Label endIf_ = new com.alibaba.json.asm.Label();
        mw.visitVarInsn(ALOAD, context.var("childContext"));
        mw.visitJumpInsn(IFNULL, endIf_);

        mw.visitVarInsn(ALOAD, context.var("childContext"));
        mw.visitVarInsn(ALOAD, context.var("instance"));
        mw.visitFieldInsn(PUTFIELD, com.alibaba.json.util.ASMUtils.type(ParseContext.class), "object", "Ljava/lang/Object;");

        mw.visitLabel(endIf_);
    }

    private void _deserialize_endCheck(Context context, com.alibaba.json.asm.MethodVisitor mw, com.alibaba.json.asm.Label reset_) {
        mw.visitIntInsn(ILOAD, context.var("matchedCount"));
        mw.visitJumpInsn(IFLE, reset_);

        mw.visitVarInsn(ALOAD, context.var("lexer"));
        mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "token", "()I");
        mw.visitLdcInsn(JSONToken.RBRACE);
        mw.visitJumpInsn(IF_ICMPNE, reset_);

        // mw.visitLabel(nextToken_);
        _quickNextTokenComma(context, mw);
    }

    private void _deserialze_list_obj(Context context, com.alibaba.json.asm.MethodVisitor mw, com.alibaba.json.asm.Label reset_, com.alibaba.json.util.FieldInfo fieldInfo,
                                      Class<?> fieldClass, Class<?> itemType, int i) {
        com.alibaba.json.asm.Label _end_if = new com.alibaba.json.asm.Label();

        mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "matchField", "([C)Z");
        mw.visitJumpInsn(IFEQ, _end_if);

        _setFlag(mw, context, i);

        com.alibaba.json.asm.Label valueNotNull_ = new com.alibaba.json.asm.Label();
        mw.visitVarInsn(ALOAD, context.var("lexer"));
        mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "token", "()I");
        mw.visitLdcInsn(JSONToken.NULL);
        mw.visitJumpInsn(IF_ICMPNE, valueNotNull_);

        mw.visitVarInsn(ALOAD, context.var("lexer"));
        mw.visitLdcInsn(JSONToken.COMMA);
        mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "nextToken", "(I)V");
        mw.visitJumpInsn(GOTO, _end_if);
        // loop_end_

        mw.visitLabel(valueNotNull_);

        com.alibaba.json.asm.Label storeCollection_ = new com.alibaba.json.asm.Label(), endSet_ = new com.alibaba.json.asm.Label(), lbacketNormal_ = new com.alibaba.json.asm.Label();
        mw.visitVarInsn(ALOAD, context.var("lexer"));
        mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "token", "()I");
        mw.visitLdcInsn(JSONToken.SET);
        mw.visitJumpInsn(IF_ICMPNE, endSet_);

        mw.visitVarInsn(ALOAD, context.var("lexer"));
        mw.visitLdcInsn(JSONToken.LBRACKET);
        mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "nextToken", "(I)V");

        _newCollection(mw, fieldClass, i, true);

        mw.visitJumpInsn(GOTO, storeCollection_);

        mw.visitLabel(endSet_);

        // if (lexer.token() != JSONToken.LBRACKET) reset
        mw.visitVarInsn(ALOAD, context.var("lexer"));
        mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "token", "()I");
        mw.visitLdcInsn(JSONToken.LBRACKET);
        mw.visitJumpInsn(IF_ICMPEQ, lbacketNormal_);

        // if (lexer.token() == JSONToken.LBRACE) reset
        mw.visitVarInsn(ALOAD, context.var("lexer"));
        mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "token", "()I");
        mw.visitLdcInsn(JSONToken.LBRACE);
        mw.visitJumpInsn(IF_ICMPNE, reset_);

        _newCollection(mw, fieldClass, i, false);
        mw.visitVarInsn(ASTORE, context.var_asm(fieldInfo));

        _getCollectionFieldItemDeser(context, mw, fieldInfo, itemType);
        mw.visitVarInsn(ALOAD, 1);
        mw.visitLdcInsn(com.alibaba.json.asm.Type.getType(com.alibaba.json.util.ASMUtils.desc(itemType)));
        mw.visitInsn(ICONST_0);
        mw.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
        mw.visitMethodInsn(INVOKEINTERFACE, com.alibaba.json.util.ASMUtils.type(ObjectDeserializer.class), "deserialze",
                "(L" + DefaultJSONParser + ";Ljava/lang/reflect/Type;Ljava/lang/Object;)Ljava/lang/Object;");
        mw.visitVarInsn(ASTORE, context.var("list_item_value"));

        mw.visitVarInsn(ALOAD, context.var_asm(fieldInfo));
        mw.visitVarInsn(ALOAD, context.var("list_item_value"));
        if (fieldClass.isInterface()) {
            mw.visitMethodInsn(INVOKEINTERFACE, com.alibaba.json.util.ASMUtils.type(fieldClass), "add", "(Ljava/lang/Object;)Z");
        } else {
            mw.visitMethodInsn(INVOKEVIRTUAL, com.alibaba.json.util.ASMUtils.type(fieldClass), "add", "(Ljava/lang/Object;)Z");
        }
        mw.visitInsn(POP);

        mw.visitJumpInsn(GOTO, _end_if);

        mw.visitLabel(lbacketNormal_);

        _newCollection(mw, fieldClass, i, false);

        mw.visitLabel(storeCollection_);
        mw.visitVarInsn(ASTORE, context.var_asm(fieldInfo));

        boolean isPrimitive = ParserConfig.isPrimitive2(fieldInfo.fieldClass);
        _getCollectionFieldItemDeser(context, mw, fieldInfo, itemType);
        if (isPrimitive) {
            mw.visitMethodInsn(INVOKEINTERFACE, com.alibaba.json.util.ASMUtils.type(ObjectDeserializer.class), "getFastMatchToken", "()I");
            mw.visitVarInsn(ISTORE, context.var("fastMatchToken"));

            mw.visitVarInsn(ALOAD, context.var("lexer"));
            mw.visitVarInsn(ILOAD, context.var("fastMatchToken"));
            mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "nextToken", "(I)V");
        } else {
            mw.visitInsn(POP);
            mw.visitLdcInsn(JSONToken.LBRACE);
            mw.visitVarInsn(ISTORE, context.var("fastMatchToken"));

            _quickNextToken(context, mw, JSONToken.LBRACE);
        }

        { // setContext
            mw.visitVarInsn(ALOAD, 1);
            mw.visitMethodInsn(INVOKEVIRTUAL, DefaultJSONParser, "getContext", "()" + com.alibaba.json.util.ASMUtils.desc(ParseContext.class));
            mw.visitVarInsn(ASTORE, context.var("listContext"));

            mw.visitVarInsn(ALOAD, 1); // parser
            mw.visitVarInsn(ALOAD, context.var_asm(fieldInfo));
            mw.visitLdcInsn(fieldInfo.name);
            mw.visitMethodInsn(INVOKEVIRTUAL, DefaultJSONParser, "setContext",
                    "(Ljava/lang/Object;Ljava/lang/Object;)" + com.alibaba.json.util.ASMUtils.desc(ParseContext.class));
            mw.visitInsn(POP);
        }

        com.alibaba.json.asm.Label loop_ = new com.alibaba.json.asm.Label();
        com.alibaba.json.asm.Label loop_end_ = new com.alibaba.json.asm.Label();

        // for (;;) {
        mw.visitInsn(ICONST_0);
        mw.visitVarInsn(ISTORE, context.var("i"));
        mw.visitLabel(loop_);

        mw.visitVarInsn(ALOAD, context.var("lexer"));
        mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "token", "()I");
        mw.visitLdcInsn(JSONToken.RBRACKET);

        mw.visitJumpInsn(IF_ICMPEQ, loop_end_);

        // Object value = itemDeserializer.deserialze(parser, null);
        // array.add(value);

        mw.visitVarInsn(ALOAD, 0);
        mw.visitFieldInsn(GETFIELD, context.className, fieldInfo.name + "_asm_list_item_deser__",
                com.alibaba.json.util.ASMUtils.desc(ObjectDeserializer.class));
        mw.visitVarInsn(ALOAD, 1);
        mw.visitLdcInsn(com.alibaba.json.asm.Type.getType(com.alibaba.json.util.ASMUtils.desc(itemType)));
        mw.visitVarInsn(ILOAD, context.var("i"));
        mw.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
        mw.visitMethodInsn(INVOKEINTERFACE, com.alibaba.json.util.ASMUtils.type(ObjectDeserializer.class), "deserialze",
                "(L" + DefaultJSONParser + ";Ljava/lang/reflect/Type;Ljava/lang/Object;)Ljava/lang/Object;");
        mw.visitVarInsn(ASTORE, context.var("list_item_value"));

        mw.visitIincInsn(context.var("i"), 1);

        mw.visitVarInsn(ALOAD, context.var_asm(fieldInfo));
        mw.visitVarInsn(ALOAD, context.var("list_item_value"));
        if (fieldClass.isInterface()) {
            mw.visitMethodInsn(INVOKEINTERFACE, com.alibaba.json.util.ASMUtils.type(fieldClass), "add", "(Ljava/lang/Object;)Z");
        } else {
            mw.visitMethodInsn(INVOKEVIRTUAL, com.alibaba.json.util.ASMUtils.type(fieldClass), "add", "(Ljava/lang/Object;)Z");
        }
        mw.visitInsn(POP);

        mw.visitVarInsn(ALOAD, 1);
        mw.visitVarInsn(ALOAD, context.var_asm(fieldInfo));
        mw.visitMethodInsn(INVOKEVIRTUAL, DefaultJSONParser, "checkListResolve", "(Ljava/util/Collection;)V");

        mw.visitVarInsn(ALOAD, context.var("lexer"));
        mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "token", "()I");
        mw.visitLdcInsn(JSONToken.COMMA);
        mw.visitJumpInsn(IF_ICMPNE, loop_);

        if (isPrimitive) {
            mw.visitVarInsn(ALOAD, context.var("lexer"));
            mw.visitVarInsn(ILOAD, context.var("fastMatchToken"));
            mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "nextToken", "(I)V");
        } else {
            _quickNextToken(context, mw, JSONToken.LBRACE);
        }
        
        mw.visitJumpInsn(GOTO, loop_);

        mw.visitLabel(loop_end_);

        // mw.visitVarInsn(ASTORE, context.var("context"));
        // parser.setContext(context);
        { // setContext
            mw.visitVarInsn(ALOAD, 1); // parser
            mw.visitVarInsn(ALOAD, context.var("listContext"));
            mw.visitMethodInsn(INVOKEVIRTUAL, DefaultJSONParser, "setContext", "(" + com.alibaba.json.util.ASMUtils.desc(ParseContext.class) + ")V");
        }

        mw.visitVarInsn(ALOAD, context.var("lexer"));
        mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "token", "()I");
        mw.visitLdcInsn(JSONToken.RBRACKET);
        mw.visitJumpInsn(IF_ICMPNE, reset_);

        _quickNextTokenComma(context, mw);
        // lexer.nextToken(JSONToken.COMMA);

        mw.visitLabel(_end_if);
    }

    private void _quickNextToken(Context context, com.alibaba.json.asm.MethodVisitor mw, int token) {
        com.alibaba.json.asm.Label quickElse_ = new com.alibaba.json.asm.Label(), quickEnd_ = new com.alibaba.json.asm.Label();
        mw.visitVarInsn(ALOAD, context.var("lexer"));
        mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "getCurrent", "()C");
        if (token == JSONToken.LBRACE) {
            mw.visitVarInsn(BIPUSH, '{');
        } else if (token == JSONToken.LBRACKET) {
            mw.visitVarInsn(BIPUSH, '[');
        } else {
            throw new IllegalStateException();
        }

        mw.visitJumpInsn(IF_ICMPNE, quickElse_);

        mw.visitVarInsn(ALOAD, context.var("lexer"));
        mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "next", "()C");
        mw.visitInsn(POP);
        mw.visitVarInsn(ALOAD, context.var("lexer"));
        mw.visitLdcInsn(token);
        mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "setToken", "(I)V");
        mw.visitJumpInsn(GOTO, quickEnd_);

        mw.visitLabel(quickElse_);
        mw.visitVarInsn(ALOAD, context.var("lexer"));
        mw.visitLdcInsn(token);
        mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "nextToken", "(I)V");

        mw.visitLabel(quickEnd_);
    }

    private void _quickNextTokenComma(Context context, com.alibaba.json.asm.MethodVisitor mw) {
        com.alibaba.json.asm.Label quickElse_ = new com.alibaba.json.asm.Label(), quickElseIf0_ = new com.alibaba.json.asm.Label(), quickElseIf1_ = new com.alibaba.json.asm.Label(), quickElseIf2_ = new com.alibaba.json.asm.Label(), quickEnd_ = new com.alibaba.json.asm.Label();
        mw.visitVarInsn(ALOAD, context.var("lexer"));
        mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "getCurrent", "()C");
        mw.visitInsn(DUP);
        mw.visitVarInsn(ISTORE, context.var("ch"));
        mw.visitVarInsn(BIPUSH, ',');
        mw.visitJumpInsn(IF_ICMPNE, quickElseIf0_);

        mw.visitVarInsn(ALOAD, context.var("lexer"));
        mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "next", "()C");
        mw.visitInsn(POP);
        mw.visitVarInsn(ALOAD, context.var("lexer"));
        mw.visitLdcInsn(JSONToken.COMMA);
        mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "setToken", "(I)V");
        mw.visitJumpInsn(GOTO, quickEnd_);
        
        mw.visitLabel(quickElseIf0_);
        mw.visitVarInsn(ILOAD, context.var("ch"));
        mw.visitVarInsn(BIPUSH, '}');
        mw.visitJumpInsn(IF_ICMPNE, quickElseIf1_);

        mw.visitVarInsn(ALOAD, context.var("lexer"));
        mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "next", "()C");
        mw.visitInsn(POP);
        mw.visitVarInsn(ALOAD, context.var("lexer"));
        mw.visitLdcInsn(JSONToken.RBRACE);
        mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "setToken", "(I)V");
        mw.visitJumpInsn(GOTO, quickEnd_);
        
        mw.visitLabel(quickElseIf1_);
        mw.visitVarInsn(ILOAD, context.var("ch"));
        mw.visitVarInsn(BIPUSH, ']');
        mw.visitJumpInsn(IF_ICMPNE, quickElseIf2_);

        mw.visitVarInsn(ALOAD, context.var("lexer"));
        mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "next", "()C");
        mw.visitInsn(POP);
        mw.visitVarInsn(ALOAD, context.var("lexer"));
        mw.visitLdcInsn(JSONToken.RBRACKET);
        mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "setToken", "(I)V");
        mw.visitJumpInsn(GOTO, quickEnd_);
        
        mw.visitLabel(quickElseIf2_);
        mw.visitVarInsn(ILOAD, context.var("ch"));
        mw.visitVarInsn(BIPUSH, JSONLexer.EOI);
        mw.visitJumpInsn(IF_ICMPNE, quickElse_);

        mw.visitVarInsn(ALOAD, context.var("lexer"));
        mw.visitLdcInsn(JSONToken.EOF);
        mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "setToken", "(I)V");
        mw.visitJumpInsn(GOTO, quickEnd_);

        mw.visitLabel(quickElse_);
        mw.visitVarInsn(ALOAD, context.var("lexer"));
        mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "nextToken", "()V");

        mw.visitLabel(quickEnd_);
    }

    private void _getCollectionFieldItemDeser(Context context, com.alibaba.json.asm.MethodVisitor mw, com.alibaba.json.util.FieldInfo fieldInfo,
                                              Class<?> itemType) {
        com.alibaba.json.asm.Label notNull_ = new com.alibaba.json.asm.Label();
        mw.visitVarInsn(ALOAD, 0);
        mw.visitFieldInsn(GETFIELD, context.className, fieldInfo.name + "_asm_list_item_deser__",
                com.alibaba.json.util.ASMUtils.desc(ObjectDeserializer.class));
        mw.visitJumpInsn(IFNONNULL, notNull_);

        mw.visitVarInsn(ALOAD, 0);

        mw.visitVarInsn(ALOAD, 1);
        mw.visitMethodInsn(INVOKEVIRTUAL, DefaultJSONParser, "getConfig", "()" + com.alibaba.json.util.ASMUtils.desc(ParserConfig.class));
        mw.visitLdcInsn(com.alibaba.json.asm.Type.getType(com.alibaba.json.util.ASMUtils.desc(itemType)));
        mw.visitMethodInsn(INVOKEVIRTUAL, com.alibaba.json.util.ASMUtils.type(ParserConfig.class), "getDeserializer",
                "(Ljava/lang/reflect/Type;)" + com.alibaba.json.util.ASMUtils.desc(ObjectDeserializer.class));

        mw.visitFieldInsn(PUTFIELD, context.className, fieldInfo.name + "_asm_list_item_deser__",
                com.alibaba.json.util.ASMUtils.desc(ObjectDeserializer.class));

        mw.visitLabel(notNull_);
        mw.visitVarInsn(ALOAD, 0);
        mw.visitFieldInsn(GETFIELD, context.className, fieldInfo.name + "_asm_list_item_deser__",
                com.alibaba.json.util.ASMUtils.desc(ObjectDeserializer.class));
    }

    private void _newCollection(com.alibaba.json.asm.MethodVisitor mw, Class<?> fieldClass, int i, boolean set) {
        if (fieldClass.isAssignableFrom(ArrayList.class) && !set) {
            mw.visitTypeInsn(NEW, "java/util/ArrayList");
            mw.visitInsn(DUP);
            mw.visitMethodInsn(INVOKESPECIAL, "java/util/ArrayList", "<init>", "()V");
        } else if (fieldClass.isAssignableFrom(LinkedList.class) && !set) {
            mw.visitTypeInsn(NEW, com.alibaba.json.util.ASMUtils.type(LinkedList.class));
            mw.visitInsn(DUP);
            mw.visitMethodInsn(INVOKESPECIAL, com.alibaba.json.util.ASMUtils.type(LinkedList.class), "<init>", "()V");
        } else if (fieldClass.isAssignableFrom(HashSet.class)) {
            mw.visitTypeInsn(NEW, com.alibaba.json.util.ASMUtils.type(HashSet.class));
            mw.visitInsn(DUP);
            mw.visitMethodInsn(INVOKESPECIAL, com.alibaba.json.util.ASMUtils.type(HashSet.class), "<init>", "()V");
        } else if (fieldClass.isAssignableFrom(TreeSet.class)) {
            mw.visitTypeInsn(NEW, com.alibaba.json.util.ASMUtils.type(TreeSet.class));
            mw.visitInsn(DUP);
            mw.visitMethodInsn(INVOKESPECIAL, com.alibaba.json.util.ASMUtils.type(TreeSet.class), "<init>", "()V");
        } else if (fieldClass.isAssignableFrom(LinkedHashSet.class)) {
            mw.visitTypeInsn(NEW, com.alibaba.json.util.ASMUtils.type(LinkedHashSet.class));
            mw.visitInsn(DUP);
            mw.visitMethodInsn(INVOKESPECIAL, com.alibaba.json.util.ASMUtils.type(LinkedHashSet.class), "<init>", "()V");
        } else if (set) {
            mw.visitTypeInsn(NEW, com.alibaba.json.util.ASMUtils.type(HashSet.class));
            mw.visitInsn(DUP);
            mw.visitMethodInsn(INVOKESPECIAL, com.alibaba.json.util.ASMUtils.type(HashSet.class), "<init>", "()V");
        } else {
            mw.visitVarInsn(ALOAD, 0);
            mw.visitLdcInsn(i);
            mw.visitMethodInsn(INVOKEVIRTUAL, com.alibaba.json.util.ASMUtils.type(JavaBeanDeserializer.class), "getFieldType",
                    "(I)Ljava/lang/reflect/Type;");
            mw.visitMethodInsn(INVOKESTATIC, com.alibaba.json.util.ASMUtils.type(TypeUtils.class), "createCollection",
                    "(Ljava/lang/reflect/Type;)Ljava/util/Collection;");
        }
        mw.visitTypeInsn(CHECKCAST, com.alibaba.json.util.ASMUtils.type(fieldClass)); // cast
    }

    private void _deserialze_obj(Context context, com.alibaba.json.asm.MethodVisitor mw, com.alibaba.json.asm.Label reset_, com.alibaba.json.util.FieldInfo fieldInfo,
                                 Class<?> fieldClass, int i) {
        com.alibaba.json.asm.Label matched_ = new com.alibaba.json.asm.Label();
        com.alibaba.json.asm.Label _end_if = new com.alibaba.json.asm.Label();

        mw.visitVarInsn(ALOAD, context.var("lexer"));
        mw.visitVarInsn(ALOAD, 0);
        mw.visitFieldInsn(GETFIELD, context.className, context.fieldName(fieldInfo), "[C");
        mw.visitMethodInsn(INVOKEVIRTUAL, JSONLexerBase, "matchField", "([C)Z");
        mw.visitJumpInsn(IFNE, matched_);
        mw.visitInsn(ACONST_NULL);
        mw.visitVarInsn(ASTORE, context.var_asm(fieldInfo));

        mw.visitJumpInsn(GOTO, _end_if);

        mw.visitLabel(matched_);

        _setFlag(mw, context, i);

        // increment matchedCount
        mw.visitVarInsn(ILOAD, context.var("matchedCount"));
        mw.visitInsn(ICONST_1);
        mw.visitInsn(IADD);
        mw.visitVarInsn(ISTORE, context.var("matchedCount"));

        _deserObject(context, mw, fieldInfo, fieldClass, i);

        mw.visitVarInsn(ALOAD, 1);
        mw.visitMethodInsn(INVOKEVIRTUAL, DefaultJSONParser, "getResolveStatus", "()I");
        mw.visitLdcInsn(com.alibaba.json.parser.DefaultJSONParser.NeedToResolve);
        mw.visitJumpInsn(IF_ICMPNE, _end_if);

        mw.visitVarInsn(ALOAD, 1);
        mw.visitMethodInsn(INVOKEVIRTUAL, DefaultJSONParser, "getLastResolveTask", "()" + com.alibaba.json.util.ASMUtils.desc(ResolveTask.class));
        mw.visitVarInsn(ASTORE, context.var("resolveTask"));

        mw.visitVarInsn(ALOAD, context.var("resolveTask"));
        mw.visitVarInsn(ALOAD, 1);
        mw.visitMethodInsn(INVOKEVIRTUAL, DefaultJSONParser, "getContext", "()" + com.alibaba.json.util.ASMUtils.desc(ParseContext.class));
        mw.visitFieldInsn(PUTFIELD, com.alibaba.json.util.ASMUtils.type(ResolveTask.class), "ownerContext", com.alibaba.json.util.ASMUtils.desc(ParseContext.class));

        mw.visitVarInsn(ALOAD, context.var("resolveTask"));
        mw.visitVarInsn(ALOAD, 0);
        mw.visitLdcInsn(fieldInfo.name);
        mw.visitMethodInsn(INVOKEVIRTUAL, com.alibaba.json.util.ASMUtils.type(JavaBeanDeserializer.class), "getFieldDeserializer",
                "(Ljava/lang/String;)" + com.alibaba.json.util.ASMUtils.desc(FieldDeserializer.class));
        mw.visitFieldInsn(PUTFIELD, com.alibaba.json.util.ASMUtils.type(ResolveTask.class), "fieldDeserializer", com.alibaba.json.util.ASMUtils.desc(FieldDeserializer.class));

        mw.visitVarInsn(ALOAD, 1);
        mw.visitLdcInsn(com.alibaba.json.parser.DefaultJSONParser.NONE);
        mw.visitMethodInsn(INVOKEVIRTUAL, DefaultJSONParser, "setResolveStatus", "(I)V");

        mw.visitLabel(_end_if);

    }

    private void _deserObject(Context context, com.alibaba.json.asm.MethodVisitor mw, com.alibaba.json.util.FieldInfo fieldInfo, Class<?> fieldClass, int i) {
        _getFieldDeser(context, mw, fieldInfo);

        com.alibaba.json.asm.Label instanceOfElse_ = new com.alibaba.json.asm.Label(), instanceOfEnd_ = new com.alibaba.json.asm.Label();
        if ((fieldInfo.parserFeatures & Feature.SupportArrayToBean.mask) != 0) {
            mw.visitInsn(DUP);
            mw.visitTypeInsn(INSTANCEOF, com.alibaba.json.util.ASMUtils.type(JavaBeanDeserializer.class));
            mw.visitJumpInsn(IFEQ, instanceOfElse_);

            mw.visitTypeInsn(CHECKCAST, com.alibaba.json.util.ASMUtils.type(JavaBeanDeserializer.class)); // cast
            mw.visitVarInsn(ALOAD, 1);
            if (fieldInfo.fieldType instanceof Class) {
                mw.visitLdcInsn(com.alibaba.json.asm.Type.getType(com.alibaba.json.util.ASMUtils.desc(fieldInfo.fieldClass)));
            } else {
                mw.visitVarInsn(ALOAD, 0);
                mw.visitLdcInsn(i);
                mw.visitMethodInsn(INVOKEVIRTUAL, com.alibaba.json.util.ASMUtils.type(JavaBeanDeserializer.class), "getFieldType",
                        "(I)Ljava/lang/reflect/Type;");
            }
            mw.visitLdcInsn(fieldInfo.name);
            mw.visitLdcInsn(fieldInfo.parserFeatures);
            mw.visitMethodInsn(INVOKEVIRTUAL, com.alibaba.json.util.ASMUtils.type(JavaBeanDeserializer.class), "deserialze",
                    "(L" + DefaultJSONParser + ";Ljava/lang/reflect/Type;Ljava/lang/Object;I)Ljava/lang/Object;");
            mw.visitTypeInsn(CHECKCAST, com.alibaba.json.util.ASMUtils.type(fieldClass)); // cast
            mw.visitVarInsn(ASTORE, context.var_asm(fieldInfo));
            
            mw.visitJumpInsn(GOTO, instanceOfEnd_);
            
            mw.visitLabel(instanceOfElse_);
        }

        mw.visitVarInsn(ALOAD, 1);
        if (fieldInfo.fieldType instanceof Class) {
            mw.visitLdcInsn(com.alibaba.json.asm.Type.getType(com.alibaba.json.util.ASMUtils.desc(fieldInfo.fieldClass)));
        } else {
            mw.visitVarInsn(ALOAD, 0);
            mw.visitLdcInsn(i);
            mw.visitMethodInsn(INVOKEVIRTUAL, com.alibaba.json.util.ASMUtils.type(JavaBeanDeserializer.class), "getFieldType",
                    "(I)Ljava/lang/reflect/Type;");
        }
        mw.visitLdcInsn(fieldInfo.name);
        mw.visitMethodInsn(INVOKEINTERFACE, com.alibaba.json.util.ASMUtils.type(ObjectDeserializer.class), "deserialze",
                "(L" + DefaultJSONParser + ";Ljava/lang/reflect/Type;Ljava/lang/Object;)Ljava/lang/Object;");
        mw.visitTypeInsn(CHECKCAST, com.alibaba.json.util.ASMUtils.type(fieldClass)); // cast
        mw.visitVarInsn(ASTORE, context.var_asm(fieldInfo));

        mw.visitLabel(instanceOfEnd_);
    }

    private void _getFieldDeser(Context context, com.alibaba.json.asm.MethodVisitor mw, com.alibaba.json.util.FieldInfo fieldInfo) {
        com.alibaba.json.asm.Label notNull_ = new Label();
        mw.visitVarInsn(ALOAD, 0);
        mw.visitFieldInsn(GETFIELD, context.className, context.fieldDeserName(fieldInfo), com.alibaba.json.util.ASMUtils.desc(ObjectDeserializer.class));
        mw.visitJumpInsn(IFNONNULL, notNull_);

        mw.visitVarInsn(ALOAD, 0);

        mw.visitVarInsn(ALOAD, 1);
        mw.visitMethodInsn(INVOKEVIRTUAL, DefaultJSONParser, "getConfig", "()" + com.alibaba.json.util.ASMUtils.desc(ParserConfig.class));
        mw.visitLdcInsn(com.alibaba.json.asm.Type.getType(com.alibaba.json.util.ASMUtils.desc(fieldInfo.fieldClass)));
        mw.visitMethodInsn(INVOKEVIRTUAL, com.alibaba.json.util.ASMUtils.type(ParserConfig.class), "getDeserializer",
                "(Ljava/lang/reflect/Type;)" + com.alibaba.json.util.ASMUtils.desc(ObjectDeserializer.class));

        mw.visitFieldInsn(PUTFIELD, context.className, context.fieldDeserName(fieldInfo), com.alibaba.json.util.ASMUtils.desc(ObjectDeserializer.class));

        mw.visitLabel(notNull_);

        mw.visitVarInsn(ALOAD, 0);
        mw.visitFieldInsn(GETFIELD, context.className, context.fieldDeserName(fieldInfo), com.alibaba.json.util.ASMUtils.desc(ObjectDeserializer.class));
    }

    private void _init(com.alibaba.json.asm.ClassWriter cw, Context context) {
        for (int i = 0, size = context.fieldInfoList.length; i < size; ++i) {
            com.alibaba.json.util.FieldInfo fieldInfo = context.fieldInfoList[i];

            com.alibaba.json.asm.FieldWriter fw = new com.alibaba.json.asm.FieldWriter(cw, ACC_PUBLIC, context.fieldName(fieldInfo), "[C");
            fw.visitEnd();
        }

        for (int i = 0, size = context.fieldInfoList.length; i < size; ++i) {
            com.alibaba.json.util.FieldInfo fieldInfo = context.fieldInfoList[i];
            Class<?> fieldClass = fieldInfo.fieldClass;

            if (fieldClass.isPrimitive()) {
                continue;
            }

            if (Collection.class.isAssignableFrom(fieldClass)) {
                com.alibaba.json.asm.FieldWriter fw = new com.alibaba.json.asm.FieldWriter(cw, ACC_PUBLIC, fieldInfo.name + "_asm_list_item_deser__",
                        com.alibaba.json.util.ASMUtils.desc(ObjectDeserializer.class));
                fw.visitEnd();
            } else {
                com.alibaba.json.asm.FieldWriter fw = new FieldWriter(cw, ACC_PUBLIC, context.fieldDeserName(fieldInfo),
                        com.alibaba.json.util.ASMUtils.desc(ObjectDeserializer.class));
                fw.visitEnd();
            }
        }

        com.alibaba.json.asm.MethodVisitor mw = new com.alibaba.json.asm.MethodWriter(cw, ACC_PUBLIC, "<init>",
                "(" + com.alibaba.json.util.ASMUtils.desc(ParserConfig.class) + com.alibaba.json.util.ASMUtils.desc(com.alibaba.json.util.JavaBeanInfo.class) + ")V", null, null);
        mw.visitVarInsn(ALOAD, 0);
        mw.visitVarInsn(ALOAD, 1);
        mw.visitVarInsn(ALOAD, 2);
        mw.visitMethodInsn(INVOKESPECIAL, com.alibaba.json.util.ASMUtils.type(JavaBeanDeserializer.class), "<init>",
                "(" + com.alibaba.json.util.ASMUtils.desc(ParserConfig.class) + com.alibaba.json.util.ASMUtils.desc(com.alibaba.json.util.JavaBeanInfo.class) + ")V");

        // init fieldNamePrefix
        for (int i = 0, size = context.fieldInfoList.length; i < size; ++i) {
            com.alibaba.json.util.FieldInfo fieldInfo = context.fieldInfoList[i];

            mw.visitVarInsn(ALOAD, 0);
            mw.visitLdcInsn("\"" + fieldInfo.name + "\":"); // public char[] toCharArray()
            mw.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "toCharArray", "()[C");
            mw.visitFieldInsn(PUTFIELD, context.className, context.fieldName(fieldInfo), "[C");

        }

        mw.visitInsn(RETURN);
        mw.visitMaxs(4, 4);
        mw.visitEnd();
    }

    private void _createInstance(ClassWriter cw, Context context) {
        Constructor<?> defaultConstructor = context.beanInfo.defaultConstructor;
        if (!Modifier.isPublic(defaultConstructor.getModifiers())) {
            return;
        }

        MethodVisitor mw = new MethodWriter(cw, ACC_PUBLIC, "createInstance",
                                            "(L" + DefaultJSONParser + ";Ljava/lang/reflect/Type;)Ljava/lang/Object;",
                                            null, null);

        mw.visitTypeInsn(NEW, com.alibaba.json.util.ASMUtils.type(context.getInstClass()));
        mw.visitInsn(DUP);
        mw.visitMethodInsn(INVOKESPECIAL, com.alibaba.json.util.ASMUtils.type(context.getInstClass()), "<init>", "()V");

        mw.visitInsn(ARETURN);
        mw.visitMaxs(3, 3);
        mw.visitEnd();
    }

    static class Context {

        static final int                   parser       = 1;
        static final int                   type         = 2;
        static final int                   fieldName    = 3;

        private int                        variantIndex = -1;
        private final Map<String, Integer> variants     = new HashMap<String, Integer>();

        private final Class<?> clazz;
        private final com.alibaba.json.util.JavaBeanInfo beanInfo;
        private final String className;
        private com.alibaba.json.util.FieldInfo[] fieldInfoList;

        public Context(String className, ParserConfig config, com.alibaba.json.util.JavaBeanInfo beanInfo, int initVariantIndex) {
            this.className = className;
            this.clazz = beanInfo.clazz;
            this.variantIndex = initVariantIndex;
            this.beanInfo = beanInfo;
            fieldInfoList = beanInfo.fields;
        }

        public Class<?> getInstClass() {
            Class<?> instClass = beanInfo.builderClass;
            if (instClass == null) {
                instClass = clazz;
            }

            return instClass;
        }

        public int var(String name, int increment) {
            Integer i = variants.get(name);
            if (i == null) {
                variants.put(name, variantIndex);
                variantIndex += increment;
            }
            i = variants.get(name);
            return i.intValue();
        }

        public int var(String name) {
            Integer i = variants.get(name);
            if (i == null) {
                variants.put(name, variantIndex++);
            }
            i = variants.get(name);
            return i.intValue();
        }

        public int var_asm(com.alibaba.json.util.FieldInfo fieldInfo) {
            return var(fieldInfo.name + "_asm");
        }

        public int var_asm(com.alibaba.json.util.FieldInfo fieldInfo, int increment) {
            return var(fieldInfo.name + "_asm", increment);
        }

        public String fieldName(com.alibaba.json.util.FieldInfo fieldInfo) {
            return validIdent(fieldInfo.name)
                    ? fieldInfo.name + "_asm_prefix__"
                    : "asm_field_" + TypeUtils.fnv1a_64_extract(fieldInfo.name);
        }


        public String fieldDeserName(com.alibaba.json.util.FieldInfo fieldInfo) {
            return validIdent(fieldInfo.name)
                    ? fieldInfo.name + "_asm_deser__"
                    : "_asm_deser__" + TypeUtils.fnv1a_64_extract(fieldInfo.name);
        }


        boolean validIdent(String name) {
            for (int i = 0; i < name.length(); ++i) {
                char ch = name.charAt(i);
                if (ch == 0) {
                    if (!com.alibaba.json.util.IOUtils.firstIdentifier(ch)) {
                        return false;
                    }
                } else {
                    if (!com.alibaba.json.util.IOUtils.isIdent(ch)) {
                        return false;
                    }
                }
            }

            return true;
        }
    }

}
