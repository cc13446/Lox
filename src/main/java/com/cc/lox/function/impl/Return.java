package com.cc.lox.function.impl;

import lombok.Getter;

/**
 * @author cc
 * @date 2023/10/13
 */
@Getter
public class Return extends RuntimeException {
    private final Object value;

    public Return(Object value) {
        super(null, null, false, false);
        this.value = value;
    }
}