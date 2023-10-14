package com.cc.lox.parser.statement;

import com.cc.lox.parser.statement.impl.BlockStatement;
import com.cc.lox.parser.statement.impl.ClassStatement;
import com.cc.lox.parser.statement.impl.ExpressionStatement;
import com.cc.lox.parser.statement.impl.FunctionStatement;
import com.cc.lox.parser.statement.impl.ReturnStatement;
import com.cc.lox.parser.statement.impl.IfStatement;
import com.cc.lox.parser.statement.impl.PrintStatement;
import com.cc.lox.parser.statement.impl.WhileStatement;
import com.cc.lox.parser.statement.impl.VarStatement;

public interface StatementVisitor<R> {
    R visitBlockStatement(BlockStatement statement);
    R visitClassStatement(ClassStatement statement);
    R visitExpressionStatement(ExpressionStatement statement);
    R visitFunctionStatement(FunctionStatement statement);
    R visitReturnStatement(ReturnStatement statement);
    R visitIfStatement(IfStatement statement);
    R visitPrintStatement(PrintStatement statement);
    R visitWhileStatement(WhileStatement statement);
    R visitVarStatement(VarStatement statement);
}
