/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.processor.jdk.lang;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import com.alibaba.hotswap.processor.basic.BaseClassVisitor;
import com.alibaba.hotswap.processor.jdk.lang.modifier.GetXConstructorsFilterModifier;
import com.alibaba.hotswap.processor.jdk.lang.modifier.GetXFieldsFilterModifier;
import com.alibaba.hotswap.processor.jdk.lang.modifier.NewInstanceModifier;
import com.alibaba.hotswap.processor.jdk.lang.modifier.PrivateGetDeclaredConstructors;
import com.alibaba.hotswap.processor.jdk.lang.modifier.PrivateGetDeclaredFieldsModifier;

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

        if (name.equals("getDeclaredFields") || name.equals("getFields")) {
            return new GetXFieldsFilterModifier(mv, access, name, desc, className);
        }

        if (name.equals("newInstance")) {
            return new NewInstanceModifier(mv, access, name, desc);
        }

        if (name.equals("privateGetDeclaredConstructors")) {
            return new PrivateGetDeclaredConstructors(mv, access, name, desc);
        }

        if (name.equals("getDeclaredConstructors") || name.equals("getConstructors")) {
            return new GetXConstructorsFilterModifier(mv, access, name, desc);
        }

        return mv;
    }
}
