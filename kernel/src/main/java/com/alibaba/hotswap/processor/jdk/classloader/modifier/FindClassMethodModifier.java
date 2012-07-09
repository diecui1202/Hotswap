/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.processor.jdk.classloader.modifier;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.alibaba.hotswap.processor.jdk.helper.ClassLoaderHelper;

/**
 * @author zhuyong 2012-6-18
 */
public class FindClassMethodModifier extends MethodVisitor {

    public FindClassMethodModifier(MethodVisitor mv){
        super(Opcodes.ASM4, mv);
    }

    @Override
    public void visitCode() {
        super.visitCode();

        Label normal = new Label();

        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(ClassLoaderHelper.class), "tryLoadVClass",
                           "(Ljava/lang/ClassLoader;Ljava/lang/String;)Ljava/lang/Class;");
        mv.visitInsn(Opcodes.DUP);
        mv.visitJumpInsn(Opcodes.IFNULL, normal);
        mv.visitInsn(Opcodes.ARETURN);

        mv.visitLabel(normal);
        mv.visitInsn(Opcodes.POP);
    }
}
