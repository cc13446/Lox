package com.cc.lox.function.impl;

import com.cc.lox.environment.Environment;
import com.cc.lox.function.LoxCallable;
import com.cc.lox.function.Return;
import com.cc.lox.interpreter.LoxInterpreter;
import com.cc.lox.parser.statement.impl.FunctionStatement;

import java.util.List;

/**
 * @author cc
 * @date 2023/10/13
 */
public class LoxFunction implements LoxCallable {
    private final FunctionStatement declaration;

    private final Environment closure;

    public LoxFunction(FunctionStatement declaration, Environment closure) {
        this.declaration = declaration;
        this.closure = closure;
    }

    @Override
    public int getArity() {
        return declaration.getParams().size();
    }

    @Override
    public Object call(LoxInterpreter interpreter, List<Object> arguments) {
        Environment environment = new Environment(closure);
        for (int i = 0; i < declaration.getParams().size(); i++) {
            environment.define(declaration.getParams().get(i), arguments.get(i));
        }
        try {
            interpreter.executeBlock(declaration.getBody(), environment);
        } catch (Return r) {
            return r.getValue();
        }
        return null;
    }

    @Override
    public String toString() {
        return "<fn " + declaration.getName().getLexeme() + ">";
    }
}
