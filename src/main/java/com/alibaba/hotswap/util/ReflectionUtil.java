package com.alibaba.hotswap.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yong.zhuy 2012-6-5 12:05:20
 */
@SuppressWarnings("unchecked")
public final class ReflectionUtil {

    public static Object getFieldValue(Object target, String fieldName) {
        return getFieldValue(target.getClass(), target, fieldName);
    }

    public static Object getFieldValue(Class<?> clazz, Object target, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(target);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Object getStaticFieldValue(Class<?> clazz, String fieldName) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void setFieldValue(Object target, String fieldName, Object fieldValue) {
        setFieldValue(target.getClass(), target, fieldName, fieldValue);
    }

    public static void setFieldValue(Class<?> clazz, Object target, String fieldName, Object fieldValue) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, fieldValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static <T> T invoke(Object target, String methodName, Object... params) {
        return invoke(target.getClass(), target, methodName, params);
    }

    public static <T> T invoke(Class<?> clazz, Object target, String methodName, Object... params) {
        try {
            Class<?>[] paraClazes = getParameterClazz(params);
            Method method = clazz.getDeclaredMethod(methodName, paraClazes);
            method.setAccessible(true);
            Object result = method.invoke(target, params);
            return (T) result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Class<?>[] getParameterClazz(Object... paras) {
        if (paras == null) {
            return new Class[0];
        }

        List<Class<?>> clazes = new ArrayList<Class<?>>();
        for (Object para : paras) {
            clazes.add(para == null ? null : para.getClass());
        }
        return clazes.toArray(new Class[0]);
    }
}
