/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.processor.jdk.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.concurrent.ConcurrentMap;

import com.alibaba.hotswap.constant.HotswapConstants;
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

        synchronized (classMeta) {
            if (publicOnly) {
                fields = classMeta.vClass.getFields();
            } else {
                fields = classMeta.vClass.getDeclaredFields();
            }
        }

        try {
            Field clazzField = Field.class.getDeclaredField("clazz");
            clazzField.setAccessible(true);
            for (Field f : fields) {
                clazzField.set(f, clazz);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fields;
    }

    @SuppressWarnings("unchecked")
    public static Object getHotswapControlledValue(Object obj, Field field) {
        Class<?> clazz = obj.getClass();
        ClassMeta classMeta = HotswapRuntime.getClassMeta(clazz.getName());

        if (Modifier.isStatic(field.getModifiers())) {
            try {
                Field staticFieldHolderField = clazz.getField(HotswapConstants.STATIC_FIELD_HOLDER);
                ConcurrentMap<String, Object> staticFieldHolder = (ConcurrentMap<String, Object>) staticFieldHolderField.get(null);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
