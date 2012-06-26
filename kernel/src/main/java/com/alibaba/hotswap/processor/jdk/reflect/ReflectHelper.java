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
import com.alibaba.hotswap.meta.FieldMeta;
import com.alibaba.hotswap.runtime.HotswapRuntime;
import com.sun.xml.internal.ws.org.objectweb.asm.Type;

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

    public static boolean isFieldInHotswapHolder(Object obj, Field field) {
        String className = obj.getClass().getName();
        if (HotswapRuntime.hasClassMeta(className)) {
            // field holder ---> true
            if (field.getName().equals(HotswapConstants.FIELD_HOLDER)
                || field.getName().equals(HotswapConstants.STATIC_FIELD_HOLDER)) {
                return false;
            }
            ClassMeta classMeta = HotswapRuntime.getClassMeta(className);
            String fk = field.getModifiers() + Type.getInternalName(field.getDeclaringClass());
            FieldMeta fm = classMeta.getFieldMeta(fk);
            if (fm != null && !fm.isAdded() && !fm.isDeleted(classMeta.loadedIndex)) {
                return false;
            } else {
                
            }

            return true;
        }

        return false;
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
