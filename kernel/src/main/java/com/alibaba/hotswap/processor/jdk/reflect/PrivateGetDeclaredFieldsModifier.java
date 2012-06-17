/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.processor.jdk.reflect;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.alibaba.hotswap.processor.basic.BaseMethodAdapter;

/**
 * @author zhuyong 2012-6-17 18:33:19
 */
public class PrivateGetDeclaredFieldsModifier extends BaseMethodAdapter {

    public PrivateGetDeclaredFieldsModifier(MethodVisitor mv, int access, String name, String desc, String className){
        super(mv, access, name, desc, className);
    }

    @Override
    public void visitInsn(int opcode) {
        if (opcode == Opcodes.ARETURN) {
            mv.visitVarInsn(Opcodes.ALOAD, 0);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(ReflectHelper.class), "filterReflectFields",
                               "([Ljava/lang/reflect/Field;Ljava/lang/Class;)[Ljava/lang/reflect/Field;");
        }
        super.visitInsn(opcode);
    }
}
