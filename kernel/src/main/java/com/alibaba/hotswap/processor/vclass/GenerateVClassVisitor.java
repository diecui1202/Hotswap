/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.processor.vclass;

import org.objectweb.asm.ClassVisitor;

import com.alibaba.hotswap.constant.HotswapConstants;
import com.alibaba.hotswap.processor.basic.BaseClassVisitor;
import com.alibaba.hotswap.runtime.HotswapRuntime;

/**
 * Generate V class
 * 
 * @author zhuyong 2012-6-18
 */
public class GenerateVClassVisitor extends BaseClassVisitor {

    public GenerateVClassVisitor(ClassVisitor cv){
        super(cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        int v = HotswapRuntime.getClassMeta(name).loadedIndex;
        super.visit(version, access, name + HotswapConstants.V_CLASS_PATTERN + v, signature, superName, interfaces);
    }
}
