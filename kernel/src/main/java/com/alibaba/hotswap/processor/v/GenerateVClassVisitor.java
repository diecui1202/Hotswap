/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.processor.v;

import java.util.HashMap;
import java.util.Map;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.commons.RemappingClassAdapter;
import org.objectweb.asm.tree.FieldNode;

import com.alibaba.hotswap.constant.HotswapConstants;
import com.alibaba.hotswap.meta.ClassMeta;
import com.alibaba.hotswap.meta.FieldMeta;
import com.alibaba.hotswap.processor.basic.BaseClassVisitor;
import com.alibaba.hotswap.runtime.HotswapRuntime;
import com.alibaba.hotswap.util.HotswapFieldUtil;
import com.alibaba.hotswap.util.HotswapThreadLocalUtil;

/**
 * Generate V class
 * 
 * @author zhuyong 2012-6-18
 */
public class GenerateVClassVisitor extends BaseClassVisitor {

    public GenerateVClassVisitor(ClassVisitor cv){
        super(new RemappingClassAdapter(cv, new Remapper() {

            @Override
            public String mapType(String typeName) {
                if (HotswapRuntime.hasClassMeta(typeName) && typeName.equals(HotswapThreadLocalUtil.getClassName())) {
                    int v = HotswapRuntime.getClassMeta(typeName).loadedIndex;
                    return typeName + HotswapConstants.V_CLASS_PATTERN + v;
                } else {
                    return typeName;
                }
            }
        }));
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        HotswapThreadLocalUtil.setClassName(name);
        super.visit(version, access, name, signature, superName, interfaces);

        ClassMeta classMeta = HotswapRuntime.getClassMeta(className);

        if (!classMeta.initialized) {
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
                    } else {
                        primaryFN.accept(cv);
                    }
                } else {
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
                    fn.accept(cv);
                } else {
                    if (classMeta.primaryFieldKeyList.contains(fieldKey)) {
                        // It's a primary field
                        if (fn.access == fm2.access) {

                        } else {
                            // Modified field, alias it
                            fn.name = HotswapConstants.PREFIX_FIELD_ALIAS + fn.name;
                            fn.accept(cv);
                        }
                    } else {
                        fn.accept(cv);
                    }
                }
            }
        }
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if (name.equals(HotswapConstants.CLINIT)) {
            return null;
        }
        return super.visitMethod(access, name, desc, signature, exceptions);
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
        HotswapThreadLocalUtil.setClassName(null);
    }
}
