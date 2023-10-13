package com.cc.lox.environment;

import com.cc.lox.interpreter.RuntimeError;
import com.cc.lox.scanner.Token;

import java.util.*;

/**
 * 保存运行时变量
 *
 * @author cc
 * @date 2023/10/11
 */
public class Environment {

    /**
     * 对外围环境的引用
     */
    private final Environment enclosing;

    private final Map<String, Object> values = new HashMap<>();

    public Environment() {
        enclosing = null;
    }

    public Environment(Environment enclosing) {
        this.enclosing = enclosing;
    }


    /**
     * @return 不可变的闭包环境
     */
    public Environment toClosure() {
        List<Environment> all = new ArrayList<>();
        Environment env = this;
        while(Objects.nonNull(env)) {
            all.add(env);
            env = env.enclosing;
        }

        Environment result = new Environment();
        for (int i = all.size() - 1; i >= 0; i--) {
            result.values.putAll(all.get(i).values);
        }
        return result;
    }

    /**
     * 定义全局变量
     *
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
        if (Objects.nonNull(enclosing)) {
            return enclosing.get(name);
        }

        throw new RuntimeError(name, "Undefined variable '" + name.getLexeme() + "'.");
    }


    /**
     * 给全局变量附值
     *
     * @param name  token
     * @param value value
     */
    public void assign(Token name, Object value) {
        if (values.containsKey(name.getLexeme())) {
            values.put(name.getLexeme(), value);
            return;
        }

        if (Objects.nonNull(enclosing)) {
            enclosing.assign(name, value);
            return;
        }

        throw new RuntimeError(name, "Undefined variable '" + name.getLexeme() + "'.");
    }
}
