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
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.tree.MethodNode;

import com.alibaba.hotswap.constant.HotswapConstants;
import com.alibaba.hotswap.processor.basic.BaseClassVisitor;
import com.alibaba.hotswap.util.HotswapThreadLocalUtil;

/**
 * @author zhuyong 2012-6-30
 */
public class InitMethodNodeHolderVisitor extends BaseClassVisitor {

    private List<MethodNode> initNodes = new ArrayList<MethodNode>();

    public InitMethodNodeHolderVisitor(ClassVisitor cv){
        super(cv);
        HotswapThreadLocalUtil.setInitMethodNodes(null);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

        if (name.equals(HotswapConstants.INIT)) {
            MethodNode mn = new MethodNode(access, name, desc, signature, exceptions);
            initNodes.add(mn);

            return mn;
        }
        return mv;
    }

    @Override
    public void visitEnd() {
        super.visitEnd();

        HotswapThreadLocalUtil.setInitMethodNodes(initNodes);
    }
}
