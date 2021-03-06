/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.processor.v;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

import com.alibaba.hotswap.constant.HotswapConstants;
import com.alibaba.hotswap.meta.ClassMeta;
import com.alibaba.hotswap.meta.FieldMeta;
import com.alibaba.hotswap.processor.basic.BaseClassVisitor;
import com.alibaba.hotswap.runtime.HotswapRuntime;
import com.alibaba.hotswap.util.HotswapFieldUtil;
import com.alibaba.hotswap.util.HotswapMethodUtil;
import com.alibaba.hotswap.util.HotswapThreadLocalUtil;

/**
 * Generate V class
 * 
 * @author zhuyong 2012-6-18
 */
public class VClassGenerateVisitor extends BaseClassVisitor {

    private List<String>            initKeys  = new ArrayList<String>();
    private Map<String, MethodNode> initNodes = new HashMap<String, MethodNode>();

    public VClassGenerateVisitor(ClassVisitor cv){
        super(new VClassRemapperVisitor(cv, new Remapper() {

            public String mapType(String type) {
                if (HotswapRuntime.hasClassMeta(type) && type.equals(HotswapThreadLocalUtil.getClassName())) {
                    int v = HotswapRuntime.getClassMeta(type).loadedIndex;
                    return type + HotswapConstants.V_CLASS_PATTERN + v;
                }

                return type;
            }
        }));
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        HotswapThreadLocalUtil.setClassName(name);
        super.visit(version, access, name, signature, superName, interfaces);
        boolean isInterface = ((access & Opcodes.ACC_INTERFACE) == Opcodes.ACC_INTERFACE);

        if (isInterface && (access & Opcodes.ACC_ABSTRACT) == Opcodes.ACC_ABSTRACT) {
            // If it is a interface, then transformer it to class
            access = access - Opcodes.ACC_INTERFACE;
        }

        ClassMeta classMeta = HotswapRuntime.getClassMeta(className);
        classMeta.isInterface = isInterface;
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
        if (!isInterface && name.equals(HotswapConstants.CLINIT)) {
            return null;
        }

        if (name.equals(HotswapConstants.INIT)) {
            MethodNode mn = new MethodNode(access, name, desc, signature, exceptions);

            String mk = HotswapMethodUtil.getMethodKey(name, desc);
            initKeys.add(mk);
            initNodes.put(mk, mn);

            return mn;
        }

        return super.visitMethod(access, name, desc, signature, exceptions);
    }

    @Override
    public void visitEnd() {
        HotswapThreadLocalUtil.setClassName(null);
        processInitMethods();
        super.visitEnd();
    }

    private void processInitMethods() {
        ClassMeta classMeta = HotswapRuntime.getClassMeta(className);
        if (!HotswapRuntime.getClassInitialized(className)) {
            // The first time
            for (String mk : initKeys) {
                initNodes.get(mk).accept(cv);
            }
        } else {
            // Reload
            for (String mk : classMeta.primaryInitKeyList) {
                MethodNode node = initNodes.remove(mk);
                if (node == null) {
                    node = classMeta.primaryInitNodes.get(mk);
                }
                node.accept(cv);
            }

            for (MethodNode node : initNodes.values()) {
                node.accept(cv);
            }
        }
    }
}
