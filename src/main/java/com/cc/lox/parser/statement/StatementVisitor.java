package com.cc.lox.parser.statement;

import com.cc.lox.parser.statement.impl.BlockStatement;
import com.cc.lox.parser.statement.impl.ExpressionStatement;
import com.cc.lox.parser.statement.impl.PrintStatement;
import com.cc.lox.parser.statement.impl.VarStatement;

public interface StatementVisitor<R> {
    R visitBlockStatement(BlockStatement statement);
    R visitExpressionStatement(ExpressionStatement statement);
    R visitPrintStatement(PrintStatement statement);
    R visitVarStatement(VarStatement statement);
}
