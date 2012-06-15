package com.alibaba.hotswap.meta;

import org.objectweb.asm.Opcodes;

import com.alibaba.hotswap.util.HotswapFieldUtil;

public class FieldMeta {

    public int    access;
    public String name;
    public String desc;
    public String signature;
    public Object value;

    public FieldMeta(int access, String name, String desc, String signature, Object value){
        this.access = access;
        this.name = name;
        this.desc = desc;
        this.signature = signature;
        this.value = value;
    }

    public boolean deleted = false;
    public boolean added   = false;

    public boolean isAdded() {
        return added;
    }

    public boolean isStatic() {
        return (access & Opcodes.ACC_STATIC) == Opcodes.ACC_STATIC;
    }

    public String getKey() {
        return HotswapFieldUtil.getFieldKey(name, desc);
    }

    public String toString() {
        return HotswapFieldUtil.getFieldKey(name, desc) + "-" + added + "-" + deleted;
    }
}
