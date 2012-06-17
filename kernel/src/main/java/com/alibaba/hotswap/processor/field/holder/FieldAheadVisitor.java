/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.processor.field.holder;

import java.util.Iterator;
import java.util.Map.Entry;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;

import com.alibaba.hotswap.constant.HotswapConstants;
import com.alibaba.hotswap.meta.ClassMeta;
import com.alibaba.hotswap.meta.FieldMeta;
import com.alibaba.hotswap.processor.basic.BaseClassVisitor;
import com.alibaba.hotswap.runtime.HotswapRuntime;
import com.alibaba.hotswap.util.HotswapUtil;

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

        if (!classMeta.initialized) {
            // First load
            for (FieldMeta fm : HotswapRuntime.getClassMeta(className).loadedFieldMetas) {
                FieldVisitor fv = cv.visitField(fm.access, fm.name, fm.desc, fm.signature, fm.value);
                if (fv != null) {
                    fv.visitEnd();
                }
            }
        } else {
            // Reload

            // 1. Visit the primary fields.
            for (String fmKey : classMeta.primaryFieldKeyList) {
                FieldMeta fm = classMeta.fieldMetas.get(fmKey);
                FieldVisitor fv = cv.visitField(fm.access, fm.name, fm.desc, fm.signature, null);
                if (fv != null) {
                    fv.visitEnd();
                }
            }

            // 2. Add and remove modified field.
            for (FieldMeta fm : classMeta.loadedFieldMetas) {
                // All these fields are the reloaded class's fields

                String fieldKey = fm.getKey();
                FieldMeta fm2 = classMeta.getFieldMeta(fieldKey);

                if (fm2 == null) {
                    // This is a new field
                    classMeta.addFieldMeta(fm.access, fm.name, fm.desc, fm.signature, fm.value);
                } else {
                    if (classMeta.primaryFieldKeyList.contains(fieldKey)) {
                        // It's a primary field
                        if (fm.access == fm2.access) {
                            // An exist field
                            classMeta.putFieldMeta(fm.access, fm.name, fm.desc, fm.signature, fm.value);
                        } else {
                            // Modified field, alias it
                            fm.name = HotswapConstants.PREFIX_FIELD_ALIAS + fm.name;
                            classMeta.addFieldMeta(fm.access, fm.name, fm.desc, fm.signature, fm.value);
                        }
                    } else {
                        classMeta.putFieldMeta(fm.access, fm.name, fm.desc, fm.signature, fm.value);
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

        HotswapUtil.sysout(classMeta);
    }
}
