/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.processor.jdk.helper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Type;

import com.alibaba.hotswap.constant.HotswapConstants;
import com.alibaba.hotswap.meta.ClassMeta;
import com.alibaba.hotswap.meta.MethodMeta;
import com.alibaba.hotswap.runtime.HotswapRuntime;
import com.alibaba.hotswap.util.HotswapMethodUtil;

/**
 * @author zhuyong 2012-7-4
 */
public class MethodReflectHelper {

    public static boolean isUniformConstructorArgsType(Class<?>[] args) {
        if (args == null || args.length != HotswapConstants.UNIFORM_CONSTRUCTOR_ARGS_TYPE.length) {
            return false;
        }

        for (int i = 0; i < args.length; i++) {
            if (!args[i].equals(HotswapConstants.UNIFORM_CONSTRUCTOR_ARGS_TYPE[i])) {
                return false;
            }
        }

        return true;
    }

    public static Constructor<?>[] filterHotswapConstructor(Constructor<?>[] constructors) {
        List<Constructor<?>> rets = new ArrayList<Constructor<?>>();
        for (Constructor<?> c : constructors) {
            if (isUniformConstructorArgsType(c.getParameterTypes())) {
                continue;
            }
            rets.add(c);
        }
        return rets.toArray(new Constructor<?>[] {});
    }

    public static Constructor<?>[] getDeclaredConstructors0(Class<?> clazz, boolean publicOnly) {
        String name = clazz.getName();
        ClassMeta classMeta = HotswapRuntime.getClassMeta(name);

        Constructor<?>[] constructors = null;
        if (publicOnly) {
            constructors = classMeta.vClass.getConstructors();
        } else {
            constructors = classMeta.vClass.getDeclaredConstructors();
        }

        try {
            List<Constructor<?>> constructorList = new ArrayList<Constructor<?>>();
            // Remove constructor which has been deleted

            Field clazzField = Constructor.class.getDeclaredField("clazz");
            clazzField.setAccessible(true);

            for (Constructor<?> c : constructors) {
                String mk = HotswapMethodUtil.getMethodKey(HotswapConstants.INIT, Type.getConstructorDescriptor(c));
                MethodMeta mm = classMeta.initMetas.get(mk);
                if (!mm.isDeleted(classMeta.loadedIndex)) {
                    clazzField.set(c, clazz);
                    constructorList.add(c);
                }
            }

            Method getDeclaredConstructors0Method = Class.class.getDeclaredMethod("getDeclaredConstructors0",
                                                                                  boolean.class);
            getDeclaredConstructors0Method.setAccessible(true);
            Constructor<?>[] tranformConstructors = (Constructor[]) getDeclaredConstructors0Method.invoke(clazz,
                                                                                                          publicOnly);
            for (Constructor<?> c : tranformConstructors) {
                if (isUniformConstructorArgsType(c.getParameterTypes())) {
                    constructorList.add(c);
                }
            }

            return constructorList.toArray(new Constructor[] {});
        } catch (Exception e) {
            e.printStackTrace();
        }

        // unreached
        return new Constructor<?>[0];
    }
}
