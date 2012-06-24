/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.processor.jdk.reflect;

import java.lang.reflect.Field;

import com.alibaba.hotswap.meta.ClassMeta;
import com.alibaba.hotswap.runtime.HotswapRuntime;

/**
 * @author zhuyong 2012-6-24
 */
public class ReflectHelper {

    public static Field[] privateGetDeclaredFields0(Class<?> clazz, boolean publicOnly) {
        String name = clazz.getName();
        ClassMeta classMeta = HotswapRuntime.getClassMeta(name);
        Field[] fields = null;
        try {
            System.out.println(classMeta.loader + "\n" + classMeta.vClass.getClassLoader());
            Class tclazz = classMeta.vClass.getClassLoader().loadClass("test.Test1");
            System.out.println(tclazz);
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (publicOnly) {
            fields = classMeta.vClass.getFields();
        } else {
            fields = classMeta.vClass.getDeclaredFields();
        }
        for (Field f : fields) {
            System.out.println(f);
        }

        return fields;
    }
}
