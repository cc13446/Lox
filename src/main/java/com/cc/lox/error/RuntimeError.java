package com.cc.lox.error;

import com.cc.lox.scanner.Token;
import lombok.Getter;

/**
 * @author cc
 * @date 2023/10/10
 */
@Getter
public class RuntimeError extends RuntimeException {
    private final Token token;

    public RuntimeError(Token token, String message) {
        super(message);
        this.token = token;
    }
}
