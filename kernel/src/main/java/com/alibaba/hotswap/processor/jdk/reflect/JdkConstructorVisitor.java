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
import com.alibaba.hotswap.processor.jdk.reflect.modifier.DeclaredAnnotationsModifier;

/**
 * @author zhuyong 2012-7-6
 */
public class JdkConstructorVisitor extends BaseClassVisitor {

    public JdkConstructorVisitor(ClassVisitor cv){
        super(cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        // if (name.equals("newInstance") && desc.equals("([Ljava/lang/Object;)Ljava/lang/Object;")) {
        // return new ConstructorNewInstanceModifier(mv, access, name, desc);
        // }

        if (name.equals("declaredAnnotations") && desc.equals("()Ljava/util/Map;")) {
            return new DeclaredAnnotationsModifier(mv, access, name, desc);
        }

        return mv;
    }
}
