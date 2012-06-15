/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.processor.loader;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * The classloader interceptor for custom loading
 * 
 * @author yong.zhuy 2012-5-18 13:14:22
 */
public class ClassLoaderVisitor extends ClassVisitor {

    public ClassLoaderVisitor(ClassVisitor cv){
        super(Opcodes.ASM4, cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

        if (name.equals("checkCerts")) {
            mv.visitCode();
            mv.visitInsn(Opcodes.RETURN);
            mv.visitMaxs(0, 0);
            mv.visitEnd();
            return null;
        }

        if (name.equals("defineClass")
            && desc.equals("(Ljava/lang/String;[BIILjava/security/ProtectionDomain;)Ljava/lang/Class;")) {
            return new DefineClassMethodModifier(mv);
        }

        return mv;
    }
}
