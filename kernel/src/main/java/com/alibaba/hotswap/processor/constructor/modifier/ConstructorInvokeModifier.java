/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.processor.constructor.modifier;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.alibaba.hotswap.configuration.HotswapConfiguration;
import com.alibaba.hotswap.constant.HotswapConstants;
import com.alibaba.hotswap.processor.basic.BaseMethodAdapter;
import com.alibaba.hotswap.runtime.HotswapMethodIndexHolder;
import com.alibaba.hotswap.util.HotswapMethodUtil;

/**
 * @author zhuyong 2012-7-3
 */
public class ConstructorInvokeModifier extends BaseMethodAdapter {

    public ConstructorInvokeModifier(MethodVisitor mv, int access, String name, String desc){
        super(mv, access, name, desc);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc) {
        if (opcode == Opcodes.INVOKESPECIAL && name.equals(HotswapConstants.INIT)) {
            if (HotswapConfiguration.getClassPathInWorkspace(owner) != null) {
                int methodIndex = HotswapMethodIndexHolder.getMethodIndex(owner, name, desc);
                Type[] argsType = Type.getArgumentTypes(desc);

                push(argsType.length);
                newArray(Type.getType(Object.class));

                for (int i = argsType.length - 1; i >= 0; i--) {
                    Type type = argsType[i];
                    swap();
                    box(type);
                    push(i);
                    mv.visitMethodInsn(Opcodes.INVOKESTATIC, Type.getInternalName(HotswapMethodUtil.class),
                                       "processConstructorArgs",
                                       "([Ljava/lang/Object;Ljava/lang/Object;I)[Ljava/lang/Object;");
                }

                mv.visitLdcInsn(Opcodes.ACONST_NULL);
                swap();
                mv.visitLdcInsn(methodIndex);
                swap();
                desc = HotswapConstants.UNIFORM_CONSTRUCTOR_DESC;
            }
        }
        super.visitMethodInsn(opcode, owner, name, desc);
    }
}
