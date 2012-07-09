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

import com.alibaba.hotswap.constant.HotswapConstants;
import com.alibaba.hotswap.processor.basic.BaseMethodAdapter;
import com.alibaba.hotswap.runtime.HotswapRuntime;
import com.alibaba.hotswap.util.HotswapMethodUtil;

/**
 * @author zhuyong 2012-7-3
 */
public class ClassNewInstanceModifier extends BaseMethodAdapter {

    public ClassNewInstanceModifier(MethodVisitor mv, int access, String name, String desc){
        super(mv, access, name, desc);
    }

    @Override
    public void visitCode() {
        super.visitCode();

        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getName", "()Ljava/lang/String;");
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(HotswapRuntime.class), "hasClassMeta",
                           "(Ljava/lang/String;)Z");
        Label old = new Label();
        mv.visitJumpInsn(Opcodes.IFEQ, old);

        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(HotswapMethodUtil.class),
                           "getConstructorParamTypes", "()[Ljava/lang/Class;");
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getConstructor",
                           "([Ljava/lang/Class;)Ljava/lang/reflect/Constructor;");

        // index
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Class", "getName", "()Ljava/lang/String;");
        mv.visitLdcInsn(HotswapConstants.INIT);
        mv.visitLdcInsn("()V");
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(HotswapMethodUtil.class), "getMethodParams",
                           "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)[Ljava/lang/Object;");

        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/reflect/Constructor", "newInstance",
                           "([Ljava/lang/Object;)Ljava/lang/Object;");
        mv.visitInsn(Opcodes.ARETURN);

        mv.visitLabel(old);
    }
}
