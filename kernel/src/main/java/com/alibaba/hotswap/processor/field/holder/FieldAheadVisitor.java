/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.processor.field.holder;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.tree.FieldNode;

import com.alibaba.hotswap.constant.HotswapConstants;
import com.alibaba.hotswap.meta.ClassMeta;
import com.alibaba.hotswap.meta.FieldMeta;
import com.alibaba.hotswap.processor.basic.BaseClassVisitor;
import com.alibaba.hotswap.runtime.HotswapRuntime;
import com.alibaba.hotswap.util.HotswapFieldUtil;

/**
 * @author zhuyong 2012-6-16 08:52:55
 */
public class FieldAheadVisitor extends BaseClassVisitor {

    private ClassMeta classMeta;

    public FieldAheadVisitor(ClassVisitor cv){
        super(cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);

        classMeta = HotswapRuntime.getClassMeta(className);

        if (!classMeta.isLoaded()) {
            // First load
            for (String key : classMeta.primaryFieldKeyList) {
                classMeta.primaryFieldNodes.get(key).accept(cv);
            }
        } else {
            // Reload
            Map<String, FieldNode> loadedFieldNodes = new HashMap<String, FieldNode>();
            loadedFieldNodes.putAll(classMeta.loadedFieldNodes);

            // 1. Visit the primary fields.
            for (String key : classMeta.primaryFieldKeyList) {
                FieldNode primaryFN = classMeta.primaryFieldNodes.get(key);
                FieldNode loadedFN = loadedFieldNodes.get(key);
                if (loadedFN != null) {
                    if (loadedFN.access == primaryFN.access) {
                        // Primary field(may change annotation/signature) or change from other field
                        loadedFN.accept(cv);
                        loadedFieldNodes.remove(key);
                        // update loadedIndex
                        classMeta.putFieldMeta(classMeta.getFieldMeta(key));
                    } else {
                        primaryFN.accept(cv);
                    }
                } else {
                    // This primary field is removed, so do not change loadedIndex
                    primaryFN.accept(cv);
                }
            }

            // 2. Add and remove modified field.
            for (FieldNode fn : loadedFieldNodes.values()) {
                // All these fields are the reloaded class's fields

                String fieldKey = HotswapFieldUtil.getFieldKey(fn.name, fn.desc);
                FieldMeta fm2 = classMeta.getFieldMeta(fieldKey);

                if (fm2 == null) {
                    // This is a new field
                    classMeta.addFieldMeta(fn.access, fn.name, fn.desc, fn.signature, fn.value);
                } else {
                    if (classMeta.primaryFieldKeyList.contains(fieldKey)) {
                        // It's a primary field
                        if (fn.access == fm2.access) {
                            // An exist field
                            classMeta.putFieldMeta(fn.access, fn.name, fn.desc, fn.signature, fn.value);
                        } else {
                            // Modified field, alias it
                            fn.name = HotswapConstants.PREFIX_FIELD_ALIAS + fn.name;
                            classMeta.addFieldMeta(fn.access, fn.name, fn.desc, fn.signature, fn.value);
                        }
                    } else {
                        classMeta.putFieldMeta(fn.access, fn.name, fn.desc, fn.signature, fn.value);
                    }
                }
            }
        }
    }

    @Override
    public void visitEnd() {
        super.visitEnd();

        Iterator<Entry<String, FieldMeta>> iter = classMeta.fieldMetas.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<String, FieldMeta> entry = iter.next();
            FieldMeta fm = entry.getValue();
            if (classMeta.loadedIndex > fm.loadedIndex && !classMeta.primaryFieldKeyList.contains(fm.getKey())) {
                iter.remove();
            }
        }
    }
}
