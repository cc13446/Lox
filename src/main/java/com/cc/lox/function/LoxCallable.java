package com.cc.lox.function;

import com.cc.lox.interpreter.LoxInterpreter;

import java.util.List;

/**
 * @author cc
 * @date 2023/10/13
 */
public interface LoxCallable {

    int getArity();

    Object call(LoxInterpreter interpreter, List<Object> arguments);

}
