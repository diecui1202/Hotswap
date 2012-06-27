/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.processor.jdk.reflect.modifier;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.alibaba.hotswap.processor.basic.BaseMethodAdapter;
import com.alibaba.hotswap.processor.jdk.reflect.ReflectFieldHelper;

/**
 * Filter hotswap field holder
 * 
 * @author zhuyong 2012-6-27
 */
public class GetDeclaredFieldsModifier extends BaseMethodAdapter {

    public GetDeclaredFieldsModifier(MethodVisitor mv, int access, String name, String desc, String className){
        super(mv, access, name, desc, className);
    }

    @Override
    public void visitInsn(int opcode) {
        if (opcode == Opcodes.ARETURN) {
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(ReflectFieldHelper.class),
                               "filterHotswapFields", "([Ljava/lang/reflect/Field;)[Ljava/lang/reflect/Field;");
        }
        super.visitInsn(opcode);
    }
}
