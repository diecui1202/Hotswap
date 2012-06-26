/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.processor.front.vclass;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.commons.RemappingClassAdapter;

import com.alibaba.hotswap.constant.HotswapConstants;
import com.alibaba.hotswap.processor.basic.BaseClassVisitor;
import com.alibaba.hotswap.runtime.HotswapRuntime;
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
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
        HotswapThreadLocalUtil.setClassName(null);
    }
}
