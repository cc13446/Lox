package com.cc.lox.clazz;

import com.cc.lox.function.LoxCallable;
import com.cc.lox.function.impl.LoxFunction;
import com.cc.lox.interpreter.LoxInterpreter;
import lombok.Getter;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author cc
 * @date 2023/10/14
 */
@Getter
public class LoxClass implements LoxCallable {

    public final static String INIT = "init";

    private final String name;

    private final Map<String, LoxFunction> methods;

    public LoxClass(String name, Map<String, LoxFunction> methods) {
        this.name = name;
        this.methods = methods;
    }

    public LoxFunction findMethod(String name) {
        if (methods.containsKey(name)) {
            return methods.get(name);
        }

        return null;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public int getArity() {
        LoxFunction initializer = findMethod(INIT);
        if (Objects.isNull(initializer)) {
            return 0;
        }
        return initializer.getArity();
    }

    @Override
    public Object call(LoxInterpreter interpreter, List<Object> arguments) {
        LoxInstance instance = new LoxInstance(this);
        LoxFunction initializer = findMethod(INIT);
        if (Objects.nonNull(initializer)) {
            initializer.bind(instance).call(interpreter, arguments);
        }
        return instance;
    }
}
