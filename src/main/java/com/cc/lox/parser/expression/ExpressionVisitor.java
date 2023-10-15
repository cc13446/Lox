package com.cc.lox.parser.expression;

import com.cc.lox.parser.expression.impl.AssignExpression;
import com.cc.lox.parser.expression.impl.BinaryExpression;
import com.cc.lox.parser.expression.impl.CallExpression;
import com.cc.lox.parser.expression.impl.GetExpression;
import com.cc.lox.parser.expression.impl.GroupingExpression;
import com.cc.lox.parser.expression.impl.LiteralExpression;
import com.cc.lox.parser.expression.impl.LogicalExpression;
import com.cc.lox.parser.expression.impl.SetExpression;
import com.cc.lox.parser.expression.impl.SuperExpression;
import com.cc.lox.parser.expression.impl.ThisExpression;
import com.cc.lox.parser.expression.impl.UnaryExpression;
import com.cc.lox.parser.expression.impl.VariableExpression;

public interface ExpressionVisitor<R> {
    R visitAssignExpression(AssignExpression expression);
    R visitBinaryExpression(BinaryExpression expression);
    R visitCallExpression(CallExpression expression);
    R visitGetExpression(GetExpression expression);
    R visitGroupingExpression(GroupingExpression expression);
    R visitLiteralExpression(LiteralExpression expression);
    R visitLogicalExpression(LogicalExpression expression);
    R visitSetExpression(SetExpression expression);
    R visitSuperExpression(SuperExpression expression);
    R visitThisExpression(ThisExpression expression);
    R visitUnaryExpression(UnaryExpression expression);
    R visitVariableExpression(VariableExpression expression);
}
