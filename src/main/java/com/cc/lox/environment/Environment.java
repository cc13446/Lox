package com.cc.lox.environment;

import com.cc.lox.interpreter.RuntimeError;
import com.cc.lox.scanner.Token;

import java.util.HashMap;
import java.util.Map;

/**
 * 保存运行时变量
 *
 * @author cc
 * @date 2023/10/11
 */
public class Environment {

    private final Map<String, Object> values = new HashMap<>();

    /**
     * 定义全局变量
     * @param token token
     * @param value value
     */
    public void define(Token token, Object value) {
        if (values.containsKey(token.getLexeme())) {
            throw new RuntimeError(token, "Duplicate defined variable '" + token.getLexeme() + "'.");
        }
        values.put(token.getLexeme(), value);
    }

    /**
     * @param name token
     * @return 变量的值
     */
    public Object get(Token name) {
        if (values.containsKey(name.getLexeme())) {
            return values.get(name.getLexeme());
        }

        throw new RuntimeError(name, "Undefined variable '" + name.getLexeme() + "'.");
    }


    /**
     * 给全局变量附值
     * @param name token
     * @param value value
     */
    public void assign(Token name, Object value) {
        if (values.containsKey(name.getLexeme())) {
            values.put(name.getLexeme(), value);
            return;
        }

        throw new RuntimeError(name, "Undefined variable '" + name.getLexeme() + "'.");
    }
}
