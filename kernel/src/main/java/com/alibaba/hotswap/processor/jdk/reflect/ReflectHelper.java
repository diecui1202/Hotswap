/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.processor.jdk.reflect;

import java.lang.reflect.Field;

/**
 * @author zhuyong 2012-6-17
 */
public class ReflectHelper {

    public static Field[] filterReflectFields(Field[] reflectFields, Class<?> clazz) {
        // String internalClassName = HotswapUtil.getInternalClassName(clazz.getName());
        // if (!HotswapRuntime.hasClassMeta(internalClassName)) {
        // return reflectFields;
        // }
        //
        // if (reflectFields == null) {
        // reflectFields = new Field[0];
        // }
        //
        // List<Field> fields = new ArrayList<Field>();
        // ClassMeta classMeta = HotswapRuntime.getClassMeta(internalClassName);
        // for (Field f : reflectFields) {
        // String key = HotswapFieldUtil.getFieldKey(f.getName(), Type.getInternalName(f.getType()));
        // FieldMeta fm = classMeta.getFieldMeta(key);
        // if (fm != null && classMeta.primaryFieldKeyList.contains(key) && fm.isDeleted(classMeta.loadedIndex)) {
        // // Primary field is deleted, so remove it
        // } else {
        // fields.add(f);
        // }
        // }
        //
        // try {
        // Constructor<Field> cf = Field.class.getDeclaredConstructor(Class.class, String.class, Class.class,
        // int.class, int.class, String.class, byte[].class);
        // for (FieldMeta fm : classMeta.fieldMetas.values()) {
        // if (fm.isAdded()) {
        // // New added field
        //
        // }
        // }
        // } catch (SecurityException e) {
        // e.printStackTrace();
        // } catch (NoSuchMethodException e) {
        // e.printStackTrace();
        // }
        //
        // reflectFields = fields.toArray(new Field[] {});
        return null;
    }
}
