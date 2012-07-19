/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.meta;

import com.alibaba.hotswap.util.HotswapMethodUtil;

/**
 * @author yong.zhuy 2012-6-15
 */
public class MethodMeta extends MetaInfo {

    public String[] exceptions;

    private int     index;

    public MethodMeta(int access, String name, String desc, String signature, String[] exceptions){
        super(access, name, desc, signature);
        this.exceptions = exceptions;
    }

    public String getKey() {
        return HotswapMethodUtil.getMethodKey(name, desc);
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
