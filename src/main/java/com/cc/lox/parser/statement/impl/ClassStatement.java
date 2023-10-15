package com.cc.lox.parser.statement.impl;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

import com.cc.lox.parser.statement.Statement;
import com.cc.lox.parser.statement.StatementVisitor;
import com.cc.lox.scanner.Token;
import com.cc.lox.parser.expression.Expression;
import com.cc.lox.parser.expression.impl.*;

@AllArgsConstructor
@Getter
public class ClassStatement extends Statement {

    private final Token name;
    private final VariableExpression superclass;
    private final List<FunctionStatement> methods;

    @Override
    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visitClassStatement(this);
    }
}
