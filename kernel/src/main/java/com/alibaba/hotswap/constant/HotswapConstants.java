/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.constant;

/**
 * @author yong.zhuy 2012-6-13 14:31:47
 */
public interface HotswapConstants {

    /**
     * &lt;clinit&gt;
     */
    String CLINIT              = "<clinit>";
    String HOTSWAP_CLINIT      = "__$$hotswap_clinit$$__";

    /**
     * &lt;init&gt;
     */
    String INIT                = "<init>";
    /**
     * field
     */
    String FIELD_HOLDER        = "__hotswap_field_holder__";
    String STATIC_FIELD_HOLDER = "__hotswap_static_field_holder__";
}
