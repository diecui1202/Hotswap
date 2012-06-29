/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.processor.clinit;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.alibaba.hotswap.constant.HotswapConstants;
import com.alibaba.hotswap.processor.basic.BaseMethodAdapter;
import com.alibaba.hotswap.runtime.HotswapRuntime;

/**
 * Insert __hotswap_static_field_holder__ initialize code at the beginning of clinit method. Set this class has beed
 * initialized.
 * 
 * @author yong.zhuy 2012-5-24 12:23:22
 */
public class ClinitModifier extends BaseMethodAdapter {

    private boolean isInterface = false;

    public ClinitModifier(MethodVisitor mv, int access, String name, String desc, String className, boolean isInterface){
        super(mv, access, name, desc, className);
        this.isInterface = isInterface;
    }

    @Override
    public void visitCode() {
        super.visitCode();
        if (!isInterface) {
            // initial __hotswap_static_field_holder__
            mv.visitTypeInsn(Opcodes.NEW, "java/util/concurrent/ConcurrentHashMap");
            mv.visitInsn(Opcodes.DUP);
            mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/util/concurrent/ConcurrentHashMap", "<init>", "()V");
            mv.visitFieldInsn(Opcodes.PUTSTATIC, className, HotswapConstants.STATIC_FIELD_HOLDER,
                              "Ljava/util/concurrent/ConcurrentHashMap;");
        }
    }

    @Override
    public void visitInsn(int opcode) {
        if (opcode == Opcodes.RETURN) {

            mv.visitLdcInsn(className);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(HotswapRuntime.class), "setClassInitialized",
                               "(Ljava/lang/String;)V");
        }
        super.visitInsn(opcode);
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
    }
}
