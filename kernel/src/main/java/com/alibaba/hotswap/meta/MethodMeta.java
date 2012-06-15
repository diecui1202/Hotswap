/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.meta;

/**
 * @author yong.zhuy 2012-6-15
 */
public class MethodMeta {

    public int      access;
    public String   name;
    public String   desc;
    public String   signature;
    public String[] exceptions;

    public MethodMeta(int access, String name, String desc, String signature, String[] exceptions){
        this.access = access;
        this.name = name;
        this.desc = desc;
        this.signature = signature;
        this.exceptions = exceptions;
    }
}
