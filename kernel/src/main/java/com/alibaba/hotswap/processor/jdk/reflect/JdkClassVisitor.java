/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.processor.jdk.reflect;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import com.alibaba.hotswap.processor.basic.BaseClassVisitor;
import com.alibaba.hotswap.processor.jdk.reflect.modifier.PrivateGetDeclaredFieldsModifier;

/**
 * @author zhuyong 2012-6-17 18:08:35
 */
public class JdkClassVisitor extends BaseClassVisitor {

    public JdkClassVisitor(ClassVisitor cv){
        super(cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if (name.equals("privateGetDeclaredFields")) {
            return new PrivateGetDeclaredFieldsModifier(mv, access, name, desc, className);
        }

        return mv;
    }
}
