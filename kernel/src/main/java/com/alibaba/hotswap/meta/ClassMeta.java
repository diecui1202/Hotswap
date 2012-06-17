/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.meta;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.alibaba.hotswap.util.HotswapFieldUtil;

/**
 * @author yong.zhuy 2012-5-18 12:39:09
 */
public class ClassMeta {

    public String                 name;
    public String                 path;
    public long                   lastModified;
    public boolean                initialized         = false;
    public Class<?>               clazz;

    // Class's fields
    public Map<String, FieldMeta> fieldMetas          = new HashMap<String, FieldMeta>();
    public List<FieldMeta>        loadedFieldMetas    = new LinkedList<FieldMeta>();

    public List<String>           primaryFieldKeyList = new LinkedList<String>();

    // loaded index
    public int                    loadedIndex         = 0;

    public void addloadedFieldMeta(int access, String name, String desc, String signature, Object value) {
        FieldMeta fm = new FieldMeta(access, name, desc, signature, value);
        loadedFieldMetas.add(fm);
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

        fm.loadedIndex = this.loadedIndex;

        fieldMetas.put(key, fm);
    }

    public void addFieldMeta(int access, String name, String desc, String signature, Object value) {
        FieldMeta fm = new FieldMeta(access, name, desc, signature, value);
        String key = fm.getKey();

        fm.loadedIndex = this.loadedIndex;
        fm.added = true;

        fieldMetas.put(key, fm);
    }

    public void removeFieldMeta(String fieldKey) {
        fieldMetas.remove(fieldKey);
    }

    public boolean containField(String fieldKey) {
        return fieldMetas.containsKey(fieldKey);
    }

    public FieldMeta getFieldMeta(String fieldKey) {
        FieldMeta fm = fieldMetas.get(fieldKey);

        return fm;
    }

    public void reset() {
        loadedFieldMetas.clear();
        this.loadedIndex++;
    }

    public String toString() {
        return name + " [" + initialized + "], loadedIndex [" + loadedIndex + "], fieldMeta {" + getFieldMetasString()
               + "}";
    }

    private String getFieldMetasString() {
        StringBuilder sb = new StringBuilder();
        int index = 0, size = fieldMetas.size();
        for (FieldMeta fm : fieldMetas.values()) {
            sb.append(fm.toString(this.loadedIndex));
            if (++index != size) {
                sb.append("; ");
            }
        }

        return sb.toString();
    }
}
