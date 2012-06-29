/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.processor.v;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.commons.RemappingClassAdapter;

/**
 * @author zhuyong 2012-6-29
 */
public class VClassRemapperVisitor extends RemappingClassAdapter {

    public VClassRemapperVisitor(ClassVisitor cv, Remapper remapper){
        super(cv, remapper);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.className = name;
        cv.visit(version, access, remapper.mapType(name), signature, superName, interfaces);
    }
}
