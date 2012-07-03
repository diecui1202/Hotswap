/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.processor.constructor.modifier;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * @author zhuyong 2012-7-2
 */
public class ConstructorLVTAdjustModifier extends MethodVisitor {

    private int delta;

    public ConstructorLVTAdjustModifier(MethodVisitor mv, int delta){
        super(Opcodes.ASM4, mv);
        this.delta = delta;
    }

    @Override
    public void visitVarInsn(int opcode, int var) {
        super.visitVarInsn(opcode, var == 0 ? var : delta + var);
    }

    @Override
    public void visitIincInsn(int var, int increment) {
        super.visitIincInsn(var == 0 ? var : delta + var, increment);
    }

    @Override
    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
        super.visitLocalVariable(name, desc, signature, start, end, index == 0 ? index : delta + index);
    }
}
