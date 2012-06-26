/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.processor.jdk.classloader;

import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import com.alibaba.hotswap.constant.HotswapConstants;
import com.alibaba.hotswap.meta.ClassMeta;
import com.alibaba.hotswap.runtime.HotswapRuntime;

/**
 * @author zhuyong 2012-6-26
 */
public class ClassLoaderHelper {

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Class<?> tryLoadVClass(final ClassLoader loader, String vClassName) throws ClassNotFoundException {
        int vIndex = vClassName.indexOf(HotswapConstants.V_CLASS_PATTERN);
        if (vIndex < 0) {
            return null;
        }

        String className = vClassName.substring(0, vIndex);
        final ClassMeta classMeta = HotswapRuntime.getClassMeta(className);
        
        if(loader != classMeta.loader) {
            return null;
        }
        
        final byte[] bytes = classMeta.loadedBytes;

        try {
            return (Class<?>) AccessController.doPrivileged(new PrivilegedExceptionAction() {

                public Object run() throws ClassNotFoundException {
                    try {
                        Method defineClassMethod = ClassLoader.class.getDeclaredMethod("defineClass", new Class<?>[] {
                                String.class, byte[].class, int.class, int.class });
                        defineClassMethod.setAccessible(true);
                        return defineClassMethod.invoke(loader, classMeta.vClassName, bytes, 0, bytes.length);
                    } catch (Exception e) {
                        throw new ClassNotFoundException(classMeta.vClassName, e);
                    }
                }
            }, AccessController.getContext());
        } catch (PrivilegedActionException pae) {
            throw (ClassNotFoundException) pae.getException();
        }
    }
}
