/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.processor.jdk.classloader;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.alibaba.hotswap.constant.HotswapConstants;
import com.alibaba.hotswap.runtime.HotswapRuntime;
import com.sun.xml.internal.ws.org.objectweb.asm.Type;

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

        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitLdcInsn(HotswapConstants.V_CLASS_PATTERN);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "indexOf", "(Ljava/lang/String;)I");
        mv.visitInsn(Opcodes.DUP);
        mv.visitJumpInsn(Opcodes.IFLT, normal);
        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitInsn(Opcodes.SWAP);
        mv.visitLdcInsn(0);
        mv.visitInsn(Opcodes.SWAP);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/String", "substring", "(II)Ljava/lang/String;");
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(HotswapRuntime.class), "getVClassBytes",
                           "(Ljava/lang/String;)[B");
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitInsn(Opcodes.SWAP);// this, [B
        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitInsn(Opcodes.SWAP);// this, name, [B
        mv.visitInsn(Opcodes.DUP);// this, name, [B, [B
        mv.visitInsn(Opcodes.ARRAYLENGTH);// this, name, [B, len
        mv.visitLdcInsn(0);
        mv.visitInsn(Opcodes.SWAP);// this, name, [B, 0, len
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/ClassLoader", "defineClass",
                           "(Ljava/lang/String;[BII)Ljava/lang/Class;");
        mv.visitInsn(Opcodes.ARETURN);

        mv.visitLabel(normal);
        mv.visitInsn(Opcodes.POP);
    }
}
