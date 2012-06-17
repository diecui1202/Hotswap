/*
 * Copyright 2012 Alibaba.com All right reserved. This software is the
 * confidential and proprietary information of Alibaba.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Alibaba.com.
 */
package com.alibaba.hotswap.exception;

/**
 * @author yong.zhuy 2012-5-21 17:51:29
 */
public class HotswapException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public HotswapException(String message){
        this(message, null);
    }

    public HotswapException(String message, Throwable t){
        super(message, t);
    }
}
