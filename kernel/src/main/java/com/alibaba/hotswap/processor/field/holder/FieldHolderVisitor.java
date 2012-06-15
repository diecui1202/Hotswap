/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.processor.field.holder;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;

import com.alibaba.hotswap.constant.HotswapConstants;
import com.alibaba.hotswap.meta.ClassMeta;
import com.alibaba.hotswap.meta.FieldMeta;
import com.alibaba.hotswap.processor.basic.BaseClassVisitor;
import com.alibaba.hotswap.runtime.HotswapRuntime;

/**
 * Add <code>__$$hotswap_field_holder$$__</code> and <code>__$$hotswap_static_field_holder$$__</code>
 * 
 * @author yong.zhuy 2012-6-13
 */
public class FieldHolderVisitor extends BaseClassVisitor {

    private List<FieldMeta> fms = new ArrayList<FieldMeta>();

    public FieldHolderVisitor(ClassVisitor cv){
        super(cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);

        addFieldHolder();
        addStaticFieldHolder();
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        fms.add(new FieldMeta(access, name, desc, signature, value));

        if (!HotswapRuntime.getClassInitialized(className)) {
            HotswapRuntime.getClassMeta(className).putFieldMeta(access, name, desc, signature, value);
        }

        return null;
    }

    private void addFieldHolder() {
        FieldVisitor fv = cv.visitField(Opcodes.ACC_TRANSIENT + Opcodes.ACC_PUBLIC + Opcodes.ACC_FINAL,
                                        HotswapConstants.FIELD_HOLDER, "Ljava/util/concurrent/ConcurrentHashMap;",
                                        null, null);
        if (fv != null) {
            fv.visitEnd();
        }
    }

    private void addStaticFieldHolder() {
        FieldVisitor fv = cv.visitField(Opcodes.ACC_TRANSIENT + Opcodes.ACC_STATIC + Opcodes.ACC_PUBLIC
                                                + Opcodes.ACC_FINAL, HotswapConstants.STATIC_FIELD_HOLDER,
                                        "Ljava/util/concurrent/ConcurrentHashMap;", null, null);
        if (fv != null) {
            fv.visitEnd();
        }
    }

    @Override
    public void visitEnd() {
        if (!HotswapRuntime.getClassInitialized(className)) {
            // First load
            for (FieldMeta fm : fms) {
                FieldVisitor fv = cv.visitField(fm.access, fm.name, fm.desc, fm.signature, fm.value);
                if (fv != null) {
                    fv.visitEnd();
                }
            }
        } else {
            // Reload
            ClassMeta cm = HotswapRuntime.getClassMeta(className);
            for (String fmKey : cm.primaryFieldKeyList) {
                FieldMeta fm = cm.fieldMetas.get(fmKey);
                FieldVisitor fv = cv.visitField(fm.access, fm.name, fm.desc, fm.signature, null);
                if (fv != null) {
                    fv.visitEnd();
                }
            }

            // Set add/remove field, set deleted flag to false
            cm.reset();
            for (FieldMeta fm : fms) {
                String fmKey = fm.getKey();
                FieldMeta fm2 = cm.getFieldMeta(fmKey);
                if (fm2 == null) {
                    // This is a new field, set added flag and deleted flag
                    cm.addFieldMeta(fm.access, fm.name, fm.desc, fm.signature, fm.value);
                } else {
                    // This is an exist field, update access, signature and deleted flag
                    cm.putFieldMeta(fm.access, fm.name, fm.desc, fm.signature, fm.value);
                }
            }
        }

        super.visitEnd();
    }
}
