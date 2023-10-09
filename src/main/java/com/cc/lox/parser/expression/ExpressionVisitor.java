package com.cc.lox.parser.expression;

import com.cc.lox.parser.expression.impl.BinaryExpression;
import com.cc.lox.parser.expression.impl.GroupingExpression;
import com.cc.lox.parser.expression.impl.LiteralExpression;
import com.cc.lox.parser.expression.impl.UnaryExpression;

public interface ExpressionVisitor<R> {
    R visitBinaryExpression(BinaryExpression expression);
    R visitGroupingExpression(GroupingExpression expression);
    R visitLiteralExpression(LiteralExpression expression);
    R visitUnaryExpression(UnaryExpression expression);
}
