/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.processor.field.holder;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import com.alibaba.hotswap.constant.HotswapConstants;
import com.alibaba.hotswap.processor.basic.BaseClassVisitor;

/**
 * @author yong.zhuy 2012-6-13
 */
public class FieldHolderInitVisitor extends BaseClassVisitor {

    public FieldHolderInitVisitor(ClassVisitor cv){
        super(cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if (name.equals(HotswapConstants.INIT)) {
            MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
            return new FieldHolderInitModifier(mv, access, name, desc, className);
        }
        return super.visitMethod(access, name, desc, signature, exceptions);
    }
}
