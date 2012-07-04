/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.constant;

import org.objectweb.asm.Type;

/**
 * @author yong.zhuy 2012-6-13 14:31:47
 */
public interface HotswapConstants {

    /**
     * &lt;clinit&gt; method name
     */
    String     CLINIT                        = "<clinit>";
    String     HOTSWAP_CLINIT                = "__$$hotswap_clinit$$__";

    /**
     * &lt;init&gt;
     */
    String     INIT                          = "<init>";

    String     UNIFORM_CONSTRUCTOR_DESC      = Type.getMethodDescriptor(Type.VOID_TYPE,
                                                                        new Type[] {
            Type.getType(HotswapConstructorSign.class), Type.INT_TYPE, Type.getType(Object[].class) });

    Class<?>[] UNIFORM_CONSTRUCTOR_ARGS_TYPE = new Class<?>[] { HotswapConstructorSign.class, int.class, Object[].class };
    /**
     * field
     */
    String     FIELD_HOLDER                  = "__$$hotswap_field_holder$$__";
    String     STATIC_FIELD_HOLDER           = "__$$hotswap_static_field_holder$$__";

    /**
     * field which is modified its access, then alias it
     */
    String     PREFIX_FIELD_ALIAS            = "__$$hotswap_field_alias$$__";

    String     V_CLASS_PATTERN               = "$$V$$";
}
