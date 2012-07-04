/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.processor.jdk.lang.modifier;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.alibaba.hotswap.processor.basic.BaseMethodAdapter;
import com.alibaba.hotswap.processor.jdk.helper.MethodReflectHelper;

/**
 * @author zhuyong 2012-7-4
 */
public class GetXConstructorsFilterModifier extends BaseMethodAdapter {

    public GetXConstructorsFilterModifier(MethodVisitor mv, int access, String name, String desc){
        super(mv, access, name, desc);
    }

    @Override
    public void visitInsn(int opcode) {
        if (opcode == Opcodes.ARETURN) {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(MethodReflectHelper.class),
                               "filterHotswapConstructor",
                               "([Ljava/lang/reflect/Constructor;)[Ljava/lang/reflect/Constructor;");
        }
        super.visitInsn(opcode);
    }
}
