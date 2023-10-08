package com.cc.lox.token.type;

/**
 * @author cc
 * @date 2023/10/8
 */
@FunctionalInterface
public interface TokenMatchFunction<T, P, R> {
    R apply(T t, P p);
}
