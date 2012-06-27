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
import org.objectweb.asm.commons.Method;

import com.alibaba.hotswap.processor.basic.BaseMethodAdapter;
import com.alibaba.hotswap.processor.jdk.reflect.ReflectFieldHelper;

/**
 * @author zhuyong 2012-6-27
 */
public class SetXModifier extends BaseMethodAdapter {

    public SetXModifier(MethodVisitor mv, int access, String name, String desc){
        super(mv, access, name, desc);
    }

    @Override
    public void visitCode() {
        super.visitCode();

        loadArg(0);
        loadThis();
        invokeStatic(Type.getType(ReflectFieldHelper.class),
                     new Method("isInHotswapFieldHolder", "(Ljava/lang/Object;Ljava/lang/reflect/Field;)Z"));
        Label old = newLabel();
        ifZCmp(EQ, old);

        loadArg(0);
        loadThis();
        loadArg(1);
        box(Type.getType(Object.class));

        invokeStatic(Type.getType(ReflectFieldHelper.class),
                     new Method("setHotswapFieldHolderValue",
                                "(Ljava/lang/Object;Ljava/lang/reflect/Field;Ljava/lang/Object;)V"));

        mv.visitInsn(Opcodes.RETURN);
        mark(old);
    }
}
