/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.processor.jdk;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.hotswap.processor.basic.BaseClassVisitor;
import com.alibaba.hotswap.processor.jdk.classloader.ClassLoaderVisitor;
import com.alibaba.hotswap.processor.jdk.classloader.URLClassLoaderVisitor;
import com.alibaba.hotswap.processor.jdk.lang.JdkClassVisitor;
import com.alibaba.hotswap.processor.jdk.reflect.JdkConstructorVisitor;
import com.alibaba.hotswap.processor.jdk.reflect.JdkFieldVisitor;

/**
 * @author zhuyong 2012-6-19
 */
public class JdkClassProcessorFactory {

    public static final Map<Class<?>, Class<? extends BaseClassVisitor>> jdk_class_processor_holder = new HashMap<Class<?>, Class<? extends BaseClassVisitor>>();

    static {
        jdk_class_processor_holder.put(ClassLoader.class, ClassLoaderVisitor.class);
        jdk_class_processor_holder.put(URLClassLoader.class, URLClassLoaderVisitor.class);
        jdk_class_processor_holder.put(Class.class, JdkClassVisitor.class);
        jdk_class_processor_holder.put(Field.class, JdkFieldVisitor.class);
        jdk_class_processor_holder.put(Constructor.class, JdkConstructorVisitor.class);
    }
}
