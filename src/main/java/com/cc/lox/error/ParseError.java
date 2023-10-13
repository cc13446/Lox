package com.cc.lox.error;

/**
 * @author cc
 * @date 2023/10/10
 */
public class ParseError extends RuntimeException {
    public ParseError(String message) {
        super(message);
    }
}
