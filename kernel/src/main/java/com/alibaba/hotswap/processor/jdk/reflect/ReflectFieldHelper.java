/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.processor.jdk.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.objectweb.asm.Type;

import com.alibaba.hotswap.constant.HotswapConstants;
import com.alibaba.hotswap.meta.ClassMeta;
import com.alibaba.hotswap.meta.FieldMeta;
import com.alibaba.hotswap.runtime.HotswapRuntime;
import com.alibaba.hotswap.util.HotswapFieldUtil;
import com.alibaba.hotswap.util.ReflectionUtil;

/**
 * @author zhuyong 2012-6-24
 */
public class ReflectFieldHelper {

    public static Field[] filterHotswapFields(Field[] fields) {
        List<Field> rets = new ArrayList<Field>();
        for (Field f : fields) {
            if (f.getName().equals(HotswapConstants.STATIC_FIELD_HOLDER)
                || f.getName().equals(HotswapConstants.FIELD_HOLDER)) {
                continue;
            }
            rets.add(f);
        }
        return rets.toArray(new Field[] {});
    }

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

        List<Field> holderFields = new ArrayList<Field>();
        try {
            // Remove field which has been deleted
            Field clazzField = Field.class.getDeclaredField("clazz");
            Field nameField = Field.class.getDeclaredField("name");
            clazzField.setAccessible(true);
            nameField.setAccessible(true);

            for (Field f : fields) {
                clazzField.set(f, clazz);

                String fk = HotswapFieldUtil.getFieldKey(f.getName(), Type.getDescriptor(f.getType()));
                FieldMeta fm = classMeta.getFieldMeta(fk);
                if (!fm.isDeleted(classMeta.loadedIndex)) {
                    if (fm.isAdded() && f.getName().indexOf(HotswapConstants.PREFIX_FIELD_ALIAS) == 0) {
                        String fieldName = f.getName().substring(HotswapConstants.PREFIX_FIELD_ALIAS.length());
                        if (classMeta.getFieldMeta(HotswapFieldUtil.getFieldKey(fieldName,
                                                                                Type.getDescriptor(f.getType()))).isDeleted(classMeta.loadedIndex)) {

                            nameField.set(f, fieldName);
                        }
                    }
                    holderFields.add(f);
                }
            }

            Method getDeclaredFields0Method = Class.class.getDeclaredMethod("getDeclaredFields0", boolean.class);
            getDeclaredFields0Method.setAccessible(true);
            Field[] tranformFields = (Field[]) getDeclaredFields0Method.invoke(clazz, publicOnly);
            for (Field f : tranformFields) {
                if (f.getName().equals(HotswapConstants.STATIC_FIELD_HOLDER)
                    || f.getName().equals(HotswapConstants.FIELD_HOLDER)) {
                    holderFields.add(f);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return holderFields.toArray(new Field[] {});
    }

    public static boolean isInHotswapFieldHolder(Object obj, Field field) {
        String className = obj.getClass().getName();
        if (HotswapRuntime.hasClassMeta(className)) {
            // field holder ---> true
            if (field.getName().equals(HotswapConstants.FIELD_HOLDER)
                || field.getName().equals(HotswapConstants.STATIC_FIELD_HOLDER)) {
                return false;
            }
            ClassMeta classMeta = HotswapRuntime.getClassMeta(className);
            String fk = HotswapFieldUtil.getFieldKey(field.getName(), Type.getDescriptor(field.getType()));
            FieldMeta fm = classMeta.getFieldMeta(fk);
            if (fm != null && !fm.isAdded() && !fm.isDeleted(classMeta.loadedIndex)) {
                // a primary field
                return false;
            } else if (fm != null && !fm.isAdded() && fm.isDeleted(classMeta.loadedIndex)) {
                // a primary field, but it has beed deleted, so access alias field
                String aliasFK = HotswapFieldUtil.getFieldKey(HotswapConstants.PREFIX_FIELD_ALIAS + field.getName(),
                                                              Type.getDescriptor(field.getType()));
                FieldMeta aliasFM = classMeta.getFieldMeta(aliasFK);
                if (aliasFM != null && aliasFM.isAdded() && !aliasFM.isDeleted(classMeta.loadedIndex)) {
                    return true;
                }
            } else if (fm != null && fm.isAdded() && !fm.isDeleted(classMeta.loadedIndex)) {
                // new field
                return true;
            }
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    public static Object getHotswapFieldHolderValue(Object object, Field field) {
        Class<?> clazz = object.getClass();
        ClassMeta classMeta = HotswapRuntime.getClassMeta(clazz.getName());

        try {
            ConcurrentHashMap<String, Object> fieldHolder = null;
            if (Modifier.isStatic(field.getModifiers())) {
                fieldHolder = (ConcurrentHashMap<String, Object>) ReflectionUtil.getFieldValue(object,
                                                                                               HotswapConstants.STATIC_FIELD_HOLDER);
            } else {
                fieldHolder = (ConcurrentHashMap<String, Object>) ReflectionUtil.getFieldValue(object,
                                                                                               HotswapConstants.FIELD_HOLDER);
            }
            // get field key
            String fk = HotswapFieldUtil.getFieldKey(field.getName(), Type.getDescriptor(field.getType()));
            FieldMeta fm = classMeta.getFieldMeta(fk);
            if (fm != null && !fm.isAdded() && fm.isDeleted(classMeta.loadedIndex)) {
                String aliasFK = HotswapFieldUtil.getFieldKey(HotswapConstants.PREFIX_FIELD_ALIAS + field.getName(),
                                                              Type.getDescriptor(field.getType()));
                FieldMeta aliasFM = classMeta.getFieldMeta(aliasFK);
                if (aliasFM != null && aliasFM.isAdded() && !aliasFM.isDeleted(classMeta.loadedIndex)) {
                    fk = aliasFK;
                }
            }

            return fieldHolder.get(fk);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static void setHotswapFieldHolderValue(Object object, Field field, Object value) {
        Class<?> clazz = object.getClass();
        ClassMeta classMeta = HotswapRuntime.getClassMeta(clazz.getName());

        try {
            ConcurrentHashMap<String, Object> fieldHolder = null;
            if (Modifier.isStatic(field.getModifiers())) {
                fieldHolder = (ConcurrentHashMap<String, Object>) ReflectionUtil.getFieldValue(object,
                                                                                               HotswapConstants.STATIC_FIELD_HOLDER);
            } else {
                fieldHolder = (ConcurrentHashMap<String, Object>) ReflectionUtil.getFieldValue(object,
                                                                                               HotswapConstants.FIELD_HOLDER);
            }
            // get field key
            String fk = HotswapFieldUtil.getFieldKey(field.getName(), Type.getDescriptor(field.getType()));
            FieldMeta fm = classMeta.getFieldMeta(fk);
            if (fm != null && !fm.isAdded() && fm.isDeleted(classMeta.loadedIndex)) {
                String aliasFK = HotswapFieldUtil.getFieldKey(HotswapConstants.PREFIX_FIELD_ALIAS + field.getName(),
                                                              Type.getDescriptor(field.getType()));
                FieldMeta aliasFM = classMeta.getFieldMeta(aliasFK);
                if (aliasFM != null && aliasFM.isAdded() && !aliasFM.isDeleted(classMeta.loadedIndex)) {
                    fk = aliasFK;
                }
            }

            fieldHolder.put(fk, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
