/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.util;

import org.objectweb.asm.Type;

import com.alibaba.hotswap.constant.HotswapConstructorSign;
import com.alibaba.hotswap.exception.HotswapException;
import com.alibaba.hotswap.runtime.HotswapMethodIndexHolder;

/**
 * @author yong.zhuy 2012-6-14
 */
public class HotswapMethodUtil {

    public static String getMethodKey(String name, String desc) {
        return name + "[" + desc + "]";
    }

    public static String getMethodName(String methodKey) {
        return methodKey.substring(0, methodKey.indexOf('['));
    }

    public static String getMethodDesc(String methodKey) {
        return methodKey.substring(methodKey.indexOf('[') + 1, methodKey.indexOf(']'));
    }

    public static String getNormalMethodDesc(String desc) {
        Type[] ts = Type.getArgumentTypes(desc);
        StringBuilder sb = new StringBuilder("(");
        for (int i = 0; i < ts.length; i++) {
            sb.append(ts[i].getClassName());
            if (i < ts.length - 1) {
                sb.append(" ,");
            }
        }
        sb.append(")");

        return sb.toString();
    }

    public static Throwable noSuchMethodError(String className, int index) {
        String methodKey = HotswapMethodIndexHolder.getMethodKeyByIndex(className, index);
        if (methodKey == null) {
            return new HotswapException("can't invoke a non-exist constructor.");
        }
        return new NoSuchMethodError(className.replace("/", ".") + "." + getMethodName(methodKey)
                                     + getNormalMethodDesc(getMethodDesc(methodKey)));
    }

    public static Object[] processConstructorArgs(Object[] objs, int index, Object arg) {
        objs[index] = arg;
        return objs;
    }

    public static Class<?>[] getConstructorParamTypes() {
        Class<?>[] types = new Class<?>[3];
        types[0] = HotswapConstructorSign.class;
        types[1] = int.class;
        types[2] = Object[].class;

        return types;
    }

    public static Object[] getMethodParams(String className, String name, String desc) {
        Object[] params = new Object[3];
        params[0] = null;
        params[1] = HotswapMethodIndexHolder.getMethodIndex(className, name, desc);
        params[2] = null;

        return params;
    }
}
