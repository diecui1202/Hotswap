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

import com.alibaba.hotswap.processor.basic.BaseMethodAdapter;

/**
 * @author zhuyong 2012-6-26
 */
public class GetXXXModifier extends BaseMethodAdapter {

    public GetXXXModifier(MethodVisitor mv, int access, String name, String desc){
        super(mv, access, name, desc);
    }

    @Override
    public void visitCode() {
        super.visitCode();

        mv.visitVarInsn(Opcodes.ALOAD, 1);
    }
}
