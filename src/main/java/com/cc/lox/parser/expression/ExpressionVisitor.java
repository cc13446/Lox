package com.cc.lox.parser.expression;

import com.cc.lox.parser.expression.impl.AssignExpression;
import com.cc.lox.parser.expression.impl.BinaryExpression;
import com.cc.lox.parser.expression.impl.GroupingExpression;
import com.cc.lox.parser.expression.impl.LiteralExpression;
import com.cc.lox.parser.expression.impl.LogicalExpression;
import com.cc.lox.parser.expression.impl.UnaryExpression;
import com.cc.lox.parser.expression.impl.VariableExpression;

public interface ExpressionVisitor<R> {
    R visitAssignExpression(AssignExpression expression);
    R visitBinaryExpression(BinaryExpression expression);
    R visitGroupingExpression(GroupingExpression expression);
    R visitLiteralExpression(LiteralExpression expression);
    R visitLogicalExpression(LogicalExpression expression);
    R visitUnaryExpression(UnaryExpression expression);
    R visitVariableExpression(VariableExpression expression);
}
