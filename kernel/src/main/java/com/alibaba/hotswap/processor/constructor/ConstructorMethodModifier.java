/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.processor.constructor;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.alibaba.hotswap.constant.HotswapConstants;
import com.alibaba.hotswap.meta.ClassMeta;
import com.alibaba.hotswap.processor.basic.BaseMethodAdapter;
import com.alibaba.hotswap.runtime.HotswapRuntime;

/**
 * @author yong.zhuy 2012-6-13
 */
public class ConstructorMethodModifier extends BaseMethodAdapter {

    public ConstructorMethodModifier(MethodVisitor mv, int access, String name, String desc, String className){
        super(mv, access, name, desc, className);
    }

    @Override
    public void visitCode() {
        super.visitCode();

        // this.__hotswap_field_holder__ = new ConcurrentHashMap();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitTypeInsn(Opcodes.NEW, "java/util/concurrent/ConcurrentHashMap");
        mv.visitInsn(Opcodes.DUP);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/util/concurrent/ConcurrentHashMap", "<init>", "()V");
        mv.visitFieldInsn(Opcodes.PUTFIELD, className, HotswapConstants.FIELD_HOLDER,
                          "Ljava/util/concurrent/ConcurrentHashMap;");
    }

    @Override
    public void visitInsn(int opcode) {
        if (opcode == Opcodes.RETURN) {
            // HotswapRuntime.getClassMeta(className).addNewInstance(this);
            mv.visitLdcInsn(className);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(HotswapRuntime.class), "getClassMeta",
                               "(Ljava/lang/String;)Lcom/alibaba/hotswap/meta/ClassMeta;");
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, Type.getInternalName(ClassMeta.class), "addNewInstance",
                               "(Ljava/lang/Object;)V");
        }
        super.visitInsn(opcode);
    }
}
