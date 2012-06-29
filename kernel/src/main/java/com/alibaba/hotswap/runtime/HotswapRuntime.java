/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.runtime;

import java.io.File;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Method;
import java.util.HashMap;

import com.alibaba.hotswap.constant.HotswapConstants;
import com.alibaba.hotswap.exception.HotswapException;
import com.alibaba.hotswap.loader.CustomerLoadClassBytes;
import com.alibaba.hotswap.meta.ClassMeta;
import com.alibaba.hotswap.util.HotswapUtil;

/**
 * @author yong.zhuy 2012-5-22 13:11:05
 */
public class HotswapRuntime {

    private static Instrumentation           inst;
    public static HashMap<String, ClassMeta> CLASS_METAS = new HashMap<String, ClassMeta>();

    public static boolean hasClassMeta(String className) {
        className = HotswapUtil.getInternalClassName(className);
        ClassMeta meta = CLASS_METAS.get(className);
        return meta != null;
    }

    public static String getVClassName(String className) {
        return getClassMeta(className).vClassName;
    }

    public static byte[] getVClassBytes(String className) {
        return getClassMeta(className).loadedBytes;
    }

    public static ClassMeta getClassMeta(String className) {
        className = HotswapUtil.getInternalClassName(className);

        ClassMeta meta = CLASS_METAS.get(className);
        if (meta == null) {
            meta = new ClassMeta();
            CLASS_METAS.put(className, meta);
        }

        return meta;
    }

    public static void updateClassMeta(String className, ClassLoader loader) {
        if (className.indexOf(HotswapConstants.V_CLASS_PATTERN) > 0) {
            return;
        }

        ClassMeta meta = getClassMeta(className);
        meta.loader = loader;
    }

    public static void updateClassMeta(String className, File classFile) {
        if (className.indexOf(HotswapConstants.V_CLASS_PATTERN) > 0) {
            return;
        }
        ClassMeta meta = getClassMeta(className);
        meta.name = HotswapUtil.getInternalClassName(className);
        meta.path = classFile.getAbsolutePath();
        meta.lastModified = classFile.lastModified();
    }

    public static void updateClassMetaClass(String className, Class<?> clazz) {
        if (className.indexOf(HotswapConstants.V_CLASS_PATTERN) > 0) {
            return;
        }
        ClassMeta meta = getClassMeta(className);
        meta.clazz = clazz;
    }

    @Deprecated
    public static void setClassInitialized(String className) {

    }

    public static boolean getClassInitialized(String className) {
        if (className.indexOf(HotswapConstants.V_CLASS_PATTERN) > 0) {
            return false;
        }
        ClassMeta classMeta = getClassMeta(className);
        return classMeta != null && classMeta.clazz != null;
    }

    public static void redefineClass(ClassMeta meta) {
        byte[] klass = null;

        try {
            klass = CustomerLoadClassBytes.loadBytesFromPath(meta.name, meta.path);
        } catch (HotswapException he) {
            HotswapUtil.sysout(he);
            return;
        }

        ClassDefinition definitions = new ClassDefinition(meta.clazz, klass);
        try {
            inst.redefineClasses(definitions);

            try {
                Method clinit = meta.clazz.getDeclaredMethod(HotswapConstants.HOTSWAP_CLINIT, (Class<?>[]) null);
                clinit.setAccessible(true);
                clinit.invoke(null, (Object[]) null);
            } catch (Exception e) {
                if (!(e instanceof NoSuchMethodException)) {
                    System.out.println("invoke " + HotswapConstants.HOTSWAP_CLINIT + " error, className: "
                                       + meta.name.replace('/', '.'));
                    e.printStackTrace();
                }
            }

            HotswapUtil.sysout("Success to reload class: " + meta.name.replace('/', '.') + " @ " + meta.path);
        } catch (Exception e) {
            throw new HotswapException("redefine class error, name: " + meta.name.replace('/', '.'), e);
        }
    }

    public static void holdInst(Instrumentation inst) {
        HotswapRuntime.inst = inst;
    }
}
