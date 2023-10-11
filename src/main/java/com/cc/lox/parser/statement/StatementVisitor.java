package com.cc.lox.parser.statement;

import com.cc.lox.parser.statement.impl.ExpressionStatement;
import com.cc.lox.parser.statement.impl.PrintStatement;

public interface StatementVisitor<R> {
    R visitExpressionStatement(ExpressionStatement statement);
    R visitPrintStatement(PrintStatement statement);
}
