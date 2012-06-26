/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.processor.front;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.tree.FieldNode;

import com.alibaba.hotswap.meta.ClassMeta;
import com.alibaba.hotswap.processor.basic.BaseClassVisitor;
import com.alibaba.hotswap.runtime.HotswapRuntime;

/**
 * @author zhuyong 2012-6-18
 */
public class FieldNodeHolderVisitor extends BaseClassVisitor {

    private List<FieldNode> fieldNodes = new ArrayList<FieldNode>();
    private ClassMeta       classMeta;

    public FieldNodeHolderVisitor(ClassVisitor cv){
        super(cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        classMeta = HotswapRuntime.getClassMeta(className);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        if (!HotswapRuntime.getClassInitialized(className)) {
            classMeta.putFieldMeta(access, name, desc, signature, value);
        }
        FieldNode fn = new FieldNode(access, name, desc, signature, value);
        fieldNodes.add(fn);
        return fn;
    }

    @Override
    public void visitEnd() {
        for (int i = 0; i < fieldNodes.size(); i++) {
            classMeta.addloadedFieldMeta(fieldNodes.get(i));
        }
        if (!classMeta.initialized) {
            classMeta.primaryFieldNodes.putAll(classMeta.loadedFieldNodes);
        }
        super.visitEnd();
    }
}
