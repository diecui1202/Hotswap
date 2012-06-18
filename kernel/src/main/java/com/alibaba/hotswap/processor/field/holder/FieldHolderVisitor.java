/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.processor.field.holder;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;

import com.alibaba.hotswap.constant.HotswapConstants;
import com.alibaba.hotswap.processor.basic.BaseClassVisitor;
import com.alibaba.hotswap.runtime.HotswapRuntime;

/**
 * Add <code>__$$hotswap_field_holder$$__</code> and <code>__$$hotswap_static_field_holder$$__</code>
 * 
 * @author yong.zhuy 2012-6-13
 */
public class FieldHolderVisitor extends BaseClassVisitor {

    public FieldHolderVisitor(ClassVisitor cv){
        super(cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);

        addFieldHolder();
        addStaticFieldHolder();
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        return null;
    }

    private void addFieldHolder() {
        FieldVisitor fv = cv.visitField(Opcodes.ACC_TRANSIENT + Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL,
                                        HotswapConstants.FIELD_HOLDER, "Ljava/util/concurrent/ConcurrentHashMap;",
                                        null, null);
        if (fv != null) {
            fv.visitEnd();
        }
    }

    private void addStaticFieldHolder() {
        FieldVisitor fv = cv.visitField(Opcodes.ACC_TRANSIENT + Opcodes.ACC_STATIC + Opcodes.ACC_PUBLIC
                                                + Opcodes.ACC_FINAL, HotswapConstants.STATIC_FIELD_HOLDER,
                                        "Ljava/util/concurrent/ConcurrentHashMap;", null, null);
        if (fv != null) {
            fv.visitEnd();
        }
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
    }
}
