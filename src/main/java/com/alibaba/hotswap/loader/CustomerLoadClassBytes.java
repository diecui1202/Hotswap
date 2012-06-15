/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.util.TraceClassVisitor;

import com.alibaba.hotswap.configuration.HotswapConfiguration;
import com.alibaba.hotswap.exception.HotswapException;
import com.alibaba.hotswap.processor.HotswapProcessorHolder;
import com.alibaba.hotswap.runtime.HotswapRuntime;

/**
 * @author yong.zhuy 2012-5-21 16:00:01
 */
public class CustomerLoadClassBytes {

    public static byte[] loadBytesFromPath(String name, String classPath) {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);

        ClassVisitor cv = cw;
        if (HotswapConfiguration.TRACE) {
            cv = new TraceClassVisitor(cw, new PrintWriter(System.out));
        }

        cv = HotswapProcessorHolder.generateClassVisitor(cv);
        ClassReader cr;
        File classFile = new File(classPath);

        HotswapRuntime.updateClassMeta(name, classFile);
        try {
            cr = new ClassReader(new FileInputStream(classFile));
            cr.accept(cv, ClassReader.EXPAND_FRAMES);
            byte[] klass = cw.toByteArray();

            return klass;
        } catch (IOException e) {
            throw new HotswapException("load class error, name: " + name, e);
        }
    }
}
