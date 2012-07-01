/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.processor.constructor;

import java.util.List;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.MethodNode;

import com.alibaba.hotswap.constant.HotswapConstants;
import com.alibaba.hotswap.processor.basic.BaseClassVisitor;
import com.alibaba.hotswap.util.HotswapThreadLocalUtil;

/**
 * @author yong.zhuy 2012-6-13
 */
public class InitVisitor extends BaseClassVisitor {

    public InitVisitor(ClassVisitor cv){
        super(cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

        if (name.equals(HotswapConstants.INIT)) {
            return null;
        }
        return mv;
    }

    @Override
    public void visitEnd() {
        List<MethodNode> initNodes = HotswapThreadLocalUtil.getInitMethodNodes();
        for (MethodNode init : initNodes) {
        }
        super.visitEnd();
    }
}
