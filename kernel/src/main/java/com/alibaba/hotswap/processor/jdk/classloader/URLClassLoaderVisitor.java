/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.processor.jdk.classloader;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import com.alibaba.hotswap.processor.basic.BaseClassVisitor;
import com.alibaba.hotswap.processor.jdk.classloader.modifier.FindClassMethodModifier;

/**
 * @author zhuyong 2012-6-18
 */
public class URLClassLoaderVisitor extends BaseClassVisitor {

    public URLClassLoaderVisitor(ClassVisitor cv){
        super(cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

        if (name.equals("findClass") && desc.equals("(Ljava/lang/String;)Ljava/lang/Class;")) {
            return new FindClassMethodModifier(mv);
        }

        return mv;
    }
}
