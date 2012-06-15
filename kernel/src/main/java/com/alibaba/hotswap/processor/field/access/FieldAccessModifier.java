/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.processor.field.access;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.alibaba.hotswap.constant.HotswapConstants;
import com.alibaba.hotswap.meta.FieldMeta;
import com.alibaba.hotswap.processor.basic.BaseMethodAdapter;
import com.alibaba.hotswap.runtime.HotswapRuntime;
import com.alibaba.hotswap.util.HotswapFieldUtil;

/**
 * @author yong.zhuy 2012-6-15
 */
public class FieldAccessModifier extends BaseMethodAdapter {

    public FieldAccessModifier(MethodVisitor mv, int access, String name, String desc, String className){
        super(mv, access, name, desc, className);
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
        if (HotswapRuntime.getClassInitialized(className) && HotswapRuntime.hasClassMeta(owner)
            && !name.equals(HotswapConstants.STATIC_FIELD_HOLDER) && !name.equals(HotswapConstants.FIELD_HOLDER)) {
            if (opcode == Opcodes.PUTSTATIC) {
                // put static
                FieldMeta fm = HotswapRuntime.getClassMeta(owner).getFieldMeta(HotswapFieldUtil.getFieldKey(name, desc));
                if (fm == null || fm.isAdded()) {
                    box(Type.getType(desc));
                    mv.visitFieldInsn(Opcodes.GETSTATIC, owner, HotswapConstants.STATIC_FIELD_HOLDER,
                                      "Ljava/util/concurrent/ConcurrentHashMap;");
                    mv.visitLdcInsn(name);
                    mv.visitLdcInsn(desc);
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(HotswapFieldUtil.class),
                                       "setFieldValue",
                                       "(Ljava/lang/Object;Ljava/util/concurrent/ConcurrentHashMap;Ljava/lang/String;Ljava/lang/String;)V");
                    return;
                }
            } else if (opcode == Opcodes.GETSTATIC) {
                // get static
                FieldMeta fm = HotswapRuntime.getClassMeta(owner).getFieldMeta(HotswapFieldUtil.getFieldKey(name, desc));
                if (fm == null || fm.isAdded()) {
                    mv.visitFieldInsn(Opcodes.GETSTATIC, owner, HotswapConstants.STATIC_FIELD_HOLDER,
                                      "Ljava/util/concurrent/ConcurrentHashMap;");
                    mv.visitLdcInsn(name);
                    mv.visitLdcInsn(desc);
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(HotswapFieldUtil.class),
                                       "getFieldValue",
                                       "(Ljava/util/concurrent/ConcurrentHashMap;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/Object;");
                    unbox(Type.getType(desc));
                    return;
                }
            } else if (opcode == Opcodes.PUTFIELD) {
                // put field
                FieldMeta fm = HotswapRuntime.getClassMeta(owner).getFieldMeta(HotswapFieldUtil.getFieldKey(name, desc));
                if (fm == null || fm.isAdded()) {
                    // stack: obj fieldValue
                    box(Type.getType(desc));
                    mv.visitInsn(Opcodes.SWAP);
                    mv.visitFieldInsn(Opcodes.GETFIELD, owner, HotswapConstants.FIELD_HOLDER,
                                      "Ljava/util/concurrent/ConcurrentHashMap;");
                    mv.visitLdcInsn(name);
                    mv.visitLdcInsn(desc);
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(HotswapFieldUtil.class),
                                       "setFieldValue",
                                       "(Ljava/lang/Object;Ljava/util/concurrent/ConcurrentHashMap;Ljava/lang/String;Ljava/lang/String;)V");

                    return;
                }
            } else if (opcode == Opcodes.GETFIELD) {
                // get field
                FieldMeta fm = HotswapRuntime.getClassMeta(owner).getFieldMeta(HotswapFieldUtil.getFieldKey(name, desc));
                if (fm == null || fm.isAdded()) {
                    // stack: obj
                    mv.visitFieldInsn(Opcodes.GETFIELD, owner, HotswapConstants.FIELD_HOLDER,
                                      "Ljava/util/concurrent/ConcurrentHashMap;");
                    mv.visitLdcInsn(name);
                    mv.visitLdcInsn(desc);
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(HotswapFieldUtil.class),
                                       "getFieldValue",
                                       "(Ljava/util/concurrent/ConcurrentHashMap;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/Object;");

                    Label notnull = newLabel();
                    Type type = Type.getType(desc);

                    mv.visitInsn(Opcodes.DUP);
                    mv.visitJumpInsn(Opcodes.IFNONNULL, notnull);

                    Label end = newLabel();
                    switch (type.getSort()) {
                        case Type.BOOLEAN:
                            mv.visitInsn(Opcodes.POP);
                            mv.visitLdcInsn(new Boolean(false));
                            break;

                        case Type.CHAR:
                            mv.visitInsn(Opcodes.POP);
                            mv.visitLdcInsn(new Character(' '));
                            break;

                        case Type.BYTE:
                            mv.visitInsn(Opcodes.POP);
                            mv.visitLdcInsn(new Byte((byte) 0));
                            break;

                        case Type.SHORT:
                            mv.visitInsn(Opcodes.POP);
                            mv.visitLdcInsn(new Short((short) 0));
                            break;

                        case Type.INT:
                            mv.visitInsn(Opcodes.POP);
                            mv.visitLdcInsn(new Integer(0));
                            break;

                        case Type.FLOAT:
                            mv.visitInsn(Opcodes.POP);
                            mv.visitLdcInsn(new Float(0));
                            break;

                        case Type.LONG:
                            mv.visitInsn(Opcodes.POP);
                            mv.visitLdcInsn(new Long(0));
                            break;

                        case Type.DOUBLE:
                            mv.visitInsn(Opcodes.POP);
                            mv.visitLdcInsn(new Double(0));
                            break;

                        default:
                            break;
                    }
                    mv.visitJumpInsn(Opcodes.GOTO, end);

                    mark(notnull);
                    unbox(type);

                    mark(end);
                    return;
                }
            }
        }
        super.visitFieldInsn(opcode, owner, name, desc);
    }
}
