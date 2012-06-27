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
import com.alibaba.hotswap.processor.jdk.reflect.modifier.GetXModifier;
import com.alibaba.hotswap.processor.jdk.reflect.modifier.SetXModifier;

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
            return new GetXModifier(mv, access, name, desc);
        }

        if ((name.equals("set") && desc.equals("(Ljava/lang/Object;Ljava/lang/Object;)V"))
            || (name.equals("setBoolean") && desc.equals("(Ljava/lang/Object;Z)V"))
            || (name.equals("setByte") && desc.equals("(Ljava/lang/Object;B)V"))
            || (name.equals("setChar") && desc.equals("(Ljava/lang/Object;C)V"))
            || (name.equals("setShort") && desc.equals("(Ljava/lang/Object;S)V"))
            || (name.equals("setInt") && desc.equals("(Ljava/lang/Object;I)V"))
            || (name.equals("setLong") && desc.equals("(Ljava/lang/Object;J)V"))
            || (name.equals("setFloat") && desc.equals("(Ljava/lang/Object;F)V"))
            || (name.equals("setDouble") && desc.equals("(Ljava/lang/Object;D)V"))) {
            return new SetXModifier(mv, access, name, desc);
        }

        return mv;
    }
}
