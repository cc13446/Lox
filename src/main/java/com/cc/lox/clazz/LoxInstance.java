package com.cc.lox.clazz;

import com.cc.lox.error.RuntimeError;
import com.cc.lox.function.impl.LoxFunction;
import com.cc.lox.scanner.Token;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author cc
 * @date 2023/10/14
 */
public class LoxInstance {

    private final LoxClass klass;

    private final Map<String, Object> fields = new HashMap<>();

    public LoxInstance(LoxClass klass) {
        this.klass = klass;
    }

    public Object get(Token name) {

        if (fields.containsKey(name.getLexeme())) {
            return fields.get(name.getLexeme());
        }

        LoxFunction method = klass.findMethod(name.getLexeme());
        if (Objects.nonNull(method)) {
            return method.bind(this);
        }

        throw new RuntimeError(name, "Undefined property '" + name.getLexeme() + "'.");
    }

    public void set(Token name, Object value) {
        fields.put(name.getLexeme(), value);
    }

    @Override
    public String toString() {
        return klass.getName() + "(instance)";
    }
}
