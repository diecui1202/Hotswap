/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.processor.annotation;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;

import com.alibaba.hotswap.meta.ClassMeta;
import com.alibaba.hotswap.processor.annotation.type.Annotation;
import com.alibaba.hotswap.runtime.HotswapRuntime;

/**
 * @author yong.zhuy 2012-6-18
 */
public class AnnotationHolderVisitor extends AnnotationVisitor {

    private Annotation anno;

    private ClassMeta  classMeta = null;

    public AnnotationHolderVisitor(AnnotationVisitor av, Annotation anno){
        super(Opcodes.ASM4, av);

        this.anno = anno;

        if (anno != null && HotswapRuntime.hasClassMeta(anno.getClassName())) {
            classMeta = HotswapRuntime.getClassMeta(anno.getClassName());
        }
    }

    @Override
    public void visit(String name, Object value) {
        super.visit(name, value);
    }

    @Override
    public void visitEnum(String name, String desc, String value) {
        super.visitEnum(name, desc, value);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String name, String desc) {
        return new AnnotationHolderVisitor(super.visitAnnotation(name, desc), anno);
    }

    @Override
    public AnnotationVisitor visitArray(String name) {
        return super.visitArray(name);
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
    }
}
