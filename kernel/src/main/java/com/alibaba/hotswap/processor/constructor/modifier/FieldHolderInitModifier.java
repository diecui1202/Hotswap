/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.processor.constructor.modifier;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.alibaba.hotswap.constant.HotswapConstants;
import com.alibaba.hotswap.processor.basic.BaseMethodAdapter;

/**
 * Add &lt;__$$hotswap_field_holder$$__&gt; initialize code
 * 
 * @author yong.zhuy 2012-6-13
 */
public class FieldHolderInitModifier extends BaseMethodAdapter {

    public FieldHolderInitModifier(MethodVisitor mv, int access, String name, String desc, String className){
        super(mv, access, name, desc, className);
    }

    @Override
    public void visitCode() {
        super.visitCode();

        // this.__$$hotswap_field_holder$$__ = new ConcurrentHashMap();
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitTypeInsn(Opcodes.NEW, "java/util/concurrent/ConcurrentHashMap");
        mv.visitInsn(Opcodes.DUP);
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/util/concurrent/ConcurrentHashMap", "<init>", "()V");
        mv.visitFieldInsn(Opcodes.PUTFIELD, className, HotswapConstants.FIELD_HOLDER,
                          "Ljava/util/concurrent/ConcurrentHashMap;");
    }
}
