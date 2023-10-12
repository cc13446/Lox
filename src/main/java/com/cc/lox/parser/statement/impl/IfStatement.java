package com.cc.lox.parser.statement.impl;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

import com.cc.lox.parser.statement.Statement;
import com.cc.lox.parser.statement.StatementVisitor;
import com.cc.lox.scanner.Token;
import com.cc.lox.parser.expression.Expression;

@AllArgsConstructor
@Getter
public class IfStatement extends Statement {

    private final Expression condition;
    private final Statement thenBranch;
    private final Statement elseBranch;

    @Override
    public <R> R accept(StatementVisitor<R> visitor) {
        return visitor.visitIfStatement(this);
    }
}
