package com.cc.lox.scanner;

import com.cc.lox.scanner.type.TokenType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author cc
 * @date 2023/10/8
 */

@Getter
@ToString
@AllArgsConstructor
public class Token {
    private final TokenType type;
    private final String lexeme;
    private final Object literal;
    private final int line;
}
