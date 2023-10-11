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

    public void define(Token token, Object value) {
        values.put(token.getLexeme(), value);
    }

    public Object get(Token name) {
        if (values.containsKey(name.getLexeme())) {
            return values.get(name.getLexeme());
        }

        throw new RuntimeError(name, "Undefined variable '" + name.getLexeme() + "'.");
    }


}
