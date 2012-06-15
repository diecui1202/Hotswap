/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.processor;

import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.List;

import org.objectweb.asm.ClassVisitor;

import com.alibaba.hotswap.processor.clinit.ClinitMethodVisitor;
import com.alibaba.hotswap.processor.compile.CompilerErrorVisitor;
import com.alibaba.hotswap.processor.constructor.ConstructorVisitor;
import com.alibaba.hotswap.processor.field.access.FieldAccessVisitor;
import com.alibaba.hotswap.processor.field.holder.FieldHolderVisitor;

/**
 * @author yong.zhuy 2012-6-13
 */
public class HotswapProcessorHolder {

    private static final List<Class<? extends ClassVisitor>> hotswap_processor_holder = new LinkedList<Class<? extends ClassVisitor>>();

    static {
        // Do not change these processors' order!!!
        hotswap_processor_holder.add(CompilerErrorVisitor.class);
        hotswap_processor_holder.add(FieldHolderVisitor.class);
        hotswap_processor_holder.add(ConstructorVisitor.class);
        hotswap_processor_holder.add(ClinitMethodVisitor.class);
        hotswap_processor_holder.add(FieldAccessVisitor.class);
    }

    @SuppressWarnings("unchecked")
    public static ClassVisitor generateClassVisitor(ClassVisitor cw) {
        if (cw == null) {
            throw new IllegalArgumentException("ClassWriter cw is null.");
        }

        ClassVisitor instance = cw;
        try {
            for (Class<?> clazz : hotswap_processor_holder) {
                Constructor<ClassVisitor> c = (Constructor<ClassVisitor>) clazz.getConstructor(ClassVisitor.class);
                instance = c.newInstance(instance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return instance;
    }
}
