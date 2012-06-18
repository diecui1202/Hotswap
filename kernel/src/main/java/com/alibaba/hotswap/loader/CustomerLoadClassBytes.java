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

import com.alibaba.hotswap.exception.HotswapException;
import com.alibaba.hotswap.meta.ClassMeta;
import com.alibaba.hotswap.processor.HotswapProcessorFactory;
import com.alibaba.hotswap.runtime.HotswapRuntime;

/**
 * @author yong.zhuy 2012-5-21 16:00:01
 */
public class CustomerLoadClassBytes {

    public static byte[] loadBytesFromPath(String name, String classPath) {
        File classFile = new File(classPath);
        HotswapRuntime.updateClassMeta(name, classFile);
        ClassMeta classMeta = HotswapRuntime.getClassMeta(name);
        try {
            FileInputStream fis = new FileInputStream(classFile);
            byte[] classByte = null;
            int len = fis.available();
            if (len > 0) {
                classByte = new byte[len];
                int readLen = fis.read(classByte);
                if (readLen != len) {
                    throw new IllegalStateException("read class bytes error, available not equal readLen");
                }
            } else {
                throw new IllegalStateException("read class bytes error, len == 0");
            }

            try {
                classMeta.loadedBytes = classByte;
                Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(name.replace("/", ".")
                                                                                                  + "$$V$$"
                                                                                                  + classMeta.loadedIndex);
                HotswapRuntime.getClassMeta(name).newestClass = clazz;
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            return HotswapProcessorFactory.process(classByte);
        } catch (IOException e) {
            throw new HotswapException("load class error, name: " + name, e);
        }
    }
}
