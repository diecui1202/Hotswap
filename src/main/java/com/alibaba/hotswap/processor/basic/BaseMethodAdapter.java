/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.processor.basic;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.commons.GeneratorAdapter;

/**
 * @author yong.zhuy 2012-6-13
 */
public class BaseMethodAdapter extends GeneratorAdapter {

    protected String className;

    public BaseMethodAdapter(MethodVisitor mv, int access, String name, String desc, String className){
        super(mv, access, name, desc);

        this.className = className;
    }
}
