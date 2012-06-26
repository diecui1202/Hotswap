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
import org.objectweb.asm.commons.GeneratorAdapter;

import com.alibaba.hotswap.configuration.HotswapConfiguration;
import com.alibaba.hotswap.loader.CustomerLoadClassBytes;
import com.alibaba.hotswap.runtime.HotswapRuntime;

/**
 * @author yong.zhuy 2012-5-21 15:41:49
 */
public class DefineClassMethodModifier extends GeneratorAdapter {

    public DefineClassMethodModifier(MethodVisitor mv, int access, String name, String desc){
        super(Opcodes.ASM4, mv, access, name, desc);
    }

    @Override
    public void visitCode() {
        super.visitCode();

        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(HotswapConfiguration.class),
                           "getClassPathInWorkspace", "(Ljava/lang/String;)Ljava/lang/String;");
        mv.visitInsn(Opcodes.DUP);
        Label old = new Label();
        mv.visitJumpInsn(Opcodes.IFNULL, old);

        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(HotswapRuntime.class), "updateClassMeta",
                           "(Ljava/lang/String;Ljava/lang/ClassLoader;)V");

        mv.visitVarInsn(Opcodes.ALOAD, 1); // className
        mv.visitInsn(Opcodes.SWAP);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(CustomerLoadClassBytes.class),
                           "loadBytesFromPath", "(Ljava/lang/String;Ljava/lang/String;)[B");
        mv.visitVarInsn(Opcodes.ASTORE, 2);// store class bytes into 2
        mv.visitVarInsn(Opcodes.ALOAD, 2);// load class bytes
        mv.visitInsn(Opcodes.ARRAYLENGTH); // length of the class bytes
        mv.visitVarInsn(Opcodes.ISTORE, 4);// store length into 4

        Label end = new Label();
        mv.visitJumpInsn(Opcodes.GOTO, end);

        mv.visitLabel(old);
        mv.visitInsn(Opcodes.POP);

        mv.visitLabel(end);
    }

    @Override
    public void visitInsn(int opcode) {
        if (opcode == Opcodes.ARETURN) {
            mv.visitInsn(Opcodes.DUP);
            mv.visitVarInsn(Opcodes.ALOAD, 1);
            mv.visitInsn(Opcodes.SWAP);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(HotswapRuntime.class),
                               "updateClassMetaClass", "(Ljava/lang/String;Ljava/lang/Class;)V");
        }

        super.visitInsn(opcode);
    }
}
