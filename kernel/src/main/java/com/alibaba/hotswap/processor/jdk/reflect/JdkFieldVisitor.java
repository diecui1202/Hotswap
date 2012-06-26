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
import com.alibaba.hotswap.processor.jdk.reflect.modifier.GetXXXModifier;

/**
 * Add Field get[X]?() support
 * 
 * @author zhuyong 2012-6-26
 */
public class JdkFieldVisitor extends BaseClassVisitor {

    public JdkFieldVisitor(ClassVisitor cv){
        super(cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

        if ((name.equals("get") && desc.equals("(Ljava/lang/Object;)Ljava/lang/Object;"))
            || (name.equals("getBoolean") && desc.equals("(Ljava/lang/Object;)Z"))
            || (name.equals("getByte") && desc.equals("(Ljava/lang/Object;)B"))
            || (name.equals("getChar") && desc.equals("(Ljava/lang/Object;)C"))
            || (name.equals("getShort") && desc.equals("(Ljava/lang/Object;)S"))
            || (name.equals("getInt") && desc.equals("(Ljava/lang/Object;)I"))
            || (name.equals("getLong") && desc.equals("(Ljava/lang/Object;)J"))
            || (name.equals("getFloat") && desc.equals("(Ljava/lang/Object;)F"))
            || (name.equals("getDouble") && desc.equals("(Ljava/lang/Object;)D"))) {
            return new GetXXXModifier(mv, access, name, desc);
        }
        return mv;
    }
}
