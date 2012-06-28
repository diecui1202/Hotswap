/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.processor.jdk.reflect.modifier;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.alibaba.hotswap.processor.basic.BaseMethodAdapter;
import com.alibaba.hotswap.processor.jdk.reflect.FieldReflectHelper;

/**
 * @author zhuyong 2012-6-28
 */
public class DeclaredAnnotationsModifier extends BaseMethodAdapter {

    public DeclaredAnnotationsModifier(MethodVisitor mv, int access, String name, String desc){
        super(mv, access, name, desc);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc) {
        if (opcode == Opcodes.INVOKEVIRTUAL && owner.equals("java/lang/reflect/Field")
            && name.equals("getDeclaringClass") && desc.equals("()Ljava/lang/Class;")) {
            super.visitMethodInsn(opcode, owner, name, desc);
            dup();
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(FieldReflectHelper.class),
                               "getVClassByClass", "(Ljava/lang/Class;)Ljava/lang/Class;");
            dup();
            Label old = newLabel();
            ifNull(old);
            swap();

            mark(old);
            pop();
            return;
        }
        super.visitMethodInsn(opcode, owner, name, desc);
    }
}
