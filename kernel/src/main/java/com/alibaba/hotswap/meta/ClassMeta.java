/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.meta;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.alibaba.hotswap.constant.HotswapConstants;
import com.alibaba.hotswap.util.HotswapFieldUtil;
import com.alibaba.hotswap.util.ReflectionUtil;

/**
 * @author yong.zhuy 2012-5-18 12:39:09
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class ClassMeta {

    public String                           name;
    public String                           path;
    public long                             lastModified;
    public boolean                          initialized         = false;
    public Class<?>                         clazz;

    // Class's fields
    public ConcurrentMap<String, FieldMeta> fieldMetas          = new ConcurrentHashMap<String, FieldMeta>();
    public List<String>                     primaryFieldKeyList = new LinkedList<String>();

    private List<WeakReference<Object>>     objs                = new LinkedList<WeakReference<Object>>();

    public void addNewInstance(Object obj) {
        objs.add(new WeakReference(obj));
    }

    public void putFieldMeta(int access, String name, String desc, String signature, Object value) {
        String key = HotswapFieldUtil.getFieldKey(name, desc);
        FieldMeta fm = fieldMetas.get(key);
        if (fm == null) {
            fm = new FieldMeta(access, name, desc, signature, value);
            fm.added = false;
            primaryFieldKeyList.add(key);
        }

        fm.access = access;
        fm.name = name;
        fm.desc = desc;
        fm.signature = signature;
        fm.value = value;

        fm.deleted = false;

        fieldMetas.put(key, fm);
    }

    public void addFieldMeta(int access, String name, String desc, String signature, Object value) {
        FieldMeta fm = new FieldMeta(access, name, desc, signature, value);
        String key = fm.getKey();

        fm.deleted = false;
        fm.added = true;

        fieldMetas.put(key, fm);
    }

    public void setOldInstanceField(int access, String name, String desc, Object value) {
        Iterator<WeakReference<Object>> iter = objs.iterator();

        while (iter.hasNext()) {
            WeakReference<Object> wf = iter.next();
            Object o = wf.get();
            if (o == null) {
                iter.remove();
                continue;
            }

            ConcurrentHashMap fieldHolder = (ConcurrentHashMap) ReflectionUtil.getFieldValue(o,
                                                                                             HotswapConstants.FIELD_HOLDER);
            if (fieldHolder != null) {
                fieldHolder.put(name + "(" + desc + ")", value);
            }
        }
    }

    public boolean containField(String fieldKey) {
        return fieldMetas.containsKey(fieldKey);
    }

    public FieldMeta getFieldMeta(String fieldKey) {
        return fieldMetas.get(fieldKey);
    }

    public void reset() {
        for (FieldMeta fm : fieldMetas.values()) {
            fm.deleted = true;
        }
    }

    public String toString() {
        return name + " [" + initialized + "]";
    }
}
