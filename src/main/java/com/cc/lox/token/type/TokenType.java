package com.cc.lox.token.type;

import com.cc.lox.token.Scanner;
import lombok.Getter;

import static com.cc.lox.token.Scanner.*;
import static com.cc.lox.token.type.TokenMetaType.*;

/**
 * token 类型
 *
 * @author cc
 * @date 2023/10/8
 */
@Getter
public enum TokenType {


    // Single-character tokens.
    LEFT_PAREN("(", SIMPLE),
    RIGHT_PAREN(")", SIMPLE),
    LEFT_BRACE("{", SIMPLE),
    RIGHT_BRACE("}", SIMPLE),
    COMMA(",", SIMPLE),
    DOT(".", SIMPLE),
    MINUS("-", SIMPLE),
    PLUS("+", SIMPLE),
    SEMICOLON(";", SIMPLE),
    STAR("*", SIMPLE),

    // One or two character tokens.
    BANG("!", MORE, getNoMoreMatch('=')),
    BANG_EQUAL("!=", MORE),
    EQUAL("=", MORE, getNoMoreMatch('=')),
    EQUAL_EQUAL("==", MORE),
    GREATER(">", MORE, getNoMoreMatch('=')),
    GREATER_EQUAL(">=", MORE),
    LESS("<", MORE, getNoMoreMatch('=')),
    LESS_EQUAL("<=", MORE),
    SLASH("/", MORE, getNoMoreMatch('/')),
    COMMIT("//", MORE, COMMIT_MATCH),

    // Literals.
    IDENTIFIER("IDENTIFIER", LITERALS, IDENTIFIER_MATCH),
    STRING("STRING", LITERALS, STRING_MATCH),
    NUMBER("NUMBER", LITERALS, NUMBER_MATCH),

    // Keywords.
    AND("and", KEYWORD),
    CLASS("class", KEYWORD),
    ELSE("else", KEYWORD),
    FALSE("false", KEYWORD),
    FUN("fun", KEYWORD),
    FOR("for", KEYWORD),
    IF("if", KEYWORD),
    NIL("nil", KEYWORD),
    OR("or", KEYWORD),
    PRINT("print", KEYWORD),
    RETURN("return", KEYWORD),
    SUPER("super", KEYWORD),
    THIS("this", KEYWORD),
    TRUE("true", KEYWORD),
    VAR("var", KEYWORD),
    WHILE("while", KEYWORD),

    SPACE(" ", SPLIT),
    SPLASH_T("\t", SPLIT),
    SPLASH_R("\r", SPLIT),
    SPLASH_N("\n", SPLIT),
    EOF("", SPLIT, FALSE_MATCH);

    private final String code;

    private final TokenMetaType type;

    /**
     * 是否和当前token匹配
     */
    private final TokenMatchFunction<Scanner, String, Boolean> match;

    TokenType(String code, TokenMetaType type) {
        this(code, type, DEFAULT_MATCH);
    }

    TokenType(String code, TokenMetaType type, TokenMatchFunction<Scanner, String, Boolean> match) {
        this.code = code;
        this.type = type;
        this.match = match;
    }

    public boolean match(Scanner scanner) {
        return this.match.apply(scanner, code);
    }

}
