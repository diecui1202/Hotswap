/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.processor.jdk.lang.modifier;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.alibaba.hotswap.processor.basic.BaseMethodAdapter;
import com.alibaba.hotswap.processor.jdk.helper.MethodReflectHelper;
import com.alibaba.hotswap.runtime.HotswapRuntime;

/**
 * @author zhuyong 2012-7-4
 */
public class PrivateGetDeclaredConstructors extends BaseMethodAdapter {

    public PrivateGetDeclaredConstructors(MethodVisitor mv, int access, String name, String desc){
        super(mv, access, name, desc);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc) {

        if (opcode == Opcodes.INVOKESPECIAL && owner.equals("java/lang/Class")
            && name.equals("getDeclaredConstructors0") && desc.equals("(Z)[Ljava/lang/reflect/Constructor;")) {
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getName", "()Ljava/lang/String;");
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(HotswapRuntime.class), "hasClassMeta",
                               "(Ljava/lang/String;)Z");
            Label old = new Label();
            mv.visitJumpInsn(Opcodes.IFEQ, old);

            mv.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(MethodReflectHelper.class),
                               "getDeclaredConstructors0", "(Ljava/lang/Class;Z)[Ljava/lang/reflect/Constructor;");
            Label end = new Label();
            mv.visitJumpInsn(Opcodes.GOTO, end);
            mv.visitLabel(old);
            super.visitMethodInsn(opcode, owner, name, desc);
            mv.visitLabel(end);
        } else {
            super.visitMethodInsn(opcode, owner, name, desc);
        }
    }
}
