/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.processor.method;

import org.objectweb.asm.ClassVisitor;

import com.alibaba.hotswap.processor.basic.BaseClassVisitor;

/**
 * @author zhuyong 2012-7-27
 */
public class NormalMethodVisitor extends BaseClassVisitor {

    public NormalMethodVisitor(ClassVisitor cv){
        super(cv);
    }
}
