/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.meta;

import org.objectweb.asm.Opcodes;

/**
 * @author zhuyong 2012-7-18
 */
public abstract class MetaInfo {

    public int     access;
    public String  name;
    public String  desc;
    public String  signature;

    public int     loadedIndex;
    public boolean added = false;

    public MetaInfo(int access, String name, String desc, String signature){
        this.access = access;
        this.name = name;
        this.desc = desc;
        this.signature = signature;
    }

    public abstract String getKey();

    public boolean isAdded() {
        return added;
    }

    public boolean isDeleted(int loadedIndex) {
        return this.loadedIndex < loadedIndex;
    }

    public boolean isStatic() {
        return (access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC;
    }
}
