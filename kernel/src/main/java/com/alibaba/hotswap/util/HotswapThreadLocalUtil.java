/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.util;

import java.util.List;

import org.objectweb.asm.tree.MethodNode;

/**
 * @author zhuyong 2012-6-26
 */
public class HotswapThreadLocalUtil {

    private static final ThreadLocal<String>           classNameThreadLocal       = new ThreadLocal<String>();

    private static final ThreadLocal<List<MethodNode>> initMethodNodesThreadLocal = new ThreadLocal<List<MethodNode>>();

    public static void setClassName(String className) {
        classNameThreadLocal.set(className);
    }

    public static String getClassName() {
        return classNameThreadLocal.get();
    }

    public static void setInitMethodNodes(List<MethodNode> initNodes) {
        initMethodNodesThreadLocal.set(initNodes);
    }

    public static List<MethodNode> getInitMethodNodes() {
        return initMethodNodesThreadLocal.get();
    }
}
