package com.alibaba.hotswap.meta;

import com.alibaba.hotswap.util.HotswapFieldUtil;

public class FieldMeta extends MetaInfo {

    public Object value;

    public FieldMeta(int access, String name, String desc, String signature, Object value){
        super(access, name, desc, signature);
        this.value = value;
    }

    public String getKey() {
        return HotswapFieldUtil.getFieldKey(name, desc);
    }

    public String toString(int loadedIndex) {
        return access + HotswapFieldUtil.getFieldKey(name, desc) + ", signature: " + signature + ", added: " + added
               + ", deleted: " + isDeleted(loadedIndex) + ", loadedIndex: " + this.loadedIndex;
    }
}
